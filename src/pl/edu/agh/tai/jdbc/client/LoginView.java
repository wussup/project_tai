package pl.edu.agh.tai.jdbc.client;


import pl.edu.agh.tai.jdbc.client.windows.AuthorizationWindow;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widget.client.TextButton;

public class LoginView implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	@Override
	public void onModuleLoad() {
		RootPanel rootPanel = RootPanel.get();

		final TextBox textBox = new TextBox();
		rootPanel.add(textBox, 159, 131);

		final PasswordTextBox textBox_1 = new PasswordTextBox();
		rootPanel.add(textBox_1, 159, 167);

		TextButton button = new TextButton("New User");
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				greetingService.registrate("Kuba", "Ko³odziej", "aragorn",
						"tai.2013", new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								MessageBox.info("Success", "Done", null);
							}

							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("Error!", caught.getMessage(),
										null);
							}
						});
			}
		});

		RootPanel.get().add(button, 0, 0);

		TextButton txtbtnLogin = new TextButton("Login");
		txtbtnLogin.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String login = textBox.getText();
				String password = textBox_1.getText();
			

				greetingService.tryLogin(login, password, false,
						new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								MessageBox.alert("Error!", caught.getMessage(),
										null);
								
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result){
									// redirect
									RootPanel.get().clear();
									Info.display("Success!",
											"You have successfully logged in!");
									 AuthorizationWindow window = new AuthorizationWindow();
									 window.show();
											
																	
									
								}
								else {
									MessageBox.alert("Error!",
											"Wrong login or password", null);
								}
							}

						});
			}
		});
		rootPanel.add(txtbtnLogin, 221, 213);
	}
	
//	public Window authWindow(){
//		final Window window = new Window();
//	//	ContentPanel panel = new ContentPanel();
//		Button closeButton = new Button("Zamknij");
//		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				window.hide();
//				
//			}
//		});
//		window.addButton(closeButton);
//		final TextField<String> field = new TextField<String>();
//		field.setWidth(630);
//		field.setTitle("Skopiuj adres do przegl¹darki, a nastêpnie wklej go w pole poni¿ej");
//		final TextField<String> authorizationCode = new TextField<String>();
//		window.setWidth(640);
//		window.add(field);
//		greetingService.getAuthorizationLink(new AsyncCallback<String>() {
//
//			@Override
//			public void onFailure(Throwable caught) {
//				Info.display("dupa", "Not works");
//				
//			}
//
//			@Override
//			public void onSuccess(String result) {
//				field.setValue(result);	
//				}
//		});
//		return window;
//	}
}
