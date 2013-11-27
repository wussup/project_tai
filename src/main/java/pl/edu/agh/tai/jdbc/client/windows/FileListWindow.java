package pl.edu.agh.tai.jdbc.client.windows;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.GreetingServiceAsync;
import pl.edu.agh.tai.jdbc.client.LoginView;
import pl.edu.agh.tai.jdbc.shared.ImageProvider;
import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Window where all files are display in grid
 * 
 * @since 26.11.2013
 * @author Jakub Kolodziej&Taras Melon
 * 
 */
public class FileListWindow extends Window {

	private Button logoutButton = new Button("Logout",
			AbstractImagePrototype.create(ImageProvider.INSTANCE
					.getLogoutIcon()));
	private Button downloadButton = new Button("Download File",
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getFooIcon()));
	private Button dropboxLinking = new Button(
			"Change Dropbox Linking Account",
			AbstractImagePrototype.create(ImageProvider.INSTANCE
					.getDropboxIcon()));
	private Button addNewUser = new Button("Add new user",
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getPlusIcon()));
	private Button createInvoice = new Button("Create new Invoice",
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getPlusIcon()));
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private Grid<ModelData> grid;
	private ColumnModel cm;
	private ListLoader<ListLoadResult<Invoice>> loader;
	private ListStore<ModelData> itemStore;
	private RpcProxy<List<Invoice>> proxy;

	/**
	 * constructor, adding all fields
	 */
	public FileListWindow(final int type) {
		setWidth(400);
		setHeight(300);
		setOnEsc(false);
		setClosable(false);
		setModal(true);
		setResizable(false);

		createToolbars(type);

		createProxy(type);

		loader = new BaseListLoader<ListLoadResult<Invoice>>(proxy);
		itemStore = new ListStore<ModelData>(loader);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("name", "File name", 380);
		configs.add(column);

		cm = new ColumnModel(configs);

		itemStore.getLoader().load();
		grid = new Grid<ModelData>(itemStore, cm);
		grid.setHeight(205);

		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ModelData>() {

					@Override
					public void selectionChanged(
							final SelectionChangedEvent<ModelData> se) {
						if (type == 0) {
							boolean isDir = (!se.getSelectedItem().get("name")
									.toString().contains("."));
							if (isDir) {
								downloadButton.setVisible(false);
								grid.getStore().getLoader().load();

							} else {
								downloadButton.setVisible(true);
							}
						}
					}
				});

		add(grid);
		logoutButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
				greetingService.logout(new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(Void result) {

						LoginView lv = new LoginView();
						lv.showWindow();

					}
				});
			}
		});
		addButton(logoutButton);

	}

	private void createProxy(int type) {
		proxy = new RpcProxy<List<Invoice>>() {
			@Override
			protected void load(Object loadConfig,
					final AsyncCallback<List<Invoice>> callback) {
				greetingService.getFileList(callback);
			}
		};
	}

	private void createToolbars(int type) {
		ToolBar toolbar = new ToolBar();
		ToolBar bottomToolbar = new ToolBar();
		toolbar.setHeight(25);
		if (type == 0) {
			toolbar.add(createInvoice);
			bottomToolbar.add(dropboxLinking);
			FillToolItem item = new FillToolItem();
			bottomToolbar.add(item);
			bottomToolbar.add(addNewUser);

			createInvoice
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							try {
								InvoiceWindow window = new InvoiceWindow();
								window.show();
								window.addListener(Events.Hide,
										new Listener<BaseEvent>() {

											@Override
											public void handleEvent(BaseEvent be) {
												grid.getStore().getLoader()
														.load();
											}
										});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

			dropboxLinking
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							new AuthorizationWindow().show();
						}
					});

			addNewUser
					.addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							new AddUserWindow().show();
						}

					});

		}
		FillToolItem item = new FillToolItem();
		toolbar.add(item);
		toolbar.add(downloadButton);
		downloadButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						for (final ModelData data : grid.getSelectionModel()
								.getSelectedItems()) {
							greetingService.downloadFile(data.get("name")
									.toString(), new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onSuccess(Boolean result) {
									Info.display("Successfully", "File "
											+ data.get("name").toString()
											+ " is downloaded!");
								}
							});
						}
					}
				});

		setTopComponent(toolbar);
		setBottomComponent(bottomToolbar);

	}

}
