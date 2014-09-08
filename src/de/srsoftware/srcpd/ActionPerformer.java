/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.srcpd;

import android.accounts.NetworkErrorException;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import de.dermoba.srcp.common.exception.SRCPException;

public class ActionPerformer implements OnClickListener,DialogInterface.OnClickListener {

	private Context context;
	
	public ActionPerformer(Context c) {
		this.context=c;
	}
	
	public void onClick(View v) {
			if (v instanceof CommandButton){
				try {
					((CommandButton) v).click();
				} catch (SRCPException e) {
					e.printStackTrace();
				} catch (NetworkErrorException e) {
					e.printStackTrace();
					AlertDialog.Builder db=new Builder(context);
					db.setMessage(R.string.no_server_connection);
					db.setPositiveButton(R.string.ok, this);				
					db.show();				
				}
			}
	}

	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		
	}
}
