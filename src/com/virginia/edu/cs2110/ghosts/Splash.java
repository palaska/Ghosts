package com.virginia.edu.cs2110.ghosts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		final Intent intent = new Intent(this, ChooseGame.class);
		Thread logoTimer = new Thread() {
			public void run() {
				try {
					sleep(3000);
					startActivity(intent);
				} catch (InterruptedException e) {
				} finally {
					finish();
				}
			}
		};
		logoTimer.start();
	}

}

