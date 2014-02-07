package com.mbpro.tweebook.twitter.ui;

import oauth.signpost.OAuth;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.Constants;
import com.mbpro.tweebook.ui.BaseActivity;

public class ReplyTweet extends BaseActivity {
	TwitterSegmentedTabMenu tweetTab;
	Button btn_send_tweet;
	private SharedPreferences prefs;
	private ConfigurationBuilder cb;
	EditText et_tweet_text;

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
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessSecret);
		cb.setIncludeEntitiesEnabled(true);

		btn_send_tweet = (Button) findViewById(R.id.btn_send_tweet);
		et_tweet_text = (EditText) findViewById(R.id.et_tweet_text);

		btn_send_tweet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String tweetMessage = et_tweet_text.getText().toString();
				if (!tweetMessage.equals("")) {
					new SendMessagesTask().execute(tweetMessage);
				} else {
					Toast.makeText(ReplyTweet.this, "Enter Message to send",
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

	@Override
	protected Dialog onCreateDialog(int id) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View textEntryView = factory.inflate(
				R.layout.alert_dialog_text_entry, null);
		return new AlertDialog.Builder(this)
				.setTitle("Tweetbook Reply")
				.setView(textEntryView)
				.setPositiveButton("Reply",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								showDialog(1);

							}

						})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								/* User clicked cancel so do some stuff */
							}
						}).create();

	}

	private class SendMessagesTask extends AsyncTask<String, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				ReplyTweet.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Updating Status...");
			this.dialog.show();

		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(String... args) {
			String response = null;

			try {
				TwitterFactory factory = new TwitterFactory(cb.build());
				// gets Twitter instance with default credentials
				Twitter twitter = factory.getInstance();
				User user = twitter.verifyCredentials();
				twitter4j.Status status = twitter.updateStatus(args[0]);

				response = status.getText().toString();
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to update Status: "
						+ te.getMessage());
				// System.exit(-1);
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}

			if (!response.equals("")) {
				Toast.makeText(ReplyTweet.this,
						"Status Updated to " + response, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}
}
