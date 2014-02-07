package com.mbpro.tweebook.facebook;

import java.io.ByteArrayOutputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.FacebookError;
import com.facebook.android.library.Util;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookListNewsFeeds extends BaseActivity implements
		OnItemClickListener {

	// private Handler mHandler;
	ProgressDialog dialog;
	ListView listNewsFeeds;
	ListViewCustomAdapter adapter;
	protected static JSONArray jsonArray;
	FacebookSegmentedTabMenu fbTab;
	private SharedPreferences prefs;
	String access_token;
	private static final int PICK_IMAGE = 1;
	private static final int CAPTURE = 2;
	private static final int REMOVE_BACKGROUND = 3;
	private Bitmap bitmap, bitmap_display;
	public static int imageflag = 0;
	Editor editor;
	String prefBackground;
	int prefBackgroundColor = 0;
	int backgroundSelected = 0;
	Uri mCapturedImageURI;
	String ID;
	private Handler mHandler;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mHandler = new Handler();
		setContentView(R.layout.facebook_tabview);

		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");

		// System.out.println("apiResponse ==== " + apiResponse);
		try {
			jsonArray = new JSONObject(apiResponse).getJSONArray("data");
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
		getActivityHelper().setupActionBar(getTitle(), 0);
		fbTab = new FacebookSegmentedTabMenu(this);
		fbTab.setSeletected(1);
		try {
			SharedPreferences sharedPreferences = getApplicationContext()
					.getSharedPreferences("facebook_background_preference",
							Context.MODE_PRIVATE);
			prefBackground = sharedPreferences.getString("f_background_image",
					null);
			prefBackgroundColor = sharedPreferences.getInt(
					"f_background_color", 0);
		} catch (Exception e) {

		}
		mHandler = new Handler();
		listNewsFeeds = (ListView) findViewById(R.id.listNewsFeeds);
		adapter = new ListViewCustomAdapter(FacebookListNewsFeeds.this);
		listNewsFeeds.setAdapter(adapter);
		Boolean isSDPresent = android.os.Environment.getExternalStorageState()
				.equals(android.os.Environment.MEDIA_MOUNTED);

		if (isSDPresent && prefBackground != null) {
			decodeFile(prefBackground);
		} else if (prefBackgroundColor != 0) {
			listNewsFeeds.setBackgroundResource(prefBackgroundColor);
		}
		adapter.notifyDataSetChanged();
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		access_token = Utility.mFacebook.getAccessToken();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fbTab.setSeletected(1);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			LayoutInflater factorycolorpicker = LayoutInflater.from(this);

			final View colorPickerView = factorycolorpicker.inflate(
					R.layout.colorpicker_layout, null);
			final Button btncolorBlue = (Button) colorPickerView
					.findViewById(R.id.btn_color_blue);
			final Button btncolorRed = (Button) colorPickerView
					.findViewById(R.id.btn_color_red);
			final Button btncolorBlack = (Button) colorPickerView
					.findViewById(R.id.btn_color_black);
			final Button btncolorPink = (Button) colorPickerView
					.findViewById(R.id.btn_color_pink);
			final Button btncolorPurple = (Button) colorPickerView
					.findViewById(R.id.btn_color_purple);
			final Button btncolorGreen = (Button) colorPickerView
					.findViewById(R.id.btn_color_green);

			btncolorBlue.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_blue;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorRed.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_red;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorBlack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_black;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorPink.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_pink;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorPurple.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_purple;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorGreen.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_green;
					listNewsFeeds.setBackgroundResource(backgroundSelected);
				}
			});

			return new AlertDialog.Builder(this)
					.setTitle("Tweebook ColorPicker")
					.setView(colorPickerView)
					.setPositiveButton("Ok",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									editor = getApplicationContext()
											.getSharedPreferences(
													"facebook_background_preference",
													Context.MODE_PRIVATE)
											.edit();
									editor.remove("f_background_image");
									editor.remove("f_background_color");
									editor.putInt("f_background_color",
											backgroundSelected);
									editor.commit();
									removeDialog(1);
								}

							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									Boolean isSDPresent = android.os.Environment
											.getExternalStorageState()
											.equals(android.os.Environment.MEDIA_MOUNTED);

									if (isSDPresent && prefBackground != null) {
										decodeFile(prefBackground);
									} else if (prefBackgroundColor != 0) {
										listNewsFeeds
												.setBackgroundResource(prefBackgroundColor);
									} else {
										listNewsFeeds
												.setBackgroundResource(R.drawable.bg);
									}
									removeDialog(1);
								}
							}).create();
		}
		return null;

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		fbTab.setSeletected(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.face_book_home_page, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			refreshNewsFeeds();
			return true;
		case R.id.menu_change_background:
			openAddPhoto();
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
					final Intent intent1 = new Intent(
							FacebookListNewsFeeds.this, MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookListNewsFeeds.this.startActivity(intent1);
				}
			});
		}
	}

	public void refreshNewsFeeds() {
		String rootString = "me/home";
		if (!TextUtils.isEmpty(rootString)) {
			dialog = ProgressDialog.show(this, "", "Loading News Feeds", true,
					true);
			Bundle params = new Bundle();
			params.putString("metadata", "1");
			params.putString("limit", "30");
			params.putString(
					"fields",
					"id,from, picture, name,message,link,caption,likes,comments,type,object_id,actions");
			Utility.mAsyncRunner.request(rootString, params,
					new NewsFeedRequestListener());
		}
	}

	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class NewsFeedRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();

			try {
				JSONObject json = Util.parseJson(response);
				try {
					Intent myIntent = new Intent(getApplicationContext(),
							FacebookListNewsFeeds.class);
					myIntent.putExtra("API_RESPONSE", json.toString(2));
					startActivity(myIntent);
					finish();

				} catch (JSONException e) {
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

		}

	}

	class ListViewCustomAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		FacebookListNewsFeeds facebookActivity;

		public ListViewCustomAdapter(FacebookListNewsFeeds facebookActivity) {
			this.facebookActivity = facebookActivity;
			if (Utility.model == null) {
				Utility.model = new GetProfileImages();
			}
			if (Utility.feed_model == null) {
				Utility.feed_model = new GetImages();
			}
			Utility.feed_model.setListener(this);
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(facebookActivity.getBaseContext());

		}

		@Override
		public int getCount() {
			return jsonArray.length();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			JSONObject jsonObject = null;
			try {
				jsonObject = jsonArray.getJSONObject(position);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.facebook_newsfeed_row, null);
				ViewHolder holder = new ViewHolder();
				holder.newsfeed_user = (TextView) hView
						.findViewById(R.id.newsfeed_user);
				holder.neewsFeedIcon = (ImageView) hView
						.findViewById(R.id.neewsFeedIcon);

				holder.newsFeedText = (TextView) hView
						.findViewById(R.id.newsFeedText);
				holder.newsfeed_like = (TextView) hView
						.findViewById(R.id.newsfeed_like);

				holder.newsfeed_comments = (TextView) hView
						.findViewById(R.id.newsfeed_comments);
				holder.profile_icon = (ImageView) hView
						.findViewById(R.id.profile_icon);

				hView.setTag(holder);
			}

			final ViewHolder holder = (ViewHolder) hView.getTag();

			try {
				ID = jsonObject.getString("id");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				if (jsonObject.has("picture")) {
					holder.neewsFeedIcon.setImageBitmap(Utility.feed_model
							.getImage(jsonObject.getString("id"),
									jsonObject.getString("picture")));
				}
				/*
				 * else if(jsonObject.has("type")){
				 * if(jsonObject.getString("type").equals("photo")){
				 * holder.neewsFeedIcon
				 * .setImageBitmap(Utility.feed_model.getImage
				 * (jsonObject.getString
				 * ("id"),jsonObject.getString("picture"))); } }
				 */
				else {
					holder.neewsFeedIcon.setImageBitmap(null);
				}
			} catch (JSONException e) {
				holder.neewsFeedIcon.setImageBitmap(null);
				e.printStackTrace();
			}

			try {

				holder.profile_icon.setImageBitmap(Utility.model.getImage(
						jsonObject.getJSONObject("from").getString("id"),
						"http://graph.facebook.com/"
								+ jsonObject.getJSONObject("from").getString(
										"id") + "/picture"));

			} catch (JSONException e) {
				holder.profile_icon.setImageResource(R.drawable.ic_launcher);
			}

			try {
				holder.newsfeed_user.setText(jsonObject.getJSONObject("from")
						.getString("name"));//
			} catch (JSONException e) {
				holder.newsfeed_user.setText("");
			}
			try {
				if (jsonObject.has("message")) {
					holder.newsFeedText
							.setText(jsonObject.getString("message"));
				} else if (jsonObject.has("caption")) {
					holder.newsFeedText
							.setText(jsonObject.getString("caption"));
				} else if (jsonObject.has("name")) {
					holder.newsFeedText.setText(jsonObject.getString("name"));
				} else {
					holder.newsFeedText.setText(jsonObject.getString(""));
				}
			} catch (JSONException e) {
				holder.newsFeedText.setText("");
			}

			try {
				holder.newsfeed_like.setText(jsonObject.getJSONObject("likes")
						.getString("count") + " likes");
			} catch (JSONException e) {
				holder.newsfeed_like.setText("0 likes");
			}

			try {
				holder.newsfeed_comments.setText(jsonObject.getJSONObject(
						"comments").getString("count")
						+ " comments");
			} catch (JSONException e) {
				holder.newsfeed_comments.setText("no comments");
			}

			if (jsonObject.has("link")) {
				try {
					final String link = jsonObject.getString("link");
					//System.out.println("link: " + link);
					// ** Setting Click Listener to goto Detail View Activity .
					// **/

					holder.neewsFeedIcon
							.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {
									startActivity(new Intent(
											Intent.ACTION_VIEW, Uri.parse(link)));

								}
							});
				} catch (JSONException e) { // TODO Auto-generated catchblock
					e.printStackTrace();
				}

			}

			try {
				JSONArray actionArray = jsonObject.getJSONArray("actions");
				// System.out.println("actionArray ==== " + actionArray);
				for (int actionIndex = 0; actionIndex < actionArray.length(); actionIndex++) {
					String action = actionArray.getJSONObject(actionIndex)
							.getString("name").toString();
					final String link = actionArray.getJSONObject(actionIndex)
							.getString("link").toString();
					if (action.equals("Like")) {
						/**
						 * Setting Click Listener to goto Detail View Activity .
						 **/
						holder.newsfeed_like
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {

										startActivity(new Intent(
												Intent.ACTION_VIEW, Uri
														.parse(link)));

									}
								});
					}
					if (action.equals("Comment")) {
						/**
						 * Setting Click Listener to goto Detail View Activity .
						 **/
						holder.newsfeed_comments
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										startActivity(new Intent(
												Intent.ACTION_VIEW, Uri
														.parse(link)));

									}
								});
					}
				}

			} catch (JSONException e) {

			}

			return hView;
		}

	}

	class ViewHolder {
		TextView newsFeedText, newsfeed_user, newsfeed_like, newsfeed_comments;
		ImageView profile_icon, neewsFeedIcon;
		int position;
	}

	/*
	 * private void makeToast(final String msg) { mHandler.post(new Runnable() {
	 * 
	 * @Override public void run() { Toast.makeText(FacebookListNewsFeeds.this,
	 * msg, Toast.LENGTH_LONG).show(); } }); }
	 */

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	public void openAddPhoto() {

		String[] addPhoto = new String[] { "Camera", "Gallery", "Change Color",
				"Remove Background" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Change Background Image");
		dialog.setItems(addPhoto, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				if (id == 0) {
					/*
					 * Intent cameraIntent = new Intent(
					 * android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					 * startActivityForResult(cameraIntent, CAPTURE);
					 */

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
				if (id == 2) {
					showDialog(1);
				}
				if (id == 3) {
					listNewsFeeds.setBackgroundResource(R.drawable.bg);
					editor = getApplicationContext().getSharedPreferences(
							"facebook_background_preference",
							Context.MODE_PRIVATE).edit();
					editor.remove("f_background_image");
					editor.remove("f_background_color");
					editor.commit();
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
				Uri selectedImageUri = data.getData();
				String filePath = null;

				try {
					// OI FILE Manager
					String filemanagerstring = selectedImageUri.getPath();

					// MEDIA GALLERY
					String selectedImagePath = getPath(selectedImageUri);

					if (selectedImagePath != null) {
						filePath = selectedImagePath;
					} else if (filemanagerstring != null) {
						filePath = filemanagerstring;
					} else {
						Toast.makeText(getApplicationContext(), "Unknown path",
								Toast.LENGTH_LONG).show();
						Log.e("Bitmap", "Unknown path");
					}

					if (filePath != null) {

						editor = getApplicationContext().getSharedPreferences(
								"facebook_background_preference",
								Context.MODE_PRIVATE).edit();
						editor.remove("f_background_image");
						editor.remove("f_background_color");
						editor.putString("f_background_image", filePath);

						editor.commit();
						decodeFile(filePath);

						// new ImageUploadTask().execute();
					} else {
						bitmap = null;
					}
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Internal error",
							Toast.LENGTH_LONG).show();
					Log.e(e.getClass().getName(), e.getMessage(), e);
				}
			}
			break;
		case CAPTURE:
			if (resultCode == RESULT_OK) {

				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(mCapturedImageURI, projection,
						null, null, null);
				int column_index_data = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String capturedImageFilePath = cursor
						.getString(column_index_data);

				// Image captured and saved to fileUri specified in the Intent
				// Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
				editor = getApplicationContext().getSharedPreferences(
						"facebook_background_preference", Context.MODE_PRIVATE)
						.edit();
				editor.remove("f_background_image");
				editor.remove("f_background_color");
				editor.putString("f_background_image", capturedImageFilePath);
				editor.commit();
				decodeFile(capturedImageFilePath);
			}
			break;

		case -1:
			//System.out.println("no imgae");
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

	public void decodeFile(String filePath) {
		try {
			// Decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, o);

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
			bitmap = BitmapFactory.decodeFile(filePath, o2);
			bitmap_display = getResizedBitmap(bitmap, 240, 300);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap_display.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			Drawable drawable = new BitmapDrawable(getResources(),
					bitmap_display);
			listNewsFeeds.setBackgroundDrawable(drawable);
		} catch (Exception e) {
			// TODO: handle exception
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

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class LikeRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			final JSONObject jsonObject;
			try {
				//System.out.println("response: " + response);
				jsonObject = new JSONObject(response);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
