/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.srcpd;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.widget.EditText;

public class ServerSettings extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_settings);
        SharedPreferences prefs=getSharedPreferences("SRCPD", Activity.MODE_PRIVATE);
        ((EditText)findViewById(R.id.editPort)).setText(""+prefs.getInt("port", 4303));
        ((EditText)findViewById(R.id.editHost)).setText(""+prefs.getString("host", ""));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_server_settings, menu);
        return true;
    }
    
    @Override
    protected void onStop() {
    	super.onStop();    	
    	Editor editor = getSharedPreferences("SRCPD", Activity.MODE_PRIVATE).edit();
    	String host=((EditText)findViewById(R.id.editHost)).getText().toString();
    	editor.putString("host", host);
    	String portString=((EditText)findViewById(R.id.editHost)).getText().toString();
    	try {
    		int port=Integer.parseInt(portString);
      	editor.putInt("port", port);
      } catch (NumberFormatException nfe){}
    	editor.commit();
    }
}
