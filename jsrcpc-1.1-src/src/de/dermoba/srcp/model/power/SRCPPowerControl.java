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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.POWER;
import de.dermoba.srcp.devices.listener.POWERInfoListener;
import de.dermoba.srcp.model.Constants;

/**
 * This class provides a controller for
 * power supplies. The controller is modeled as a singleton,
 * the controlled devices are passed as arguments to the
 * controller's methods.
 * 
 * In addition the controller acts as a directory and maintains a set of known
 * power supplies based on the info messages from the session's 
 * info channel.
 * 
 * @author mnl
 */
public class SRCPPowerControl implements POWERInfoListener, Constants {
    private static Logger logger = Logger
        .getLogger(SRCPPowerControl.class);
    
    private static SRCPPowerControl instance;
    private SRCPSession session;
    private Set<SRCPPowerSupply> srcpPowerSupplies;
    private List<SRCPPowerSupplyChangeListener> listeners;
    
    private SRCPPowerControl () {
        logger.info("SRCPPowerControl loaded");
        srcpPowerSupplies = new HashSet<SRCPPowerSupply>();
        listeners = new ArrayList<SRCPPowerSupplyChangeListener>();
    }

    /**
     * Returns the single common instance of this controller.
     * 
     * @return the controller instance
     */
    public static SRCPPowerControl getInstance() {
        if (instance == null) {
            instance = new SRCPPowerControl();
        }
        return instance;
    }
    
    /**
     * Assign a session to this controller. The session is used to
     * send the commands to the SRCP server.
     * 
     * @param session the session
     */
    public void setSession (SRCPSession session) {
        this.session = session;
        if (session != null) {
            session.getInfoChannel().addPOWERInfoListener(this);
        }
    }

    /**
     * Return the set of know power supplies.
     * 
     * @return the known power supplies
     */
    public Set<SRCPPowerSupply> getKnownPowerSupplies() {
        return Collections.unmodifiableSet(srcpPowerSupplies);
    }
    
    /**
     * Puts the given power supply in the given state.
     * 
     * @param powerSupply the power supply to update
     * @param state the new state
     * @throws SRCPPowerSupplyException if a problem occurs
     */
    public void setState(SRCPPowerSupply powerSupply, SRCPPowerState state)
        throws SRCPPowerSupplyException {
        POWER device = new POWER(session, powerSupply.getBus());
        try {
            device.set(state == SRCPPowerState.ON);
        } catch (SRCPException e) {
            throw new SRCPPowerSupplyException(ERR_FAILED, e);
        }
        // listeners will be informed by messages on the info channel.
    }

    /**
     * Sets the state of all known power supplies.
     * 
     * @param state the new state
     * @throws SRCPPowerSupplyException if a problem occurs
     */
    public void setAllStates(SRCPPowerState state)
        throws SRCPPowerSupplyException {
        for (SRCPPowerSupply ps : srcpPowerSupplies) {
            setState(ps, state);
        }
    }

    /**
     * Return <code>true</code> if all known power supplies have the
     * given state.
     * 
     * @param state the state to verify
     * @return the result
     */
    public boolean isCommonState(SRCPPowerState state) {
        for (SRCPPowerSupply ps : srcpPowerSupplies) {
            if (ps.getState() != state) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Handle a power set information. If the power supply is not
     * known, it is added to the internally maintained list of
     * power supplies.
     * 
     * @see de.dermoba.srcp.devices.listener.POWERInfoListener
     * #POWERset(double, int, boolean)
     */
    public void POWERset(double timestamp, int bus, boolean powerOn) {
        SRCPPowerSupply ps = null;
        for (SRCPPowerSupply iter : srcpPowerSupplies) {
            if (iter.getBus() == bus) {
                ps = iter;
                break;
            }
        }
        if (ps == null) {
            ps = new SRCPPowerSupply(bus);
            srcpPowerSupplies.add(ps);
        }
        if (powerOn) {
            ps.setState(SRCPPowerState.ON);
        } else {
            ps.setState(SRCPPowerState.OFF);
        }
        informListeners(ps);
    }

    /* (non-Javadoc)
     * @see de.dermoba.srcp.devices.POWERInfoListener
     * #POWERterm(double, int)
     */
    public void POWERterm(double timestamp, int bus) {
        for (SRCPPowerSupply iter : srcpPowerSupplies) {
            if (iter.getBus() == bus) {
                srcpPowerSupplies.remove(iter);
                break;
            }
        }
    }

    public void addPowerSupplyChangeListener
        (SRCPPowerSupplyChangeListener l) {
        listeners.add(l);
    }

    public void removePowerSupplyChangeListener
        (SRCPPowerSupplyChangeListener l) {
        listeners.remove(l);
    }

    public void removeAllPowerSupplyChangeListener() {
        listeners.clear();
    }

    private void informListeners(SRCPPowerSupply changedPowerSupply) {
        for (SRCPPowerSupplyChangeListener l : listeners) {
            l.powerSupplyChanged(changedPowerSupply);
        }
    }
}
