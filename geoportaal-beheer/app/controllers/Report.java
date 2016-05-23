package controllers;

import static models.QUser.user;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

import javax.inject.Inject;

import org.joda.time.LocalDate;

import actions.DefaultAuthenticator;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;

@Security.Authenticated(DefaultAuthenticator.class)
public class Report extends Controller {
	@Inject QueryDSL q;
	
	private BufferedWriter csvWriter;
	
	/**
	 * The rendering of the report page
	 * 
	 * @return the {@link Result} of the report page
	 */
	public Result renderReport() {
		return q.withTransaction(tx -> {
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			if(!roleId.equals(2)) {
				return ok(views.html.report.render(roleId));
			} else {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
		});
	}
	
	public Result writeCSV() throws Exception {
		LocalDate ld = LocalDate.now();
		
		response().setContentType("text/csv");
		response().setHeader("Content-Disposition", "attachment; filename=\"rapport_dublincore_" + ld.getYear() + ld.getMonthOfYear() + 
				ld.getDayOfMonth() + ".csv\"");
		
		String header = "\"file_name\";" + "\"title\";\"creator\";\"subject\";\"description\";\"publisher\";\"contributor\";\"date\";\"issued\";"
				+ "\"valid_start\";\"valid_end\";\"type\";\"format\";\"identifier\";\"references\";\"relation_id\";\"source\";\"language\";"
				+ "\"relation_attachment\";\"http_status_code\";\"http_content_length_in_mb\";\"rights_file\";\"rights_use\";\"temporal_start\";"
				+ "\"temportal_end\";\"bbox_lowercorner\";\"bbox_uppercorner\"";
		
		StringBuilder strb = new StringBuilder();
		strb.append(header);
		strb.append(System.lineSeparator());
		
		return q.withTransaction(tx -> {
			// Fetches the role of the logged in user
			Integer roleId = tx.select(user.roleId)
				.from(user)
				.where(user.username.eq(session("username")))
				.fetchOne();
			
			if(!roleId.equals(2)) {
				return ok(strb.toString().getBytes());
			} else {
				return status(UNAUTHORIZED, Messages.get("unauthorized"));
			}
		});
	}
}
