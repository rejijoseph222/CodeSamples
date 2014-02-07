package com.mbpro.tweebook.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.facebook.android.library.Facebook;
import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.twitter.ui.TwitterHomeActivity;

public class FacebookFragment extends Fragment {

	Button facebookLogin;
	private Facebook mFb;
	public static final String APP_ID = "419262621422240";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFb = new Facebook(APP_ID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup root = (ViewGroup) inflater.inflate(
				R.layout.facebook_main_view, null);
		facebookLogin = (Button) root.findViewById(R.id.img_btn_login_facebook);
		facebookLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				if (!mFb.isSessionValid()) {
					Intent i = new Intent(getActivity(),
							FacebookMainActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent(getActivity(),
							TwitterHomeActivity.class);
					startActivity(i);
				}

			}
		});
		return root;
	}

}