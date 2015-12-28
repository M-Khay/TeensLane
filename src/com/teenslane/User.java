package com.teenslane;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class User 
{

	public int id = 0;
	public String session = "";
	public String nickname = "";
	public int gender = 0;
	public int iwant = 0;
	public String image = "";
	public String cover_image = "";
	public String metric = "metric";
	public String dob = "";
	
	public String location_country = "";
	public String location_region = "";
	public String location_city = "";
	
	public int credits = 0;
	public int hide_on_map = 0;
	public int hide_on_search = 0;
	public int terms_agreed = 0;

	public int premium = 0;
	
	public String describe = "";
	public int body = 0;
	public int ethnicity = 0;
	public String height;
	public String weight;
	public String eye_color;
	public String hair_color;
	
	public int loves = 0;
	public ArrayList<String> images = new ArrayList<String>();
	public ArrayList<Boolean> images_loaded = new ArrayList<Boolean>();
	public ArrayList<String> images_passwords = new ArrayList<String>();
	public ArrayList<JSONObject> images_pure = new ArrayList<JSONObject>();
	
	public Map<Integer, Boolean> features_active = new HashMap<Integer, Boolean>();
	public Map<Integer, String> features_expiration = new HashMap<Integer, String>();
	
	public int age = 0;
	public int dob_day = 0;
	public int dob_month = 0;
	public int dob_year = 0;
	
	public int maxGalleryImages = 14;
	
	public Double latitude = null;
	public Double longitude = null;
	public Double distance = null;
	public int mDistance = 0;
	public String distanceText = "";
	
	public JSONObject search = null;
	
	public int blockedToMe = 0;
	public int myPet = 0;
		
	public User(int pId, String pSession, String pNickname, int pGender, int pIwant, String pImage, String pCoverImage, String pDob, String pCountry, int pCredits, int pPremium, int pHideOnMap, int pHideOnSearch, int pTermsAgreed, JSONObject otherData) throws JSONException
	{
		id = pId;
		session = pSession;
		nickname = pNickname;
		gender = pGender;
		iwant = pIwant;
		image = pImage;
		cover_image = pCoverImage;
		dob = pDob; 
		location_country = pCountry;
		credits = pCredits;
		premium = pPremium;
		hide_on_map = pHideOnMap;
		hide_on_search = pHideOnSearch;
		terms_agreed = pTermsAgreed;

		location_region = otherData.getString("location_region");
		location_city = otherData.getString("location_city");
		
		if(dob != null && dob.equalsIgnoreCase("") == false)
			setUserDob();

		setUserData(otherData.getString("describe"), otherData.getInt("body"), otherData.getInt("ethnicity"), otherData.getInt("loves"), otherData.getJSONArray("images"));
		
		if (otherData.optJSONArray("features") != null) 
			setUserFeatures(otherData.getJSONArray("features"));
		
		search = otherData.optJSONObject("search");
	}
	
	public void setUserFeatures(JSONArray features) {
		features_active.clear();
		features_expiration.clear();
		
		for(int i = 0; i < features.length(); i++) {
			try {
				JSONObject feature = features.getJSONObject(i);
	        	
	        	int featureId = feature.getInt("id");
	        	boolean active = feature.getInt("active") != 0;
	        	long expires = feature.getLong("expires");
	        	
	        	Date date = new Date((long)expires*1000);
	        	DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);  
	        		        		        	
	    		features_active.put(featureId, active);    
	    		features_expiration.put(featureId, formatter.format(date)); 
			} catch (JSONException e) {
			}
	    }		    		
	}
	
	public boolean isFeatureActive(int featureId) {
		if (features_active.containsKey(featureId))
			return features_active.get(featureId);
		
		return false;
	}
	
	public String getFeatureExpiration(int featureId) {
		if (features_expiration.containsKey(featureId))
			return features_expiration.get(featureId);
		
		return "";
	}
	
	public void setUserData(String pDescribe, int pBody, int pEthnicity, int pLoves, JSONArray pImages)
	{
		describe = pDescribe;
		body = pBody;
		ethnicity = pEthnicity;
		loves = pLoves;
		
		if(pImages.length() > 0)
		{ 
			for(int i=0; i<pImages.length(); i++)
			{
				try 
				{
					JSONObject tempData;
					tempData = pImages.getJSONObject(i);
					images.add(tempData.getString("image"));
					images_loaded.add(false);
					images_passwords.add(tempData.getString("password"));
					images_pure.add(tempData);
				}
				catch (JSONException e1) 
				{
				}
			}
		}
	}
	
	public User()
	{
		
	}
	
	public void setUserDob()
	{
		String[] dobParts = dob.split("[.]");
		
		dob_day = Integer.valueOf(dobParts[0]);
		dob_month = Integer.valueOf(dobParts[1]);
		dob_year = Integer.valueOf(dobParts[2]);
		
		Calendar today = Calendar.getInstance();
		
		Integer todayDay = today.get(Calendar.DAY_OF_MONTH);
		Integer todayMonth = today.get(Calendar.MONTH)+1;
		Integer todayYear = today.get(Calendar.YEAR);
		
		if(todayYear > dob_year)
		{
			age = todayYear - dob_year;

			if(todayMonth < dob_month)
			{
				age--;
			}
			else if(todayMonth == dob_month)
			{
				//check days
				if(todayDay < dob_day)
					age--;
				
			} 
		}

		// Log.e("com.teenslane.user.age", "age: " + age);
	}
}
