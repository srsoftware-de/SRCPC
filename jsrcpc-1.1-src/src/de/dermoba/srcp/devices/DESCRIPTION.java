/*
 * Created on 31.10.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class DESCRIPTION {
    private final SRCPSession session;
    private final int bus;

    public DESCRIPTION(SRCPSession pSession, int bus) {
        this.session = pSession;
        this.bus = bus;
    }

    /** SRCP syntax: GET &lt;bus&gt; DESCRIPTION */
    public String get() throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "GET " + bus + " DESCRIPTION ");
        }
        return "";
    }

    /**
     * SRCP syntax: GET &lt;bus&gt; DESCRIPTION &lt;devicegroup&gt;
     * [&lt;address&gt;]
     */
    public String get(String pDevicegroup) throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "GET " + bus + " DESCRIPTION " + pDevicegroup);
        }
        return "";
    }

    /**
     * SRCP syntax: GET &lt;bus&gt; DESCRIPTION &lt;devicegroup&gt;
     * [&lt;address&gt;]
     */
    public String get(String pDevicegroup, int pAddress) throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "GET " + bus + " DESCRIPTION " + pDevicegroup + " "
                            + pAddress);
        }
        return "";
    }
}
