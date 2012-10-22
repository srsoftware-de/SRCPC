package de.dermoba.srcp.client;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import de.dermoba.srcp.common.Response;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPIOException;

/**
 * This class translates incoming SRCP protocol reply strings into instances of
 * appropriate SRCPException objects.
 *
 * @author kurt
 *
 */
public class ReceivedExceptionFactory extends Properties {

    private static final long serialVersionUID = 4995179873064071652L;

    private static ReceivedExceptionFactory instance = null;


    private final String EXCEPTIONS_FILE = "srcp_exceptions.properties";

    private ReceivedExceptionFactory() throws SRCPIOException {
    	
    	setProperty("400", "de.dermoba.srcp.common.exception.SRCPUnsupportedProtocolException");
    	setProperty("401", "de.dermoba.srcp.common.exception.SRCPUnsupportedConnectionModeException");
    	setProperty("402", "de.dermoba.srcp.common.exception.SRCPUnsufficientDataException");
    	setProperty("410", "de.dermoba.srcp.common.exception.SRCPUnknownCommandException");
    	setProperty("411", "de.dermoba.srcp.common.exception.SRCPUnknownValueException");
    	setProperty("412", "de.dermoba.srcp.common.exception.SRCPWrongValueException");
    	setProperty("413", "de.dermoba.srcp.common.exception.SRCPTemporarlyProhibitedException");
    	setProperty("414", "de.dermoba.srcp.common.exception.SRCPDeviceLockedException");
    	setProperty("415", "de.dermoba.srcp.common.exception.SRCPForbiddenException");
    	setProperty("416", "de.dermoba.srcp.common.exception.SRCPNoDataException");
    	setProperty("417", "de.dermoba.srcp.common.exception.SRCPTimeoutException");
    	setProperty("418", "de.dermoba.srcp.common.exception.SRCPListTooLongException");
    	setProperty("419", "de.dermoba.srcp.common.exception.SRCPListTooShortException");
    	setProperty("420", "de.dermoba.srcp.common.exception.SRCPUnsupportedDeviceProtocolException");
    	setProperty("421", "de.dermoba.srcp.common.exception.SRCPUnsupportedDeviceException");
    	setProperty("422", "de.dermoba.srcp.common.exception.SRCPUnsupportedDeviceGroupException");
    	setProperty("423", "de.dermoba.srcp.common.exception.SRCPUnsupportedOperationException");
    	setProperty("424", "de.dermoba.srcp.common.exception.SRCPDeviceReinitializedException");
    	setProperty("425", "de.dermoba.srcp.common.exception.SRCPNotSupportedException");
    	setProperty("499", "de.dermoba.srcp.common.exception.SRCPUnspecifiedErrorException");
    	setProperty("500", "de.dermoba.srcp.common.exception.SRCPOutOfResourcesException");
    	setProperty("601", "de.dermoba.srcp.common.exception.SRCPInternalErrorException");
    	setProperty("602", "de.dermoba.srcp.common.exception.SRCPHostNotFoundException");
    	setProperty("603", "de.dermoba.srcp.common.exception.SRCPIOException");
    }

    /**
	 * create a SRCPException object from a protocol reply
	 *
	 * @param request	The String sent to the server
	 * @param response	The String received from the server
	 * @return			an SRCPException object corresponding to the error number
	 * @throws SRCPException
	 * @throws NumberFormatException
	 */
    public static SRCPException parseResponse(String request, String response)
        throws SRCPException, NumberFormatException {
        if (instance == null) {
            instance = new ReceivedExceptionFactory();
        }
        if (instance == null) {
        	return null;
        }
        Response resp = new Response (response);
        SRCPException ex = null;
        try {
            if(resp.getCode() >= 400) {
                try {
                    ex = (SRCPException)
                    	(Class.forName
                         (instance.get(Integer.toString(resp.getCode()))
                          .toString()).newInstance());
                    ex.setRequestString(request);
                } catch (ClassNotFoundException x) {
                    throw new SRCPIOException();
                } catch (InstantiationException x) {
                    throw new SRCPIOException();
                } catch (IllegalAccessException x) {
                    throw new SRCPIOException();
                }
            }
        } catch (NumberFormatException x) {
            // null will be returned anyhow
        }
        return ex;
    }
}
