/*
 * Created on 31.10.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class LOCK {
    private final SRCPSession session;
    private final int bus;

    public LOCK(SRCPSession pSession, int pBus) {
        session = pSession;
        bus = pBus;
    }

    /** SRCP syntax GET &lt;bus&gt; LOCK &lt;devicegroup&gt; &lt;addr&gt; */
    public String get(String pDevicegroup, int pAddr) throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "GET " + bus + " LOCK " + " " + pDevicegroup + " " + pAddr);
        }
        return "";
    }

    /**
     * SRCP syntax: SET &lt;bus&gt; LOCK &lt;device group&gt; &lt;addr&gt;
     * &lt;duration&gt;
     */
    public String set(String pDevicegroup, int pAddr, int pDuration)
            throws SRCPException {

        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "SET " + bus + " LOCK " + pDevicegroup + " " + pAddr + " "
                            + pDuration);
        }
        return "";
    }

    /** SRCP syntax: TERM &lt;bus&gt; LOCK &lt;device group&gt; &lt;addr&gt; */
    public String term(String pDevicegroup, int pAddr) throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "TERM " + bus + " LOCK " + pDevicegroup + " " + pAddr);
        }
        return "";
    }
}
