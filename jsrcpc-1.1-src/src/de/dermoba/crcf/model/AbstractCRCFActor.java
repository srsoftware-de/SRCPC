package de.dermoba.crcf.model;

import java.util.UUID;

import de.dermoba.srcp.client.InfoChannel;
import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.CRCF;
import de.dermoba.srcp.devices.listener.CRCFInfoListener;

/**
 * @author Michael Oppenauer
 * 10.02.2009
 *
 */
public abstract class AbstractCRCFActor implements CRCFInfoListener{

	protected CRCF crcfSession = null; 
	protected InfoChannel infoChannel = null;
	
	public AbstractCRCFActor(SRCPSession session) {
		super();
		if (session != null) {
			crcfSession = new CRCF(session);
			infoChannel = session.getInfoChannel();
			infoChannel.addCRCFInfoListener(this);
		}
	}

	/**
	 * @return name of the actor, should be from CRCFConstants
	 */
	protected abstract String getActorName();
	
	abstract public UUID getUuid();

	abstract public String getName();

	abstract public void setName(String name);
	
	/**
	 * Sends CRCF GET Messages to initialize the Object.
	 * Deviated classes should add here other requests,
	 * for initialize their attributes.
	 * 
	 * @param sessionId
	 * @throws SRCPException
	 */
	public void init(int sessionId) throws SRCPException {
		crcfSession.get(sessionId, getActorName(), getUuid(), CRCFConstants.NAME);
	}
	
	/**
	 * Handle following messages:
	 * GET ID
	 * GET NAME
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFget(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String)
	 */
	public void CRCFget(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute) {
		//check if this messages is for me
		if ((actor_id.equals(getUuid()) | actor_id.equals(CRCF.BROADCAST)) 
				& (actor.equals(getActorName()))) {
			if (attribute.equals(CRCFConstants.ID)) {
				this.sendId(replyTo);
			};
			if (attribute.equals(CRCFConstants.NAME)) {
				this.sendName(replyTo);
			};
		}
	}

	/**
	 * Handle following messages:
	 * INFO NAME: set the name if it is different
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFinfo(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String, java.lang.String)
	 */
	public void CRCFinfo(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute, String attribute_value) {
		//check if this messages is for me
		if (actor_id.equals(getUuid()) & actor.equals(getActorName())) {
			// New name for this object in the info message?
			if (attribute.equals(CRCFConstants.NAME) &
					!attribute_value.equals(this.getName())) {
				this.setName(attribute_value);
			};
		}
	}

	/**
	 * Does nothing.
	 * Other classes may override this method,
	 * if they have to react on incoming LIST messages.
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFlist(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String)
	 */
	public void CRCFlist(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute) {
	}

	/**
	 * Does nothing.
	 * Other classes may override this method,
	 * if they have to react on incoming SET messages.
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFset(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String, java.lang.String)
	 */
	public void CRCFset(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute, String attribute_value) {
	}
	

	/**
	 * Sends an CRCF Message with the ID of this Object to another INFO Channel.
	 * 
	 * @param sendTo Number of the info channel where to send.
	 */
	public void sendId(int sendTo) {
		try {
			crcfSession.info(sendTo, getActorName(), getUuid(),
					         CRCFConstants.ID, getUuid().toString());
		} catch (SRCPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sends an CRCF Message with the Name of this Object to another INFO Channel.
	 * 
	 * @param sendTo Number of the info channel where to send.
	 */
	public void sendName(int sendTo) {
		try {
			crcfSession.info(sendTo, getActorName(), getUuid(),
					         CRCFConstants.NAME, getName());
		} catch (SRCPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		infoChannel.removeCRCFInfoListener(this);
		super.finalize();
	}

}