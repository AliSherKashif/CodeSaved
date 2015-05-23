package com.cygnus.honda;

import com.cygnus.honda.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.Display;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class Splash extends Activity {
	RelativeLayout mainRRBG;
	SharedPreferences prefs;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		mainRRBG = (RelativeLayout) findViewById(R.id.main_rr_bg);
		prefs = getSharedPreferences(LoginActivity.MY_PREFS_NAME, MODE_PRIVATE);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// Do some stuff
			OrientationLandScape();
		}

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// Do some stuff
			OrientationPortrait();
		}

		// thread to show the splash screen for few seconds
		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 3000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					}

					Intent welcomeIntent = new Intent(Splash.this,
							Welcome.class);
					startActivity(welcomeIntent);
					finish();

				} catch (InterruptedException e) { // TODO Auto-generated catch
					// block
					e.printStackTrace();
				} finally {
					finish();
				}
			}
		};
		logoTimer.start();

	}

	private void OrientationPortrait() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.splash_bg_dayp));

		} else {

			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.splash_bg_nightp));

		}

	}

	private void OrientationLandScape() {
		// TODO Auto-generated method stub

		if (ModeCheck()) {
			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.splash_bg_dayl));

		} else {

			mainRRBG.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.splash_bg_nightl));

		}

	}

	private boolean ModeCheck() {
		// TODO return the bool value of remember credentail
		return prefs.getBoolean("appmode", false);

	}

	// ////////////////////// orientation of screens

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen for landscape and portrait
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			OrientationLandScape();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			OrientationPortrait();
		}
	}

}
