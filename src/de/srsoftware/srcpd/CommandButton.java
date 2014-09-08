/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.srcpd;

import java.util.Vector;

import de.dermoba.srcp.common.exception.SRCPException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.widget.Button;

public class CommandButton extends Button {

	private MainActivity mainActivity;
	private Vector<Function> functions;
	private int color=Color.LTGRAY;
	private int functionIndex;

	public CommandButton(Context context) {
		super(context);
		mainActivity=(MainActivity)context;
		functions=new Vector<Function>();
		functionIndex=0;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mainActivity.loadLayout(this);
	}

	public Vector<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(Vector<Function> newFunctions) {
		functions=newFunctions;
		functionIndex=0;
	}

	public void addFunction(Function function) {
		functions.add(function);
		
	}

	public void click() throws SRCPException, NetworkErrorException{
		if (functions.isEmpty()) return;
		functions.get(functionIndex).execute();
		functionIndex++;
		if (functionIndex>=functions.size()) functionIndex=0;
	}
	
	public void setBackgroundColor(int color) {
		getBackground().setColorFilter(color, Mode.MULTIPLY);
		this.color=color;
	}

	public int getColor() {
		return color;
	}
}
