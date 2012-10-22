package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

/**
 * SRCP: service mode commands
 * 
 * @author Andr&eacute; Schenk
 * @version $Revision: 1.2 $
 */
public class SM {

    private final SRCPSession session;
    private final int bus;

    public SM(SRCPSession pSession, int bus) {
        this.session = pSession;
        this.bus = bus;
    }

    /**
     * SRCP syntax: INIT &lt;bus&gt; SM &lt;protocol&gt;
     */
    public String init(String pProtocol) throws SRCPException {
        return session.getCommandChannel().send(
                "INIT " + bus + " SM " + pProtocol);
    }

    /**
     * SRCP syntax GET &lt;bus&gt; SM &lt;decoderaddress&gt; &lt;type&gt; &lt;1
     * or more values&gt;
     */
    public String get(int address, String type, String values)
            throws SRCPException {
        return session.getCommandChannel().send(
                "GET " + bus + " SM " + address + " " + type + " " + values);
    }

    /**
     * SRCP syntax: SET &lt;bus&gt; SM &lt;decoderaddress&gt; &lt;type&gt;
     * &lt;1or more values&gt;
     */
    public String set(int address, String type, String values)
            throws SRCPException {
        return session.getCommandChannel().send(
                "SET " + bus + " SM " + address + " " + type + " " + values);
    }

    /**
     * SRCP syntax: TERM &lt;bus&gt; SM
     */
    public String term() throws SRCPException {
        return session.getCommandChannel().send("TERM " + bus + " SM");
    }

    /**
     * SRCP syntax: VERIFY &lt;bus&gt; SM &lt;decoderaddress&gt; &lt;type&gt;
     * &lt;1 or more values&gt;
     */
    public String verify(int address, String type, String values)
            throws SRCPException {
        return session.getCommandChannel().send(
                "VERIFY " + bus + " SM " + address + " " + type + " " + values);
    }
}
