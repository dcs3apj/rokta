package uk.co.unclealex.rokta.client.presenters;

import com.google.gwt.user.client.ui.IsWidget;

public interface HasDisplay<D extends IsWidget> {

	D getDisplay();

}
