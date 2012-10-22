package de.dermoba.crcf.model;

import java.util.UUID;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.CRCF;

/**
 * @author Michael Oppenauer
 * 10.02.2009
 *
 */
public abstract class LOCO extends AbstractCRCFActor {

	public LOCO(SRCPSession session) {
		super(session);
	}

	@Override
	public void init(int sessionId) throws SRCPException {
		super.init(sessionId);
		crcfSession.get(sessionId, getActorName(), getUuid(), CRCFConstants.V_MAX);		
	}

	/**
	 * Handle following messages:
	 * GET V_MAX
	 * 
	 * @see de.dermoba.crcf.model.AbstractCRCFActor#CRCFget(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String)
	 */
	@Override
	public void CRCFget(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute) {
		super.CRCFget(timestamp, bus, sendTo, replyTo, actor, actor_id, attribute);
		//check if this messages is for me
		if ((actor_id.equals(getUuid()) | actor_id.equals(CRCF.BROADCAST)) 
			& (actor.equals(getActorName()))) {
			if (attribute.equals(CRCFConstants.V_MAX)) {
				try {
					crcfSession.info(sendTo, getActorName(), getUuid(),
							         CRCFConstants.V_MAX, getVmax().toString());
				} catch (SRCPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}
	}

	/**
	 * Handle following messages:
	 * INFO V_MAX: set the v_max if it is different
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFinfo(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String, java.lang.String)
	 */
	public void CRCFinfo(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute, String attribute_value) {
		super.CRCFinfo(timestamp, bus, sendTo, replyTo, actor, actor_id, attribute, attribute_value);
		//check if this messages is for me
		if (actor_id.equals(getUuid()) & actor.equals(getActorName())) {
			// New name for this object in the info message?
			if (attribute.equals(CRCFConstants.V_MAX) &
					!attribute_value.equals(this.getVmax())) {
				this.setVmax(Integer.parseInt(attribute_value));
			};
		}
	}

	abstract public Integer getVmax();

	abstract public void setVmax(int vmax);

	@Override
	protected String getActorName() {
		return CRCFConstants.LOCO;
	}
}
