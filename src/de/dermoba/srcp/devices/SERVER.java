/*
 * Created on 31.10.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class SERVER {
    private final SRCPSession session;
    private final int bus = 0;

    public SERVER(SRCPSession pSession) {
        session = pSession;
    }

    /** SRCP syntax: RESET <bus> SERVER */
    public String reset() throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel()
                    .send("RESET " + bus + " SERVER ");
        }
        return "";
    }

    /** SRCP syntax: TERM <bus> SERVER */
    public String term() throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send("TERM " + bus + " SERVER ");
        }
        return "";
    }
}
