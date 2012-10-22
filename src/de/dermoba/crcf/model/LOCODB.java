package de.dermoba.crcf.model;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.CRCF;

/**
 * Abstract Class which represents Loco Database
 * 
 * @author Michael Oppenauer
 * 09.02.2009
 *
 */
public abstract class LOCODB extends AbstractCRCFActor {
	
	public LOCODB(SRCPSession session) {
		super(session);
	}

	@Override
	protected String getActorName() {
		return CRCFConstants.LOCODB;
	}

	/**
	 * @return a list with all LOCOs in this database
	 */
	abstract protected List<LOCO> getLOCOList();
	
	/**
	 * Handle following messages:
	 * LIST LOCO
	 * 
	 * @see de.dermoba.srcp.devices.listener.CRCFInfoListener#CRCFlist(double, int, int, int, java.lang.String, java.util.UUID, java.lang.String)
	 */
	@Override
	public void CRCFlist(double timestamp, int bus, int sendTo, int replyTo,
			String actor, UUID actor_id, String attribute) {
		//First call super method
		super.CRCFlist(timestamp, bus, sendTo, replyTo, actor, actor_id, attribute);
		
		if ((actor_id.equals(getUuid()) | actor_id.equals(CRCF.BROADCAST)) 
				& (actor.equals(getActorName()))) {
			if (attribute.equals(CRCFConstants.LOCO)) {
				try {
					//Send LOCOCOUNT
					crcfSession.info(replyTo, getActorName(), getUuid(),
							 CRCFConstants.LOCO+CRCFConstants.COUNT, Integer.toString(getLOCOList().size()));
					Iterator<LOCO> iter = getLOCOList().iterator();
					while (iter.hasNext()) {
						//Send all LOCO IDs
						crcfSession.info(replyTo, getActorName(), getUuid(),
								 CRCFConstants.LOCO, iter.next().getUuid().toString());
					}
				} catch (SRCPException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			};
		}
	}
}
