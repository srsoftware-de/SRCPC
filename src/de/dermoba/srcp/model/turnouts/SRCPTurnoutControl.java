/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPTurnoutControl.java,v 1.8 2011/12/18 09:43:06 andre_schenk Exp $
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

package de.dermoba.srcp.model.turnouts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;



import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPDeviceLockedException;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.GA;
import de.dermoba.srcp.devices.listener.GAInfoListener;
import de.dermoba.srcp.model.Constants;
import de.dermoba.srcp.model.InvalidAddressException;
import de.dermoba.srcp.model.NoSessionException;
import de.dermoba.srcp.model.SRCPAddress;
import de.dermoba.srcp.model.SRCPModelException;
import de.srsoftware.Logging.Logger;

public class SRCPTurnoutControl implements GAInfoListener {
	private static Logger					logger					= Logger
																			.getLogger(SRCPTurnoutControl.class);
	private static SRCPTurnoutControl		instance;

	private List<SRCPTurnoutChangeListener>	listeners;

	private List<SRCPTurnout>				srcpTurnouts;

	private Map<SRCPAddress, SRCPTurnout>	addressTurnoutCache;
	private Map<SRCPAddress, SRCPTurnout>	addressThreewayCache;
	private SRCPTurnout						lastChangedTurnout;

	private SRCPTurnoutState				previousState;
	private SRCPSession						session;
	private boolean							interface6051Connected	= Constants.INTERFACE_6051_CONNECTED;
	private int								turnoutActivationTime	= Constants.DEFAULT_ACTIVATION_TIME;
	private int								cutterActivationTime	= Constants.DEFAULT_CUTTER_ACTIVATION_TIME;

	private SRCPTurnoutControl() {
		logger.info("SRCPTurnoutControl loaded");
		listeners = new ArrayList<SRCPTurnoutChangeListener>();
		srcpTurnouts = new ArrayList<SRCPTurnout>();

		this.addressTurnoutCache = new HashMap<SRCPAddress, SRCPTurnout>();
		this.addressThreewayCache = new HashMap<SRCPAddress, SRCPTurnout>();
	}

	public static SRCPTurnoutControl getInstance() {
		if (instance == null) {
			instance = new SRCPTurnoutControl();
		}
		return instance;
	}

	public void update(Set<SRCPTurnout> turnouts) {
		this.addressTurnoutCache.clear();
		this.addressThreewayCache.clear();
		this.srcpTurnouts.clear();
		for (SRCPTurnout turnout : turnouts) {
			srcpTurnouts.add(turnout);
			turnout.setSession(session);

			addressTurnoutCache.put(new SRCPAddress(turnout.getBus1(), turnout
					.getAddress1(), turnout.getBus2(), turnout.getAddress2()),
					turnout);
			if (turnout.isThreeWay()) {
				addressThreewayCache.put(new SRCPAddress(turnout.getBus1(),
						turnout.getAddress1(), 0, 0), turnout);
				addressThreewayCache.put(new SRCPAddress(0, 0, turnout
						.getBus2(), turnout.getAddress2()), turnout);
			}
		}
	}

	public void setSession(SRCPSession session) {
		this.session = session;
		if (session != null) {
			session.getInfoChannel().addGAInfoListener(this);
		}
		for (SRCPTurnout st : srcpTurnouts) {
			st.setSession(session);
		}
	}

	/**
	 * Returns the port to activate according to the addressSwitched flag.
	 * 
	 * @param wantedPort
	 *            The port to 'convert'
	 * @return The 'converted' port
	 */
	int getPort(SRCPTurnout turnout, int wantedPort) {
		if (!turnout.isAddress1Switched()) {
			return wantedPort;
		} else {
			if (wantedPort == SRCPTurnout.TURNOUT_STRAIGHT_PORT) {
				return SRCPTurnout.TURNOUT_CURVED_PORT;
			} else {
				return SRCPTurnout.TURNOUT_STRAIGHT_PORT;
			}
		}
	}

	public void refresh(SRCPTurnout turnout) throws SRCPTurnoutException,
			SRCPModelException {
		checkTurnout(turnout);
		if (turnout.isThreeWay()) {
			refreshThreeWay(turnout);
			return;
		}

		switch (turnout.getTurnoutState()) {
		case STRAIGHT:
			setStraight(turnout);
			break;
		case LEFT:
			setCurvedLeft(turnout);
			break;
		case RIGHT:
			setCurvedRight(turnout);
			break;
		default:
		}
		// informListeners(turnout);
	}

