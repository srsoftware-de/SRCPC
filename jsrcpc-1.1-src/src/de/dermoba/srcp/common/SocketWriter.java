/*
 * Created on 01.07.2005
 *
 */
package de.dermoba.srcp.common;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketWriter {

    protected OutputStream outputStream = null;
    protected OutputStreamWriter outputWriter = null;

    public SocketWriter(Socket communicationSocket) throws IOException {
        outputStream=communicationSocket.getOutputStream();
        outputWriter = new OutputStreamWriter(outputStream);
    }

    public void write(String buffer) throws IOException {
        outputWriter.write(buffer.toCharArray());
        outputWriter.flush();
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
