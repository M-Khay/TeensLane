package com.teenslane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.graphics.Shader.TileMode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SexActivity extends Activity
{
	public static final String REST_SERVER_NAME = "http://sex.pike-apps.com";
	public static final String REST_SCRIPT = "/rest.teenslane.php"; // "/rest.staging.marek.php";
	public static final String REST_IMAGE_SIZE_36 = "/sized_teenslane/36";
	public static final String REST_IMAGE_SIZE_60 = "/sized_teenslane/60";
	public static final String REST_IMAGE_SIZE_148 = "/sized_teenslane/148";
	public static final String REST_IMAGE_SIZE_240 = "/sized_teenslane/240";
	public static final String REST_IMAGE_COVER = "/sized_teenslane/cover";
	public static String APP_TOKEN = "";
	public static String APP_ESSENCE = "";
	
	public static Integer httpTimeout = 30000;
	public static Integer soTimeout = 30000;
	
	public static User me = new User(); 
	public static User otherUser = new User();
	public static SexActivity instance = null;
	public static ArrayList<User> users = new ArrayList<User>();
	
	public static boolean menuReturn = false;
	public static boolean filterReturn = false;
	public static boolean reloadGallery = false;
	public static int usersPerPage = 9;
	public static int usersMapFilterPerPage = 250;
	public Dialog loadingDialog = null;
	
	public static ArrayList<JSONObject> loadedUsers = new ArrayList<JSONObject>();
	
	public static final int attributesBodySexy = 2;
	public static final int attributesBodySporty = 4;
	public static final int attributesBodySlender = 8;
	public static final int attributesBodyJuicy = 16;
	public static final int attributesBodyMuscular = 32;
	
	public static final int attributesEthnicityAsian = 2;
	public static final int attributesEthnicityBlack = 4;
	public static final int attributesEthnicityOther = 8;
	public static final int attributesEthnicityLatino = 16;
	public static final int attributesEthnicityWhite = 32;
	
	public static final int attributesGenderMan = 2;
	public static final int attributesGenderWoman = 4;
	public static final int attributesGenderPairStraight = 8;
	public static final int attributesGenderPairLesbian = 16;
	public static final int attributesGenderPairGay = 32;
	
	public static final int attributesLovesMissionary = 2;
	public static final int attributesLoves69 = 4;
	public static final int attributesLovesDoggie = 8;
	public static final int attributesLovesBJ = 16;
	public static final int attributesLovesPiss = 32;
	public static final int attributesLovesSM = 64;
	public static final int attributesLovesOrgy = 128;
	public static final int attributesLovesFetish = 256;
	public static final int attributesLovesHJ = 512;
	
	public static final int feed_status = 1;
	public static final int feed_viewed = 2;
	public static final int feed_pets_added = 3;
	public static final int feed_pets_removed = 4;
	public static final int feed_kiss = 5;
	public static final int feed_slap = 6;
	public static final int feed_badge = 7;
	public static final int feed_gift = 8;
	public static final int feed_rate_image_hot = 9;
	public static final int feed_rate_image_wtf = 15;
	public static final int feed_profile_image_added = 10;
	public static final int feed_profile_image_changed = 11;
	public static final int feed_gallery_image_added = 12;
	public static final int feed_system_message = 13;
	public static final int feed_advertisement_message = 14;

	public static final int feature_feed_view = 1;
	public static final int feature_pets_added = 2;
	public static final int feature_pets_removed = 4;
	
	public static final int gifts_resources[] = { 
		R.drawable.animal_bad_piggy,
		R.drawable.animal_bunny,
		R.drawable.animal_chick,
		R.drawable.animal_duck,
		R.drawable.animal_piggy,
		R.drawable.animal_teddy,
		
		R.drawable.badge_man_ass,
		R.drawable.badge_man_bad,
		R.drawable.badge_man_hot,
		R.drawable.badge_man_mrbig,
		R.drawable.badge_woman_bad,
		R.drawable.badge_woman_hot_ass,
		R.drawable.badge_woman_hot_body,
		R.drawable.badge_woman_sexy_legs,

		R.drawable.condom_angel,
		R.drawable.condom_badboy,
		R.drawable.condom_cactus,
		R.drawable.condom_devil,
		R.drawable.condom_gigolo,
		R.drawable.condom_ninja,
		R.drawable.condom_sexy,
		R.drawable.condom_sm,
		R.drawable.condom_viking
	};
	
	public static final int gifts_id[] = {  
		2130837504,
		2130837505,
		2130837506,
		2130837507,
		2130837508,
		2130837509,

		2130837512,
		2130837513,
		2130837514,
		2130837515,
		2130837516,
		2130837517,
		2130837518,
		2130837519,

		2130837534,
		2130837535,
		2130837536,
		2130837537,
		2130837538,
		2130837539,
		2130837540,
		2130837541,
		2130837542
	};
	
	public static SexActivity getInstance()
	{
		return instance;
	}
	
	public static String getTz()
	{
		TimeZone tz = TimeZone.getDefault();
		Date now = new Date();
		int offsetFromUtc = tz.getOffset(now.getTime())/36000;
		
		int hours = (int) Math.floor(offsetFromUtc/100);
		int minutes = (int)Math.floor(offsetFromUtc%100);
		
		if(minutes<0)
			minutes = minutes*-1;
		
		if(minutes == 50)
			minutes = 30;
		
		String minutesStr = Integer.toString(minutes);
		String sign = (hours<0) ? "-" : "";
		
		if(hours<0)
			hours = hours*-1;
		
		String hoursStr = Integer.toString(hours);
		if(hoursStr.length() == 1)
			hoursStr = "0" + hoursStr;
		
		if(minutesStr.length() == 1)
			minutesStr = minutesStr + "0";
		
		String timezone = sign + hoursStr + ":" + minutesStr;
		return timezone;
	}
	
	
	protected void onCreate(Bundle bundle)
	{
		super.onCreate(bundle);
		instance = this;
		
		TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
		
		if(telephonyManager.getDeviceId() != null)
			me.session = telephonyManager.getDeviceId();
		else
			me.session = Settings.Secure.getString(getApplicationContext().getContentResolver(),  Settings.Secure.ANDROID_ID);
		
	}
	
	
	static Integer isConnected()
	{
		try
		{
			if(instance != null)
			{
				ConnectivityManager conMgr =  (ConnectivityManager) instance.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo i = conMgr.getActiveNetworkInfo();
			  
				if(i == null || !i.isConnected() || !i.isAvailable())
					return 0;
				else
					return 1;
			  
		  	}
		  	else 
		  		return 1;
			
		}
		catch(Exception e)
		{
			return 0;
		}
	}
	
	public float getScreenHeight() 
	{
	    DisplayMetrics displaymetrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	    return (float) displaymetrics.heightPixels;
	}
	
	public float getScreenWidth() 
	{
	    DisplayMetrics displaymetrics = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
	    return (float) (displaymetrics.widthPixels/2);
	}
	
	public int getTopOffset()
	{
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
	
		 int value = 25;
		 switch (metrics.densityDpi) 
		 {
		     case DisplayMetrics.DENSITY_HIGH:
		         value = 30;
		         break;
		     case DisplayMetrics.DENSITY_MEDIUM:
		         value = 22;
		         break;
		     case DisplayMetrics.DENSITY_LOW:
		         value = 20;
		         break;
		     default:
		    	 break;
		 }
		 
	    return value;
	} 


	public static User getUserFromJsonObj(JSONObject jUser) throws JSONException
	{
		me = new User(jUser.getInt("id"), jUser.getString("session"), jUser.getString("nickname"), jUser.getInt("gender"), jUser.getInt("iwant"), jUser.getString("image"), jUser.getString("cover"), jUser.getString("dob"), jUser.getString("location_country"), jUser.getInt("credits"), jUser.getInt("premium"), jUser.getInt("hide_on_map"), jUser.getInt("hide_on_search"), jUser.getInt("terms_agreed"), jUser);
		return me;
	}
	
	public static User getOtherUserFromJsonObj(JSONObject jUser) throws JSONException
	{
		otherUser = new User(jUser.getInt("id"), jUser.getString("session"), jUser.getString("nickname"), jUser.getInt("gender"), jUser.getInt("iwant"), jUser.getString("image"), jUser.getString("cover"), jUser.getString("dob"), jUser.getString("location_country"), jUser.getInt("credits"), jUser.getInt("premium"), jUser.getInt("hide_on_map"), jUser.getInt("hide_on_search"), jUser.getInt("terms_agreed"), jUser);
		return otherUser;
	}
	
	public void httpGetOtherUserData(int pId)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_other_user_data"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("id", String.valueOf(pId)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1)
				{
					JSONObject jUser = json_data.getJSONObject("userdata");
					otherUser = SexActivity.getOtherUserFromJsonObj(jUser);
					
					String country = getResources().getConfiguration().locale.getISO3Country();
					otherUser.metric = "metric";
			       	if(country.equalsIgnoreCase("USA") == true || country.equalsIgnoreCase("GBR") == true || country.equalsIgnoreCase("UK") == true)
			       		otherUser.metric = "imperial";

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
	
	public boolean httpDeleteUser()
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "delete_account"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
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
	
	public void httpGetUserData()
	{ 
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			 
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_user_data"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1)
				{
					JSONObject jUser = json_data.getJSONObject("userdata");
					me = SexActivity.getUserFromJsonObj(jUser);
					
					String country = getResources().getConfiguration().locale.getISO3Country();
					me.metric = "metric";
			       	if(country.equalsIgnoreCase("USA") == true || country.equalsIgnoreCase("GBR") == true || country.equalsIgnoreCase("UK") == true)
			       		me.metric = "imperial";

				}
			}
		} 
     	catch (UnsupportedEncodingException e) 
     	{
     		//Log.d("user UnsupportedEncodingException", e.getMessage(), e);
		}
        catch (ClientProtocolException e) 
        {
        	//Log.d("user ClientProtocolException", e.getMessage(), e);
        }
        catch (IOException e) 
        {
        	//Log.d("user IOException", e.getMessage(), e);
        } 
		catch(Exception e)
		{
			//Log.d("user Exception", e.getMessage(), e);
  		}
	}

	class setImageFromUrl extends AsyncTask<String, Integer, Bitmap>
	{
		String imageUrl = "";
		ImageView imageView = null;
		ProgressBar imageLoading = null; 
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		Animation animate = null;
		String crop = "";
		boolean setVisibility = true;
		
		public setImageFromUrl(String url, ImageView view, ProgressBar loading, int sampleSize)
		{
			super();
			imageUrl = url;
			imageView = view;
			imageLoading = loading;
			bmOptions.inSampleSize = sampleSize;
		}
		
		public setImageFromUrl(String url, ImageView view, ProgressBar loading)
		{
			super();
			imageUrl = url;
			imageView = view;
			imageLoading = loading;
			bmOptions.inSampleSize = 1;
		}
		
		public setImageFromUrl(String pUrl, ImageView pImageVview)
		{
			super();
			imageUrl = pUrl;
			imageView = pImageVview;
			bmOptions.inSampleSize = 1;
		}
		
		public setImageFromUrl(BitmapFactory.Options pOption, String pUrl, ImageView pImageView)
		{
			super();
			imageUrl = pUrl;
			imageView = pImageView;
			bmOptions = pOption;
		}
		
	    public setImageFromUrl(Animation pAnimation, Options pOption, String pUrl, ImageView pImageView) 
	    {
	    	super();
			imageUrl = pUrl;
			imageView = pImageView;
			bmOptions = pOption;
			animate = pAnimation;
		}

		public setImageFromUrl(String pUrl, ImageView pImageView, String pParam) 
		{
			super();
			imageUrl = pUrl;
			imageView = pImageView;
			crop = pParam;
		}

		public setImageFromUrl(Options pOptions, String pUrl, ImageView pImageView, boolean pVisibility) 
		{
			super();
			imageUrl = pUrl;
			imageView = pImageView;
			bmOptions = pOptions;
			setVisibility = pVisibility;
		}

		public setImageFromUrl(AlphaAnimation pAnimation, Options pOptions, String pUrl, ImageView pImageView, boolean pVisibility) 
		{
			super();
			imageUrl = pUrl;
			imageView = pImageView;
			bmOptions = pOptions;
			animate = pAnimation;
			setVisibility = pVisibility;
		}

		protected void onPostExecute(Bitmap result)
	    {
	    	if(!isCancelled())
	    	{
				Log.d("Image Loader", "Loaded " + imageUrl);
				
	    		if(result != null)
			    {
		    		if(imageLoading != null)
		    			imageLoading.setVisibility(ProgressBar.GONE);
		    		
		    		if(imageView.getDrawingCache() != null)
		    		{
		    			imageView.getDrawingCache().recycle();
		    		}
		    		
		    		if(crop.equalsIgnoreCase("circle") == true)
		    		{
		    			Bitmap bitmap = result;
		    			Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

		    			BitmapShader shader = new BitmapShader(bitmap,  TileMode.CLAMP, TileMode.CLAMP);
		    			Paint paint = new Paint();
		    			paint.setFlags(Paint.ANTI_ALIAS_FLAG); 
    			        paint.setShader(shader);

		    			Canvas c = new Canvas(circleBitmap);
		    			c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

		    			// no need anymore
		    			bitmap.recycle();
		    			imageView.setImageBitmap(circleBitmap); 
		    		}
		    		else
		    		{
		    			imageView.setImageBitmap(result);
		    		}
		    		
		    		if(setVisibility == true)
		    		{
		    			imageView.refreshDrawableState();
		    			imageView.setVisibility(ImageView.VISIBLE);
		    			
		    			if(animate != null)
			    			imageView.startAnimation(animate);
		    		}
			    }
	    	} else {
				Log.d("Image Loader", "Cancelled " + imageUrl);	    		
	    	}
	    }

		@Override
		protected Bitmap doInBackground(String... params) 
		{			
			Context app = getApplicationContext();
			synchronized(app) {
			
				Log.d("Image Loader", imageUrl);
			
				// get view parent activity
				if (imageView == null) {
					this.cancel(true);
				}
				else {
					// check if parent activity is alive
					try {
						Activity host = (Activity)imageView.getContext();
						if (host == null || host.isFinishing()) {
							this.cancel(true);
							Log.d("Image Loader", "Activity died " + imageUrl);
						}
					} catch (Exception e) {
						
					}
				}
				
				// do not continue if cancelled
				if (isCancelled()) {
					Log.e("Image Loader", "Cancelled " + imageUrl);
					return null;
				}
				
				Bitmap bitmap = Helper.LoadImage(imageUrl, bmOptions);		
				return bitmap;
			}
		}
	}
	
    @SuppressLint("NewApi")
    public static <P, T extends AsyncTask<P, ?, ?>> void executeTask(T task, P... params) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }
    }

	public void showLoadingDialog()
	{
		if(loadingDialog == null || loadingDialog.isShowing() == false)
		{
			loadingDialog = new Dialog(this);
			loadingDialog.setContentView(R.layout.dialog_loading);
			//loadingDialog.setTitle(getString(R.string.dialog_loading_title));
			try
			{
				if(isFinishing() == false)
					loadingDialog.show();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public void hideLoadingDialog()
	{
		if(loadingDialog != null && loadingDialog.isShowing() == true)
		{
			try
			{
				if(isFinishing() == false)
					loadingDialog.cancel();
			}
			catch(Exception e)
			{
			}
		}
	}
	
	public void updateNewMessages(final RelativeLayout newMessage, int cost) {
		boolean free = false;
		if (cost < 1)
			free = true;
		
		final Button purchaseMoreBlock = (Button)newMessage.findViewById(R.id.buy_credit_block);
		final RelativeLayout writeMessageBlock = (RelativeLayout)newMessage.findViewById(R.id.write_message_block);
		final RelativeLayout creditsBlock = (RelativeLayout)newMessage.findViewById(R.id.credits_block);
		
		// set purchase more credit action
		purchaseMoreBlock.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{				
				Bundle extras = new Bundle();
				extras.putInt("premium", 1);
				Intent userDetail = new Intent(getBaseContext(), MyprofileActivity.class);
				userDetail.putExtras(extras);
				startActivity(userDetail);
			}
		});
		
		if (free) {
			purchaseMoreBlock.setVisibility(View.GONE);
			creditsBlock.setVisibility(View.GONE);
			
			writeMessageBlock.setVisibility(View.VISIBLE);
		}
		else {
			creditsBlock.setVisibility(View.VISIBLE);	
			
			TextView costsView = (TextView)creditsBlock.findViewById(R.id.text_costs);
			TextView creditsLeftView = (TextView)creditsBlock.findViewById(R.id.text_credit_left);
			
			// set cost
			costsView.setText(getString(R.string.initialization_costs) + " " + cost + " " + getString(R.string.credits));
			
			// set credit
			creditsLeftView.setText(me.credits + " " + getString(R.string.credits));
			
			// enable or hide purchase more if not enough credit	
			if (cost > me.credits) {
				purchaseMoreBlock.setVisibility(View.VISIBLE);		
				writeMessageBlock.setVisibility(View.GONE);
			} 
			else {
				purchaseMoreBlock.setVisibility(View.GONE);		
				writeMessageBlock.setVisibility(View.VISIBLE);
			}
		}
	}
}
