package com.mbpro.tweebook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.chartboost.sdk.ChartBoost;
import com.mbpro.tweebook.R;

public class MainActivity extends BaseActivity {

	public static final String TAG_TWITTER = "twitter";
	public static final String TAG_FACEBOOK = "facebook";

	private TabHost mTabHost;
	private TabWidget mTabWidget;

	private TwitterFragment mSessionsFragment;
	private FacebookFragment mVendorsFragment;
	public static boolean ShowAd = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (ShowAd) {
			ShowAd = false;
			// Configure ChartBoost
			ChartBoost _cb = ChartBoost.getSharedChartBoost();
			_cb.setContext(this);
			_cb.setAppId("4f831623f87659720200000d");
			_cb.setAppSignature("4110c15cef4ad6302f0c183420bc2a197c50c8bc");

			// Notify an install
			_cb.install();

			// Load an interstitial
			_cb.loadInterstitial();
		}

		setContentView(R.layout.activity_main);
		getActivityHelper().setupActionBar(getTitle(), 0); // setupActionBar(getTitle(),
															// 0);

		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabWidget = (TabWidget) findViewById(android.R.id.tabs);
		mTabHost.setup();
		setupTwitterTab();
		setupFacebookTab();
		Intent extraIntent = getIntent();
		if (extraIntent.hasExtra("current_tab")) {
			String current_tab = extraIntent.getStringExtra("current_tab");
			if (current_tab.equals("f")) {
				mTabHost.setCurrentTab(1);
			} else {
				mTabHost.setCurrentTab(0);
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupSubActivity();

	}

	/**
	 * Build and add "Twitter" tab.
	 */
	private void setupTwitterTab() {
		// TODO: this is very inefficient and messy, clean it up
		FrameLayout fragmentContainer = new FrameLayout(this);
		fragmentContainer.setId(R.id.fragment_twitter);
		fragmentContainer.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		((ViewGroup) findViewById(android.R.id.tabcontent))
				.addView(fragmentContainer);

		final Intent intent = new Intent(Intent.ACTION_VIEW);

		final FragmentManager fm = getSupportFragmentManager();
		mSessionsFragment = (TwitterFragment) fm.findFragmentByTag("twitter");
		if (mSessionsFragment == null) {
			mSessionsFragment = new TwitterFragment();
			mSessionsFragment.setArguments(intentToFragmentArguments(intent));
			fm.beginTransaction()
					.add(R.id.fragment_twitter, mSessionsFragment, "twitter")
					.commit();
		}

		// Sessions content comes from reused activity
		mTabHost.addTab(mTabHost.newTabSpec(TAG_TWITTER)
				.setIndicator(buildIndicator(R.string.starred_sessions))
				.setContent(R.id.fragment_twitter));
	}

	/**
	 * Build and add "facebook" tab.
	 */
	private void setupFacebookTab() {
		// TODO: this is very inefficient and messy, clean it up
		FrameLayout fragmentContainer = new FrameLayout(this);
		fragmentContainer.setId(R.id.fragment_facebook);
		fragmentContainer.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		((ViewGroup) findViewById(android.R.id.tabcontent))
				.addView(fragmentContainer);

		final Intent intent = new Intent(Intent.ACTION_VIEW);

		final FragmentManager fm = getSupportFragmentManager();

		mVendorsFragment = (FacebookFragment) fm.findFragmentByTag("facebook");
		if (mVendorsFragment == null) {
			mVendorsFragment = new FacebookFragment();
			mVendorsFragment.setArguments(intentToFragmentArguments(intent));
			fm.beginTransaction()
					.add(R.id.fragment_facebook, mVendorsFragment, "facebook")
					.commit();
		}

		// Vendors content comes from reused activity
		mTabHost.addTab(mTabHost.newTabSpec(TAG_FACEBOOK)
				.setIndicator(buildIndicator(R.string.starred_vendors))
				.setContent(R.id.fragment_facebook));

	}

	/**
	 * Build a {@link View} to be used as a tab indicator, setting the requested
	 * string resource as its label.
	 */
	private View buildIndicator(int textRes) {
		final TextView indicator = (TextView) getLayoutInflater().inflate(
				R.layout.tab_indicator, mTabWidget, false);
		indicator.setText(textRes);
		return indicator;
	}

}
