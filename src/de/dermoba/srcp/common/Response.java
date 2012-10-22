/**
 * 
 */
package de.dermoba.srcp.common;

import de.dermoba.srcp.common.exception.SRCPUnsufficientDataException;

/**
 * This class bundles the data received from an SRCP server as response
 * to a command. 
 * 
 * @author mnl
 */
public class Response {

	private double timestamp;
	private int code;
	private String detail;
	
	/**
	 * Creates a new Response using the string returned by the server.
	 * 
	 * @param responseString the string returned by the server
	 * @throws SRCPUnsufficientDataException 
	 * @throws NumberFormatException 
	 */
	public Response(String responseString) 
		throws SRCPUnsufficientDataException {
        TokenizedLine line = new TokenizedLine(responseString);
        try {
        	timestamp = line.nextDoubleToken();
        	code = line.nextIntToken();
        	StringBuffer detailTokens = new StringBuffer();
        	while (line.hasMoreElements()) {
        		if (detailTokens.length() > 0) {
        			detailTokens.append(' ');
        		}
        		detailTokens.append(line.nextStringToken());
        	}
        } catch (NumberFormatException e) {
			throw new SRCPUnsufficientDataException(e);
		}
	}

	/**
	 * @return the timestamp
	 */
	public double getTimestamp() {
		return timestamp;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the detail
	 */
	public String getDetail() {
		return detail;
	}
}