	private void refreshThreeWay(SRCPTurnout turnout) throws SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			// initTurnout(t);
			refresh(t);
		}
	}

	public void toggle(SRCPTurnout turnout) throws SRCPTurnoutException,
			SRCPModelException {
		checkTurnout(turnout);

		if (turnout.isThreeWay()) {
			toggleThreeWay(turnout);
			return;
		}
		previousState = turnout.getTurnoutState();
		switch (previousState) {
		case STRAIGHT:
			setCurvedLeft(turnout);
			break;
		case RIGHT:
		case LEFT:
			setStraight(turnout);
			break;
		case UNDEF:
			setDefaultState(turnout);
		}
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	private void toggleThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			initTurnout(t);
		}
		switch (turnout.getTurnoutState()) {
		case LEFT:
			setStraightThreeWay(turnout);
			break;
		case STRAIGHT:
			setCurvedRightThreeWay(turnout);
			break;
		case RIGHT:
			setCurvedLeftThreeWay(turnout);
			break;
		case UNDEF:
			setStraightThreeWay(turnout);
			break;
		}
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	public void setDefaultState(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		previousState = turnout.getTurnoutState();
		if (turnout.isThreeWay()) {
			setDefaultStateThreeWay(turnout);
			return;
		}
		switch (turnout.getDefaultState()) {
		case STRAIGHT:
			setStraight(turnout);
			break;
		case LEFT:
		case RIGHT:
			setCurvedLeft(turnout);
			break;
		}
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	private void setDefaultStateThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			initTurnout(t);
		}
		setStraight(subTurnouts[0]);
		setStraight(subTurnouts[1]);
		turnout.setTurnoutState(SRCPTurnoutState.STRAIGHT);
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	public void setNonDefaultState(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		previousState = turnout.getTurnoutState();
		if (turnout.isThreeWay()) {
			setNonDefaultStateTheeWay(turnout);
			return;
		}
		switch (turnout.getDefaultState()) {
		case STRAIGHT:
			setCurvedLeft(turnout);
			break;
		case LEFT:
		case RIGHT:
			setStraight(turnout);
			break;
		}
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	private void setNonDefaultStateTheeWay(SRCPTurnout turnout) {
		// do nothing
	}

	public void setStraight(SRCPTurnout turnout) throws SRCPTurnoutException,
			SRCPModelException {
		checkTurnout(turnout);
		previousState = turnout.getTurnoutState();
		if (turnout.isThreeWay()) {
			setStraightThreeWay(turnout);
			return;
		}
		GA ga = turnout.getGA();
		try {
			int time;
			int reps;
			if (turnout.isCutter()) {
				time = cutterActivationTime;
				reps = 5;
			} else {
				time = turnoutActivationTime;
				reps = 1;
			}
			for (int i = 0; i < reps; i++) {
				ga.set(getPort(turnout, SRCPTurnout.TURNOUT_STRAIGHT_PORT),
						SRCPTurnout.TURNOUT_PORT_ACTIVATE, time);
			}
			// ga.set(getPort(turnout, SRCPTurnout.TURNOUT_CURVED_PORT),
			// SRCPTurnout.TURNOUT_PORT_DEACTIVATE, activationTime);
			if (turnout.isCutter())
				turnout.setTurnoutState(SRCPTurnoutState.LEFT);
			else
				turnout.setTurnoutState(SRCPTurnoutState.STRAIGHT);

			// informListeners(turnout);
			lastChangedTurnout = turnout;
		} catch (SRCPDeviceLockedException x1) {
			throw new SRCPTurnoutLockedException(Constants.ERR_LOCKED, x1);
		} catch (SRCPException e) {
			logger.error(e);
			throw new SRCPTurnoutException(Constants.ERR_TOGGLE_FAILED, e);
		}
	}

	private void setStraightThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			initTurnout(t);
		}
		setStraight(subTurnouts[0]);
		setStraight(subTurnouts[1]);
		turnout.setTurnoutState(SRCPTurnoutState.STRAIGHT);
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	public void setCurvedLeft(SRCPTurnout turnout) throws SRCPTurnoutException,
			SRCPModelException {
		checkTurnout(turnout);
		previousState = turnout.getTurnoutState();
		if (turnout.isThreeWay()) {
			setCurvedLeftThreeWay(turnout);
			return;
		}
		GA ga = turnout.getGA();
		try {
			int time;
			int reps;
			if (turnout.isCutter()) {
				time = cutterActivationTime;
				reps = 5;
			} else {
				time = turnoutActivationTime;
				reps = 1;
			}

			for (int i = 0; i < reps; i++) {
				ga.set(getPort(turnout, SRCPTurnout.TURNOUT_CURVED_PORT),
						SRCPTurnout.TURNOUT_PORT_ACTIVATE, time);
			}

			// ga.set(getPort(turnout, SRCPTurnout.TURNOUT_STRAIGHT_PORT),
			// SRCPTurnout.TURNOUT_PORT_DEACTIVATE, activationTime);
			turnout.setTurnoutState(SRCPTurnoutState.LEFT);
			// informListeners(turnout);
			lastChangedTurnout = turnout;
		} catch (SRCPDeviceLockedException x1) {
			throw new SRCPTurnoutLockedException(Constants.ERR_LOCKED, x1);
		} catch (SRCPException e) {
			logger.error(e);
			throw new SRCPTurnoutException(Constants.ERR_TOGGLE_FAILED, e);
		}
	}

	private void setCurvedLeftThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			initTurnout(t);
		}

		setCurvedLeft(subTurnouts[0]);
		setStraight(subTurnouts[1]);
		turnout.setTurnoutState(SRCPTurnoutState.LEFT);
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	public void setCurvedRight(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		previousState = turnout.getTurnoutState();
		if (turnout.isThreeWay()) {
			setCurvedRightThreeWay(turnout);
			return;
		}
		setCurvedLeft(turnout);

		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	private void setCurvedRightThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException, SRCPModelException {
		checkTurnout(turnout);
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout t : subTurnouts) {
			checkTurnout(t);
			initTurnout(t);
		}

		setStraight(subTurnouts[0]);
		setCurvedLeft(subTurnouts[1]);
		turnout.setTurnoutState(SRCPTurnoutState.RIGHT);
		// informListeners(turnout);
		lastChangedTurnout = turnout;
	}

	public SRCPTurnoutState getTurnoutState(SRCPTurnout turnout) {

		return turnout.getTurnoutState();

	}

	public void GAset(double timestamp, int bus, int address, int port,
			int value) {
		logger.debug("GAset(" + bus + " , " + address + " , " + port + " , "
				+ value + " )");
		SRCPTurnout turnout = getTurnoutByAddressBus(bus, address);
		if (turnout == null) {
			// a turnout which the srcp-server knows but not me
			return;
		}
		try {
			checkTurnout(turnout);
			
			if (value == 0) {
				// ignore deactivation
				return;
			}
			// a port has been activated
			if (turnout.isThreeWay()) {
				portChangedThreeway(turnout, address, port);
			} else {
				portChanged(turnout, port);
			}

			informListeners(turnout);
		} catch (SRCPModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void portChanged(SRCPTurnout turnout, int port) {
		if (turnout.isCutter()) {
			turnout.setTurnoutState(SRCPTurnoutState.LEFT);
			return;
		}

		if (port == getPort(turnout, SRCPTurnout.TURNOUT_STRAIGHT_PORT)) {
			turnout.setTurnoutState(SRCPTurnoutState.STRAIGHT);
		} else if (port == getPort(turnout, SRCPTurnout.TURNOUT_CURVED_PORT)) {
			turnout.setTurnoutState(SRCPTurnoutState.LEFT);
		}
	}

	private void portChangedThreeway(SRCPTurnout turnout, int address, int port) {
		SRCPTurnout[] subTurnouts = turnout.getSubTurnouts();
		for (SRCPTurnout subTurnout : subTurnouts) {
			if (subTurnout.getAddress1() == address) {
				portChanged(subTurnout, port);
			}
		}
		if (subTurnouts[0].getTurnoutState() == SRCPTurnoutState.STRAIGHT
				&& subTurnouts[1].getTurnoutState() == SRCPTurnoutState.STRAIGHT) {
			turnout.setTurnoutState(SRCPTurnoutState.STRAIGHT);
		} else if (subTurnouts[0].getTurnoutState() == SRCPTurnoutState.LEFT
				&& subTurnouts[1].getTurnoutState() == SRCPTurnoutState.STRAIGHT) {
			turnout.setTurnoutState(SRCPTurnoutState.LEFT);
		} else if (subTurnouts[0].getTurnoutState() == SRCPTurnoutState.STRAIGHT
				&& subTurnouts[1].getTurnoutState() == SRCPTurnoutState.LEFT) {
			turnout.setTurnoutState(SRCPTurnoutState.RIGHT);
		} else if (subTurnouts[0].getTurnoutState() == SRCPTurnoutState.LEFT
				&& subTurnouts[1].getTurnoutState() == SRCPTurnoutState.LEFT) {
			turnout.setTurnoutState(SRCPTurnoutState.UNDEF);
		}
	}

	public void GAinit(double timestamp, int bus, int address, String protocol,
			String[] params) {
		logger.debug("GAinit(" + bus + " , " + address + " , " + protocol
				+ " , " + params + " )");

		SRCPTurnout turnout = getTurnoutByAddressBus(bus, address);
		if (turnout == null)
			// i don't know this turnout
			return;
		try {
			checkTurnout(turnout);
			informListeners(turnout);
		} catch (SRCPModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GAterm(double timestamp, int bus, int address) {
		logger.debug("GAterm( " + bus + " , " + address + " )");
		SRCPTurnout turnout = getTurnoutByAddressBus(bus, address);
		try {
			checkTurnout(turnout);
			if (turnout != null) {
				turnout.setGA(null);
				turnout.setInitialized(false);
				informListeners(turnout);
			}
		} catch (SRCPModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void addTurnoutChangeListener(SRCPTurnoutChangeListener listener) {
		listeners.add(listener);
	}

	public void removeTurnoutChangeListener(SRCPTurnoutChangeListener listener) {
		listeners.remove(listener);
	}

	public void removeAllTurnoutChangeListener() {
		listeners.clear();
	}

	void informListeners(SRCPTurnout changedTurnout) {

		for (SRCPTurnoutChangeListener scl : listeners)
			scl
					.turnoutChanged(changedTurnout, changedTurnout
							.getTurnoutState());
		logger.debug("turnoutChanged(" + changedTurnout + ")");

	}

	public void addTurnout(SRCPTurnout turnout) {
		srcpTurnouts.add(turnout);
		addressTurnoutCache.put(new SRCPAddress(turnout.getBus1(), turnout
				.getAddress1(), turnout.getBus2(), turnout.getAddress2()),
				turnout);
		if (turnout.isThreeWay()) {
			addressThreewayCache.put(new SRCPAddress(turnout.getBus1(), turnout
					.getAddress1(), 0, 0), turnout);
			addressThreewayCache.put(new SRCPAddress(0, 0, turnout.getBus2(),
					turnout.getAddress2()), turnout);
		}
	}

	public void removeTurnout(SRCPTurnout turnout) {
		srcpTurnouts.remove(turnout);
		addressTurnoutCache.remove(new SRCPAddress(turnout.getBus1(), turnout
				.getAddress1(), turnout.getBus2(), turnout.getAddress2()));
		if (turnout.isThreeWay()) {

			addressTurnoutCache.remove(new SRCPAddress(turnout.getBus1(),
					turnout.getAddress1(), 0, 0));
			addressTurnoutCache.remove(new SRCPAddress(0, 0, turnout.getBus2(),
					turnout.getAddress2()));
		}
	}

	void checkTurnout(SRCPTurnout turnout) throws SRCPModelException {
		logger.debug("checkTurnout(" + turnout + ")");
		if (turnout == null) {
			return;
		}
		if (!turnout.checkBusAddress())
			throw new InvalidAddressException(
					"Turnout has an invalid address or bus");

		if (!srcpTurnouts.contains(turnout)) {
			srcpTurnouts.add(turnout);
			addressTurnoutCache.put(new SRCPAddress(turnout.getBus1(), turnout
					.getAddress1(), turnout.getBus2(), turnout.getAddress2()),
					turnout);
			if (turnout.isThreeWay()) {
				addressThreewayCache.put(new SRCPAddress(turnout.getBus1(),
						turnout.getAddress1(), 0, 0), turnout);
				addressThreewayCache.put(new SRCPAddress(0, 0, turnout
						.getBus2(), turnout.getAddress2()), turnout);
			}
		}

		if (turnout.getSession() == null && session == null) {
			throw new NoSessionException();
		}
		if (turnout.getSession() == null && session != null) {
			turnout.setSession(session);
		}

		initTurnout(turnout);
	}

	private void initTurnout(SRCPTurnout turnout) throws SRCPTurnoutException {

		if (!turnout.isInitialized()) {
			if (turnout.isThreeWay())
				initTurnoutThreeWay(turnout);
			try {
				GA ga = new GA(session, turnout.getBus1());
				if (interface6051Connected) {
					ga.init(turnout.getAddress1(), turnout.getProtocol());
				} else {
					ga.setAddress(turnout.getAddress1());
				}

				turnout.setGA(ga);
				turnout.setInitialized(true);
			} catch (SRCPException e) {
				logger.error(e);
				throw new SRCPTurnoutException(Constants.ERR_INIT_FAILED, e);
			}
		}
	}

	private void initTurnoutThreeWay(SRCPTurnout turnout)
			throws SRCPTurnoutException {
		SRCPTurnout turnout1 = (SRCPTurnout) turnout.clone();
		SRCPTurnout turnout2 = (SRCPTurnout) turnout.clone();

		turnout1.setBus1(turnout.getBus1());
		turnout1.setAddress1(turnout.getAddress1());
		turnout1.setAddress1Switched(turnout.isAddress1Switched());
		turnout1.setTurnoutType(SRCPTurnoutTypes.DEFAULT);
		turnout1.setSession(turnout.getSession());

		turnout2.setBus1(turnout.getBus2());
		turnout2.setAddress1(turnout.getAddress2());
		turnout2.setAddress1Switched(turnout.isAddress2Switched());
		turnout2.setTurnoutType(SRCPTurnoutTypes.DEFAULT);
		turnout2.setSession(turnout.getSession());

		srcpTurnouts.add(turnout1);
		srcpTurnouts.add(turnout2);
		initTurnout(turnout1);
		initTurnout(turnout2);

		SRCPTurnout[] turnouts = new SRCPTurnout[] { turnout1, turnout2 };
		turnout.setSubTurnouts(turnouts);

	}

	public void undoLastChange() throws SRCPTurnoutException,
			SRCPModelException {
		if (lastChangedTurnout == null) {
			return;
		}
		switch (previousState) {

		case STRAIGHT:
			setStraight(lastChangedTurnout);
			break;
		case LEFT:
			setCurvedLeft(lastChangedTurnout);
			break;
		case RIGHT:
			setCurvedRight(lastChangedTurnout);
			break;
		case UNDEF:
			setStraight(lastChangedTurnout);
			break;
		}
		// informListeners(lastChangedTurnout);

		lastChangedTurnout = null;
		previousState = null;
	}

	public void previousDeviceToDefault() throws SRCPTurnoutException,
			SRCPModelException {
		if (lastChangedTurnout == null) {
			return;
		}
		setDefaultState(lastChangedTurnout);
	}

	private SRCPTurnout getTurnoutByAddressBus(int bus, int address) {
		SRCPAddress key1 = new SRCPAddress(bus, address, 0, 0);
		SRCPTurnout lookup1 = addressTurnoutCache.get(key1);
		if (lookup1 != null)
			return lookup1;
		SRCPAddress key2 = new SRCPAddress(0, 0, bus, address);
		SRCPTurnout lookup2 = addressTurnoutCache.get(key2);
		if (lookup2 != null)
			return lookup2;
		SRCPTurnout threewayLookup1 = addressThreewayCache.get(key1);
		if (threewayLookup1 != null)
			return threewayLookup1;

		SRCPTurnout threewayLookup2 = addressThreewayCache.get(key2);
		if (threewayLookup2 != null)
			return threewayLookup2;

		return null;
	}

	public boolean isInterface6051Connected() {
		return interface6051Connected;
	}

	public void setInterface6051Connected(boolean interface6051Connected) {
		this.interface6051Connected = interface6051Connected;
	}

	public int getTurnoutActivationTime() {
		return turnoutActivationTime;
	}

	public void setTurnoutActivationTime(int turnoutActivationTime) {
		this.turnoutActivationTime = turnoutActivationTime;
	}

	public int getCutterActivationTime() {
		return cutterActivationTime;
	}

	public void setCutterActivationTime(int cutterActivationTime) {
		this.cutterActivationTime = cutterActivationTime;
	}
}
