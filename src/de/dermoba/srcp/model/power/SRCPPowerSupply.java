/*------------------------------------------------------------------------
 * 
 * copyright : (C) 2010 by Michael Lipp 
 * email     : mnl@mnl.de
 * website   : http://sourceforge.net/projects/adhocrailway
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
package de.dermoba.srcp.model.power;

/**
 * This class represents an SRCP power supply.
 * 
 * @author mnl
 */
public class SRCPPowerSupply {
    int bus;
    private SRCPPowerState state;

    /**
     * Create a new instance of a power supply for the given bus.
     * 
     * @param bus
     */
    public SRCPPowerSupply(int bus) {
        this.bus = bus;
    }

    /**
     * Return the bus this power supply is attached to.
     * 
     * @return the bus
     */
    public int getBus() {
        return bus;
    }

    /**
     * Return the current state of the power supply.
     * 
     * @return the state
     */
    public SRCPPowerState getState() {
        return state;
    }

    /**
     * Set the state of the power supply.
     * 
     * @param state the state to set
     */
    public void setState(SRCPPowerState state) {
        this.state = state;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bus;
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SRCPPowerSupply other = (SRCPPowerSupply) obj;
        if (bus != other.bus) {
            return false;
        }
        return true;
    }
}
