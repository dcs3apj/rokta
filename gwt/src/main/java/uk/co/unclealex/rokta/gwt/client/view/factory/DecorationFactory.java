package uk.co.unclealex.rokta.gwt.client.view.factory;

import uk.co.unclealex.rokta.gwt.client.controller.RoktaController;
import uk.co.unclealex.rokta.gwt.client.model.RoktaModel;
import uk.co.unclealex.rokta.gwt.client.view.decoration.CopyrightPanel;
import uk.co.unclealex.rokta.gwt.client.view.decoration.TitlePanel;

public class DecorationFactory extends ViewFactory {

	public DecorationFactory(RoktaController roktaController, RoktaModel roktaModel) {
		super(roktaController, roktaModel);
	}

	public TitlePanel createTitlePanel(TitlePanel.Images images) {
		TitlePanel titlePanel = new TitlePanel(getRoktaController(), getRoktaModel().getTitleModel(), images);
		titlePanel.initialise();
		return titlePanel;
	}

	public CopyrightPanel createCopyrightPanel() {
		return new CopyrightPanel();
	}

}