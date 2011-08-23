package uk.co.unclealex.rokta.client.presenters;

import java.util.SortedSet;

import javax.inject.Inject;

import uk.co.unclealex.rokta.client.cache.InformationCache;
import uk.co.unclealex.rokta.client.factories.StreaksTablePresenterFactory;
import uk.co.unclealex.rokta.client.filter.GameFilter;
import uk.co.unclealex.rokta.shared.model.Streak;
import uk.co.unclealex.rokta.shared.model.Streaks;

import com.google.inject.assistedinject.Assisted;

public class LosingStreaksPresenter extends StreaksPresenter {

	@Inject
	public LosingStreaksPresenter(@Assisted GameFilter gameFilter, InformationCache informationCache,
			StreaksTablePresenterFactory streaksTablePresenterFactory, Display display) {
		super(gameFilter, informationCache, streaksTablePresenterFactory, display);
	}

	@Override
	protected SortedSet<Streak> getAllStreaks(Streaks streaks) {
		return streaks.getLosingStreaks();
	}

	@Override
	protected SortedSet<Streak> getCurrentStreaks(Streaks streaks) {
		return streaks.getCurrentLosingStreaks();
	}

	
}
