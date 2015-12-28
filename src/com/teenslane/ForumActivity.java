package com.teenslane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

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
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class ForumActivity extends SexActivity 
{
	LayoutInflater inflater = null;
	RelativeLayout parent_layout = null;
	RelativeLayout newThread = null;
	ScrollView scroll_view = null;
	LinearLayout linear_layout = null;
	int maxMessagesPerLoad = 5;
	int costNewThread = 0;
	boolean newThreadVisible = false; 
			
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_forum, null);
		newThread = (RelativeLayout) parent_layout.findViewById(R.id.new_thread);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) parent_layout.findViewById(R.id.linear_layout); 
		
		setContentView(parent_layout); 
		
		SexActivity.executeTask(new getUserData(), "load user data");
	}
	
	@Override
    protected void onResume()
    {
		super.onResume();
		updateNewMessages(newThread, costNewThread);
		
    	try
    	{	
	    	if(SexActivity.menuReturn == true)
			{
	    		SexActivity.menuReturn = false;
				ForumActivity.this.finish();
			}
    	}
    	catch(Exception e)
    	{
    	}
    }
	
	public void proceedThreads()
	{
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_forum, null);
		newThread = (RelativeLayout) parent_layout.findViewById(R.id.new_thread);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) scroll_view.findViewById(R.id.linear_layout);
		
		setContentView(parent_layout);
		updateNewMessages(newThread, costNewThread);
		
		setupButtons();
		loadThreads(1);
		
		//newThread.setVisibility(RelativeLayout.VISIBLE);
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
			updateNewMessages(newThread, costNewThread);
			
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
		
		if (drawable != null)
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
			messageDate.setText(thread.getString("date") + ", " + getString(R.string.thread_responses) + " " + thread.getString("amount"));
			
			View.OnClickListener loadDetail = new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					
					Bundle extras = new Bundle();
					try 
					{
						extras.putString("forum", thread.getString("forum"));
						Intent forumDetail = new Intent(getBaseContext(), ForumDetailActivity.class);
						forumDetail.putExtras(extras);
						startActivity(forumDetail);
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
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_forum_threads"));
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

				if (json_data.opt("cost") != null) 
					costNewThread = json_data.getInt("cost");
				
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
		title.setText(getString(R.string.forum_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				ForumActivity.this.finish();
			}
		});
				
		// 
		final EditText newThreadTopic = (EditText) newThread.findViewById(R.id.sendmessage_text);
		newThreadTopic.setHint(getString(R.string.new_thread_topic));
		
		Button newThreadBtn = (Button) newThread.findViewById(R.id.sendmessage_button);
		newThreadBtn.setText(getString(R.string.new_thread_button));
		
		newThreadBtn.setOnClickListener(new View.OnClickListener() 
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
				
				if(newThreadTopic.getText().toString().equalsIgnoreCase("") == false)
				{
				    SexActivity.executeTask(new postMessageSync(0, newThreadTopic.getText().toString()), "post save");
				}
			}
		});
		
		newThreadTopic.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if(actionId == EditorInfo.IME_ACTION_DONE) 
				{
					if(newThreadTopic.getText().toString().equalsIgnoreCase("") == false)
					{
					    SexActivity.executeTask(new postMessageSync(0, newThreadTopic.getText().toString()), "post save");
					    newThreadTopic.setText("");
					}
				}
				return false;
			}
		});
		
		
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_forum);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if(newThreadVisible == false)
				{
					newThreadVisible = true;
					newThread.setVisibility(RelativeLayout.VISIBLE);
				}
				else
				{
					newThreadVisible = false;
					newThread.setVisibility(RelativeLayout.GONE);
				}
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
				newThreadVisible = false;
				newThread.setVisibility(RelativeLayout.GONE);
				
				// empty field
				((EditText) newThread.findViewById(R.id.sendmessage_text)).setText("");
				
				proceedThreads();
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
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		@Override
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

		@Override
		protected void onPreExecute() 
		{
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
				
				if (json_data.opt("credits") != null) {
					me.credits = json_data.getInt("credits");
				}

				if (json_data.opt("error") != null) {
					instance.lastError = json_data.getString("error");
					return false;
				}
				
				if(json_data.getInt("success") == 1) {
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

}
