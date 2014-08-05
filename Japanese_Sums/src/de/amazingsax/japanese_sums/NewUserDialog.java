package de.amazingsax.japanese_sums;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class NewUserDialog extends DialogFragment implements OnClickListener {

	Context context;
	String username;
	
	public NewUserDialog(Context context) {
		this.context=context;
	}
	
	class addNewUser implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().setTitle(R.string.usernameText);
		View view = inflater.inflate(R.layout.newuserdialog, container,
				false);
		Button newuserButton = (Button)view.findViewById(R.id.newuserButton);
		Button loginButton = (Button)view.findViewById(R.id.loginButton);
		Button offlineButton = (Button)view.findViewById(R.id.offlineButton);
		
		newuserButton.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		offlineButton.setOnClickListener(this);
		
		
		
		return view;
	}



	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.newuserButton:
			this.dismiss();
			break;
		case R.id.loginButton:
			this.dismiss();
			break;
		case R.id.offlineButton:
			this.dismiss();
		}

	}

}
