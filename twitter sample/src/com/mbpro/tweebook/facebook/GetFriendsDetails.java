package com.mbpro.tweebook.facebook;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.Facebook;
import com.facebook.android.library.FacebookError;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookProfile.AlbumRequestListener;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class GetFriendsDetails extends BaseActivity {
	ProgressDialog dialog;
	FacebookSegmentedTabMenu fbTab;
	private static final String TAG = "Facebook Profile";

	private TextView friend_full_name, friend_gender_text, friend_location_text,
			friends_label, friend_birthday_text, friend_relationship_text;
	private ImageView friend_profile_pic;
	private Handler mHandler;
	private SharedPreferences prefs;
	Intent friendIntent;
	private TextView albums_label, albums_6;
	private static String profileID;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		setContentView(R.layout.facebook_friends_profile);
		getActivityHelper().setupActionBar(getTitle(), 0);
		fbTab = new FacebookSegmentedTabMenu(this);
		fbTab.setSeletected(3);
		friend_full_name = (TextView) findViewById(R.id.friend_full_name);
		friend_profile_pic = (ImageView) findViewById(R.id.friend_profile_pic);
		friend_gender_text = (TextView) findViewById(R.id.friend_gender_text);
		friend_location_text = (TextView) findViewById(R.id.friend_location_text);
		friend_birthday_text = (TextView) findViewById(R.id.friend_birthday_text);
		friend_relationship_text = (TextView) findViewById(R.id.friend_relationship_text);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
				
		friendIntent = getIntent();
		if(friendIntent.hasExtra("profile_id")){
			profileID = friendIntent.getStringExtra("profile_id");
			getProfileDetails(profileID);
		}
		albums_label = (TextView) findViewById(R.id.albums_label);
		albums_6 = (TextView) findViewById(R.id.albums_6);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fbTab.setSeletected(3);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		fbTab.setSeletected(3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.face_book_menu, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			getProfileDetails(profileID);
			return true;
		case R.id.menu_back_to_t:
			if (TwitterUtils.isAuthenticated(prefs)) {
				Intent i = new Intent(this, TwitterHomeActivity.class);
				startActivity(i);
			} else {
				Intent i = new Intent(this, PrepareRequestTokenActivity.class);
				startActivity(i);
			}
			return true;
		case R.id.menu_logout:
			SessionEvents.onLogoutBegin();
			AsyncFacebookRunner asyncRunner = new AsyncFacebookRunner(
					Utility.mFacebook);
			asyncRunner.logout(this, new LogoutRequestListener());
			return true;
		}
		return super.onOptionsItemSelected(item);
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
					final Intent intent1 = new Intent(GetFriendsDetails.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					GetFriendsDetails.this.startActivity(intent1);
				}
			});
		}
	}

	public void getProfileDetails(String profileID) {
		String rootString = "me";
		if (!TextUtils.isEmpty(rootString)) {
			dialog = ProgressDialog.show(this, "", "Loading Profile Details",
					true, true);
			Bundle params = new Bundle();
			params.putString(
					"fields",
					"id,name,gender,email,location,relationship_status,picture,birthday,education,work");
			Utility.mAsyncRunner.request(profileID, params,
					new ProfileRequestListener());

		}
	}

	public void getAlbumDetails(String profileID) {
		String rootString = profileID+"/albums";
		if (!TextUtils.isEmpty(rootString)) {
			Bundle param_albums = new Bundle();
			param_albums.putString("fields", "id,name,count,type,cover_photo");
			Utility.mAsyncRunner.request(profileID+"/albums", param_albums,
					new AlbumRequestListener());
		}
	}

	

	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class ProfileRequestListener extends BaseRequestListener {
		String location = "";
		String gender = "";
		String relationship_status = "";
		String birthday = "";
		String hometown = "";
		String education = "";
		String picURL = "";
		String name = "";
		String email = "";

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();

			JSONObject jsonObject;
			try {
				// System.out.println("response ====" + response);
				jsonObject = new JSONObject(response);

				if (jsonObject.has("gender")) {
					gender = jsonObject.getString("gender");
				}

				if (jsonObject.has("name")) {
					name = jsonObject.getString("name");
				}
				if (jsonObject.has("picture")) {
					picURL = jsonObject.getString("picture");
				}
				if (jsonObject.has("location")) {
					location = jsonObject.getJSONObject("location").getString(
							"name");
				}
				if (jsonObject.has("relationship_status")) {
					relationship_status = jsonObject
							.getString("relationship_status");
				} else {
					relationship_status = "nil";
				}
				if (jsonObject.has("education")) {
					JSONArray educationArray = jsonObject
							.getJSONArray("education");
					for (int i = 0; i < educationArray.length(); i++) {
						// System.out.println("educationArray.getJSONObject(i) ===="
						// + educationArray.getJSONObject(i));
						if (educationArray.getJSONObject(i).has("school")) {
							education += educationArray.getJSONObject(i)
									.getJSONObject("school").getString("name");
						}
					}
				} else {
					education = "nil";
				}

				if (jsonObject.has("birthday")) {
					String[] birtthDaySplitted = birthday.split("/");

					SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
					SimpleDateFormat df2 = new SimpleDateFormat("MMM dd, yyyy");
					try {
						birthday = df2.format(df1.parse(jsonObject
								.getString("birthday")));
					} catch (java.text.ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// System.out.println("birthday ====" + birthday);

				}
				Utility.userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					@Override
					public void run() {

						friend_full_name.setText(name);
						friend_gender_text.setText(gender);
						friend_location_text.setText(location);
						friend_birthday_text.setText(birthday);
						friend_relationship_text.setText(relationship_status);
						friend_profile_pic.setImageBitmap(Utility
								.getBitmap(picURL));
						getAlbumDetails(profileID);
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();

		}

	}
	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class AlbumRequestListener extends BaseRequestListener {
		JSONArray albumsArray;
		String albumsCount = "Albums";
		Bitmap albumPic1 = null;
		Bitmap albumPic2 = null;
		Bitmap albumPic3 = null;
		Bitmap albumPic4 = null;
		Bitmap albumPic5 = null;

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();

			JSONObject jsonObject;
			try {
				// System.out.println("response ====" + response);
				jsonObject = new JSONObject(response);
				if (jsonObject.has("data")) {
					albumsArray = jsonObject.getJSONArray("data");

				}
				final Bundle params = new Bundle();
				params.putString(Facebook.TOKEN,
						Utility.mFacebook.getAccessToken());

				mHandler.post(new Runnable() {
					@Override
					public void run() {

						if (albumsArray.length() > 0) {
							albumsCount = "Albums (" + albumsArray.length()+ ")";
						} else {
							albumsCount = "You have no albums";
						}
						albums_label.setText(albumsCount);
						
						if (albumsArray.length() > 0) {
							albums_6.setVisibility(View.VISIBLE); // ADDED
							SpannableString content = new SpannableString(
									"View Album List");
							content.setSpan(new UnderlineSpan(), 0,
									content.length(), 0);
							albums_6.setText(content);
							albums_6.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									Intent albumsIntent = new Intent()
											.setClass(GetFriendsDetails.this,
													FacebookListAlbums.class);
									albumsIntent.putExtra("API_RESPONSE",
											response);
									startActivity(albumsIntent);

								}
							});

						}

					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();

		}

	}
	
}
