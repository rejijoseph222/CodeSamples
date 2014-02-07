package com.mbpro.tweebook.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.twitter.PrepareRequestTokenActivity;
import com.mbpro.tweebook.twitter.TwitterUtils;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;

public class TwitterFragment extends Fragment {

	// private static final String TAG = "TwitterFragment";

	Button twitterLogin;
	private SharedPreferences prefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(R.layout.twitter_tabview,
				null);
		twitterLogin = (Button) root.findViewById(R.id.img_btn_login_twitter);
		twitterLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (TwitterUtils.isAuthenticated(prefs)) {
					Intent i = new Intent(getActivity(),
							TwitterHomeActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent(getActivity(),
							PrepareRequestTokenActivity.class);
					startActivity(i);
				}
			}
		});
		return root;
	}

}