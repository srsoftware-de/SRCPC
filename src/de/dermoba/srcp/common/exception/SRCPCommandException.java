/*
 * $RCSfile: SRCPCommandException.java,v $
 *
 * History

 */

package de.dermoba.srcp.common.exception;


/**
 *
 * @author  osc
 * @version $Revision: 1.5 $
  */

public abstract class SRCPCommandException extends SRCPException {
    private static final long serialVersionUID = 6361481073223306967L;

    public SRCPCommandException (int number, String msg) {
        super(number,msg);
    }
    public SRCPCommandException (int number, String msg, Throwable cause) {
        super(number,msg, cause);
    }
}   
