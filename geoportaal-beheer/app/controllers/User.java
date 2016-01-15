package controllers;

import static models.QUser.user;
import static models.QRole.role1;

import java.util.List;

import javax.inject.Inject;

import com.querydsl.core.Tuple;

import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.Routes;
import play.mvc.Result;
import play.data.validation.Constraints;

public class User extends Controller {
	@Inject Database db;
	
	public Result login (final String r) {
		final Form<Login> loginForm = Form.form(Login.class).fill(new Login(r));
		
		return ok(views.html.login.render(loginForm));
	}
	
	public Result authenticate() {
		final Form<Login> loginForm = Form.form(Login.class).bindFromRequest ();
		
		validate(loginForm);
		
		if (loginForm.hasErrors()) {
			return badRequest(views.html.login.render(loginForm));
		} else {
			session().clear ();
			session("username", loginForm.get().username);
			
			if(loginForm.get().getReturnUrl() != null) {
				return redirect(loginForm.get().getReturnUrl ());
			} else {
				return redirect(routes.Index.index ());
			}
		}
	}
	
	public void validate(Form<Login> loginForm) {		
		String dbPassword = db.queryFactory
			.select(user.password)
			.from(user)
			.where(user.username.eq(loginForm.get().username))
			.fetchOne();
		
		if(dbPassword == null || !dbPassword.equals(loginForm.get().password)) {
			loginForm.reject("Ongeldige gebruikersnaam of wachtwoord");
		}
	}
	
	public Result logout () {
		session().clear();
		return redirect(routes.Index.index ());
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
}