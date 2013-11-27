package pl.edu.agh.tai.jdbc.client;

import java.io.IOException;
import java.util.List;

import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {

	/**
	 * Log in system
	 * 
	 * @param login
	 *            user login
	 * @param password
	 *            user password
	 * @return success or fail
	 */
	public boolean login(String login, String password);

	/**
	 * Is user logged in
	 * 
	 * @return true or false
	 */
	public Boolean isLoggedIn();

	/**
	 * Try login to system
	 * 
	 * @param login
	 *            user login
	 * @param password
	 *            user password
	 * @param rememberMe
	 *            whether needed to remember user
	 * @return success or fail
	 */
	public Boolean tryLogin(String login, String password, Boolean rememberMe);

	/**
	 * Logout from system
	 */
	public void logout();

	/**
	 * Registrate new user
	 * 
	 * @param name
	 *            user name
	 * @param surname
	 *            user surname
	 * @param login
	 *            user login
	 * @param password
	 *            user password
	 */

	void registrate(String name, String surname, String login,
			String plainTextPassword, int type);

	/**
	 * Get Dropbox authorization link
	 * 
	 * @return link
	 */
	public String getAuthorizationLink();

	/**
	 * Get file list in system
	 * 
	 * @return file list
	 */
	public List<Invoice> getFileList();

	/**
	 * Get current application user
	 * 
	 * @return info about user
	 */
	public User getApplicationUser();

	/**
	 * Download file
	 * 
	 * @param name
	 *            name of file
	 * @return success or fail
	 */
	public boolean downloadFile(String name);

	/**
	 * Log in dropbox with token authorization
	 * 
	 * @return dropbox account owner
	 */
	public String logOnDropbox(String code);


	/**
	 * Get all user names
	 * 
	 * @return users names
	 * @throws Exception
	 */
	public List<String> getUsersNames();

	/**
	 * Log in dropbox without token authorization
	 * 
	 * @return dropbox account owner
	 */
	public String logOnDropboxWithoutToken();

	/**
	 * 
	 * @param name
	 * 		name of file
	 * @param content
	 * 		content of file
	 * @param userLogin
	 * 		user login
	 * @return
	 * 		true is file will be saved, otherwise false
	 * @throws IOException
	 */
	public boolean uploadFile(String name, String content, String userLogin)
			throws IOException;

	/**
	 * Getting token to authorization Dropbox connection
	 * @return
	 * 		token from database
	 */
	public String getToken();


	
}
