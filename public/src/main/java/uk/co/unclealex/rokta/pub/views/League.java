/**
 * 
 */
package uk.co.unclealex.rokta.pub.views;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author alex
 *
 */
public class League implements Serializable {

	private boolean i_current;
	private List<LeagueRow> i_rows = new ArrayList<LeagueRow>();
	private int i_totalGames;
	private int i_totalPlayers;
	private String i_description;
	
	public double getExpectedLossesPerGame() {
		return getTotalGames() / (double) getTotalPlayers();
	}
	/**
	 * @return the totalGames
	 */
	public int getTotalGames() {
		return i_totalGames;
	}
	/**
	 * @param totalGames the totalGames to set
	 */
	public void setTotalGames(int totalGames) {
		i_totalGames = totalGames;
	}
	/**
	 * @return the totalPlayers
	 */
	public int getTotalPlayers() {
		return i_totalPlayers;
	}
	/**
	 * @param totalPlayers the totalPlayers to set
	 */
	public void setTotalPlayers(int totalPlayers) {
		i_totalPlayers = totalPlayers;
	}
	/**
	 * @return the current
	 */
	public boolean isCurrent() {
		return i_current;
	}
	/**
	 * @param current the current to set
	 */
	public void setCurrent(boolean current) {
		i_current = current;
	}
	/**
	 * @return the rows
	 */
	public List<LeagueRow> getRows() {
		return i_rows;
	}
	/**
	 * @param rows the rows to set
	 */
	public void setRows(List<LeagueRow> rows) {
		i_rows = rows;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return i_description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		i_description = description;
	}
}
