package me.cexi.tweetmaster;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class BlisdWebViewController extends Activity implements OnClickListener {
	/** Called when the activity is first created. */
	WebView webview;
	ImageButton back, forward, refresh;
	RelativeLayout webview_top_menu;
	// Button backward;
	private String mCursiveFontFamily = "cursive";

	@Override
	public void onCreate(Bundle icicle) {

		super.onCreate(icicle);
		
		/** Adds Progrss bar Support **/
		this.getWindow().requestFeature(android.view.Window.FEATURE_PROGRESS);
		setContentView(R.layout.webviewlayout);
		/** Makes Progress bar Visible **/
		getWindow().setFeatureInt(android.view.Window.FEATURE_PROGRESS,
				android.view.Window.PROGRESS_VISIBILITY_ON);
		/** getting extras from from intent . If no values present setting up default value**/
		Bundle extras = getIntent().getExtras();

		String url = extras.getString("url");
		if (url.equals("")) {
			url = "https://twitter.com/#!/johnstack";
		}
		
		/** Configuring Web View **/
		
		webview = (WebView) findViewById(R.id.webview);

		webview_top_menu = (RelativeLayout) findViewById(R.id.webview_top_menu);
		webview_top_menu.setVisibility(View.GONE);

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
				MyActivity.setTitle("   Loading...");
				MyActivity.setProgress(progress * 100); /** Make the bar
														* disappear after URL
														* is loaded **/

				/**   Return the app name after finish loading **/
				if (progress == 100) {
					MyActivity.setTitle(R.string.app_title);
					webview_top_menu.setVisibility(View.VISIBLE);
				}
			}
		});
		
		/**Setting custom Top bar for webview**/
		
		back = (ImageButton) findViewById(R.id.backward);
		back.setOnClickListener(this);

		forward = (ImageButton) findViewById(R.id.forward);
		forward.setOnClickListener(this);

		refresh = (ImageButton) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);

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
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.backward:
			webview.goBack();
			return;
		case R.id.forward:
			webview.goForward();
			return;
		case R.id.refresh:
			webview.reload();
			return;
		}
	}

	public String getmCursiveFontFamily() {
		return mCursiveFontFamily;
	}

	public void setmCursiveFontFamily(String mCursiveFontFamily) {
		this.mCursiveFontFamily = mCursiveFontFamily;
	}
}
