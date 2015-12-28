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

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MessagesActivity extends SexActivity
{
	LayoutInflater inflater = null;
	RelativeLayout parent_layout = null;
	ScrollView scroll_view = null;
	LinearLayout linear_layout = null;
	int maxMessagesPerLoad = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		LocalService.chatting = 0; 
		super.onCreate(savedInstanceState);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_messages, null);
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
	
	public void proceedThreads()
	{
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_users, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) scroll_view.findViewById(R.id.linear_layout);
		
		setContentView(parent_layout);
		
		setupButtons();
		loadThreads(1);
	}
	
	public void loadThreads(int offset)
	{
		showLoadingDialog();
		SexActivity.executeTask(new getThreads(offset), "load threads");
	}
	
	class getThreads extends AsyncTask<String, Integer, JSONArray>
	{
		int offset = 0;
		
		public getThreads(int pOffset)
		{
			super();
			offset = pOffset;
		}
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			if(result != null && result.length()>0)
				showThreads(result, offset);
			
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return httpGetThreads(offset);
		}
		
		@Override
		protected void onPreExecute() {
			showLoadingDialog();
		}
	}
	
	public void showThreads(JSONArray threads, final int offset) 
	{
		for(int i=0; i<Math.min(threads.length(), maxMessagesPerLoad); i++)
		{
			JSONObject temp = null;
			try 
			{
				temp = threads.getJSONObject(i); 
				showThread(temp);
			}
			catch (JSONException e) 
			{
			}
		}
		
		if(threads.length() > maxMessagesPerLoad)
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
					
					SexActivity.executeTask(new getThreads(offset+1), "load threads");
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

	private void setTouchable(View line, int layoutId, int imageViewId, 
			final int backgroundId, final int imageBackgroundId,
			final int selectedBackgroundId, final int selectedImageBackgroundId) 
	{
		final RelativeLayout layout = (RelativeLayout) line.findViewById(layoutId);
		layout.setBackgroundResource(backgroundId);

		final ImageView imageOverlay = (ImageView) line.findViewById(imageViewId);
		imageOverlay.setDuplicateParentStateEnabled(true);
		imageOverlay.setImageResource(imageBackgroundId);
		
		/*
		View.OnTouchListener stateChange = new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int iAction= event.getAction();
				
//				Log.e("com.teenslane.action", "action" + iAction);
		        if (iAction == MotionEvent.ACTION_UP) {
					layout.setBackgroundResource(backgroundId);
		        	imageOverlay.setImageResource(imageBackgroundId);
		        }
		        else if (iAction == MotionEvent.ACTION_DOWN) {
					layout.setBackgroundResource(selectedBackgroundId);
		        	imageOverlay.setImageResource(selectedImageBackgroundId);
		        }
		        
		        return false;
			}
		};
		
		layout.setOnTouchListener(stateChange);
		*/
	}
	
	private boolean isEntryEven = false;
	
	public void showThread(final JSONObject thread)
	{
		View line = null;
		line = (View) inflater.inflate(R.layout.include_message_thread, null);
		
		ImageView user_image_src = (ImageView) line.findViewById(R.id.user_image_src);
		ProgressBar user_loading = (ProgressBar) line.findViewById(R.id.gallery_loading);

		if (android.os.Build.VERSION.SDK_INT < 14) {
			/* no effect on touch */
			if (isEntryEven) {
				setTouchable(line, R.id.message_user, R.id.gallery_image, 
						R.drawable.item_background_even, R.drawable.myprofile_gallery_image_overlay_even,
						R.drawable.item_background_selected, R.drawable.myprofile_gallery_image_overlay_selected);
			}
			else {
				setTouchable(line, R.id.message_user, R.id.gallery_image, 
						R.drawable.item_background, R.drawable.myprofile_gallery_image_overlay,
						R.drawable.item_background_selected, R.drawable.myprofile_gallery_image_overlay_selected);
			}
		}
		else {
			/* nicer! */
			if (isEntryEven) {
				setTouchable(line, R.id.message_user, R.id.gallery_image, 
						R.drawable.css_item_background_even, R.drawable.css_item_background_gallery_image_overlay_even,
						R.drawable.css_item_background_even, R.drawable.css_item_background_gallery_image_overlay_even);
			}
			else {
				setTouchable(line, R.id.message_user, R.id.gallery_image, 
						R.drawable.css_item_background, R.drawable.css_item_background_gallery_image_overlay,
						R.drawable.css_item_background, R.drawable.css_item_background_gallery_image_overlay);
			}
		}
		
		try 
		{
			if(thread.get("image") != null && thread.getString("image").equals("") == false)
			{
				user_loading.setVisibility(ProgressBar.VISIBLE);
				
				//set user image
				String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_148 + "/" + thread.getString("image");
				SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1), "load user image");
			}
			else
			{
				user_loading.setVisibility(ProgressBar.GONE);
				setNoPhotoImage(user_image_src, thread.getInt("gender"));
			}
			
			TextView nickname = (TextView) line.findViewById(R.id.nickname);
			nickname.setText(thread.getString("nickname"));
			
			TextView messageBody = (TextView) line.findViewById(R.id.message_body);
			messageBody.setText(thread.getString("message"));
		
			TextView messageDate = (TextView) line.findViewById(R.id.message_thread_date);
			messageDate.setText(thread.getString("date"));
			
			View.OnClickListener loadDetail = new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					Bundle extras = new Bundle();
					try 
					{
						extras.putString("user_id", thread.getString("user"));
						extras.putInt("force_messages", 1);
						Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
						userDetail.putExtras(extras);
						startActivity(userDetail);
					}
					catch (JSONException e) 
					{
					}
				}
			};
			
			//user_image_src.setOnClickListener(loadDetail);
			//nickname.setOnClickListener(loadDetail);
			//messageBody.setOnClickListener(loadDetail);

			RelativeLayout layout = (RelativeLayout) line.findViewById(R.id.message_user);
			layout.setOnClickListener(loadDetail);
			
			isEntryEven = !isEntryEven;	
			linear_layout.addView(line);
		}
		catch (JSONException e) 
		{
		}
	}
	
	public JSONArray httpGetThreads(int offset)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_messages_threads"));
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
				if(json_data.opt("threads") != null)
					return json_data.optJSONArray("threads");
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
		title.setText(getString(R.string.messages_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				MessagesActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_menu);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				LocalService.chatting = 0;
				MessagesActivity.this.finish();
			}
		});
	}
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedThreads();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}
}
