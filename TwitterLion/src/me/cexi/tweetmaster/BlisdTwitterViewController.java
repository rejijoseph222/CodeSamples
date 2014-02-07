package me.cexi.tweetmaster;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.cexi.tweetmaster.model.BlisdTweetModel;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

/*ACtivity for Displaying Tweets 
 * */
public class BlisdTwitterViewController extends ListActivity {
	/** Called when the activity is first created. */
	private  List<BlisdTweetModel> tweetListCache;
	private PullToRefreshListView mPullRefreshListView;
	private ArrayAdapter<BlisdTweetModel> mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		/** Setting up Pull Refresh Listner **/
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				/** Async Task for retrieving tweets **/
				AsyncTweetRetriever tweetRetrieverTask = new AsyncTweetRetriever();
				tweetRetrieverTask.execute("johnstack");
			}
		});
		
		/** Additonal code for handling rotation **/
		if (getLastNonConfigurationInstance() == null) {
			AsyncTweetRetriever tweetRetrieverTask = new AsyncTweetRetriever();
			tweetRetrieverTask.execute("johnstack");
		} else {
			populateList((List<BlisdTweetModel>) getLastNonConfigurationInstance());
		}
	}

	/** Creating options menu for refresh and get user tweets. Here both are doing same tasks **/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.user:
			Intent intent = new Intent(this, BlisdTwitterViewController.class);
			startActivity(intent);
			return true;
		case R.id.refresh:
			Intent intent1 = new Intent(this, BlisdTwitterViewController.class);
			startActivity(intent1);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * Retrieves the tweets that match the search terms as an asynchronous task
	 * and displays a progress dialog accordingly.
	 * 
	 * 
	 */
	private class AsyncTweetRetriever extends
			AsyncTask<String, Integer, List<BlisdTweetModel>> {
		private ProgressDialog progressDialog;
		private String error;

		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(BlisdTwitterViewController.this);
			progressDialog.setMessage(getText(R.string.searching));
			progressDialog.show();
		}
		/** Executing background Tasks to fetch tweets **/
		@Override
		protected List<BlisdTweetModel> doInBackground(String... params) {
			String search = params[0];

			BlisdTwitterClient twitterClient = new BlisdTwitterClient();
			List<BlisdTweetModel> tweets = null;

			try {
				tweets = twitterClient.search(search);
			} catch (ClientProtocolException e) {
				error = getText(R.string.tweet_retrieval_failed).toString();
				e.printStackTrace();
			} catch (IOException e) {
				error = getText(R.string.tweet_retrieval_failed_connection)
						.toString();
				e.printStackTrace();
			} catch (JSONException e) {
				error = getText(R.string.tweet_retrieval_failed).toString();
				e.printStackTrace();
			} catch (ParseException e) {
				error = getText(R.string.tweet_retrieval_failed).toString();
				e.printStackTrace();
			}

			return tweets;
		}

		@Override
		protected void onPostExecute(List<BlisdTweetModel> tweets) {
			progressDialog.dismiss();
			if (tweets != null) {
				tweetListCache = tweets;
				populateList(tweets);

			} else {
				Toast.makeText(BlisdTwitterViewController.this, error,
						Toast.LENGTH_SHORT).show();
				BlisdTwitterViewController.this.finish();
			}
			super.onPostExecute(tweets);
		}
	}
	/** Model Class  Constructor calling here. **/
	BlisdTweetModel get(String fromUser, String fromUserName,
			String toUserName, String toUser, String text, String textLink,
			String created, String profileImage){
				return new BlisdTweetModel(fromUser,fromUserName,toUserName,toUser,text,textLink,created,profileImage);
		
	}
	

	/**
	 * Populates the list of tweets.
	 * 
	 * @param tweets
	 *            The tweets to populate the list with.
	 */
	private void populateList(List<BlisdTweetModel> tweets) {
		try{
			mAdapter = new TweetListAdapter(this,tweets);
			setListAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}catch (Exception e) {
			// TODO: handle exception
		}
		// Call onRefreshComplete when the list has been refreshed.
		mPullRefreshListView.onRefreshComplete();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		return tweetListCache;
	}

	/**
	 * List adapter to display Tweets.
	 * 
	 * 
	 */
	public  class TweetListAdapter extends ArrayAdapter<BlisdTweetModel> {

		private final java.text.DateFormat DATE_FORMAT;
		private final java.text.DateFormat TIME_FORMAT;
		private BlisdDrawableManager drawableManager;
		private LayoutInflater inflater;
		private final List<BlisdTweetModel> tweets;
		private Map<Integer, ViewHolder> viewHolders;
		private final Activity context;



		/**
		 * Constructor
		 * 
		 * @param context
		 *            The application context.
		 * @param tweets
		 *            The list of tweets to display.
		 */
		public TweetListAdapter(Activity context, List<BlisdTweetModel> tweets) {
			super(context, R.layout.result_item, tweets);
			DATE_FORMAT = DateFormat.getMediumDateFormat(context);
			TIME_FORMAT = DateFormat.getTimeFormat(context);

			viewHolders = new HashMap<Integer, ViewHolder>();
			drawableManager = new BlisdDrawableManager();
			inflater = LayoutInflater.from(context);
			
			this.tweets = tweets;
			this.context = context;
		}

		public int getCount() {
			return tweets.size();
		}

		

		public long getItemId(int position) {
			return tweets.get(position).getId();
		}
		
		/** Calling Detail View activity and passing extras to avoid re-fetching datas from web. **/
		public  void showDetailView(String fromUser, String fromUserName,
				String toUserName, String toUser, String text, String textLink,
				String created, String profileImageUrl) {
			Intent intent = new Intent(BlisdTwitterViewController.this,BlisdTwitterDetailViewController.class);
			intent.putExtra("fromUser", fromUser);
			intent.putExtra("fromUserName", fromUserName);
			intent.putExtra("toUserName", toUserName);
			intent.putExtra("toUser", toUser);
			intent.putExtra("text", text);
			intent.putExtra("textLink", textLink);
			intent.putExtra("created", created);
			intent.putExtra("profileImageUrl", profileImageUrl);
			BlisdTwitterViewController.this.startActivity(intent);
		}
		/** Using View Holder for Better Performance **/
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final ViewHolder viewHolder;

			if (convertView == null) {
				convertView = inflater.inflate(R.layout.result_item, null);

				viewHolder = new ViewHolder();
				viewHolder.thumb = (ImageView) convertView
						.findViewById(R.id.tweetUserThumbImageView);
				viewHolder.userName = (TextView) convertView
						.findViewById(R.id.tweetUserTextView);
				viewHolder.tweet = (TextView) convertView
						.findViewById(R.id.tweetTextView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolders.put(position, viewHolder);

			final BlisdTweetModel tweet = tweets.get(position);
			
			/** Loading Drawable Image from web only at the time of viewing list . **/
			BlisdDrawableManager.DrawableFetchedHandler thumbFetchedHandler = new BlisdDrawableManager.DrawableFetchedHandler() {

				public void setDrawable(Drawable drawable) {
					tweet.setProfileImage(drawable);
					ViewHolder viewHolder = viewHolders.get(position);
					if (viewHolder.position == position) {
						viewHolder.thumb.setImageDrawable(tweet
								.getProfileImage());
						notifyDataSetChanged();
					}
				}
			};

			drawableManager.fetchDrawableAsync(tweet.getProfileImageUrl(),
					thumbFetchedHandler);
			viewHolder.tweet.setText(tweet.getText());
			/** Setting Click Listener to goto Detail View Activity . **/
			viewHolder.tweet.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = ((tweet.getTextLink()).equals("novalue"))?tweet.getTextMediaLink():tweet.getTextLink();
					showDetailView(tweet.getFromUser(),tweet.getFromUserName(),tweet.getToUserName(),tweet.getToUser(),tweet.getText(),url,tweet.getTweetDate(),tweet.getProfileImageUrl());
				}
			});

			viewHolder.userName.setText(tweet.getFromUser());
			viewHolder.thumb.setImageDrawable(tweet.getProfileImage());
			/** Setting Click Listener to goto Detail View Activity . **/
			viewHolder.thumb.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = ((tweet.getTextLink()).equals("novalue"))?tweet.getTextMediaLink():tweet.getTextLink();
					showDetailView(tweet.getFromUser(),tweet.getFromUserName(),tweet.getToUserName(),tweet.getToUser(),tweet.getText(),url,tweet.getTweetDate(),tweet.getProfileImageUrl());
				}
			});
			viewHolder.position = position;

			return convertView;
		}

		/**
		 * Returns a string representation of the specified date and time
		 * according to phone settings.
		 * 
		 * @param calendar
		 *            A calendar object representing the date and time.
		 * @return The string representation of the specified date and time.
		 */
		private String getFormattedDate(Calendar calendar) {
			return DATE_FORMAT.format(calendar.getTime()) + " "
					+ TIME_FORMAT.format(calendar.getTime());
		}

		/**
		 * View holder for the tweet view.
		 * 
		 * 
		 */
		 class ViewHolder {
			ImageView thumb;
			TextView userName;
			TextView tweet;
			int position;
		}
	}

}
