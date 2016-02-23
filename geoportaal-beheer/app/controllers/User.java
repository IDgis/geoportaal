package controllers;

import static models.QUser.user;
import static models.QRole.role1;

import java.util.List;

import javax.inject.Inject;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.querydsl.core.Tuple;

import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.Routes;
import play.mvc.Result;
import util.QueryDSL;
import play.data.validation.Constraints;

public class User extends Controller {
	@Inject QueryDSL q;
	
	public Result login (final String r) {
		final Form<Login> loginForm = Form.form(Login.class).fill(new Login(r));
		
		return ok(views.html.login.render(loginForm));
	}
	
	public Result authenticate() {
		final Form<Login> loginForm = Form.form(Login.class).bindFromRequest();
		
		validate(loginForm);
		
		if(loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm));
		} else {
			session().clear();
			session("username", loginForm.get().username);
			
			if(loginForm.get().getReturnUrl() != null) {
				return redirect(loginForm.get().getReturnUrl ());
			} else {
				return redirect(controllers.routes.Index.index("", "none", "none", "none", "", ""));
			}
		}
	}
	
	public void validate(Form<Login> loginForm) {
		q.withTransaction(tx -> {
			String dbPassword = tx
				.select(user.password)
				.from(user)
				.where(user.username.eq(loginForm.get().username))
				.fetchOne();
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if(dbPassword == null || !encoder.matches(loginForm.get().password, dbPassword)) {
				loginForm.reject("Ongeldige gebruikersnaam of wachtwoord");
			}
		});
	}
	
	public Result logout () {
		session().clear();
		return redirect(controllers.routes.Index.index("", "none", "none", "none", "", ""));
	}
	
	public Result renderChangePassword() {
		final Form<ChangePassword> cpForm = Form.form(ChangePassword.class);
		
		return ok(views.html.changepassword.render(cpForm));
	}
	
	public Result changePassword() {
		final Form<ChangePassword> cpForm = Form.form(ChangePassword.class).bindFromRequest();
		
		if("".equals(cpForm.get().username) || "".equals(cpForm.get().oldPassword) || "".equals(cpForm.get().newPassword) || 
			"".equals(cpForm.get().repeatNewPassword)) {
			cpForm.reject("Alle velden moeten ingevuld zijn");
		}
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		q.withTransaction(tx -> {
			String dbPassword = tx
				.select(user.password)
				.from(user)
				.where(user.username.eq(cpForm.get().username))
				.fetchOne();
			
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			if(dbPassword == null || !encoder.matches(cpForm.get().oldPassword, dbPassword)) {
				cpForm.reject("Gebruikersnaam en wachtwoord komen niet overeen");
			}
		});
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		if(!cpForm.get().newPassword.equals(cpForm.get().repeatNewPassword)) {
			cpForm.reject("Nieuwe wachtwoord en herhaling komen niet overeen");
		}
		
		if(cpForm.hasErrors()) {
			return badRequest(views.html.changepassword.render(cpForm));
		}
		
		q.withTransaction(tx -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			String encodedNP = encoder.encode(cpForm.get().newPassword);
			tx.update(user)
				.set(user.password, encodedNP)
				.where(user.username.eq(cpForm.get().username))
				.execute();
		});
		
		return redirect(controllers.routes.Index.index("", "none", "none", "none", "", ""));
	}
	
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
}