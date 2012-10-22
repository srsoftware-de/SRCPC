/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class FB {

    private final SRCPSession session;
    private final int bus;

    public FB(SRCPSession pSession, int bus) {
        this.session = pSession;
        this.bus = bus;
    }

    /**
     * SRCP syntax: INIT &lt;bus&gt; FB &lt;addr&gt; &lt;device protocol&gt;
     * [&lt;parameter&gt;.. ]
     */
    public String init(int pAddress, String pProtocol, String[] pParameters)
            throws SRCPException {
        StringBuffer paramBuf = new StringBuffer();

        if (pParameters != null) {
            for (int i = 0; i < pParameters.length; i++) {
                paramBuf.append(pParameters[i]);
                paramBuf.append(" ");
            }
        }
        return session.getCommandChannel().send(
                "INIT " + bus + " FB " + pAddress + " " + pProtocol + " "
                        + paramBuf);
    }

    /** SRCP syntax GET &lt;bus&gt; FB &lt;addr&gt; */
    public String get(int address) throws SRCPException {
        return session.getCommandChannel()
                .send("GET " + bus + " FB " + address);
    }

    /** SRCP syntax: SET &lt;bus&gt; FB &lt;addr&gt; &lt;value&gt; */
    public String set(int address, int value) throws SRCPException {
        return session.getCommandChannel().send(
                "SET " + bus + " FB " + address + " " + value);
    }

    /** SRCP syntax: TERM &lt;bus&gt; FB */
    public String term() throws SRCPException {
        return session.getCommandChannel().send("TERM " + bus + " FB ");
    }

    /**
     * SRCP syntax: WAIT &lt;bus&gt; FB &lt;addr&gt; &lt;value&gt;
     * &lt;timeout&gt;
     */
    public String wait(int address, int value, int timeout)
            throws SRCPException {
        return session.getCommandChannel().send(
                "WAIT " + bus + " FB " + address + " " + value + " " + timeout);
    }
}
