package com.mbpro.tweebook.facebook.model;

import android.graphics.drawable.Drawable;

public class FacebookNewsFeedModel {

	String newsFeedText, newfeed_profile_imageurl, newsfeed_user,
			newsfeed_date;
	private Drawable profileImage;

	public FacebookNewsFeedModel() {

	}

	public FacebookNewsFeedModel(String newsFeedText, Drawable profileImage,
			String newsfeed_user, String newsfeed_date) {
		this.profileImage = profileImage;
		this.newsFeedText = newsFeedText;
		this.newsfeed_user = newsfeed_user;
		this.newsfeed_date = newsfeed_date;
	}

	public String getNewsFeedText() {
		return newsFeedText;
	}

	public void setNewsFeedText(String newsFeedText) {
		this.newsFeedText = newsFeedText;
	}

	public String getNewfeed_profile_imageurl() {
		return newfeed_profile_imageurl;
	}

	public void setNewfeed_profile_imageurl(String newfeedProfileImageurl) {
		newfeed_profile_imageurl = newfeedProfileImageurl;
	}

	public String getNewsfeed_user() {
		return newsfeed_user;
	}

	public void setNewsfeed_user(String newsfeedUser) {
		newsfeed_user = newsfeedUser;
	}

	public String getNewsfeed_date() {
		return newsfeed_date;
	}

	public void setNewsfeed_date(String newsfeedDate) {
		newsfeed_date = newsfeedDate;
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
