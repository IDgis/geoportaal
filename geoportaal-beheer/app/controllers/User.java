package controllers;

import static models.QUser.user;
import static models.QRole.role1;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.querydsl.core.Tuple;

import nl.idgis.commons.utils.Mail;
import play.i18n.*;
import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.Routes;
import play.mvc.Result;
import util.QueryDSL;
import play.data.validation.Constraints;

/**
 * The class for the user entity
 * 
 * @author Sandro
 *
 */
public class User extends Controller {
	@Inject QueryDSL q;
	
	/**
	 * Renders the login page
	 * 
	 * @param r the return url
	 * @return the {@link Result} of the login page
	 */
	public Result login(final String r) {
		// Create a login form and fill in the saved fields
		final Form<Login> loginForm = Form.form(Login.class).fill(new Login(r));
		
		// Fetches the change password and forgot password values, if null they result in an empty string
		String cpMsg = session("changePassword");
		String fpMsg = session("forgotPassword");
		
		if(cpMsg == null) {
			cpMsg = "";
		}
		
		if(fpMsg == null) {
			fpMsg = "";
		}
		
		// Empties the change password and forgot password keys
		session("changePassword", "");
		session("forgotPassword", "");
		
		// Returns the login page
		return ok(views.html.login.render(loginForm, cpMsg, fpMsg));
	}
	
