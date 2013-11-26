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
			if (user.getType() == 1)
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
				salt.toString(), 1);
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
	public String logOnDropbox(String code){
	      
	      DbxAppInfo appInfo = new DbxAppInfo(StaticData.getAPP_KEY(), StaticData.getAPP_SECRET());

	      DbxRequestConfig config = new DbxRequestConfig(StaticData.getPROJECT_NAME(),
	      Locale.getDefault().toString());
	      DbxWebAuthNoRedirect webAuth = new DbxWebAuthNoRedirect(config, appInfo);
	      DbxAuthFinish authFinish;		
				try {
					authFinish = webAuth.finish(code);
				    DbxClient client = new DbxClient(config, authFinish.accessToken);
				    MySQLAccess sql = new MySQLAccess();
				    sql.setDropboxToken(authFinish.accessToken);
				    currentClient = client;
			        return ("Linked account:" + client.getAccountInfo().displayName);
				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		

		   return null;
	}
	
	@Override
	public String logOnDropboxWithoutToken() {
		DbxRequestConfig config = new DbxRequestConfig(
				StaticData.getPROJECT_NAME(), Locale.getDefault().toString());
		MySQLAccess sql = new MySQLAccess();
		String token = sql.getDropboxToken();
		DbxClient client = new DbxClient(config, token);
		currentClient = client;
		try {
			return ("Linked account:"+ client.getAccountInfo().displayName);
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
			if (applicationUser.getType() == 1)
				listing = currentClient.getMetadataWithChildren("/"
					+ applicationUser.getLogin());
			else 
				listing = currentClient.getMetadataWithChildren("/");
						
			
			DbxEntry.WithChildren childrenListing;
			System.out.println("Files in the root path:");
			for (DbxEntry child : listing.children) {
				if (child.isFolder()){
					childrenListing = currentClient.getMetadataWithChildren("/"
							+ child.name);
					for  (DbxEntry children : childrenListing.children) {
						Invoice inv = new Invoice();
						inv.setName(children.name + " - file for user - " + child.name);
						result.add(inv);
						System.out.println("	" + children.name + ": " + children.toString());
						System.out.println("ŚCIEZKA --->" + children.path);
					}
				} else {
					Invoice invoice = new Invoice();
					invoice.setName(child.name);
					result.add(invoice);
					System.out.println("	" + child.name + ": " + child.toString());
					System.out.println("ŚCIEZKA --->" + child.path);
				}
			}
			
			java.util.Collections.sort(result, new Comparator<Invoice>(){

				@Override
				public int compare(Invoice arg0, Invoice arg1) {
					if (applicationUser.getType() == 1)
						return arg0.getName().compareTo(arg1.getName());
					else 
						return arg0.getName().split(" - ")[2].compareTo(arg1.getName().split(" - ")[2]);					
				}
				
			});

			return result;
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	@Override
	public boolean downloadFile(String name) {
		FileOutputStream outputStream = null;
		try {
			
			File dir = new File("C:\\invoices");
			if (!dir.exists()) {
				dir.mkdir();
			}
			
			outputStream = new FileOutputStream("C:\\invoices\\"+name.split(" - ")[0]);
			DbxEntry.File downloadedFile;
			
			if (name.contains(" - ")){
			
			downloadedFile = currentClient.getFile(
					"/" + name.split(" - ")[2] + "/" + name.split(" - ")[0], null,
					outputStream);
			
			} else {
				downloadedFile = currentClient.getFile(
						"/" + applicationUser.getLogin() + "/" + name, null,
						outputStream);
			}
//			System.out.println(downloadedFile.Reader.toString());
			
			System.out.println("Metadata: " + downloadedFile.toString());
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		finally {
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	@Override
	public boolean uploadFile(String name, String content, String userLogin) throws IOException{
		try {
		File file = new File (name+".txt");
		if (!file.exists()){
			file.createNewFile();
		} 
		
		FileWriter writer = new FileWriter(file.getName(), true);
		BufferedWriter bufferWriter = new BufferedWriter(writer);
		bufferWriter.write(content);
		bufferWriter.close();
		
		FileInputStream uploadFile = new FileInputStream(file);
		try{			
			currentClient.uploadFile("/"+userLogin+"/"+name+".txt", DbxWriteMode.force(), file.length(), uploadFile);			
			return true;
		} catch (DbxException e){
			e.printStackTrace();
			uploadFile.close();
			return false;
		}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public String getToken(){
		MySQLAccess sql = new MySQLAccess();
		return sql.getDropboxToken();
	}

}
