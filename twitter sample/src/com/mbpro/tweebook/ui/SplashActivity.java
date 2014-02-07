package com.mbpro.tweebook.ui;

import java.util.Timer;
import java.util.TimerTask;

import com.mbpro.tweebook.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {
	private long splashDelay = 1500;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				finish();
				Intent hackbookIntent = new Intent().setClass(
						SplashActivity.this, MainActivity.class);
				startActivity(hackbookIntent);
			}
		};

		Timer timer = new Timer();
		timer.schedule(task, splashDelay);
	}
}
