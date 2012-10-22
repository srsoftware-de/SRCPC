/*
 * Created on 01.07.2005
 *
 */
package de.dermoba.srcp.common;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import de.dermoba.srcp.common.exception.SRCPUnsufficientDataException;
import de.dermoba.srcp.common.exception.SRCPWrongValueException;

/**
 * create an object containing a complete line and pass tokens taken
 * from this line.
 * tokens will be decoded as URL in RFC 2396. 
 *
 * @author kurt
 * @author Michael Oppenauer
 *
 */
public class TokenizedLine {

    private ArrayList<String> tokens = null;
    private int tokenPosition = 0;

/**
 * create a new tokenizer for a complete String.
 *
 * @param in	String containing white space
 */
    public TokenizedLine(String in) {
        tokens = new ArrayList<String>();
        String[] t = in.split("\\s");
        for (int i = 0; i < t.length; i++) {
            if (t[i].length() > 0) {
                tokens.add(t[i]);
            }
        }
    }

    public String nextStringToken() throws SRCPUnsufficientDataException {
        if (tokenPosition >= tokens.size()) {
            throw new SRCPUnsufficientDataException();
        }
        return tokens.get(tokenPosition++).trim();
    }

    public String nextURLStringToken() throws SRCPUnsufficientDataException, SRCPWrongValueException {
        try {
			return URLDecoder.decode(nextStringToken(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new SRCPWrongValueException(e);
		}
    }
    
    public double nextDoubleToken() throws SRCPUnsufficientDataException, NumberFormatException  {
        return Double.parseDouble(nextStringToken());
    }
    public int nextIntToken() throws SRCPUnsufficientDataException, NumberFormatException  {
        return Integer.parseInt(nextStringToken());
    }

    public int nextIntToken(int min) throws SRCPUnsufficientDataException, NumberFormatException, SRCPWrongValueException {
        return nextIntToken(min, Integer.MAX_VALUE);
    }

    public int nextIntToken(int min, int max) throws SRCPUnsufficientDataException, NumberFormatException, SRCPWrongValueException {
        int h = nextIntToken();
        if (h < min || h > max) {
            throw new SRCPWrongValueException();
        }
        return h;
    }

    public boolean hasMoreElements() {
        return tokenPosition < tokens.size();
    }

    public String toString() {
        return tokens.toString();
    }
}
