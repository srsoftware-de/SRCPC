/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPRouteException.java,v 1.2 2008/06/09 12:31:34 andre_schenk Exp $
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

import de.dermoba.srcp.model.SRCPModelException;

public class SRCPRouteException extends SRCPModelException {
    private static final long serialVersionUID = -7323591106207067532L;

	public SRCPRouteException() {
		// TODO Auto-generated constructor stub
	}

	public SRCPRouteException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public SRCPRouteException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public SRCPRouteException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