	/**
	 * Authentication of the user
	 * 
	 * @return the {@link Result} of the authentication handling: either the login page with an error message, the previous request or the index page
	 */
	public Result authenticate() {
		// Create a login form object from the submitted form
		final Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		
		// Call the validate method
		validate(loginForm);
		
		// Check if form has errors
		if(loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm, "", ""));
		} else {
			// Clear the session and set the username key to the logged in username
			session().clear();
			session("username", loginForm.get().username.trim());
			
			// Check if getReturnUrl method is null, if so the URL results into the index method
			if(loginForm.get().getReturnUrl() != null) {
				return redirect(loginForm.get().getReturnUrl());
			} else {
				return redirect(controllers.routes.Index.index("", "none", "none", "none", "", "", "dateDesc", ""));
			}
		}
	}
	
	/**
	 * Checks if the given password belongs to the username
	 * 
	 * @param loginForm the login form where the username and password is stored
	 */
	public void validate(Form<Login> loginForm) {
		q.withTransaction(tx -> {
			String username = loginForm.get().username.trim();
			
			// Fetches the password that belongs to the username
			String dbPassword = tx
				.select(user.password)
				.from(user)
				.where(user.username.equalsIgnoreCase(username))
				.fetchOne();
			
			// Create a BCrypt encoder
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			
			// Checks if there is a password at all or if the password matches to the one from storage
			if(dbPassword == null || !encoder.matches(loginForm.get().password, dbPassword)) {
				loginForm.reject(Messages.get("login.error.message"));
			}
		});
	}
	
	/**
	 * Log out of the admin
	 * 
	 * @return the {@link Result} of the index page (because the user is logged out it essentially defaults back to the login page)
	 */
	public Result logout() {
		// Clear all keys from the session
		session().clear();
		
		// Returns the internal portal page
		return redirect(Play.application().configuration().getString("geoportaal.internal.url"));
	}
	
	/**
	 * Render the change password page
	 * 
	 * @return the {@link Result} of the change password page
	 */
	public Result renderChangePassword() {
		// Create the form object of change password
		final Form<ChangePassword> cpForm = Form.form(ChangePassword.class);
		
		// Returns the change password page
		return ok(views.html.changepassword.render(cpForm));
	}
	
	/**
	 * Changing the password in the database if form is correctly filled out
	 * 
	 * @return the {@link Result} of the login page with a message about the success of changing the password
	 */
	public Result changePassword() {
		// Create a change password form object from the submitted form
		final Form<ChangePassword> cpForm = Form.form(ChangePassword.class).bindFromRequest();
		
		// Reject and return with a message if one of the input fields is empty
		if("".equals(cpForm.get().username.trim()) || "".equals(cpForm.get().oldPassword.trim()) || "".equals(cpForm.get().newPassword.trim()) || 
			"".equals(cpForm.get().repeatNewPassword.trim())) {
			cpForm.reject(Messages.get("password.edit.error.incomplete.message"));
		}
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		q.withTransaction(tx -> {
			// Fetch the password that belongs to the given username
			String dbPassword = tx
				.select(user.password)
				.from(user)
				.where(user.username.eq(cpForm.get().username.trim()))
				.fetchOne();
			
			// Reject and return with a message if the username isn't known or if the username and old password don't match
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if(dbPassword == null || !encoder.matches(cpForm.get().oldPassword, dbPassword)) {
				cpForm.reject(Messages.get("password.edit.error.mismatch.message"));
			}
		});
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		// Reject and return with a message if the new password and the repeating of the new password don't match
		if(!cpForm.get().newPassword.equals(cpForm.get().repeatNewPassword)) {
			cpForm.reject(Messages.get("password.edit.error.repeat.message"));
		}
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		q.withTransaction(tx -> {
			// Encode the new password and update password in the database
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedNP = encoder.encode(cpForm.get().newPassword);
			tx.update(user)
				.set(user.password, encodedNP)
				.where(user.username.eq(cpForm.get().username.trim()))
				.execute();
		});
		
		// Set the changepassword key in the session for displaying success message
		session("changePassword", Messages.get("password.edit.success"));
		
		// Return the index page, which defaults to login page because the user is not logged in
		return redirect(controllers.routes.Index.index("", "none", "none", "none", "", "", "dateDesc", ""));
	}
	
	/**
	 * Render the forgot password page
	 * 
	 * @return the {@link Result} of the forgot password page
	 */
	public Result renderForgotPassword() {
		// Create the form object of forgot password
		final Form<ForgotPassword> fpForm = Form.form(ForgotPassword.class);
		
		// Return the forgot password page
		return ok(views.html.forgotpassword.render(fpForm));
	}
	
	/**
	 * Sending an e-mail with a new password if the given username is known
	 * 
	 * @return the {@link Result} of the login page with a message about the success of sending the e-mail
	 */
	public Result forgotPassword() {
		// Create a forgot password form object from the submitted form
		final Form<ForgotPassword> fpForm = Form.form(ForgotPassword.class).bindFromRequest();
		
		// Fetches the username and password of the e-mail client
		final String emailUsername = Play.application().configuration().getString("geoportaal.email.username");
		final String emailPassword = Play.application().configuration().getString("geoportaal.email.password");
		
		// Generates a String and an encoded version of that string
		String password = RandomStringUtils.randomAlphanumeric(10);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodedPW = encoder.encode(password);
		
		q.withTransaction(tx -> {
			// Updates password in the database
			Long count = tx.update(user)
				.set(user.password, encodedPW)
				.where(user.username.eq(fpForm.get().username.trim()))
				.execute();
			
			Integer finalCount = count.intValue();
			if(finalCount.equals(1)) {
				// Create a hashmap with a password key and value
				Map<String, Object> placeholders = new HashMap<String, Object>();
				placeholders.put("password", password);
				
				// Format the message of the e-mail
				String msg = Mail.createMsg(placeholders, Messages.get("password.forgot.email.message", "${password}"));
				try {
					// Send the e-mail
					Mail.send(emailUsername, emailPassword, "mail.your-server.de", 25, fpForm.get().username.trim(), emailUsername, 
						Messages.get("password.forgot.email.subject"), msg);
				} catch(Exception e) {
					throw e;
				}
			}
			
			// Throw exception if the count of the affected rows is more than 1
			if(finalCount > 1) {
				throw new Exception("Resetting password: too many rows affected");
			}
		});
		
		// Set the forgotpassword key in the session for displaying success message
		session("forgotPassword", Messages.get("password.forgot.success"));
		
		// Return the index page, which defaults to login page because the user is not logged in
		return redirect(controllers.routes.Index.index("", "none", "none", "none", "", "", "dateDesc", ""));
	}
	
	public Result loginHelp() {
		return ok(views.html.loginhelp.render());
	}
	
	/**
	 * The login form
	 * 
	 * @author Sandro
	 *
	 */
	public static class Login {
		@Constraints.Required
		private String username;
		
		private String password;
		
		private String returnUrl;
		
		public Login() {
		}
		
		public Login(final String returnUrl) {
			this.returnUrl = returnUrl;
		}
		
		public String getUsername() {			
			return username;
		}
		
		public void setUsername(final String username) {
			this.username = username;
		}
		
		public String getPassword() {
			return password;
		}
		
		public void setPassword(final String password) {
			this.password = password;
		}
		
		public String getReturnUrl() {
			return returnUrl;
		}
		
		public void setReturnUrl(final String returnUrl) {
			this.returnUrl = returnUrl;
		}
	}
	
	/**
	 * The change password form
	 * 
	 * @author Sandro
	 *
	 */
	public static class ChangePassword {
		private String username;
		private String oldPassword;
		private String newPassword;
		private String repeatNewPassword;
		
		public ChangePassword() {
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(final String username) {
			this.username = username;
		}
		
		public String getOldPassword() {
			return oldPassword;
		}
		
		public void setOldPassword(final String oldPassword) {
			this.oldPassword = oldPassword;
		}
		
		public String getNewPassword() {
			return newPassword;
		}
		
		public void setNewPassword(final String newPassword) {
			this.newPassword = newPassword;
		}
		
		public String getRepeatNewPassword() {
			return repeatNewPassword;
		}
		
		public void setRepeatNewPassword(final String repeatNewPassword) {
			this.repeatNewPassword = repeatNewPassword;
		}
	}
	
	/**
	 * The forgot password form
	 * 
	 * @author Sandro
	 *
	 */
	public static class ForgotPassword {
		private String username;
		
		public ForgotPassword() {
		}
		
		public String getUsername() {
			return username;
		}
		
		public void setUsername(final String username) {
			this.username = username;
		}
	}
}