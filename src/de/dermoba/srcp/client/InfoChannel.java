/*
 * Created on 26.09.2005
 *
 */

package de.dermoba.srcp.client;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import de.dermoba.srcp.common.SocketReader;
import de.dermoba.srcp.common.SocketWriter;
import de.dermoba.srcp.common.TokenizedLine;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.common.exception.SRCPHostNotFoundException;
import de.dermoba.srcp.common.exception.SRCPIOException;
import de.dermoba.srcp.common.exception.SRCPUnsufficientDataException;
import de.dermoba.srcp.common.exception.SRCPWrongValueException;
import de.dermoba.srcp.devices.listener.CRCFInfoListener;
import de.dermoba.srcp.devices.listener.FBInfoListener;
import de.dermoba.srcp.devices.listener.GAInfoListener;
import de.dermoba.srcp.devices.listener.GLInfoListener;
import de.dermoba.srcp.devices.listener.GMInfoListener;
import de.dermoba.srcp.devices.listener.LOCKInfoListener;
import de.dermoba.srcp.devices.listener.POWERInfoListener;
import de.dermoba.srcp.devices.listener.SERVERInfoListener;
import de.dermoba.srcp.devices.listener.SMInfoListener;
import de.dermoba.srcp.model.locomotives.SRCPLocomotiveDirection;

public class InfoChannel implements Runnable {

    private static final int INFO_SET = 100;

    private static final int INFO_INIT = 101;

    private static final int INFO_TERM = 102;

    private Socket socket = null;

    private SocketWriter out = null;

    private SocketReader in = null;

    private final String serverName;

    private final int serverPort;

    private int id;

    private Collection<FBInfoListener> FBListeners = new ArrayList<FBInfoListener>();

    private Collection<GAInfoListener> GAListeners = new ArrayList<GAInfoListener>();

    private Collection<GLInfoListener> GLListeners = new ArrayList<GLInfoListener>();

    private Collection<LOCKInfoListener> LOCKListeners = new ArrayList<LOCKInfoListener>();

    private Collection<POWERInfoListener> POWERListeners = new ArrayList<POWERInfoListener>();

    private Collection<SERVERInfoListener> SERVERListeners = new ArrayList<SERVERInfoListener>();

    private Collection<SMInfoListener> SMListeners = new ArrayList<SMInfoListener>();

    private Collection<GMInfoListener> GMListeners = new ArrayList<GMInfoListener>();

    private Collection<CRCFInfoListener> CRCFListeners = new ArrayList<CRCFInfoListener>();

    // private List<DESCRIPTIONInfoListener> DESCRIPTIONListeners;
    // private List<SESSIONInfoListener> SESSIONListeners;
    private Collection<InfoDataListener> listeners = new ArrayList<InfoDataListener>();

    private Thread infoThread;

    private CRCFHandler CRCFHandle = new CRCFHandler();

    /**
     * creates a new SRCP connection on the info channel to handle all info
     * communication.
     * 
     * @param pServerName
     *            server name or IP address
     * @param pServerPort
     *            TCP port number
     * @throws SRCPException
     */
    public InfoChannel(String pServerName, int pServerPort) {
        serverName = pServerName;
        serverPort = pServerPort;
        GMListeners.add(CRCFHandle);
    }

    public void connect() throws SRCPException {
        try {
            socket = new Socket(serverName, serverPort);
            out = new SocketWriter(socket);
            in = new SocketReader(socket);

            // Application protocol layer initialization
            String s = in.read(); // Ignore welcome message
            send("SET CONNECTIONMODE SRCP INFO");
            s = in.read();
            send("GO");
            s = in.read();
            try {
                String[] sSplitted = s.split(" ");

                if (sSplitted.length >= 5) {
                    id = Integer.parseInt(sSplitted[4]);
                }
            } catch (NumberFormatException e) {
                System.err.println(s + ": cannot convert the 5. token from \""
                        + s + "\" into an integer");
            }

            // Now receive and handle messages continuously.
            infoThread = new Thread(this);
            infoThread.setDaemon(true);
            infoThread.start();
        } catch (UnknownHostException e) {
            throw new SRCPHostNotFoundException();
        } catch (IOException e) {
        	throw new SRCPIOException(e.getCause());
				}
    }

