/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPRouter.java,v 1.3 2008/05/12 18:02:23 fork_ch Exp $
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

import java.util.List;

import de.dermoba.srcp.model.SRCPModelException;
import de.dermoba.srcp.model.turnouts.SRCPTurnout;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutChangeListener;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutControl;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutException;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutState;

public class SRCPRouter extends Thread implements SRCPTurnoutChangeListener {

	private boolean							enableRoute;
	private int								waitTime;
	private List<SRCPRouteChangeListener>	listener;
	private SRCPModelException				switchException;
	private SRCPRoute						sRoute;

	public SRCPRouter(SRCPRoute sRoute, boolean enableRoute, int waitTime,
			List<SRCPRouteChangeListener> listener) {
		this.sRoute = sRoute;
		this.enableRoute = enableRoute;
		this.waitTime = waitTime;
		this.listener = listener;
	}

	public void run() {
		try {
			sRoute.setRouteState(SRCPRouteState.ROUTING);

			SRCPTurnoutControl sc = SRCPTurnoutControl.getInstance();
			sc.addTurnoutChangeListener(this);
			if (enableRoute) {
				enableRoute();
			} else {
				disableRoute();
			}
			sc.removeTurnoutChangeListener(this);
		} catch (SRCPModelException e) {
			this.switchException = e;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void disableRoute() throws SRCPTurnoutException, SRCPModelException, InterruptedException {
		List<SRCPRouteItem> routeItems = sRoute.getRouteItems();
		SRCPTurnoutControl sc = SRCPTurnoutControl.getInstance();
		for (SRCPRouteItem ri : routeItems) {
			SRCPTurnout turnoutToRoute = ri.getTurnout();

			sc.setDefaultState(turnoutToRoute);
			
			Thread.sleep(waitTime);
		}
		sRoute.setRouteState(SRCPRouteState.DISABLED);
		for (SRCPRouteChangeListener l : listener) {
			l.routeChanged(sRoute);
		}
	}

	private void enableRoute() throws SRCPTurnoutException, SRCPModelException, InterruptedException {
		List<SRCPRouteItem> routeItems = sRoute.getRouteItems();
		SRCPTurnoutControl sc = SRCPTurnoutControl.getInstance();
		for (SRCPRouteItem ri : routeItems) {
			SRCPTurnout turnoutToRoute = ri.getTurnout();
			switch (ri.getRoutedState()) {
			case STRAIGHT:
				sc.setStraight(turnoutToRoute);
				break;
			case LEFT:
				sc.setCurvedLeft(turnoutToRoute);
				break;
			case RIGHT:
				sc.setCurvedRight(turnoutToRoute);
				break;
			}
			Thread.sleep(waitTime);
		}
		sRoute.setRouteState(SRCPRouteState.ENABLED);
		for (SRCPRouteChangeListener l : listener) {
			l.routeChanged(sRoute);
		}
	}

	public SRCPModelException getSwitchException() {
		return switchException;
	}

	public void turnoutChanged(SRCPTurnout changedTurnout,
			SRCPTurnoutState newState) {
		for(SRCPRouteItem item : sRoute.getRouteItems()) {
			if(item.getTurnout().equals(changedTurnout)) {
				for (SRCPRouteChangeListener l : listener) {
					if(enableRoute) 
						l.nextTurnoutRouted(sRoute);
					else
						l.nextTurnoutDerouted(sRoute);
				}
			}
		}
		
		
	}
}
