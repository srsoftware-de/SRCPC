/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPLock.java,v 1.2 2008/04/24 18:37:37 fork_ch Exp $
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

import de.dermoba.srcp.devices.LOCK;

public class SRCPLock {

	private LOCK	lock;
	private boolean	locked;
	private int		sessionID;

	public SRCPLock(LOCK lock, boolean locked, int sessionID) {
		super();
		this.lock = lock;
		this.locked = locked;
		this.sessionID = sessionID;
	}

	public void setLock(LOCK lock) {
		this.lock = lock;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public LOCK getLock() {
		return lock;
	}

	public boolean isLocked() {
		return locked;
	}

	public int getSessionID() {
		return sessionID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (locked ? 1231 : 1237);
		result = prime * result + sessionID;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final SRCPLock other = (SRCPLock) obj;
		if (!lock.equals(other.lock))
			return false;
		if (locked != other.locked)
			return false;
		if (sessionID != other.sessionID)
			return false;
		return true;
	}

}
