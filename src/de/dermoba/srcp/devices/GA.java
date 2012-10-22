/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class GA {

	private final SRCPSession session;
	private final int bus;
	private int address;
	private String protocol;
	private String[] parameters;
	private int lastPort = 0;

	public GA(SRCPSession pSession, int bus) {
		this.session = pSession;
		this.bus = bus;
	}

	/**
	 * SRCP syntax: INIT &lt;bus&gt; GA &lt;addr&gt; &lt;device protocol&gt; [&lt;parameter&gt;.. ]
	 */
	public String init(int pAddress, String pProtocol) throws SRCPException {
		return init(pAddress, pProtocol, new String[0]);
	}

	/**
	 * SRCP syntax: INIT &lt;bus&gt; GA &lt;addr&gt; &lt;device protocol&gt; [&lt;parameter&gt;.. ]
	 */
	public String init(int pAddress, String pProtocol, String[] pParameters) throws SRCPException {
		address = pAddress;
		protocol = pProtocol;
		parameters = pParameters;
		StringBuffer paramBuf = new StringBuffer();
		for (int i = 0; i < parameters.length; i++) {
			paramBuf.append(parameters[i]);
			paramBuf.append(" ");
		}
//		System.out.println("remove this return");
//		if (10<100) return "";
		if (!session.isOldProtocol()) {
			return session.getCommandChannel().send("INIT " + bus + " GA " + address + " " + protocol + " " + paramBuf.toString());
		}
		return "";
	}

	/**
	 * SRCP syntax SET &lt;bus&gt; GA &lt;addr&gt; &lt;port&gt; &lt;value&gt; &lt;delay&gt;
	 */
	public String set(int port, int value, int delay) throws SRCPException {
		lastPort=port;
		String command="SET " + bus + " GA " + address + " " + port + " " + value + " " + delay;		
//		System.out.println(command);
//		System.out.println("Remove this retrun");
//		if (1<10) return "";
		if (session.isOldProtocol()) {
			command="SET  GA " + protocol + " " + address + " " + port + " " + value + " " + delay;
		}
		return session.getCommandChannel().send(command);
	}

	/** SRCP syntax GET &lt;bus&gt; GA &lt;addr&gt; &lt;port&gt; */
	public String get(int port) throws SRCPException {
		if (session.isOldProtocol()) {
			return session.getCommandChannel().send("GET GA " + protocol + " " + address + " " + port);
		}
		return session.getCommandChannel().send("GET " + bus + " GA " + address + " " + port);
	}

	/** SRCP syntax: TERM &lt;bus&gt; GA &lt;addr&gt; */
	public String term() throws SRCPException {
		if (session.isOldProtocol()) {
			return "";
		}
		return session.getCommandChannel().send("TERM " + bus + " GA " + address);
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public String[] getParameters() {
		return parameters;
	}

	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	public void toogle() throws SRCPException {
		set((lastPort+1)%2,1,300);
	}
}
