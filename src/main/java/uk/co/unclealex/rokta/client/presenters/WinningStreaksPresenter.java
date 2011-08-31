package uk.co.unclealex.rokta.client.presenters;

import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.rokta.client.cache.InformationCache;
import uk.co.unclealex.rokta.client.factories.StreaksTablePresenterFactory;
import uk.co.unclealex.rokta.client.filter.GameFilter;
import uk.co.unclealex.rokta.client.messages.TitleMessages;
import uk.co.unclealex.rokta.shared.model.Streak;
import uk.co.unclealex.rokta.shared.model.Streaks;

import com.google.inject.assistedinject.Assisted;

public class WinningStreaksPresenter extends StreaksPresenter {

	@Inject
	public WinningStreaksPresenter(@Assisted GameFilter gameFilter, InformationCache informationCache,
			StreaksTablePresenterFactory streaksTablePresenterFactory, Display display, TitleMessages titleMessages) {
		super(gameFilter, informationCache, streaksTablePresenterFactory, display, titleMessages);
	}

	@Override
	protected String createAllStreaksTitle(TitleMessages titleMessages, SortedSet<Streak> allStreaks) {
		return titleMessages.allWinningStreaks(allStreaks.size());
	}
	
	@Override
	protected String createCurrentStreaksTitle(TitleMessages titleMessages, SortedSet<Streak> currentStreaks) {
		return titleMessages.currentWinningStreaks();
	}
	

	@Override
	protected SortedSet<Streak> getAllStreaks(Streaks streaks) {
		return streaks.getWinningStreaks();
	}

	@Override
	protected SortedSet<Streak> getCurrentStreaks(Streaks streaks) {
		return streaks.getCurrentWinningStreaks();
	}

	
}