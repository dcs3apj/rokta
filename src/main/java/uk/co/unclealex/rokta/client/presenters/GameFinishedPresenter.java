package uk.co.unclealex.rokta.client.presenters;

import java.util.Date;

import javax.inject.Inject;

import uk.co.unclealex.rokta.client.filter.NoOpModifier;
import uk.co.unclealex.rokta.client.filter.YearGameFilter;
import uk.co.unclealex.rokta.client.places.LeaguePlace;
import uk.co.unclealex.rokta.client.util.AsyncCallbackExecutor;
import uk.co.unclealex.rokta.client.util.ExecutableAsyncCallback;
import uk.co.unclealex.rokta.client.util.FailureAsPopupExecutableAsyncCallback;
import uk.co.unclealex.rokta.shared.model.Game;
import uk.co.unclealex.rokta.shared.service.AnonymousRoktaServiceAsync;
import uk.co.unclealex.rokta.shared.service.UserRoktaServiceAsync;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.assistedinject.Assisted;

public class GameFinishedPresenter implements Presenter {

	public static interface Display extends IsWidget {
		HasText getLoser();
		HasClickHandlers getSubmitButton();
	}
	
	private final AsyncCallbackExecutor i_asyncCallbackExecutor;
	private final Game i_game;
	private final Display i_display;
	private final PlaceController i_placeController;
	
	@Inject
	public GameFinishedPresenter(
			PlaceController placeController, AsyncCallbackExecutor asyncCallbackExecutor, @Assisted Game game, Display display) {
		super();
		i_placeController = placeController;
		i_asyncCallbackExecutor = asyncCallbackExecutor;
		i_game = game;
		i_display = display;
	}

	@Override
	public void show(AcceptsOneWidget container) {
		Display display = getDisplay();
		container.setWidget(display);
		display.getLoser().setText(getGame().getLoser());
		display.getSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				submitGame();
			}
		});
	}

	protected void submitGame() {
		ExecutableAsyncCallback<Void> callback = new FailureAsPopupExecutableAsyncCallback<Void>() {
			@Override
			public void onSuccess(Void result) {
				getPlaceController().goTo(new LeaguePlace(new YearGameFilter(new NoOpModifier(), new Date())));
			}
			@Override
			public void execute(AnonymousRoktaServiceAsync anonymousRoktaService, UserRoktaServiceAsync userRoktaService,
					AsyncCallback<Void> callback) {
				userRoktaService.submitGame(getGame(), callback);
			}
		};
		getAsyncCallbackExecutor().execute(callback);
	}

	public AsyncCallbackExecutor getAsyncCallbackExecutor() {
		return i_asyncCallbackExecutor;
	}

	public Game getGame() {
		return i_game;
	}

	public Display getDisplay() {
		return i_display;
	}

	public PlaceController getPlaceController() {
		return i_placeController;
	}

}
