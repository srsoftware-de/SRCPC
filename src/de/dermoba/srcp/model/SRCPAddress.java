/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2008 by Benjamin Mueller 
 * email     : news@fork.ch
 * website   : http://sourceforge.net/projects/adhocrailway
 * version   : $Id: SRCPAddress.java,v 1.1 2008/04/24 06:19:05 fork_ch Exp $
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

package de.dermoba.srcp.model;

public class SRCPAddress {
	private int	bus1;
	private int	address1;
	private int	bus2;
	private int	address2;

	public SRCPAddress(int bus1, int address1) {
		this(bus1, address1, 0, 0);
	}

	public SRCPAddress(int bus1, int address1, int bus2, int address2) {
		super();
		this.bus1 = bus1;
		this.address1 = address1;
		this.bus2 = bus2;
		this.address2 = address2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + address1;
		result = prime * result + address2;
		result = prime * result + bus1;
		result = prime * result + bus2;
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

		final SRCPAddress other = (SRCPAddress) obj;
		if (address1 != other.address1)
			return false;
		if (address2 != other.address2)
			return false;
		if (bus1 != other.bus1)
			return false;
		if (bus2 != other.bus2)
			return false;
		return true;
	}

	public String toString() {
		if (bus2 == 0 && address2 == 0) {
			return "[" + bus1 + "," + address1 + "]";
		}
		return "[" + bus1 + "," + address1 + "][" + bus2 + "," + address2 + "]";
	}

	public int getBus1() {
		return bus1;
	}

	public int getAddress1() {
		return address1;
	}

	public int getBus2() {
		return bus2;
	}

	public int getAddress2() {
		return address2;
	}
}
