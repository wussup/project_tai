package pl.edu.agh.tai.jdbc.client;

import java.util.List;

import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {

	public void login(String login, String password,
			AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	public void isLoggedIn(AsyncCallback<Boolean> callback);

	public void tryLogin(String login, String password, Boolean rememberMe,
			AsyncCallback<Boolean> callback);

	public void logout(AsyncCallback<Void> callback);

	public void registrate(String name, String surname, String login,
			String password, AsyncCallback<Void> callback);

	public void getAuthorizationLink(AsyncCallback<String> callback);

	public void logOnDropbox(AsyncCallback<String> callback);

	public void getFileList(AsyncCallback<List<Invoice>> callback);

	public void addFile(String name, AsyncCallback<Void> callback);

	public void getApplicationUser(AsyncCallback<User> callback);

	public void downloadFile(String name, AsyncCallback<Boolean> callback);

	public void getAdminFileList(String folderName,
			AsyncCallback<List<Invoice>> callback);

	public void getUsersNames(AsyncCallback<List<String>> callback);
}
