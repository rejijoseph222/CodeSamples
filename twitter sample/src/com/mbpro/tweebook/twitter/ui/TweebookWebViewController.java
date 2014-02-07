package com.mbpro.tweebook.twitter.ui;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.ui.BaseActivity;

public class TweebookWebViewController extends BaseActivity  {
	/** Called when the activity is first created. */
	ImageButton back, forward, refresh;
	RelativeLayout webview_top_menu;
	// Button backward;
	private String mCursiveFontFamily = "cursive";
	 private WebView webview;
	  private ProgressBar mLoadingSpinner;
	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);  
		setProgressBarIndeterminateVisibility(true); 
		/** Adds Progrss bar Support **/
		setContentView(R.layout.webviewlayout);
		getActivityHelper().setupActionBar(getTitle(), 0);
		/** Makes Progress bar Visible **/

		/** getting extras from from intent . If no values present setting up default value**/
		Bundle extras = getIntent().getExtras();

		String url = extras.getString("url");
		if (url.equals("")) {
			url = "http://twitter.com";
		}
		//System.out.println("url === "+ url);
		/** Configuring Web View **/
		
		webview = (WebView) findViewById(R.id.webview);

		 mLoadingSpinner = (ProgressBar) findViewById(R.id.loading_spinner);
		webview.setWebViewClient(new HelloWebViewClient());

		WebSettings websettings = webview.getSettings();
		websettings.setJavaScriptEnabled(true);

		websettings.setTextSize(android.webkit.WebSettings.TextSize.LARGER);

		websettings.setFantasyFontFamily(mCursiveFontFamily);
		websettings
				.setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
		webview.loadUrl(url);

		/**  Sets the Chrome Client, and defines the onProgressChanged**/
		/**  This makes the Progress bar be updated.**/
		final Activity MyActivity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				/**  Make the bar disappear after URL is loaded, and changes **/
				/**  string to Loading... **/
				mLoadingSpinner.setVisibility(View.VISIBLE);
				webview.setVisibility(View.INVISIBLE);
				MyActivity.setProgress(progress * 100); /** Make the bar
														* disappear after URL
														* is loaded **/

				/**   Return the app name after finish loading **/
				if (progress == 100) {
					mLoadingSpinner.setVisibility(View.INVISIBLE);
					webview.setVisibility(View.VISIBLE);
					
				}
			}
		});
		
		
	}

	/** Impementing Override Methods for webview**/
	private class HelloWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	}
	
	/** Added Custom menu for webview top bar.
	 *  Setting Onclick for menu**/
	

	public String getmCursiveFontFamily() {
		return mCursiveFontFamily;
	}

	public void setmCursiveFontFamily(String mCursiveFontFamily) {
		this.mCursiveFontFamily = mCursiveFontFamily;
	}
}
