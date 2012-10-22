
package de.dermoba.srcp.devices.listener;

import de.dermoba.srcp.common.TokenizedLine;
import de.dermoba.srcp.common.exception.SRCPUnsufficientDataException;
import de.dermoba.srcp.common.exception.SRCPWrongValueException;

/**
 * Interface for Listeners of GM Messages.
 * 
 * @author Michael Oppenauer
 * 03.02.2009
 *
 */
public interface GMInfoListener {

	/**
	 * Is called if an GM message is received and the listener is
	 * registered at the InfoChannel.
	 * 
	 * @param timestamp
	 * @param bus
	 * @param sendTo
	 * @param replyTo
	 * @param messageType
	 * @param tokenLine
	 * @throws SRCPUnsufficientDataException
	 * @throws NumberFormatException
	 * @throws SRCPWrongValueException
	 */
	public void GMset(double timestamp, int bus, int sendTo, int replyTo,
			String messageType, TokenizedLine tokenLine) throws SRCPUnsufficientDataException, NumberFormatException, SRCPWrongValueException;
}
