package controllers;

import static models.QHarvestSession.harvestSession;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.querydsl.core.Tuple;

import models.DataSource;
import models.PublisherTask;
import models.portal.DCInfo;
import models.portal.DatasetInfo;
import models.portal.MetadataInfo;
import models.portal.CountDifference;
import models.portal.CountDifferenceType;
import models.portal.HarvestInfo;
import models.portal.InfoFromTime;
import models.portal.InfoLast;
import models.portal.ServiceInfo;
import play.Configuration;
import play.Logger;
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;
import util.DashboardConfig;
import views.html.index;

public class Application extends Controller {
	@Inject Configuration config;
	@Inject WSClient ws;
	@Inject QueryDSL q;
	
	public Promise<Result> index() {
		return getDataSources().map(dataSources -> {
			return ok(index.render(
					new DashboardConfig(config),
					dataSources, 
					getPublisherTasks(), 
					new HarvestInfo(
							(DatasetInfo) getMetadataInfo("dataset"), 
							(ServiceInfo) getMetadataInfo("service"), 
							(DCInfo) getMetadataInfo("dc"))));
		});
	}
	
	private Promise<List<DataSource>> getDataSources() {
		final String url = config.getString("dashboard.provider.connection.url");
		
		WSRequest request = ws.url(url).setFollowRedirects(true);
			Promise<List<DataSource>> listDataSources = request.get().map(response -> {
				Gson gson = new GsonBuilder().create();
				
				try(InputStream input = response.getBodyAsStream()) {
					String json = IOUtils.toString(input, StandardCharsets.UTF_8.name());
					
					DataSource[] dataSources = gson.fromJson(json, DataSource[].class);
					
					if(dataSources != null) {
						return Arrays.asList(dataSources);
					}
				} catch(IOException ioe) {
					Logger.error("Something went wrong parsing the datasources to a string", ioe);
				} catch(JsonSyntaxException jse) {
					Logger.error("Something went wrong parsing the datasources to the java class", jse);
				}
				
				return Collections.emptyList();
			});
			
			return listDataSources.recover((Throwable throwable) -> {
				return Collections.emptyList();
			});
	}
	
	private List<PublisherTask> getPublisherTasks() {
		final String databaseUser = config.getString("dashboard.publisher.db.user");
		final String databasePassword = config.getString("dashboard.publisher.db.password");
		
		String sql = 
				"select js.job_id, js.state, js.create_time, j.type from publisher.job_state js "
					+ "join publisher.job j on j.id = js.job_id "
					+ "left join publisher.service_job sj on sj.job_id = js.job_id "
					+ "where js.state != ? "
					+ "and (j.type = ? or j.type = ? or j.type = ?) "
					+ "and (sj.type = ? or sj.type is null) "
					+ "order by js.create_time desc "
					+ "limit 10;";
		
		try(Connection conn = DriverManager.getConnection("jdbc:postgresql://pub.db/publisher", 
				databaseUser, databasePassword);
			PreparedStatement stmt = conn.prepareStatement(sql)) {
				Class.forName("org.postgresql.Driver");
				
				stmt.setString(1, "STARTED");
				stmt.setString(2, "HARVEST");
				stmt.setString(3, "IMPORT");
				stmt.setString(4, "SERVICE");
				stmt.setString(5, "ENSURE");
				
				return getPublisherTasksResult(conn, stmt);
		} catch(SQLException se) {
			Logger.error("Something went wrong querying publisher tasks", se);
		} catch(ClassNotFoundException cnfe) {
			Logger.error("Something went wrong getting the database driver", cnfe);
		}
		
		return Collections.emptyList();
	}
	
	private List<PublisherTask> getPublisherTasksResult(Connection conn, PreparedStatement stmt) throws SQLException {
		List<PublisherTask> publisherTasks = new ArrayList<>();
		
		try(ResultSet rs = stmt.executeQuery()) {
			while(rs.next()) {
				int jobId = rs.getInt("job_id");
				String state = rs.getString("state");
				Timestamp createTime = rs.getTimestamp("create_time");
				String jobType = rs.getString("type");
				
				String name = getPublisherTaskName(conn, jobType, jobId);
				
				publisherTasks.add(
						new PublisherTask(jobType.toLowerCase(), 
							name, 
							createTime.toLocalDateTime(), 
							"SUCCEEDED".equals(state)));
			}
		} catch(SQLException se) {
			throw se;
		}
		
		return publisherTasks;
	}
	
