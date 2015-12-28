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

import android.app.Dialog;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teenslane.R;
 
public class MapActivity extends FragmentActivity implements LocationListener, OnInfoWindowClickListener {
 
    GoogleMap googleMap;
    public Dialog loadingDialog = null;
 
    public boolean animate = true;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	animate = true;
    	
    	LocalService.chatting = 0;
        super.onCreate(savedInstanceState);
        
        showLoadingDialog();
		SexActivity.executeTask(new getUserData(), "load user data");
    }
    
    public void proceedMap()
    {
    	setContentView(R.layout.activity_map);
        
    	TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.map_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				MapActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_search);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				MapActivity.this.finish();
				
				Intent filterIntent = new Intent(getBaseContext(), FilterActivity.class);
				Bundle extras = new Bundle();
				
				extras.putString("jump_to_map", "1");
				filterIntent.putExtras(extras);
				
				startActivity(filterIntent);
			}
		});
    	
        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
 
        // Showing status
        if(status != ConnectionResult.SUCCESS){ // Google Play Services are not available
 
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            try
    		{
    			if(isFinishing() == false)
    				dialog.show();
    		}
    		catch(Exception e)
    		{
    		}
        }
        else
        {
        	// Google Play Services are available
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            
            googleMap.setMyLocationEnabled(true);
            if(animate == true)
            {
            	LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            	 
                // Creating a criteria object to retrieve provider
                Criteria criteria = new Criteria();
     
                // Getting the name of the best provider
                String provider = locationManager.getBestProvider(criteria, true);
     
                if(provider != null)
                {
                	/*
                	// Getting Current Location
	                Location location = locationManager.getLastKnownLocation(provider);
	                if(location!=null)
	                {
	                    onLocationChanged(location);
	                }
	                */
                }
                else
                {
                	googleMap.setMyLocationEnabled(false);
                }
            }
            
            Bundle extras = getIntent().getExtras();
            if(extras != null && extras.isEmpty() == false && extras.containsKey("load_users"))
            {
            	//Log.d("loading users", "from filter");
            	//return from filter
            	SexActivity.loadedUsers = new ArrayList<JSONObject>();
            	loadUsers(1, SexActivity.usersMapFilterPerPage);
            }
            else 
            {
	            //if(SexActivity.loadedUsers.size()>0)
	            //{
	            	//Log.d("loading users", "got users");
	            	//showUsersMarkers(googleMap, SexActivity.loadedUsers);
	            //}
	            //else
	            //{
	            	Log.d("loading users", "from default filter");
	            	loadUsers(1, SexActivity.usersMapFilterPerPage);//usersPerPage);
	            //}
            }
        }
    }
    
    public void loadUsers(int offset, int usersPerPage)
	{
		showLoadingDialog();
		SexActivity.executeTask(new getUsers(offset, usersPerPage), "load users");
	}
    
    class getUsers extends AsyncTask<String, Integer, JSONArray>
	{
		int offset = 0;
		int perPage = SexActivity.usersPerPage;
		
		public getUsers(int pOffset, int pPerPage)
		{
			super();
			offset = pOffset;
			perPage = pPerPage;
		}
		
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			loadUsersMarkers(result, offset, perPage);
			
			if(result != null && result.length()>0)
				showUsersMarkers(googleMap, SexActivity.loadedUsers);
			
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return httpGetUsers(offset, perPage);
		}
	}
    
    private void loadUsersMarkers(JSONArray result, final int offset, int perPage) 
	{
		if(result == null || result.length() == 0)
			Toast.makeText(getApplicationContext(), getString(R.string.nousers), Toast.LENGTH_LONG).show();
		else
		{
			int lastElement = Math.min(perPage, result.length());
			for(int i=0; i<lastElement; i++)
			{
				JSONObject user = null;
				try 
				{
					user = result.getJSONObject(i);
				}
				catch (JSONException e) 
				{
				}
				
				if(user != null)
				{
					SexActivity.loadedUsers.add(user);
				}
			}
		}
	}
    
    public JSONArray httpGetUsers(int offset, int perPage)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        
	        nameValuePairs.add(new BasicNameValuePair("method", "get_users"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("per_page", String.valueOf(perPage)));//SexActivity.usersPerPage)));//usersMapFilterPerPage
	        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(offset)));
	        nameValuePairs.add(new BasicNameValuePair("metric", SexActivity.me.metric));
	        
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
	
    
    private void showUsersMarkers(GoogleMap googleMap,ArrayList<JSONObject> loadedUsers) 
    {
    	boolean zoomed = false;
		for(int i=0; i<loadedUsers.size(); i++)
		{
			JSONObject user = loadedUsers.get(i);
			if(user != null)
			{
				try 
				{ 
					BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
					
					if(user.getInt("gender") == SexActivity.attributesGenderMan)
			  		{
						icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_man);
			  		}
			  		else if(user.getInt("gender") == SexActivity.attributesGenderWoman)
			  		{
			  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_woman);
			  		}	
			  		else if(user.getInt("gender") == SexActivity.attributesGenderPairStraight)
			  		{
			  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_straight);
			  		}
			  		else if(user.getInt("gender") == SexActivity.attributesGenderPairLesbian)
			  		{
			  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_lesbian);
			  		}
			  		else if(user.getInt("gender") == SexActivity.attributesGenderPairGay)
			  		{
			  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_gay);
			  		}
					
					if(user.getInt("hide_on_map") == 0)
					{
						googleMap.addMarker(new MarkerOptions()
							.position(new LatLng(user.getDouble("latitude"), user.getDouble("longitude")))
						    .title(user.getString("nickname"))
						    .icon(icon));
						
						if(zoomed == false)
						{
							zoomed = true;
							double latitude = user.getDouble("latitude");
							double longitude = user.getDouble("longitude");
					        LatLng latLng = new LatLng(latitude, longitude);
					        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
					        googleMap.animateCamera(CameraUpdateFactory.zoomTo(2));

						}
					}
					else
					{
						googleMap.addMarker(new MarkerOptions()
						.position(new LatLng(user.getDouble("latitude"), user.getDouble("longitude")))
					    .title(user.getString("nickname"))
					    .icon(icon).visible(false));
					}
					
				}
				catch (JSONException e) 
				{
				}
			}   
		}
		googleMap.setOnInfoWindowClickListener(this);
	}
    
    @Override
	public void onInfoWindowClick(Marker marker) 
    {
    	Bundle extras = new Bundle();
		extras.putString("user_index", String.valueOf(marker.getId()));
		
		Intent userDetail = new Intent(getBaseContext(), UserDetailActivity.class);
		userDetail.putExtras(extras);
		startActivity(userDetail);
		
	}

	@Override
    public void onLocationChanged(Location location) 
	{
		if(animate == true)
		{
			animate = false;
			// TextView tvLocation = (TextView) findViewById(R.id.tv_location);
			
	        // Getting latitude of the current location
	        double latitude = location.getLatitude();
	 
	        // Getting longitude of the current location
	        double longitude = location.getLongitude();
	 
	        // Creating a LatLng object for the current location
	        LatLng latLng = new LatLng(latitude, longitude);
	 
	        // Showing the current location in Google Map
	        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
	 
	        // Zoom in the Google Map
	        googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
		}
        // Setting latitude and longitude in the TextView tv_location
        //tvLocation.setText("Latitude:" +  latitude  + ", Longitude:"+ longitude );
 
    }
 
    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void showLoadingDialog()
	{
		if(loadingDialog == null || loadingDialog.isShowing() == false)
		{
			loadingDialog = new Dialog(this);
			loadingDialog.setContentView(R.layout.dialog_loading);
			loadingDialog.setTitle(getString(R.string.dialog_loading_title));
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
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			hideLoadingDialog();
			proceedMap();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
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
					SexActivity.me = SexActivity.getUserFromJsonObj(jUser);
					
					String country = getResources().getConfiguration().locale.getISO3Country();
					SexActivity.me.metric = "metric";
			       	if(country.equalsIgnoreCase("USA") == true || country.equalsIgnoreCase("GBR") == true || country.equalsIgnoreCase("UK") == true)
			       		SexActivity.me.metric = "imperial";

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