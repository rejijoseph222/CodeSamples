package com.mbpro.tweebook.facebook;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.facebook.android.library.AsyncFacebookRunner;
import com.facebook.android.library.FacebookError;
import com.mbpro.tweebook.BuildConfig;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.images.FaceBookAlbumImages;
import com.mbpro.tweebook.facebook.images.FacebookImageDetailActivity;
import com.mbpro.tweebook.facebook.images.util.ImageCache;
import com.mbpro.tweebook.facebook.images.util.ImageCache.ImageCacheParams;
import com.mbpro.tweebook.facebook.images.util.ImageFetcher;
import com.mbpro.tweebook.facebook.images.util.ImageResizer;
import com.mbpro.tweebook.facebook.images.util.Utils;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;
import com.mbpro.tweebook.ui.BaseActivity;
import com.mbpro.tweebook.ui.MainActivity;

public class FacebookListAlbumPhotos extends BaseActivity implements
OnItemClickListener {
	ProgressDialog dialog;
	private static final String TAG = "Facebook Album Images";

	private Handler mHandler;
	private SharedPreferences prefs;
	Intent albumIntent;
	private static String albumID;
	
    private static final String IMAGE_CACHE_DIR = "thumbs";

    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageResizer mImageWorker;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mHandler = new Handler();
		setContentView(R.layout.facebook_album_image_grid_fragment);
		getActivityHelper().setupActionBar(getTitle(), 0);
		
		
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
				
		albumIntent = getIntent();
		if(albumIntent.hasExtra("album_id")){
			albumID = albumIntent.getStringExtra("album_id");
			getAlbumImages(albumID);
		}
		

	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getActivityHelper().setupHomeActivity();

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
			getAlbumImages(albumID);
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
		/*case R.id.menu_clear_cache:
			 final ImageCache cache = mImageWorker.getImageCache();
             if (cache != null) {
                 mImageWorker.getImageCache().clearCaches();
                 DiskLruCache.clearCache(this, ImageFetcher.HTTP_CACHE_DIR);
                 Toast.makeText(this, R.string.clear_cache_complete,
                         Toast.LENGTH_SHORT).show();
             }
			return true;*/
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
					final Intent intent1 = new Intent(FacebookListAlbumPhotos.this,
							MainActivity.class);
					intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					intent1.putExtra("current_tab", "f");
					FacebookListAlbumPhotos.this.startActivity(intent1);
				}
			});
		}
	}

	public void getAlbumImages(String AlbumId) {
		if (!TextUtils.isEmpty(AlbumId)) {
			dialog = ProgressDialog.show(this, "", "Loading Photos",
					true, true);
			Bundle params = new Bundle();
			params.putString("limit","100");
			params.putString("fields","id,picture,source");
			Utility.mAsyncRunner.request(AlbumId+"/photos", params,
					new AlbumImagesRequestListener());

		}
	}


	@Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(FacebookListAlbumPhotos.this, FacebookImageDetailActivity.class);
        i.putExtra(FacebookImageDetailActivity.EXTRA_IMAGE, (int) id);
        startActivity(i);
    }

	/*
	 * Callback after a given Graph API request is executed Get the response and
	 * show it.
	 */
	public class AlbumImagesRequestListener extends BaseRequestListener {
		String id = "";
		String picture = "";
		String source = "";
		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();

			JSONObject jsonObject;
			try {
				// System.out.println("response ====" + response);
				jsonObject = new JSONObject(response);
				if (jsonObject.has("data")) {
					JSONArray dataArray = jsonObject.getJSONArray("data");
					FaceBookAlbumImages.albumImageUrls = new String[dataArray.length()];
					FaceBookAlbumImages.albumImageThumbUrls = new String[dataArray.length()];
					int albumImageUrlsIndex = 0;
					int albumThumbImageUrlsIndex = 0;
					for (int dataIndex = 0; dataIndex < dataArray.length(); dataIndex++) {
						if (dataArray.getJSONObject(dataIndex).has("id")) {
							id = dataArray.getJSONObject(dataIndex).getString("id");
						}
						if (dataArray.getJSONObject(dataIndex).has("picture")) {
							picture = dataArray.getJSONObject(dataIndex).getString("picture");
							FaceBookAlbumImages.albumImageThumbUrls[albumThumbImageUrlsIndex] = picture;
							albumThumbImageUrlsIndex++;
						}
						if (dataArray.getJSONObject(dataIndex).has("source")) {
							source = dataArray.getJSONObject(dataIndex).getString("source");
							FaceBookAlbumImages.albumImageUrls[albumImageUrlsIndex] = source;
							albumImageUrlsIndex++;
						}
						
					}
					
				}
				

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					 mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
				        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

				        mAdapter = new ImageAdapter(FacebookListAlbumPhotos.this);

				        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);

				        // Allocate a third of the per-app memory limit to the bitmap memory cache. This value
				        // should be chosen carefully based on a number of factors. Refer to the corresponding
				        // Android Training class for more discussion:
				        // http://developer.android.com/training/displaying-bitmaps/
				        // In this case, we aren't using memory for much else other than this activity and the
				        // ImageDetailActivity so a third lets us keep all our sample image thumbnails in memory
				        // at once.
				        cacheParams.memCacheSize = 1024 * 1024 * Utils.getMemoryClass(FacebookListAlbumPhotos.this) / 3;

				        // The ImageWorker takes care of loading images into our ImageView children asynchronously
				        mImageWorker = new ImageFetcher(FacebookListAlbumPhotos.this, mImageThumbSize);
				        mImageWorker.setAdapter(FaceBookAlbumImages.imageThumbWorkerUrlsAdapter);
				        mImageWorker.setLoadingImage(R.drawable.ic_launcher);
				        mImageWorker.setImageCache(ImageCache.findOrCreateCache(FacebookListAlbumPhotos.this, cacheParams));
					/*if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
			            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			            ft.add(android.R.id.content, new FacebookImageGridFragment(), TAG);
			            ft.commit();*/
				        
				        
				        final GridView mGridView = (GridView) findViewById(R.id.gridView);
				        mGridView.setAdapter(mAdapter);
				        mGridView.setOnItemClickListener(FacebookListAlbumPhotos.this);
				     
				        // This listener is used to get the final width of the GridView and then calculate the
				        // number of columns and the width of each column. The width of each column is variable
				        // as the GridView has stretchMode=columnWidth. The column width is used to set the height
				        // of each view so we get nice square thumbnails.
				        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				                new ViewTreeObserver.OnGlobalLayoutListener() {
				                    @Override
				                    public void onGlobalLayout() {
				                        if (mAdapter.getNumColumns() == 0) {
				                            final int numColumns = (int) Math.floor(
				                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
				                            if (numColumns > 0) {
				                                final int columnWidth =
				                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
				                                mAdapter.setNumColumns(numColumns);
				                                mAdapter.setItemHeight(columnWidth);
				                                if (BuildConfig.DEBUG) {
				                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
				                                }
				                            }
				                        }
				                    }
				                });
				}
			});
			
	        
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();

		}

	}
	 
	/**
     * The main adapter that backs the GridView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = -1;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        @Override
        public int getCount() {
            // Size of adapter + number of columns for top empty row
            return mImageWorker.getAdapter().getSize() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : mImageWorker.getAdapter().getItem(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            // First check if this is the top row
            if (position < mNumColumns) {
                if (convertView == null) {
                    convertView = new View(mContext);
                }
                // Calculate ActionBar height
                if (mActionBarHeight < 0) {
                    TypedValue tv = new TypedValue();
                    if (mContext.getTheme().resolveAttribute(
                            android.R.attr.actionBarSize, tv, true)) {
                        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                                tv.data, mContext.getResources().getDisplayMetrics());
                    } else {
                        // No ActionBar style (pre-Honeycomb or ActionBar not in theme)
                        mActionBarHeight = 0;
                    }
                }
                // Set empty view with height of ActionBar
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
                return convertView;
            }

            // Now handle the main ImageView thumbnails
            ImageView imageView;
            if (convertView == null) { // if it's not recycled, instantiate and initialize
                imageView = new ImageView(mContext);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(mImageViewLayoutParams);
            } else { // Otherwise re-use the converted view
                imageView = (ImageView) convertView;
            }

            // Check the height matches our calculated column width
            if (imageView.getLayoutParams().height != mItemHeight) {
                imageView.setLayoutParams(mImageViewLayoutParams);
            }

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            mImageWorker.loadImage(position - mNumColumns, imageView);
            return imageView;
        }

        /**
         * Sets the item height. Useful for when we know the column width so the height can be set
         * to match.
         *
         * @param height
         */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageWorker.setImageSize(height);
            notifyDataSetChanged();
        }

        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
}
