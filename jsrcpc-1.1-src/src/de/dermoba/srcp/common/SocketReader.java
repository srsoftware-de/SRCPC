/*
 * Created on 01.07.2005
 *
 */
package de.dermoba.srcp.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class SocketReader {

    private static final int LINEFEED = 0x0a;

    protected InputStreamReader inputReader = null;

    public SocketReader(Socket communicationSocket) throws IOException {
        inputReader = new InputStreamReader(communicationSocket.getInputStream());
    }

    public String read() throws IOException {
        StringBuffer buffer = new StringBuffer();
        boolean lineComplete=false;
        int input;
        while(!lineComplete) {
            input = inputReader.read();
            if (input<0) {
                throw new IOException();
            }
            if (input == LINEFEED ) {
                lineComplete=true;
            }
            if (input>0) {
                buffer.append((char)input);
            }
        }
        return buffer.toString().trim();
    }
}
