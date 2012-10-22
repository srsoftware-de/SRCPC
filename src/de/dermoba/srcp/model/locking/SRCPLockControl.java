/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPLockControl.java,v 1.5 2011/12/18 09:15:44 andre_schenk Exp $
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

package de.dermoba.srcp.model.locking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPDeviceLockedException;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.LOCK;
import de.dermoba.srcp.devices.listener.LOCKInfoListener;
import de.dermoba.srcp.model.Constants;
import de.dermoba.srcp.model.SRCPAddress;
import de.srsoftware.Logging.Logger;

public class SRCPLockControl implements LOCKInfoListener, Constants {
	private static Logger					logger			= Logger.getLogger(SRCPLockControl.class);
	private static SRCPLockControl			instance		= null;
	private SRCPSession						session;

	Map<String, Map<SRCPAddress, Object>>	addressToControlObject;
	Map<Object, SRCPLock>					locks;

	private List<SRCPLockChangeListener>	listeners;
	private int								lockDuration	= Constants.DEFAULT_LOCK_DURATION;

	private SRCPLockControl() {
		addressToControlObject = new HashMap<String, Map<SRCPAddress, Object>>();
		locks = new HashMap<Object, SRCPLock>();
		listeners = new ArrayList<SRCPLockChangeListener>();
	}

	public static SRCPLockControl getInstance() {
		if (instance == null) {
			instance = new SRCPLockControl();
		}
		return instance;
	}

	public void registerControlObject(String deviceGroup, SRCPAddress address,
			Object object) {
		if (addressToControlObject.get(deviceGroup) == null)
			addressToControlObject.put(deviceGroup,
					new HashMap<SRCPAddress, Object>());
		
		if(addressToControlObject.get(deviceGroup).get(address) != null) {
			//got it already
			return;
		}
		addressToControlObject.get(deviceGroup).put(address, object);
		SRCPLock lock = new SRCPLock(new LOCK(session, address.getBus1()),
				false, -1);
		locks.put(object, lock);
	}

	public void unregisterControlObject(String deviceGroup, SRCPAddress address) {
		if (addressToControlObject.get(deviceGroup) == null)
			return;
		addressToControlObject.get(deviceGroup).remove(address);
	}

	public void setSession(SRCPSession session) {
		this.session = session;
		if (session != null)
			session.getInfoChannel().addLOCKInfoListener(this);
	}

	public boolean acquireLock(String deviceGroup, SRCPAddress address)
			throws SRCPLockingException, SRCPDeviceLockedException {
		logger.info("acquireLock( " + deviceGroup + " , " + address + " )");
		if (addressToControlObject.get(deviceGroup) == null)
			throw new SRCPLockingException("Object to lock not found");
		if (addressToControlObject.get(deviceGroup).get(address) == null)
			throw new SRCPLockingException("Object to lock not found");

		Object obj = addressToControlObject.get(deviceGroup).get(address);
		SRCPLock sLock = locks.get(obj);
		LOCK lock = sLock.getLock();
		try {
			// sLock.setLocked(true);
			// sLock.setSessionID(session.getCommandChannelID());
			lock.set(deviceGroup, address.getAddress1(), lockDuration);
		} catch (SRCPException e) {
			throw new SRCPLockingException(ERR_FAILED, e);
		}
		return true;
	}

	public boolean releaseLock(String deviceGroup, SRCPAddress address)
			throws SRCPLockingException, SRCPDeviceLockedException {

		logger.info("releaseLock( " + deviceGroup + " , " + address + " )");
		if (addressToControlObject.get(deviceGroup) == null)
			throw new SRCPLockingException("Object to unlock not found");
		if (addressToControlObject.get(deviceGroup).get(address) == null)
			throw new SRCPLockingException("Object to unlock not found");

		Object obj = addressToControlObject.get(deviceGroup).get(address);

		SRCPLock sLock = locks.get(obj);
		LOCK lock = sLock.getLock();
		try {
			lock.term(deviceGroup, address.getAddress1());
			// sLock.setLocked(false);
			// sLock.setSessionID(-1);
		} catch (SRCPException e) {
			throw new SRCPLockingException(ERR_FAILED, e);
		}
		return true;
	}

	public void releaseAllLocks() throws SRCPLockingException {

	}

	public void LOCKset(double timestamp, int bus, int address,
			String deviceGroup, int duration, int sessionID) {
		logger.debug("LOCKset( " + bus + " , " + address + " , " + deviceGroup
				+ " , " + duration + " , " + sessionID + " )");
		SRCPAddress addr = new SRCPAddress(bus, address);

		Object object = addressToControlObject.get(deviceGroup).get(addr);
		if (object != null) {
			SRCPLock sLock = locks.get(object);
			sLock.setLocked(true);
			sLock.setSessionID(sessionID);
			informListeners(object, true);
		}
	}

	public void LOCKterm(double timestamp, int bus, int address,
			String deviceGroup) {
		logger.debug("LOCKterm( " + bus + " , " + address + " , " + deviceGroup
				+ " )");
		SRCPAddress addr = new SRCPAddress(bus, address);

		Object object = addressToControlObject.get(deviceGroup).get(addr);
		if (object != null) {
			SRCPLock sLock = locks.get(object);
			sLock.setLocked(false);
			sLock.setSessionID(-1);
			informListeners(object, false);
		}
	}

	private void informListeners(Object object, boolean locked) {
		for (SRCPLockChangeListener l : listeners) {
			l.lockChanged(object, locked);
		}
		logger.debug("lockChanged");
	}

	public void addLockChangeListener(SRCPLockChangeListener l) {
		listeners.add(l);
	}

	public void removeLockChangeListener(SRCPLockChangeListener l) {
		listeners.remove(l);
	}

	public void removeAllLockChangeListener() {
		listeners.clear();
	}

	public boolean isLocked(String deviceGroup, SRCPAddress lookupAddress) {

		Map<SRCPAddress, Object> deviceGroupLocks = addressToControlObject
				.get(deviceGroup);
		if (deviceGroupLocks == null) {
			return false;
		}
		Object object = deviceGroupLocks.get(lookupAddress);

		if (object != null) {
			SRCPLock sLock = locks.get(object);
			return sLock.isLocked();
		} else {
			return false;
		}
	}

	public int getLockingSessionID(String deviceGroup, SRCPAddress lookupAddress) {

		Object object = addressToControlObject.get(deviceGroup).get(
				lookupAddress);
		if (object != null) {
			SRCPLock sLock = locks.get(object);
			return sLock.getSessionID();
		} else {
			return -1;
		}
	}

	public void setLockDuration(int lockDuration) {
		this.lockDuration = lockDuration;
	}
}
