package controllers;

import static models.QUser.user;
import static models.QRole.role1;

import javax.inject.Inject;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import actions.DefaultAuthenticator;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.QueryDSL;

/**
 * The class for the add user entity
 * 
 * @author Sandro
 *
 */
@Security.Authenticated(DefaultAuthenticator.class)
public class AddUser extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * Create a supplier
	 * 
	 * @return the {@link Result} of the index page
	 */
	public Result execute() {
		// Fetch the formdata
		DynamicForm requestData = Form.form().bindFromRequest();
		String newSupplierName = requestData.get("newSupplierName").trim();
		String newSupplierEmail = requestData.get("newSupplierEmail").trim();
		String newSupplierPassword = requestData.get("newSupplierPassword").trim();
		
		// Check if one field isn't filled
		if("".equals(newSupplierName) || 
			"".equals(newSupplierEmail) || 
			"".equals(newSupplierPassword)) {
				return internalServerError("every field needs to be filled");
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		
		return q.withTransaction(tx -> {
			// Check what the role is of the user that executes this request
			String loggedInRole = tx
				.select(role1.role)
				.from(user)
				.join(role1).on(role1.id.eq(user.roleId))
				.where(user.username.equalsIgnoreCase(session("username")))
				.fetchOne();
			
			// Only allow admins to execute this request
			if(!"admin".equals(loggedInRole)) return forbidden("you don't have permission to do this");
			
			// Check if the given name or email already exists
			long nameCount = tx
				.select(user.label)
				.from(user)
				.where(user.label.equalsIgnoreCase(newSupplierName))
				.fetchCount();
			
			long emailCount = tx
					.select(user.username)
					.from(user)
					.where(user.username.equalsIgnoreCase(newSupplierEmail))
					.fetchCount();
				
			if(nameCount > 0) return internalServerError("the name already exists");
			else if(emailCount > 0) return internalServerError("the email already exists");
			
			// Fetch the role id of supplier
			int roleId = tx
				.select(role1.id)
				.from(role1)
				.where(role1.role.equalsIgnoreCase("supplier"))
				.fetchOne();
			
			// Insert the new supplier
			tx.insert(user)
				.set(user.username, newSupplierEmail)
				.set(user.password, encoder.encode(newSupplierPassword))
				.set(user.roleId, roleId)
				.set(user.label, newSupplierName)
				.set(user.archived, false)
				.execute();
			
			// Return index page
			return redirect(controllers.routes.Index.index("", "none", "none", "", "", "", "", "dateDesc", ""));
		});
	}
}