package pl.edu.agh.tai.jdbc.client;

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
	public void registrate(String name, String surname, String login,
			String password);

	/**
	 * Get Dropbox authorization link
	 * 
	 * @return link
	 */
	public String getAuthorizationLink();

	/**
	 * Add file to system
	 * 
	 * @param name
	 *            name of file
	 */
	public void addFile(String name);

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
	 * Log in dropbox
	 * 
	 * @return dropbox account owner
	 */
	public String logOnDropbox();

	/**
	 * Get admin file list
	 * 
	 * @param folderName
	 *            name of folder
	 * @return list of invoices
	 */
	public List<Invoice> getAdminFileList(String folderName);

	/**
	 * Get all user names
	 * 
	 * @return users names
	 * @throws Exception
	 */
	public List<String> getUsersNames();
}
