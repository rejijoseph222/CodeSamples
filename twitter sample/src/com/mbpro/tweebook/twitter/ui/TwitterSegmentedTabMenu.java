package com.mbpro.tweebook.twitter.ui;

import android.app.Activity;
import android.content.Intent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.mbpro.tweebook.R;
import com.mbpro.tweebook.facebook.FacebookMainActivity;
import com.mbpro.tweebook.ui.SegmentedRadioGroup;

public class TwitterSegmentedTabMenu implements OnCheckedChangeListener {
	SegmentedRadioGroup segmentText;
	Toast mToast;
	public Activity context;

	public TwitterSegmentedTabMenu(Activity act) {
		this.context = act;
		segmentText = (SegmentedRadioGroup) context
				.findViewById(R.id.segment_text);
		segmentText.setOnCheckedChangeListener(this);

		mToast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	}

	public void setSeletected(int checkedId) {
		if (checkedId == 1) {
			RadioButton rdbtn1 = (RadioButton) context
					.findViewById(R.id.button_one);

			rdbtn1.setChecked(true);
		} else if (checkedId == 2) {
			RadioButton rdbtn2 = (RadioButton) context
					.findViewById(R.id.button_two);
			rdbtn2.setChecked(true);
		} else if (checkedId == 3) {
			RadioButton rdbtn3 = (RadioButton) context
					.findViewById(R.id.button_three);
			rdbtn3.setChecked(true);
		} else if (checkedId == 4) {
			RadioButton rdbtn4 = (RadioButton) context
					.findViewById(R.id.button_four);
			rdbtn4.setChecked(true);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		if (group == segmentText) {
			if (checkedId == R.id.button_one) {
				Intent i = new Intent(context, TwitterHomeActivity.class);
				context.startActivity(i);
			} else if (checkedId == R.id.button_two) {
				Intent i = new Intent(context, TwitterMentionsActivity.class);
				context.startActivity(i);
			} else if (checkedId == R.id.button_three) {
				Intent i = new Intent(context, TwitterMessagesActivity.class);
				context.startActivity(i);

			} else if (checkedId == R.id.button_four) {
				Intent i = new Intent(context, SendTweet.class);
				context.startActivity(i);

			}
		}
	}

}
