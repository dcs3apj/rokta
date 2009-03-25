package uk.co.unclealex.rokta.internal.process;

public interface SecurityService {

	public boolean logIn(String username, String password);

	public String getCurrentlyLoggedInUser();

	public void logOut();

}
