package controllers;

import play.Play;
import play.data.Form;
import play.mvc.Controller;
import play.Routes;
import play.mvc.Result;
import play.data.validation.Constraints;

public class User extends Controller {
	
	private static String configUsername = Play.application().configuration().getString("geoportaal.admin.username");
	private static String configPassword = Play.application().configuration().getString("geoportaal.admin.password");

	public Result login (final String r) {
		final Form<Login> loginForm = Form.form(Login.class).fill(new Login(r));
		
		return ok(views.html.login.render(loginForm));
	}
	
	public Result authenticate() {
		final Form<Login> loginForm = Form.form(Login.class).bindFromRequest ();
		
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
		
		public String validate() {
			if (!configUsername.equals(username) || !configPassword.equals(password)) {
				return "Ongeldige gebruikersnaam of wachtwoord";
			}
			
			return null;
		}
	}
}