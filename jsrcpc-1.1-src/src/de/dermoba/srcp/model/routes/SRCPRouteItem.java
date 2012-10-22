/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPRouteItem.java,v 1.1 2008/04/24 07:29:53 fork_ch Exp $
 * 
 *----------------------------------------------------------------------*/

/*------------------------------------------------------------------------
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 *----------------------------------------------------------------------*/

package de.dermoba.srcp.model.routes;

import de.dermoba.srcp.model.turnouts.SRCPTurnout;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutState;

public class SRCPRouteItem {

	private SRCPTurnout			turnout;

	private SRCPRoute			route;

	private SRCPTurnoutState	routedState;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SRCPRouteItem other = (SRCPRouteItem) obj;
		if (route == null) {
			if (other.route != null)
				return false;
		} else if (!route.equals(other.route))
			return false;
		if (routedState == null) {
			if (other.routedState != null)
				return false;
		} else if (!routedState.equals(other.routedState))
			return false;
		if (turnout == null) {
			if (other.turnout != null)
				return false;
		} else if (!turnout.equals(other.turnout))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((route == null) ? 0 : route.hashCode());
		result = prime * result
				+ ((routedState == null) ? 0 : routedState.hashCode());
		result = prime * result + ((turnout == null) ? 0 : turnout.hashCode());
		return result;
	}

	/** default constructor */
	public SRCPRouteItem() {
	}

	/** full constructor */
	public SRCPRouteItem(SRCPTurnout turnout, SRCPRoute route,
			SRCPTurnoutState routedState) {
		this.turnout = turnout;
		this.route = route;
		this.routedState = routedState;
	}

	public SRCPTurnout getTurnout() {
		return turnout;
	}

	public void setTurnout(SRCPTurnout turnout) {
		this.turnout = turnout;
	}

	public SRCPRoute getRoute() {
		return route;
	}

	public void setRoute(SRCPRoute route) {
		this.route = route;
	}

	public SRCPTurnoutState getRoutedState() {
		return routedState;
	}

	public void setRoutedState(SRCPTurnoutState routedState) {
		this.routedState = routedState;
	}
}
