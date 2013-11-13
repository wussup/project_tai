package pl.edu.agh.tai.jdbc.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import pl.edu.agh.tai.jdbc.shared.Invoice;
import pl.edu.agh.tai.jdbc.shared.StaticData;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxAuthFinish;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWebAuthNoRedirect;
import com.dropbox.core.DbxWriteMode;
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
	private DbxClient currentClient;
	private User applicationUser;

	public GreetingServiceImpl() {
		// IniSecurityManagerFactory factory = new IniSecurityManagerFactory();
		Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory();
		org.apache.shiro.mgt.SecurityManager securityManager = factory
				.getInstance();
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
			// collect user principals and credentials in a gui specific manner
			// such as username/password html form, X509 certificate, OpenID,
			// etc.
			// We'll use the username/password example here since it is the most
			// common.
			UsernamePasswordToken token = new UsernamePasswordToken(username,
					password);
			// this is all you have to do to support 'remember me' (no config -
			// built in!):
			token.setRememberMe(rememberMe);

			try {
				currentUser.login(token);
				log.info("User [" + currentUser.getPrincipal().toString()
						+ "] logged in successfully.");
				MySQLAccess sql = new MySQLAccess();
				applicationUser = sql.getUserByLogin(username);
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
	public void registrate(String name, String surname, String login,
			String plainTextPassword) {
		RandomNumberGenerator rng = new SecureRandomNumberGenerator();
		Object salt = rng.nextBytes();

		// Now hash the plain-text password with the random salt and multiple
		// iterations and then Base64-encode the value (requires less space than
		// Hex):
		String hashedPasswordBase64 = new Sha256Hash(plainTextPassword, salt,
				1024).toBase64();

		// TODO pobieraæ z GUI typ
		User user = new User(name, surname, login, hashedPasswordBase64,
				salt.toString(), 0);
		this.createUser(user);
	}

	private void createUser(User user) {
		MySQLAccess access = new MySQLAccess();
		access.createUser(user);
		log.info("User with login:" + user.getLogin() + " hashedPassword:"
				+ user.getPassword() + " salt:" + user.getSalt());
	}

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid.
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back
			// to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
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

	@Override
	public User getApplicationUser() {
		return applicationUser;
	}

	@Override
	public String getAuthorizationLink() {

		DbxAppInfo appInfo = new DbxAppInfo(StaticData.getAPP_KEY(),
				StaticData.getAPP_SECRET());

		DbxRequestConfig config = new DbxRequestConfig(
				StaticData.getPROJECT_NAME(), Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

		return webAuth.start();
	}

	@Override
	public String logOnDropbox(String code) {

		DbxAppInfo appInfo = new DbxAppInfo(StaticData.getAPP_KEY(),
				StaticData.getAPP_SECRET());

		DbxRequestConfig config = new DbxRequestConfig(
				StaticData.getPROJECT_NAME(), Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
		DbxAuthFinish authFinish;
		try {
			authFinish = webAuth.finish(code);
			DbxClient client = new DbxClient(config, authFinish.accessToken);
			currentClient = client;
			return ("Linked account:" + client.getAccountInfo().displayName);
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public List<Invoice> getFileList() {

		DbxEntry.WithChildren listing;
		List<Invoice> result = new ArrayList<Invoice>();
		try {
			listing = currentClient.getMetadataWithChildren("/"
					+ applicationUser.getLogin());
			System.out.println("Files in the root path:");
			for (DbxEntry child : listing.children) {
				Invoice invoice = new Invoice();
				invoice.setName(child.name);
				result.add(invoice);
				System.out.println("	" + child.name + ": " + child.toString());
			}

			return result;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public void addFile(String name) {
		try {
			String text = "Hapaj dzidke";
			File inputFile = new File("hapaj.txt");
			BufferedWriter output = new BufferedWriter(
					new FileWriter(inputFile));
			output.write(text);
			output.close();
			FileInputStream inputStream = new FileInputStream(inputFile);
			DbxEntry.File uploadedFile = currentClient.uploadFile("/hapaj.txt",
					DbxWriteMode.add(), inputFile.length(), inputStream);
			System.out.println("Uploaded: " + uploadedFile.toString());
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void dropbox() throws IOException {
		// Get your app key and secret from the Dropbox developers website.
		final String APP_KEY = "apm3dfhibte9645";
		final String APP_SECRET = "unq7i41xib8rnto";

		DbxAppInfo appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);

		DbxRequestConfig config = new DbxRequestConfig("JavaTutorial/1.0",
				Locale.getDefault().toString());
		DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);

		// Have the user sign in and authorize your app.
		String authorizeUrl = webAuth.start();
		System.out.println("1. Go to: " + authorizeUrl);
		System.out.println("2. Click 'Allow' (you might have to log in first)");
		System.out.println("3. Copy the authorization code.");
		String code = "EDitTrVSnqYAAAAAAAAAAYfIl6lsR8rJUR9zQ5dWRiI";

		// This will fail if the user enters an invalid authorization code.
		DbxAuthFinish authFinish;
		try {
			authFinish = webAuth.finish(code);

			DbxClient client = new DbxClient(config, authFinish.accessToken);

			System.out.println("Linked account: "
					+ client.getAccountInfo().displayName);

			// File inputFile = new File("working-draft.txt");
			// FileInputStream inputStream = new FileInputStream(inputFile);
			// try {
			// DbxEntry.File uploadedFile =
			// client.uploadFile("/magnum-opus.txt",
			// DbxWriteMode.add(), inputFile.length(), inputStream);
			// System.out.println("Uploaded: " + uploadedFile.toString());
			// } finally {
			// inputStream.close();
			// }

			DbxEntry.WithChildren listing = client
					.getMetadataWithChildren("/TAI");
			System.out.println("Files in the root path:");
			for (DbxEntry child : listing.children) {
				System.out.println("	" + child.name + ": " + child.toString());
			}

			FileOutputStream outputStream = new FileOutputStream(
					"magnum-opus.txt");
			try {
				DbxEntry.File downloadedFile = client.getFile(
						"/magnum-opus.txt", null, outputStream);
				System.out.println("Metadata: " + downloadedFile.toString());
			} finally {
				outputStream.close();
			}
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean downloadFile(String name) {
		FileOutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(name);
			DbxEntry.File downloadedFile;
			downloadedFile = currentClient.getFile(
					"/" + applicationUser.getLogin() + "/" + name, null,
					outputStream);
			System.out.println("Metadata: " + downloadedFile.toString());
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException | IOException e) {
			e.printStackTrace();
		} finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}
