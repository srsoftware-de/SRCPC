/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

import de.dermoba.srcp.common.SocketReader;
import de.dermoba.srcp.common.SocketWriter;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPHostNotFoundException;
import de.dermoba.srcp.common.exception.SRCPIOException;

public class CommandChannel {

    private int id;

    String serverName = null;

    int serverPort;

    private Socket socket = null;

    private SocketWriter out = null;

    private SocketReader in = null;

    private Set<CommandDataListener> listeners;

    /**
     * creates a new SRCP connection on the command channel to handle all
     * command communication.
     * 
     * @param pServerName
     *            name or IP-Address of server
     * @param pServerPort
     *            TCP port number
     * @throws SRCPException
     */
    public CommandChannel(String pServerName, int pServerPort)
        throws SRCPException {
        serverName = pServerName;
        serverPort = pServerPort;
        listeners = new HashSet<CommandDataListener>();
    }

    public void connect() throws SRCPException {
        try {
            socket = new Socket(serverName, serverPort);
            out = new SocketWriter(socket);
            in = new SocketReader(socket);
            String incoming = in.read();
            informListenersReceived(incoming);
        }
        catch (UnknownHostException e) {
            throw new SRCPHostNotFoundException();
        }
        catch (IOException e) {
            throw new SRCPIOException(e);
        }
        send("SET CONNECTIONMODE SRCP COMMAND");
        String output = sendReceive("GO");
        String[] outputSplitted = output.split(" ");

        if (outputSplitted.length >= 5) {
            id = Integer.parseInt(outputSplitted[4]);
        }
    }

    public void disconnect() throws SRCPException {
        try {
            // sendReceive("SESSION 0 TERM");
            if (socket != null) {
                socket.close();
                socket = null;
            }
        }
        catch (IOException e) {
            throw new SRCPIOException(e);
        }
    }

    public String sendReceive(String output) throws SRCPException {
    	System.out.println("sendRecieve("+output+")");
        if (out == null) {
            throw new SRCPIOException();
        }
        String s = "";
        try {
            if (output != null) {
                informListenersSent(output);
                output += "\n";
                out.write(output);
            }
            s = in.read();
            if (s == null) throw new SRCPIOException();
        }
        catch (IOException e) {
            throw new SRCPIOException();
        }
        informListenersReceived(s);
        return s;
    }

    /**
     * send a command to the server
     * 
     * @param pCommand
     *            the command to send
     * @return the ervers reply
     * @throws SRCPException
     */
    public String send(String pCommand) throws SRCPException {
        String response = sendReceive(pCommand);
        SRCPException ex =
            ReceivedExceptionFactory.parseResponse(pCommand, response);
        if (ex != null) {
            throw ex;
        }
        return response;
    }

    private void informListenersReceived(String s) {
        for (CommandDataListener l : listeners) {
            l.commandDataReceived(s);
        }
    }

    private void informListenersSent(String s) {
        for (CommandDataListener l : listeners) {
            l.commandDataSent(s);
        }
    }

    public void addCommandDataListener(CommandDataListener l) {
        listeners.add(l);
    }

    public void removeCommandDataListener(CommandDataListener l) {
        listeners.remove(l);
    }

    public int getID() {
        return id;
    }
}