	private String getPublisherTaskName(Connection conn, String jobType, int jobId) throws SQLException {
		String sql = PublisherTask.getSQL(jobType);
		
		if(sql != null) {
			try(PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setInt(1, jobId);
				
				return getPublisherTaskNameResult(stmt, jobType);
			} catch(SQLException se) {
				throw se;
			}
		}
		
		return PublisherTask.getUnknownName();
	}
	
	private String getPublisherTaskNameResult(PreparedStatement stmt, String jobType) throws SQLException {
		try(ResultSet rs = stmt.executeQuery()) {
			rs.next();
			
			if("HARVEST".equals(jobType) || "IMPORT".equals(jobType)) {
				return rs.getString("name");
			} else if("SERVICE".equals(jobType)) {
				return rs.getString("name") + 
						(rs.getBoolean("published") ? 
								" (" + Messages.get("index.publishertasks.service.published") + ")" 
								: 
								" (" + Messages.get("index.publishertasks.service.staging") + ")"
						);
			}
		} catch(SQLException se) {
			throw se;
		}
		
		return PublisherTask.getUnknownName();
	}
	
	private MetadataInfo getMetadataInfo(String metadataType) {
		return q.withTransaction(tx -> {
			Tuple lastTuple = tx.select(harvestSession.internCount, harvestSession.externCount, harvestSession.archivedCount)
						.from(harvestSession)
						.where(harvestSession.type.eq(metadataType))
						.orderBy(harvestSession.createTime.desc())
						.fetchFirst();
			
			if(lastTuple != null) {
				return getMetadataInfoResult(lastTuple, metadataType);
			}
			
			return null;
		});
	}
	
	private MetadataInfo getMetadataInfoResult(Tuple lastTuple, String metadataType) {
		InfoLast infoLast = new InfoLast(lastTuple.get(harvestSession.externCount), lastTuple.get(harvestSession.internCount), lastTuple.get(harvestSession.archivedCount));
		
		InfoFromTime weekAgoInfo = getPortalInfoFromTime(metadataType, infoLast, LocalDateTime.now(), 1, 
				ChronoUnit.WEEKS);
		InfoFromTime dayAgoInfo = getPortalInfoFromTime(metadataType, infoLast, LocalDateTime.now(), 1, 
				ChronoUnit.DAYS);
		InfoFromTime hourAgoInfo = getPortalInfoFromTime(metadataType, infoLast, LocalDateTime.now(), 1, 
				ChronoUnit.HOURS);
		
		if("dataset".equals(metadataType)) {
			return new DatasetInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
		} else if("service".equals(metadataType)) {
			return new ServiceInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
		} else if("dc".equals(metadataType)) {
			return new DCInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
		}
		
		return null;
	}
	
	private InfoFromTime getPortalInfoFromTime(String metadataType,
			InfoLast infoLast,
			LocalDateTime now,
			int amountToSubtract,
			ChronoUnit unit) {
				LocalDateTime timeAgo = now.minus(amountToSubtract, unit);
				
				return q.withTransaction(tx -> {
					Tuple timeTuple = tx.select(harvestSession.internCount, harvestSession.externCount, harvestSession.archivedCount)
								.from(harvestSession)
								.where(harvestSession.type.eq(metadataType)
										.and(harvestSession.createTime.loe(Timestamp.valueOf(timeAgo))))
								.orderBy(harvestSession.createTime.desc())
								.fetchFirst();
					
					if(timeTuple != null) {
						CountDifference countDifferenceExtern = getPortalCountDifference(infoLast.getCountExtern(), timeTuple.get(harvestSession.externCount));
						CountDifference countDifferenceIntern = getPortalCountDifference(infoLast.getCountIntern(), timeTuple.get(harvestSession.internCount));
						CountDifference countDifferenceArchived = getPortalCountDifference(infoLast.getCountArchived(), timeTuple.get(harvestSession.archivedCount));
						
						return new InfoFromTime(countDifferenceExtern, countDifferenceIntern, countDifferenceArchived);
					} else {
						return null;
					}
				});
	}
	
	private CountDifference getPortalCountDifference(Integer lastCount, Integer timeCount) {
		if(lastCount == null || timeCount == null) return new CountDifference(CountDifferenceType.NOCHANGE, 0);
		
		if(lastCount > timeCount) {
			return new CountDifference(CountDifferenceType.ADDITION, lastCount - timeCount);
		} else if(lastCount < timeCount) {
			return new CountDifference(CountDifferenceType.SUBTRACTION, timeCount - lastCount);
		} else {
			return new CountDifference(CountDifferenceType.NOCHANGE, 0);
		}
	}
}
