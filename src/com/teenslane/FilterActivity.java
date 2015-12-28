
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
import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FilterActivity extends SexActivity 
{
	int distanceDefaultValue = 50;
	int distanceMaxValue = 1000;
	int distanceStepValue = 10;
	int distanceMinValue = 10;
	
	int ageDefaultValue = 13;
	int ageMinValue = 13;
	int ageMaxValue = 19;
	int ageStepValue = 1;
	
	int progressFromValue = ageDefaultValue;
	int progressToValue = progressFromValue + ageStepValue;
	
	boolean lookingForManChecked = false;
  	boolean lookingForWomanChecked = false; 
  	boolean lookingForPairStraightChecked = false;
  	boolean lookingForPairLesbianChecked = false;
  	boolean lookingForPairGayChecked = false;
  	
  	boolean bodySexy = false;
  	boolean bodySporty = false;
  	boolean bodySlender = false;
  	boolean bodyJuicy = false;
  	boolean bodyMuscle = false;
	
  	boolean ethnicityAsian = false;
  	boolean ethnicityBlack = false;
  	boolean ethnicityOther = false;
  	boolean ethnicityLatino = false;
  	boolean ethnicityWhite = false;
  			
  	JSONArray countryListArray = new JSONArray();
  	JSONArray stateListArray = new JSONArray();
  	JSONArray citiesListArray = new JSONArray();
  	
  	int loves = 0;
  	
  	Button filter_lookingfor_input;
	Button filter_age_input;
	Button filter_country_input;
	Button filter_state_input;
	Button filter_city_input;
	Button filter_loves_input;
	Button filter_photo_input;
	RadioButton filter_photo_input_radio;
	boolean filter_photo = false;
	Button filter_pets_input;
	RadioButton filter_pets_input_radio;
	boolean filter_pets = false;
	Button filter_looks_body_input;
	Button filter_looks_ethnicity_input;
	SeekBar distance_meter;
	TextView seekBarValue;
	
	ImageView search_boys = null;
	ImageView search_girls = null;
	
	Boolean search_boys_value = false;
	Boolean search_girls_value = false;
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filter);
		
		search_boys = (ImageView) findViewById(R.id.search_boys);
		search_girls = (ImageView) findViewById(R.id.search_girls);
		
		search_boys.setAlpha(125);
		search_girls.setAlpha(125);
		
		SexActivity.executeTask(new getUserData(), "load user date");
	}
	
	public void proceedFilter()
	{
		filter_lookingfor_input = (Button) findViewById(R.id.filter_lookingfor_input);
		filter_age_input = (Button) findViewById(R.id.filter_age_input);
		filter_country_input = (Button) findViewById(R.id.filter_country_input);
		filter_state_input = (Button) findViewById(R.id.filter_state_input);
		filter_city_input = (Button) findViewById(R.id.filter_city_input);
		filter_loves_input = (Button) findViewById(R.id.filter_loves_input);
		filter_photo_input = (Button) findViewById(R.id.filter_photo_input);
		filter_photo_input_radio = (RadioButton) findViewById(R.id.filter_photo_input_radio);
		filter_pets_input = (Button) findViewById(R.id.filter_pets_input);
		filter_pets_input_radio = (RadioButton) findViewById(R.id.filter_pets_input_radio);
		filter_looks_body_input = (Button) findViewById(R.id.filter_looks_body_input);
		filter_looks_ethnicity_input = (Button) findViewById(R.id.filter_looks_ethnicity_input);
		distance_meter = (SeekBar) findViewById(R.id.distance_meter);
		distance_meter.incrementProgressBy(distanceStepValue);
		distance_meter.setMax(distanceMaxValue);
		seekBarValue = (TextView) findViewById(R.id.filter_distance_value);
		
		filter_state_input.setVisibility(Button.GONE);
		filter_city_input.setVisibility(Button.GONE);
		
		setupSearchPrefs();
		setupButtons();
	}
	
	@SuppressWarnings("deprecation")
	public void setupSearchPrefs()
	{
		try 
		{
			//lookingfor
			if(SexActivity.me.search == null || SexActivity.me.search.opt("looking_for") == null || SexActivity.me.search.getInt("looking_for") == 0)
			{
				if(SexActivity.me.iwant == SexActivity.attributesGenderMan)
				{
					search_boys.setAlpha(255);
					search_boys_value = true;
				}
				else if(SexActivity.me.iwant == SexActivity.attributesGenderWoman)
				{
					search_girls.setAlpha(255);
					search_girls_value = true;
				}
					
			}
			else
			{
				int value = SexActivity.me.search.getInt("looking_for");
		  	
		  		if(value != 0)
		  		{
		  			if((SexActivity.attributesGenderMan & value) != 0)
		  			{
		  				search_boys.setAlpha(255);
		  				search_boys_value = true;
		  			}
		  			
		  			if((SexActivity.attributesGenderWoman & value) != 0)
		  			{
		  				search_girls.setAlpha(255);
		  				search_girls_value = true;
		  			}
		  		}
			}
		  		
		  	//country
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("country_name") != null && SexActivity.me.search.getString("country_name").equals("") == false)
	  		{
	  			filter_country_input.setText(SexActivity.me.search.getString("country_name"));
	  			
	  			distance_meter.setProgress(0);
				if(me.metric.equals("metric") == true)
					seekBarValue.setText("0 " + getString(R.string.metric_metric_distance));
				else
					seekBarValue.setText("0 " + getString(R.string.metric_imperial_distance));
	  			
	  			//state
		  		if(SexActivity.me.search.opt("country_states") != null && SexActivity.me.search.getInt("country_states")>0)
		  		{
		  			filter_state_input.setVisibility(Button.VISIBLE);
		  			
		  			if(SexActivity.me.search.opt("state_name") != null && SexActivity.me.search.getString("state_name").equals("") == false)
		  				filter_state_input.setText(SexActivity.me.search.getString("state_name"));

		  			if(SexActivity.me.search.opt("state_cities") != null && SexActivity.me.search.getInt("state_cities")>0)
		  			{
		  				filter_city_input.setVisibility(Button.VISIBLE);
		  				if(SexActivity.me.search.opt("city_name") != null && SexActivity.me.search.getString("city_name").equals("") == false)
		  					filter_city_input.setText(SexActivity.me.search.getString("city_name"));
		  				
		  			}
		  		}
		  		else
		  		{
		  			if(SexActivity.me.search.opt("country_cities") != null && SexActivity.me.search.getInt("country_cities")==1)
		  			{
		  				filter_city_input.setVisibility(Button.VISIBLE);
		  				if(SexActivity.me.search.opt("city_name") != null && SexActivity.me.search.getString("city_name").equals("") == false)
		  					filter_city_input.setText(SexActivity.me.search.getString("city_name"));
		  				
		  			}
		  		}
	  		}
	  		else
	  		{
	  			//distance
		  		if(SexActivity.me.search != null && SexActivity.me.search.opt("distance") != null && SexActivity.me.search.getInt("distance")>0)
				{
		  			distanceDefaultValue = SexActivity.me.search.getInt("distance");
		  			distance_meter.setProgress(distanceDefaultValue);
		  			
		  			Log.d("metric2", me.metric);
		  			 
		  			if(me.metric.equals("metric") == true)
		  				seekBarValue.setText(String.valueOf(distanceDefaultValue) + " " + getString(R.string.metric_metric_distance));
		  			else
		  				seekBarValue.setText(String.valueOf(distanceDefaultValue) + " " + getString(R.string.metric_imperial_distance));
				}
	  		}
	  		
	  		//loves
	  		/*
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("loves") != null && SexActivity.me.search.getInt("loves") != 0)
	  		{
	  			int value = SexActivity.me.search.getInt("loves");
		  		String inputValue = "";
		  		if(value != 0)
		  		{
		  			if((SexActivity.attributesLovesMissionary & value) != 0)
					{
		  				loves += SexActivity.attributesLovesMissionary;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_missionary);
					}
					
		  			if((SexActivity.attributesLoves69 & value) != 0)
					{
		  				loves += SexActivity.attributesLoves69;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_69);
					}
		  			
		  			if((SexActivity.attributesLovesDoggie & value) != 0)
					{
		  				loves += SexActivity.attributesLovesDoggie;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_doggie);
					}
		  			
		  			if((SexActivity.attributesLovesBJ & value) != 0)
					{
		  				loves += SexActivity.attributesLovesBJ;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_bj);
					}
		  			
		  			if((SexActivity.attributesLovesPiss & value) != 0)
					{
		  				loves += SexActivity.attributesLovesPiss;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_piss);
					}
		  			
		  			if((SexActivity.attributesLovesSM & value) != 0)
					{
		  				loves += SexActivity.attributesLovesSM;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_sm);
					}
		  			
		  			if((SexActivity.attributesLovesOrgy & value) != 0)
					{
		  				loves += SexActivity.attributesLovesOrgy;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_orgy);
					}
		  			
		  			if((SexActivity.attributesLovesFetish & value) != 0)
					{
		  				loves += SexActivity.attributesLovesFetish;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_fetish);
					}
		  			
		  			if((SexActivity.attributesLovesHJ & value) != 0)
					{
		  				loves += SexActivity.attributesLovesHJ;
						inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_hj);
					}
			  		
		  		}
		  		
		  		if(inputValue.equals("") == false)
		  		{
		  			filter_loves_input.setText(inputValue);
		  		}
	  		}
	  		*/
	  		
	  		//photo
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("photo") != null)
	  		{
	  			if(SexActivity.me.search.getInt("photo") == 1)
	  			{
	  				filter_photo = true;
	  				filter_photo_input_radio.setChecked(true);
	  			}
	  			else
	  			{
	  				filter_photo = false;
	  				filter_photo_input_radio.setChecked(false);
	  			}
	  		}
	  		
	  		//pets
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("pets") != null)
	  		{
	  			if(SexActivity.me.search.getInt("pets") == 1)
	  			{
	  				filter_pets = true;
	  				filter_pets_input_radio.setChecked(true);
	  			}
	  			else
	  			{
	  				filter_pets = false;
	  				filter_pets_input_radio.setChecked(false);
	  			}
	  		}
	  		
	  		//body
	  		String newValueBody = "";
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("body") != null && SexActivity.me.search.getInt("body") != 0)
			{
				int value = SexActivity.me.search.getInt("body");
		  	
		  		if(value != 0)
		  		{
		  			if((SexActivity.attributesBodySexy & value) != 0)
		  				newValueBody += ((newValueBody.equals("")==true) ? "" : ", ") + getString(R.string.body_sexy);
		  			
		  			if((SexActivity.attributesBodySporty & value) != 0)
		  				newValueBody += ((newValueBody.equals("")==true) ? "" : ", ") + getString(R.string.body_sporty);
		  			
		  			if((SexActivity.attributesBodySlender & value) != 0)
		  				newValueBody += ((newValueBody.equals("")==true) ? "" : ", ") + getString(R.string.body_slender);
		  			
		  			if((SexActivity.attributesBodyJuicy & value) != 0)
		  				newValueBody += ((newValueBody.equals("")==true) ? "" : ", ") + getString(R.string.body_juicy);
		  			
		  			if((SexActivity.attributesBodyMuscular & value) != 0)
		  				newValueBody += ((newValueBody.equals("")==true) ? "" : ", ") + getString(R.string.body_muscle);
			  		
		  		}
		  		
		  		if(newValueBody.equals("") == false)
		  			filter_looks_body_input.setText(newValueBody);
		  		
			}
	  		
	  		//ethnicity
	  		String newValueEthnicity = "";
	  		if(SexActivity.me.search != null && SexActivity.me.search.opt("ethnicity") != null && SexActivity.me.search.getInt("ethnicity") != 0)
			{
				int value = SexActivity.me.search.getInt("ethnicity");
		  	
		  		if(value != 0)
		  		{
		  			if((SexActivity.attributesEthnicityAsian & value) != 0)
		  				newValueEthnicity += ((newValueEthnicity.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_asian);

		  			if((SexActivity.attributesEthnicityBlack & value) != 0)
		  				newValueEthnicity += ((newValueEthnicity.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_black);
		  			
		  			if((SexActivity.attributesEthnicityOther & value) != 0)
		  				newValueEthnicity += ((newValueEthnicity.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_other);
		  			
		  			if((SexActivity.attributesEthnicityLatino & value) != 0)
		  				newValueEthnicity += ((newValueEthnicity.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_latino);
		  			
		  			if((SexActivity.attributesEthnicityWhite & value) != 0)
		  				newValueEthnicity += ((newValueEthnicity.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_white);
		  			 
		  		}
		  		
		  		if(newValueEthnicity.equals("") == false)
		  			filter_looks_ethnicity_input.setText(newValueEthnicity);
		  		
			}
		}
		catch (JSONException e) 
		{
		}
	}
	
	public void setupButtons()
	{
		search_boys.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) 
			{
				if(search_boys_value == true)
				{
					search_boys_value = false;
					search_boys.setAlpha(125);
				}
				else
				{
					search_boys_value = true;
					search_boys.setAlpha(255);
				}
				
				save_looking_for();
			}
		});
		
		search_girls.setOnClickListener(new View.OnClickListener()
		{
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) 
			{
				if(search_girls_value == true)
				{
					search_girls_value = false;
					search_girls.setAlpha(125);
				}
				else
				{
					search_girls_value = true;
					search_girls.setAlpha(255);
				}
				
				save_looking_for();
			}
		});
		
		//looking for
		filter_lookingfor_input.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawLookingForDialog();
			}
		});
		
		//age
		filter_age_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				drawAgeDialog();
			}
		});
		
		distance_meter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
		    {
		        progress = progress / distanceStepValue;
		        progress = progress * distanceStepValue;
		        progress += distanceMinValue;
		        
		        Log.d("metric", me.metric);
		        
		        if(me.metric.equals("metric") == true)
		        	seekBarValue.setText(String.valueOf(progress) + " " + getString(R.string.metric_metric_distance));
				else
					seekBarValue.setText(String.valueOf(progress) + " " + getString(R.string.metric_imperial_distance));
		        
		        //save data
				SexActivity.executeTask(new saveUserSearchStringTask("distance", String.valueOf(progress)), "save filter distance");
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) 
		    {
		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) 
		    {
		    	//save country
				SexActivity.executeTask(new saveUserSearchStringTask("country", "0"), "save filter country");
				SexActivity.executeTask(new saveUserSearchStringTask("state", "0"), "save filter state");
				SexActivity.executeTask(new saveUserSearchStringTask("city", "0"), "save filter city");
								
				filter_country_input.setText(getString(R.string.filter_distance_mylocation));
				filter_state_input.setText(getString(R.string.filter_distance_state));
				filter_city_input.setText(getString(R.string.filter_distance_city));
				
				filter_state_input.setVisibility(Button.GONE);
				filter_city_input.setVisibility(Button.GONE);
		    }
		});
		
		
		//country
		filter_country_input.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawCountryDialog();
			}
		});
		
		//state
		filter_state_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try 
				{
					if(SexActivity.me.search != null && SexActivity.me.search.opt("country") != null && SexActivity.me.search.getInt("country")>0)
						drawStatesDialog(SexActivity.me.search.getInt("country"));
				}
				catch (JSONException e) 
				{
				}
			}
		});
		
		//city
		filter_city_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				try 
				{
					if
					(
						SexActivity.me.search != null && 
						SexActivity.me.search.opt("country") != null && SexActivity.me.search.getInt("country")>0 &&
						SexActivity.me.search.opt("country_cities") != null && SexActivity.me.search.getInt("country_cities")==1
					)
					{
						drawCitiesDialog(0, SexActivity.me.search.getInt("country"));
					}
					else if
					(
						SexActivity.me.search != null && 
						SexActivity.me.search.opt("state") != null && SexActivity.me.search.getInt("state")>0 &&
						SexActivity.me.search.opt("state_cities") != null && SexActivity.me.search.getInt("state_cities")>0
					)
					{
						drawCitiesDialog(SexActivity.me.search.getInt("state"), SexActivity.me.search.getInt("country"));
					}
				}
				catch (JSONException e) 
				{
				}
			}
		});
		
		//loves
		filter_loves_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				drawLovesDialog();
			}
		});
		
		//photo
		filter_photo_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(filter_photo == false)
				{
					filter_photo = true;
					filter_photo_input_radio.setChecked(true);
				}
				else
				{
					filter_photo = false;
					filter_photo_input_radio.setChecked(false);
				}
				
				int checked = (filter_photo==true) ? 1 : 0;				
				SexActivity.executeTask(new saveUserSearchStringTask("photo", String.valueOf(checked)), "save filter photo");				
			}
		});
		filter_photo_input_radio.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(filter_photo == false)
				{
					filter_photo = true;
					filter_photo_input_radio.setChecked(true);
				}
				else
				{
					filter_photo = false;
					filter_photo_input_radio.setChecked(false);
				}
				
				int checked = (filter_photo==true) ? 1 : 0;
				SexActivity.executeTask(new saveUserSearchStringTask("photo", String.valueOf(checked)), "save filter photo");
			}
		});
		
		//pets
		filter_pets_input.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(filter_pets == false)
				{
					filter_pets = true;
					filter_pets_input_radio.setChecked(true);
				}
				else
				{
					filter_pets = false;
					filter_pets_input_radio.setChecked(false);
				}
				
				int checked = (filter_pets==true) ? 1 : 0;
				SexActivity.executeTask(new saveUserSearchStringTask("pets", String.valueOf(checked)), "save filter pets");
			}
		});
		filter_pets_input_radio.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				if(filter_pets == false)
				{
					filter_pets = true;
					filter_pets_input_radio.setChecked(true);
				}
				else
				{
					filter_pets = false;
					filter_pets_input_radio.setChecked(false);
				}
				
				int checked = (filter_pets==true) ? 1 : 0;

				SexActivity.executeTask(new saveUserSearchStringTask("pets", String.valueOf(checked)), "save filter pets");
			}
		});
		
		
		//body
		filter_looks_body_input.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawBodyDialog();
			}
		});

		//ethnicity
		filter_looks_ethnicity_input.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawEthnicityDialog();
			}
		});
		
		TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.filter_title));
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setVisibility(ImageView.GONE);
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_detail_save);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SexActivity.loadedUsers = new ArrayList<JSONObject>();
				FilterActivity.this.finish();
				
				Bundle extras = getIntent().getExtras();
				if(extras != null && extras.isEmpty() == false)
				{
					if(extras.containsKey("jump_to_users"))
					{
						Intent usersIntent = new Intent(getBaseContext(), UsersActivity.class);
						startActivity(usersIntent);
					}
					else if(extras.containsKey("jump_to_map"))
					{
						Intent mapIntent = new Intent(getBaseContext(), MapActivity.class);
						Bundle mapExtras = new Bundle();
						
						mapExtras.putString("load_users", "1");
						mapIntent.putExtras(mapExtras);
						
						startActivity(mapIntent);
					}
				}
			}
		});
	}
	
	public void save_looking_for()
	{
		int lookingForValue = 0;
		if(search_boys_value == true)
			lookingForValue += SexActivity.attributesGenderMan;
		
		if(search_girls_value == true)
			lookingForValue += SexActivity.attributesGenderWoman;
		
		SexActivity.executeTask(new saveUserSearchStringTask("looking_for", String.valueOf(lookingForValue)), "save filter lookingfor");
	}
	
	public void drawCountryDialog()
	{
		showLoadingDialog();

		SexActivity.executeTask(new getCountries(), "get country list");
	}
	
	public void drawStatesDialog(int pCountry)
	{
		showLoadingDialog();

		SexActivity.executeTask(new getStates(pCountry), "get states list");
	}
	
	public void drawCitiesDialog(int pState, int pCountry)
	{
		showLoadingDialog();

		SexActivity.executeTask(new getCities(pState, pCountry), "get cities list");
	}
	
	public void setupCitiesDialog(JSONArray jList)
	{
		citiesListArray = jList;
		
		final Dialog cityDialog = new Dialog(this);
		cityDialog.setContentView(R.layout.dialog_cities);
		cityDialog.setTitle(getString(R.string.filter_distance_city));
		
		//drop down
		int selected = 0;
		List<String> list = new ArrayList<String>();
		if(jList != null && jList.length()>0)
		{
			for(int i=0; i<jList.length(); i++)
			{
				try 
				{
					JSONObject jTemp = jList.getJSONObject(i);
					if(jTemp != null && jTemp.opt("name") != null && jTemp.getString("name").equals("") == false)
					{
						list.add(jTemp.getString("name"));
						
						if(SexActivity.me.search != null && SexActivity.me.search.opt("city_name") != null && SexActivity.me.search.getString("city_name").equals(jTemp.getString("name")) == true)
							selected = i;
						
					}
				}
				catch (JSONException e) 
				{
				}
			}
		}
		
		final Spinner cityList = (Spinner) cityDialog.findViewById(R.id.city_input);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cityList.setAdapter(dataAdapter);
		cityList.setSelection(selected);
		
		Button cityConfirm = (Button) cityDialog.findViewById(R.id.city_confirm);
		cityConfirm.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				String cityName = getString(R.string.filter_distance_city);
				int cityIndex = cityList.getSelectedItemPosition();
				try 
				{
					JSONObject jTemp = citiesListArray.getJSONObject(cityIndex);
					if(jTemp != null)
					{
						cityName = jTemp.getString("name");
						cityIndex = jTemp.getInt("id");
					}
				
				}
				catch (JSONException e) 
				{
				}
				
				//set city name field
				filter_city_input.setText(cityName);
				
				//save city
				SexActivity.executeTask(new saveUserSearchStringTask("city", String.valueOf(cityIndex)), "save filter city");
				
				//show city
				try
				{
					if(isFinishing() == false)
						cityDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}
		});
		
		try
		{
			if(isFinishing() == false)
				cityDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void setupStatesDialog(JSONArray jList)
	{
		stateListArray = jList;
		
		final Dialog stateDialog = new Dialog(this);
		stateDialog.setContentView(R.layout.dialog_states);
		stateDialog.setTitle(getString(R.string.filter_distance_state));
		
		//drop down
		int selected = 0;
		List<String> list = new ArrayList<String>();
		if(jList != null && jList.length()>0)
		{
			for(int i=0; i<jList.length(); i++)
			{
				try 
				{
					JSONObject jTemp = jList.getJSONObject(i);
					if(jTemp != null && jTemp.opt("name") != null && jTemp.getString("name").equals("") == false)
					{
						list.add(jTemp.getString("name"));
						
						if(SexActivity.me.search != null && SexActivity.me.search.opt("state_name") != null && SexActivity.me.search.getString("state_name").equals(jTemp.getString("name")) == true)
							selected = i;
						
					}
				}
				catch (JSONException e) 
				{
				}
			}
		}
		
		final Spinner stateList = (Spinner) stateDialog.findViewById(R.id.state_input);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stateList.setAdapter(dataAdapter);
		stateList.setSelection(selected);
		
		Button stateConfirm = (Button) stateDialog.findViewById(R.id.state_confirm);
		stateConfirm.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				String stateName = getString(R.string.filter_distance_state);
				int stateIndex = stateList.getSelectedItemPosition();
				boolean gotCities = false;
				try 
				{
					JSONObject jTemp = stateListArray.getJSONObject(stateIndex);
					if(jTemp != null)
					{
						stateName = jTemp.getString("name");
						stateIndex = jTemp.getInt("id"); 
						if(jTemp.getInt("cities")>0)
							gotCities = true;
					}
				
				}
				catch (JSONException e) 
				{
				}
				
				
				filter_city_input.setText(getString(R.string.filter_distance_city));
				if(gotCities == true)
					filter_city_input.setVisibility(Button.VISIBLE);
				else
					filter_city_input.setVisibility(Button.GONE);
				
				
				//set state name field
				filter_state_input.setText(stateName);
				
				//save state
				SexActivity.executeTask(new saveUserSearchStringTask("state", String.valueOf(stateIndex)), "save filter state");
				SexActivity.executeTask(new saveUserSearchStringTask("city", "0"), "save filter city");
								
				//show state
				try
				{
					if(isFinishing() == false)
						stateDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}
		});
		
		try
		{
			if(isFinishing() == false)
				stateDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void setupCountryDialog(JSONArray jList)
	{
		countryListArray = jList;
		
		final Dialog countryDialog = new Dialog(this);
		countryDialog.setContentView(R.layout.dialog_countries);
		countryDialog.setTitle(getString(R.string.filter_distance_country));
		
		//drop down
		int selected = 0;
		List<String> list = new ArrayList<String>();
		list.add(getString(R.string.filter_distance_mylocation));
		if(jList != null && jList.length()>0)
		{
			for(int i=0; i<jList.length(); i++)
			{
				try 
				{
					JSONObject jTemp = jList.getJSONObject(i);
					if(jTemp != null && jTemp.opt("name") != null && jTemp.getString("name").equals("") == false)
					{
						list.add(jTemp.getString("name"));
						
						if(SexActivity.me.search != null && SexActivity.me.search.opt("country_name") != null && SexActivity.me.search.getString("country_name").equals(jTemp.getString("name")) == true)
							selected = i+1;
						
					}
				}
				catch (JSONException e) 
				{
				}
			}
		}
		
		final Spinner countryList = (Spinner) countryDialog.findViewById(R.id.country_input);
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		countryList.setAdapter(dataAdapter);
		countryList.setSelection(selected);
		
		Button countryConfirm = (Button) countryDialog.findViewById(R.id.country_confirm);
		countryConfirm.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				String countryName = getString(R.string.filter_distance_mylocation);
				int countryIndex = countryList.getSelectedItemPosition();
				boolean gotStates = false;
				boolean gotCities = false;
				if(countryIndex>0)
				{
					countryIndex--;
					try 
					{
						JSONObject jTemp = countryListArray.getJSONObject(countryIndex);
						if(jTemp != null)
						{
							countryName = jTemp.getString("name");
							countryIndex = jTemp.getInt("id");
							if(jTemp.getInt("states")>0)
								gotStates = true;
							else if(jTemp.getInt("cities")>0)
								gotCities = true;
							
						}
					
					}
					catch (JSONException e) 
					{
					}
				}
				
				filter_state_input.setText(getString(R.string.filter_distance_state));
				if(gotStates == true)
					filter_state_input.setVisibility(Button.VISIBLE);
				else
					filter_state_input.setVisibility(Button.GONE);
				
				
				filter_city_input.setText(getString(R.string.filter_distance_city));
				if(gotCities == true)
					filter_city_input.setVisibility(Button.VISIBLE);
				else
					filter_city_input.setVisibility(Button.GONE);
				
				//set country name field
				filter_country_input.setText(countryName);
				
				//save country
				SexActivity.executeTask(new saveUserSearchStringTask("country", String.valueOf(countryIndex)), "save filter country");
				SexActivity.executeTask(new saveUserSearchStringTask("state", "0"), "save filter state");
				SexActivity.executeTask(new saveUserSearchStringTask("city", "0"), "save filter city");
				SexActivity.executeTask(new saveUserSearchStringTask("distance", "0"), "save filter distance"); 
				
				distance_meter.setProgress(0);
				if(me.metric.equals("metric") == true)
					seekBarValue.setText("0 " + getString(R.string.metric_metric_distance));
				else
					seekBarValue.setText("0 " + getString(R.string.metric_imperial_distance));
				
				//show country
				try
				{
					if(isFinishing() == false)
						countryDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}
		});
		
		try
		{
			if(isFinishing() == false)
				countryDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	
	public int loveSelected(ArrayList<String> values, String value)
	{
		if(values != null && values.size()>0)
		{
			for(int i=0; i<values.size(); i++)
			{
				if(value.equalsIgnoreCase(values.get(i)) == true)
					return i;
				
			}
			return 100;
		}
		else
			return 100;
	}
	
	public void drawLovesDialog()
	{
		final Dialog lovesDialog = new Dialog(this);
		lovesDialog.setContentView(R.layout.dialog_loves);
		lovesDialog.setTitle(getString(R.string.filter_loves_input));

		final Button loves_missionary = (Button) lovesDialog.findViewById(R.id.loves_missionary);
		if((SexActivity.attributesLovesMissionary & loves) != 0)
		{
			loves_missionary.setBackgroundResource(R.drawable.loves_missionary_hover);
		}
		loves_missionary.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesMissionary & loves) == 0)
				{
					loves += SexActivity.attributesLovesMissionary;
					loves_missionary.setBackgroundResource(R.drawable.loves_missionary_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesMissionary;
					loves_missionary.setBackgroundResource(R.drawable.loves_missionary);
				}
			}
		});
		
		final Button loves_69 = (Button) lovesDialog.findViewById(R.id.loves_69);
		if((SexActivity.attributesLoves69 & loves) != 0)
		{
			loves_69.setBackgroundResource(R.drawable.loves_69_hover);
		}
		loves_69.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLoves69 & loves) == 0)
				{
					loves += SexActivity.attributesLoves69;
					loves_69.setBackgroundResource(R.drawable.loves_69_hover);
				}
				else
				{
					loves -= SexActivity.attributesLoves69;
					loves_69.setBackgroundResource(R.drawable.loves_69);
				}
			}
		});
		
		final Button loves_doggie = (Button) lovesDialog.findViewById(R.id.loves_doggie);
		if((SexActivity.attributesLovesDoggie & loves) != 0)
		{
			loves_doggie.setBackgroundResource(R.drawable.loves_doggie_hover);
		}
		loves_doggie.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesDoggie & loves) == 0)
				{
					loves += SexActivity.attributesLovesDoggie;
					loves_doggie.setBackgroundResource(R.drawable.loves_doggie_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesDoggie;
					loves_doggie.setBackgroundResource(R.drawable.loves_doggie);
				}
			}
		});
		
		final Button loves_bj = (Button) lovesDialog.findViewById(R.id.loves_bj);
		if((SexActivity.attributesLovesBJ & loves) != 0)
		{
			loves_bj.setBackgroundResource(R.drawable.loves_bj_hover);
		}
		loves_bj.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesBJ & loves) == 0)
				{
					loves += SexActivity.attributesLovesBJ;
					loves_bj.setBackgroundResource(R.drawable.loves_bj_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesBJ;
					loves_bj.setBackgroundResource(R.drawable.loves_bj);
				}
			}
		});
		
		final Button loves_piss = (Button) lovesDialog.findViewById(R.id.loves_piss);
		if((SexActivity.attributesLovesPiss & loves) != 0)
		{
			loves_piss.setBackgroundResource(R.drawable.loves_piss_hover);
		}
		loves_piss.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesPiss & loves) == 0)
				{
					loves += SexActivity.attributesLovesPiss;
					loves_piss.setBackgroundResource(R.drawable.loves_piss_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesPiss;
					loves_piss.setBackgroundResource(R.drawable.loves_piss);
				}
			}
		});
		
		final Button loves_sm = (Button) lovesDialog.findViewById(R.id.loves_sm);
		if((SexActivity.attributesLovesSM & loves) != 0)
		{
			loves_sm.setBackgroundResource(R.drawable.loves_sm_hover);
		}
		loves_sm.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesSM & loves) == 0)
				{
					loves += SexActivity.attributesLovesSM;
					loves_sm.setBackgroundResource(R.drawable.loves_sm_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesSM;
					loves_sm.setBackgroundResource(R.drawable.loves_sm);
				}
			}
		});
		
		final Button loves_orgy = (Button) lovesDialog.findViewById(R.id.loves_orgy);
		if((SexActivity.attributesLovesOrgy & loves) != 0)
		{
			loves_orgy.setBackgroundResource(R.drawable.loves_orgy_hover);
		}
		loves_orgy.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesOrgy & loves) == 0)
				{
					loves += SexActivity.attributesLovesOrgy;
					loves_orgy.setBackgroundResource(R.drawable.loves_orgy_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesOrgy;
					loves_orgy.setBackgroundResource(R.drawable.loves_orgy);
				}
			}
		});
		
		final Button loves_fetish = (Button) lovesDialog.findViewById(R.id.loves_fetish);
		if((SexActivity.attributesLovesFetish & loves) != 0)
		{
			loves_fetish.setBackgroundResource(R.drawable.loves_fetish_hover);
		}
		loves_fetish.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesFetish & loves) == 0)
				{
					loves += SexActivity.attributesLovesFetish;
					loves_fetish.setBackgroundResource(R.drawable.loves_fetish_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesFetish;
					loves_fetish.setBackgroundResource(R.drawable.loves_fetish);
				}
			}
		});
		
		final Button loves_hj = (Button) lovesDialog.findViewById(R.id.loves_hj);
		if((SexActivity.attributesLovesHJ & loves) != 0)
		{
			loves_hj.setBackgroundResource(R.drawable.loves_hj_hover);
		}
		loves_hj.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				if((SexActivity.attributesLovesHJ & loves) == 0)
				{
					loves += SexActivity.attributesLovesHJ;
					loves_hj.setBackgroundResource(R.drawable.loves_hj_hover);
				}
				else
				{
					loves -= SexActivity.attributesLovesHJ;
					loves_hj.setBackgroundResource(R.drawable.loves_hj);
				}
			}
		});
		
		Button lovesConfirm = (Button) lovesDialog.findViewById(R.id.loves_confirm);
		lovesConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				setLovesValue(loves);
				SexActivity.executeTask(new saveUserSearchStringTask("loves", String.valueOf(loves)), "save filter loves");
				try
				{
					if(isFinishing() == false)
						lovesDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		});
		
		try
		{
			if(isFinishing() == false)
				lovesDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void setLovesValue(int value)
	{
		/*
		String inputValue = getString(R.string.filter_loves_input);
		if(value != 0)
		{
			inputValue = "";
			
			if((SexActivity.attributesLovesMissionary & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_missionary);
			}
			
			if((SexActivity.attributesLoves69 & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_69);
			}
			
			if((SexActivity.attributesLovesDoggie & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_doggie);
			}
			
			if((SexActivity.attributesLovesBJ & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_bj);
			}
			
			if((SexActivity.attributesLovesPiss & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_piss);
			}
			
			if((SexActivity.attributesLovesSM & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_sm);
			}
			
			if((SexActivity.attributesLovesOrgy & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_orgy);
			}
			
			if((SexActivity.attributesLovesFetish & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_fetish);
			}
			
			if((SexActivity.attributesLovesHJ & value) != 0)
			{
				inputValue += ((inputValue.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_hj);
			}
		}
		
		filter_loves_input.setText(inputValue);
		*/
	}
	
	public void drawAgeDialog()
	{
		final Dialog ageDialog = new Dialog(this);
		ageDialog.setContentView(R.layout.dialog_filter_age);
		ageDialog.setTitle(getString(R.string.filter_age));
		
		//setup sliders
		final TextView age_from = (TextView) ageDialog.findViewById(R.id.age_from);
		final TextView age_to = (TextView) ageDialog.findViewById(R.id.age_to);
		
		final SeekBar age_from_input = (SeekBar) ageDialog.findViewById(R.id.age_from_input);
		final SeekBar age_to_input = (SeekBar) ageDialog.findViewById(R.id.age_to_input);
		
		try 
		{
			if(SexActivity.me.search != null && SexActivity.me.search.opt("age_from") != null && SexActivity.me.search.opt("age_to") != null && SexActivity.me.search.getInt("age_from")>0 && SexActivity.me.search.getInt("age_to")>0)
			{
				progressFromValue = SexActivity.me.search.getInt("age_from");
				progressToValue = SexActivity.me.search.getInt("age_to");
				
				age_from.setText(getString(R.string.filter_age_from_input) + ": " + SexActivity.me.search.getString("age_from"));
				age_to.setText(getString(R.string.filter_age_to_input) + ": " + SexActivity.me.search.getString("age_to"));
				
				age_from_input.setProgress(SexActivity.me.search.getInt("age_from")-ageMinValue);
				age_to_input.setProgress(SexActivity.me.search.getInt("age_to")-ageMinValue);
			}
			else
			{
				age_from.setText(getString(R.string.filter_age_from_input) + ": " + String.valueOf(ageMinValue));
				age_to.setText(getString(R.string.filter_age_to_input) + ": " + String.valueOf(ageMinValue+ageStepValue));
				
				age_from_input.setProgress(ageDefaultValue/ageMinValue);
				age_to_input.setProgress(age_from_input.getProgress()+ageStepValue);
			}
			
			age_from_input.incrementProgressBy(ageStepValue);
			age_to_input.incrementProgressBy(ageStepValue);

			age_from_input.setMax(ageMaxValue);
			age_to_input.setMax(ageMaxValue+ageStepValue);
		} 
		catch (JSONException e) 
		{
		}
		
		age_from_input.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
		    {
		    	if(fromUser == true)
		    	{
		    		progressFromValue = progress / ageStepValue;
		    		progressFromValue = progressFromValue * ageStepValue;
		    		progressFromValue += ageMinValue;
			        age_from.setText(getString(R.string.filter_age_from_input) + ": " + String.valueOf(progressFromValue));
			        
			        if(age_to_input.getProgress()<=progress)
			        {
			        	progressToValue = progressFromValue+ageStepValue;
			        	age_to_input.setProgress(progress+ageStepValue);
			        	age_to.setText(getString(R.string.filter_age_to_input) + ": " + String.valueOf(progressToValue));
			        }
		    	}
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) 
		    {
		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) 
		    {
		    }
		});
		
		age_to_input.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) 
		    {
		    	if(fromUser == true)
		    	{
		    		progressToValue = progress / ageStepValue;
		    		progressToValue = progressToValue * ageStepValue;
		    		progressToValue += ageMinValue + ageStepValue;
		        	age_to.setText(getString(R.string.filter_age_to_input) + ": " + String.valueOf(progressToValue));
		        	
		        	if(age_from_input.getProgress()>=progress)
			        {
		        		progressFromValue = progressToValue-ageStepValue;
		        		age_from_input.setProgress(progress-ageStepValue);
			        	age_from.setText(getString(R.string.filter_age_to_input) + ": " + String.valueOf(progressFromValue));
			        }
		    	}
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) 
		    {
		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) 
		    {
		    }
		});


		Button ageConfirm = (Button) ageDialog.findViewById(R.id.age_confirm);
		ageConfirm.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				filter_age_input.setText(String.valueOf(progressFromValue) + "-" + String.valueOf(progressToValue));
				SexActivity.executeTask(new saveUserSearchStringTask("age_range", String.valueOf(progressFromValue) + "-" + String.valueOf(progressToValue)), "save filter age-range");
				try
				{
					if(isFinishing() == false)
						ageDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}
		});
		
		try
		{
			if(isFinishing() == false)
				ageDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void drawEthnicityDialog()
	{
		final Dialog ethnicityDialog = new Dialog(this);
		ethnicityDialog.setContentView(R.layout.dialog_filter_ethnicity);
		ethnicityDialog.setTitle(getString(R.string.filter_looks_ethnicity));
	  	
	  	final RadioButton ethnicityAsianRadio = (RadioButton) ethnicityDialog.findViewById(R.id.ethnicity_asian);
	  	final RadioButton ethnicityBlackRadio = (RadioButton) ethnicityDialog.findViewById(R.id.ethnicity_black);
	  	final RadioButton ethnicityOtherRadio = (RadioButton) ethnicityDialog.findViewById(R.id.ethnicity_other);
	  	final RadioButton ethnicityLatinoRadio = (RadioButton) ethnicityDialog.findViewById(R.id.ethnicity_latino);
	  	final RadioButton ethnicityWhiteRadio = (RadioButton) ethnicityDialog.findViewById(R.id.ethnicity_white);
	  	
	  	if(filter_looks_ethnicity_input.getText().toString().equals(getString(R.string.filter_looks_ethnicity)) == false)
	  	{
	  		String[] values = new String[10];
	  		values = filter_looks_ethnicity_input.getText().toString().split(",");
	  		
	  		if(values.length>0)
	  		{
		  		for(int i=0; i<values.length; i++)
		  		{
		  			if(values[i].trim().equals(getString(R.string.ethnicity_asian)) == true)
		  				ethnicityAsianRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.ethnicity_black)) == true)
		  				ethnicityBlackRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.ethnicity_other)) == true)
		  				ethnicityOtherRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.ethnicity_latino)) == true)
		  				ethnicityLatinoRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.ethnicity_white)) == true)
		  				ethnicityWhiteRadio.setChecked(true);
		  			
		  		}
	  		}
	  	}
	  	
	  	ethnicityAsian = ethnicityAsianRadio.isChecked();
	  	ethnicityBlack = ethnicityBlackRadio.isChecked();
	  	ethnicityOther = ethnicityOtherRadio.isChecked();
	  	ethnicityLatino = ethnicityLatinoRadio.isChecked();
	  	ethnicityWhite = ethnicityWhiteRadio.isChecked();
	  	
	  	ethnicityAsianRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(ethnicityAsian == true)
				{
					ethnicityAsian = false;
					ethnicityAsianRadio.setChecked(false);
				}
				else
				{
					ethnicityAsian = true;
					ethnicityAsianRadio.setChecked(true);
				}
			}
		});
	  	ethnicityBlackRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(ethnicityBlack == true)
				{
					ethnicityBlack = false;
					ethnicityBlackRadio.setChecked(false);
				}
				else
				{
					ethnicityBlack = true;
					ethnicityBlackRadio.setChecked(true);
				}
			}
		});
	  	ethnicityOtherRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(ethnicityOther == true)
				{
					ethnicityOther = false;
					ethnicityOtherRadio.setChecked(false);
				}
				else
				{
					ethnicityOther = true;
					ethnicityOtherRadio.setChecked(true);
				}
			}
		});
	  	ethnicityLatinoRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(ethnicityLatino == true)
				{
					ethnicityLatino = false;
					ethnicityLatinoRadio.setChecked(false);
				}
				else
				{
					ethnicityLatino = true;
					ethnicityLatinoRadio.setChecked(true);
				}
			}
		});
	  	ethnicityWhiteRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(ethnicityWhite == true)
				{
					ethnicityWhite = false;
					ethnicityWhiteRadio.setChecked(false);
				}
				else
				{
					ethnicityWhite = true;
					ethnicityWhiteRadio.setChecked(true);
				}
			}
		});
	  	
	  	Button ethnicityConfirm = (Button) ethnicityDialog.findViewById(R.id.ethnicity_confirm);
	  	ethnicityConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				int ethnicityValue = 0;
				String ethnicityValueLang = "";
				if(ethnicityAsianRadio.isChecked() == true)
				{
					ethnicityValue += SexActivity.attributesEthnicityAsian;
					ethnicityValueLang = getString(R.string.ethnicity_asian);
				}
				if(ethnicityBlackRadio.isChecked() == true)
				{
					ethnicityValue += SexActivity.attributesEthnicityBlack;
					ethnicityValueLang += ((ethnicityValueLang.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_black);
				}
				if(ethnicityOtherRadio.isChecked() == true)
				{
					ethnicityValue += SexActivity.attributesEthnicityOther;
					ethnicityValueLang += ((ethnicityValueLang.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_other);
				}
				if(ethnicityLatinoRadio.isChecked() == true)
				{
					ethnicityValue += SexActivity.attributesEthnicityLatino;
					ethnicityValueLang += ((ethnicityValueLang.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_latino);
				}
				if(ethnicityWhiteRadio.isChecked() == true)
				{
					ethnicityValue += SexActivity.attributesEthnicityWhite;
					ethnicityValueLang += ((ethnicityValueLang.equals("")==true) ? "" : ", ") + getString(R.string.ethnicity_white);
				}
				
				if(ethnicityValue != 0)
				{
					setEthnicityValue(ethnicityValueLang);
				}
				else
					filter_looks_ethnicity_input.setText(getString(R.string.filter_looks_ethnicity));
				
				SexActivity.executeTask(new saveUserSearchStringTask("ethnicity", String.valueOf(ethnicityValue)), "save filter ethnicity");
				try
				{
					if(isFinishing() == false)
						ethnicityDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				ethnicityDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void drawBodyDialog()
	{
		final Dialog bodyDialog = new Dialog(this);
		bodyDialog.setContentView(R.layout.dialog_filter_body);
		bodyDialog.setTitle(getString(R.string.filter_looks_body));
	  	
	  	final RadioButton bodySexyRadio = (RadioButton) bodyDialog.findViewById(R.id.body_sexy);
	  	final RadioButton bodySportyRadio = (RadioButton) bodyDialog.findViewById(R.id.body_sporty);
	  	final RadioButton bodySlenderRadio = (RadioButton) bodyDialog.findViewById(R.id.body_slender);
	  	final RadioButton bodyJuicyRadio = (RadioButton) bodyDialog.findViewById(R.id.body_juicy);
	  	final RadioButton bodyMuscleRadio = (RadioButton) bodyDialog.findViewById(R.id.body_muscle);
	  	
	  	if(filter_looks_body_input.getText().toString().equals(getString(R.string.filter_looks_body)) == false)
	  	{
	  		String[] values = new String[10];
	  		values = filter_looks_body_input.getText().toString().split(",");
	  		
	  		if(values.length>0)
	  		{
		  		for(int i=0; i<values.length; i++)
		  		{
		  			if(values[i].trim().equals(getString(R.string.body_sexy)) == true)
		  				bodySexyRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.body_sporty)) == true)
		  				bodySportyRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.body_slender)) == true)
		  				bodySlenderRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.body_juicy)) == true)
		  				bodyJuicyRadio.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.body_muscle)) == true)
		  				bodyMuscleRadio.setChecked(true);
		  			
		  		}
	  		}
	  	}
	  	
	  	bodySexy = bodySexyRadio.isChecked();
	  	bodySporty = bodySportyRadio.isChecked();
	  	bodySlender = bodySlenderRadio.isChecked();
	  	bodyJuicy = bodyJuicyRadio.isChecked();
	  	bodyMuscle = bodyMuscleRadio.isChecked();
	  	
	  	bodySexyRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(bodySexy == true)
				{
					bodySexy = false;
					bodySexyRadio.setChecked(false);
				}
				else
				{
					bodySexy = true;
					bodySexyRadio.setChecked(true);
				}
			}
		});
	  	bodySportyRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(bodySporty == true)
				{
					bodySporty = false;
					bodySportyRadio.setChecked(false);
				}
				else
				{
					bodySporty = true;
					bodySportyRadio.setChecked(true);
				}
			}
		});
	  	bodySlenderRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(bodySlender == true)
				{
					bodySlender = false;
					bodySlenderRadio.setChecked(false);
				}
				else
				{
					bodySlender = true;
					bodySlenderRadio.setChecked(true);
				}
			}
		});
	  	bodyJuicyRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(bodyJuicy == true)
				{
					bodyJuicy = false;
					bodyJuicyRadio.setChecked(false);
				}
				else
				{
					bodyJuicy = true;
					bodyJuicyRadio.setChecked(true);
				}
			}
		});
	  	bodyMuscleRadio.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(bodyMuscle == true)
				{
					bodyMuscle = false;
					bodyMuscleRadio.setChecked(false);
				}
				else
				{
					bodyMuscle = true;
					bodyMuscleRadio.setChecked(true);
				}
			}
		});
	  	
	  	Button bodyConfirm = (Button) bodyDialog.findViewById(R.id.body_confirm);
	  	bodyConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				int bodyValue = 0;
				String bodyValueLang = "";
				if(bodySexyRadio.isChecked() == true)
				{
					bodyValue += SexActivity.attributesBodySexy;
					bodyValueLang = getString(R.string.body_sexy);
				}
				if(bodySportyRadio.isChecked() == true)
				{
					bodyValue += SexActivity.attributesBodySporty;
					bodyValueLang += ((bodyValueLang.equals("")==true) ? "" : ", ") + getString(R.string.body_sporty);
				}
				if(bodySlenderRadio.isChecked() == true)
				{
					bodyValue += SexActivity.attributesBodySlender;
					bodyValueLang += ((bodyValueLang.equals("")==true) ? "" : ", ") + getString(R.string.body_slender);
				}
				if(bodyJuicyRadio.isChecked() == true)
				{
					bodyValue += SexActivity.attributesBodyJuicy;
					bodyValueLang += ((bodyValueLang.equals("")==true) ? "" : ", ") + getString(R.string.body_juicy);
				}
				if(bodyMuscleRadio.isChecked() == true)
				{
					bodyValue += SexActivity.attributesBodyMuscular;
					bodyValueLang += ((bodyValueLang.equals("")==true) ? "" : ", ") + getString(R.string.body_muscle);
				}
				
				if(bodyValue != 0)
				{
					setBodyValue(bodyValueLang);
				}
				else
					filter_looks_body_input.setText(getString(R.string.filter_looks_body));
				
				SexActivity.executeTask(new saveUserSearchStringTask("body", String.valueOf(bodyValue)), "save filter body");
				try
				{
					if(isFinishing() == false)
						bodyDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				bodyDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void drawLookingForDialog()
	{
		final Dialog genderDialog = new Dialog(this);
	  	genderDialog.setContentView(R.layout.dialog_filter_gender);
	  	genderDialog.setTitle(getString(R.string.filter_lookingfor));
	  	
	  	final RadioButton lookingForMan = (RadioButton) genderDialog.findViewById(R.id.gender_man);
	  	final RadioButton lookingForWoman = (RadioButton) genderDialog.findViewById(R.id.gender_woman);
	  	//final RadioButton lookingForPairStraight = (RadioButton) genderDialog.findViewById(R.id.gender_pair_straight);
	  	//final RadioButton lookingForPairLesbian = (RadioButton) genderDialog.findViewById(R.id.gender_pair_lesbian);
	  	//final RadioButton lookingForPairGay = (RadioButton) genderDialog.findViewById(R.id.gender_pair_gay);
	  	
	  	if(filter_lookingfor_input.getText().toString().equals(getString(R.string.filter_lookingfor_input)) == false)
	  	{
	  		String[] values = new String[10];
	  		values = filter_lookingfor_input.getText().toString().split(",");
	  		
	  		if(values.length>0)
	  		{
		  		for(int i=0; i<values.length; i++)
		  		{
		  			if(values[i].trim().equals(getString(R.string.gender_man)) == true)
		  				lookingForMan.setChecked(true);
		  			else if(values[i].trim().equals(getString(R.string.gender_woman)) == true)
		  				lookingForWoman.setChecked(true);
		  			//else if(values[i].trim().equals(getString(R.string.gender_pair_straight)) == true)
		  			//	lookingForPairStraight.setChecked(true);
		  			//else if(values[i].trim().equals(getString(R.string.gender_pair_lesbian)) == true)
		  			//	lookingForPairLesbian.setChecked(true);
		  			//else if(values[i].trim().equals(getString(R.string.gender_pair_gay)) == true)
		  			//	lookingForPairGay.setChecked(true);
		  			
		  		}
	  		}
	  	}
	  	
	  	lookingForManChecked = lookingForMan.isChecked();
	  	lookingForWomanChecked = lookingForWoman.isChecked();
	  	//lookingForPairStraightChecked = lookingForPairStraight.isChecked();
	  	//lookingForPairLesbianChecked = lookingForPairLesbian.isChecked();
	  	//lookingForPairGayChecked = lookingForPairGay.isChecked();
	  	
	  	lookingForMan.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(lookingForManChecked == true)
				{
					lookingForManChecked = false;
					lookingForMan.setChecked(false);
				}
				else
				{
					lookingForManChecked = true;
					lookingForMan.setChecked(true);
				}
			}
		});
	  	lookingForWoman.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(lookingForWomanChecked == true)
				{
					lookingForWomanChecked = false;
					lookingForWoman.setChecked(false);
				}
				else
				{
					lookingForWomanChecked = true;
					lookingForWoman.setChecked(true);
				}
			}
		});
	  	/*
	  	lookingForPairStraight.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(lookingForPairStraightChecked == true)
				{
					lookingForPairStraightChecked = false;
					lookingForPairStraight.setChecked(false);
				}
				else
				{
					lookingForPairStraightChecked = true;
					lookingForPairStraight.setChecked(true);
				}
			}
		});
	  	lookingForPairLesbian.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(lookingForPairLesbianChecked == true)
				{
					lookingForPairStraightChecked = false;
					lookingForPairLesbian.setChecked(false);
				}
				else
				{
					lookingForPairStraightChecked = true;
					lookingForPairLesbian.setChecked(true);
				}
			}
		});
	  	lookingForPairGay.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{ 
				if(lookingForPairGayChecked == true)
				{
					lookingForPairGayChecked = false;
					lookingForPairGay.setChecked(false);
				}
				else
				{
					lookingForPairGayChecked = true;
					lookingForPairGay.setChecked(true);
				}
			}
		});
	  	*/
	  	Button genderConfirm = (Button) genderDialog.findViewById(R.id.gender_confirm);
	  	genderConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				int lookingForValue = 0;
				String lookingForValueLang = "";
				if(lookingForMan.isChecked() == true)
				{
					lookingForValue += SexActivity.attributesGenderMan;
					lookingForValueLang = getString(R.string.gender_man);
				}
				if(lookingForWoman.isChecked() == true)
				{
					lookingForValue += SexActivity.attributesGenderWoman;
					lookingForValueLang += ((lookingForValueLang.equals("")==true) ? "" : ", ") + getString(R.string.gender_woman);
				}
				/*
				if(lookingForPairStraight.isChecked() == true)
				{
					lookingForValue += SexActivity.attributesGenderPairStraight;
					lookingForValueLang += ((lookingForValueLang.equals("")==true) ? "" : ", ") + getString(R.string.gender_pair_straight);
				}
				if(lookingForPairLesbian.isChecked() == true)
				{
					lookingForValue += SexActivity.attributesGenderPairLesbian;
					lookingForValueLang += ((lookingForValueLang.equals("")==true) ? "" : ", ") + getString(R.string.gender_pair_lesbian);
				}
				if(lookingForPairGay.isChecked() == true)
				{
					lookingForValue += SexActivity.attributesGenderPairGay;
					lookingForValueLang += ((lookingForValueLang.equals("")==true) ? "" : ", ") + getString(R.string.gender_pair_gay);
				}
				*/

				
				if(lookingForValue != 0)
				{
					setLookingForValue(lookingForValueLang);
				}
				else
					filter_lookingfor_input.setText(getString(R.string.filter_lookingfor_input));
				
				SexActivity.executeTask(new saveUserSearchStringTask("looking_for", String.valueOf(lookingForValue)), "save filter lookingfor");
				try
				{
					if(isFinishing() == false)
						genderDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				genderDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void setEthnicityValue(String value)
	{
		if(value.equals("") == false)
			filter_looks_ethnicity_input.setText(value);
		else
			filter_looks_ethnicity_input.setText(getString(R.string.filter_looks_ethnicity));
	}
	
	public void setBodyValue(String value)
	{
		if(value.equals("") == false)
			filter_looks_body_input.setText(value);
		else
			filter_looks_body_input.setText(getString(R.string.filter_looks_body));
		
	}
	
	public void setLookingForValue(String value)
	{
		if(value.equals("") == false)
			filter_lookingfor_input.setText(value);
		else
			filter_lookingfor_input.setText(getString(R.string.filter_lookingfor_input));
		
	}
	
	public void saveUserSearchValue(String key, String value)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "set_user_search"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("key", key));
	        nameValuePairs.add(new BasicNameValuePair(key, value));
	     	
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
				else
					Toast.makeText(getApplicationContext(), getString(R.string.toast_error_later), Toast.LENGTH_SHORT).show();
				
			}
			else
				Toast.makeText(getApplicationContext(), getString(R.string.toast_error_later), Toast.LENGTH_SHORT).show();
			
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
	
	public JSONArray getCountryList()
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_locations"));
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
				JSONArray jLocations = json_data.optJSONArray("locations");
				
				return jLocations;
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
	
	public JSONArray getCitiesList(int pState, int pCountry)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_cities"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("country", String.valueOf(pCountry)));
	        nameValuePairs.add(new BasicNameValuePair("state", String.valueOf(pState)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				JSONArray jCities = json_data.optJSONArray("cities");
				
				return jCities;
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
	
	public JSONArray getStatesList(int pCountry)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "get_states"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("country", String.valueOf(pCountry)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				JSONArray jStates = json_data.optJSONArray("states");
				
				return jStates;
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
	
	private class saveUserSearchStringTask extends AsyncTask<String, Integer, String>
	{
		public String key = "";
		public String value = "";
		
	    public saveUserSearchStringTask(String pKey, String pValue)
	    {
	    	super();
	    	key = pKey;
	    	value = pValue;
		}

		
	    protected void onPostExecute(String result)
	    {
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			saveUserSearchValue(key, value);
			return "ok";
		}
	}
	
	class getCountries extends AsyncTask<String, JSONArray, JSONArray>
	{
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			setupCountryDialog(result);
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return getCountryList();
		}
	}
	
	class getStates extends AsyncTask<String, JSONArray, JSONArray>
	{
		int country = 0;
		public getStates(int pCountry)
		{
			super();
			country = pCountry;
		} 
		
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			setupStatesDialog(result);
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return getStatesList(country);
		}
	}
	
	class getCities extends AsyncTask<String, JSONArray, JSONArray>
	{
		int country = 0;
		int state = 0;
		public getCities(int pState, int pCountry)
		{
			super();
			state = pState;
			country = pCountry;
		}
		
		protected void onPostExecute(JSONArray result)
	    {
			hideLoadingDialog();
			setupCitiesDialog(result);
	    }
		
		@Override
		protected JSONArray doInBackground(String... params) 
		{
			return getCitiesList(state, country);
		}
	}
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedFilter();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}
}
