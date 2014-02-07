package com.mbpro.tweebook.twitter;

public class Constants {

	public static final String CONSUMER_KEY = "pVieMoqaXUZe2tQkmpr1oA";// KGPAsnFIBlvmzKZGTWkGA
	public static final String CONSUMER_SECRET = "wRF59EjCdocImR7xqFQuOjrK55lKrODXjjjxMJkaAE";// pMhr3cqXCb7gzJchV3aBKwTkepbYgtX1zTN085rgzc
	public static final String TWITPIC_API_KEY = "46944142c217f8f1b16077a53c86a42f";

	/*
	 * public static final String CONSUMER_KEY = "pVieMoqaXUZe2tQkmpr1oA";
	 * public static final String CONSUMER_SECRET=
	 * "wRF59EjCdocImR7xqFQuOjrK55lKrODXjjjxMJkaAE";
	 */

	public static final String REQUEST_URL = "https://api.twitter.com/oauth/request_token";
	public static final String ACCESS_URL = "https://api.twitter.com/oauth/access_token";
	public static final String AUTHORIZE_URL = "https://api.twitter.com/oauth/authorize";

	public static final String OAUTH_CALLBACK_SCHEME = "x-oauthflow-twitter";
	public static final String OAUTH_CALLBACK_HOST = "callback";
	public static final String OAUTH_CALLBACK_URL = OAUTH_CALLBACK_SCHEME
			+ "://" + OAUTH_CALLBACK_HOST;

}
