package pl.edu.agh.tai.jdbc.client.windows;

import java.io.File;
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
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
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

public class FileListWindow extends Window {

	private Button closeButton = new Button("Wyloguj");
	private Button downloadButton = new Button("Download File",
			AbstractImagePrototype.create(ImageProvider.INSTANCE.getFooIcon()));
	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);
	private Grid<ModelData> grid;
	private ColumnModel cm;
	private final ListLoader<ListLoadResult<Invoice>> loader;
	private final ListStore<ModelData> itemStore;
	private RpcProxy<List<Invoice>> proxy;
	
	private String folder = null;

	public FileListWindow(final int type) {
		setWidth(400);
		setHeight(300);

		ToolBar toolbar = new ToolBar();
		toolbar.setHeight(25);
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

		if (type == 1) {
			proxy = new RpcProxy<List<Invoice>>() {
				@Override
				protected void load(Object loadConfig,
						final AsyncCallback<List<Invoice>> callback) {
					greetingService.getFileList(callback);
				}
			};
		} else {
			proxy = new RpcProxy<List<Invoice>>() {
				@Override
				protected void load(Object loadConfig,
						final AsyncCallback<List<Invoice>> callback) {
					greetingService.getAdminFileList(folder, callback);
				}
			};
		}

		loader = new BaseListLoader<ListLoadResult<Invoice>>(proxy);
		itemStore = new ListStore<ModelData>(loader);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("name", "File name", 380);
		configs.add(column);

		cm = new ColumnModel(configs);

		itemStore.getLoader().load();
		grid = new Grid<ModelData>(itemStore, cm);
		grid.setHeight(205);
//		grid.setLoadMask(true);

		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ModelData>() {

					@Override
					public void selectionChanged(
							final SelectionChangedEvent<ModelData> se) {
						if (type == 0) {
							boolean isDir = ((Invoice) se.getSelectedItem()).isDir();
							if (isDir)
							{
								folder = se.getSelectedItem().get("name").toString();
								itemStore.getLoader().load();
								grid.repaint();
							}
						}
					}
				});

		add(grid);
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

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
		addButton(closeButton);

	}

}
