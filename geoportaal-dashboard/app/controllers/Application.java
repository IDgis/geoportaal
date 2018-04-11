package controllers;

import static models.QHarvestSession.harvestSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.querydsl.core.Tuple;
import com.querydsl.sql.SQLQuery;

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
import play.i18n.Messages;
import play.libs.F.Promise;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.mvc.Controller;
import play.mvc.Result;
import util.QueryDSL;
import views.html.index;

public class Application extends Controller {
	@Inject Configuration config;
	@Inject WSClient ws;
	@Inject QueryDSL q;
	
	public Promise<Result> index() {
		final String logo = config.getString("dashboard.client.logo");
		
		return getDataSources().map(dataSources -> {
			return ok(index.render(logo, 
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
				String json = new String(response.asByteArray());
				
				try {
					DataSource[] dataSources = gson.fromJson(json, DataSource[].class);
					
					if(dataSources != null) {
						return Arrays.asList(dataSources);
					}
				} catch(JsonSyntaxException jse) {
					jse.printStackTrace();
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
		
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		List<PublisherTask> publisherTasks = new ArrayList<PublisherTask>();
		
		try {
			Class.forName("org.postgresql.Driver");
			
			conn = 
					DriverManager
						.getConnection("jdbc:postgresql://pub.db/publisher", databaseUser, databasePassword);
			
			String sql = 
					"select js.job_id, js.state, js.create_time, j.type from publisher.job_state js "
						+ "join publisher.job j on j.id = js.job_id "
						+ "left join publisher.service_job sj on sj.job_id = js.job_id "
						+ "where js.state != ? "
						+ "and (j.type = ? or j.type = ? or j.type = ?) "
						+ "and (sj.type = ? or sj.type is null) "
						+ "order by js.create_time desc "
						+ "limit 10;";
			
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "STARTED");
			stmt.setString(2, "HARVEST");
			stmt.setString(3, "IMPORT");
			stmt.setString(4, "SERVICE");
			stmt.setString(5, "ENSURE");
			
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				int jobId = rs.getInt("job_id");
				String state = rs.getString("state");
				Timestamp createTime = rs.getTimestamp("create_time");
				String type = rs.getString("type");
				
				String name = getPublisherTaskName(conn, type, jobId);
				
				publisherTasks.add(
						new PublisherTask(type.toLowerCase(), 
							name, 
							createTime.toLocalDateTime(), 
							"SUCCEEDED".equals(state)));
			}
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		}  finally {
			try {
				if(rs != null) {
					rs.close();
				}
				
				if(stmt != null) {
					stmt.close();
				}
				
				if(conn != null) {
					conn.close();
				}
			} catch(SQLException se) {
				se.printStackTrace();
			}
			
			return publisherTasks;
		}
	}
	
	private String getPublisherTaskName(Connection conn, String type, int jobId) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		String publisherTaskName = "unknown";
		
		try {
			if("HARVEST".equals(type)) {
				String sql = "select ds.name as name from publisher.harvest_job hj "
						+ "join publisher.data_source ds on ds.id = hj.data_source_id "
						+ "where hj.job_id = ?";
				
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, jobId);
				rs = stmt.executeQuery();
				
				rs.next();
				
				publisherTaskName = rs.getString("name");
			} else if("IMPORT".equals(type)) {
				String sql = "select ij.job_id, d.name as name from publisher.import_job ij "
						+ "join publisher.dataset d on d.id = ij.dataset_id "
						+ "where ij.job_id = ?";
				
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, jobId);
				rs = stmt.executeQuery();
				
				rs.next();
				
				publisherTaskName = rs.getString("name");
			} else if("SERVICE".equals(type)) {
				String sql = "select gl.name as name, sj.published published "
						+ "from publisher.service_job sj "
						+ "join publisher.service s on s.id = sj.service_id "
						+ "join publisher.generic_layer gl on gl.id = s.generic_layer_id "
						+ "where sj.job_id = ?";
				
				stmt = conn.prepareStatement(sql);
				stmt.setInt(1, jobId);
				rs = stmt.executeQuery();
				
				rs.next();
				
				String name = rs.getString("name");
				boolean published = rs.getBoolean("published");
				
				if(published) {
					publisherTaskName = name + " (" + 
							Messages.get("index.publishertasks.service.published") + 
							")";
				} else {
					publisherTaskName = name + " (" + 
							Messages.get("index.publishertasks.service.staging") + 
							")";
				}
			}
		} catch(SQLException se) {
			se.printStackTrace();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(rs != null) {
					rs.close();
				}
				
				if(stmt != null) {
					stmt.close();
				}
			} catch(SQLException se) {
				se.printStackTrace();
			}
			
			return publisherTaskName;
		}
	}
	
	private MetadataInfo getMetadataInfo(String metadataType) {
		return q.withTransaction(tx -> {
			Tuple lastTuple = tx.select(harvestSession.internCount, harvestSession.externCount)
						.from(harvestSession)
						.where(harvestSession.type.eq(metadataType))
						.orderBy(harvestSession.createTime.desc())
						.fetchFirst();
			
			if(lastTuple != null) {
				int lastCountExtern = lastTuple.get(harvestSession.externCount);
				int lastCountIntern = lastTuple.get(harvestSession.internCount);
				
				InfoLast infoLast = new InfoLast(lastCountExtern, lastCountIntern);
				
				LocalDateTime now = LocalDateTime.now();
				InfoFromTime weekAgoInfo = getPortalInfoFromTime(
						metadataType, infoLast, now, 1, ChronoUnit.WEEKS);
				InfoFromTime dayAgoInfo = getPortalInfoFromTime(
						metadataType, infoLast, now, 1, ChronoUnit.DAYS);
				InfoFromTime hourAgoInfo = getPortalInfoFromTime(
						metadataType, infoLast, now, 1, ChronoUnit.HOURS);
				
				if("dataset".equals(metadataType)) {
					return new DatasetInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
				} else if("service".equals(metadataType)) {
					return new ServiceInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
				} else if("dc".equals(metadataType)) {
					return new DCInfo(infoLast, weekAgoInfo, dayAgoInfo, hourAgoInfo);
				}
			}
			
			return null;
		});
	}
	
	private InfoFromTime getPortalInfoFromTime(String metadataType,
			InfoLast infoLast,
			LocalDateTime now,
			int amountToSubtract,
			ChronoUnit unit) {
				LocalDateTime timeAgo = now.minus(1, unit);
				
				return q.withTransaction(tx -> {
					Tuple timeTuple = tx.select(harvestSession.internCount, harvestSession.externCount)
								.from(harvestSession)
								.where(harvestSession.type.eq(metadataType)
										.and(harvestSession.createTime.loe(Timestamp.valueOf(timeAgo))))
								.orderBy(harvestSession.createTime.desc())
								.fetchFirst();
					
					if(timeTuple != null) {
						int countExtern = timeTuple.get(harvestSession.externCount);
						int countIntern = timeTuple.get(harvestSession.internCount);
						
						CountDifference countDifferenceExtern = 
								getPortalCountDifference(infoLast.getCountExtern(), countExtern);
						
						CountDifference countDifferenceIntern = 
								getPortalCountDifference(infoLast.getCountIntern(), countIntern);
						
						return new InfoFromTime(countDifferenceExtern, countDifferenceIntern);
					} else {
						return null;
					}
				});
	}
	
	private CountDifference getPortalCountDifference(int lastCount, int timeCount) {
		if(lastCount > timeCount) {
			return new CountDifference(CountDifferenceType.ADDITION, lastCount - timeCount);
		} else if(lastCount < timeCount) {
			return new CountDifference(CountDifferenceType.SUBTRACTION, timeCount - lastCount);
		} else {
			return new CountDifference(CountDifferenceType.NOCHANGE, 0);
		}
	}
}
