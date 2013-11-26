package pl.edu.agh.tai.jdbc.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
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
import pl.edu.agh.tai.jdbc.shared.Invoice;
import pl.edu.agh.tai.jdbc.shared.StaticData;

import com.dropbox.core.DbxAppInfo;
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
	// private static final String token =
	// "G0jQojXYSYUAAAAAAAAAAX66jXUpnUeAYfR2nAqaFlI5wwyUYjTDas88VV0oW2Vt";
	private static final transient Logger log = LoggerFactory
			.getLogger(GreetingServiceImpl.class);

	private org.apache.shiro.subject.Subject currentUser;
	private DbxClient currentClient;
	private User applicationUser;

	/**
	 * Constructor
	 */
	public GreetingServiceImpl() {
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
	public List<String> getUsersNames() {
		MySQLAccess sql = new MySQLAccess();
		List<String> names = new ArrayList<String>();
		for (User user : sql.getAllUsers()) {
			names.add(user.toString());
		}

		return names;

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

		// TODO pobierac z GUI typ
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
	public String logOnDropbox() {
		DbxRequestConfig config = new DbxRequestConfig(
				StaticData.getPROJECT_NAME(), Locale.getDefault().toString());
		try {
			MySQLAccess sql = new MySQLAccess();
			String token = sql.getDropboxToken();
			if (token != null) {
				DbxClient client = new DbxClient(config, token);

				currentClient = client;
				return ("Linked account:" + client.getAccountInfo().displayName);
			} else {
				// TODO: Dorobic funkcje createTokenCosTam
			}
		} catch (DbxException e) {
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
				System.out.println("�CIEZKA --->" + child.path);
			}

			return result;
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public List<Invoice> getAdminFileList(String folderName) {

		DbxEntry.WithChildren listing;
		List<Invoice> result = new ArrayList<Invoice>();
		try {
			if (folderName == null) {
				listing = currentClient.getMetadataWithChildren("/");
			} else {
				listing = currentClient.getMetadataWithChildren("/"
						+ folderName);
			}
			System.out.println("Files in the root path:");
			for (DbxEntry child : listing.children) {
				Invoice invoice = new Invoice();
				invoice.setName(child.name);
				invoice.setDir(child.isFolder());
				result.add(invoice);
				System.out.println("	" + child.name + ": " + child.toString());
				System.out.println("�CIEZKA --->" + child.path);
			}

			java.util.Collections.sort(result, new Comparator<Invoice>() {

				@Override
				public int compare(Invoice o1, Invoice o2) {
					if (o1.isDir() && !o2.isDir())
						return -1;
					else if (o2.isDir() && !o1.isDir())
						return 1;
					else
						return 0;
				}
			});

			return result;
		} catch (DbxException e) {
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
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean downloadFile(String name) {
		FileOutputStream outputStream = null;
		try {

			File dir = new File("C:\\invoices");
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File("C:\\invoices\\" + name);
			outputStream = new FileOutputStream(file);
			DbxEntry.File downloadedFile;

			downloadedFile = currentClient
					.getFile("/" + applicationUser.getLogin() + "/" + name, null,
							outputStream);
			if (downloadedFile!=null)
			{
				System.out
						.println(com.dropbox.core.DbxEntry.File.Reader.toString());
	
				System.out.println("Metadata: " + downloadedFile.toString());
				return true;
			}
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
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
