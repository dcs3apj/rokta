package uk.co.unclealex.rokta.client.presenters;

import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import uk.co.unclealex.rokta.client.cache.InformationCache;
import uk.co.unclealex.rokta.client.filter.GameFilter;
import uk.co.unclealex.rokta.client.messages.TitleMessages;
import uk.co.unclealex.rokta.shared.model.CurrentInformation;
import uk.co.unclealex.rokta.shared.model.League;
import uk.co.unclealex.rokta.shared.model.LeagueRow;
import uk.co.unclealex.rokta.shared.model.Leagues;

import com.google.common.collect.Maps;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class GraphPresenter extends InformationPresenter<Leagues> {

	public static interface Display extends IsWidget {
		void drawGraph(Map<String, String> coloursByName, Map<String, SortedMap<Date, Double>> percentagesByDateByName);
	}

	private final TitleMessages i_titleMessages;
	private final Display i_display;

	@Inject
	public GraphPresenter(@Assisted GameFilter gameFilter, InformationCache informationCache, TitleMessages titleMessages,
			Display display) {
		super(gameFilter, informationCache);
		i_titleMessages = titleMessages;
		i_display = display;
	}

	@Override
	protected Leagues asSpecificInformation(CurrentInformation currentInformation) {
		return currentInformation.getLeagues();
	}
	
	@Override
	protected void show(GameFilter gameFilter, AcceptsOneWidget panel, final Leagues leagues) {
		Display display = getDisplay();
		panel.setWidget(display);
		Map<String, SortedMap<Date, Double>> percentagesByDateByName = Maps.newHashMap();
		for (League league : leagues.getLeagues()) {
			Date leagueDate = league.getLastGameDate();
			for (LeagueRow leagueRow : league.getRows()) {
				String name = leagueRow.getPersonName();
				double lossesPerGame = leagueRow.getLossesPerGame();
				SortedMap<Date, Double> percentagesByDate = percentagesByDateByName.get(name);
				if (percentagesByDate == null) {
					percentagesByDate = Maps.newTreeMap();
					percentagesByDateByName.put(name, percentagesByDate);
				}
				percentagesByDate.put(leagueDate, lossesPerGame * 100.0);
			}
		}
		display.drawGraph(leagues.getHtmlColoursByName(), percentagesByDateByName);
	}
		
	public TitleMessages getTitleMessages() {
		return i_titleMessages;
	}

	public Display getDisplay() {
		return i_display;
	}
}
