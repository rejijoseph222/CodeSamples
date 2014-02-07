package com.mbpro.tweebook.twitter.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuth;
import twitter4j.DirectMessage;
import twitter4j.IDs;
import twitter4j.ResponseList;
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
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.twitter.Constants;
import com.mbpro.tweebook.twitter.model.DirectMessagesModel;
import com.mbpro.tweebook.twitter.model.TweetTimelineModel;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class TwitterMessagesActivity extends BaseActivity implements
		OnItemClickListener {
	TwitterSegmentedTabMenu tweetTab;
	private SharedPreferences prefs;
	private ConfigurationBuilder cb;
	DirectMessagesModel dmmodel,dmmodelsearch;
	ListView listMessages;
	private ArrayList<Object> itemList;
	ListViewCustomAdapter1 adapter;
	TwitterFactory factory;
	Twitter twitter;
	String prefBackground;
	int prefBackgroundColor = 0;
	private Bitmap bitmap, bitmap_display;
	Spinner sp_direct_messages;
	static String replyTo;
	static int reply_user;
	User user;
	Handler getUsers;
	TweetTimelineModel tmodel;
	private ArrayList<Object> usersList;
	int textlength=0;
	private ArrayList<Object> array_search;
	EditText DirectMessageUser ;
	ListView listFollowers;
	ListViewCustomAdapterSearch searchadapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_messages_view);
		getActivityHelper().setupActionBar(getTitle(), 0);
		getUsers = new Handler();
		listMessages = (ListView) findViewById(R.id.listMessages);
		tweetTab = new TwitterSegmentedTabMenu(this);
		tweetTab.setSeletected(3);
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

	public void prepareArrayList() {
		itemList = new ArrayList<Object>();
		new ListMessagesTask().execute();

	}
	public void prepareUserArrayList() {
		if(usersList != null && usersList.size() > 0){
			openDirectMesagesDialog();
		}else{
			usersList = new ArrayList<Object>();
			new ListUsersTask().execute();
		}
	}
	
	// Add one item into the Array List
	public void AddObjectToUsersList(long l,String name,String screenName) {
		dmmodel = new DirectMessagesModel(l,name,screenName);
		usersList.add(dmmodel);
	}
	// Add one item into the Array List
	public void AddObjectToUsersSearchList(long l,String name,String screenName) {
		dmmodelsearch = new DirectMessagesModel(l,name,screenName);
		array_search.add(dmmodelsearch);
	}
	
	public void openReplyRetweetDialog(final TweetTimelineModel obj) {
		twitter = factory.getInstance();
		String[] timelineOptions;

		timelineOptions = new String[] { "Reply" };
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("TweeBook");
		dialog.setItems(timelineOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int id) {
				dialog.dismiss();
				if (id == 0) {
					getUsers.post(new Runnable() {
						@Override
						public void run() {
							replyTo = obj.getTimeline_tweet_user();
							showDialog(1);
						}
					});
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

	public void openDirectMesagesDialog() {
		
		twitter = factory.getInstance();
		LayoutInflater factory1 = LayoutInflater.from(this);
		final View textEntryView1 = factory1.inflate(
				R.layout.alert_dialog_send_dircet_message, null);
		final EditText  DirectMessage = (EditText) textEntryView1
				.findViewById(R.id.et_direct_message_text);
		
		DirectMessageUser = (EditText) textEntryView1
				.findViewById(R.id.et_direct_message_user);
		listFollowers = (ListView)textEntryView1.findViewById(R.id.listFollowers);
		ImageButton btn_search_user = (ImageButton)textEntryView1.findViewById(R.id.btn_search_user);
		btn_search_user.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				addChangeListner();
			}
		});
		
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Direct Message");
		dialog.setView(textEntryView1);
		dialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					twitter.sendDirectMessage(DirectMessageUser.getText()
							.toString(), DirectMessage.getText().toString());
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dialog.dismiss();
			}

		});
		dialog.setNegativeButton("cancel",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						dialog.dismiss();
					}
				});

		dialog.show();
	}
	
	 public void addChangeListner(){
			InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.hideSoftInputFromWindow(DirectMessageUser.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		 textlength = DirectMessageUser.getText().length();
			array_search = new ArrayList<Object>();
			//System.out.println("addChangeListner usersList.size() ===="+usersList.size());
			if(textlength > 0){
				
				for (int i = 0; i < usersList.size(); i++) {
					DirectMessagesModel obj = (DirectMessagesModel) usersList.get(i);
					String name = obj.getName();
					
					if (textlength <= name.length()) {
						if (DirectMessageUser.getText().toString().equalsIgnoreCase(
								(String) name.subSequence(0, textlength))) {
							AddObjectToUsersSearchList(obj.getUserId(),obj.getName(),obj.getScreenName());
							listFollowers.setVisibility(View.VISIBLE);
						}
					}
				}

				searchadapter = new ListViewCustomAdapterSearch(TwitterMessagesActivity.this, array_search);
				listFollowers.setAdapter(searchadapter);
				listFollowers.setOnItemClickListener(TwitterMessagesActivity.this);
				searchadapter.notifyDataSetChanged();
			}else{
				Toast.makeText(TwitterMessagesActivity.this, "Enter atleast one character", Toast.LENGTH_SHORT).show();
			}
     }

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:

			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.alert_dialog_direct_messages_reply, null);
			final EditText replyDirectMessage = (EditText) textEntryView
					.findViewById(R.id.et_direct_message_reply_text);

			return new AlertDialog.Builder(this)
					.setTitle("Send Message to " + replyTo)
					.setView(textEntryView)
					.setPositiveButton("Reply",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									try {
										twitter.sendDirectMessage(replyTo,
												replyDirectMessage.getText()
														.toString());
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		//System.out.println("arg1===="+arg1);
		if(arg0 == listMessages){
			TweetTimelineModel tM = (TweetTimelineModel) adapter.getItem(position);
			if (tM.getTimeline_tweet_user() != null) {
				openReplyRetweetDialog(tM);
			}
		}
		if(arg0 == listFollowers){
			DirectMessagesModel dM = (DirectMessagesModel) searchadapter.getItem(position);
			if (dM.getName() != null) {
				DirectMessageUser.setText(dM.getScreenName());
				listFollowers.setVisibility(View.GONE);
			}
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tweetTab.setSeletected(3);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		tweetTab.setSeletected(3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.twitter_menu_messages, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent;
		switch (item.getItemId()) {

		case R.id.menu_send_message:
			prepareUserArrayList();
			return true;

		case R.id.menu_refresh:
			itemList.clear();
			new ListMessagesTask().execute();
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

	class ListViewCustomAdapterSearch extends BaseAdapter {

		ArrayList<Object> usersList;

		public Activity context;
		public LayoutInflater inflater;

		public ListViewCustomAdapterSearch(Activity context,
				ArrayList<Object> itemList) {
			super();

			this.context = context;
			this.usersList = itemList;

			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return usersList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return usersList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView twit_user_name;;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.following_usres_row, null);

				holder.twit_user_name = (TextView) convertView
						.findViewById(R.id.twit_user_name);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			DirectMessagesModel dm = (DirectMessagesModel) usersList.get(position);

			holder.twit_user_name.setText(dm.getName());
			return convertView;
		}

	}
	class ListViewCustomAdapter1 extends BaseAdapter {

		ArrayList<Object> itemList;
		private DrawableManager drawableManager;
		public Activity context;
		public LayoutInflater inflater;
		private Map<Integer, ViewHolder> viewHolders;

		public ListViewCustomAdapter1(Activity context,
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
				convertView = inflater.inflate(R.layout.twitter_messages_row,
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

	private class ListMessagesTask extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				TwitterMessagesActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading Messages...");
			this.dialog.show();

		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(Void... args) {
			String response = null;

			try {
				twitter = factory.getInstance();
				User user = twitter.verifyCredentials();
				ResponseList<DirectMessage> statuses = twitter
						.getDirectMessages();
				String image_url = null;
				for (DirectMessage status : statuses) {

					image_url = status.getSender().getProfileImageURL()
							.toString();
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
					AddObjectToList(status.getText(), imageIcon,
							status.getSenderScreenName(), date);
				}
				response = "success";
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out
						.println("Failed to get timeline: " + te.getMessage());
				// System.exit(-1);
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			adapter = new ListViewCustomAdapter1(TwitterMessagesActivity.this,
					itemList);
			listMessages.setAdapter(adapter);
			listMessages.setOnItemClickListener(TwitterMessagesActivity.this);
			Boolean isSDPresent = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);
			if (isSDPresent && prefBackground != null) {
				decodeFile(prefBackground);
			} else if (prefBackgroundColor != 0) {
				listMessages.setBackgroundResource(prefBackgroundColor);
			}

			adapter.notifyDataSetChanged();

		}
	}
	private class ListUsersTask extends AsyncTask<String, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				TwitterMessagesActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading Users...");
			this.dialog.show();

		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(String... args) {
			String response = null;

			try {
				twitter = factory.getInstance();
				User user = twitter.verifyCredentials();
				long lCursor = -1;
				long[] IDArray = null;
				long[] IDArray2 = null;
				long[] IDArray3 = null;
				long[] IDArray4 = null;
				IDs friendsIDs = twitter.getFollowersIDs(user.getId(), lCursor);// user.getId()//37132276
				 if(friendsIDs.getIDs().length < 100){
						IDArray =	friendsIDs.getIDs();
					}
				 else if(friendsIDs.getIDs().length > 100 && friendsIDs.getIDs().length < 200){
					IDArray = new long[100];
					for(int i= 0; i< 100; i++){
						IDArray[i] =	friendsIDs.getIDs()[i];
					}
					IDArray2 = new long[friendsIDs.getIDs().length-100];
					int indexIDArray2 = 0;
					for(int j= 100; j< friendsIDs.getIDs().length; j++){
						//System.out.println("j ===="+j);
						IDArray2[indexIDArray2] =	friendsIDs.getIDs()[j];
						indexIDArray2++;
					}
				}
				 else if(friendsIDs.getIDs().length > 200 && friendsIDs.getIDs().length < 300){
						IDArray = new long[100];
						for(int i= 0; i< 100; i++){
							IDArray[i] =	friendsIDs.getIDs()[i];
						}
						IDArray2 = new long[100];
						int indexIDArray2 = 0;
						for(int j= 100; j< 200; j++){
							//System.out.println("j ===="+j);
							IDArray2[indexIDArray2] =	friendsIDs.getIDs()[j];
							indexIDArray2++;
						}
						
						IDArray3 = new long[friendsIDs.getIDs().length-200];
						int indexIDArray3 = 0;
						for(int i= 200; i< friendsIDs.getIDs().length; i++){
							IDArray3[indexIDArray3] =	friendsIDs.getIDs()[i];
							indexIDArray3++;
						}
						
					}
				else if(friendsIDs.getIDs().length > 300 && friendsIDs.getIDs().length < 400){
					IDArray = new long[100];
					for(int i= 0; i< 100; i++){
						IDArray[i] =	friendsIDs.getIDs()[i];
					}
					IDArray2 = new long[100];
					int indexIDArray2 = 0;
					for(int j= 100; j< 200; j++){
						//System.out.println("j ===="+j);
						IDArray2[indexIDArray2] =	friendsIDs.getIDs()[j];
						indexIDArray2++;
					}
					
					IDArray3 = new long[100];
					int indexIDArray3 = 0;
					for(int i= 200; i< 300; i++){
						IDArray3[indexIDArray3] =	friendsIDs.getIDs()[i];
						indexIDArray3++;
					}
					IDArray4 = new long[friendsIDs.getIDs().length-300];
					int  indexIDArray4 = 0;
					for(int j= 300; j< friendsIDs.getIDs().length; j++){
						IDArray4[indexIDArray4] =	friendsIDs.getIDs()[j];
						indexIDArray4++;
					}
				}
				else if(friendsIDs.getIDs().length > 400){
					IDArray = new long[100];
					for(int i= 0; i< 100; i++){
						IDArray[i] =	friendsIDs.getIDs()[i];
					}
					IDArray2 = new long[100];
					int indexIDArray2 = 0;
					for(int j= 100; j< 200; j++){
						//System.out.println("j ===="+j);
						IDArray2[indexIDArray2] =	friendsIDs.getIDs()[j];
						indexIDArray2++;
					}
					
					IDArray3 = new long[100];
					int indexIDArray3 = 0;
					for(int i= 200; i< 300; i++){
						IDArray3[indexIDArray3] =	friendsIDs.getIDs()[i];
						indexIDArray3++;
					}
					IDArray4 = new long[100];
					int  indexIDArray4 = 0;
					for(int j= 300; j< 400; j++){
						IDArray4[indexIDArray4] =	friendsIDs.getIDs()[j];
						indexIDArray4++;
					}
				}
				
				if(IDArray != null && IDArray.length > 0){
					ResponseList<User> users = twitter.lookupUsers(IDArray);
		            for (User followingUser : users) {
		            	
		            	AddObjectToUsersList(followingUser.getId(), followingUser.getName(),followingUser.getScreenName());
		            }
				}if(IDArray2 != null && IDArray2.length > 0){
					
					ResponseList<User> users2 = twitter.lookupUsers(IDArray2);
		            for (User followingUser1 : users2) {
		            	
		            	AddObjectToUsersList(followingUser1.getId(), followingUser1.getName(),followingUser1.getScreenName());
		            }
		            
				}
				if(IDArray3 != null && IDArray3.length > 0){
					ResponseList<User> users3 = twitter.lookupUsers(IDArray3);
		            for (User followingUser : users3) {
		            	AddObjectToUsersList(followingUser.getId(), followingUser.getName(),followingUser.getScreenName());
		            }
				}
				if(IDArray4 != null && IDArray4.length > 0){
					ResponseList<User> users4 = twitter.lookupUsers(IDArray4);
		            for (User followingUser : users4) {
		            	AddObjectToUsersList(followingUser.getId(), followingUser.getName(),followingUser.getScreenName());
		            }
				}
				
					/*do
					{
						for (long i : friendsIDs.getIDs())
					   {
							 AddObjectToUsersList(i, twitter.showUser(i).getName(),twitter.showUser(i).getScreenName());
					   }
					}while(friendsIDs.hasNext());
			*/

				response = "success";
			} catch (TwitterException te) {
				te.printStackTrace();
				System.out.println("Failed to get Users: " + te.getMessage());
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			//System.out.println("onPostExecute usersList.size() ===="+usersList.size());
			openDirectMesagesDialog();
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
}