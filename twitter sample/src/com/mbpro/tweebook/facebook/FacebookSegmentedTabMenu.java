package com.mbpro.tweebook.facebook;

import android.app.Activity;
import android.content.Intent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.ui.SegmentedRadioGroup;

public class FacebookSegmentedTabMenu implements OnCheckedChangeListener {
	SegmentedRadioGroup segmentTextFacebook;
	Toast mToast;
	public Activity context;

	public FacebookSegmentedTabMenu(Activity act) {
		this.context = act;
		segmentTextFacebook = (SegmentedRadioGroup) context
				.findViewById(R.id.segment_facebooktext);
		segmentTextFacebook.setOnCheckedChangeListener(this);
		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}

	public void setSeletected(int checkedId) {
		if (checkedId == 1) {
			RadioButton rdbtn1 = (RadioButton) context
					.findViewById(R.id.button_fone);
			rdbtn1.setPressed(true);
		} else if (checkedId == 2) {
			RadioButton rdbtn2 = (RadioButton) context
					.findViewById(R.id.button_ftwo);
			rdbtn2.setPressed(true);
		} else if (checkedId == 3) {
			RadioButton rdbtn3 = (RadioButton) context
					.findViewById(R.id.button_fthree);
			rdbtn3.setPressed(true);
		} else if (checkedId == 4) {
			RadioButton rdbtn4 = (RadioButton) context
					.findViewById(R.id.button_ffour);
			rdbtn4.setPressed(true);
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == segmentTextFacebook) {
			if (checkedId == R.id.button_fone) {
				Intent i = new Intent(context, FacebookMainActivity.class);
				context.startActivity(i);
			} else if (checkedId == R.id.button_ftwo) {
				Intent i = new Intent(context, FacebookMessages.class);
				context.startActivity(i);
			} else if (checkedId == R.id.button_fthree) {
				Intent i = new Intent(context, FacebookProfile.class);
				context.startActivity(i);

			} else if (checkedId == R.id.button_ffour) {
				Intent i = new Intent(context, FacebookPostStatus.class);
				context.startActivity(i);

			}
		}
	}

}
