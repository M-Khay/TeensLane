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
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GiftActivity extends SexActivity 
{
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gift);
		
		SexActivity.executeTask(new getUserData(), "load user data");
	}

	public void proceedGifts()
	{
		TextView title = (TextView) findViewById(R.id.header_heading);
		title.setText(getString(R.string.sendgift_title));
	
		Bundle extras = getIntent().getExtras();
		if(extras != null && extras.isEmpty() == false && extras.containsKey("usernickname"))
		{
			title.setText(getString(R.string.sendgift_title) + " " + extras.getString("usernickname"));
		}
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				GiftActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setVisibility(ImageView.GONE);
		
		setupGiftsAction();
	}
	
	public void setupGiftsAction()
	{
		final int priceCondoms = 50;
		final int priceAnimals = 150;
		final int priceBadges  = 200;
		
		View.OnClickListener giftBtnListener = new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Object tag = v.getTag();
				int stringID = getResources().getIdentifier(tag.toString(), "string", getPackageName());
				int drawableID = getResources().getIdentifier(tag.toString(), "drawable", getPackageName());
				int price = 500;
				if(tag.toString().contains("condom") == true)
					price = priceCondoms;
				else if(tag.toString().contains("animal") == true)
					price = priceAnimals;
				else if(tag.toString().contains("badge") == true)
					price = priceBadges;
				
				showGiftDialog(getString(stringID), price, drawableID);
			}
		};
		/*
		Button condom_devil = (Button) findViewById(R.id.condom_devil);
		condom_devil.setTag("condom_devil");
		condom_devil.setOnClickListener(giftBtnListener);
		Button condom_angel = (Button) findViewById(R.id.condom_angel);
		condom_angel.setTag("condom_angel");
		condom_angel.setOnClickListener(giftBtnListener);
		Button condom_sm = (Button) findViewById(R.id.condom_sm);
		condom_sm.setTag("condom_sm");
		condom_sm.setOnClickListener(giftBtnListener);
		Button condom_ninja = (Button) findViewById(R.id.condom_ninja);
		condom_ninja.setTag("condom_ninja");
		condom_ninja.setOnClickListener(giftBtnListener);
		Button condom_badboy = (Button) findViewById(R.id.condom_badboy);
		condom_badboy.setTag("condom_badboy");
		condom_badboy.setOnClickListener(giftBtnListener);
		Button condom_viking = (Button) findViewById(R.id.condom_viking);
		condom_viking.setTag("condom_viking");
		condom_viking.setOnClickListener(giftBtnListener);
		Button condom_gigolo = (Button) findViewById(R.id.condom_gigolo);
		condom_gigolo.setTag("condom_gigolo");
		condom_gigolo.setOnClickListener(giftBtnListener);
		Button condom_sexy = (Button) findViewById(R.id.condom_sexy);
		condom_sexy.setTag("condom_sexy");
		condom_sexy.setOnClickListener(giftBtnListener);
		Button condom_cactus = (Button) findViewById(R.id.condom_cactus);
		condom_cactus.setTag("condom_cactus");
		condom_cactus.setOnClickListener(giftBtnListener);
        */
		Button animal_piggy = (Button) findViewById(R.id.animal_piggy);
		animal_piggy.setTag("animal_piggy");
		animal_piggy.setOnClickListener(giftBtnListener);
		Button animal_duck = (Button) findViewById(R.id.animal_duck);
		animal_duck.setTag("animal_duck");
		animal_duck.setOnClickListener(giftBtnListener);
		Button animal_chick = (Button) findViewById(R.id.animal_chick);
		animal_chick.setTag("animal_chick");
		animal_chick.setOnClickListener(giftBtnListener);
		Button animal_bad_piggy = (Button) findViewById(R.id.animal_bad_piggy);
		animal_bad_piggy.setTag("animal_bad_piggy");
		animal_bad_piggy.setOnClickListener(giftBtnListener);
		Button animal_teddy = (Button) findViewById(R.id.animal_teddy);
		animal_teddy.setTag("animal_teddy");
		animal_teddy.setOnClickListener(giftBtnListener);
		Button animal_bunny = (Button) findViewById(R.id.animal_bunny);
		animal_bunny.setTag("animal_bunny");
		animal_bunny.setOnClickListener(giftBtnListener);
		
		/*
		Button badge_woman_bad = (Button) findViewById(R.id.badge_woman_bad);
		badge_woman_bad.setTag("badge_woman_bad");
		badge_woman_bad.setOnClickListener(giftBtnListener);
		Button badge_woman_hot_ass = (Button) findViewById(R.id.badge_woman_hot_ass);
		badge_woman_hot_ass.setTag("badge_woman_hot_ass");
		badge_woman_hot_ass.setOnClickListener(giftBtnListener);
		*/
		Button badge_woman_hot_body = (Button) findViewById(R.id.badge_woman_hot_body);
		badge_woman_hot_body.setTag("badge_woman_hot_body");
		badge_woman_hot_body.setOnClickListener(giftBtnListener);
		Button badge_man_bad = (Button) findViewById(R.id.badge_man_bad);
		badge_man_bad.setTag("badge_man_bad");
		badge_man_bad.setOnClickListener(giftBtnListener);
		/*
		Button badge_man_ass = (Button) findViewById(R.id.badge_man_ass);
		badge_man_ass.setTag("badge_man_ass");
		badge_man_ass.setOnClickListener(giftBtnListener);
		Button badge_man_hot = (Button) findViewById(R.id.badge_man_hot);
		badge_man_hot.setTag("badge_man_hot");
		badge_man_hot.setOnClickListener(giftBtnListener);
		Button badge_man_mrbig = (Button) findViewById(R.id.badge_man_mrbig);
		badge_man_mrbig.setTag("badge_man_mrbig");
		badge_man_mrbig.setOnClickListener(giftBtnListener);
		Button badge_woman_sexy_legs = (Button) findViewById(R.id.badge_woman_sexy_legs);
		badge_woman_sexy_legs.setTag("badge_woman_sexy_legs");
		badge_woman_sexy_legs.setOnClickListener(giftBtnListener);
		*/
	}
	
	public void showGiftDialog(String giftName, final int giftPrice, final int giftId)
	{
		final Dialog giftDialog = new Dialog(this);
		giftDialog.setContentView(R.layout.dialog_gift); 
		giftDialog.setTitle(R.string.gift_detail);

		ImageView gift_image = (ImageView) giftDialog.findViewById(R.id.gift_image);
		gift_image.setImageResource(giftId);
		
		TextView gift_name = (TextView) giftDialog.findViewById(R.id.gift_name);
		gift_name.setText(giftName);
		
		TextView gift_price = (TextView) giftDialog.findViewById(R.id.gift_price);
		gift_price.setText(String.valueOf(giftPrice) + " " + getString(R.string.credits));
		
		Button dialogConfirm = (Button) giftDialog.findViewById(R.id.gift_action);
		
		View.OnClickListener buygiftAction = new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				SexActivity.executeTask(new sendGift(SexActivity.otherUser.session, giftPrice, giftId, giftDialog), "send gift to user");
				
			}	
		};
		
		View.OnClickListener buycreditsAction = new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				Bundle extras = new Bundle();
				extras.putInt("premium", 1);
				Intent userDetail = new Intent(getBaseContext(), MyprofileActivity.class);
				userDetail.putExtras(extras);
				startActivity(userDetail);
				try
				{
					if(isFinishing() == false)
						giftDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		};
		
		if(SexActivity.me.credits < giftPrice)
		{
			dialogConfirm.setText(getString(R.string.buy_credits));
			dialogConfirm.setOnClickListener(buycreditsAction);
		}
		else
		{
			dialogConfirm.setText(getString(R.string.buy_gift));
			dialogConfirm.setOnClickListener(buygiftAction);
		}
		
		try
		{
			if(isFinishing() == false)
				giftDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	//
	class getUserData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedGifts();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetUserData();
			return "ok";
		}
	}
	
	class sendGift extends AsyncTask<String, Integer, Boolean>
	{
		String user;
		int price;
		int gift;
		Dialog giftDialog;
		
		public sendGift(String pUser, int pPrice, int pGift, Dialog pGiftDialog)
		{
			user = pUser;
			gift = pGift;
			price = pPrice;
			giftDialog = pGiftDialog;
		}
		
		protected void onPostExecute(Boolean result)
	    {
			//Log.d("user credits after", String.valueOf(SexActivity.me.credits));
			if(result == true)
			{
				Toast.makeText(getApplicationContext(), getString(R.string.gift_sent), Toast.LENGTH_SHORT).show();
				if(giftDialog != null)
				{
					try
					{
						if(isFinishing() == false)
							giftDialog.cancel();
					}
					catch(Exception e)
					{
					}
				}
				
			}
			else
				Toast.makeText(getApplicationContext(), getString(R.string.gift_notsent), Toast.LENGTH_SHORT).show();
	    }
		
		@Override
		protected Boolean doInBackground(String... params) 
		{
			Log.d("user credits before", String.valueOf(SexActivity.me.credits));
			return httpSendGift(user, price, gift);
		}
	}
	
	public Boolean httpSendGift(String user, int price, int gift)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        int giftId = SexActivity.gifts_id[0];
	        for (int i = 0; i < SexActivity.gifts_resources.length; i++) {
	        	if (SexActivity.gifts_resources[i] == gift) {
	        		giftId = SexActivity.gifts_id[i];
	        		break;
	        	}
	        }
	        
	        nameValuePairs.add(new BasicNameValuePair("method", "send_gift"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("other_session", user));
	        nameValuePairs.add(new BasicNameValuePair("price", String.valueOf(price)));
	        nameValuePairs.add(new BasicNameValuePair("gift", String.valueOf(giftId)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.opt("success") != null && json_data.opt("credits") != null) 
				{
					SexActivity.me.credits = json_data.getInt("credits");
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