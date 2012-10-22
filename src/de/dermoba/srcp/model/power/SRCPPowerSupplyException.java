/**
 * 
 */
package de.dermoba.srcp.model.power;

import de.dermoba.srcp.model.SRCPModelException;

/**
 * @author mnl
 *
 */
@SuppressWarnings("serial")
public class SRCPPowerSupplyException extends SRCPModelException {

    /**
     * 
     */
    public SRCPPowerSupplyException() {
    }

    /**
     * @param message
     */
    public SRCPPowerSupplyException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public SRCPPowerSupplyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public SRCPPowerSupplyException(Throwable cause) {
        super(cause);
    }

}
