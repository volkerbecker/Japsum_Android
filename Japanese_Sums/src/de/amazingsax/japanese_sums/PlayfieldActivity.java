package de.amazingsax.japanese_sums;

import java.lang.ref.Reference;
import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
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
	ProgressDialog progressdialog;
	Playfield riddle;
	
	
	int playfieldSize;
	int maxNumber;
	int maxSum;
	boolean playAsGame; // wahr wenn zum spielen genutzt, falsch wenn nur zum l�sen genutzt
	
	//final int zellwidth=100;
	//final int zellhight=100;  // to do, zellgroesse dynamisch bestimmen
	
	
	ArrayList<byte[]> hblocks;
	ArrayList<byte[]> vblocks; // fuer die intialisierung des solvers
	
	GridLayout playField;
	
	Button[][] eintraege;
	Button[] vorgabenHorizontal;
	Button[] vorgabenVertikal;
	
	byte[][] values;
	
	ImageView linksoben;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		refereceForstaticHandler = this;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_playfield);
		Intent intent = getIntent();
		Bundle data = intent.getExtras();
		
		playfieldSize = data.getInt("playfieldSize");  // Spielfeldgr��e setzen
		maxNumber = data.getInt("maxNumber"); //Maximalwert setzen
		playAsGame=data.getBoolean("playAsGame",false);
		maxSum = maxNumber * (maxNumber + 1) / 2; // groesstmoegliche summe

		handler = new calculationReadyHandler();
		linksoben = new ImageView(this);
		linksoben.setImageDrawable(getResources().getDrawable(
				R.drawable.ic_launcher));

		playField = (GridLayout) findViewById(R.id.playfield);
		eintraege = new Button[playfieldSize][playfieldSize];
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
			vorgabenVertikal[i].setOnClickListener(this);
			GridLayout.LayoutParams buttonparams = new GridLayout.LayoutParams();
			buttonparams.setGravity(Gravity.FILL_VERTICAL);
			playField.addView(vorgabenVertikal[i], buttonparams);
		}

		// Buttons f�r horizontale summen hinzuf�gen
		for (int i = 0; i < playfieldSize; ++i) {
			vorgabenHorizontal[i] = new Button(this);
			vorgabenHorizontal[i].setGravity(Gravity.RIGHT
					| Gravity.CENTER_VERTICAL);
			// vorgabenHorizontal[i].setHeight(zellhight);
			vorgabenHorizontal[i].setText(R.string.leereVorgabe);
			GridLayout.LayoutParams buttonparams = new GridLayout.LayoutParams();
			buttonparams.setGravity(Gravity.FILL_HORIZONTAL);
			playField.addView(vorgabenHorizontal[i], buttonparams);
			vorgabenHorizontal[i].setOnClickListener(this);

			for (int j = 0; j < playfieldSize; ++j) {
				eintraege[i][j] = new Button(this);
				eintraege[i][j].setText(R.string.leererEintrag);
				eintraege[i][j].setBackgroundResource(R.drawable.rectangle);
				// eintraege[i][j].setWidth(zellwidth);
				// eintraege[i][j].setWidth(zellhight);
				playField.addView(eintraege[i][j]);
			}
		}
		hblocks = new ArrayList<byte[]>();
		vblocks = new ArrayList<byte[]>();
		for (int i = 0; i < playfieldSize; ++i) {
			hblocks.add(null);
			vblocks.add(null);
		}

	}
	
	public int getmaxSum() {
		return maxSum;
	}
	
	public void setVorgaben(boolean horizontal, int werte[],int zeileorSpalte) {
		if(zeileorSpalte<playfieldSize) {
			Button tempbutton = horizontal ? vorgabenHorizontal[zeileorSpalte] : vorgabenVertikal[zeileorSpalte];
			tempbutton.setText(intArray2String(werte, horizontal));
			byte[] werteb=new byte[werte.length];
			for(int i=0;i<werte.length;++i) {
				werteb[i]=(byte)werte[i];
			}
			if(horizontal) {
				hblocks.set(zeileorSpalte,werteb);
			} else {
				vblocks.set(zeileorSpalte,werteb);
			}				
		}
	}
	
	private String intArray2String(int[] intarray,boolean horizontal) {
		String s = new String();
		char trennzeichen= horizontal ? ' ' : '\n';
		for(int i=0;i<intarray.length;++i) {
			s+=String.valueOf(intarray[i]);
			if(i!=intarray.length-1) s+=trennzeichen;
		}
		return s;
	}
	
	private void solveRiddle() {
		//erstmal testen ob alles ausgefuellt ist
		
		for(int i=0;i<playfieldSize;++i) {
			if(hblocks.get(i)==null || vblocks.get(i)==null) {
				Toast toast = Toast.makeText(this,R.string.playfieldnotOkay,Toast.LENGTH_SHORT);
				toast.show();
				return;
			}
		} //
		
		
		progressdialog = new ProgressDialog(this);
		progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressdialog.setMessage(getResources().getString(R.string.progressdialog));
		progressdialog.setMax(playfieldSize*playfieldSize);
		progressdialog.setCancelable(false);
		progressdialog.setOwnerActivity(this);
		progressdialog.show();
	
		riddle = new Playfield((byte)playfieldSize, (byte)maxNumber, hblocks, vblocks,progressdialog,handler);
		if(riddle.isSolveable()) {
			riddle.start();
		} else {
			showsolution();
			progressdialog.dismiss();
		}
	}

	private void showsolution() {
		values = riddle.getEntries();
		if (riddle.isSolveable() && riddle.isSolved()) {
			for (int i = 0; i < playfieldSize; ++i) {
				for (int j = 0; j < playfieldSize; ++j) {
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
				}

			}
		} else {
			if (!riddle.isSolveable()) {
				Toast toast = Toast.makeText(this, R.string.thereisnoSolution,
						Toast.LENGTH_LONG); // todo -> alert Dialog
				toast.show();
			} else {
				Toast toast = Toast.makeText(this, R.string.noSolutionFound,
						Toast.LENGTH_LONG); // todo -> alert Dialog
				toast.show();
			}
		}

	}
	
	public void onClick(View v){
		if (v.getId() == R.id.solveButton2) {
			solveRiddle();
		} else {
			if (v.getId() == R.id.backButton) {
				Intent intent = new Intent(this, StartActivity.class);
				startActivity(intent);
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
	
	private void showSumDialog(int i,boolean horizontal) {
        FragmentManager fm = getFragmentManager();
        SummenDialog summenDialog = new SummenDialog();
        summenDialog.setContext(this,i,horizontal);
        summenDialog.show(fm, "enter sum");
    }

}
