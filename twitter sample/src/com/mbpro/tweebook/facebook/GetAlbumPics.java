package com.mbpro.tweebook.facebook;

import java.util.Hashtable;
import java.util.Stack;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

public class GetAlbumPics {

	Hashtable<String, Bitmap> albumImages;
	Hashtable<String, String> positionRequested;
	BaseAdapter listener;
	int runningCount = 0;
	Stack<ItemPair> queue;

	/*
	 * 15 max async tasks at any given time.
	 */
	final static int MAX_ALLOWED_TASKS = 30;

	public GetAlbumPics() {
		albumImages = new Hashtable<String, Bitmap>();
		positionRequested = new Hashtable<String, String>();
		queue = new Stack<ItemPair>();
	}

	/*
	 * Inform the listener when the image has been downloaded. listener is
	 * FriendsList here.
	 */
	public void setListener(BaseAdapter listener) {
		this.listener = listener;
		reset();
	}

	public void reset() {
		positionRequested.clear();
		runningCount = 0;
		queue.clear();
	}

	/*
	 * If the profile picture has already been downloaded and cached, return it
	 * else execute a new async task to fetch it - if total async tasks >15,
	 * queue the request.
	 */
	public Bitmap getImage(String album_id, String url) {
		Bitmap image = albumImages.get(album_id);
		if (image != null) {
			return image;
		}
		if (!positionRequested.containsKey(album_id)) {
			positionRequested.put(album_id, "");
			if (runningCount >= MAX_ALLOWED_TASKS) {
				queue.push(new ItemPair(album_id, url));
			} else {
				runningCount++;
				new GetProfilePicAsyncTask().execute(album_id, url);
			}
		}
		return null;
	}

	public void getNextImage() {
		if (!queue.isEmpty()) {
			ItemPair item = queue.pop();
			new GetProfilePicAsyncTask().execute(item.album_id, item.url);
		}
	}

	/*
	 * Start a AsyncTask to fetch the request
	 */
	private class GetProfilePicAsyncTask extends
			AsyncTask<Object, Void, Bitmap> {
		String album_id;

		@Override
		protected Bitmap doInBackground(Object... params) {
			this.album_id = (String) params[0];
			String url = (String) params[1];
			return Utility.getBitmap(url);
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			runningCount--;
			if (result != null) {
				albumImages.put(album_id, result);
				listener.notifyDataSetChanged();
				getNextImage();
			}
		}
	}

	class ItemPair {
		String album_id;
		String url;

		public ItemPair(String album_id, String url) {
			this.album_id = album_id;
			this.url = url;
		}
	}

}
