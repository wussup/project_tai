package pl.edu.agh.tai.jdbc.server;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.jdbc.JdbcRealm;

import pl.edu.agh.tai.jdbc.client.User;
import pl.edu.agh.tai.jdbc.server.mysql.MySQLAccess;

/**
 * Apache Shiro helper
 * 
 * @since 26.11.2013
 * @author Taras Melon&Jakub Kolodziej
 */
public class MyRealm extends JdbcRealm {

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(
			AuthenticationToken token) throws AuthenticationException {
		// identify account to log to
		UsernamePasswordToken userPassToken = (UsernamePasswordToken) token;
		final String username = userPassToken.getUsername();

		if (username == null) {
			return null;
		}

		// read password hash and salt from db
		final PasswdSalt passwdSalt = getPasswordForUser(username);

		if (passwdSalt == null) {
			return null;
		}

		// return salted credentials
		SaltedAuthenticationInfo info = new MySaltedAuthentificationInfo(
				username, passwdSalt.password, passwdSalt.salt);

		return info;
	}

	private PasswdSalt getPasswordForUser(String username) {
		User user = getUserByLogin(username);
		if (user == null) {
			return null;
		}
		return new PasswdSalt(user.getPassword(), user.getSalt());
	}

	private User getUserByLogin(String login) {
		MySQLAccess access = new MySQLAccess();
		User user = access.getUserByLogin(login);
		return user;
	}

	class PasswdSalt {
		public String password;
		public String salt;

		public PasswdSalt(String password, String salt) {
			super();
			this.password = password;
			this.salt = salt;
		}
	}

}
