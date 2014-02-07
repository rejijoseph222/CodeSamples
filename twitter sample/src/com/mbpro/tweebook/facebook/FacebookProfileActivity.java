package com.mbpro.tweebook.facebook;

import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.facebook.android.library.FacebookError;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookListNewsFeeds.ListViewCustomAdapter;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookProfileActivity extends BaseActivity {

	ProgressDialog dialog;
	protected static JSONArray jsonArray;
	FacebookSegmentedTabMenu fbTab;
	private TextView user_full_name, user_gender_text, user_location_text,
			user_birthday_text, user_relationship_text, user_email_text;
	private ImageView user_profile_pic;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		Bundle extras = getIntent().getExtras();
		String response = extras.getString("response");
		new DiplayProfileDetailsTask().execute(response);
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
			Intent i = new Intent(this, MainActivity.class);
			i.putExtra("current_tab", "t");
			this.startActivity(i);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void getProfileDetails() {
		String rootString = "me";
		if (!TextUtils.isEmpty(rootString)) {
			dialog = ProgressDialog.show(this, "", "Loading Profile Details",
					true, true);
			Bundle params = new Bundle();
			params.putString("fields",
					"id,name,gender,email,location,relationship_status,picture,birthday");
			Utility.mAsyncRunner.request("me", params,
					new ProfileRequestListener());
		}
	}

	public class ProfileRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			Intent myIntent = new Intent(getApplicationContext(),
					FacebookProfileActivity.class);
			myIntent.putExtra("response", response);
			startActivity(myIntent);
			finish();
			// new DiplayProfileDetailsTask().execute(response);
			/*
			 * JSONObject jsonObject; try { System.out.println("response ====" +
			 * response); jsonObject = new JSONObject(response); final String
			 * picURL = jsonObject.getString("picture"); final String name =
			 * jsonObject.getString("name"); gender =
			 * jsonObject.getString("gender"); final String email =
			 * jsonObject.getString("email"); if (jsonObject.has("location")) {
			 * location = jsonObject.getJSONObject("location").getString(
			 * "name"); } if (jsonObject.has("relationship_status")) {
			 * relationship_status = jsonObject
			 * .getString("relationship_status"); } else { relationship_status =
			 * "nil"; }
			 * 
			 * if (jsonObject.has("birthday")) { String[] birtthDaySplitted =
			 * birthday.split("/");
			 * 
			 * SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");
			 * SimpleDateFormat df2 = new SimpleDateFormat("MMM dd, yyyy"); try
			 * { birthday =
			 * df2.format(df1.parse(jsonObject.getString("birthday"))); } catch
			 * (java.text.ParseException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); } System.out.println("birthday ====" +
			 * birthday);
			 * 
			 * } Utility.userUID = jsonObject.getString("id");
			 * 
			 * mHandler.post(new Runnable() {
			 * 
			 * @Override public void run() {
			 * 
			 * user_full_name.setText(name); user_gender_text.setText(gender);
			 * user_location_text.setText(location);
			 * user_birthday_text.setText(birthday);
			 * user_relationship_text.setText(relationship_status);
			 * user_email_text.setText(email);
			 * user_profile_pic.setImageBitmap(Utility .getBitmap(picURL)); }
			 * });
			 * 
			 * } catch (JSONException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();

		}

	}

	private class DiplayProfileDetailsTask extends
			AsyncTask<String, Void, String> {
		String location = "";
		String gender = "";
		String relationship_status = "";
		String birthday = "";
		String hometown = "";

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(String... args) {
			return args[0];
		}

		protected void onPostExecute(final String response) {

			JSONObject jsonObject;
			try {
				//System.out.println("response ====" + response);
				jsonObject = new JSONObject(response);
				final String picURL = jsonObject.getString("picture");
				final String name = jsonObject.getString("name");
				gender = jsonObject.getString("gender");
				final String email = jsonObject.getString("email");
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
					//System.out.println("birthday ====" + birthday);

				}
				Utility.userUID = jsonObject.getString("id");

				user_full_name.setText(name);
				user_gender_text.setText(gender);
				user_location_text.setText(location);
				user_birthday_text.setText(birthday);
				user_relationship_text.setText(relationship_status);
				user_email_text.setText(email);
				user_profile_pic.setImageBitmap(Utility.getBitmap(picURL));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
