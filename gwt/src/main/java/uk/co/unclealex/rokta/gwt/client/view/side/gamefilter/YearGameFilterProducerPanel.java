package uk.co.unclealex.rokta.gwt.client.view.side.gamefilter;

import java.util.Date;

import uk.co.unclealex.rokta.gwt.client.controller.RoktaController;
import uk.co.unclealex.rokta.gwt.client.model.InitialDatesModel;
import uk.co.unclealex.rokta.pub.filter.GameFilter;
import uk.co.unclealex.rokta.pub.filter.YearGameFilter;
import uk.co.unclealex.rokta.pub.views.InitialDatesView;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;

public class YearGameFilterProducerPanel extends GameFilterProducerComposite {

	private Integer i_year;
	private ListBox i_yearListBox;
	
	public YearGameFilterProducerPanel(
			RoktaController roktaController, InitialDatesModel model, GameFilterProducerListener... gameFilterProducerListeners) {
		super(roktaController, model, gameFilterProducerListeners);
		final ListBox yearListBox = new ListBox();
		ChangeHandler changeHandler = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				int year = Integer.valueOf(yearListBox.getValue(yearListBox.getSelectedIndex()));
				setYear(year);
				setGameFilter(createGameFilter());
			}
		};
		yearListBox.addChangeHandler(changeHandler);
		setYearListBox(yearListBox);
		SimplePanel panel = new SimplePanel();
		panel.add(yearListBox);
		initWidget(panel);
	}

	@Override
	public void onLoading() {
		getYearListBox().setEnabled(false);
	}
	
	public void onValueChanged(InitialDatesView initialDatesView) {
		ListBox yearListBox = getYearListBox();
		int earliestYear = getYear(initialDatesView.getEarliestDate());
		int latestYear = getYear(initialDatesView.getLatestDate());
		int initialYear;
		if (getYear() == null) {
			initialYear = getYear(initialDatesView.getInitialDate());
		}
		else {
			initialYear = getYear();
		}
		
		setYear(initialYear);
		int selectedIndex = 0;
		for (int year = earliestYear; year <= latestYear; year++) {
			if (initialYear == year) {
				selectedIndex = yearListBox.getItemCount();
			}
			yearListBox.addItem(Integer.toString(year));
		}
		yearListBox.setSelectedIndex(selectedIndex);
	}
	
	@Override
	public void onLoaded() {
		getYearListBox().setEnabled(true);
	}
	
	@SuppressWarnings("deprecation")
	protected int getYear(Date date) {
		return date.getYear() + 1900;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected GameFilter createGameFilter() {
		return new YearGameFilter(new Date(getYear() - 1900, 1, 1));
	}
	
	protected Integer getYear() {
		return i_year;
	}

	protected void setYear(Integer year) {
		i_year = year;
	}

	public ListBox getYearListBox() {
		return i_yearListBox;
	}

	public void setYearListBox(ListBox yearListBox) {
		i_yearListBox = yearListBox;
	}

}
