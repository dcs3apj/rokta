package uk.co.unclealex.rokta.internal.quotient;

import org.joda.time.DateTime;

import uk.co.unclealex.rokta.internal.quotient.visitor.DatePlayedQuotientFunctionVisitor;

public class YearDatePlayedQuotientFunction extends DatePlayedQuotientFunction {

	@Override
	public long transformDatePlayed(DateTime dateTime) {
		return dateTime.getYear();
	}

	public <T> T accept(DatePlayedQuotientFunctionVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
