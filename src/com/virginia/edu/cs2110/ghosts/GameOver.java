package com.virginia.edu.cs2110.ghosts;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioGroup;

public class GameOver extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		ImageButton gameOver = (ImageButton) findViewById(R.id.imageButton1);
		gameOver.setOnClickListener(goToMenu);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_over, menu);
		return true;
	}
	
	private View.OnClickListener goToMenu = new View.OnClickListener(){
		public void onClick(View v) {
			doButtonMenu();
		}
	};
	
	private void doButtonMenu(){
		startActivity(new Intent(this, ChooseGame.class));
	}

}

