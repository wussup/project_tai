package pl.edu.agh.tai.jdbc.shared;


import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModel;

public class Invoice extends BaseModel implements Serializable{

	private static final long serialVersionUID = -9097047980599913344L;
	private String name;
	
	public Invoice(){
		super();
	}
	
	public Invoice(String name){
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
