package com.mbpro.tweebook.facebook;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.FacebookError;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookMessages extends BaseActivity {
	ProgressDialog dialog;
	private Handler mHandler;
	ListView listMessages;
	ListViewMessagesCustomAdapter adapter;
	protected static JSONArray jsonMesagesArray;
	FacebookSegmentedTabMenu fbTab;
	private SharedPreferences prefs;
	String prefBackground;
	int prefBackgroundColor = 0;
	private Bitmap bitmap, bitmap_display;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		setContentView(R.layout.facebook_list_messages);
		getActivityHelper().setupActionBar(getTitle(), 0);
		fbTab = new FacebookSegmentedTabMenu(this);
		fbTab.setSeletected(2);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
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
		getMessageDetails();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		fbTab.setSeletected(2);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		fbTab.setSeletected(2);
	}

	public void getMessageDetails() {
		String rootString = "me";
		if (!TextUtils.isEmpty(rootString)) {
			dialog = ProgressDialog.show(this, "", "Loading Messages...", true,
					true);
			Bundle params = new Bundle();
			params.putString("fields", "id,from,to,updated_time,message");
			Utility.mAsyncRunner.request("me/inbox", params,
					new MessageRequestListener());
		}
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
			getMessageDetails();
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
					final Intent intent1 = new Intent(FacebookMessages.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookMessages.this.startActivity(intent1);
				}
			});
		}
	}

	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class MessageRequestListener extends BaseRequestListener {
		String location = "";
		String gender = "";
		String relationship_status = "";
		String birthday = "";
		String hometown = "";

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			try {
				System.out.println("response ====" + response);
				try {
					new DiplayMessagesTask().execute(response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// new DiplayMessagesTask().execute(response);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();

		}

	}

	class ListViewMessagesCustomAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		FacebookMessages facebookActivity;
		String updated = "";

		public ListViewMessagesCustomAdapter(FacebookMessages facebookActivity) {

			this.facebookActivity = facebookActivity;
			if (Utility.model == null) {
				Utility.model = new GetProfileImages();
			}
			Utility.model.setListener(this);
			mInflater = LayoutInflater.from(facebookActivity.getBaseContext());
		}

		@Override
		public int getCount() {
			return jsonMesagesArray.length();
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
				jsonObject = jsonMesagesArray.getJSONObject(position);

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.facebook_messages_row, null);
				ViewHolder holder = new ViewHolder();
				holder.message_user_name = (TextView) hView
						.findViewById(R.id.message_user_name);
				holder.messages_profile_icon = (ImageView) hView
						.findViewById(R.id.messages_profile_icon);

				holder.messages_text = (TextView) hView
						.findViewById(R.id.messages_text);
				holder.messages_time = (TextView) hView
						.findViewById(R.id.messages_time);

				hView.setTag(holder);
			}

			final ViewHolder holder = (ViewHolder) hView.getTag();
			try {

				holder.messages_profile_icon.setImageBitmap(Utility.model
						.getImage(
								jsonObject.getJSONObject("from")
										.getString("id"),
								"http://graph.facebook.com/"
										+ jsonObject.getJSONObject("from")
												.getString("id") + "/picture"));

			} catch (JSONException e) {
				holder.messages_profile_icon
						.setImageResource(R.drawable.ic_launcher);
			}

			try {
				holder.message_user_name.setText(jsonObject.getJSONObject(
						"from").getString("name"));//
			} catch (JSONException e) {
				holder.message_user_name.setText("");
			}

			try {
				SimpleDateFormat df1 = new SimpleDateFormat(
						"yyyy-MM-dd'T'HH:mm:ssZ");
				SimpleDateFormat df2 = new SimpleDateFormat("MMM dd, yyyy");
				try {
					updated = df2.format(df1.parse(jsonObject
							.getString("updated_time")));
					holder.messages_time.setText(updated);//
				} catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (JSONException e) {
				holder.messages_time.setText("");
			}
			try {
				if (jsonObject.has("message")) {
					holder.messages_text.setText(jsonObject
							.getString("message"));
				} else if (jsonObject.has("caption")) {
					holder.messages_text.setText(jsonObject
							.getString("caption"));
				} else if (jsonObject.has("name")) {
					holder.messages_text.setText(jsonObject.getString("name"));
				} else {
					holder.messages_text.setText(jsonObject.getString(""));
				}
			} catch (JSONException e) {
				holder.messages_text.setText("");
			}
			
			if (jsonObject.has("id")) {
				try {
					final String link = "https://m.facebook.com/messages/read?action=read&tid=id."+jsonObject.getString("id");

					hView.setOnClickListener(new View.OnClickListener() {

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
			//
			return hView;
		}

	}

	class ViewHolder {
		TextView message_user_name, messages_time, messages_text;
		ImageView messages_profile_icon;
		int position;
	}

	private class DiplayMessagesTask extends AsyncTask<String, Void, String> {

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(String... args) {
			String response = null;
			try {

				jsonMesagesArray = new JSONObject(args[0]).getJSONArray("data");
				response = "sucess";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			try {

				listMessages = (ListView) findViewById(R.id.listMessages);
				adapter = new ListViewMessagesCustomAdapter(
						FacebookMessages.this);
				listMessages.setAdapter(adapter);
				Boolean isSDPresent = android.os.Environment
						.getExternalStorageState().equals(
								android.os.Environment.MEDIA_MOUNTED);

				if (isSDPresent && prefBackground != null) {
					decodeFile(prefBackground);
				} else if (prefBackgroundColor != 0) {
					listMessages.setBackgroundResource(prefBackgroundColor);
				}
				adapter.notifyDataSetChanged();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void decodeFile(String filePath) {
		// Decode image size
		try {
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
			listMessages.setBackgroundDrawable(drawable);
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
}
