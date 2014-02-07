package me.cexi.tweetmaster;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*Detail View ACtivity Shows Individual Tweets
 * 
 * */
public class BlisdTwitterDetailViewController extends Activity {
	private BlisdDrawableManager drawableManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailtweetview);
		/** Getting Extras from Twitter View Controller **/
		try{
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				drawableManager = new BlisdDrawableManager();
				final String fromUser = extras.getString("fromUser");
				final String fromUserName = extras.getString("fromUserName");
				final String toUserName = extras.getString("toUserName");
				final String toUser = extras.getString("toUser");
				String text = extras.getString("text");
				final String textLink = extras.getString("textLink");
				String created = extras.getString("created");
	
				/**
				 * Here we Fetching bigger image from twitter profile image url. We
				 * have only normal image url and we are creating bigger image url
				 * from it.
				 **/
				String profileImageUrl = extras.getString("profileImageUrl");
				String image_url = profileImageUrl;
				try{
					String[] path = profileImageUrl.split("/");
					String normal_image = path[path.length - 1];
					String bigger_image = normal_image.replace("normal", "bigger");
					image_url = profileImageUrl;
					image_url = image_url.replace(normal_image, bigger_image);
				}catch (Exception e) {
					// TODO: handle exception
				}
				/******************************************************************/
				final ImageView profileImage = (ImageView) findViewById(R.id.thumbImageDetail);
	
				/** Setting Onclick Listner for Profile Image **/
				profileImage.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(
								BlisdTwitterDetailViewController.this,
								BlisdWebViewController.class);
						intent
								.putExtra("url", "https://twitter.com/#!/"
										+ fromUser);
						startActivity(intent);
					}
				});
				TextView txtUserName = (TextView) findViewById(R.id.txtUserName);
				TextView txtName = (TextView) findViewById(R.id.txtName);
				TextView txtDate = (TextView) findViewById(R.id.txtDate);
				txtUserName.setText(fromUser);
				txtName.setText(fromUserName);
				txtDate.setText(created);
	
				/** Fetching the Bigger image as a drawable **/
				try{
					BlisdDrawableManager.DrawableFetchedHandler thumbFetchedHandler = new BlisdDrawableManager.DrawableFetchedHandler() {
		
						public void setDrawable(Drawable drawable) {
							profileImage.setImageDrawable(drawable);
						}
					};
					drawableManager.fetchDrawableAsync(image_url, thumbFetchedHandler);
				} catch (Exception e) {
					// TODO: handle exception
				}
				/**********************************************************************/
				TextView txtTweetText = (TextView) findViewById(R.id.txtTweetText);
				Button txtUserLink = (Button) findViewById(R.id.txtUserLink);
	
				txtTweetText.setText(text);
				txtUserLink.setText(fromUserName);
	
				/** Setting Onclick Listner for User Name **/
				txtUserLink.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(
								BlisdTwitterDetailViewController.this,
								BlisdWebViewController.class);
						intent
								.putExtra("url", "https://twitter.com/#!/"
										+ fromUser);
						startActivity(intent);
					}
				});
	
				if (!textLink.equals("novalue") && !textLink.equals("null")) {
	
					Button txtExtraLink = (Button) findViewById(R.id.txtExtraLink);
					txtExtraLink.setText(textLink);
					/** Setting Onclick Listner for Link **/
					txtExtraLink.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Intent intent = new Intent(
									BlisdTwitterDetailViewController.this,
									BlisdWebViewController.class);
							intent.putExtra("url", textLink);
							startActivity(intent);
						}
					});
				} else {
					/**Hiding Layout to avoid blank space if no extra links present **/
					LinearLayout linear_layout_link = (LinearLayout) findViewById(R.id.linear_layout_link);
					linear_layout_link.setVisibility(View.GONE);
				}
				if (!toUser.equals("novalue") && !toUser.equals("null")) {
	
					Button txtFromAndToUser = (Button) findViewById(R.id.txtFromAndToUser);
					txtFromAndToUser.setText(fromUserName + "+" + toUserName);
					/** Setting Onclick Listner for From and To User **/
					txtFromAndToUser.setOnClickListener(new View.OnClickListener() {
						public void onClick(View v) {
							Intent intent = new Intent(
									BlisdTwitterDetailViewController.this,
									BlisdWebViewController.class);
							intent.putExtra("url", "https://twitter.com/#!/search/"
									+ fromUser + "+" + toUser);
							startActivity(intent);
						}
					});
				} else {
					/**Hiding Layout to avoid blank space if null for both from and to user **/
					LinearLayout linear_layout_fromtouser = (LinearLayout) findViewById(R.id.linear_layout_fromtouser);
					linear_layout_fromtouser.setVisibility(View.GONE);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
