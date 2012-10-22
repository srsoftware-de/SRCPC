/*
 * Created on 26.09.2005
 *
 */
package de.dermoba.srcp.devices;

import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.model.locomotives.SRCPLocomotiveDirection;

public class GL {

	private final SRCPSession session;
	private final int bus;
	private int address = 0;
	private String protocol = null;
	private String[] parameters = null;
	private int lastDirection=-1;
	private int lastSpeed;
	private int lastMaxSpeed;
	private boolean[] funcStates=new boolean[0];
	
	private static final int DEFAULT_PROTOCOL_VERSION = 1;
	private static final int DEFAULT_STEPS = 128;
	private static final int DEFAULT_FUNCTIONS = 4;

	public GL(SRCPSession pSession, int bus) {
		this.session = pSession;
		this.bus = bus;
	}

	/**
	 * SRCP syntax: INIT &lt;bus&gt; GL &lt;addr&gt; &lt;protocol&gt; [&lt;parameter&gt;.. ]
	 */
	public String init(int pAddress, String pProtocol) throws SRCPException {
		String [] params={""+DEFAULT_PROTOCOL_VERSION,""+DEFAULT_STEPS,""+DEFAULT_FUNCTIONS};
		return init(pAddress, pProtocol, params);
	}

	/**
	 * SRCP syntax: INIT &lt;bus&gt; GL &lt;addr&gt; &lt;protocol&gt; [&lt;parameter&gt;.. ]
	 */
	public String init(int pAddress, String pProtocol, String[] pParameters) throws SRCPException {
		address = pAddress;
		protocol = pProtocol;
		parameters = pParameters;
		StringBuffer paramBuf = new StringBuffer();
		for (int i = 0; i < parameters.length; i++) {
			paramBuf.append(" ");
			paramBuf.append(parameters[i]);
		}
		if (session.isOldProtocol()) {
			return "";
		}
		return session.getCommandChannel().send("INIT " + bus + " GL " + address + " " + protocol + paramBuf.toString());
	}

	/**
	 * SRCP syntax SET &lt;bus&gt; GL &lt;addr&gt; &lt;drivemode&gt; &lt;V&gt; &lt;V_max&gt; &lt;f1&gt; .. &lt;fn&gt;
	 */
	public String set(SRCPLocomotiveDirection drivemode, int v, int vmax, boolean[] f) throws SRCPException {
		if (drivemode==null) drivemode=SRCPLocomotiveDirection.FORWARD;
		lastDirection=drivemode.getDirection();
		lastSpeed=v;
		lastMaxSpeed=vmax;
		StringBuffer functionBuf = new StringBuffer();
		if (f != null) {
			for (int i = 0; i < f.length; i++) {
				functionBuf.append(f[i] ? "1 " : "0 ");
			}
		}
		if (session.isOldProtocol()) {
			return session.getCommandChannel().send("SET GL " + protocol + " " + address + " " + drivemode.getDirection() + " " + v + " " + vmax + " " + functionBuf);
		}
		return session.getCommandChannel().send("SET " + bus + " GL " + address + " " + drivemode.getDirection() + " " + v + " " + vmax + " " + functionBuf);
	}

	/** SRCP syntax GET &lt;bus&gt; GL &lt;addr&gt; */
	public GLData get() throws SRCPException {
		if (session.isOldProtocol()) {
			return new GLData(session.getCommandChannel().send("GET GL " + address));
		}
		return new GLData(session.getCommandChannel().send("GET " + bus + " GL " + address));
	}

	/** SRCP syntax: TERM &lt;bus&gt; GL &lt;addr&gt; */
	public String term() throws SRCPException {
		if (session.isOldProtocol()) {
			return "";
		}
		return session.getCommandChannel().send("TERM " + bus + " GL " + address);
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public void toggleDirection() throws SRCPException {
		set(SRCPLocomotiveDirection.valueOf((lastDirection+1)%2),lastSpeed, lastMaxSpeed,funcStates);
	}

	public void setDirection(boolean b) throws SRCPException {
		SRCPLocomotiveDirection dir = b?SRCPLocomotiveDirection.FORWARD:SRCPLocomotiveDirection.REVERSE;
		set(dir,lastSpeed, lastMaxSpeed, funcStates);
		
	}

	public void changeSpeed(int diff) throws SRCPException {
		System.out.println("changeSpeed("+diff+")");
		lastSpeed=Math.max(0, Math.min(lastMaxSpeed, lastSpeed+diff));
		set(SRCPLocomotiveDirection.valueOf(lastDirection),lastSpeed,lastMaxSpeed,funcStates);
		
	}

	public void set(SRCPLocomotiveDirection direction, int speed, int maxSpeed) throws SRCPException {
		set(direction,speed,maxSpeed,new boolean[0]);
	}

	public void setSpeed(int speed) throws SRCPException {
		speed=Math.max(0, Math.min(lastMaxSpeed, speed));
		set(SRCPLocomotiveDirection.valueOf(lastDirection),speed,lastMaxSpeed,funcStates);		
	}

	public void setFunction(int funcNum, boolean state) throws SRCPException {
		System.out.println("setFun("+funcNum+", "+state+")");
		if (funcStates.length<=funcNum){
			boolean [] dummy = new boolean[funcNum+1];
			for (int i=0; i<funcStates.length; i++) dummy[i]=funcStates[i];			
			funcStates=dummy;
		}
		funcStates[funcNum]=state;
		set(SRCPLocomotiveDirection.valueOf(lastDirection), lastSpeed, lastMaxSpeed, funcStates);
		
	}

	public void toogleFunction(int funcNum) throws SRCPException {
		if (funcStates.length<=funcNum){
			setFunction(funcNum,true);
		} else setFunction(funcNum, !funcStates[funcNum]);
	}
}
