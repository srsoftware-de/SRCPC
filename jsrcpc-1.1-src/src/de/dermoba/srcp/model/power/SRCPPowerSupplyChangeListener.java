/**
 * 
 */
package de.dermoba.srcp.model.power;

/**
 * Thsi interface defined a listener for state changes of an SRCPPowerSupply.
 * 
 * @author mnl
 */
public interface SRCPPowerSupplyChangeListener {
    /**
     * Inform about a change of the given power supply.
     * 
     * @param powerSupply the power supply
     */
    void powerSupplyChanged(SRCPPowerSupply powerSupply);
}
