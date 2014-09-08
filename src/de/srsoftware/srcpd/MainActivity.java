/* Copyright ©2012  Stephan Richter

    This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110, USA
*/
package de.srsoftware.srcpd;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;
import de.dermoba.srcp.client.SRCPSession;
import de.dermoba.srcp.common.exception.SRCPException;

public class MainActivity extends Activity implements OnClickListener, android.content.DialogInterface.OnClickListener {
		
    private OnClickListener currentListener;
		private ActionPerformer normalListener;
		private LayoutEditor layoutEditListener;
		private FunctionEditor functionEditListener;
		private boolean loaded;
		private SRCPSession srcpsession;
		
		@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
        setContentView(R.layout.activity_main);
        createListeners();
        createLayout();
     }		

		private void createListeners() {    	
    	normalListener = new ActionPerformer();
    	layoutEditListener = new LayoutEditor(this);  
    	functionEditListener=new FunctionEditor(this);
    	currentListener=normalListener;
		}

		protected void loadLayout(CommandButton button) {
			if (loaded) return;
			loaded=true;
			SharedPreferences settings=getSharedPreferences("SRCPD", MODE_PRIVATE);
			String layout=settings.getString("Layout", "");
			
			if (layout.length()<1) return;
			//System.out.println(layout);
			ViewGroup current=(ViewGroup) button.getParent();
			current.removeView(button);
			CommandButton btn=null;
			int h=current.getHeight();
			int w=current.getWidth();
			if (layout.length()>0){				
				String[] lines=layout.split("\n");				
				for (int i=0; i<lines.length; i++){
					String line=lines[i];
					if (line.equals("<layout>")){
					} else if (line.equals("</horizontal>")){
						current=(ViewGroup) current.getParent();
						w=w*2;
					} else if (line.equals("</vertical>")){
						current=(ViewGroup) current.getParent();
						h=h*2;
					} else if (line.equals("<horizontal>")){
						LinearLayout l=new LinearLayout(this);
						l.setLayoutParams(new LayoutParams(w,h));
						l.setOrientation(LinearLayout.HORIZONTAL);
						w=w/2;
						current.addView(l);
						current=l;
					} else if (line.equals("<vertical>")){						
						LinearLayout l=new LinearLayout(this);
						l.setLayoutParams(new LayoutParams(w,h));
						l.setOrientation(LinearLayout.VERTICAL);
						h=h/2;
						current.addView(l);
						current=l;
					} else if (line.startsWith("<button")){
						btn=new CommandButton(this);
						line=line.substring(8,line.length()-1);						
						while (line.length()>0){
							if (line.startsWith("text=\"")){
								line=line.substring(6);
								int end=line.indexOf("\"");
								String text=line.substring(0,end);
								btn.setText(text);
								line=line.substring(end+1).trim();
							} else if (line.startsWith("color=\"")){
								line=line.substring(7);
								int end=line.indexOf("\"");
								String col=line.substring(0,end);
								try{
									int color=Integer.parseInt(col);
									btn.setBackgroundColor(color);
								} catch (NumberFormatException nfe){}
								line=line.substring(end+1).trim();
							} else {
								System.err.println("unknown input line: "+line);
								line="";
							}
							
						}						
						
						btn.setLayoutParams(new LayoutParams(w,h));
						btn.setOnClickListener(this);
						current.addView(btn);						
					} else if (line.equals("</button>")){
					} else if (line.equals("<function>")){
						i++;
						btn.addFunction(new Function(lines[i]));
					} else if (line.equals("<functions>")){
					} else if (line.equals("</function>")){
					} else if (line.equals("</functions>")){
					} else if (line.equals("</layout>")){
					} else {
						System.out.println(line);
					}
				}
			}
		}

		private Button createLayout() {
			CommandButton base=new CommandButton(this);
			base.setText(R.string.new_button);			
			base.setOnClickListener(this);			
			setContentView(base);
			return base;
		}

		@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	boolean	result=super.onOptionsItemSelected(item);
    	int i=item.getItemId();
    	System.out.println(i);
    	switch (i){
    	case R.id.menu_layout:
    		currentListener=layoutEditListener;
    		break;
    	case R.id.function:    	
    		currentListener=functionEditListener;
    		break;
    	case R.id.menu_normal:
    		currentListener=normalListener;
    		break;
    	case R.id.menu_server:
    		startActivity(new Intent(this,ServerSettings.class));
    		break;
    	case R.id.help:
    		startHelp();
    		break;
    	}
    	System.out.println(currentListener);
    	return result;
    }

		private void startHelp() {
			Intent browser=new Intent(Intent.ACTION_VIEW,Uri.parse("http://srsoftware.de/SRCPDoku"));
			startActivity(browser);
		}


		public void onClick(View v) {
			currentListener.onClick(v);
		}

		protected void storeLayout(View v) {	
			ViewParent parent = v.getParent();
			while (parent!=null && !(v instanceof FrameLayout)){
				v=(ViewGroup)parent;
				parent = v.getParent();
			}		
			try {
				StringBuffer sb=new StringBuffer();
				sb.append("<layout>\n");
				walkTree((ViewGroup) v,sb);
				sb.append("</layout>");
				Editor settings=getSharedPreferences("SRCPD", Activity.MODE_PRIVATE).edit();				
				settings.putString("Layout", sb.toString());
				settings.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void walkTree(View view, StringBuffer sb) throws IOException {
			ViewGroup v=(ViewGroup)view;
			int c=v.getChildCount();
			for (int i=0; i<c; i++){
				View child = v.getChildAt(i);
				if (child instanceof LinearLayout){
					switch (((LinearLayout) child).getOrientation()) {
					case LinearLayout.HORIZONTAL:
						sb.append("<horizontal>\n");
						walkTree(child, sb);
						sb.append("</horizontal>\n");
						break;
					case LinearLayout.VERTICAL:
						sb.append("<vertical>\n");
						walkTree(child, sb);
						sb.append("</vertical>\n");
						break;

					default:
						break;
					}

					
				}
				if (child instanceof CommandButton){
					CommandButton mb = (CommandButton)child;
					sb.append("<button text=\""+mb.getText()+"\" color=\""+mb.getColor()+"\">\n");
					Vector<Function> functions = mb.getFunctions();
					if (!functions.isEmpty()){
						sb.append("<functions>\n");
						for (Function function:functions)	sb.append(function.tag());
						sb.append("</functions>\n");
					}
					sb.append("</button>\n");
				}
			}
		}
		
		private class ConnectionThread extends Thread{
			private String host;
			private int port;
			
			public ConnectionThread(String host, int port) {
				this.host=host;
				this.port=port;
			}

			@Override
			public void run() {
				super.run();
				try {
					srcpsession=new SRCPSession(host, port);
					srcpsession.connect();
					Function.setSrcpSession(srcpsession);	
					Toast.makeText(null, "Serververbindung hergestellt", Toast.LENGTH_LONG).show();
				} catch (SRCPException e) {
					Toast.makeText(null, "Fehler bei Verbindungsherstellung!", Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}
		
		@Override
		protected void onResume() {
			super.onResume();
      if (firstStart()) return;

			SharedPreferences prefs=getSharedPreferences("SRCPD", Activity.MODE_PRIVATE);
			String host=prefs.getString("host", "");
			int port=prefs.getInt("port", 4303);
			if (host.length()==0) {
				AlertDialog.Builder db=new Builder(this);
				db.setMessage(R.string.no_host);
				db.setPositiveButton(R.string.ok, this);				
				db.show();				
			} else if (srcpsession==null){
				ConnectionThread conThread=new ConnectionThread(host,port);
				conThread.start();
			}
		}
		
		
    private boolean firstStart() {
			final SharedPreferences settings=getSharedPreferences("SRCPD", MODE_PRIVATE);
			if (!settings.contains("firststart")){
				AlertDialog.Builder adb=new Builder(this);
				adb.setMessage(R.string.copyright);
				adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						settings.edit().putBoolean("firststart", false).commit();
						onResume();

					}
				});
				adb.create().show();
				return true;
			}			
			return false;
		}
		
		@Override
		protected void onDestroy() {
			super.onDestroy();
			System.out.println("onDestry");
			try {
				Function.execute("SET 1 POWER OFF/SET 2 POWER OFF/SET 3 POWER OFF");
				if (srcpsession!=null) srcpsession.disconnect();
				srcpsession=null;
				loaded=false;
				Function.reset();
			} catch (SRCPException e) {}
		}

		public void onClick(DialogInterface dialog, int which) {
			Intent einst=new Intent(this, ServerSettings.class);
			startActivity(einst);			
		}
}
