package com.mbpro.tweebook.twitter.model;

import android.graphics.drawable.Drawable;

public class TweetTimelineModel {

	String timeline_tweetText, timeline_profile_imageurl, timeline_tweet_user,
			timeline_date;
	String[] tweet_url_entities,tweet_media_entities;
	private Drawable profileImage;
	boolean IsRetweetedByMe, isRetweet;
	long tweet_user_id;
	long status_id;

	public TweetTimelineModel() {

	}

	public TweetTimelineModel(String timeline_tweetText,
			Drawable timeline_profile_imageurl, String timeline_tweet_user,
			String timeline_date) {
		this.profileImage = timeline_profile_imageurl;
		this.timeline_tweetText = timeline_tweetText;
		this.timeline_tweet_user = timeline_tweet_user;
		this.timeline_date = timeline_date;

	}

	public TweetTimelineModel(String timeline_tweetText,
			Drawable timeline_profile_imageurl, String timeline_tweet_user,
			String timeline_date, boolean IsRetweetedByMe, boolean isRetweet,
			long tweetReplyUserId, long statusId,String[] tweet_url_entities,String[] tweet_media_entities) {
		this.profileImage = timeline_profile_imageurl;
		this.timeline_tweetText = timeline_tweetText;
		this.timeline_tweet_user = timeline_tweet_user;
		this.timeline_date = timeline_date;
		this.IsRetweetedByMe = IsRetweetedByMe;
		this.isRetweet = isRetweet;
		this.tweet_user_id = tweetReplyUserId;
		this.status_id = statusId;
		this.tweet_url_entities = tweet_url_entities;
		this.tweet_media_entities = tweet_media_entities;
	}

	public String[] getTweet_media_entities() {
		return tweet_media_entities;
	}

	public void setTweet_media_entities(String[] tweet_media_entities) {
		this.tweet_media_entities = tweet_media_entities;
	}

	public String[] getTweet_url_entities() {
		return tweet_url_entities;
	}

	public void setTweet_url_entities(String[] tweet_url_entities) {
		this.tweet_url_entities = tweet_url_entities;
	}

	public void setTweet_user_id(long tweet_user_id) {
		this.tweet_user_id = tweet_user_id;
	}

	public long getStatus_id() {
		return status_id;
	}

	public void setStatus_id(long statusId) {
		status_id = statusId;
	}

	public long getTweet_user_id() {
		return tweet_user_id;
	}

	public void setTweet_user_id(int tweetUserId) {
		tweet_user_id = tweetUserId;
	}

	public boolean getIsRetweetedByMe() {
		return IsRetweetedByMe;
	}

	public void setIsRetweetedByMe(boolean isRetweetedByMe) {
		this.IsRetweetedByMe = isRetweetedByMe;
	}

	public boolean isRetweet() {
		return isRetweet;
	}

	public void setRetweet(boolean isRetweet) {
		this.isRetweet = isRetweet;
	}

	public String getTimeline_tweetText() {
		return timeline_tweetText;
	}

	public void setTimeline_tweetText(String timelineTweetText) {
		timeline_tweetText = timelineTweetText;
	}

	public String getTimeline_profile_imageurl() {
		return timeline_profile_imageurl;
	}

	public void setTimeline_profile_imageurl(String timelineProfileImageurl) {
		timeline_profile_imageurl = timelineProfileImageurl;
	}

	public String getTimeline_tweet_user() {
		return timeline_tweet_user;
	}

	public void setTimeline_tweet_user(String timelineTweetUser) {
		timeline_tweet_user = timelineTweetUser;
	}

	public String getTimeline_date() {
		return timeline_date;
	}

	public void setTimeline_date(String timelineDate) {
		timeline_date = timelineDate;
	}

	/** getting profile icon drawable **/
	public Drawable getProfileImage() {
		return profileImage;
	}

	/** setting up profile icon drawable ***/
	public void setProfileImage(Drawable profileImage) {
		this.profileImage = profileImage;
	}
}
