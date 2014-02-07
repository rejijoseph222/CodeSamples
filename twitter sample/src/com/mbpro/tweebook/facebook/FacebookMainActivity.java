package com.mbpro.tweebook.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.DialogError;
import com.facebook.android.library.Facebook;
import com.facebook.android.library.Facebook.DialogListener;
import com.facebook.android.library.FacebookError;
import com.facebook.android.library.Util;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.SessionEvents.AuthListener;
import com.mbpro.tweebook.facebook.SessionEvents.LogoutListener;
import com.mbpro.tweebook.ui.BaseActivity;

public class FacebookMainActivity extends BaseActivity implements
		OnItemClickListener {

	/*
	 * Your Facebook Application ID must be set before running this example See
	 * http://www.facebook.com/developers/createapp.php
	 */
	public static final String APP_ID = "419262621422240";

	private LoginButton mLoginButton;
	private TextView mText;
	private ImageView mUserPic;
	private Handler mHandler;
	ProgressDialog dialog;

	private Bundle params;
	private String url, mParentObjectId;
	private String rootString;

	private final static String BASE_GRAPH_URL = "https://graph.facebook.com";

	private JSONObject metadataObject;

	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;

	String[] permissions = { "offline_access", "publish_stream", "user_photos",
			"publish_checkins", "photo_upload", "read_mailbox", "read_stream",
			"user_hometown", "user_birthday", "user_relationship_details",
			"friends_photos", "user_location","friends_birthday","friends_hometown","friends_location","friends_location","friends_relationship_details","friends_work_history"};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see FbAPIs.java");
			return;
		}

		setContentView(R.layout.facebook_tabview);
		mHandler = new Handler();

		// mText = (TextView) FacebookMainActivity.this.findViewById(R.id.txt);
		// mUserPic = (ImageView)
		// FacebookMainActivity.this.findViewById(R.id.user_pic);

		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		// Instantiate the asynrunner object for asynchronous api calls.

		// mLoginButton = (LoginButton) findViewById(R.id.login);

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());
		url = BASE_GRAPH_URL; // Base URL
		params = new Bundle();
		// System.out.println("hello here");
		if (Utility.mFacebook.isSessionValid()) {
			//System.out.println("hello isSessionValid");
		} else {
			Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
			Utility.mFacebook.authorize(this, permissions,
					AUTHORIZE_ACTIVITY_RESULT_CODE, new LoginDialogListener());
		}
		/*
		 * Source Tag: login_tag
		 */
		// mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE,
		// Utility.mFacebook, permissions);
		getActivityHelper().setupActionBar(getTitle(), 0);

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {

		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			break;
		}

		}
	}

	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class graphApiRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			// access token is appended by Facebook object, hence params are
			// added here after request is complete
			if (!params.isEmpty()) {
				url += "?" + Util.encodeUrl(params); // Params
			}
			metadataObject = null;
			params.clear();
			try {
				JSONObject json = Util.parseJson(response);
				if (json.has("metadata")) {
					metadataObject = json.getJSONObject("metadata");
					json.remove("metadata");
				} else {
					metadataObject = null;
				}
				try {
					Intent myIntent = new Intent(getApplicationContext(),
							FacebookListNewsFeeds.class);
					myIntent.putExtra("API_RESPONSE", json.toString(2));
					startActivity(myIntent);
					finish();

				} catch (JSONException e) {
					makeToast("Error: " + e.getMessage());
					return;
				}

			} catch (JSONException e) {

				e.printStackTrace();
			} catch (FacebookError e) {
				// setText(e.getMessage());
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			params.clear();
			metadataObject = null;
		}

	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				final String picURL = jsonObject.getString("picture");
				final String name = jsonObject.getString("name");
				Utility.userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					@Override
					public void run() {

					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	public class FbAPIsAuthListener implements AuthListener {

		@Override
		public void onAuthSucceed() {
			// requestUserData();
		}

		@Override
		public void onAuthFail(String error) {

		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {

		}

		@Override
		public void onLogoutFinish() {

		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {

		Bundle params = new Bundle();
		params.putString("fields", "name, picture");
		Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	private final class LoginDialogListener implements DialogListener {

		@Override
		public void onComplete(Bundle values) {
			SessionEvents.onLoginSuccess();

			rootString = "me/home";
			if (!TextUtils.isEmpty(rootString)) {
				dialog = ProgressDialog.show(FacebookMainActivity.this, "",
						"Loading News Feeds", true, true);
				// params.putString("metadata", "1");
				params.putString("limit", "30");
				params.putString(
						"fields",
						"id,from, picture,link, name,message,caption,likes,comments,type,object_id,actions");
				Utility.mAsyncRunner.request(rootString, params,
						new graphApiRequestListener());
				url += "/" + rootString; // Relative Path provided by you
			}
		}

		@Override
		public void onFacebookError(FacebookError error) {
			//System.out.println("onFacebookError here" + error.getMessage());
			SessionEvents.onLoginError(error.getMessage());
		}

		@Override
		public void onError(DialogError error) {
		//	System.out.println("onError here");
			SessionEvents.onLoginError(error.getMessage());
		}

		@Override
		public void onCancel() {
			SessionEvents.onLoginError("Action Canceled");
		}
	}

	private class LogoutRequestListener extends BaseRequestListener {
		@Override
		public void onComplete(String response, final Object state) {
			/*
			 * callback should be run in the original thread, not the background
			 * thread
			 */
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					SessionEvents.onLogoutFinish();
				}
			});
		}
	}

	private void makeToast(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(FacebookMainActivity.this, msg,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}
