package de.dermoba.srcp.model.turnouts;

public interface SRCPTurnoutChangeListener {

	public void turnoutChanged(SRCPTurnout changedTurnout, SRCPTurnoutState newState);

}
