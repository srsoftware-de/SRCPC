/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.client;

import de.dermoba.srcp.common.exception.SRCPException;

public class SRCPSession {

    private String serverName = null;
    private int serverPort = 0;
    private boolean oldProtocol;
    private CommandChannel commandChannel = null;
    private InfoChannel infoChannel = null;
    private ReceivedExceptionFactory exceptionHandler;

    /**
     * creates a new SRCP session by connecting to serverName with port serverPort
     * using one command session and one info session.
     * 
     * @param pServerName
     * @param pServerPort
     */
    public SRCPSession(String pServerName, int pServerPort) throws SRCPException {
        this(pServerName, pServerPort, false);
    }

    /**
     * creates a new SRCP session by connecting to serverName with port serverPort
     * using one command session and one info session.
     * 
     * @param pServerName
     * @param pServerPort
     * @param pOldProtocol
     */
    public SRCPSession(String pServerName, int pServerPort, boolean pOldProtocol) 
        throws SRCPException {
        serverName = pServerName;
        serverPort = pServerPort;
        oldProtocol = pOldProtocol;
        infoChannel = new InfoChannel(serverName, serverPort);
        try {
            Thread.sleep(1000);
        } catch (Exception x ) {
        	// no interrupt expected :-)
        }
        commandChannel = new CommandChannel(serverName, serverPort);
    }
    
    public void connect() throws SRCPException {
    	infoChannel.connect();
    	commandChannel.connect();
    }
    
    public void disconnect() throws SRCPException {
    	commandChannel.disconnect();
    	infoChannel.disconnect();
    }

    public CommandChannel getCommandChannel() {
        return commandChannel;
    }

    public InfoChannel getInfoChannel() {
        return infoChannel;
    }

    public String getServerName() {
        return serverName;
    }

    public int getServerPort() {
        return serverPort;
    }

    public ReceivedExceptionFactory getExceptionHandler() {
        return exceptionHandler;
    }

    public boolean isOldProtocol() {
        return oldProtocol;
    }
    
    public int getCommandChannelID() {
        return getCommandChannel().getID();
    }
    
    public int getInfoChannelID() {
        return getInfoChannel().getID();
    }
}
