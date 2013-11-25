package pl.edu.agh.tai.jdbc.client;

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
	public String greetServer(String name) throws IllegalArgumentException;

	public ArrayList<User> getUser();

	public boolean login(String login, String password);

	public Boolean isLoggedIn();

	public Boolean tryLogin(String login, String password, Boolean rememberMe);

	public void logout();

	public void registrate(String name, String surname, String login,
			String password);

	public String getAuthorizationLink();

	public void addFile(String name);

	public List<Invoice> getFileList();

	public User getApplicationUser();

	public boolean downloadFile(String name);

	public String logOnDropbox();

	public List<Invoice> getAdminFileList(String folderName);

	public List<String> getUsersNames() throws Exception;
}
