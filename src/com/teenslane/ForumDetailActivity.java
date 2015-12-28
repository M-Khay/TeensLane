package com.teenslane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.teenslane.ForumActivity.postMessageSync;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

public class ForumDetailActivity extends SexActivity
{
	LayoutInflater inflater = null;
	RelativeLayout parent_layout = null;
	ScrollView scroll_view = null;
	LinearLayout linear_layout = null;
	JSONArray olderMessages = new JSONArray();
	int maxMessagesPerLoad = 10;
	RelativeLayout userprofile_message_content = null;
	int costForResponse = 0; // this will be updated asap after thread will be loaded
	
	Bitmap otherBitmap = null;
	Integer otherDrawable = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		olderMessages = new JSONArray();
		
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_forum_detail, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) parent_layout.findViewById(R.id.linear_layout); 
		userprofile_message_content = (RelativeLayout) parent_layout.findViewById(R.id.userprofile_message_content);
		
		setContentView(parent_layout);
		SexActivity.executeTask(new getUserData(), "load user data");
	}
	
	public void proceedThreadDetail()
	{
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_forum_detail, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) scroll_view.findViewById(R.id.linear_layout);
		userprofile_message_content = (RelativeLayout) parent_layout.findViewById(R.id.userprofile_message_content);
		
		setContentView(parent_layout);
		updateNewMessages(userprofile_message_content, costForResponse);
		
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.isEmpty() == false && extras.containsKey("forum"))
		{
			SexActivity.executeTask(new loadOldMessages(extras.getString("forum"), 1));
			setupButtons(extras.getString("forum"));
		}
		else
			setupButtons("");
		
		userprofile_message_content.setVisibility(RelativeLayout.VISIBLE);
	}
	
	@Override
    protected void onResume()
    {
		super.onResume();
		updateNewMessages(userprofile_message_content, costForResponse);
    }
	
	class loadOldMessages extends AsyncTask<String, Integer, String>
	{
		String forum = "";
		int offset = 1;
		
		public loadOldMessages(String pForum, int pOffset)
		{
			forum = pForum;
			offset = pOffset;
		}
		
		protected void onPostExecute(String result)
	    {
			hideLoadingDialog();
			updateNewMessages(userprofile_message_content, costForResponse);
			showOlderMessages(forum, offset);
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpLoadOlderMessages(forum, offset);
			return "ok";
		}

		@Override
		protected void onPreExecute() {
			showLoadingDialog();
		}
	}
		
	public int setNoPhotoImage(ImageView imageView, int gender)
	{
		int drawable = 0;
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
		
		if(drawable != 0)
			imageView.setImageResource(drawable);
		
		return drawable;
	}
	
	public String getNow()
	{
		ArrayList<String> months = new ArrayList<String>();
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");  
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		
		return String.valueOf(hours) + ":" + ((String.valueOf(minutes).length()==1) ? "0" : "") + String.valueOf(minutes) + " " + months.get(month) + " " + String.valueOf(day);
		
	}
	
	public void showMessage(String forum, final String user, String gender, String nickname, String image, String message, int offset, String datetime)
	{
		View line = (View) inflater.inflate(R.layout.include_message_thread_item, null);
		
		ImageView user_image_src = (ImageView) line.findViewById(R.id.user_image_src);
		ProgressBar user_loading = (ProgressBar) line.findViewById(R.id.gallery_loading);
		
		if(image != null && image.equalsIgnoreCase("") == false)
		{
			user_loading.setVisibility(ProgressBar.VISIBLE);
			
			//set user image

			String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_60 + "/" + image;  
			SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1), "load user image");
		}
		else
		{
			user_loading.setVisibility(ProgressBar.GONE);
			otherDrawable = setNoPhotoImage(user_image_src, Integer.valueOf(gender));
		}
			
		user_image_src.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if(Integer.valueOf(user) != SexActivity.me.id)
				{
					Bundle extras = new Bundle();
					extras.putString("user_id", user);
					Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
					userDetail.putExtras(extras);
					startActivity(userDetail);
				}
			}
		});
		
		TextView messageBody = (TextView) line.findViewById(R.id.message_body);
		messageBody.setText(message);
		
		TextView messageUser = (TextView) line.findViewById(R.id.message_thread_user);
		messageUser.setText(nickname);
		
		TextView messageDate = (TextView) line.findViewById(R.id.message_thread_date);
		
		if(datetime.equals("now") == false)
			messageDate.setText(datetime);
		else
			messageDate.setText(getNow());
		
		
		try
		{
			if(offset <= 1)
			{
				linear_layout.addView(line);
				scrollToBottom();
			}
			else
				linear_layout.addView(line, 0);
			
			
		}
		catch(Exception e)
		{
		}
	}
	
	public void scrollToBottom()
	{
		scroll_view.post(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		    	scroll_view.fullScroll(View.FOCUS_DOWN);
		    } 
		});
	}
	
	public void showOlderMessages(final String forum, final int offset)
	{
		int startIndex = Math.min(olderMessages.length(), maxMessagesPerLoad);
		if(offset <= 1)
		{
			for(int i=startIndex; i>=0; --i)
			{
				JSONObject temp = null;
				try 
				{
					temp = olderMessages.getJSONObject(i);
					showMessage(temp.getString("forum"), temp.getString("user_from"), temp.getString("gender"), temp.getString("nickname"), temp.getString("image"), temp.getString("message"), offset, temp.getString("date"));
				}
				catch (JSONException e) 
				{
				}
			}
		}
		else
		{
			for(int i=0; i<startIndex; i++)
			{
				JSONObject temp = null;
				try 
				{
					temp = olderMessages.getJSONObject(i);
					showMessage(temp.getString("forum"), temp.getString("user_from"), temp.getString("gender"), temp.getString("nickname"), temp.getString("image"), temp.getString("message"), offset, temp.getString("date"));
				}
				catch (JSONException e) 
				{
				}
			}
		}
		
		if(olderMessages.length() > maxMessagesPerLoad)
		{
			View showMore = (View) inflater.inflate(R.layout.include_users_showmore, null);
			final Button showMoreBtn = (Button) showMore.findViewById(R.id.showmore_btn);
			showMoreBtn.setText(getString(R.string.showolder_messages));
			showMoreBtn.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showMoreBtn.setVisibility(Button.GONE);
					SexActivity.executeTask(new loadOldMessages(forum, offset+1));
				}
			});
			
			linear_layout.addView(showMore, 0);
		}
	}
	
	public void httpLoadOlderMessages(String pForum, int pOffset)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "forum_thread"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("forum", String.valueOf(pForum)));
	        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(pOffset)));
	        nameValuePairs.add(new BasicNameValuePair("timezone", SexActivity.getTz())); 
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				
				if(json_data.get("messages") != null && json_data.getJSONArray("messages").length()>0) 
				{
					olderMessages = json_data.getJSONArray("messages");
				}

				if (json_data.opt("cost") != null) {
					costForResponse = json_data.getInt("cost");
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
		
	public void setupButtons(final String forum)
	{
		TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.forumdetail_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ForumDetailActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_menu);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				ForumDetailActivity.this.finish();
				try
				{
					SexActivity.menuReturn = true;
				}
				catch(Exception e)
				{
				}
			}
		});
		
		final EditText newPostText = (EditText) userprofile_message_content.findViewById(R.id.sendmessage_text);
		Button newPostBtn = (Button) userprofile_message_content.findViewById(R.id.sendmessage_button);
		
		newPostBtn.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try
				{
					InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}
				catch(Exception e)
				{
				}
				
				if(newPostText.getText().toString().equalsIgnoreCase("") == false)
				{
				    SexActivity.executeTask(new postMessageSync(Integer.valueOf(forum), newPostText.getText().toString()), "post save");
				}
			}
		});
		
		newPostText.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if(actionId == EditorInfo.IME_ACTION_DONE) 
				{
					if(newPostText.getText().toString().equalsIgnoreCase("") == false)
					{
					    SexActivity.executeTask(new postMessageSync(Integer.valueOf(forum), newPostText.getText().toString()), "post save");
						newPostText.setText("");
					}
				}
				return false;
			}
		});
	}
	
	class postMessageSync extends AsyncTask<String, Integer, Boolean>
	{
		int toThread = 0;
		String message = "";
		public String lastError = "";
		
		public postMessageSync(int pToThread, String pMessage)
		{
			super();
			toThread = pToThread;
			message = pMessage;
		}
		
		protected void onPostExecute(Boolean result)
	    {
			if (!isFinishing())
				hideLoadingDialog();
			
			if(result == true)
			{
				proceedThreadDetail();
				((EditText)userprofile_message_content.findViewById(R.id.sendmessage_text)).setText("");
			}
			else 
			{
				Toast.makeText(getApplicationContext(), lastError, Toast.LENGTH_SHORT).show();
			}
	    }
		
		@Override
		protected Boolean doInBackground(String... params) 
		{
			return saveMessage(this, toThread, message);
		}

		@Override
		protected void onPreExecute() {
			if (!isFinishing())
				showLoadingDialog();
		}
	}
	
	public Boolean saveMessage(postMessageSync instance, int pToThread, String pMessage)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "save_forum_msg"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("forum", String.valueOf(pToThread)));
	        nameValuePairs.add(new BasicNameValuePair("msg", pMessage));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			instance.lastError = "";
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);

				if (json_data.opt("credits") != null) 
				{
					me.credits = json_data.getInt("credits");
				}
				
				if (json_data.opt("error") != null) 
				{
					instance.lastError = json_data.getString("error");
					return false;
				}
				
				if(json_data.getInt("success") == 1) 
				{
					return true;
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
		return false;
	}
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		@Override
		protected void onPostExecute(String result)
	    {			
			proceedThreadDetail();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}

		@Override
		protected void onPreExecute() 
		{
		}
	}
}