    public void disconnect() throws SRCPException {
        try {
            GMListeners.remove(CRCFHandle);
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            throw new SRCPIOException(e);
        }
    }

    private void send(String s) throws IOException {
        informListenersSent(s);
        out.write(s + "\n");
    }

    public void run() {
        try {
            while (true) {
                String s = in.read();
                if (s == null)
                    break;
                informListenersReceived(s);
            }
        } catch (SocketException e) {
            return;
        } catch (IOException e) {
            // what to do, if IOException on info channel?
        }
    }

    public void addInfoDataListener(InfoDataListener listener) {
        listeners.add(listener);
    }

    public void removeInfoDataListener(InfoDataListener listener) {
        listeners.remove(listener);
    }

    private void informListenersReceived(String s) {
        try {
            TokenizedLine tokenLine = new TokenizedLine(s);

            if (tokenLine.hasMoreElements()) {
                double timestamp = tokenLine.nextDoubleToken();
                int number = tokenLine.nextIntToken();

                if (number < 200) {
                    tokenLine.nextStringToken();
                    int bus = tokenLine.nextIntToken();
                    String deviceGroup = tokenLine.nextStringToken()
                            .toUpperCase();
                    if (deviceGroup.equals("FB")) {
                        handleFB(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("GA")) {
                        handleGA(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("GL")) {
                        handleGL(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("LOCK")) {
                        handleLOCK(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("POWER")) {
                        handlePOWER(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("SERVER")) {
                        handleSERVER(tokenLine, timestamp, number);
                    } else if (deviceGroup.equals("DESCRIPTION")) {
                        // TODO: parse DESCRIPTION-Info
                    } else if (deviceGroup.equals("SESSION")) {
                        // TODO: parse SESSION-Info
                    } else if (deviceGroup.equals("GM")) {
                        handleGM(tokenLine, timestamp, number, bus);
                    } else if (deviceGroup.equals("SM")) {
                        handleSM(tokenLine, timestamp, number, bus);
                    }
                }
            }
        } catch (SRCPUnsufficientDataException e) {
            System.err.println("cannot parse line \"" + s + "\"");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("cannot convert the next token from \"" + s
                    + "\" into an integer");
            e.printStackTrace();
        } catch (SRCPWrongValueException e) {
            System.err.println("wrong value in line \"" + s + "\"");
            e.printStackTrace();
        }

        for (InfoDataListener listener : listeners) {
            listener.infoDataReceived(s);
        }
    }

    private void informListenersSent(String s) {
        for (InfoDataListener listener : listeners) {
            listener.infoDataSent(s);
        }
    }

    private void handleFB(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {

        if (number == INFO_SET) {
            int address = tokenLine.nextIntToken();
            int value = tokenLine.nextIntToken();
            synchronized (FBListeners) {
                for (FBInfoListener l : FBListeners) {
                    l.FBset(timestamp, bus, address, value);
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (FBListeners) {
                for (FBInfoListener l : FBListeners) {
                    l.FBterm(timestamp, bus);
                }
            }
        }
    }

    private void handleGL(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {
        int address = tokenLine.nextIntToken();

        if (number == INFO_SET) {
            SRCPLocomotiveDirection drivemode = SRCPLocomotiveDirection
                    .valueOf(Integer.parseInt(tokenLine.nextStringToken()));
            int v = tokenLine.nextIntToken();
            int vMax = tokenLine.nextIntToken();
            Collection<Boolean> functions = new ArrayList<Boolean>();

            while (tokenLine.hasMoreElements()) {
                functions.add(tokenLine.nextStringToken().equals("1"));
            }

            boolean[] f = new boolean[functions.size()];
            int i = 0;

            for (Boolean function : functions) {
                f[i++] = function.booleanValue();
            }
            synchronized (GLListeners) {
                for (GLInfoListener l : GLListeners) {
                    l.GLset(timestamp, bus, address, drivemode, v, vMax, f);
                }
            }
        } else if (number == INFO_INIT) {
            String protocol = tokenLine.nextStringToken();
            while (tokenLine.hasMoreElements()) {
                // TODO: get params
                tokenLine.nextStringToken();
            }
            synchronized (GLListeners) {
                for (GLInfoListener l : GLListeners) {
                    l.GLinit(timestamp, bus, address, protocol, null);
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (GLListeners) {
                for (GLInfoListener l : GLListeners) {
                    l.GLterm(timestamp, bus, address);
                }
            }
        }
    }

    private void handleGA(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {
        int address = tokenLine.nextIntToken();
        if (number == INFO_SET) {
            int port = tokenLine.nextIntToken();
            int value = tokenLine.nextIntToken();
            synchronized (GAListeners) {
                for (GAInfoListener l : GAListeners) {
                    l.GAset(timestamp, bus, address, port, value);
                }
            }
        } else if (number == INFO_INIT) {
            String protocol = tokenLine.nextStringToken();

            while (tokenLine.hasMoreElements()) {
                // TODO: get params

                tokenLine.nextStringToken();
            }
            synchronized (GAListeners) {
                for (GAInfoListener l : GAListeners) {
                    l.GAinit(timestamp, bus, address, protocol, null);
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (GAListeners) {
                for (GAInfoListener l : GAListeners) {
                    l.GAterm(timestamp, bus, address);
                }
            }
        }
    }

    private void handleLOCK(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {

        String lockedDeviceGroup = tokenLine.nextStringToken();
        int address = tokenLine.nextIntToken();
        if (number == INFO_SET) {
            int duration = tokenLine.nextIntToken();
            int sessionID = tokenLine.nextIntToken();
            synchronized (LOCKListeners) {
                for (LOCKInfoListener l : LOCKListeners) {
                    l.LOCKset(timestamp, bus, address, lockedDeviceGroup,
                            duration, sessionID);
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (LOCKListeners) {
                for (LOCKInfoListener l : LOCKListeners) {
                    l.LOCKterm(timestamp, bus, address, lockedDeviceGroup);
                }
            }
        }
    }

    private void handlePOWER(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {

        if (number == INFO_SET) {
            boolean powerOn = tokenLine.nextStringToken().equals("ON");
            synchronized (POWERListeners) {
                for (POWERInfoListener l : POWERListeners) {
                    l.POWERset(timestamp, bus, powerOn);
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (POWERListeners) {
                for (POWERInfoListener l : POWERListeners) {
                    l.POWERterm(timestamp, bus);
                }
            }
        }
    }

    private void handleSERVER(TokenizedLine tokenLine, double timestamp,
            int number) throws SRCPUnsufficientDataException {

        if (number == INFO_SET) {
            final String action = tokenLine.nextStringToken();

            synchronized (SERVERListeners) {
                for (SERVERInfoListener l : SERVERListeners) {
                    if (action.equals("RESETTING")) {
                        l.SERVERreset(timestamp);
                    } else if (action.equals("TERMINATING")) {
                        l.SERVERterm(timestamp);
                    }
                }
            }
        }
    }

    private void handleSM(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException {
        if (number == INFO_INIT) {
            String protocol = tokenLine.nextStringToken();

            synchronized (SMListeners) {
                for (SMInfoListener l : SMListeners) {
                    l.SMinit(timestamp, bus, protocol);
                }
            }
        } else if (number == INFO_SET) {
            int address = tokenLine.nextIntToken();
            String type = tokenLine.nextStringToken();
            Collection<String> values = new ArrayList<String>();

            while (tokenLine.hasMoreElements()) {
                values.add(tokenLine.nextStringToken());
            }
            synchronized (SMListeners) {
                for (SMInfoListener l : SMListeners) {
                    l.SMset(timestamp, bus, address, type,
                            values.toArray(new String[0]));
                }
            }
        } else if (number == INFO_TERM) {
            synchronized (SMListeners) {
                for (SMInfoListener l : SMListeners) {
                    l.SMterm(timestamp, bus);
                }
            }
        }
    }

    /**
     * Handles GM Messages, calls all registered GMInfoListeners.
     * 
     * @param tokenLine
     * @param timestamp
     * @param number
     * @param bus
     * @throws SRCPUnsufficientDataException
     * @throws NumberFormatException
     * @throws SRCPWrongValueException
     */
    private void handleGM(TokenizedLine tokenLine, double timestamp,
            int number, int bus) throws SRCPUnsufficientDataException,
            NumberFormatException, SRCPWrongValueException {

        if (number == INFO_SET) {
            int sendTo = tokenLine.nextIntToken();
            int replyTo = tokenLine.nextIntToken();
            String messageType = tokenLine.nextStringToken();
            synchronized (GMListeners) {
                for (GMInfoListener l : GMListeners) {
                    l.GMset(timestamp, bus, sendTo, replyTo, messageType,
                            tokenLine);
                }
            }
        }
    }

    public synchronized void addFBInfoListener(FBInfoListener l) {
        FBListeners.add(l);
    }

    public synchronized void addGAInfoListener(GAInfoListener l) {
        GAListeners.add(l);
    }

    public synchronized void addGLInfoListener(GLInfoListener l) {
        GLListeners.add(l);
    }

    public synchronized void addLOCKInfoListener(LOCKInfoListener l) {
        LOCKListeners.add(l);
    }

    public synchronized void addPOWERInfoListener(POWERInfoListener l) {
        POWERListeners.add(l);
    }

    public synchronized void addSERVERInfoListener(SERVERInfoListener l) {
        SERVERListeners.add(l);
    }

    public synchronized void addSMInfoListener(SMInfoListener l) {
        SMListeners.add(l);
    }

    public synchronized void addGMInfoListener(GMInfoListener l) {
        GMListeners.add(l);
    }

    public synchronized void addCRCFInfoListener(CRCFInfoListener l) {
        CRCFListeners.add(l);
    }

    public synchronized void removeFBInfoListener(FBInfoListener l) {
        FBListeners.remove(l);
    }

    public synchronized void removeGAInfoListener(GAInfoListener l) {
        GAListeners.remove(l);
    }

    public synchronized void removeGLInfoListener(GLInfoListener l) {
        GLListeners.remove(l);
    }

    public synchronized void removeLOCKInfoListener(LOCKInfoListener l) {
        LOCKListeners.remove(l);
    }

    public synchronized void removePOWERInfoListener(POWERInfoListener l) {
        POWERListeners.remove(l);
    }

    public synchronized void removeSERVERInfoListener(SERVERInfoListener l) {
        SERVERListeners.remove(l);
    }

    public synchronized void removeSMInfoListener(SMInfoListener l) {
        SMListeners.remove(l);
    }

    public synchronized void removeGMInfoListener(GMInfoListener l) {
        GMListeners.remove(l);
    }

    public synchronized void removeCRCFInfoListener(CRCFInfoListener l) {
        CRCFListeners.remove(l);
    }

    public int getID() {
        return id;
    }

    /**
     * Handler for CRCF Messages, is registered as GMInfoListener. Calls
     * CRCFInfoListener.
     * 
     * @author Michael Oppenauer 03.02.2009
     * 
     */
    class CRCFHandler implements GMInfoListener {

        /*
         * (non-Javadoc)
         * 
         * @see de.dermoba.srcp.devices.GMInfoListener#GMset(double, int, int,
         * int, java.lang.String, de.dermoba.srcp.common.TokenizedLine)
         */
        public void GMset(double timestamp, int bus, int sendTo, int replyTo,
                String messageType, TokenizedLine tokenLine)
                throws SRCPUnsufficientDataException, NumberFormatException,
                SRCPWrongValueException {
            if (messageType.equals("CRCF")) {
                String actor = tokenLine.nextURLStringToken();
                UUID actor_id = UUID.fromString(tokenLine.nextURLStringToken());
                String method = tokenLine.nextStringToken();
                String attribute = tokenLine.nextURLStringToken();
                String attribute_value = "";
                if (tokenLine.hasMoreElements()) {
                    attribute_value = tokenLine.nextURLStringToken();
                }
                synchronized (CRCFListeners) {
                    for (CRCFInfoListener l : CRCFListeners) {
                        if (method.equals("GET")) {
                            l.CRCFget(timestamp, bus, sendTo, replyTo, actor,
                                    actor_id, attribute);
                        } else if (method.equals("SET")) {
                            l.CRCFset(timestamp, bus, sendTo, replyTo, actor,
                                    actor_id, attribute, attribute_value);
                        } else if (method.equals("INFO")) {
                            l.CRCFinfo(timestamp, bus, sendTo, replyTo, actor,
                                    actor_id, attribute, attribute_value);
                        } else if (method.equals("LIST")) {
                            l.CRCFlist(timestamp, bus, sendTo, replyTo, actor,
                                    actor_id, attribute);
                        }
                    }
                }
            }
        }
    }
}
