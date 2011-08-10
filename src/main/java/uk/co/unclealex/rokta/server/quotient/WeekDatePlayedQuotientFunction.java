package uk.co.unclealex.rokta.server.quotient;

import org.joda.time.DateTime;

import uk.co.unclealex.rokta.server.quotient.visitor.DatePlayedQuotientFunctionVisitor;

public class WeekDatePlayedQuotientFunction extends DatePlayedQuotientFunction {

	@Override
	public long transformDatePlayed(DateTime dateTime) {
		return dateTime.getYear() * 100 + dateTime.getWeekOfWeekyear();
	}

	public <T> T accept(DatePlayedQuotientFunctionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}