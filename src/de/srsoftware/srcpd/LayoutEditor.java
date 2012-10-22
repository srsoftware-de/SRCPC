package de.srsoftware.srcpd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class LayoutEditor implements OnClickListener {

	
	private MainActivity activity;
	private static int num=0;

	public LayoutEditor(MainActivity activity) {
		this.activity=activity;
	}
	
	public AlertDialog createDialog(final View v){
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setPositiveButton(R.string.tile_horizontally, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				LinearLayout subLayout=new LinearLayout(activity);
				subLayout.setOrientation(LinearLayout.HORIZONTAL);				
				ViewGroup parent = (ViewGroup)v.getParent();
				int w=v.getWidth();
				int h=v.getHeight();
				int pos=0;
				if (parent.getChildAt(pos)!=v) pos=1;				
				CommandButton newButton2=new CommandButton(activity);
				v.setLayoutParams(new LayoutParams(w/2,h));
				newButton2.setLayoutParams(new LayoutParams(w/2, h));
				subLayout.setLayoutParams(new LayoutParams(w, h));
				newButton2.setText(activity.getString(R.string.new_button)+" "+num++);
				newButton2.setOnClickListener(activity);
				parent.removeView(v);
				subLayout.addView(v);
				subLayout.addView(newButton2);
				parent.addView(subLayout,pos);
				activity.storeLayout(v);
			}
		});
		builder.setNegativeButton(R.string.tile_vertically, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				LinearLayout subLayout=new LinearLayout(activity);
				subLayout.setOrientation(LinearLayout.VERTICAL);				
				ViewGroup parent = (ViewGroup)v.getParent();
				int w=v.getWidth();
				int h=v.getHeight();
				int pos=0;
				if (parent.getChildAt(pos)!=v) pos=1;				
				CommandButton newButton2=new CommandButton(activity);
				v.setLayoutParams(new LayoutParams(w,h/2));
				newButton2.setLayoutParams(new LayoutParams(w, h/2));
				subLayout.setLayoutParams(new LayoutParams(w, h));				
				newButton2.setText(activity.getString(R.string.new_button)+" "+num++);
				newButton2.setOnClickListener(activity);
				parent.removeView(v);
				subLayout.addView(v);
				subLayout.addView(newButton2);
				parent.addView(subLayout,pos);
				activity.storeLayout(v);
			}
		});
		builder.setNeutralButton(R.string.delete, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				ViewGroup parent = (ViewGroup)v.getParent();
				int count=parent.getChildCount();
				View otherChild=null;
				for (int i=0; i<count; i++){
					otherChild = parent.getChildAt(i);
					if (otherChild!=v) {
						break;
					} else otherChild=null;
				}
				if (otherChild!=null){
					otherChild.setLayoutParams(parent.getLayoutParams());
					parent.removeView(otherChild);
					ViewGroup grandParent = (ViewGroup)parent.getParent();
					grandParent.removeView(parent);
					grandParent.addView(otherChild);
					activity.storeLayout(grandParent);
				} else activity.storeLayout(parent);

			}
		});
		return builder.create();
	}





	protected int width(ViewGroup parent) {
		if (parent instanceof LinearLayout){
			switch (((LinearLayout) parent).getOrientation()){
				case LinearLayout.HORIZONTAL: return parent.getWidth()/2; 
				case LinearLayout.VERTICAL: return parent.getHeight();
			}
		}
		return parent.getWidth();
	}

	public void onClick(View v) {
		createDialog(v).show();
	}

}
