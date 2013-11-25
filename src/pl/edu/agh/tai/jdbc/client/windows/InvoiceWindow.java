package pl.edu.agh.tai.jdbc.client.windows;

import java.util.List;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.GreetingServiceAsync;
import pl.edu.agh.tai.jdbc.shared.ImageProvider;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout.HBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class InvoiceWindow extends Window{
	
	private SimpleComboBox<String> loginCombo = new SimpleComboBox<String>();
	private TextField<Integer> value = new TextField<Integer>();
	private TextField<String> name = new TextField<String>();
	private Button save = new Button("Save", 
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getSaveIcon()));
	private Button close = new Button ("Close", 
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getCloseIcon()));
	private FieldSet set = new FieldSet();
	private LabelField label1 = new LabelField();
	private LabelField label2 = new LabelField();
	private LabelField label3 = new LabelField();
	
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	
	public InvoiceWindow() throws Exception{
		setWidth(250);
		setHeight(250);
		setOnEsc(false);
		setClosable(false);
		setModal(true);
		setResizable(false);
		
		VBoxLayout layout = new VBoxLayout(VBoxLayoutAlign.STRETCH);
		setLayout(layout);
		
		greetingService.getUsersNames(new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				loginCombo.add(result);				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
		label1.setValue("User");
		label2.setValue("Invoice value");
		label3.setValue("Invoice file name");
		loginCombo.setFieldLabel("User");
		loginCombo.setHideLabel(false);
		value.setFieldLabel("Invoice value");
		value.setWidth(200);
		loginCombo.setWidth(200);
		name.setWidth(200);
		loginCombo.setStyleAttribute("padding-bottom", "10px");
		value.setStyleAttribute("padding-bottom", "10px");
		HBoxLayout setLayout = new HBoxLayout();
		setLayout.setHBoxLayoutAlign(HBoxLayoutAlign.STRETCH);
		set.setHeadingHtml("Set invoice data");
		set.add(label1);
		set.add(loginCombo);
		set.add(label2);
		set.add(value);
		set.add(label3);
		set.add(name);
		add (set);
		addButtonListeners();
		addButton(save);
		addButton(close);
	}
	
	private void addButtonListeners(){
		close.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
	}

}
