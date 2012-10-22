package de.dermoba.srcp.devices;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPWrongValueException;

/**
 * Class for sending CRCF Messages.
 * 
 * @author Michael Oppenauer
 * 03.02.2009
 *
 */
public class CRCF {

	public static final UUID BROADCAST = UUID.fromString("00000000-0000-0000-0000-000000000000");
	
    private int myInfoChannel;
    private GM gmSession;

    public CRCF(SRCPSession pSession){
    	gmSession = new GM(pSession);
        myInfoChannel = pSession.getInfoChannelID();
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; &lt;messageType&gt; &lt;attribute&gt; &lt;attribute_value&gt;*/
    private String message(int sendTo, int replyTo, String actor, UUID actor_id, String messageType, String attribute, String attribute_value) throws SRCPException {
    	String[] message;
        if (attribute_value.equals("")) {
        	message = new String[5];
        } else {
        	message = new String[6];
        }
        try {
        	message[0] = "CRCF";
        	message[1] = actor;
			message[2] = URLEncoder.encode(actor_id.toString(), "UTF-8");
	        message[3] = messageType;
	        message[4] = attribute;
	        if (!attribute_value.equals("")) {
	            message[5] = attribute_value;
	        }
		} catch (UnsupportedEncodingException e) {
			System.out.println("Can't encode UUID.");
			throw new SRCPWrongValueException(e);
		}
		return gmSession.set(sendTo, replyTo, message); 
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; SET &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String set(int sendTo, int replyTo, String actor, UUID actor_id, String attribute, String attribute_value) throws SRCPException {
		return message(sendTo, replyTo, actor, actor_id, "SET", attribute, attribute_value); 
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; SET &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String set(int sendTo, String actor, UUID actor_id, String attribute, String attribute_value) throws SRCPException {
    	return set(sendTo, this.myInfoChannel, actor, actor_id, attribute, attribute_value);
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; SET &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String set(int sendTo, int replyTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return set(sendTo, replyTo, actor, actor_id, attribute, "");
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; SET &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String set(int sendTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return set(sendTo, this.myInfoChannel, actor, actor_id, attribute, "");
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; GET &lt;attribute&gt; */
    public String get(int sendTo, int replyTo, String actor, UUID actor_id, String attribute) throws SRCPException {
		return message(sendTo, replyTo, actor, actor_id, "GET", attribute, ""); 
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; GET &lt;attribute&gt; */
    public String get(int sendTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return get(sendTo, this.myInfoChannel, actor, actor_id, attribute);
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; INFO &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String info(int sendTo, int replyTo, String actor, UUID actor_id, String attribute, String attribute_value) throws SRCPException {
		return message(sendTo, replyTo, actor, actor_id, "INFO", attribute, attribute_value); 
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; INFO &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String info(int sendTo, String actor, UUID actor_id, String attribute, String attribute_value) throws SRCPException {
    	return info(sendTo, this.myInfoChannel, actor, actor_id, attribute, attribute_value);
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; INFO &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String info(int sendTo, int replyTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return info(sendTo, replyTo, actor, actor_id, attribute, "");
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; INFO &lt;attribute&gt; &lt;attribute_value&gt;*/
    public String info(int sendTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return info(sendTo, this.myInfoChannel, actor, actor_id, attribute, "");
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; LIST &lt;attribute&gt; */
    public String list(int sendTo, int replyTo, String actor, UUID actor_id, String attribute) throws SRCPException {
		return message(sendTo, replyTo, actor, actor_id, "LIST", attribute, ""); 
    }

    /** CRCF syntax &lt;actor&gt; &lt;actor_id&gt; LIST &lt;attribute&gt; */
    public String list(int sendTo, String actor, UUID actor_id, String attribute) throws SRCPException {
    	return list(sendTo, this.myInfoChannel, actor, actor_id, attribute);
    }

}
