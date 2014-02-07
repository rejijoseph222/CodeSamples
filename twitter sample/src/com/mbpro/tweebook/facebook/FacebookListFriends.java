package com.mbpro.tweebook.facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.library.AsyncFacebookRunner;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookListFriends extends BaseActivity implements
		OnItemClickListener {
	private Handler mHandler;

	protected ListView listFriends;
	protected static JSONArray jsonArray;
	private SharedPreferences prefs;
	TextView list_friends_label;

	/*
	 * Layout the friends' list
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mHandler = new Handler();
		setContentView(R.layout.facebook_friends_list);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		Bundle extras = getIntent().getExtras();
		String apiResponse = extras.getString("API_RESPONSE");
		getActivityHelper().setupActionBar(getTitle(), 0);
		list_friends_label = (TextView) findViewById(R.id.list_friends_label);
		try {
			jsonArray = new JSONObject(apiResponse).getJSONArray("data");
			list_friends_label.setText("Friends(" + jsonArray.length() + ")");
		} catch (JSONException e) {
			showToast("Error: " + e.getMessage());
			return;
		}
		listFriends = (ListView) findViewById(R.id.listFriends);
		listFriends.setOnItemClickListener(this);
		listFriends.setAdapter(new FriendListAdapter(this));

	}

	/*
	 * Clicking on a friend should popup a dialog for user to post on friend's
	 * wall.
	 */
	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		final String friendId;
		try {
			friendId = jsonArray.getJSONObject(position).getString("id");
			if(friendId != null){
				Intent i = new Intent(FacebookListFriends.this,GetFriendsDetails.class);
				i.putExtra("profile_id", friendId);
				startActivity(i);
			}
		} catch (JSONException e) {
			showToast("Error: " + e.getMessage());
		}

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
					final Intent intent1 = new Intent(FacebookListFriends.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookListFriends.this.startActivity(intent1);
				}
			});
		}
	}

	/*
	 * Callback after the message has been posted on friend's wall.
	 */
	public class PostDialogListener extends BaseDialogListener {
		@Override
		public void onComplete(Bundle values) {
			final String postId = values.getString("post_id");
			if (postId != null) {
				showToast("Message posted on the wall.");
			} else {
				showToast("No message posted on the wall.");
			}
		}
	}

	public void showToast(final String msg) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(FacebookListFriends.this, msg,
						Toast.LENGTH_LONG);
				toast.show();
			}
		});
	}

	/**
	 * Definition of the list adapter
	 */
	public class FriendListAdapter extends BaseAdapter {
		private LayoutInflater mInflater;
		FacebookListFriends friendsList;

		public FriendListAdapter(FacebookListFriends friendsList) {
			this.friendsList = friendsList;
			if (Utility.friends_model == null) {
				Utility.friends_model = new GetFriendImages();
			}
			Utility.friends_model.setListener(this);
			mInflater = LayoutInflater.from(friendsList.getBaseContext());
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
				// System.out.println("jsonObject=== "+jsonObject);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			View hView = convertView;
			if (convertView == null) {
				hView = mInflater.inflate(R.layout.facebook_friend_item, null);
				ViewHolder holder = new ViewHolder();
				holder.friend_profile_pic = (ImageView) hView
						.findViewById(R.id.friend_profile_pic);
				holder.name = (TextView) hView.findViewById(R.id.name);
				hView.setTag(holder);
			}

			ViewHolder holder = (ViewHolder) hView.getTag();

			try {

				holder.friend_profile_pic.setImageBitmap(Utility.friends_model
						.getImage(jsonObject.getString("id"),
								jsonObject.getString("picture")));

			} catch (JSONException e) {
				e.printStackTrace();
				holder.name.setText("");
			}
			try {
				holder.name.setText(jsonObject.getString("name"));
			} catch (JSONException e) {
				holder.name.setText("");
			}

			return hView;
		}

	}

	class ViewHolder {
		ImageView friend_profile_pic;
		TextView name;
		TextView info;
	}
}
