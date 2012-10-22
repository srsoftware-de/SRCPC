/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.model.locomotives.SRCPLocomotiveDirection;

public class GL {

    private final SRCPSession session;
    private final int bus;
    private int address = 0;
    private String protocol = null;
    private String[] parameters = null;

    public GL(SRCPSession pSession, int bus) {
        this.session = pSession;
        this.bus = bus;
    }

    /**
     * SRCP syntax: INIT &lt;bus&gt; GL &lt;addr&gt; &lt;protocol&gt;
     * [&lt;parameter&gt;.. ]
     */
    public String init(int pAddress, String pProtocol) throws SRCPException {
        return init(pAddress, pProtocol, new String[0]);
    }

    /**
     * SRCP syntax: INIT &lt;bus&gt; GL &lt;addr&gt; &lt;protocol&gt;
     * [&lt;parameter&gt;.. ]
     */
    public String init(int pAddress, String pProtocol, String[] pParameters)
            throws SRCPException {
        address = pAddress;
        protocol = pProtocol;
        parameters = pParameters;
        StringBuffer paramBuf = new StringBuffer();
        for (int i = 0; i < parameters.length; i++) {
            paramBuf.append(parameters[i]);
            paramBuf.append(" ");
        }
        if (session.isOldProtocol()) {
            return "";
        }
        return session.getCommandChannel().send(
                "INIT " + bus + " GL " + address + " " + protocol + " "
                        + paramBuf.toString());
    }

    /**
     * SRCP syntax SET &lt;bus&gt; GL &lt;addr&gt; &lt;drivemode&gt; &lt;V&gt;
     * &lt;V_max&gt; &lt;f1&gt; .. &lt;fn&gt;
     */
    public String set(SRCPLocomotiveDirection drivemode, int v, int vmax, boolean[] f)
            throws SRCPException {
        StringBuffer functionBuf = new StringBuffer();
        if (f != null) {
            for (int i = 0; i < f.length; i++) {
                functionBuf.append(f[i] ? "1 " : "0 ");
            }
        }
        if (session.isOldProtocol()) {
            return session.getCommandChannel().send(
                    "SET GL " + protocol + " " + address + " "
                            + drivemode.getDirection() + " " + v + " " + vmax
                            + " " + functionBuf);
        }
        return session.getCommandChannel().send(
                "SET " + bus + " GL " + address + " "
                        + drivemode.getDirection() + " " + v + " " + vmax + " "
                        + functionBuf);
    }

    /** SRCP syntax GET &lt;bus&gt; GL &lt;addr&gt; */
    public GLData get() throws SRCPException {
        if (session.isOldProtocol()) {
            return new GLData(session.getCommandChannel().send(
                    "GET GL " + address));
        }
        return new GLData(session.getCommandChannel().send(
                "GET " + bus + " GL " + address));
    }

    /** SRCP syntax: TERM &lt;bus&gt; GL &lt;addr&gt; */
    public String term() throws SRCPException {
        if (session.isOldProtocol()) {
            return "";
        }
        return session.getCommandChannel().send(
                "TERM " + bus + " GL " + address);
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
