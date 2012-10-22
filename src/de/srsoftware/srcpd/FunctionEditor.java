package de.srsoftware.srcpd;

import java.util.Vector;

import net.yougli.shakethemall.ColorPickerDialog;
import net.yougli.shakethemall.ColorPickerDialog.OnColorChangedListener;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class FunctionEditor implements OnClickListener {

	private MainActivity context;

	public FunctionEditor(MainActivity mainActivity) {
		context=mainActivity;
	}

	public void onClick(View v) {
		System.out.println(v.getClass());
		if (v instanceof CommandButton)	createOptionDialog((CommandButton)v).show();
	}

	private AlertDialog createOptionDialog(CommandButton b) {
		AlertDialog.Builder builder=new Builder(context);
		builder.setPositiveButton(R.string.change_text, textChanger(b));
		builder.setNeutralButton(R.string.edit_function, functionChanger(b));
		builder.setNegativeButton(R.string.choose_color, colorChanger(b));
		return builder.create();
	}

	private android.content.DialogInterface.OnClickListener colorChanger(final CommandButton b) {
		return new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {				
				ColorPickerDialog cp=new ColorPickerDialog(context, colorChangeListener(b), null, 0, b.getColor());
				cp.show();
			}
		};
	}

	protected OnColorChangedListener colorChangeListener(final CommandButton b) {
		// TODO Auto-generated method stub
		return new OnColorChangedListener() {
			
			public void colorChanged(String key, int color) {
				b.setBackgroundColor(color);
				context.storeLayout(b);
			}			
		};
	}

	private DialogInterface.OnClickListener functionChanger(final CommandButton b) {
		return new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog.Builder builder=new Builder(context);
				LinearLayout list=new LinearLayout(context);
				list.setOrientation(LinearLayout.VERTICAL);
				list.addView(newFunctionButton(null));
				for (Function function: b.getFunctions()){
					list.addView(newFunctionButton(function));
					list.addView(newFunctionButton(null));
				}
				builder.setView(list);
				builder.setPositiveButton(R.string.ok, saveFunctions(b,list));
				builder.setNegativeButton(R.string.cancel, null);
				builder.create().show();
			}
		};
	}
	
	

	protected DialogInterface.OnClickListener saveFunctions(final CommandButton b, final LinearLayout list) {		
		return new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				int count = list.getChildCount();
				Vector<Function> newFunctions=new Vector<Function>();
				for (int index=0; index<count; index++){
					View child = list.getChildAt(index);
					if (child instanceof Button){
						Button button = (Button)child;
						String text=button.getText().toString();
						if (!text.equals(context.getString(R.string.insert_command))){
							newFunctions.add(new Function(text));
						}
					}
				}
				b.setFunctions(newFunctions);
				context.storeLayout(b);
			}
		};
	}

	protected Button newFunctionButton(Function f) {
		Button button=new Button(context);
		if (f==null){
			button.setText(R.string.insert_command);
			button.setOnClickListener(newFunction());
		} else {
			button.setText(f.code());
			button.setOnClickListener(changeFunction());
		}
		return button;
	}
	
	private OnClickListener changeFunction() {
		return new OnClickListener() {
			
			public void onClick(View v) {
				System.out.println(v);
				if (v instanceof Button){
					final Button b = (Button)v;
					AlertDialog.Builder builder=new Builder(context);
					final EditText text=new EditText(context);
					text.setText(b.getText());
					builder.setView(text);
					builder.setPositiveButton(R.string.ok, new AlertDialog.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							String tx=text.getText().toString().trim();
							if (tx.length()>0){
								b.setText(tx);
							} else {
								ViewGroup parent = (ViewGroup)b.getParent();
								int i=0;
								while (parent.getChildAt(i) != b) i++;
								parent.removeViewAt(i);
								parent.removeViewAt(i);
							}
						}
					});
					builder.setNegativeButton(R.string.cancel, null);
					builder.create().show();
				}
				
			}
		};
	}

	private OnClickListener newFunction() {
		return new OnClickListener() {
			
			public void onClick(final View v) {
				AlertDialog.Builder builder=new Builder(context);
				final EditText text=new EditText(context);
				builder.setView(text);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						ViewGroup parent = (ViewGroup)v.getParent();
						int index=parent.indexOfChild(v);
						parent.addView(newFunctionButton(new Function(text.getText().toString().trim())), index);
						parent.addView(newFunctionButton(null), index);
						
					}
				});
				builder.setNegativeButton(R.string.cancel, null	);
				builder.create().show();
				
			}
		};
	}

	private DialogInterface.OnClickListener textChanger(final Button b) {
		return new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				AlertDialog.Builder builder=new Builder(context);
				final EditText text=new EditText(context);
				text.setText(b.getText());
				builder.setView(text);
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int which) {
						b.setText(text.getText().toString().trim());
						context.storeLayout(b);
					}
				});
				builder.setNegativeButton(R.string.cancel, null	);
				builder.create().show();

			}
		};
	}



}
