/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPRouteControl.java,v 1.2 2008/04/24 18:37:38 fork_ch Exp $
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

import java.util.ArrayList;
import java.util.List;



import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.model.Constants;
import de.dermoba.srcp.model.NoSessionException;
import de.dermoba.srcp.model.turnouts.SRCPTurnoutException;
import de.srsoftware.Logging.Logger;

public class SRCPRouteControl {
	private static Logger					logger				= Logger
																		.getLogger(SRCPRouteControl.class);
	private static SRCPRouteControl			instance;

	private List<SRCPRouteChangeListener>	listeners;

	private SRCPRouteState					lastRouteState;

	private SRCPRoute						lastChangedRoute;

	protected String						ERR_TOGGLE_FAILED	= "Toggle of switch failed";

	protected SRCPSession					session;
	private int								routingDelay		= Constants.DEFAULT_ROUTING_DELAY;

	public SRCPSession getSession() {
		return session;
	}

	public void setSession(SRCPSession session) {
		this.session = session;
	}

	private SRCPRouteControl() {
		logger.info("SRCPRouteControl loaded");
		listeners = new ArrayList<SRCPRouteChangeListener>();
	}

	public static SRCPRouteControl getInstance() {
		if (instance == null) {
			instance = new SRCPRouteControl();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.fork.AdHocRailway.domain.routes.RouteControlIface#enableRoute(ch.fork.AdHocRailway.domain.routes.Route)
	 */
	public void enableRoute(SRCPRoute route) throws SRCPTurnoutException, SRCPRouteException {
		checkRoute(route);
		logger.debug("enabling route: " + route);

		SRCPRouter switchRouter = new SRCPRouter(route, true, routingDelay, listeners);
		switchRouter.start();
		lastChangedRoute = route;
		lastRouteState = SRCPRouteState.ENABLED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.fork.AdHocRailway.domain.routes.RouteControlIface#disableRoute(ch.fork.AdHocRailway.domain.routes.Route)
	 */
	public void disableRoute(SRCPRoute route) throws SRCPTurnoutException , SRCPRouteException{
		checkRoute(route);
		logger.debug("disabling route: " + route);
		
		SRCPRouter switchRouter = new SRCPRouter(route, false, routingDelay, listeners);
		switchRouter.start();
		lastChangedRoute = route;
		lastRouteState = SRCPRouteState.DISABLED;
	}

	private void checkRoute(SRCPRoute r) throws SRCPRouteException {
		if (session == null)
			throw new SRCPRouteException(Constants.ERR_NOT_CONNECTED,
					new NoSessionException());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.fork.AdHocRailway.domain.routes.RouteControlIface#addRouteChangeListener(ch.fork.AdHocRailway.domain.routes.Route,
	 *      ch.fork.AdHocRailway.domain.routes.RouteChangeListener)
	 */
	public void addRouteChangeListener(SRCPRouteChangeListener listener) {

		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.fork.AdHocRailway.domain.routes.RouteControlIface#removeAllRouteChangeListeners()
	 */
	public void removeAllRouteChangeListeners() {
		listeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.fork.AdHocRailway.domain.routes.RouteControlIface#removeRouteChangeListener(ch.fork.AdHocRailway.domain.routes.Route)
	 */
	public void removeRouteChangeListener(SRCPRouteChangeListener listener) {
		listeners.remove(listener);
	}

	public void undoLastChange() throws SRCPRouteException {
		if (lastChangedRoute == null)
			return;
		try {
			switch (lastRouteState) {
			case ENABLED:

				disableRoute(lastChangedRoute);

				break;
			case DISABLED:
				enableRoute(lastChangedRoute);
				break;
			}
			lastChangedRoute = null;
			lastRouteState = null;
		} catch (SRCPTurnoutException e) {
			throw new SRCPRouteException(e);
		}
	}

	public void previousDeviceToDefault() throws SRCPRouteException {
		if (lastChangedRoute == null)
			return;
		try {
			disableRoute(lastChangedRoute);
		} catch (SRCPTurnoutException e) {
			throw new SRCPRouteException(e);
		}
		lastChangedRoute = null;
		lastRouteState = null;
	}

	public void setRoutingDelay(int routingDelay) {
		this.routingDelay = routingDelay;
	}

}
