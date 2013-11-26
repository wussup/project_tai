package pl.edu.agh.tai.jdbc.client.windows;


import java.util.List;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.GreetingServiceAsync;
import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
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
/**
 * window for authorize access to dropbox account
 * @author Kuba
 *
 */
public class AuthorizationWindow extends Window{
	private Button logInDropBox = new Button("Save token in database and login");
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

	/**
	 * constructor, adding all fields
	 */
	public AuthorizationWindow(){
		
		setLayout(new VBoxLayout(VBoxLayoutAlign.STRETCH));
		
		
		
		addButton(logInDropBox);
		
		LabelField label = new LabelField();
		label.setValue("Dropbox Authorization Link");
		label.setStyleAttribute("padding-left", "5px");
		add (label);
		final TextField<String> field = new TextField<String>();
		field.setStyleAttribute("padding-left", "5px");
		field.setStyleAttribute("padding-right", "5px");
		field.setStyleAttribute("padding-bottom", "10px");
		field.setStyleAttribute("padding-top", "10px");
		field.setWidth(650);
		field.setTitle("Copy this address to the new tab in web browser. After this paste authorization code in a field below.");
		add(field);
		LabelField authLabel = new LabelField();
		authLabel.setStyleAttribute("padding-left", "5px");
		authLabel.setValue("Authorization Code");
		add (authLabel);
		final TextField<String> authorizationCode = new TextField<String>();
		authorizationCode.setWidth(400);
		authorizationCode.setStyleAttribute("padding-left", "5px");
		authorizationCode.setStyleAttribute("padding-right", "5px");
		authorizationCode.setStyleAttribute("padding-top", "10px");
		authorizationCode.setStyleAttribute("padding-bottom", "10px");
		authorizationCode.setValidator(new Validator() {
			
			@Override
			public String validate(Field<?> field, String value) {
				if (authorizationCode.getValue() == null){
					return "Pole musi zawierać kod autoryzacyjny z linku powyżej";
				}
				return null;
			}
		});
		add(authorizationCode);
		
		greetingService.getAuthorizationLink(new AsyncCallback<String>() {
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Fail", "Fail in getting Authorization Code");
				
			}
	
			@Override
			public void onSuccess(String result) {
				field.setValue(result);	
				}
		});
		
		logInDropBox.addSelectionListener(new SelectionListener<ButtonEvent>() {			
					
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (authorizationCode.getValue() != null){
							greetingService.logOnDropbox(authorizationCode.getValue(), new AsyncCallback<String>() {

										@Override
										public void onFailure(Throwable caught) {
											// TODO Auto-generated method stub
											
										}

										@Override
										public void onSuccess(String result) { 
											String [] list = result.split(":");
											Info.display(list[0], list[1]);
											hide();
											greetingService.getFileList(new AsyncCallback<List<Invoice>>() {

												@Override
												public void onFailure(Throwable caught) {
													// TODO Auto-generated method stub
													
												}

												@Override
												public void onSuccess(
														List<Invoice> result) {
													new FileListWindow(0).show();											
												}
											
											});							
																			
										}
							});
							
						} else {
							authorizationCode.validate();
						}
					}
		});
	
		
		setWidth(680);
		setHeight(195);
		setResizable(false);
		setClosable(false);
		setModal(true);
		
		
	}

}
