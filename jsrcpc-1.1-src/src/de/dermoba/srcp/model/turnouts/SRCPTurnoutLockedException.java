/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPTurnoutLockedException.java,v 1.2 2008/06/09 12:31:34 andre_schenk Exp $
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

public class SRCPTurnoutLockedException extends SRCPTurnoutException {
    private static final long serialVersionUID = -2591868435018483946L;

    public SRCPTurnoutLockedException(String msg) {
		super(msg);
	}

	public SRCPTurnoutLockedException(String msg, Exception parent) {
		super(msg, parent);
	}
}
