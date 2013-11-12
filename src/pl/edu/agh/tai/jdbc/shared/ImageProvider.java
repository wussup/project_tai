package pl.edu.agh.tai.jdbc.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ImageProvider extends ClientBundle {

	public ImageProvider INSTANCE = GWT.create(ImageProvider.class);
	

	@Source("img/foo.png")
	ImageResource getFooIcon();
	
	@Source("img/dropbox.jpg")
	ImageResource getDropboxIcon();
	
	@Source("img/logo.png")
	ImageResource getLogo();
	
}