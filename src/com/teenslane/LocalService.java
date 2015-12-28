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
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceActivities.Controller}
 * and {@link LocalServiceActivities.Binding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service 
{
    public JSONObject[] notifMessages = new JSONObject[2];
    public static String session = ""; 
    public static int chatting = 0;
    Timer timer = new Timer(true);
    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder 
    {
        LocalService getService() 
        {
            return LocalService.this;
        }
    }
    
    @Override
    public void onCreate() 
    {
    	TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    	
    	if(telephonyManager.getDeviceId() != null)
		{
			session = telephonyManager.getDeviceId();
		}
		else
		{
			session = Settings.Secure.getString(getApplicationContext().getContentResolver(),  Settings.Secure.ANDROID_ID);
		}
    }
    
    class getToken extends AsyncTask<String, Integer, String>
    {
    	protected void onPostExecute(String result)
	    {
   			SexActivity.executeTask(new getMessages(), "get new messages");
	    }
    	
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetToken();
			return "";
		}
    	
    }
    
    class getMessages extends AsyncTask<String, Integer, JSONObject>
    {
    	protected void onPostExecute(JSONObject result)
	    {
    		if(result != null)
    		{
    			try 
    			{
    				if(chatting != result.getInt("id"))
    					createNotification(0, result.getString("nickname"), result.getInt("id"));
    				
				}
    			catch (JSONException e) 
    			{
				}
    		}
    		else
    		{
    		}
	    }
    	
		@Override
		protected JSONObject doInBackground(String... params) 
		{
			return httpGetMessages();
		}
    	
    }
    
    public void httpGetToken()
    {
    	try  
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "pre_save"));
	        nameValuePairs.add(new BasicNameValuePair("session", session)); 
	     	
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			
			HttpResponse response = httpclient.execute(httpPost);
			  
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.opt("success") != null && json_data.getString("success").equals("") != false)
				{
					String[] tokenParts = json_data.getString("success").split("::");
					SexActivity.APP_TOKEN = "";
					SexActivity.APP_ESSENCE = "";
					if(tokenParts.length == 2)
					{
						SexActivity.APP_TOKEN = tokenParts[0];
						SexActivity.APP_ESSENCE = tokenParts[1];
					}
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
    
    public JSONObject httpGetMessages()
	{
    	try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_new_message"));
	        nameValuePairs.add(new BasicNameValuePair("session", session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.get("nickname") != null && json_data.getString("nickname").equals("") == false) 
				{
					return json_data;
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
    	
    	return null;
	}
   

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
    {
		if (timer != null) {
			timer.cancel();
			timer.purge();
			timer = null;
		}
		
		timer = new Timer(true);
    	startCheck();
    	
    	return START_REDELIVER_INTENT;
    }
    
    public void startCheck()
    {
    	Integer timing = 900000;
    	
    	final Handler handler = new Handler();
    	
    	TimerTask doAsynchronousTask = new TimerTask() 
    	{       
            @Override
            public void run() 
            {
                handler.post(new Runnable() 
                {
                    public void run() 
                    {   
                        Log.e("time", "timer: check (with token get)");
                        
                    	try 
                        {
                        	//SexActivity.executeTask(new getMessages(), "oiasjdoi");
                    		SexActivity.executeTask(new getToken(), "obtain application token");
                        }
                        catch (Exception e) 
                        {
                        }
                    }
                });
            }
        };
        
        timer.schedule(doAsynchronousTask, 0, timing);
    }
    
    public void startCheckInApp()
    {
    	Integer timing = 30000;
    	
    	final Handler handler = new Handler();
    	
    	TimerTask doAsynchronousTask = new TimerTask() 
    	{       
            @Override
            public void run() 
            {
                handler.post(new Runnable() 
                {
                    public void run() 
                    {       
                    	Log.e("time", "timer: check in app (messages)");
                    
                        try 
                        {
                        	SexActivity.executeTask(new getMessages(), "oiasjdoi");
                        }
                        catch (Exception e) 
                        {
                        }
                    }
                });
            }
        };

        timer.schedule(doAsynchronousTask, 0, timing);
    }

    
    @Override
    public void onDestroy() 
    {
    }

    @Override
    public boolean onUnbind (Intent intent)
    {    	
    	try
    	{
    		if (timer != null) {
    			timer.cancel();
    			timer.purge();
    			timer = null;
    		}
    		
    		timer = new Timer(true);
    		startCheck();
    	}
    	catch(Exception e)
    	{
    	}
		return true;
    }
    
    @Override 
    public void onRebind(Intent intent)
    {
    	try
    	{
    		if (timer != null) {
    			timer.cancel();
    			timer.purge();
    			timer = null;
    		}
    		
    		timer = new Timer(true);
    		startCheckInApp();
    	}
    	catch(Exception e)
    	{
    	}
    }
    
    @Override
    public IBinder onBind(Intent intent) 
    {
    	
    	try
    	{
    		if (timer != null) {
    			timer.cancel();
    			timer.purge();
    			timer = null;
    		}
    		
    		timer = new Timer(true);
    		startCheckInApp();
    	}
    	catch(Exception e)
    	{
    	}
        return mBinder;
    }
    
    
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    //public void createNotification(Integer index, String name, String msgId, String image, String fromSession)
    public void createNotification(Integer index, String name, int packetId)
    {
    	// Prepare intent which is triggered if the
	    Intent intent = new Intent(this, UserDetailActivity.class);
	    
	    Bundle extras = new Bundle();
	  	extras.putString("user_id", String.valueOf(packetId));
	  	extras.putInt("force_messages", 1);
	  	intent.putExtras(extras);
	  	
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
	    
	    // Build notification
	    // Actions are just fake
	    Notification notify = new NotificationCompat.Builder(this)
	          .setContentTitle(getString(R.string.newmessage_from) + ": " + name)
	          .setSmallIcon(R.drawable.marker_woman)
	          .setContentInfo("message" + String.valueOf(packetId))
	          .setContentIntent(pIntent).build();
	      
	    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	      
	    // Hide the notification after its selected
	    notify.flags |= Notification.FLAG_AUTO_CANCEL;
	    notificationManager.notify(index + packetId, notify);
    }
}
 