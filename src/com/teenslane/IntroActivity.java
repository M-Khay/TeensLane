package com.teenslane;	

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.telephony.TelephonyManager;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class IntroActivity extends SexActivity
{
	public LocalService mBoundService = null;
	public Boolean mIsBound;
	public boolean updatedLocation = false;
	public boolean settingPrompted = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		updatedLocation = false; 
		LocalService.chatting = 0; 
	
		try
		{
			doUnbindService();
		}
		catch(Exception e)
		{
		}
		
		super.onCreate(savedInstanceState);
		SexActivity.menuReturn = false;
		
		showLoadingDialog();
		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		if(telephonyManager.getDeviceId() != null)
			SexActivity.me.session = telephonyManager.getDeviceId();
		else
			SexActivity.me.session = Settings.Secure.getString(getApplicationContext().getContentResolver(),  Settings.Secure.ANDROID_ID);
		
		new besure().execute("once again pre save user");
	}

	public double[] getGPS() 
	{
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);

        Location l = null;
        
        if(providers.size() > 0)
        {
        	for(int i=providers.size()-1; i>=0; i--) //the last one is always accurate
	        {
        		l = lm.getLastKnownLocation(providers.get(i));
        		if(l != null)
        			break;
	        	
	        }
        }
        
        double[] gps = new double[2];
        if (l != null) 
        {
                gps[0] = l.getLatitude();
                gps[1] = l.getLongitude();
        }
        else
        {
        	// Define a listener that responds to location updates
        	LocationListener locationListener = new LocationListener() 
        	{
        	    public void onLocationChanged(Location location) 
        	    {
        	    	if(updatedLocation == false)
        	    	{
        	    		updatedLocation = true;
        	    		new saveUserDataLocationTask(location.getLatitude(), location.getLongitude()).execute("save user location");
        	    	}
        	    }

        	    public void onStatusChanged(String provider, int status, Bundle extras) { }

        	    public void onProviderEnabled(String provider) {  }

        	    public void onProviderDisabled(String provider)
        	    { 
        	    	if(settingPrompted == false && updatedLocation == false)
        	    	{
        	    		settingPrompted = true;
        	    		Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        	    		startActivity(myIntent);
        	    	}
                }
        	    
        	  };

        	  try
        	  {
        		  lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        	  }
        	  catch(Exception e)
        	  {
        	  }
        	  
        	  try
        	  {
        		  lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        	  }
        	  catch(Exception e)
        	  {
        	  }
        	  
        	  try
        	  {
        		  lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, locationListener);
        	  }
        	  catch(Exception e)
        	  {
        	  }
        }
        return gps;
	}
	
	public void prepareUser()
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "xmpp_register_me"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httpclient.execute(httpPost);
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
	public void proceedIntro()
	{
		if(SexActivity.me.nickname.equals("") == false && SexActivity.me.terms_agreed == 1 && SexActivity.me.age >= 13 && SexActivity.me.age <= 19)
		{
			setContentView(R.layout.activity_intro);
			setupButtonsLoged();
			
			new xmppUser().execute("prepare user");
			
			double[] location = getGPS();
			if(location[0] != 0.0 && location[1] != 0.0)
				new saveUserDataLocationTask(location[0], location[1]).execute("save user location");
			
		}
		else
		{
			setContentView(R.layout.activity_intro_notlogged);
			
			ImageView createProfile = (ImageView) findViewById(R.id.image_create);
			createProfile.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					//IntroActivity.this.finish();
					Intent mainIntent = new Intent(getBaseContext(), MyprofileActivity.class);
		       		startActivity(mainIntent);
				}
			});
		}
			
		getUserEmails();
	} 
	
	class xmppUser extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
		}
		
		@Override
		protected String doInBackground(String... params) 
		{
			prepareUser();
			return "ok";
		}
	}
	
	public void saveUserDataLocation(double latitude, double longitude)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "set_user_location"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(latitude)));
	        nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(longitude)));
	     	
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httpclient.execute(httpPost);
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
	
	public void saveUserDataEmails(ArrayList<String> emails)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "set_user_emails"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("emails", new JSONArray(emails).toString()));
	     	
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			httpclient.execute(httpPost);
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
	
	public void getUserEmails()
	{
		Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
		Account[] accounts = AccountManager.get(getBaseContext()).getAccounts();
		ArrayList<String> emails = new ArrayList<String>();
		for(int i=0; i<accounts.length; i++) 
		{
		    if(emailPattern.matcher(accounts[i].name).matches()) 
		    {
		    	emails.add(accounts[i].name);
		    }
		}
		
		if(emails.size() > 0)
			new saveUserDataEmailsTask(emails).execute("save user emails");
		
	}
	
	public void setupButtonsLoged()
	{
		Button users = (Button) findViewById(R.id.btn_users);
		users.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent usersIntent = new Intent(getBaseContext(), UsersActivity.class);
				startActivity(usersIntent);
			}
		});
		
		Button filter = (Button) findViewById(R.id.btn_filter);
		filter.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				/*
				Bundle extras = new Bundle();
				extras.putInt("jump_to_users", 1);
				Intent filterIntent = new Intent(getBaseContext(), FilterActivity.class);
				filterIntent.putExtras(extras);
				startActivity(filterIntent);
				*/
				Intent forumIntent = new Intent(getBaseContext(), ForumActivity.class);
				startActivity(forumIntent);
			} 
		});
		
		Button pets = (Button) findViewById(R.id.btn_pets);
		pets.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent petsIntent = new Intent(getBaseContext(), MypetsActivity.class);
				startActivity(petsIntent);
			} 
		});
		
		Button feed = (Button) findViewById(R.id.btn_feed);
		feed.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				Intent feedIntent = new Intent(getBaseContext(), MyfeedActivity.class);
				startActivity(feedIntent);
			} 
		});
		
		Button mapView = (Button) findViewById(R.id.btn_map);
		mapView.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v)
			{
				Intent mapIntent = new Intent(getBaseContext(), MapActivity.class);
				startActivity(mapIntent);
			}
		});
		
		Button messages = (Button) findViewById(R.id.btn_messages); 
		messages.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v)
			{
				Intent messagesIntent = new Intent(getBaseContext(), MessagesActivity.class);
				startActivity(messagesIntent);
			}
		});
		
		ImageView createProfile = (ImageView) findViewById(R.id.loaded_image);
		createProfile.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				//IntroActivity.this.finish();
				Intent myprofileIntent = new Intent(getBaseContext(), MyprofileActivity.class);
	       		startActivity(myprofileIntent);
			}
		});
		
		if(SexActivity.me.image != null && SexActivity.me.image.equalsIgnoreCase("") == false)
		{
			//set user image
			String userImageUrl = SexActivity.REST_SERVER_NAME + REST_IMAGE_SIZE_240 + "/" + SexActivity.me.image;
    		new setImageFromUrl(userImageUrl, createProfile, "circle").execute("load user image");
		}
		else
		{
			if(SexActivity.me.gender == SexActivity.attributesGenderMan)
			{
				createProfile.setImageResource(R.drawable.intro_nophoto_man);
			}
			else if(SexActivity.me.gender == SexActivity.attributesGenderWoman)
			{
				createProfile.setImageResource(R.drawable.intro_nophoto_woman);
			}
			else if(SexActivity.me.gender == SexActivity.attributesGenderPairStraight)
			{
				createProfile.setImageResource(R.drawable.intro_nophoto_pair_manwoman);
			}
			else if(SexActivity.me.gender == SexActivity.attributesGenderPairLesbian)
			{
				createProfile.setImageResource(R.drawable.intro_nophoto_pair_woman);
			}
			else if(SexActivity.me.gender == SexActivity.attributesGenderPairGay)
			{
				createProfile.setImageResource(R.drawable.intro_nophoto_pair_man);
			}
		}
		
		startMessenger();
	} 
	
	private class saveUserDataEmailsTask extends AsyncTask<String, Integer, String>
	{ 
		ArrayList<String> emails;
		
		public saveUserDataEmailsTask(ArrayList<String> pEmails)
		{
			super(); 
			emails = pEmails;
		}
		
		protected void onPostExecute(String result)
	    {
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			saveUserDataEmails(emails);
			return "ok";
		}
	}
	
	private boolean isMyServiceRunning()
	{
	    ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
	    {
	        if("com.teenslane.LocalService".equals(service.service.getClassName()))
	        	return true;
	        
	    }
	    
	    return false;
	}
	
	public void startMessenger()
	{
		if(isMyServiceRunning() == false)
			startService(new Intent(IntroActivity.this, LocalService.class));
		  
		doBindService();
	}
	  
	private class saveUserDataLocationTask extends AsyncTask<String, Integer, String>
	{
		double latitude = 0;
		double longitude = 0;
		
		public saveUserDataLocationTask(double pLatitude, double pLongitude)
		{
			super();
			latitude = pLatitude;
			longitude = pLongitude;
		}
		
		protected void onPostExecute(String result)
	    {
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			saveUserDataLocation(latitude, longitude);
			return "ok";
		}
	}
	
	class besure extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			new getUserData().execute("load user data");
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			preSaveUser();
			return "ok";
		}
	}
	
	public void preSaveUser()
	{
		try  
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2); 
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "pre_save"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session)); 
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse response = httpclient.execute(httpPost);
			  
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.opt("success") != null && json_data.getString("success").equals("") == false)
				{
					String[] tokenParts = json_data.getString("success").split("::");
					SexActivity.APP_TOKEN = "";
					SexActivity.APP_ESSENCE = "";
					if(tokenParts.length == 2)
					{
						SexActivity.APP_TOKEN = tokenParts[0];
						SexActivity.APP_ESSENCE = tokenParts[1];
					}
					else
						Toast.makeText(this, getString(R.string.toast_error_later), Toast.LENGTH_SHORT).show();
					
				}
				else
					Toast.makeText(this, getString(R.string.toast_error_later), Toast.LENGTH_SHORT).show();
				
			}
			else
				Toast.makeText(this, getString(R.string.toast_error_later), Toast.LENGTH_SHORT).show();
			
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
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			hideLoadingDialog();
			proceedIntro();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}
	
	void doBindService() 
	{
		bindService(new Intent(IntroActivity.this, LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;
	}

	void doUnbindService() 
	{
	    if(mIsBound) 
	    {
	    	unbindService(mConnection);
	        mIsBound = false;
	    }
	}
	 
	private ServiceConnection mConnection = new ServiceConnection() 
	{
		@Override
	    public void onServiceConnected(ComponentName className, IBinder service) 
	    {
	        mBoundService = ((LocalService.LocalBinder)service).getService();
	    }

		@Override
	    public void onServiceDisconnected(ComponentName className) 
	    {
	        mBoundService = null;
	    }

	};
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		LocalService.chatting = 0;
		try
		{
			doUnbindService();
		}
		catch(Exception e)
		{
		}
	}
}
