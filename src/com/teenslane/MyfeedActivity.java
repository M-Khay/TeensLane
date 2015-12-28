package com.teenslane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract.Colors;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MyfeedActivity extends SexActivity
{
	LayoutInflater inflater = null;
	RelativeLayout parent_layout = null;
	ScrollView scroll_view = null;
	LinearLayout linear_layout = null;
	int maxFeedPerLoad = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		LocalService.chatting = 0;
		super.onCreate(savedInstanceState);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_myfeed, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) parent_layout.findViewById(R.id.linear_layout); 
		
		setContentView(parent_layout);
		
		SexActivity.executeTask(new getUserData(), "load user data");
	}
	
	@Override
    protected void onResume()
    {
		LocalService.chatting = 0;
		super.onResume();
    }
	
	public void proceedFeed()
	{
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_myfeed, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) scroll_view.findViewById(R.id.linear_layout);
		
		setContentView(parent_layout);
		
		setupButtons();
		loadFeed(1);
	}
	
	public void loadFeed(int offset)
	{
		showLoadingDialog();
		SexActivity.executeTask(new getFeed(offset), "load threads");
	}
	
	class getFeed extends AsyncTask<String, Integer, JSONArray>
	{
		int offset = 0;
		
		public getFeed(int pOffset)
		{
			super();
			offset = pOffset;
		}
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			if(result != null && result.length()>0)
				showFeed(result, offset);
			
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return httpGetFeed(offset);
		}
		
		@Override
		protected void onPreExecute() {
			showLoadingDialog();
		}
	}
	
	public void showFeed(JSONArray feed, final int offset)
	{
		for(int i=0; i<Math.min(feed.length(), maxFeedPerLoad); i++)
		{
			JSONObject temp = null;
			try 
			{
				temp = feed.getJSONObject(i);
				showFeed(temp);
			}
			catch (JSONException e) 
			{
			}
		}
		
		if(feed.length() > maxFeedPerLoad)
		{
			View showMore = (View) inflater.inflate(R.layout.include_users_showmore, null);
			final Button showMoreBtn = (Button) showMore.findViewById(R.id.showmore_btn);
			final View showMoreHolder = (View) showMore;
			
			showMoreBtn.setText(getString(R.string.showmore));
			showMoreBtn.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showMoreBtn.setVisibility(Button.GONE);
					// also hide holder so the margin will not stay
					showMoreHolder.setVisibility(Button.GONE);
					 
					SexActivity.executeTask(new getFeed(offset+1), "load threads");
				}
			});
			
			linear_layout.addView(showMore);
		}
	}
	
	public int setNoPhotoImage(ImageView imageView, int gender)
	{
		Integer drawable = null;
		if(gender == SexActivity.attributesGenderMan)
		{
			drawable = R.drawable.myprofile_nophoto_man;
		}
		else if(gender == SexActivity.attributesGenderWoman)
		{
			drawable = R.drawable.myprofile_nophoto_woman;
		}
		else if(gender == SexActivity.attributesGenderPairStraight)
		{
			drawable = R.drawable.myprofile_nophoto_pairmanwomain;
		}
		else if(gender == SexActivity.attributesGenderPairLesbian)
		{
			drawable = R.drawable.myprofile_nophoto_pairwoman;
		}
		else if(gender == SexActivity.attributesGenderPairGay)
		{
			drawable = R.drawable.myprofile_nophoto_pairman;
		}
		
		if(drawable != null)
			imageView.setImageResource(drawable);
		
		return drawable;
	}
	
	//private boolean isFeedEntryEven = false;
	
	public void showFeed(final JSONObject feed)
	{
		View line = null;
		line = (View) inflater.inflate(R.layout.include_feed_entry, null);
		
		/*
		if (isFeedEntryEven) {
			RelativeLayout layout = (RelativeLayout) line.findViewById(R.id.holder);
			layout.setBackgroundResource(R.drawable.css_item_background_even);
			
			ImageView imageOverlay = (ImageView) line.findViewById(R.id.gallery_image);
			imageOverlay.setImageResource(R.drawable.myprofile_gallery_image_overlay_even);
		} */
		
		boolean userVisible = false;
		boolean regardToYou = false;
		String userNickname = "";
		int feedUserId = 0;
		
		try 
		{
			int connectionType = feed.getInt("connection_type");
			
			//user is visible 
			//if status = status change 
			//if status is about image changes or system or advertisement
			//if I am premium
			//if I am user <-- this shouldn't happen
			if ((connectionType != SexActivity.feed_pets_added && connectionType != SexActivity.feed_pets_removed && connectionType != SexActivity.feed_viewed) || SexActivity.me.premium > 0 || SexActivity.me.id == feed.getInt("uid"))
				userVisible = true;
			
			if (connectionType == SexActivity.feed_pets_added && SexActivity.me.isFeatureActive(SexActivity.feature_pets_added))
				userVisible = true;
			
			if (connectionType == SexActivity.feed_pets_removed && SexActivity.me.isFeatureActive(SexActivity.feature_pets_removed))
				userVisible = true;
		
			if (connectionType == SexActivity.feed_viewed && SexActivity.me.isFeatureActive(SexActivity.feature_feed_view))
				userVisible = true;
			
			//feed is from other user to me
			//.equals(SexActivity.me.session) == true)
			if(feed.opt("r_id") != null && feed.getString("r_id").equals(String.valueOf(SexActivity.me.id)) == true)
				regardToYou = true;
			
			ImageView feed_image = (ImageView) line.findViewById(R.id.feed_image);
			ImageView user_image_src = (ImageView) line.findViewById(R.id.user_image_src);
			ProgressBar user_loading = (ProgressBar) line.findViewById(R.id.gallery_loading);
			TextView nickname = (TextView) line.findViewById(R.id.nickname);
			
			if(feed.getInt("user_pet") == 1 || regardToYou == true)
			{
				feedUserId = feed.getInt("u_id");
				if(userVisible == true && feed.get("u_image") != null && feed.getString("u_image").equals("") == false)
				{
					user_loading.setVisibility(ProgressBar.VISIBLE);
					
					//set user image
					String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_60 + "/" + feed.getString("u_image");
					SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1), "load user image");
				}
				else
				{
					user_loading.setVisibility(ProgressBar.GONE);
					setNoPhotoImage(user_image_src, feed.getInt("u_gender"));
				}
				
				userNickname = feed.getString("u_nickname");
				if(userVisible == true)
					nickname.setText(feed.getString("u_nickname"));
				else
					nickname.setText(getString(R.string.someone));
			}
			else
			{
				feedUserId = feed.getInt("r_id");
				if(userVisible == true && feed.get("r_image") != null && feed.getString("r_image").equals("") == false)
				{
					user_loading.setVisibility(ProgressBar.VISIBLE);
					
					//set user image
					String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_60 + "/" + feed.getString("r_image");
					SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1), "load user image");
				}
				else
				{
					user_loading.setVisibility(ProgressBar.GONE);
					setNoPhotoImage(user_image_src, feed.getInt("r_gender"));
				}
				
				userNickname = feed.getString("r_nickname");
				if(userVisible == true)
					nickname.setText(feed.getString("r_nickname"));
				else
					nickname.setText(getString(R.string.someone));
			}
			
			TextView date = (TextView) line.findViewById(R.id.date);
			if(feed.getString("date").equals("") == false)
				date.setText(feed.getString("date"));
			else
				date.setVisibility(TextView.INVISIBLE);
			
			TextView messageBody = (TextView) line.findViewById(R.id.message_body);
			if(feed.getString("message").equals("") == false)
				messageBody.setText(feed.getString("message"));
			else
				messageBody.setText(parseFeedAction(feed, userVisible, regardToYou, feed_image, feedUserId, userNickname));
		
			
			View.OnClickListener loadDetail = new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					
					try 
					{
						Bundle extras = new Bundle();
						extras.putString("user_id", feed.getString("u_id"));
						Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
						userDetail.putExtras(extras);
						startActivity(userDetail);
					}
					catch (JSONException e) 
					{
					}
				}
			};
			View.OnClickListener loadPremium = new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Bundle extras = new Bundle();
					extras.putInt("premium", 1);
					Intent myDetail = new Intent(getBaseContext(), MyprofileActivity.class);
					myDetail.putExtras(extras);
					startActivity(myDetail);
				}
			};
			
			if(userVisible == true)
			{
				user_image_src.setOnClickListener(loadDetail);
				nickname.setOnClickListener(loadDetail);
				messageBody.setOnClickListener(loadDetail);
			}
			else
			{
				user_image_src.setOnClickListener(loadPremium);
				nickname.setOnClickListener(loadPremium);
				messageBody.setOnClickListener(loadPremium);
			}

			// isFeedEntryEven = !isFeedEntryEven;			
			linear_layout.addView(line);
		}
		catch (JSONException e) 
		{
			Log.d("feed", e.getMessage(), e);
		}
	}
	
	public void showGiftDialog(final int giftId, final int feedUserId, boolean userVisible, String userNickname)
	{
		final Dialog giftDialog = new Dialog(this);
		giftDialog.setContentView(R.layout.dialog_gift_detail); 
		giftDialog.setTitle(R.string.gift_detail);

		ImageView gift_image = (ImageView) giftDialog.findViewById(R.id.gift_image);
		gift_image.setImageResource(giftId);
		
		
		String stringID = getResources().getResourceName(giftId);
		Integer giftID = getResources().getIdentifier(stringID.replace("com.teenslane:drawable/", ""), "string", getPackageName());
		
		TextView gift_name = (TextView) giftDialog.findViewById(R.id.gift_name);
		if(giftID != null && giftID != 0)
			gift_name.setText(getString(giftID));
		else
			gift_name.setVisibility(TextView.INVISIBLE);
		
		
		Button dialogConfirm = (Button) giftDialog.findViewById(R.id.gift_action);
		View.OnClickListener gotoGiftsAction = new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				Bundle extras = new Bundle();
				extras.putString("user_id", String.valueOf(feedUserId));
				extras.putInt("gifts", 1);
				Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
				userDetail.putExtras(extras);
				startActivity(userDetail);
				try
				{
					if(isFinishing() == false)
						giftDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		};
		
		if(userVisible == true)
		{
			dialogConfirm.setText(getString(R.string.gift_send_too_firstpart) + " " + userNickname + " " + getString(R.string.gift_send_too_sedondpard));
			dialogConfirm.setOnClickListener(gotoGiftsAction);
		}
		else
			dialogConfirm.setVisibility(Button.GONE);
		
		try
		{
			if(isFinishing() == false)
				giftDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public String parseFeedAction(final JSONObject feed, final boolean userVisible, boolean regardToYou, ImageView feed_image, final int feedUserId, final String userNickname) throws JSONException
	{
		/*
		public static int feed_status = 1;
		public static int feed_viewed = 2;
		public static int feed_pets_added = 3;
		public static int feed_pets_removed = 4;
		public static int feed_kiss = 5;
		public static int feed_slap = 6;
		public static int feed_badge = 7;
		public static int feed_gift = 8;
		public static int feed_rate_image_hot = 9;
		public static int feed_rate_image_wtf = 15;
		public static int feed_profile_image_added = 10;
		public static int feed_profile_image_changed = 11;
		public static int feed_gallery_image_added = 12;
		public static int feed_system_message = 13;
		public static int feed_advertisement_message = 14;
		*/
		
		String value = "";
		
		if(feed.get("connection_type") != null)
		{
			int connectionType = feed.getInt("connection_type");
			switch(connectionType)
			{
				case SexActivity.feed_viewed:
					value = getString(R.string.feed_viewed) + " " + getString((regardToYou==true) ? R.string.feedgrammar_your : R.string.someone) + " " +getString(R.string.feedgrammer_profile);
					break;
	
				case SexActivity.feed_pets_added:
					value = getString(R.string.feed_pets_added) + " " + getString((regardToYou==true) ? R.string.feedgrammar_you : R.string.someone) + " " + getString(R.string.feedgrammer_to_pets);
					break;
					
				case SexActivity.feed_pets_removed:
					value = getString(R.string.feed_pets_removed) + " " + getString((regardToYou==true) ? R.string.feedgrammar_you : R.string.someone) + " " + getString(R.string.feedgrammer_from_pets);
					break;
					
				case SexActivity.feed_kiss:
					value = getString(R.string.feed_kiss) + " " + getString((regardToYou==true) ? R.string.feedgrammar_you : R.string.someone);
					break;
					
				case SexActivity.feed_slap:
					value = getString(R.string.feed_slap) + " " + getString((regardToYou==true) ? R.string.feedgrammar_you : R.string.someone);
					break;
					
				case SexActivity.feed_gift:
					
					if(regardToYou==true)
					{
						value = getString(R.string.feedgrammar_send) + " " + getString(R.string.feedgrammar_you) + " " + getString(R.string.feed_gift);
					}
					else
					{
						if(feed.getInt("user_pet") == 1)
						{
							value = getString(R.string.feedgrammar_send) + " " + getString(R.string.feed_gift);
						}
						else
						{
							if(feed.getInt("u_id") == SexActivity.me.id)
								value = getString(R.string.feedgrammar_received) + " " + getString(R.string.feed_gift) + " " + getString(R.string.feedgrammar_from)+ " " + getString(R.string.feedgrammar_you);
							else
								value = getString(R.string.feedgrammar_received) + " " + getString(R.string.feed_gift);
						}
					}
					
					if(feed.opt("connection") != null)
					{
						int giftId = feed.getInt("connection");
						
				        int resourceGiftIdTemp = SexActivity.gifts_resources[0];
				        for (int i = 0; i < SexActivity.gifts_id.length; i++) {
				        	if (SexActivity.gifts_id[i] == giftId) {
				        		resourceGiftIdTemp = SexActivity.gifts_resources[i];
				        		break;
				        	}
				        }
				        
				        final int resourceGiftId = resourceGiftIdTemp;
						
						feed_image.setImageResource(resourceGiftId);
						feed_image.setVisibility(ImageView.VISIBLE);
						
						feed_image.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								try 
								{
									showGiftDialog(resourceGiftId, feedUserId, userVisible, userNickname);
								}
								catch (Exception e) 
								{
								}
							}
						});
					}
					
					break;
					
				case SexActivity.feed_profile_image_changed:
					value = getString(R.string.feed_profile_image_changed);
					break;
					
				case SexActivity.feed_gallery_image_added:
					value = getString(R.string.feed_gallery_image_added);
					
					if(feed.opt("connection") != null && feed.opt("image") != null && feed.opt("image_visible") != null && feed.getString("image").equals("") == false && feed.getInt("image_visible") == 1)
					{
						//set user image
						String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_240 + "/" + feed.getString("image");
						SexActivity.executeTask(new setImageFromUrl(userImageUrl, feed_image, null, 1), "load user image");

						// set border around image
						feed_image.setPadding(2,2,2,2);
						feed_image.setBackgroundColor(Color.WHITE);
						
						feed_image.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								Bundle extras = new Bundle();
								extras.putString("user_id", String.valueOf(feedUserId));
								extras.putInt("gallery_force", 1);
								Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
								userDetail.putExtras(extras);
								startActivity(userDetail);
							}
						});
					}
					
					break;
					
				case SexActivity.feed_rate_image_hot:
					
					if(feed.opt("connection") != null && feed.getInt("connection") == 0)
						value = getString(R.string.feed_rate_profile) + " " + getString(R.string.feed_rate_hot);
					else
						value = getString(R.string.feed_rate) + " " + getString(R.string.feed_rate_hot);
					
					if(feed.opt("connection") != null && feed.opt("image") != null && feed.opt("image_visible") != null && feed.getString("image").equals("") == false && feed.getInt("image_visible") == 1)
					{
						//set user image
						String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_240 + "/" + feed.getString("image");
						SexActivity.executeTask(new setImageFromUrl(userImageUrl, feed_image, null, 1), "load user image");
						
						// set border around image
						feed_image.setPadding(2,2,2,2);
						feed_image.setBackgroundColor(Color.WHITE);
						
						feed_image.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								try 
								{
									if(feed.opt("image_owner") != null && feed.getInt("image_owner") == SexActivity.me.id)
									{
										Intent myDetail = new Intent(getBaseContext(), MyprofileActivity.class);
										startActivity(myDetail);
									}
									else
									{
										if(userVisible == true)
										{
											Bundle extras = new Bundle();
											extras.putString("user_id", String.valueOf(feedUserId));
											extras.putInt("gallery_force", 1);
											Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
											userDetail.putExtras(extras);
											startActivity(userDetail);
										}
									}
								}
								catch (JSONException e) 
								{
								}
							}
						});
					}
					
					break;
					
				case SexActivity.feed_rate_image_wtf:
					
					if(feed.opt("connection") != null && feed.getInt("connection") == 0)
						value = getString(R.string.feed_rate_profile) + " " + getString(R.string.feed_rate_wtf);
					else
						value = getString(R.string.feed_rate) + " " + getString(R.string.feed_rate_wtf);
					
					if(feed.opt("connection") != null && feed.opt("image") != null && feed.opt("image_visible") != null && feed.getString("image").equals("") == false && feed.getInt("image_visible") == 1)
					{
						//set user image
						String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_240 + "/" + feed.getString("image");
						SexActivity.executeTask(new setImageFromUrl(userImageUrl, feed_image, null, 1), "load user image");
						
						// set border around image
						feed_image.setPadding(2,2,2,2);
						feed_image.setBackgroundColor(Color.WHITE);
						
						feed_image.setOnClickListener(new View.OnClickListener() 
						{
							@Override
							public void onClick(View v) 
							{
								if(feedUserId == SexActivity.me.id)
								{
									Intent myDetail = new Intent(getBaseContext(), MyprofileActivity.class);
									startActivity(myDetail);
								}
								else
								{
									if(userVisible == true)
									{
										Bundle extras = new Bundle();
										extras.putString("user_id", String.valueOf(feedUserId));
										extras.putInt("gallery_force", 1);
										Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
										userDetail.putExtras(extras);
										startActivity(userDetail);
									}
								}
							}
						});
					}
					
					break;
					
				default:
					break;
			}
		}
		
		return value;
	}
	
	public JSONArray httpGetFeed(int offset)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_myfeed_entries"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
	        nameValuePairs.add(new BasicNameValuePair("timezone", SexActivity.getTz()));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.opt("entries") != null)
					return json_data.optJSONArray("entries");
			}
		} 
     	catch (UnsupportedEncodingException e) 
     	{
		}
        catch (ClientProtocolException e) 
        {
        }
        catch (IOException e) 
        {
        } 
		catch(Exception e)
		{
  		}
		return null;
	}
	
	public void setupButtons()
	{	
		TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.myfeed_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				MyfeedActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_reload);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				refreshFeed();
			}
		});
	

		Button statusUpdate = (Button) findViewById(R.id.status_update);
		statusUpdate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				drawStatusDialog();
			}
		});	
	}
	
	private void refreshFeed() {
		if (null != linear_layout && linear_layout.getChildCount() > 0) 
		{                 
		    try 
		    {
		    	linear_layout.removeViews (0, linear_layout.getChildCount());
		    }
		    catch (Exception e) 
		    {
		    }
		}
		
		loadFeed(1);
	}
	
	private void drawStatusDialog() {
		final Dialog statusDialog = new Dialog(this);
		statusDialog.setContentView(R.layout.dialog_status_update);
		statusDialog.setTitle(getString(R.string.feed_update_status));
		
	  	Button bodyConfirm = (Button) statusDialog.findViewById(R.id.state_confirm);
	  	bodyConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				EditText text = (EditText)statusDialog.findViewById(R.id.status_text);
				String statusText = text.getText().toString();
				
				if (statusText.trim().length() == 0) {
					Toast.makeText(getApplicationContext(), getString(R.string.feed_update_status_empty), Toast.LENGTH_LONG).show();								
				}
				else {
					try
					{
						if (isFinishing() == false)
							statusDialog.cancel();
					}
					catch(Exception e)
					{
					}

					postStatus status = new postStatus();
					status.statusText = statusText;
					status.execute();		
				}
			}
	  	});

		try
		{
			if (isFinishing() == false)
				statusDialog.show();
		}
		catch(Exception e)
		{
		}
	}

	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedFeed();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}

	/**
	 * Background task that actually posts status and refresh current feed
	 */
	class postStatus extends AsyncTask<String, Integer, String>
	{
		public String statusText;
		
		protected void onPostExecute(String result)
	    {
			refreshFeed();	

			// this crashes app no idea why
			// hideLoadingDialog();			
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpUpdateStatus(statusText);
			
			// this crashes app no idea why
			// showLoadingDialog();			
			
			return "ok";
		}
	}

	public void httpUpdateStatus(String status)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "status_update"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("status", status));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1) 
				{
					Log.d("Status Update", "ok");
				}
			}
		} 
     	catch (UnsupportedEncodingException e) 
     	{
		}
        catch (ClientProtocolException e) 
        {
        }
        catch (IOException e) 
        {
        } 
		catch(Exception e)
		{
  		}
	}
}
