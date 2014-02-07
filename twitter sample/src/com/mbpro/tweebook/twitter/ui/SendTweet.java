package com.mbpro.tweebook.twitter.ui;

import java.io.File;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.twitter.Constants;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class SendTweet extends BaseActivity {
	TwitterSegmentedTabMenu tweetTab;
	Button btn_send_tweet, btn_pick_image;
	private SharedPreferences prefs;
	private ConfigurationBuilder cb;
	EditText et_tweet_text;
	private static final int PICK_IMAGE = 1;
	private static final int CAPTURE = 2;
	private static final int REMOVE_BACKGROUND = 3;
	private Bitmap bitmap, bitmap_display;
	public static int imageflag = 0;
	Editor editor;
	String prefBackground;
	Uri mCapturedImageURI;
	String capturedImageFilePath;
	Configuration conf;
	String url = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_tweet);
		getActivityHelper().setupActionBar(getTitle(), 0);
		tweetTab = new TwitterSegmentedTabMenu(this);
		tweetTab.setSeletected(4);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String accessToken = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String accessSecret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Constants.CONSUMER_KEY)
				.setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
				.setMediaProviderAPIKey(Constants.TWITPIC_API_KEY)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessSecret);
		cb.setIncludeEntitiesEnabled(true);
		conf = cb.build();
		btn_send_tweet = (Button) findViewById(R.id.btn_send_tweet);
		et_tweet_text = (EditText) findViewById(R.id.et_tweet_text);
		btn_pick_image = (Button) findViewById(R.id.btn_pick_image);
		btn_pick_image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openTwitterAddPhoto();
			}
		});
		btn_send_tweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				String tweetMessage = et_tweet_text.getText().toString();
				if (!tweetMessage.equals("")) {
					new SendMessagesTask().execute(tweetMessage);
				} else {
					Toast.makeText(SendTweet.this, "Enter Message to send",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tweetTab.setSeletected(4);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		tweetTab.setSeletected(4);
	}

	private class SendMessagesTask extends AsyncTask<String, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(SendTweet.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Updating Status...");
			this.dialog.show();

		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(String... args) {
			String response = null;

			try {
				if (capturedImageFilePath != null) {
					ImageUpload upload = new ImageUploadFactory(conf)
							.getInstance(MediaProvider.TWITPIC);
					url = upload.upload(new File(capturedImageFilePath));
					capturedImageFilePath = null;
				}
				TwitterFactory factory = new TwitterFactory(conf);
				// gets Twitter instance with default credentials
				Twitter twitter = factory.getInstance();
				User user = twitter.verifyCredentials();
				if (url != null) {
					twitter4j.Status status = twitter.updateStatus(args[0]
							+ " " + url);
					url = null;
					response = status.getText().toString();
				} else {
					twitter4j.Status status = twitter.updateStatus(args[0]);
					response = status.getText().toString();
				}

			} catch (TwitterException te) {
				te.printStackTrace();
				//System.out.println("Failed to update Status: "
						//+ te.getMessage());
				// System.exit(-1);
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}

			if (!response.equals("") && response != null) {
				et_tweet_text.setText("");
				Toast.makeText(SendTweet.this, "Status Updated to " + response,
						Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(SendTweet.this, "An error occured please try again later",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_send_tweet, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_back:
			Intent i = new Intent(this, FacebookMainActivity.class);
			i.putExtra("current_tab", "f");
			this.startActivity(i);
			return true;
		case R.id.menu_logout:
			final Editor edit = prefs.edit();
			edit.putString(OAuth.OAUTH_TOKEN, "");
			edit.putString(OAuth.OAUTH_TOKEN_SECRET, "");
			edit.commit();
			final Intent intent1 = new Intent(this, MainActivity.class);
			intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent1);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void openTwitterAddPhoto() {

		String[] addPhoto = new String[] { "Camera", "Gallery" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Change Background Image");
		dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				if (id == 0) {
					String fileName = "temp.jpg";
					ContentValues values = new ContentValues();
					values.put(MediaStore.Images.Media.TITLE, fileName);
					mCapturedImageURI = getContentResolver().insert(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							values);
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					intent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
					startActivityForResult(intent, CAPTURE);

				}
				if (id == 1) {
					// call gallery
					try {
						Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								PICK_IMAGE);
					} catch (Exception e) {
						Toast.makeText(getApplicationContext(), e.getMessage(),
								Toast.LENGTH_LONG).show();
						Log.e(e.getClass().getName(), e.getMessage(), e);
					}
				}

			}
		});

		dialog.setNeutralButton("cancel",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});
		dialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case PICK_IMAGE:
			if (resultCode == Activity.RESULT_OK) {
				mCapturedImageURI = data.getData();
				capturedImageFilePath = getPath(mCapturedImageURI);
			}
			break;
		case CAPTURE:
			if (resultCode == RESULT_OK) {
				capturedImageFilePath = getPath(mCapturedImageURI);
			}
			break;
		case -1:
			System.out.println("no imgae");
			break;
		default:
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}
}
