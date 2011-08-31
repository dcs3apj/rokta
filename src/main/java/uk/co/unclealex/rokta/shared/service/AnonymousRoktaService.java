package uk.co.unclealex.rokta.shared.service;

import java.util.Date;

import uk.co.unclealex.rokta.client.filter.GameFilter;
import uk.co.unclealex.rokta.shared.model.ColourView;
import uk.co.unclealex.rokta.shared.model.CurrentInformation;
import uk.co.unclealex.rokta.shared.model.GameSummary;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("anonymous")
public interface AnonymousRoktaService extends RemoteService {

	public Date getDateFirstGamePlayed();
	public Date getDateLastGamePlayed();
	
	public CurrentInformation getCurrentInformation(GameFilter gameFilter, Date currentDate, int targetStreaksSize);
	public GameSummary getLastGameSummary();
	
	public String[] getAllUsersNames();
	public String[] getAllPlayerNames();
	public ColourView[] getAllColourViews();
	
	public String getUserPrincipal();
	
	public boolean authenticate(String username, String password);
	void logout();
}