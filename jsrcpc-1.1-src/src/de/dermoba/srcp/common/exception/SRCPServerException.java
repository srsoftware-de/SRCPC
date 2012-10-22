package de.dermoba.srcp.common.exception;

/**
 * 
 * @author osc
 * @version $Revision: 1.4 $
 */

public abstract class SRCPServerException extends SRCPException {
    private static final long serialVersionUID = -2825143687829379449L;

	public SRCPServerException(int Number, String msg) {
		super(Number, msg);
	}
	public SRCPServerException(int Number, String msg, Throwable cause) {
		super(Number, msg, cause);
	}

}
