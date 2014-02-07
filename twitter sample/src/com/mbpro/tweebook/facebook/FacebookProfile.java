package com.mbpro.tweebook.facebook;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.Facebook;
import com.facebook.android.library.FacebookError;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.AdActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookProfile extends BaseActivity {
	ProgressDialog dialog;
	FacebookSegmentedTabMenu fbTab;
	private static final String TAG = "Facebook Profile";

	private TextView user_full_name, user_gender_text, user_location_text,
			friends_label, user_birthday_text, user_relationship_text,
			user_email_text, albums_label;
	private ImageView user_profile_pic, friends_1, friends_2, friends_3,
			friends_4, friends_5, albums_1, albums_2, albums_3, albums_4,
			albums_5;
	private Handler mHandler;
	private SharedPreferences prefs;
	private TextView friends_11, albums_6;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		setContentView(R.layout.facebook_profile);
		getActivityHelper().setupActionBar(getTitle(), 0);
		fbTab = new FacebookSegmentedTabMenu(this);
		fbTab.setSeletected(3);
		user_full_name = (TextView) findViewById(R.id.user_full_name);
		user_profile_pic = (ImageView) findViewById(R.id.user_profile_pic);
		user_gender_text = (TextView) findViewById(R.id.user_gender_text);
		user_location_text = (TextView) findViewById(R.id.user_location_text);
		user_birthday_text = (TextView) findViewById(R.id.user_birthday_text);
		user_relationship_text = (TextView) findViewById(R.id.user_relationship_text);
		user_email_text = (TextView) findViewById(R.id.user_email_text);

		friends_label = (TextView) findViewById(R.id.friends_label);
		albums_label = (TextView) findViewById(R.id.albums_label);

		friends_1 = (ImageView) findViewById(R.id.friends_1);

		friends_2 = (ImageView) findViewById(R.id.friends_2);
		friends_3 = (ImageView) findViewById(R.id.friends_3);
		friends_4 = (ImageView) findViewById(R.id.friends_4);
		friends_5 = (ImageView) findViewById(R.id.friends_5);

		friends_11 = (TextView) findViewById(R.id.friends_11);

		albums_1 = (ImageView) findViewById(R.id.albums_1);
		albums_2 = (ImageView) findViewById(R.id.albums_2);
		albums_3 = (ImageView) findViewById(R.id.albums_3);
		albums_4 = (ImageView) findViewById(R.id.albums_4);
		albums_5 = (ImageView) findViewById(R.id.albums_5);
		albums_6 = (TextView) findViewById(R.id.albums_6);

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		getProfileDetails();
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
			getProfileDetails();
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
					final Intent intent1 = new Intent(FacebookProfile.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookProfile.this.startActivity(intent1);
				}
			});
		}
	}

	public void getProfileDetails() {
		String rootString = "me";
		if (!TextUtils.isEmpty(rootString)) {
			dialog = ProgressDialog.show(this, "", "Loading Profile Details",
					true, true);
			Bundle params = new Bundle();
			params.putString(
					"fields",
					"id,name,gender,email,location,relationship_status,picture,birthday,education,work");
			Utility.mAsyncRunner.request("me", params,
					new ProfileRequestListener());

		}
	}

	public void getFreindsDetails() {
		String rootString = "me/friends";
		if (!TextUtils.isEmpty(rootString)) {
			Bundle param_friends = new Bundle();
			param_friends
					.putString("fields",
							"id,name,gender,email,location,relationship_status,picture,birthday");
			Utility.mAsyncRunner.request("me/friends", param_friends,
					new FriendRequestListener());
		}
	}

	public void getAlbumDetails() {
		String rootString = "me/albums";
		if (!TextUtils.isEmpty(rootString)) {
			Bundle param_albums = new Bundle();
			param_albums.putString("fields", "id,name,count,type,cover_photo");
			Utility.mAsyncRunner.request("me/albums", param_albums,
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

				if (jsonObject.has("email")) {
					email = jsonObject.getString("email");
				}
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

						user_full_name.setText(name);
						user_gender_text.setText(gender);
						user_location_text.setText(location);
						user_birthday_text.setText(birthday);
						user_relationship_text.setText(relationship_status);
						user_email_text.setText(email);
						user_profile_pic.setImageBitmap(Utility
								.getBitmap(picURL));
						getFreindsDetails();
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
	public class FriendRequestListener extends BaseRequestListener {
		Bitmap friendPic1 = null;
		Bitmap friendPic2 = null;
		Bitmap friendPic3 = null;
		Bitmap friendPic4 = null;
		Bitmap friendPic5 = null;

		String friendsCount = "Friends";
		JSONArray friendsArray;
		ProgressBar progressBar1;

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();

			JSONObject jsonObject;
			try {
				// System.out.println("response ====" + response);
				jsonObject = new JSONObject(response);
				if (jsonObject.has("data")) {
					friendsArray = jsonObject.getJSONArray("data");

				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (friendsArray.length() > 0) {
							try {
								if (friendsArray.getJSONObject(0).has("id")) {
									friendPic1 = Utility
											.getBitmap("http://graph.facebook.com/"
													+ friendsArray
															.getJSONObject(0)
															.getString("id")
													+ "/picture");
									friends_1.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String friendId;
											try {
												friendId = friendsArray.getJSONObject(0).getString("id");
												if(friendId != null){
													Intent i = new Intent(FacebookProfile.this,GetFriendsDetails.class);
													i.putExtra("profile_id", friendId);
													startActivity(i);
												}
											} catch (JSONException e) {
												
											}

										}
									});

								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								if (friendsArray.getJSONObject(1).has("id")) {
									friendPic2 = Utility
											.getBitmap("http://graph.facebook.com/"
													+ friendsArray
															.getJSONObject(1)
															.getString("id")
													+ "/picture");
									friends_2.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String friendId;
											try {
												friendId = friendsArray.getJSONObject(1).getString("id");
												if(friendId != null){
													Intent i = new Intent(FacebookProfile.this,GetFriendsDetails.class);
													i.putExtra("profile_id", friendId);
													startActivity(i);
												}
											} catch (JSONException e) {
												
											}

										}
									});

								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								if (friendsArray.getJSONObject(2).has("id")) {
									friendPic3 = Utility
											.getBitmap("http://graph.facebook.com/"
													+ friendsArray
															.getJSONObject(2)
															.getString("id")
													+ "/picture");
									friends_3.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String friendId;
											try {
												friendId = friendsArray.getJSONObject(2).getString("id");
												if(friendId != null){
													Intent i = new Intent(FacebookProfile.this,GetFriendsDetails.class);
													i.putExtra("profile_id", friendId);
													startActivity(i);
												}
											} catch (JSONException e) {
												
											}

										}
									});
								}
							} catch (JSONException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							try {
								if (friendsArray.getJSONObject(3).has("id")) {
									friendPic4 = Utility
											.getBitmap("http://graph.facebook.com/"
													+ friendsArray
															.getJSONObject(3)
															.getString("id")
													+ "/picture");
									
									friends_4.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String friendId;
											try {
												friendId = friendsArray.getJSONObject(3).getString("id");
												if(friendId != null){
													Intent i = new Intent(FacebookProfile.this,GetFriendsDetails.class);
													i.putExtra("profile_id", friendId);
													startActivity(i);
												}
											} catch (JSONException e) {
												
											}

										}
									});
									
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								if (friendsArray.getJSONObject(4).has("id")) {
									friendPic5 = Utility
											.getBitmap("http://graph.facebook.com/"
													+ friendsArray
															.getJSONObject(4)
															.getString("id")
													+ "/picture");
									friends_5.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											final String friendId;
											try {
												friendId = friendsArray.getJSONObject(4).getString("id");
												if(friendId != null){
													Intent i = new Intent(FacebookProfile.this,GetFriendsDetails.class);
													i.putExtra("profile_id", friendId);
													startActivity(i);
												}
											} catch (JSONException e) {
												
											}

										}
									});
									
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							friendsCount = "Friends (" + friendsArray.length()
									+ ")";
						} else {
							friendsCount = "You have no friends";
						}

						friends_label.setText(friendsCount);
						// progressBar1.setVisibility(View.GONE); //ADDED
						friends_1.setVisibility(View.VISIBLE); // ADDED
						friends_1.setImageBitmap(friendPic1);
						
						friends_2.setVisibility(View.VISIBLE); // ADDED
						friends_2.setImageBitmap(friendPic2);
						friends_3.setVisibility(View.VISIBLE); // ADDED
						friends_3.setImageBitmap(friendPic3);
						friends_4.setVisibility(View.VISIBLE); // ADDED
						friends_4.setImageBitmap(friendPic4);
						friends_5.setVisibility(View.VISIBLE); // ADDED
						friends_5.setImageBitmap(friendPic5);

						if (friendsArray.length() > 5) {
							friends_11.setVisibility(View.VISIBLE); // ADDED
							SpannableString content = new SpannableString(
									"View All");
							content.setSpan(new UnderlineSpan(), 0,
									content.length(), 0);
							friends_11.setText(content);
							friends_11
									.setOnClickListener(new View.OnClickListener() {
										public void onClick(View v) {
											Intent friendsIntent = new Intent()
													.setClass(
															FacebookProfile.this,
															FacebookListFriends.class);
											friendsIntent.putExtra(
													"API_RESPONSE", response);
											startActivity(friendsIntent);

										}
									});

						}

						getAlbumDetails();
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
							/*
							 * try { if(albumsArray.getJSONObject(0).has("id")){
							 * try { if
							 * (albumsArray.getJSONObject(0).has("cover_photo"))
							 * { albumPic1 = Utility
							 * .getBitmap("https://graph.facebook.com/"
							 * +albumsArray.getJSONObject(0).getString("id") +
							 * "/picture?type=thumbnail&access_token="
							 * +Utility.mFacebook.getAccessToken()); }
							 * //albumPic1 = Utility
							 * //.getBitmap(decode(Utility.
							 * mFacebook.request(albumsArray
							 * .getJSONObject(0).getString("id")+ "/picture")));
							 * } catch (Exception e) { // TODO Auto-generated
							 * catch block e.printStackTrace(); }
							 * 
							 * } } catch (JSONException e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 * try { albumPic2 = Utility
							 * .getBitmap("https://graph.facebook.com/"
							 * +albumsArray.getJSONObject(1).getString("id") +
							 * "/picture?type=thumbnail&access_token="
							 * +Utility.mFacebook.getAccessToken()); } catch
							 * (JSONException e) { // TODO Auto-generated catch
							 * block e.printStackTrace(); } try {
							 * if(albumsArray.length() >1){ albumPic3 = Utility
							 * .
							 * getBitmap("https://graph.facebook.com/"+albumsArray
							 * .getJSONObject(2).getString("id") +
							 * "/picture?type=thumbnail&access_token="
							 * +Utility.mFacebook.getAccessToken());
							 * 
							 * } } catch (JSONException e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 * try { if(albumsArray.length() > 2){ albumPic4 =
							 * Utility
							 * .getBitmap("https://graph.facebook.com/"+albumsArray
							 * .getJSONObject(3).getString("id") +
							 * "/picture?type=thumbnail&access_token="
							 * +Utility.mFacebook.getAccessToken());
							 * 
							 * } } catch (JSONException e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 * try { if(albumsArray.length() >3){ albumPic5 =
							 * Utility
							 * .getBitmap("https://graph.facebook.com/"+albumsArray
							 * .getJSONObject(4).getString("id") +
							 * "/picture?type=thumbnail&access_token="
							 * +Utility.mFacebook.getAccessToken());
							 * 
							 * } } catch (JSONException e) { // TODO
							 * Auto-generated catch block e.printStackTrace(); }
							 */

							albumsCount = "Albums (" + albumsArray.length()
									+ ")";
						} else {
							albumsCount = "You have no albums";
						}
						albums_label.setText(albumsCount);
						/*
						 * albums_1.setVisibility(View.VISIBLE); //ADDED
						 * albums_1.setImageBitmap(albumPic1);
						 * albums_2.setVisibility(View.VISIBLE); //ADDED
						 * albums_2.setImageBitmap(albumPic2);
						 * albums_3.setVisibility(View.VISIBLE); //ADDED
						 * albums_3.setImageBitmap(albumPic3);
						 * albums_4.setVisibility(View.VISIBLE); //ADDED
						 * albums_4.setImageBitmap(albumPic4);
						 * albums_5.setVisibility(View.VISIBLE); //ADDED
						 * albums_5.setImageBitmap(albumPic5);
						 */
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
											.setClass(FacebookProfile.this,
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

	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

		int width = bm.getWidth();

		int height = bm.getHeight();

		float scaleWidth = ((float) newWidth) / width;

		float scaleHeight = ((float) newHeight) / height;

		// create a matrix for the manipulation

		Matrix matrix = new Matrix();

		// resize the bit map

		matrix.postScale(scaleWidth, scaleHeight);

		// recreate the new Bitmap

		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, true);

		return resizedBitmap;

	}

	private String decode(String url) {
		return url.replace("&amp;", "&");
	}
}
