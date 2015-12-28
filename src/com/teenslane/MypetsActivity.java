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
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class MypetsActivity extends SexActivity 
{

	public LayoutInflater inflater = null;
	public RelativeLayout parent_layout = null;
	public ScrollView scroll_view = null; 
	public LinearLayout linear_layout = null;
	public int maxOffset = 25;
	 
	@Override 
	protected void onCreate(Bundle savedInstanceState)
	{
		LocalService.chatting = 0;
		super.onCreate(savedInstanceState);
		
		SexActivity.executeTask(new getUserData(), "load user data");
	}
	
	@Override
    protected void onResume()
    {
		LocalService.chatting = 0;
		super.onResume();
    }
	
	public void proceedUsers()
	{
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_users, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) scroll_view.findViewById(R.id.linear_layout);
		
		setContentView(parent_layout);
		
		setupButtons();
		loadUsers(1);
	}
	
	public void loadUsers(int offset)
	{
		showLoadingDialog();
		SexActivity.executeTask(new getUsers(offset), "load users");
	}
	
	public void setupButtons()
	{
		TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.mypets_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				MypetsActivity.this.finish();
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
				MypetsActivity.this.finish();
			}
		});
	}

	private void showUsers(JSONArray result, final int offset) throws JSONException 
	{
		if(result == null || result.length() == 0)
		{
			if(offset == 1)
				Toast.makeText(getApplicationContext(), getString(R.string.nousers), Toast.LENGTH_LONG).show();
			else
				Toast.makeText(getApplicationContext(), getString(R.string.nomoreusers), Toast.LENGTH_LONG).show();
		}
		else
		{
			RelativeLayout line = (RelativeLayout) inflater.inflate(R.layout.include_linear_line, null);
			int align = 0;
			
			int lastElement = Math.min(SexActivity.usersPerPage, result.length());
			for(int i=0; i<lastElement; i++)
			{
				final JSONObject user = result.getJSONObject(i);
				
				if(user != null)
				{
					View segment = (View) inflater.inflate(R.layout.include_user_thumbnail, null);
					boolean parse = false;
					
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					params.addRule(RelativeLayout.CENTER_VERTICAL);
					
					if(align == 0)
					{
						align = 1;
						params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					}
					else if(align == 1)
					{
						align = 2;
						params.addRule(RelativeLayout.CENTER_HORIZONTAL);
					} 
					else if(align == 2)
					{
						parse = true;
						align = 0;
						params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
					}
					
					ImageView image = (ImageView) segment.findViewById(R.id.user_image_src);
					image.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							try 
							{
								Bundle extras = new Bundle();
								extras.putString("user_id", user.getString("id"));
								Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
								userDetail.putExtras(extras);
								startActivity(userDetail);
							}
							catch (JSONException e) 
							{
							}
						}
					});
					
					ProgressBar galleryLoading = (ProgressBar) segment.findViewById(R.id.gallery_loading);
					TextView nickname = (TextView) segment.findViewById(R.id.user_nickname);
					
					try
					{
						if(user.opt("nickname") != null)
						{
							nickname.setText(user.getString("nickname").toString());
						}
						
						if(user.opt("image") != null && user.getString("image").equals("") == false)
						{
							String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_148 + "/" + user.getString("image");
							SexActivity.executeTask(new setImageFromUrl(userImageUrl, image, galleryLoading), "load user image");
						}
						else
						{
							galleryLoading.setVisibility(ProgressBar.GONE);
							if(user.opt("gender") != null && user.getInt("gender") != 0)
							{
								int gender = user.getInt("gender");
								if(gender == SexActivity.attributesGenderMan)
								{
									image.setImageResource(R.drawable.intro_nophoto_man);
								}
								else if(gender == SexActivity.attributesGenderWoman)
								{
									image.setImageResource(R.drawable.intro_nophoto_woman);
								}
								else if(gender == SexActivity.attributesGenderPairStraight)
								{
									image.setImageResource(R.drawable.intro_nophoto_pair_manwoman);
								}
								else if(gender == SexActivity.attributesGenderPairLesbian)
								{
									image.setImageResource(R.drawable.intro_nophoto_pair_woman);
								}
								else if(gender == SexActivity.attributesGenderPairGay)
								{
									image.setImageResource(R.drawable.intro_nophoto_pair_man);
								}
							}
						}
						
						line.addView(segment, params);
						
						if(i+1 == lastElement && parse == false)
							parse = true;
						
						if(parse == true)
						{
							parse = false;
							linear_layout.addView(line);
							line = null;
							line = (RelativeLayout) inflater.inflate(R.layout.include_linear_line, null);
						}
						
					}
					catch(JSONException e) 
					{
					}
				}
			}
			
			//show more button
			if(result.length() > SexActivity.usersPerPage)
			{
				line = null;
				line = (RelativeLayout) inflater.inflate(R.layout.include_linear_line, null);
				
				View showMore = (View) inflater.inflate(R.layout.include_users_showmore, null);
				final Button showMoreBtn = (Button) showMore.findViewById(R.id.showmore_btn);
				showMoreBtn.setOnClickListener(new View.OnClickListener() 
				{
					@Override
					public void onClick(View v) 
					{
						showMoreBtn.setVisibility(Button.GONE);
						loadUsers(offset+1);
					}
				});

				// remove padding from line for equal display of show more buttons
				line.setPadding(0, 0, 0, 0);
				
				line.addView(showMore);
				linear_layout.addView(line);
				line = null;
			}
		}
	}
	
	public JSONArray httpGetUsers(int offset)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_pets"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.opt("users") != null)
					return json_data.optJSONArray("users");
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
	
	class getUsers extends AsyncTask<String, Integer, JSONArray>
	{
		int offset = 0;
		
		public getUsers(int pOffset)
		{
			super();
			offset = pOffset;
		}
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			try 
			{
				showUsers(result, offset);
			}
			catch (JSONException e) 
			{
			}
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return httpGetUsers(offset);
		}

		@Override
		protected void onPreExecute() {
			showLoadingDialog();
		}
	}
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedUsers();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}

}
