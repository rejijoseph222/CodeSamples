package com.mbpro.tweebook.twitter.model;

public class DirectMessagesModel {

	private String name;
	private String screen_name;
	private long id;

	public DirectMessagesModel(long id,String name,String screen_name) {
		this.name = name;
		this.id = id;
		this.screen_name = screen_name;
	}

	public String getScreenName() {
		return screen_name;
	}

	public void setScreenName(String screen_name) {
		this.screen_name = screen_name;
	}

	public String getName() {
		return name;
	}

	public long getUserId() {
		return id;
	}

	public String toString() {
		return name = name.substring(0, 1).toUpperCase() + name.substring(1);
	}
}
