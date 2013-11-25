package pl.edu.agh.tai.jdbc.shared;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Provides images from sources
 * 
 * @since 26.11.2013
 * @author Taras Melon&Jakub Kolodziej
 */
public interface ImageProvider extends ClientBundle {

	public ImageProvider INSTANCE = GWT.create(ImageProvider.class);

	@Source("img/foo.png")
	ImageResource getFooIcon();

	@Source("img/dropbox.jpg")
	ImageResource getDropboxIcon();

	@Source("img/logo.png")
	ImageResource getLogo();

	@Source("img/plusIcon.gif")
	ImageResource getPlusIcon();

	@Source("img/saveIcon.gif")
	ImageResource getSaveIcon();

	@Source("img/closeIcon.gif")
	ImageResource getCloseIcon();

	@Source("img/logout.png")
	ImageResource getLogoutIcon();

}
