package com.virginia.edu.cs2110.ghosts;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ChooseGame extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_game);
		ImageButton newGame = (ImageButton) findViewById(R.id.imageButton1);
		ImageButton savedGame = (ImageButton) findViewById(R.id.imageButton2);
		ImageButton exit = (ImageButton) findViewById(R.id.imageButton3);
		newGame.setOnClickListener(goToNewGame);
		savedGame.setOnClickListener((android.view.View.OnClickListener) goToSavedGame);
		exit.setOnClickListener((android.view.View.OnClickListener) goToExit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_game, menu);
		return true;
	}

	/**
	 * OnClickListener for the New Game Button
	 */
	private View.OnClickListener goToNewGame = new View.OnClickListener(){
		public void onClick(View v) {
			RadioGroup g = (RadioGroup) findViewById(R.id.radioGroup1);
			doButtonNewGame(g);
		}
	};
	
	private View.OnClickListener goToSavedGame = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			doButtonSavedGame();
		}
	};
	
	private View.OnClickListener goToExit = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			doButtonExit();
		}
	};
	
	/**
	 *
	 * @param g - the radio group with a radio button selected
	 */
	private void doButtonNewGame(RadioGroup g){
		Intent i = new Intent(this, MainActivity.class);
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		int id = rg.getCheckedRadioButtonId();
		int d = 0;
		
		switch(id){
			case -1:
				Toast noChoice = Toast.makeText(getApplicationContext(), "Please Choose a Difficulty", Toast.LENGTH_SHORT);
				noChoice.show();
				return;
			case R.id.radio0:
				d = 5;
				break;
			case R.id.radio1:
				d = 7;
				break;
			case R.id.radio2:
				d = 9;
				break;
		}
		
		i.putExtra("difficulty", d);
		i.putExtra("gameType", "newGame");
		startActivity(i);
		}
	
	private void doButtonSavedGame(){
		Intent i = new Intent(this, MainActivity.class);
		i.putExtra("gameType", "savedGame");
		startActivity(i);
	}
	
	private void doButtonExit(){
		this.finish();
	}
}
