package de.amazingsax.japanese_sums;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter.LengthFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

public class StartActivity extends Activity implements OnClickListener {
	
	final int maxplayfieldSize=11; // gibt eignetlich keinen zwingenden Grund
	final int maxnumber=9; // aber wenn zweistellige Zahlen erlaubt waeren wirds unübersichtlich
	
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
			} // Eigentlich eine unnötige einschränkung - deshalb auskommentiert
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
		pickerPlayfieldSize.setMaxValue(11);
		pickerPlayfieldSize.setValue(6);
		pickerPlayfieldSize.setWrapSelectorWheel(false);
		pickerPlayfieldSize.setOnValueChangedListener(valueChangeListener);
		
		pickerMaxNumber.setMinValue(1);
		pickerMaxNumber.setMaxValue(9);
		pickerMaxNumber.setValue(5);
		pickerMaxNumber.setWrapSelectorWheel(false);
		
		playButton=(Button)findViewById(R.id.newGameButton);
		solveButton=(Button)findViewById(R.id.solveButton2);
		playButton.setOnClickListener(this);
		solveButton.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.solveButton2:
			Intent intent = new Intent(this,PlayfieldActivity.class);
			intent.putExtra("playfieldSize",pickerPlayfieldSize.getValue());
			intent.putExtra("maxNumber",pickerMaxNumber.getValue());
			intent.putExtra("playAsGame",false);
			startActivity(intent); // Seite mit Spielfeld starten.
			break;
		case R.id.newGameButton:
			Toast to = Toast.makeText(this,R.string.notimplementes,Toast.LENGTH_SHORT);
			to.show();

		default:
			break;
		}
		
	}
}
