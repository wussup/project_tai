package pl.edu.agh.tai.jdbc.client;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getUser(AsyncCallback<ArrayList<User>> callback)
			throws IllegalArgumentException;
	
	void login(String login, String password, AsyncCallback<Boolean> callback) throws IllegalArgumentException;

	public void isLoggedIn(AsyncCallback<Boolean> callback);
    public void tryLogin(String login, String password, Boolean rememberMe, AsyncCallback<Boolean> callback);
    public void logout(AsyncCallback<Void> callback);
    public void registrate(String name, String surname, String login, String password, AsyncCallback<Void> callback);

	void dropbox(AsyncCallback<Void> callback);

	void getAuthorizationLink(AsyncCallback<String> callback);

	void logOnDropbox(String code, AsyncCallback<String> callback);

	void getFileList(AsyncCallback<String> callback);
}
