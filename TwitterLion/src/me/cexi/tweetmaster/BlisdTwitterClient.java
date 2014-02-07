package me.cexi.tweetmaster;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.zip.GZIPInputStream;

import me.cexi.tweetmaster.model.BlisdTweetModel;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.Html;
import android.util.Log;

/**
 * Provides methods to retrieve Tweets using Twitter's API.
 * 
 */
public class BlisdTwitterClient {
	private static final String SEARCH_URI = "http://search.twitter.com/search.json?q=%s&rpp=20&include_entities=true&result_type=mixed";
	private static final String TAG = "TwitterClient";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"EEE, d MMM yyyy HH:mm:ss Z");

	private HttpClient httpClient;

	/**
	 * Constructor
	 */
	public BlisdTwitterClient() {
		httpClient = new DefaultHttpClient();
	}

	/**
	 * Retrieves the latest tweets that match the keywords specified.
	 * 
	 * @param query
	 *            The keywords to search.
	 * @return A list of the latest tweets the match the search keywords.
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ParseException
	 */
	public List<BlisdTweetModel> search(String query)
			throws ClientProtocolException, IOException, JSONException,
			ParseException {
		List<BlisdTweetModel> tweets = new ArrayList<BlisdTweetModel>();
		URI uri;
		try {
			uri = new URI(String.format(SEARCH_URI, URLEncoder.encode(query,
					"UTF-8")));
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

		String response = sendGetRequest(uri);

		if (response == null) {
			throw new IOException("Failed to read response");
		}
		JSONObject jsonResponse = new JSONObject(response);
		JSONArray jsonResults = jsonResponse.getJSONArray("results");
		for (int i = 0; i < jsonResults.length(); i++) {
			tweets.add(parseTweet(jsonResults.getJSONObject(i)));
		}
		Log.i(TAG, "<jsonobject>\n" + jsonResponse.toString()
				+ "\n</jsonobject>");

		return tweets;
	}

	/**
	 * Sends a get HTTP request and retrieves the response body.
	 * 
	 * @param uri
	 *            The URI of the HTTP resource to query
	 * @return The body of the HTTP response
	 * @throws IOException
	 */
	private String sendGetRequest(URI uri) throws IOException {
		HttpGet request = new HttpGet(uri);
		HttpResponse response = httpClient.execute(request);
		HttpEntity entity = response.getEntity();

		if (entity != null) {
			InputStream inputStream = entity.getContent();
			try {
				Header contentEncoding = response
						.getFirstHeader("Content-Encoding");
				if (contentEncoding != null
						&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
					inputStream = new GZIPInputStream(inputStream);
				}
				return readStream(inputStream);
			} finally {
				inputStream.close();
			}

		}
		return null;
	}

	/**
	 * Converts Twitter's JSON response to a Tweet object.
	 * 
	 * @param jsonTweet
	 *            Twitter's JSON response.
	 * @return A Tweet object containing the information of the tweet.
	 * @throws JSONException
	 * @throws ParseException
	 */
	private static BlisdTweetModel parseTweet(JSONObject jsonTweet) throws JSONException, ParseException {
		
			BlisdTweetModel tweet = new BlisdTweetModel();
			tweet.setId(jsonTweet.getLong("id"));
			tweet.setFromUser(jsonTweet.getString("from_user"));
			tweet.setFromUserName(jsonTweet.getString("from_user_name"));
			tweet.setToUser(jsonTweet.getString("to_user"));
			tweet.setToUserName(jsonTweet.getString("to_user_name"));
			JSONObject jsonEntities = jsonTweet.getJSONObject("entities");
			if (jsonEntities.has("urls")) {
				JSONArray jsonUrlsArray = jsonEntities.getJSONArray("urls");
				for (int i = 0; i < jsonUrlsArray.length(); i++) {
					JSONObject jsonUrl = jsonUrlsArray.getJSONObject(i);
					if (jsonUrl.has("url")) {
						tweet.setTextLink(jsonUrl.getString("url"));
					} else {
						tweet.setTextLink(null);
					}
				}
			}
			if (jsonEntities.has("media")) {
				JSONArray jsonUrlsArray = jsonEntities.getJSONArray("media");
				for (int i = 0; i < jsonUrlsArray.length(); i++) {
					JSONObject jsonUrl = jsonUrlsArray.getJSONObject(i);
					if (jsonUrl.has("url")) {
						tweet.setTextMediaLink(jsonUrl.getString("url"));
					} else {
						tweet.setTextMediaLink("novalue");
					}
				}
			}

			tweet
					.setText(Html.fromHtml(jsonTweet.getString("text"))
							.toString());
			tweet.setTweetDate(jsonTweet.getString("created_at"));
			Calendar created = Calendar.getInstance();
			created.setTimeInMillis(DATE_FORMAT.parse(
					jsonTweet.getString("created_at")).getTime());
			tweet.setCreated(created);
			tweet.setProfileImageUrl(jsonTweet.getString("profile_image_url"));
			return tweet;
		
	}

	/**
	 * Reads the input stream and returns the string read from it.
	 * 
	 * @param inputStream
	 *            The InputStream to read.
	 * @return The string read from the InputStream.
	 */
	private static String readStream(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				inputStream));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
