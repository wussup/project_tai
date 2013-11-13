package pl.edu.agh.tai.jdbc.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;

	ArrayList<User> getUser();

	boolean login(String login, String password);

	public Boolean isLoggedIn();

	public Boolean tryLogin(String login, String password, Boolean rememberMe);

	public void logout();

	public void registrate(String name, String surname, String login,
			String password);

	void dropbox() throws IOException;

	String getAuthorizationLink();

	String logOnDropbox(String code);

	void addFile(String name);

	List<Invoice> getFileList();

	User getApplicationUser();

	boolean downloadFile(String name);
}
