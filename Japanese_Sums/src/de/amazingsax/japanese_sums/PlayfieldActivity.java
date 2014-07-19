package de.amazingsax.japanese_sums;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PlayfieldActivity extends Activity implements OnClickListener {

	static PlayfieldActivity refereceForstaticHandler;

	static class calculationReadyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			refereceForstaticHandler.showsolution();
		}

	}

	calculationReadyHandler handler;
	public ProgressDialog progressdialog;
	Playfield riddle;

	int playfieldSize;
	int maxNumber;
	int maxSum;
	boolean isAGame; // wahr wenn zum spielen genutzt, falsch wenn nur zum
	// lösen genutzt

	// final int zellwidth=100;
	// final int zellhight=100; // to do, zellgroesse dynamisch bestimmen

	ArrayList<byte[]> hblocks;
	ArrayList<byte[]> vblocks; // fuer die intialisierung des solvers

	GridLayout playField;

	EditText[][] eintraege;
	Button[] vorgabenHorizontal;
	Button[] vorgabenVertikal;

	byte[][] values;

	ImageView linksoben;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		refereceForstaticHandler = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playfield);
		Intent intent = getIntent();
		Bundle data = intent.getExtras();

		playfieldSize = data.getInt("playfieldSize"); // Spielfeldgröße setzen
		maxNumber = data.getInt("maxNumber"); // Maximalwert setzen
		isAGame = data.getBoolean("playAsGame", false); // Soll gespielt werden oder soll Rätsel gelöst werden
		maxSum = maxNumber * (maxNumber + 1) / 2; // groesstmoegliche summe

		handler = new calculationReadyHandler();
		linksoben = new ImageView(this);
		linksoben.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_launcher));

		playField = (GridLayout) findViewById(R.id.playfield);
		//eintraege = new Button[playfieldSize][playfieldSize];
		eintraege = new EditText[playfieldSize][playfieldSize];
		vorgabenHorizontal = new Button[playfieldSize];
		vorgabenVertikal = new Button[playfieldSize];

		playField.setRowCount(playfieldSize + 1);
		playField.setColumnCount(playfieldSize + 1);

		playField.addView(linksoben);

		// Buttons fuer Vertikale Summenen zufuegen
		for (int i = 0; i < playfieldSize; ++i) {
			vorgabenVertikal[i] = new Button(this);
			vorgabenVertikal[i].setGravity(Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL);
			vorgabenVertikal[i].setText(R.string.leereVorgabe);
			if(!isAGame) {  
				vorgabenVertikal[i].setOnClickListener(this); // Summen nur Editierbar wenn als solver genutzt
			}
			GridLayout.LayoutParams buttonparams = new GridLayout.LayoutParams();
			buttonparams.setGravity(Gravity.FILL_VERTICAL);
			playField.addView(vorgabenVertikal[i], buttonparams);
		}

		// Buttons für horizontale summen hinzufügen
		for (int i = 0; i < playfieldSize; ++i) {
			vorgabenHorizontal[i] = new Button(this);
			vorgabenHorizontal[i].setGravity(Gravity.RIGHT
					| Gravity.CENTER_VERTICAL);
			// vorgabenHorizontal[i].setHeight(zellhight);
			vorgabenHorizontal[i].setText(R.string.leereVorgabe);
			GridLayout.LayoutParams buttonparams = new GridLayout.LayoutParams();
			buttonparams.setGravity(Gravity.FILL_HORIZONTAL);
			playField.addView(vorgabenHorizontal[i], buttonparams);
			if(!isAGame) {
				vorgabenHorizontal[i].setOnClickListener(this);  //Summen nur Editierbar wenn als solver genutzt
			}

			for (int j = 0; j < playfieldSize; ++j) {
				//eintraege[i][j] = new Button(this);
				GridLayout.LayoutParams entryparams = new GridLayout.LayoutParams();
				entryparams.setGravity(Gravity.FILL_HORIZONTAL|Gravity.FILL_VERTICAL);
				eintraege[i][j] = new EditText(this);
				eintraege[i][j].setText(R.string.leererEintrag);
				eintraege[i][j].setBackgroundResource(R.drawable.rectangle);
				eintraege[i][j].setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
				eintraege[i][j].setInputType(InputType.TYPE_CLASS_NUMBER);
				if(isAGame) {
					eintraege[i][j].setOnFocusChangeListener(new OnFocusChangeListener() {
						
						@Override
						public void onFocusChange(View v, boolean hasFocus) {
							if(!hasFocus) {
								EditText cell=(EditText) v;
								String s=cell.getText().toString();
								int wert;
								try {
									wert=Integer.valueOf(s);
									if(wert==0) {
										cell.setBackgroundResource(R.drawable.blackrectangle);
										cell.setTextColor(getResources().getColor(R.color.hintergrundfarbe));
									} else
									{
										cell.setBackgroundResource(R.drawable.rectangle);
										cell.setTextColor(getResources().getColor(R.color.black));
									}
								} catch (NumberFormatException e) {
									cell.setText("");
								}
							}
						}
					}); 
				}
				// eintraege[i][j].setWidth(zellwidth);
				// eintraege[i][j].setWidth(zellhight);
				playField.addView(eintraege[i][j],entryparams);
			}
		}
		hblocks = new ArrayList<byte[]>();
		vblocks = new ArrayList<byte[]>();
		if (isAGame) {
			Button checkButton=(Button)findViewById(R.id.checkButton);
			checkButton.setOnClickListener(this);
			checkButton.setVisibility(View.VISIBLE);
			
			ProgressDialog createProgress = new ProgressDialog(this);
			progressdialog=createProgress;
			RiddleCreator riddleCreator= new RiddleCreator(this,createProgress,(byte)this.playfieldSize,(byte)this.maxNumber);
			riddle=riddleCreator;
			createProgress.setMessage(getResources().getString(R.string.createRiddle));
			createProgress.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					// TODO Auto-generated method stub
					hblocks=riddle.gethBlocks(); // nach beenden des tasks ausführen
					vblocks=riddle.getvBlocks();
					setAllVorgabenFromBlocks();
					values=riddle.getEntries();
					riddle=null; // Der riddlecreator wird nun nicht mehr gebraucht -> dereferenzieren -> speicher freigeben
				}
			});
			createProgress.setCancelable(false);
			createProgress.setButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					progressdialog.cancel();
				}
			});
			
			createProgress.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					riddle.interrupt();
					riddle=null;
					finish();	
				}
			});
			createProgress.show();
			riddleCreator.start();
			//riddleCreator.createRiddle(); // to do in eigenen task packen
			//showsolution(); // for testing
			
		} else {
			for (int i = 0; i < playfieldSize; ++i) {
				hblocks.add(null);
				vblocks.add(null);
			}
		}

	}

	public int getmaxSum() {
		return maxSum;
	}
	
	private void setAllVorgabenFromBlocks() {
		for(int i=0;i<playfieldSize;++i) {
			Button tmpHbutton = vorgabenHorizontal[i];
			Button tmpVbutton = vorgabenVertikal[i];
			
			tmpHbutton.setText(byteArray2String(hblocks.get(i),true));
			tmpVbutton.setText(byteArray2String(vblocks.get(i),false));
		}
	}

	public void setVorgaben(boolean horizontal, int werte[], int zeileorSpalte) {
		if (zeileorSpalte < playfieldSize) {
			Button tempbutton = horizontal ? vorgabenHorizontal[zeileorSpalte]
					: vorgabenVertikal[zeileorSpalte];
			tempbutton.setText(intArray2String(werte, horizontal));
			byte[] werteb = new byte[werte.length];
			for (int i = 0; i < werte.length; ++i) {
				werteb[i] = (byte) werte[i];
			}
			if (horizontal) {
				hblocks.set(zeileorSpalte, werteb);
			} else {
				vblocks.set(zeileorSpalte, werteb);
			}
		}
	}

	private String intArray2String(int[] intarray, boolean horizontal) {
		String s = new String();
		char trennzeichen = horizontal ? ' ' : '\n';
		for (int i = 0; i < intarray.length; ++i) {
			s += String.valueOf(intarray[i]);
			if (i != intarray.length - 1)
				s += trennzeichen;
		}
		return s;
	}
	
	private String byteArray2String(byte[] byteArray, boolean horizontal) {
		String s = new String();
		char trennzeichen = horizontal ? ' ' : '\n';
		for (int i = 0; i < byteArray.length; ++i) {
			s += String.valueOf(byteArray[i]);
			if (i != byteArray.length - 1)
				s += trennzeichen;
		}
		return s;
	}

	private void solveRiddle() {
		// erstmal testen ob alles ausgefuellt ist

		for (int i = 0; i < playfieldSize; ++i) {
			if (hblocks.get(i) == null || vblocks.get(i) == null) {
				Toast toast = Toast.makeText(this, R.string.playfieldnotOkay,
						Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
		} //

		progressdialog = new ProgressDialog(this);
		progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressdialog.setMessage(getResources().getString(
				R.string.progressdialog));
		progressdialog.setMax(playfieldSize * playfieldSize + 2*playfieldSize);
		progressdialog.setCancelable(false);
		progressdialog.setOwnerActivity(this);
		progressdialog.show();

		riddle = new Playfield(this,(byte) playfieldSize, (byte) maxNumber, hblocks,
				vblocks, progressdialog, handler);
		if (riddle.isSolveable()) {
			riddle.start();
		} else {
			showsolution();
			progressdialog.dismiss();
		}
	}

	private void showsolution() {
		values = riddle.getEntries();
		
		if(riddle.isToComplex) {
			Toast toast = Toast.makeText(this, R.string.toComplex,
					Toast.LENGTH_LONG); // todo -> alert Dialog
			toast.show();
			return;
		}
		if (riddle.isSolveable()) {
			for (int i = 0; i < playfieldSize; ++i) {
				for (int j = 0; j < playfieldSize; ++j) {
					if (riddle.getFieldElement((byte) i, (byte) j).isFixed()) {
						String s;
						s = String.valueOf((int) values[i][j]);
						eintraege[i][j].setText(s);
						if (values[i][j] == (byte) 0) {
							eintraege[i][j]
									.setBackgroundResource(R.drawable.blackrectangle);
						} else {
							eintraege[i][j]
									.setBackgroundResource(R.drawable.rectangle);
						}
					} else {
						eintraege[i][j].setText("");
						eintraege[i][j]
								.setBackgroundResource(R.drawable.rectangle);
					}

				}

			}
		} else {

			Toast toast = Toast.makeText(this, R.string.thereisnoSolution,
					Toast.LENGTH_LONG); // todo -> alert Dialog
			toast.show();
			return;
		}
		
		if (!riddle.isSolved()) {
			Toast toast = Toast.makeText(this, R.string.noSolutionFound,
					Toast.LENGTH_LONG); // todo -> alert Dialog
			toast.show();
		}

	}
	
	private void checkInput() {
		String s;
		int wert;
		boolean allright=true;
		
		for(int i = 0 ; i<playfieldSize;++i) {
			for(int j=0;j<playfieldSize;++j) {
				s=eintraege[i][j].getText().toString();
				try{
					wert=Integer.valueOf(s);
					if(wert == values[i][j]) {
						eintraege[i][j].setTextColor(getResources().getColor(R.color.richtig));
					} else {
						eintraege[i][j].setTextColor(getResources().getColor(R.color.falsch));
						allright=false;
					}
				} catch (NumberFormatException e) {
					allright=false;
				}
			}
		}
		if(allright) {
			Toast toast= Toast.makeText(this,R.string.richtig,Toast.LENGTH_LONG);
			toast.show();
		}
	}

	public void onClick(View v) {
		if (v.getId() == R.id.solveButton2) {
			solveRiddle();
		} else {
			if (v.getId() == R.id.backButton) {
				// Intent intent = new Intent(this, StartActivity.class);
				// startActivity(intent);
				this.finish();
			} else {
				if (v.getId() == R.id.checkButton) {
					checkInput();
				} else {
					// ist vorgabebutton gedrueckt wurden?
					for (int i = 0; i < playfieldSize; ++i) {
						boolean horizontal;
						if ((horizontal = (v == vorgabenHorizontal[i]))
								|| v == vorgabenVertikal[i]) {
							showSumDialog(i, horizontal);
						}
					}

				}
			}
		}
	}

	private void showSumDialog(int i, boolean horizontal) {
		FragmentManager fm = getFragmentManager();
		SummenDialog summenDialog = new SummenDialog();
		summenDialog.setContext(this, i, horizontal);
		summenDialog.show(fm, "enter sum");
	}

}
