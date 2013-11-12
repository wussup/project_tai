package pl.edu.agh.tai.jdbc.client.windows;

import java.util.ArrayList;
import java.util.List;

import pl.edu.agh.tai.jdbc.client.GreetingService;
import pl.edu.agh.tai.jdbc.client.GreetingServiceAsync;
import pl.edu.agh.tai.jdbc.shared.Invoice;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FileListWindow extends Window{
	
	private Button closeButton = new Button("Zamknij");
	private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);
	private Grid<ModelData> grid;
	private ColumnModel cm;
	private final ListLoader<ListLoadResult<Invoice>> loader;
	private final ListStore<ModelData> itemStore;
	

	public FileListWindow() {
		setWidth(400);
		setHeight(300);
		
		RpcProxy<List<Invoice>> proxy = new RpcProxy<List<Invoice>>() {
            @Override
            protected void load(Object loadConfig, final AsyncCallback<List<Invoice>> callback) {
                greetingService.getFileList(callback);
            }
        };
		
        loader = new BaseListLoader<ListLoadResult<Invoice>>(proxy);
        itemStore = new ListStore<ModelData>(loader);
        
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
	    ColumnConfig column = new ColumnConfig("name", "File name", 200);  
	    configs.add(column);  
	  
	    cm = new ColumnModel(configs);  
	  
	  	itemStore.getLoader().load();
		grid = new Grid<ModelData>(itemStore, cm);
		grid.setHeight(200);
		add(grid);
		addButton(closeButton);			
	    
	   
		
	}

}
