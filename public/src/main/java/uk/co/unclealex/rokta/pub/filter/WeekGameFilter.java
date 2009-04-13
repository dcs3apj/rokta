package uk.co.unclealex.rokta.pub.filter;

import java.util.Date;

public class WeekGameFilter extends AbstractGameFilter {

	private Date i_date;
	
	protected WeekGameFilter() {
	}
	
	public WeekGameFilter(Date date) {
		super();
		i_date = date;
	}

	@Override
	public boolean isContinuous() {
		return true;
	}

	@Override
	public String[] toStringArgs() {
		return new String[] { Long.toString(getDate().getTime()) }; 
	}
	
	@Override
	public <T> T accept(GameFilterVistor<T> gameFilterVisitor) {
		return gameFilterVisitor.visit(this);
	}

	public Date getDate() {
		return i_date;
	}
	
	protected void setDate(Date date) {
		i_date = date;
	}
}
