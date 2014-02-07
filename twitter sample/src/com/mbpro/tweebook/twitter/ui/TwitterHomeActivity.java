package com.mbpro.tweebook.twitter.ui;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuth;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.twitter.Constants;
import com.mbpro.tweebook.twitter.model.TweetTimelineModel;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class TwitterHomeActivity extends BaseActivity implements
		OnItemClickListener {
	TwitterSegmentedTabMenu tweetTab;
	private SharedPreferences prefs;
	private ConfigurationBuilder cb;
	TweetTimelineModel tmodel;
	ListView listTimeline;
	private ArrayList<Object> itemList;
	ListViewCustomAdapter adapter;
	TwitterFactory factory;
	Twitter twitter;
	private static final int PICK_IMAGE = 1;
	private static final int CAPTURE = 2;
	private static final int REMOVE_BACKGROUND = 3;
	private Bitmap bitmap, bitmap_display;
	public static int imageflag = 0;
	Editor editor;
	String prefBackground;
	int prefBackgroundColor = 0;
	Uri mCapturedImageURI;
	static String replyTo;
	static long reply_user;
	static long tweet_status_id;
	static String retweet_message;
	int backgroundSelected = 0;
	String[] timelineOptions;
	String[] CombinedOptions;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.twitter_main_view);
		getActivityHelper().setupActionBar(getTitle(), 0);

		listTimeline = (ListView) findViewById(R.id.listTimeline);
		tweetTab = new TwitterSegmentedTabMenu(this);
		tweetTab.setSeletected(1);
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
		twitter = factory.getInstance();
		try {
			User user = twitter.verifyCredentials();
		} catch (TwitterException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

	public void openReplyRetweetDialog(final TweetTimelineModel obj) {
		twitter = factory.getInstance();
		User user = null;

		
		try {
			user = twitter.verifyCredentials();

		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (user.getId() == obj.getTweet_user_id() || obj.getIsRetweetedByMe()) {
			timelineOptions = new String[] { "Reply", };
			
		} else {
			timelineOptions = new String[] { "Reply", "Retweet"};
		}
		if(obj.getTweet_url_entities() != null && obj.getTweet_media_entities() != null){
			CombinedOptions = merge(timelineOptions,obj.getTweet_url_entities(),obj.getTweet_media_entities());
		}else if(obj.getTweet_url_entities() != null){
			CombinedOptions = merge(timelineOptions,obj.getTweet_url_entities());
		}else if(obj.getTweet_media_entities() != null){
			CombinedOptions = merge(timelineOptions,obj.getTweet_media_entities());
		}else{
			CombinedOptions = merge(timelineOptions);
		}
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("TweeBook");
		dialog.setItems(CombinedOptions, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int id) {

				if (id == 0) {
					replyTo = obj.getTimeline_tweet_user();
					reply_user = obj.getTweet_user_id();
					showDialog(1);
				}
				if(timelineOptions.length <2){
					if(id > 0){
						Intent intent1 = new Intent(
								TwitterHomeActivity.this,
								TweebookWebViewController.class);
						intent1.putExtra("url",CombinedOptions[id]);
						startActivity(intent1);
					}
				}else{
					if (id == 1) {
						tweet_status_id = obj.getStatus_id();
						retweet_message = obj.getTimeline_tweetText();
						showDialog(2);
					}
					if(id > 1){
						Intent intent1 = new Intent(
								TwitterHomeActivity.this,
								TweebookWebViewController.class);
						intent1.putExtra("url",CombinedOptions[id]);
						startActivity(intent1);
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
	public static <T> int totalLength(T[]... arrays) {
        int length = 0;
        for(T[] arr : arrays) length += arr.length;
        return length;
    }

    public static <T> T[] merge(T[]... arrays) {
        int length = totalLength(arrays);
        if(length == 0) return (T[])new Object[0];
        List<T> list = new ArrayList<T>(length);
        for(T[] array : arrays) {
            for(T t : array) {
                list.add(t);
            }
        }
        return (T[])(list.toArray(arrays[0])); 
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
		case 2:
			return new AlertDialog.Builder(this)
					.setTitle("Tweebook Retweet")
					.setMessage(retweet_message)
					.setPositiveButton("Retweet",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									try {
										Status val = twitter
												.retweetStatus(tweet_status_id);
										//System.out.println("val" + val);
									} catch (TwitterException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									removeDialog(2);
								}

							})
					.setNegativeButton("cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									removeDialog(2);
								}
							}).create();
		case 3:
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
					listTimeline.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorRed.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_red;
					listTimeline.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorBlack.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_black;
					listTimeline.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorPink.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_pink;
					listTimeline.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorPurple.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_purple;
					listTimeline.setBackgroundResource(backgroundSelected);
				}
			});
			btncolorGreen.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					backgroundSelected = R.color.time_line_background_green;
					listTimeline.setBackgroundResource(backgroundSelected);
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
													"background_preference",
													Context.MODE_PRIVATE)
											.edit();
									editor.remove("background_image");
									editor.remove("background_color");
									editor.putInt("background_color",
											backgroundSelected);
									editor.commit();
									removeDialog(3);
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
										listTimeline
												.setBackgroundResource(prefBackgroundColor);
									} else {
										listTimeline
												.setBackgroundResource(R.drawable.bg);
									}
									removeDialog(3);
								}
							}).create();
		}
		return null;

	}

	public void prepareArrayList() {
		itemList = new ArrayList<Object>();
		new ListTimelineTask().execute();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		tweetTab.setSeletected(1);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();
		tweetTab.setSeletected(1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.home_page_menu_items, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final Intent intent;

		switch (item.getItemId()) {
		case R.id.menu_refresh:
			itemList.clear();
			new ListTimelineTask().execute();
			return true;
		case R.id.menu_change_background:
			openAddPhoto();
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
			String timeline_date, boolean IsRetweetedByMe, boolean isRetweet,
			long tweetReplyUserId, long statusId,String[] urlArray,String[] mediaArray) {
		// aModel = new ArtistModel(id,name,location,image);
		tmodel = new TweetTimelineModel(timeline_tweetText,
				timeline_profile_image, timeline_tweet_user, timeline_date,
				IsRetweetedByMe, isRetweet, tweetReplyUserId, statusId,urlArray,mediaArray);
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
				convertView = inflater.inflate(R.layout.tweet_timeline_row,
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

	private class ListTimelineTask extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(
				TwitterHomeActivity.this);

		// can use UI thread here
		protected void onPreExecute() {
			this.dialog.setMessage("Loading Timeline...");
			this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected String doInBackground(Void... args) {
			String response = null;

			try {
				// gets Twitter instance with default credentials
				twitter = factory.getInstance();
				User user = twitter.verifyCredentials();
				List<twitter4j.Status> statuses = twitter.getHomeTimeline();
				String image_url = null;
				String name = null;
				String [] urlArray = null;
				String [] mediaArray = null;
				long tweet_reply_user_id = 0;
				long status_id = 0;
				boolean isRetweet = false;
				boolean isRetweetbyMe = false;
		
				for (twitter4j.Status status : statuses) {
					status_id = status.getId();
					
					URLEntity[] uent = status.getURLEntities();
					MediaEntity[] ment = status.getMediaEntities();
					
					if (uent != null && uent.length > 0) {
						urlArray = new String[uent.length];
						for (int k = 0; k < uent.length; k++) {
							urlArray[k] = uent[k].getURL().toString();
					       }
					}else{
						urlArray = null;
					}

					if (ment != null && ment.length > 0) {
						mediaArray = new String[ment.length];
						for (int m = 0; m < ment.length; m++) {
							mediaArray[m] = ment[m].getURL().toString();
					      }
					}else{
						mediaArray = null;
					}


					if (status.isRetweet()) {
						isRetweet = true;
						// System.out.println("getInReplyToUserId ===  "+
						// status.getRetweetedStatus().getUser().getId());
						image_url = status.getRetweetedStatus().getUser()
								.getProfileImageURL().toString();
						name = status.getRetweetedStatus().getUser()
								.getScreenName();
						tweet_reply_user_id = status.getRetweetedStatus()
								.getUser().getId();
					} else {
						isRetweet = false;
						image_url = status.getUser().getProfileImageURL()
								.toString();
						name = status.getUser().getScreenName();
						tweet_reply_user_id = status.getUser().getId();
					}
					if (status.isRetweetedByMe()) {
						isRetweetbyMe = true;

					} else {
						isRetweetbyMe = false;
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

					AddObjectToList(status.getText(), imageIcon, name, date,
							isRetweetbyMe, isRetweet, tweet_reply_user_id,
							status_id,urlArray,mediaArray);
				}
				response = "success";
			} catch (TwitterException te) {
				te.printStackTrace();
				//System.out
						//.println("Failed to get timeline: " + te.getMessage());
				System.exit(-1);
			}
			return response;
		}

		protected void onPostExecute(final String response) {
			if (this.dialog.isShowing()) {
				this.dialog.dismiss();
			}
			adapter = new ListViewCustomAdapter(TwitterHomeActivity.this,
					itemList);
			listTimeline.setAdapter(adapter);
			listTimeline.setOnItemClickListener(TwitterHomeActivity.this);
			Boolean isSDPresent = android.os.Environment
					.getExternalStorageState().equals(
							android.os.Environment.MEDIA_MOUNTED);

			if (isSDPresent && prefBackground != null) {
				decodeFile(prefBackground);
			} else if (prefBackgroundColor != 0) {
				listTimeline.setBackgroundResource(prefBackgroundColor);
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

	public void openAddPhoto() {

		String[] addPhoto = new String[] { "Camera", "Gallery", "Change Color",
				"Remove Background" };
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
				if (id == 3) {
					listTimeline.setBackgroundResource(R.drawable.bg);
					editor = getApplicationContext().getSharedPreferences(
							"background_preference", Context.MODE_PRIVATE)
							.edit();
					editor.remove("background_image");
					editor.remove("background_color");
					editor.commit();
				}
				if (id == 2) {
					showDialog(3);
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
								"background_preference", Context.MODE_PRIVATE)
								.edit();
						editor.remove("background_image");
						editor.remove("background_color");
						editor.putString("background_image", filePath);
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
						"background_preference", Context.MODE_PRIVATE).edit();
				editor.remove("background_image");
				editor.remove("background_color");
				editor.putString("background_image", capturedImageFilePath);
				editor.commit();
				decodeFile(capturedImageFilePath);

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
			bitmap_display = getResizedBitmap(bitmap, 320, 480);
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
			Drawable drawable = new BitmapDrawable(getResources(), bitmap);
			listTimeline.setBackgroundDrawable(drawable);
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