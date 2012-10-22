package de.dermoba.srcp.common.exception;

public class SRCPIOException extends SRCPServerException {

	private static final long serialVersionUID = 2283032416479175261L;

    public final static int NUMBER = 603;

    
	public SRCPIOException (Throwable cause) {
        super(NUMBER,"io exception", cause);
    }
	
	public SRCPIOException () {
        super(NUMBER,"io exception");
    }

	public SRCPException cloneExc() {
		return new SRCPIOException();
	}

}
