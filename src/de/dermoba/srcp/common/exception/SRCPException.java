package de.dermoba.srcp.common.exception;

/**
 *
 * @author  osc
 * @version $Revision: 1.8 $
 */

public abstract class SRCPException extends java.lang.Exception {
    private static final long serialVersionUID = 5999256199563204126L;

    protected String requestString = null;
    protected int errorNumber;
    protected String errorMessage;

    /**
     * Create a new SRCPException with specified error number and message
     *
     * @param number
     * @param msg
     */
    public SRCPException(int number, String msg) {
        super(msg);
        errorNumber = number;
        errorMessage=msg;
    }

    public SRCPException(int number, String msg, Throwable cause) {
        super(msg, cause);
        errorNumber = number;
        errorMessage=msg;
    }

    public int getErrorNumber() {
        return errorNumber;
    }

    public String getMessage() {
        return errorMessage;
    }

    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    public String toString() {
        if (requestString != null) {
            return "\"" + requestString + "\" failed: " + getMessage();
        }
        else {
            return getMessage();
        }
    }

    public abstract SRCPException cloneExc();
}



