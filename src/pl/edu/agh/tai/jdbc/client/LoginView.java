package pl.edu.agh.tai.jdbc.client;

import pl.edu.agh.tai.jdbc.client.windows.FileListWindow;
import pl.edu.agh.tai.jdbc.shared.ImageProvider;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * Login window
 * 
 * @since 26.11.2013
 * @author Taras Melon&Jakub Kolodziej
 */
public class LoginView implements EntryPoint {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final static GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	private static final int WIDTH = 415;
	private static final int HEIGHT = 345;

	private static final Window WINDOW = new Window();
	private static final LayoutContainer WELCOME_IMG_PANEL = new LayoutContainer();
	private static final TextField<String> USERNAME_FIELD = createField(
			"Login", false);
	private static final TextField<String> PASSWORD_FIELD = createField(
			"Has�o", true);

	private static final Status STATUS_BOX = new Status();
	private static final Button LOGIN_BTN = new Button("Zaloguj");

	static {
		WINDOW.setIcon(AbstractImagePrototype.create(ImageProvider.INSTANCE
				.getDropboxIcon()));
		WINDOW.setHeadingText("Logowanie Dropbox TAI Project");
		WINDOW.setSize(WIDTH, HEIGHT);
		WINDOW.setResizable(false);
		WINDOW.setClosable(false);
		WINDOW.setModal(true);
		WINDOW.setLayout(new BorderLayout());

		FieldSet fieldSet = new FieldSet();
		Image logo = new Image(ImageProvider.INSTANCE.getLogo());
		WELCOME_IMG_PANEL.add(logo);
		USERNAME_FIELD.setStyleAttribute("padding-left", "130px");
		USERNAME_FIELD.setStyleAttribute("padding-bottom", "10px");
		PASSWORD_FIELD.setStyleAttribute("padding-left", "130px");

		fieldSet.add(USERNAME_FIELD);
		fieldSet.add(PASSWORD_FIELD);

		WINDOW.add(WELCOME_IMG_PANEL, new BorderLayoutData(LayoutRegion.CENTER));
		WINDOW.add(fieldSet, new BorderLayoutData(LayoutRegion.SOUTH, 80));

		LOGIN_BTN.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent arg0) {
				login();
			}
		});

		WINDOW.setButtonAlign(HorizontalAlignment.LEFT);
		WINDOW.getButtonBar().add(STATUS_BOX);
		WINDOW.getButtonBar().add(new FillToolItem());
		WINDOW.addButton(LOGIN_BTN);

		KeyListener keyListener = new LoginFormKeyListener();
		USERNAME_FIELD.addKeyListener(keyListener);
		PASSWORD_FIELD.addKeyListener(keyListener);
	}

	@Override
	public void onModuleLoad() {

		greetingService.isLoggedIn(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error!",
						"Sorry, but error is occured in isLoggedIn method!",
						null);
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result == true) {
					greetingService
							.getApplicationUser(new AsyncCallback<User>() {

								@Override
								public void onFailure(Throwable caught) {
									MessageBox
											.alert("Error!",
													"Sorry, but error is occured in getApplicationUser method!",
													null);
								}

								@Override
								public void onSuccess(User result) {
									FileListWindow fileWindow = new FileListWindow(
											result.getType());
									fileWindow.show();
								}
							});
				} else {
					WINDOW.show();
				}
			}
		});
	}

	private static TextField<String> createField(final String label,
			final boolean password) {
		TextField<String> field = new TextField<String>();
		field.setMaxLength(255);

		if (!Util.isEmptyString(label)) {
			field.setFieldLabel(label);
		}

		field.setAllowBlank(false);
		field.setAutoValidate(true);
		field.setPassword(password);
		field.setValidator(new Validator() {
			@Override
			public String validate(final Field<?> field, final String value) {
				if (Util.isEmptyString(value)
						|| Util.isEmptyString(value.trim())) {
					return "To pole jest wymagane";
				}

				return null;
			}
		});

		return field;
	}

	private static final class LoginFormKeyListener extends KeyListener {
		private static final String DEFAULT_PASSWORD = "tai.2013";

		@Override
		public void componentKeyUp(final ComponentEvent event) {
			if (KeyCodes.KEY_ENTER == event.getKeyCode()) {
				login();
			}

			if (!GWT.isScript()) {
				if (KeyCodes.KEY_HOME == event.getKeyCode()) {
					USERNAME_FIELD.setValue("aragorn");
					PASSWORD_FIELD.setValue(DEFAULT_PASSWORD);
					login();
				}
				if (KeyCodes.KEY_PAGEUP == event.getKeyCode()) {
					USERNAME_FIELD.setValue("taras");
					PASSWORD_FIELD.setValue("melon");
					login();
				}
			}
		}
	}

	private static void login() {

		String login = USERNAME_FIELD.getValue();
		String password = PASSWORD_FIELD.getValue();

		greetingService.tryLogin(login, password, false,
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						MessageBox
								.alert("Error!",
										"Sorry, but error is occured in tryLogin method!",
										null);
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result) {

							WINDOW.hide();
							Info.display("Success!",
									"You have successfully logged in!");
							greetingService
									.logOnDropbox(new AsyncCallback<String>() {

										@Override
										public void onFailure(Throwable caught) {
											MessageBox
													.alert("Error!",
															"Sorry, but error is occured in logOnDropbox method!",
															null);
										}

										@Override
										public void onSuccess(String result) {
											String[] splitted = result
													.split(":");
											Info.display(splitted[0],
													splitted[1]);
											greetingService
													.getApplicationUser(new AsyncCallback<User>() {

														@Override
														public void onFailure(
																Throwable caught) {
															MessageBox
																	.alert("Error!",
																			"Sorry, but error is occured in getApplicationUser method!",
																			null);
														}

														@Override
														public void onSuccess(
																User result) {
															FileListWindow fileWindow = new FileListWindow(
																	result.getType());
															fileWindow.show();

														}
													});
										}
									});

						} else {
							MessageBox.alert("Error!",
									"Wrong login or password", null);
						}
					}

				});
	}

	public void showWindow() {
		WINDOW.show();
	}
}
