package de.amazingsax.japanese_sums;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

public class StartActivity extends Activity implements OnClickListener {
	
	final int maxplayfieldSize=15; // gibt eignetlich keinen zwingenden Grund
	final int maxnumber=9; // aber wenn zweistellige Zahlen erlaubt waeren wirds un�bersichtlich
	
	String username;
	boolean online=false;
	
	NumberPicker pickerPlayfieldSize;
	NumberPicker pickerMaxNumber;
	
	Button playButton;
	Button solveButton;
	
	OnValueChangeListener valueChangeListener = new OnValueChangeListener() {

		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			switch (picker.getId()) {
			case R.id.playfieldSizePicker:
				int newMaxNumberValue = newVal > 10 ? 9 : newVal - 1;
				pickerMaxNumber.setValue(newMaxNumberValue);
				// pickerMaxNumber.setMaxValue(newMaxNumberValue); // Die
				// maximale Ziffernanzahl darf nicht groesser als das Spielfeld
				// sein
			} // Eigentlich eine unn�tige einschr�nkung - deshalb auskommentiert
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		pickerPlayfieldSize=(NumberPicker)findViewById(R.id.playfieldSizePicker);
		pickerMaxNumber=(NumberPicker)findViewById(R.id.maxNumberPicker);
		
		//Voreingestellte Werte setzen
		pickerPlayfieldSize.setMinValue(3);
		pickerPlayfieldSize.setMaxValue(maxplayfieldSize);
		pickerPlayfieldSize.setValue(6);
		pickerPlayfieldSize.setWrapSelectorWheel(false);
		pickerPlayfieldSize.setOnValueChangedListener(valueChangeListener);
		
		pickerMaxNumber.setMinValue(1);
		pickerMaxNumber.setMaxValue(maxnumber);
		pickerMaxNumber.setValue(5);
		pickerMaxNumber.setWrapSelectorWheel(false);
		
		playButton=(Button)findViewById(R.id.newGameButton);
		solveButton=(Button)findViewById(R.id.solveButton2);
		playButton.setOnClickListener(this);
		solveButton.setOnClickListener(this);
		
		Runtime rt = Runtime.getRuntime();
		long maxMemory = rt.maxMemory();
		Log.v("amazing", "maxMemory:" + Long.toString(maxMemory));
		
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
		Log.v("amazing", "memoryClass:" + Integer.toString(memoryClass));
		
		loadSettings();
		if(username==null && online == true ) {
			showNewUserDialog();
		}		
	}
	
	

	private void showNewUserDialog() {
		FragmentManager fm = getFragmentManager();
		NewUserDialog newUser= new NewUserDialog(this);
		newUser.setCancelable(false);
		newUser.show(fm, "new user");		
	}



	@Override
	protected void onResume() {
		super.onResume();
		TextView tv = (TextView)findViewById(R.id.highscores);
		tv.setText(Integer.toString(readHighscore()));
	}
	
	private void loadSettings() {
		SharedPreferences pref = getSharedPreferences("GAME",0);
		username=pref.getString("USERNAME",null);
		online=pref.getBoolean("ONLINE",true);
	}



	private int readHighscore() {
		SharedPreferences pref = getSharedPreferences("GAME",0);
		return pref.getInt("HIGHSCORE",0);
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1) {
			if(resultCode>readHighscore()) {
				writeHighscore(resultCode);
			}
		}
	}



	private void writeHighscore(int highscore) {
		SharedPreferences pref=getSharedPreferences("GAME",0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt("HIGHSCORE", highscore);
		editor.commit();
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.solveButton2:
			Intent intent = new Intent(this,PlayfieldActivity.class);
			intent.putExtra("playfieldSize",pickerPlayfieldSize.getValue());
			intent.putExtra("maxNumber",pickerMaxNumber.getValue());
			intent.putExtra("playAsGame",false);
			startActivity(intent); // Activity Spielfeld als l�ser starten.
			break;
		case R.id.newGameButton:
			Intent intent2 = new Intent(this,PlayfieldActivity.class);
			intent2.putExtra("playfieldSize",pickerPlayfieldSize.getValue());
			intent2.putExtra("maxNumber",pickerMaxNumber.getValue());
			intent2.putExtra("playAsGame",true);
			startActivityForResult(intent2, 1); // Activity Spielfeld als R�tsel starten
//			Toast toast = Toast.makeText(this,R.string.notimplementes,Toast.LENGTH_LONG);
//			toast.show();
			break;
		default:
			break;
		}
		
	}
}
