package de.dermoba.srcp.common.exception;

public class SRCPHostNotFoundException extends SRCPServerException {

    private static final long serialVersionUID = -706788801560579655L;

    public final static int NUMBER = 602;

	public SRCPHostNotFoundException () {
        super(NUMBER,"host not found");
    }

	public SRCPHostNotFoundException (Throwable cause) {
        super(NUMBER,"host not found", cause);
    }

	public SRCPException cloneExc() {
		return new SRCPHostNotFoundException();
	}

}
