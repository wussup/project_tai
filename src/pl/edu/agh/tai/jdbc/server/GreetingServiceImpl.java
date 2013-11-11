package pl.edu.agh.tai.jdbc.server;

import java.util.ArrayList;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.crypto.RandomNumberGenerator;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.apache.shiro.util.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.User;
import pl.edu.agh.tai.jdbc.server.mysql.MySQLAccess;
import pl.edu.agh.tai.jdbc.shared.FieldVerifier;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	private static final long serialVersionUID = -4051026136441981243L;
    private static final transient Logger log = LoggerFactory
            .getLogger(GreetingServiceImpl.class);

    private org.apache.shiro.subject.Subject currentUser;

    public GreetingServiceImpl() {
    	//IniSecurityManagerFactory factory = new IniSecurityManagerFactory();
        Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
        org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();
        SecurityUtils.setSecurityManager(securityManager);
    }

    @Override
    public Boolean isLoggedIn() {
        currentUser = SecurityUtils.getSubject();

        if (currentUser.isAuthenticated()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Boolean tryLogin(String username, String password, Boolean rememberMe) {
        // get the currently executing user:
        currentUser = SecurityUtils.getSubject();

        // let's login the current user so we can check against roles and
        // permissions:
        if (!currentUser.isAuthenticated()) {
             //collect user principals and credentials in a gui specific manner 
            //such as username/password html form, X509 certificate, OpenID, etc.
            //We'll use the username/password example here since it is the most common.
            UsernamePasswordToken token = new UsernamePasswordToken(username,password);
             //this is all you have to do to support 'remember me' (no config - built in!):
            token.setRememberMe(rememberMe);

            try {
                currentUser.login(token);
                log.info("User [" + currentUser.getPrincipal().toString() + "] logged in successfully.");
                return true;
            } catch (UnknownAccountException uae) {
                log.info("There is no user with username of "
                        + token.getPrincipal());
            } catch (IncorrectCredentialsException ice) {
                log.info("Password for account " + token.getPrincipal()
                        + " was incorrect!");
            } catch (LockedAccountException lae) {
                log.info("The account for username " + token.getPrincipal()
                        + " is locked.  "
                        + "Please contact your administrator to unlock it.");
            } catch (AuthenticationException ae) {
                log.error(ae.getLocalizedMessage());
            }
        }

        return false;
    }

    @Override
    public void logout() {
        currentUser = SecurityUtils.getSubject();
        currentUser.logout();
    }

    @Override
    public void registrate(String name, String surname, String login, String plainTextPassword) {
        RandomNumberGenerator rng = new SecureRandomNumberGenerator();
        Object salt = rng.nextBytes();

        // Now hash the plain-text password with the random salt and multiple
        // iterations and then Base64-encode the value (requires less space than Hex):
        String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt,1024).toBase64();

        User user = new User(name, surname, login, hashedPasswordBase64, salt.toString());
        this.createUser(user);
    }

    private void createUser(User user) {
    	MySQLAccess access = new MySQLAccess();
    	access.createUser(user);
        log.info("User with login:" + user.getLogin() + " hashedPassword:"+ user.getPassword() + " salt:" + user.getSalt());
    }
	
	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	@Override
	public ArrayList<User> getUser() {
		MySQLAccess access = new MySQLAccess();
		try {
			return access.readDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean login(String login, String password) {
		MySQLAccess access = new MySQLAccess();
		return access.login(login, password);
	}
}
