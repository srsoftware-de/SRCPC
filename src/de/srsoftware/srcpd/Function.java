/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.srcpd;

import java.util.TreeMap;

import de.dermoba.srcp.client.CommandChannel;
import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;
import de.dermoba.srcp.devices.GA;
import de.dermoba.srcp.devices.GL;

public class Function {

	
	private static final int DEFAULT_BUS = 1;
	private static final String DEFAULT_PROTOCOL = "N";
	/*private static TreeMap <Integer,Lok>loks=new TreeMap<Integer,Lok>(); 
	private static Lok selectedLok; /*/
	private static TreeMap <Integer,GL>loks=new TreeMap<Integer,GL>(); 
	private static GL selectedLok; // */
	private static TreeMap <Integer,GA>switches=new TreeMap<Integer,GA>();
	private static SRCPSession srcpdsession;
	private static CommandChannel channel;
	private String code;

	public Function(String code) {
		this.code=code;
	}

	public String code() {
		return code;
	}

	public String tag() {		
		return "<function>\n"+code+"\n</function>\n";
	}

	public void execute() throws SRCPException {
		execute(code);
		
	}

	public static void setSrcpSession(SRCPSession session) {		
		srcpdsession=session;
		channel=session.getCommandChannel();
	}
	
	static void send(String cmd) {
		System.out.println("Function.send("+cmd+")");
		if (channel!=null) try {
			channel.send(cmd.trim());
		} catch (SRCPException e) {
			e.printStackTrace();
		}		
	}

	public static void execute(String string) throws SRCPException {		
			String[] commands = string.split("/");
			for (String cmd : commands) {
				cmd=parse(cmd.trim());
				if (cmd!=null && cmd.length()>2) send(cmd);
			}			
	}

	private static String parse(String cmd) {
		System.out.println("parse("+cmd+")");
		String firstWord=firstWord(cmd);
		try {
			if (firstWord.equals("stop")){
				selectedLok.setSpeed(0);
				return null;
			}
			if (firstWord.equals("function")){
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				int funcNum=Integer.parseInt(firstWord);
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				if (firstWord.length()<1){
					selectedLok.toogleFunction(funcNum);
					return null;
				} 
				System.out.println(firstWord);
				int state=Integer.parseInt(firstWord);
				selectedLok.setFunction(funcNum,state>0);
				return null;
			}
			if (firstWord.equals("switch")){
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
			
				int switchNumber=Integer.parseInt(firstWord);
				if (!	switches.containsKey(switchNumber)) switches.put(switchNumber,createWeiche(switchNumber));
				GA weiche = switches.get(switchNumber);
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				if (firstWord.length()<1) {
					weiche.toogle();
					return null;
				}
				int position=Integer.parseInt(firstWord);
				weiche.set(position,1,300);
				return null;		
			}
			if (firstWord.equals("select")){
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				int number=Integer.parseInt(firstWord);
				/* if (!loks.containsKey(number)) loks.put(number,new Lok(number)); 
				selectedLok=loks.get(number); /*/
				if (!loks.containsKey(number)) loks.put(number,createLok(number));
				selectedLok=loks.get(number); //*/
				return null;
			}
			if (firstWord.equals("direction")){
				if (selectedLok==null) return cmd;
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				if (firstWord.length()<1){
					selectedLok.toggleDirection();
					return null;
				}
				int dir=Integer.parseInt(firstWord);
				if (dir==0) selectedLok.setDirection(true);
				if (dir>0) selectedLok.setDirection(false);
				return null;
			}
			if (firstWord.equals("speed")){
				cmd=remains(cmd);
				firstWord=firstWord(cmd);
				if (firstWord.equals("+")){
					cmd=remains(cmd);
					firstWord=firstWord(cmd);
					int diff=Integer.parseInt(firstWord);
					selectedLok.changeSpeed(diff);
					return null;
				}
				if (firstWord.equals("-")){
					cmd=remains(cmd);
					firstWord=firstWord(cmd);
					int diff=Integer.parseInt(firstWord);
					selectedLok.changeSpeed(-diff);
					return null;
				}			
				int speed=Integer.parseInt(firstWord);
				selectedLok.setSpeed(speed);
				return null;
			}
		} catch (NumberFormatException nfe){			
		} catch (SRCPException e) {
			e.printStackTrace();			
		}
		return cmd.trim();
	}

	private static GL createLok(int number) throws SRCPException {
		System.out.println("createLok("+number+")");
		GL lok=new GL(srcpdsession, DEFAULT_BUS);		
		lok.init(number, DEFAULT_PROTOCOL);
		lok.set(null, 0, 100);
		return lok;
	}

	private static GA createWeiche(int switchNumber) throws SRCPException {
		System.out.println("createWeiche("+switchNumber+")");
		GA result=new GA(srcpdsession, DEFAULT_BUS);
		result.init(switchNumber, DEFAULT_PROTOCOL);
		return result;
	}

	private static String remains(String cmd) {
		int i=cmd.indexOf(" ");		
		if (i>0) return cmd.substring(i).trim().toLowerCase();
		return "";
	}

	private static String firstWord(String cmd) {
		int i=cmd.indexOf(" ");		
		if (i>0) return cmd.substring(0,i).toLowerCase();
		return cmd;
	}
	
	public static void reset(){
		loks=new TreeMap<Integer, GL>();
		switches=new TreeMap<Integer, GA>();
	}

}
