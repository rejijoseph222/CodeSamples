package com.mbpro.tweebook.twitter.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuth;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.twitter.Constants;
import com.mbpro.tweebook.twitter.model.TweetTimelineModel;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class TwitterMentionsActivity extends BaseActivity implements
		OnItemClickListener {
	TwitterSegmentedTabMenu tweetTab;
	private SharedPreferences prefs;
	private ConfigurationBuilder cb;
	TweetTimelineModel tmodel;
	ListView listMentions;
	private ArrayList<Object> itemList;
	ListViewCustomAdapter adapter;
	TwitterFactory factory;
	Twitter twitter;
	String prefBackground;
	int prefBackgroundColor = 0;
	private Bitmap bitmap, bitmap_display;
	static String replyTo;
	static long reply_user;
	static long tweet_status_id;
	static String retweet_message;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_mentions_activity);
		getActivityHelper().setupActionBar(getTitle(), 0);

		listMentions = (ListView) findViewById(R.id.listMentions);
		tweetTab = new TwitterSegmentedTabMenu(this);
		tweetTab.setSeletected(2);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		String accessToken = prefs.getString(OAuth.OAUTH_TOKEN, "");
		String accessSecret = prefs.getString(OAuth.OAUTH_TOKEN_SECRET, "");
		cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Constants.CONSUMER_KEY)
				.setOAuthConsumerSecret(Constants.CONSUMER_SECRET)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessSecret);
		cb.setIncludeEntitiesEnabled(true);
		factory = new TwitterFactory(cb.build());
		try {
			SharedPreferences sharedPreferences = getApplicationContext()
					.getSharedPreferences("background_preference",
							Context.MODE_PRIVATE);
			prefBackground = sharedPreferences.getString("background_image",
					null);
			prefBackgroundColor = sharedPreferences.getInt("background_color",
					0);
			// System.out.println("prefBackground : " + prefBackground);
		} catch (Exception e) {

		}
		prepareArrayList();

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		TweetTimelineModel tM = (TweetTimelineModel) adapter.getItem(position);
		if (tM.getTimeline_tweet_user() != null) {
			openReplyRetweetDialog(tM);
		}
	}

	public void prepareArrayList() {
		itemList = new ArrayList<Object>();
		new ListMentionsTask().execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tweetTab.setSeletected(2);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		tweetTab.setSeletected(2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_menu, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent;

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			itemList.clear();
			new ListMentionsTask().execute();
			return true;
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

	public void openReplyRetweetDialog(final TweetTimelineModel obj) {
		twitter = factory.getInstance();
		User user = null;
		String[] timelineOptions;
		try {
			user = twitter.verifyCredentials();

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		timelineOptions = new String[] { "Reply" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("TweeBook");
		dialog.setItems(timelineOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				if (id == 0) {
					replyTo = obj.getTimeline_tweet_user();
					reply_user = obj.getTweet_user_id();
					showDialog(1);
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
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alert_dialog_text_entry, null);
			final EditText replyTweet = (EditText) textEntryView
					.findViewById(R.id.et_reply_text);
			replyTweet.setText("@" + replyTo);
			return new AlertDialog.Builder(this)
					.setTitle("Tweebook Reply")
					.setView(textEntryView)

					.setPositiveButton("Reply",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									try {
										twitter.updateStatus(new StatusUpdate(
												replyTweet.getText().toString())
												.inReplyToStatusId(reply_user));
									} catch (TwitterException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									removeDialog(1);
								}

							})
					.setNegativeButton("cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(1);
								}
							}).create();
		}
		return null;

	}

	/***************** Async Task for fetch Timeline Tweets ********************/
	// Add one item into the Array List
	public void AddObjectToList(String timeline_tweetText,
			Drawable timeline_profile_image, String timeline_tweet_user,
			String timeline_date) {
		// aModel = new ArtistModel(id,name,location,image);
		tmodel = new TweetTimelineModel(timeline_tweetText,
				timeline_profile_image, timeline_tweet_user, timeline_date);
		itemList.add(tmodel);
	}

	class ListViewCustomAdapter extends BaseAdapter {

		ArrayList<Object> itemList;
		private DrawableManager drawableManager;
		public Activity context;
		public LayoutInflater inflater;
		private Map<Integer, ViewHolder> viewHolders;

		public ListViewCustomAdapter(Activity context,
				ArrayList<Object> itemList) {
			super();

			this.context = context;
			this.itemList = itemList;
			drawableManager = new DrawableManager();
			viewHolders = new HashMap<Integer, ViewHolder>();
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return itemList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return itemList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView tweet_text, tweet_user_name, tweet_time;
			ImageView profile_icon;
			int position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub

			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.twitter_mentions_row,
						null);

				holder.tweet_user_name = (TextView) convertView
						.findViewById(R.id.tweet_user_name);
				holder.tweet_time = (TextView) convertView
						.findViewById(R.id.tweet_time);

				holder.tweet_text = (TextView) convertView
						.findViewById(R.id.tweet_text);

				holder.profile_icon = (ImageView) convertView
						.findViewById(R.id.profile_icon);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			final TweetTimelineModel tm = (TweetTimelineModel) itemList
					.get(position);

			holder.tweet_user_name.setText(tm.getTimeline_tweet_user());
			holder.tweet_text.setText(tm.getTimeline_tweetText());
			holder.tweet_time.setText(tm.getTimeline_date());
			holder.profile_icon.setImageDrawable(tm.getProfileImage());

			return convertView;
		}

	}

	private class ListMentionsTask extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				TwitterMentionsActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading Mentions...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(Void... args) {
			String response = null;

			try {
				twitter = factory.getInstance();
				User user = twitter.verifyCredentials();

				List<twitter4j.Status> statuses = twitter.getMentions();
				String image_url = null;

				// System.out.println("Showing @" + user.getScreenName() +
				// "'s home timeline.");
				for (twitter4j.Status status : statuses) {
					if (status.isRetweet()) {
						// System.out.println("getInReplyToUserId ===  "+
						// status.getRetweetedStatus().getUser().getId());
						image_url = status.getRetweetedStatus().getUser()
								.getProfileImageURL().toString();
					} else {
						image_url = status.getUser().getProfileImageURL()
								.toString();
					}

					Drawable imageIcon = LoadImage(image_url);
					String date = status.getCreatedAt().toGMTString()
							.replace("GMT", "");
					try {
						String[] dateArray = date.split(" ");
						if (dateArray.length > 0) {
							date = dateArray[0] + " " + dateArray[1];
						}
					} catch (Exception e) {
						// TODO: handle exception
					}
					AddObjectToList(status.getText(), imageIcon, status
							.getUser().getScreenName(), date);
				}
				response = "success";
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out
						.println("Failed to get timeline: " + te.getMessage());
				System.exit(-1);
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			adapter = new ListViewCustomAdapter(TwitterMentionsActivity.this,
					itemList);
			listMentions.setAdapter(adapter);
			listMentions.setOnItemClickListener(TwitterMentionsActivity.this);
			Boolean isSDPresent = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);

			if (isSDPresent && prefBackground != null) {
				decodeFile(prefBackground);
			} else if (prefBackgroundColor != 0) {
				listMentions.setBackgroundResource(prefBackgroundColor);
			}

			adapter.notifyDataSetChanged();

		}
	}

	public Drawable LoadImage(String url) {

		Drawable d;
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			d = Drawable.createFromStream(is, "src name");
			return d;
		} catch (NullPointerException e) {
			d = getResources().getDrawable(R.drawable.ic_launcher);
			return d;
		} catch (Exception e) {
			d = getResources().getDrawable(R.drawable.ic_launcher);
			return d;
		}
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
			listMentions.setBackgroundDrawable(drawable);
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
