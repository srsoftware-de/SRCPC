/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.Logging;

import de.dermoba.srcp.common.exception.SRCPException;


public class Logger {
	
	private static Logger logger=new Logger();
	
	public static Logger getLogger(Class clazz) {		
		return logger;
	}

	public void info(String string) {
		System.out.println(string);
		
	}

	public void debug(String string) {
		System.out.println(string);
		
	}

	public void error(SRCPException e) {
		e.printStackTrace();
		
	}

}
