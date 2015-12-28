package com.teenslane;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;


public class LoadActivity extends Activity 
{
	ConnectivityManager conMgr;
	ProgressBar pg;
	TextView infoText;
	Button reloadBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load);
		
		conMgr =  (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		pg = (ProgressBar) findViewById(R.id.progressBar1);
		infoText = (TextView) findViewById(R.id.load_info_text);
		reloadBtn = (Button) findViewById(R.id.reload);
		
		checkConnectionStateAndProcessApp();
	}
	

	public void checkConnectionStateAndProcessApp()
	{
		pg.setVisibility(ProgressBar.VISIBLE);
		infoText.setVisibility(TextView.INVISIBLE);
		reloadBtn.setVisibility(Button.INVISIBLE);
		
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		
		if(i == null || !i.isConnected() || !i.isAvailable())
		{
			Toast.makeText(this, getString(R.string.toast_not_connected), Toast.LENGTH_LONG).show();
			
			//turn off progress bar and turn on info text&button
			pg.setVisibility(ProgressBar.INVISIBLE);
			infoText.setVisibility(TextView.VISIBLE);
			reloadBtn.setVisibility(Button.VISIBLE);
			
			reloadBtn.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					checkConnectionStateAndProcessApp();
				}
			});
			
		}	
		else
		{
			runApp();
		}
	}
	
	public void runApp()
	{
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		if(telephonyManager.getDeviceId() != null)
		{
			SexActivity.me.session = telephonyManager.getDeviceId();
		} 
		else
		{
			SexActivity.me.session = Settings.Secure.getString(getApplicationContext().getContentResolver(),  Settings.Secure.ANDROID_ID);
		}
		
	    Timer timer = new Timer();
	    timer.schedule(new TimerTask() { 
			
			@Override
			public void run() 
			{
				saveUserLocation();
			} 
			
		}, 2000);
	}
	 
	
	public void saveUserLocation()
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
						
						LoadActivity.this.finish();
						Intent mainIntent = new Intent(getBaseContext(), IntroActivity.class); 
			       		startActivity(mainIntent);
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
     		//Log.d("presave UnsupportedEncodingException", e.getMessage(), e);
		}
        catch (ClientProtocolException e) 
        {
        	//Log.d("presave ClientProtocolException", e.getMessage(), e);
        }
        catch (IOException e) 
        {
        	//Log.d("presave IOException", e.getMessage(), e);
        }
		catch(Exception e)
		{
			//Log.d("presave Exception", e.getMessage(), e);
  		}
	}
}
