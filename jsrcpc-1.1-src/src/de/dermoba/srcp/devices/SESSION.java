/*
 * Created on 31.10.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPIOException;

public class SESSION {
    private final SRCPSession session;
    private final int bus = 0;

    public SESSION(SRCPSession pSession) {
        session = pSession;
    }

    /** SRCP syntax: GET <bus> SESSION */
    public String get(int pSessionID) throws SRCPException {
        return session.getCommandChannel().send(
                "GET " + bus + " SESSION " + pSessionID);
    }

    /** SRCP syntax: TERM <bus> SESSION */
    public String term() throws SRCPException {
        String result = "";

        try {
            result = session.getCommandChannel().send(
                    "TERM " + bus + " SESSION");
        } catch (SRCPIOException e) {
            // We will not get a response from the server because it will
            // immediately close the connection.
        }
        return result;
    }

    /** SRCP syntax: TERM <bus> SESSION [<sessionid>] */
    public String term(int pSessionID) throws SRCPException {
        return session.getCommandChannel().send(
                "TERM " + bus + " SESSION " + pSessionID);
    }
}
