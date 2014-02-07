package com.mbpro.tweebook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chartboost.sdk.ChartBoost;

public class AdActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Configure ChartBoost
		ChartBoost _cb = ChartBoost.getSharedChartBoost();
		_cb.setContext(this);
		_cb.setAppId("4f831623f87659720200000d");
		_cb.setAppSignature("4110c15cef4ad6302f0c183420bc2a197c50c8bc");

		// Notify an install
		_cb.install();

		// Load an interstitial
		_cb.loadInterstitial();
		this.finish();
		Intent hackbookIntent = new Intent().setClass(AdActivity.this,
				MainActivity.class);
		startActivity(hackbookIntent);

	}

	/**
	 * Called when the user dismisses the interstitial If you are displaying the
	 * add yourself, dismiss it now.
	 * 
	 * @param interstitialView
	 *            the interstitial view to dismiss
	 */
	public void didDismissInterstitial(View interstitialView) {
		//System.out.println("didDismissInterstitial");
		this.finish();
		Intent hackbookIntent = new Intent().setClass(AdActivity.this,
				MainActivity.class);
		startActivity(hackbookIntent);
	}

	public void didCloseInterstitial(View interstitialView) {
		//System.out.println("didCloseInterstitial");
		this.finish();
		Intent hackbookIntent = new Intent().setClass(AdActivity.this,
				MainActivity.class);
		startActivity(hackbookIntent);
	}

	public void didClickInterstitial(View interstitialView) {
		//System.out.println("didClickInterstitial");
		this.finish();
		Intent hackbookIntent = new Intent().setClass(AdActivity.this,
				MainActivity.class);
		startActivity(hackbookIntent);
	}

}
