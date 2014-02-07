package com.mbpro.tweebook.facebook;

import java.io.ByteArrayOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.Facebook;
import com.facebook.android.library.FacebookError;
import com.facebook.android.library.Util;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookPostStatus extends BaseActivity {
	ProgressDialog dialog;
	FacebookSegmentedTabMenu fbTab;
	Button btn_post_status, btn_pick_image;
	String access_token;
	EditText et_fbstatus_text;
	private Handler mHandler;
	private SharedPreferences prefs;
	Uri mCapturedImageURI;
	String capturedImageFilePath;
	private static final int PICK_IMAGE = 1;
	private static final int CAPTURE = 2;
	private static final int REMOVE_BACKGROUND = 3;
	private Bitmap bitmap, bitmap_display;
	String src = null;
	String tweetMessage = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.facebook_post_status);
		getActivityHelper().setupActionBar(getTitle(), 0);
		fbTab = new FacebookSegmentedTabMenu(this);
		fbTab.setSeletected(4);
		mHandler = new Handler();
		access_token = Utility.mFacebook.getAccessToken();
		btn_post_status = (Button) findViewById(R.id.btn_post_status);
		et_fbstatus_text = (EditText) findViewById(R.id.et_fbstatus_text);
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		btn_pick_image = (Button) findViewById(R.id.btn_pick_image);
		btn_pick_image.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				openStatusAddPhoto();
			}
		});
		btn_post_status.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				tweetMessage = et_fbstatus_text.getText().toString();
				if (capturedImageFilePath != null) {
					postImageonWall();
				} else {
					if (!tweetMessage.equals("")) {

						Bundle params = new Bundle();
						params.putString("message", tweetMessage);
						params.putString(Facebook.TOKEN, access_token);
						Utility.mAsyncRunner.request("me/feed", params, "POST",
								new MessageRequestListener(), "Message");
						// new PostStatusTask().execute(tweetMessage);
					} else {
						Toast.makeText(FacebookPostStatus.this,
								"Enter Message to send", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}
		});
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fbTab.setSeletected(4);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		fbTab.setSeletected(4);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.facebook_status, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
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
					final Intent intent1 = new Intent(FacebookPostStatus.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookPostStatus.this.startActivity(intent1);
				}
			});
		}
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class MessageRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			final JSONObject jsonObject;
			try {
				// System.out.println("response: " + response);
				jsonObject = new JSONObject(response);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (jsonObject.has("id")) {
							et_fbstatus_text.setText("");
							Toast.makeText(FacebookPostStatus.this,
									"Status Updated Successfully",
									Toast.LENGTH_SHORT).show();
						} else {
							if (jsonObject.has("error")) {
								try {
									Toast.makeText(
											FacebookPostStatus.this,
											jsonObject.getJSONObject("error")
													.getString("message"),
											Toast.LENGTH_SHORT).show();
								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void openStatusAddPhoto() {

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

	public void postImageonWall() {

		byte[] data = null;
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(capturedImageFilePath, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = 1024;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		Bitmap bi = BitmapFactory.decodeFile(capturedImageFilePath, o2);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bi.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		data = baos.toByteArray();

		Bundle params = new Bundle();
		params.putString(Facebook.TOKEN, Utility.mFacebook.getAccessToken());
		// params.putString("method", "photos.upload");
		params.putString("message", tweetMessage);
		params.putByteArray("picture", data);

		Utility.mAsyncRunner.request("me/photos", params, "POST",
				new SampleUploadListener(), null);
	}

	public class SampleUploadListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(FacebookPostStatus.this,
							"Photo added Successfully", Toast.LENGTH_SHORT)
							.show();
				}
			});
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub

		}
	}

}
