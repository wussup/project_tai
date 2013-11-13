package pl.edu.agh.tai.jdbc.shared;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Invoice extends BaseModel implements Serializable {

	private static final long serialVersionUID = -9097047980599913344L;
	private String name;
	private boolean isDir;

	public Invoice() {
		super();
	}

	public Invoice(String name, boolean isDir) {
		this.name = name;
		this.isDir = isDir;
		set("name", name);
		set("isDir", isDir);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		set("name", name);
	}

	public boolean isDir() {
		return isDir;
	}

	public void setDir(boolean isDir) {
		this.isDir = isDir;
		set("isDir", isDir);
	}

	
	
}
