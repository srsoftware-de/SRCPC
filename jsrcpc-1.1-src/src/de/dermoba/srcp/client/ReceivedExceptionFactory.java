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


    private final String EXCEPTIONS_FILE = "/srcp_exceptions.properties";

    private ReceivedExceptionFactory() throws SRCPIOException {
        try {
            URL url = this.getClass().getResource(EXCEPTIONS_FILE);
            load(url.openStream());
        } catch (IOException x) {
            throw new SRCPIOException(x);
        }
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
