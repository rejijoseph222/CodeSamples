package me.cexi.tweetmaster.model;

import java.util.Calendar;

import android.graphics.drawable.Drawable;

/**
 * Represents the model of tweets
 * 
 * 
 */
public class BlisdTweetModel {
	private Long id;
	private String fromUser;
	private String fromUserName;
	private String toUserName;
	private String toUser;
	private String text;
	private String textLink;
	private String textMediaLink;
	private Calendar created;
	private String createdDate;
	private String profileImageUrl;
	private Drawable profileImage;

	/** Constructor with no parameters**/
	public BlisdTweetModel()
	{
	}
	/** Constructor with all Attributes**/
	public BlisdTweetModel(String fromUser, String fromUserName,
			String toUserName, String toUser, String text, String textLink,
			String created, String profileImageUrl){
		this.fromUser = fromUser;
		this.fromUserName = fromUserName;
		this.toUserName = toUserName;
		this.toUser = toUser;
		this.text = text;
		this.textLink = textLink;
		this.createdDate = created;
		this.profileImageUrl = profileImageUrl;
		
	}
	/** getting  Tweet Id**/
	public Long getId() {
		return id;
	}
	/** Setting up Tweet Id**/
	public void setId(Long id) {
		this.id = id;
	}
	/** getting  from user**/
	public String getFromUser() {
		return fromUser;
	}
	/** Setting up From User**/
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	/** getting  from user name**/
	public String getFromUserName() {
		return fromUserName;
	}
	/** setting from  user name**/
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	/** getting  to user**/
	public String getToUser() {
		return toUser;
	}
	/** setting  to  user **/
	public void setToUser(String toUser) {
		if(toUser == null){
			toUser = "novalue";
		}
		this.toUser = toUser;
	}
	/** getting  to  user name**/
	public String getToUserName() {
		if(toUserName == null){
			toUserName = "novalue";
		}
		return toUserName;
	}
	/** setting  to  user name**/
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	
	/** getting  tweet**/
	public String getText() {
		
		return text;
	}
	/** setting  tweet**/
	public void setText(String text) {
		this.text = text;
	}
	/** getting  tweet date**/
	public Calendar getCreated() {
		return created;
	}
	/** setting up tweet date**/
	public void setCreated(Calendar created) {
		this.created = created;
	}
	/** getting  tweet date**/
	public String getTweetDate() {
		return createdDate;
	}
	/** setting up tweet date**/
	public void setTweetDate(String createdDate) {
		this.createdDate = createdDate;
	}
	/** getting  profile icon url**/
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
	/** setting up profile icon url**/
	public void setProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
	/** getting  profile icon drawable**/
	public Drawable getProfileImage() {
		return profileImage;
	}
	/** setting up profile icon drawable***/
	public void setProfileImage(Drawable profileImage) {
		this.profileImage = profileImage;
	}
	/** getting  additional links**/
	public String getTextLink() {
		if(textLink == null){
			textLink = "novalue";
		}
		return textLink;
	}
	/** setting up additional links**/
	public void setTextLink(String textLink) {
		this.textLink = textLink;
	}
	/** getting  additional media links**/
	public String getTextMediaLink() {
		if(textMediaLink == null){
			textMediaLink = "novalue";
		}
		return textMediaLink;
	}
	/** setting up additional media links**/
	public void setTextMediaLink(String textMediaLink) {
		this.textMediaLink = textMediaLink;
	}
}
