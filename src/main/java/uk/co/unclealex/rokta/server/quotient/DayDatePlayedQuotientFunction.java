package uk.co.unclealex.rokta.server.quotient;

import org.joda.time.DateTime;

import uk.co.unclealex.rokta.server.quotient.visitor.DatePlayedQuotientFunctionVisitor;

public class DayDatePlayedQuotientFunction extends DatePlayedQuotientFunction {

	@Override
	public long transformDatePlayed(DateTime dateTime) {
		return dateTime.getYear() * 400 + dateTime.getDayOfYear();
	}

	public <T> T accept(DatePlayedQuotientFunctionVisitor<T> visitor) {
		return visitor.visit(this);
	}
}