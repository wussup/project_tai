package pl.edu.agh.tai.jdbc.client.windows;


import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.GreetingServiceAsync;
import pl.edu.agh.tai.jdbc.shared.ImageProvider;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * window where admin can adding new user to application
 * @author Kuba
 *
 */
public class AddUserWindow extends Window {
	private Button addNewUser = new Button("Add new user", AbstractImagePrototype.create(ImageProvider.INSTANCE.getPlusIcon()));
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * constructor, adding all fields
	 */
	public AddUserWindow(){
		
		setLayout(new VBoxLayout(VBoxLayoutAlign.CENTER));
		
		
		
		addButton(addNewUser);
		
		LabelField surnameLabel = new LabelField();
		surnameLabel.setValue("New user surname");
		add (surnameLabel);
		final TextField<String> surnameField = new TextField<String>();
		surnameField.setStyleAttribute("padding-right", "5px");
		surnameField.setStyleAttribute("padding-bottom", "10px");
		surnameField.setStyleAttribute("padding-top", "10px");
		surnameField.setWidth(150);
		surnameField.setTitle("New user surname");
		add(surnameField);
		
		LabelField nameLabel = new LabelField();
		nameLabel.setValue("New user name");
		add (nameLabel);
		final TextField<String> nameField = new TextField<String>();
		nameField.setStyleAttribute("padding-right", "5px");
		nameField.setStyleAttribute("padding-bottom", "10px");
		nameField.setStyleAttribute("padding-top", "10px");
		nameField.setWidth(150);
		nameField.setTitle("New user name");
		add(nameField);
		
		LabelField label = new LabelField();
		label.setValue("New user login");
		add (label);
		final TextField<String> loginField = new TextField<String>();
		loginField.setStyleAttribute("padding-right", "5px");
		loginField.setStyleAttribute("padding-bottom", "10px");
		loginField.setStyleAttribute("padding-top", "10px");
		loginField.setWidth(150);
		loginField.setTitle("New user login");
		add(loginField);
		
		LabelField passwordLabel = new LabelField();
		passwordLabel.setValue("Password");
		add (passwordLabel);
		final TextField<String> passwordField = new TextField<String>();
		passwordField.setWidth(150);
		passwordField.setStyleAttribute("padding-right", "5px");
		passwordField.setStyleAttribute("padding-top", "10px");
		passwordField.setStyleAttribute("padding-bottom", "10px");
		add(passwordField);
		
	
		surnameField.setValidator(new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if (surnameField.getValue() == null){
					return "Type password";
				}
				return null;
			}
		});
		
		nameField.setValidator(new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if (nameField.getValue() == null){
					return "Type password";
				}
				return null;
			}
		});
		
		
		loginField.setValidator(new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if (nameField.getValue() == null){
					return "Type login";
				}
				return null;
			}
		});

		passwordField.setValidator(new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if (passwordField.getValue() == null){
					return "Type password";
				}
				return null;
			}
		});
	
		
			
		addNewUser.addSelectionListener(new SelectionListener<ButtonEvent>() {			
					
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (nameField.getValue() != null && surnameField.getValue() != null && loginField.getValue() != null && passwordField.getValue() != null){
							greetingService.registrate(nameField.getValue(), surnameField.getValue(), loginField.getValue(), passwordField.getValue(), new AsyncCallback<Void>() {
								
								@Override
								public void onSuccess(Void arg0) {
									MessageBox.info("Info", "New user: " + surnameField.getValue() + " " + nameField.getValue() + " added properly.", null);
									hide();
									
								}
								
								@Override
								public void onFailure(Throwable arg0) {
																	
								}
							});
						}
					}
		});
	
		
		setWidth(275);
		setHeight(300);
		setResizable(false);
		setClosable(false);
		setModal(true);
		
		
	}

}