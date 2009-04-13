package uk.co.unclealex.rokta.gwt.client.side.gamefilter;

import java.util.Date;

import uk.co.unclealex.rokta.gwt.client.DefaultAsyncCallback;
import uk.co.unclealex.rokta.gwt.client.RoktaAdaptor;
import uk.co.unclealex.rokta.pub.filter.AllGameFilter;
import uk.co.unclealex.rokta.pub.filter.BeforeGameFilter;
import uk.co.unclealex.rokta.pub.filter.BetweenGameFilter;
import uk.co.unclealex.rokta.pub.filter.FirstGameOfTheDayFilter;
import uk.co.unclealex.rokta.pub.filter.FirstGameOfTheMonthFilter;
import uk.co.unclealex.rokta.pub.filter.FirstGameOfTheWeekFilter;
import uk.co.unclealex.rokta.pub.filter.FirstGameOfTheYearFilter;
import uk.co.unclealex.rokta.pub.filter.GameFilter;
import uk.co.unclealex.rokta.pub.filter.GameFilterVistor;
import uk.co.unclealex.rokta.pub.filter.LastGameOfTheDayFilter;
import uk.co.unclealex.rokta.pub.filter.LastGameOfTheMonthFilter;
import uk.co.unclealex.rokta.pub.filter.LastGameOfTheWeekFilter;
import uk.co.unclealex.rokta.pub.filter.LastGameOfTheYearFilter;
import uk.co.unclealex.rokta.pub.filter.MonthGameFilter;
import uk.co.unclealex.rokta.pub.filter.SinceGameFilter;
import uk.co.unclealex.rokta.pub.filter.WeekGameFilter;
import uk.co.unclealex.rokta.pub.filter.YearGameFilter;
import uk.co.unclealex.rokta.pub.views.InitialDatesView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ContinuousGameFilterWidget extends GameFilterProducerComposite implements GameFilterProducer, GameFilterProducerListener, ChangeHandler {

	private DeckPanel i_panel;
	private ListBox i_listBox;
	
	private YearGameFilterProducerPanel i_yearGameFilterProducerPanel;
	private MonthGameFilterProducerPanel i_monthGameFilterProducerPanel;
	private WeekGameFilterProducerPanel i_weekGameFilterProducerPanel;
	private BeforeGameFilterProducerPanel i_beforeGameFilterProducerPanel;
	private BetweenGameFilterProducerPanel i_betweenGameFilterProducerPanel;
	private SinceGameFilterProducerPanel i_sinceGameFilterProducerPanel;
	private AllGameFilterProducerPanel i_allGameFilterProducerPanel;

	public ContinuousGameFilterWidget(final RoktaAdaptor roktaAdaptor, GameFilterProducerListener gameFilterProducerListener) {
		super(roktaAdaptor, gameFilterProducerListener);
		VerticalPanel verticalPanel = new VerticalPanel();
		ListBox listBox = new ListBox();
		setListBox(listBox);
		listBox.addChangeHandler(this);
		final DeckPanel panel = new DeckPanel();
		AsyncCallback<InitialDatesView> callback = new DefaultAsyncCallback<InitialDatesView>() {
			public void onSuccess(InitialDatesView initialDatesView) {
				Date initialDate = initialDatesView.getInitialDate();
				Date earliestDate = initialDatesView.getEarliestDate();
				Date latestDate = initialDatesView.getLatestDate();

				GameFilterMessages messages = GWT.create(GameFilterMessages.class);

				add(new ThisYearGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this), messages.thisYear());
				add(new LastFourWeeksGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this), messages.lastFourWeeks());
				add(new ThisMonthGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this), messages.thisMonth());
				add(new ThisWeekGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this), messages.thisWeek());
				add(new TodayGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this), messages.today());

				YearGameFilterProducerPanel yearGameFilterProducerPanel =
					new YearGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				add(yearGameFilterProducerPanel, messages.year());
				setYearGameFilterProducerPanel(yearGameFilterProducerPanel);

				MonthGameFilterProducerPanel monthGameFilterProducerPanel =
					new MonthGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				add(monthGameFilterProducerPanel, messages.month());
				setMonthGameFilterProducerPanel(monthGameFilterProducerPanel);
				
				WeekGameFilterProducerPanel weekGameFilterProducerPanel =
					new WeekGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				setWeekGameFilterProducerPanel(weekGameFilterProducerPanel);
				add(weekGameFilterProducerPanel, messages.week());
				
				BeforeGameFilterProducerPanel beforeGameFilterProducerPanel = 
					new BeforeGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				setBeforeGameFilterProducerPanel(beforeGameFilterProducerPanel);
				add(beforeGameFilterProducerPanel, messages.before());

				BetweenGameFilterProducerPanel betweenGameFilterProducerPanel = 
					new BetweenGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				setBetweenGameFilterProducerPanel(betweenGameFilterProducerPanel);
				add(betweenGameFilterProducerPanel, messages.between());
				
				SinceGameFilterProducerPanel sinceGameFilterProducerPanel = 
					new SinceGameFilterProducerPanel(roktaAdaptor, initialDate, earliestDate, latestDate, ContinuousGameFilterWidget.this);
				setSinceGameFilterProducerPanel(sinceGameFilterProducerPanel);
				add(sinceGameFilterProducerPanel, messages.since());
				
				AllGameFilterProducerPanel allGameFilterProducerPanel = 
					new AllGameFilterProducerPanel(roktaAdaptor, ContinuousGameFilterWidget.this);
				add(allGameFilterProducerPanel, messages.all());
				setAllGameFilterProducerPanel(allGameFilterProducerPanel);
				
				getListBox().setSelectedIndex(0);
				panel.showWidget(0);
				setGameFilter(createGameFilter());
			}			
		};
		setPanel(panel);
		verticalPanel.add(listBox);
		verticalPanel.add(panel);
		initWidget(verticalPanel);
		getRoktaAdaptor().getInitialDates(callback);
	}

	protected void add(GameFilterProducerComposite gameFilterProducerComposite, String title) {
		getPanel().add(gameFilterProducerComposite);
		getListBox().addItem(title);
	}

	public void onChange(ChangeEvent event) {
		int index = getListBox().getSelectedIndex();
		getPanel().showWidget(index);
	}
	
	public void onGameFilterProduced(GameFilterProducerEvent gameFilterProducerEvent) {
		setGameFilter(gameFilterProducerEvent.getGameFilter());
	}
	
	@Override
	protected GameFilter createGameFilter() {
		int index = getPanel().getVisibleWidget();
		return ((GameFilterProducer) getPanel().getWidget(index)).getGameFilter();
	}
	
	protected class ContinuousGameFilterVisitor extends GameFilterVistor<Object> {

		protected Object select(GameFilterProducerComposite gameFilterProducerPanel) {
			DeckPanel panel = getPanel();
			int widgetIndex = panel.getWidgetIndex(gameFilterProducerPanel);
			panel.showWidget(widgetIndex);
			return null;
		}
		
		@Override
		public Object join(Object leftResult, Object rightResult) {
			return null;
		}

		@Override
		public Object visit(BeforeGameFilter beforeGameFilter) {
			BeforeGameFilterProducerPanel beforeGameFilterProducerPanel = getBeforeGameFilterProducerPanel();
			beforeGameFilterProducerPanel.setBeforeDate(beforeGameFilter.getBefore());
			return select(beforeGameFilterProducerPanel);
		}

		@Override
		public Object visit(BetweenGameFilter betweenGameFilter) {
			BetweenGameFilterProducerPanel betweenGameFilterProducerPanel = getBetweenGameFilterProducerPanel();
			betweenGameFilterProducerPanel.setFromDate(betweenGameFilter.getFrom());
			betweenGameFilterProducerPanel.setToDate(betweenGameFilter.getTo());
			return select(betweenGameFilterProducerPanel);
		}

		@Override
		public Object visit(SinceGameFilter sinceGameFilter) {
			SinceGameFilterProducerPanel sinceGameFilterProducerPanel = getSinceGameFilterProducerPanel();
			sinceGameFilterProducerPanel.setSinceDate(sinceGameFilter.getSince());
			return select(sinceGameFilterProducerPanel);
		}

		@Override
		public Object visit(MonthGameFilter monthGameFilter) {
			MonthGameFilterProducerPanel monthGameFilterProducerPanel = getMonthGameFilterProducerPanel();
			monthGameFilterProducerPanel.updateYearAndMonth(monthGameFilter.getDate());
			return select(monthGameFilterProducerPanel);
		}

		@Override
		public Object visit(WeekGameFilter weekGameFilter) {
			WeekGameFilterProducerPanel weekGameFilterProducerPanel = getWeekGameFilterProducerPanel();
			weekGameFilterProducerPanel.updateYearAndWeek(weekGameFilter.getDate());
			return select(weekGameFilterProducerPanel);
		}

		@SuppressWarnings("deprecation")
		@Override
		public Object visit(YearGameFilter yearGameFilter) {
			YearGameFilterProducerPanel yearGameFilterProducerPanel = getYearGameFilterProducerPanel();
			yearGameFilterProducerPanel.setYear(yearGameFilter.getDate().getYear() + 1900);
			return select(yearGameFilterProducerPanel);
		}

		@Override
		public Object visit(AllGameFilter allGameFilter) {
			return select(getAllGameFilterProducerPanel());
		}

		@Override
		public Object visit(FirstGameOfTheDayFilter firstGameOfTheDayFilter) {
			return null;
		}

		@Override
		public Object visit(FirstGameOfTheWeekFilter firstGameOfTheWeekFilter) {
			return null;
		}

		@Override
		public Object visit(FirstGameOfTheMonthFilter firstGameOfTheMonthFilter) {
			return null;
		}

		@Override
		public Object visit(FirstGameOfTheYearFilter firstGameOfTheYearFilter) {
			return null;
		}

		@Override
		public Object visit(LastGameOfTheDayFilter lastGameOfTheDayFilter) {
			return null;
		}

		@Override
		public Object visit(LastGameOfTheWeekFilter lastGameOfTheWeekFilter) {
			return null;
		}

		@Override
		public Object visit(LastGameOfTheMonthFilter lastGameOfTheMonthFilter) {
			return null;
		}

		@Override
		public Object visit(LastGameOfTheYearFilter lastGameOfTheYearFilter) {
			return null;
		}		
	}

	protected YearGameFilterProducerPanel getYearGameFilterProducerPanel() {
		return i_yearGameFilterProducerPanel;
	}

	protected void setYearGameFilterProducerPanel(YearGameFilterProducerPanel yearGameFilterProducerPanel) {
		i_yearGameFilterProducerPanel = yearGameFilterProducerPanel;
	}

	protected MonthGameFilterProducerPanel getMonthGameFilterProducerPanel() {
		return i_monthGameFilterProducerPanel;
	}

	protected void setMonthGameFilterProducerPanel(MonthGameFilterProducerPanel monthGameFilterProducerPanel) {
		i_monthGameFilterProducerPanel = monthGameFilterProducerPanel;
	}

	protected WeekGameFilterProducerPanel getWeekGameFilterProducerPanel() {
		return i_weekGameFilterProducerPanel;
	}

	protected void setWeekGameFilterProducerPanel(WeekGameFilterProducerPanel weekGameFilterProducerPanel) {
		i_weekGameFilterProducerPanel = weekGameFilterProducerPanel;
	}

	protected BeforeGameFilterProducerPanel getBeforeGameFilterProducerPanel() {
		return i_beforeGameFilterProducerPanel;
	}

	protected void setBeforeGameFilterProducerPanel(BeforeGameFilterProducerPanel beforeGameFilterProducerPanel) {
		i_beforeGameFilterProducerPanel = beforeGameFilterProducerPanel;
	}

	protected BetweenGameFilterProducerPanel getBetweenGameFilterProducerPanel() {
		return i_betweenGameFilterProducerPanel;
	}

	protected void setBetweenGameFilterProducerPanel(BetweenGameFilterProducerPanel betweenGameFilterProducerPanel) {
		i_betweenGameFilterProducerPanel = betweenGameFilterProducerPanel;
	}

	protected SinceGameFilterProducerPanel getSinceGameFilterProducerPanel() {
		return i_sinceGameFilterProducerPanel;
	}

	protected void setSinceGameFilterProducerPanel(SinceGameFilterProducerPanel sinceGameFilterProducerPanel) {
		i_sinceGameFilterProducerPanel = sinceGameFilterProducerPanel;
	}

	protected AllGameFilterProducerPanel getAllGameFilterProducerPanel() {
		return i_allGameFilterProducerPanel;
	}

	protected void setAllGameFilterProducerPanel(AllGameFilterProducerPanel allGameFilterProducerPanel) {
		i_allGameFilterProducerPanel = allGameFilterProducerPanel;
	}

	public DeckPanel getPanel() {
		return i_panel;
	}

	public void setPanel(DeckPanel panel) {
		i_panel = panel;
	}

	public ListBox getListBox() {
		return i_listBox;
	}

	public void setListBox(ListBox listBox) {
		i_listBox = listBox;
	}
}
