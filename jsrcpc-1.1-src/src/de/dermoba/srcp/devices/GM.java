package de.dermoba.srcp.devices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPNotSupportedException;
import de.dermoba.srcp.common.exception.SRCPWrongValueException;

/**
 * Class for sending GM.
 * 
 * @author Michael Oppenauer
 * 03.02.2009
 *
 */
public class GM {

	public static final int BROADCAST = 0;
	
    private final SRCPSession session;
    private final int bus = 0;
    private int myInfoChannel;

    public GM(SRCPSession pSession){
        session = pSession;
        myInfoChannel = session.getInfoChannelID();
    }

    /** SRCP syntax SET &lt;bus&gt; GM &lt;sendTo&gt; &lt;replyTo&gt; &lt;message&gt; */
    public String set(int sendTo, int replyTo, String message) throws SRCPException {
    	if(session.isOldProtocol()) {
            throw new SRCPNotSupportedException();
        }
        try {
			return session.getCommandChannel().send(
			        "SET " + bus + " GM " + sendTo + " " 
			        + replyTo + " " + URLEncoder.encode(message, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new SRCPWrongValueException(e);
		}
    }

    /** SRCP syntax SET &lt;bus&gt; GM &lt;sendTo&gt; &lt;replyTo&gt; &lt;message&gt; */
    public String set(int sendTo, int replyTo, String[] message) throws SRCPException {
    	if(session.isOldProtocol()) {
            throw new SRCPNotSupportedException();
        }
        try {
            StringBuffer messageBuf = new StringBuffer();
            for(int i = 0; i < message.length; i++) {
                messageBuf.append(URLEncoder.encode(message[i], "UTF-8"));
                messageBuf.append(" ");
            }
			return session.getCommandChannel().send(
			        "SET " + bus + " GM " + sendTo + " " 
			        + replyTo + " " + messageBuf.toString());
		} catch (UnsupportedEncodingException e) {
			throw new SRCPWrongValueException(e);
		}
    }

    /** SRCP syntax SET &lt;bus&gt; GM &lt;sendTo&gt; &lt;replyTo&gt; &lt;message&gt; */
    public String set(int sendTo, String message) throws SRCPException {
    	return set(sendTo, this.myInfoChannel, message);
    }

    /** SRCP syntax SET &lt;bus&gt; GM &lt;sendTo&gt; &lt;replyTo&gt; &lt;message&gt; */
    public String set(int sendTo, String[] message) throws SRCPException {
    	return set(sendTo, this.myInfoChannel, message);
    }

}
