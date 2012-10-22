/*
 * Created on 31.10.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class POWER {
    private static final String POWER_ON = "ON";
    private static final String POWER_OFF = "OFF";

    private final SRCPSession session;
    private final int bus;

    public POWER(SRCPSession pSession, int bus) {
        this.session = pSession;
        this.bus = bus;
    }

    /** SRCP syntax: INIT &lt;bus&gt; POWER */
    public String init() throws SRCPException {
        if (!session.isOldProtocol()) {
            return session.getCommandChannel().send("INIT " + bus + " POWER");
        }
        return "";
    }

    /** SRCP syntax GET &lt;bus&gt; POWER &lt;addr&gt; */
    public boolean get() throws SRCPException {
        boolean result = false;
        String answer = null;

        if (session.isOldProtocol()) {
            answer = session.getCommandChannel().send("GET POWER");
        } else {
            answer = session.getCommandChannel().send("GET " + bus + " POWER");
        }
        if (answer != null) {
            String[] words = answer.split(" ");

            if (words.length >= 6) {
                result = words[5].equals(POWER_ON);
            }
        }
        return result;
    }

    /** SRCP syntax: SET &lt;bus&gt; POWER ON|OFF [&lt;freetext&gt;] */
    public String set(boolean on) throws SRCPException {
        return set(on, "");
    }

    /** SRCP syntax: SET &lt;bus&gt; POWER ON|OFF [&lt;freetext&gt;] */
    public String set(boolean on, String freetext) throws SRCPException {
        String power = "";
        if (on) {
            power = POWER_ON;
        } else {
            power = POWER_OFF;
        }
        if (session.isOldProtocol()) {
            return session.getCommandChannel().send("SET POWER " + power);
        }
        return session.getCommandChannel().send(
                "SET " + bus + " POWER " + power + " " + freetext);
    }

    /** SRCP syntax: TERM &lt;bus&gt; POWER */
    public String term() throws SRCPException {
        if (session.isOldProtocol()) {
            return "";
        }
        return session.getCommandChannel().send("TERM " + bus + " POWER");
    }
}
