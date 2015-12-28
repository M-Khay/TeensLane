package com.teenslane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.BitmapFactory.Options;
import android.graphics.Shader.TileMode;
import android.content.Context;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromContainsFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;


public class UserDetailActivity extends FragmentActivity
{
	
	ImageView detailBtn1 = null;
    ImageView detailBtn2 = null;
    ImageView detailBtn3 = null;
    ImageView detailBtn4 = null;
    
    ImageView detailBtnRolled1 = null;
    ImageView detailBtnRolled2 = null;
    ImageView detailBtnRolled3 = null;
    ImageView detailBtnRolled4 = null;
    
    RelativeLayout topNav = null;
    RelativeLayout topContent = null;
    RelativeLayout navigation = null;
    RelativeLayout navigation_bottom = null;
   // RelativeLayout navigation_bottom_line = null;
    
    RelativeLayout myprofile_rl_rolled = null;
    RelativeLayout myprofile_settings_content = null;
    RelativeLayout myprofile_about_content = null;
    RelativeLayout myprofile_gallery_content = null;
    RelativeLayout userprofile_map_content = null;
    RelativeLayout userprofile_messages_content = null;
    RelativeLayout navigation_top = null;
    RelativeLayout myprofile_active_content = null;
    
    View map_content = null;
    //ProgressBar user_image_loading = null;
    
    JSONObject user = null;
    Dialog loadingDialog = null;
    
    GoogleMap googleMap;
    boolean reported = false;
    boolean overlayed = false;
    
    MediaPlayer mediaPlayerKiss;
    MediaPlayer mediaPlayerSlap;
	AudioManager audioManager;
	
	public XMPPConnection connection;
    
	LayoutInflater inflater = null;
	RelativeLayout parent_layout = null;
	ScrollView scroll_view = null;
	LinearLayout linear_layout = null;
	
	JSONArray olderMessages = new JSONArray();
	int maxMessagesPerLoad = 10;
	
	Bitmap meBitmap = null;
	Integer meDrawable = null;
	
	Bitmap otherBitmap = null;
	Integer otherDrawable = null;
	boolean initMessagesLoad = false;
	
	PacketListener userPacketListener = null;
	boolean forceMessages = false;
	boolean forceGifts = false;
	boolean forceGallery = false;

	int costForResponse = 0; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if(SexActivity.me.session.equals("") == true)
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
		}
		
		LocalService.chatting = 0; 
		
		myprofile_active_content = null;
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		parent_layout = (RelativeLayout) inflater.inflate(R.layout.activity_user_profile, null);
		scroll_view = (ScrollView) parent_layout.findViewById(R.id.scroll_view);
		linear_layout = (LinearLayout) parent_layout.findViewById(R.id.linear_layout); 
		
		setContentView(parent_layout);
		
		SexActivity.executeTask(new getMeData(), "load user data");
	}
	
	@Override
    protected void onDestroy()
	{
        super.onDestroy();
        LocalService.chatting = 0;
    }
	
	class getMeData extends AsyncTask<String, Integer, String>
	{
		protected void onPostExecute(String result)
	    {
			proceedDetail();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpMeData();
			return "ok";
		}
	}
	public void httpMeData()
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
	public void proceedDetail()
	{
		Bundle extras = getIntent().getExtras();
		
		forceMessages = false;
		forceGifts = false;
		forceGallery = false;
		
		if(extras != null && extras.isEmpty() == false && (extras.containsKey("user_id") || extras.containsKey("user_index")))
		{
			
			if(extras.containsKey("force_messages"))
				forceMessages = true;
			else
			{
				if(extras.containsKey("gifts"))
					forceGifts = true;
				else
				{
					if(extras.containsKey("gallery_force"))
						forceGallery = true;
					
				}
			}
			
			
			if(extras.containsKey("user_index"))
			{
				String user_index = extras.getString("user_index");
				if(user_index.equals("") == false && SexActivity.loadedUsers.size()>0)
				{
					Integer userIndex = null;
					userIndex = Integer.valueOf(user_index.substring(1));
					if(userIndex != null)
					{
						user = SexActivity.loadedUsers.get(userIndex);
						showLoadingDialog();
						try 
						{
							LocalService.chatting = user.getInt("id");
							SexActivity.executeTask(new getUserData(Integer.valueOf(user.getString("id"))), "load user data");
						}
						catch (NumberFormatException e) 
						{
						}
						catch (JSONException e) 
						{
						}
					}
				}
			}
			else if(extras.containsKey("user_id"))
			{
				Integer userId = Integer.valueOf(extras.getString("user_id"));
				if(userId != null)
				{
					LocalService.chatting = userId;
					showLoadingDialog();
					SexActivity.executeTask(new getUserData(userId), "load user data");
				}
			}
		}
	}
   
	
    @Override
    protected void onResume()
    {
    	super.onResume();
    	if(SexActivity.menuReturn == true)
		{
    		SexActivity.menuReturn = false;
    		LocalService.chatting = 0;
    		UserDetailActivity.this.finish();
			Intent mainIntent = new Intent(getBaseContext(), IntroActivity.class);
       		startActivity(mainIntent);
		}
    	
    	if (userprofile_messages_content != null) {
			updateNewMessages(userprofile_messages_content, costForResponse);	
    	}
    }
    
	public void proceedUserProfile()
	{
		mediaPlayerKiss = MediaPlayer.create(getApplicationContext(), R.raw.kissx);
		
		setLayouts();
		setupButtons();
		setupNavigationButtons();
		 
		SexActivity.executeTask(new xmppInit(), "set xmpp"); 
		 
		if(forceMessages == true)
	  	{
	  		detailBtn3.setImageResource(R.drawable.detail_button_messages_hover);
			detailBtnRolled3.setImageResource(R.drawable.detail_button_messages_hover);
			
			myprofile_active_content = userprofile_messages_content;
			disableIntroButtons();
			setUserMessages();
			animUp();
	  	}
		else if(forceGifts == true)
		{
			Bundle extras = new Bundle();
			extras.putString("usernickname", SexActivity.otherUser.nickname);
			
			Intent giftsIntent = new Intent(getBaseContext(), GiftActivity.class);
			giftsIntent.putExtras(extras);
       		startActivity(giftsIntent);
		}
		else if(forceGallery == true)
		{
			detailBtn2.setImageResource(R.drawable.detail_button_gallery_hover);
			detailBtnRolled2.setImageResource(R.drawable.detail_button_gallery_hover);
			
			myprofile_active_content = myprofile_gallery_content;
			disableIntroButtons();
			setMyProfileGalleryClicks();
			animUp();
		}
	}
	
	public void setLayouts()
	{
		if(SexActivity.otherUser.images.size()>0)
		{
			for(int i=0; i<SexActivity.otherUser.images.size(); i++)
			{
				SexActivity.otherUser.images_loaded.set(i, false);
			}
		}
		
		TextView heading = (TextView) findViewById(R.id.header_heading);
		heading.setText(SexActivity.otherUser.nickname);
		
		//get layouts for anim and so
		topNav = (RelativeLayout) findViewById(R.id.myprofile_rl_header);
		topContent = (RelativeLayout) findViewById(R.id.myprofile_rl_intro);
		navigation = (RelativeLayout) findViewById(R.id.myprofile_rl_navigation);
		navigation_bottom = (RelativeLayout) navigation.findViewById(R.id.navigation_bottom);
		//navigation_bottom_line = (RelativeLayout) navigation_bottom.findViewById(R.id.detail_navigation_line);
		//navigation_bottom_line.setVisibility(RelativeLayout.GONE);
		
		myprofile_rl_rolled = (RelativeLayout) findViewById(R.id.myprofile_rl_rolled);
		navigation_top = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.navigation_top);;
		
		myprofile_settings_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_settings_content);
		myprofile_about_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_about_content);
		myprofile_gallery_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_gallery_content);
		userprofile_map_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.userprofile_map_content);
		userprofile_messages_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.userprofile_messages_content);
		map_content = (View) userprofile_map_content.findViewById(R.id.map);
		
		//user_image_loading = (ProgressBar) topContent.findViewById(R.id.image_loading);
		//user_image_loading.setVisibility(ProgressBar.INVISIBLE);
		
		//change navigation buttons	
		detailBtn1 = (ImageView) navigation.findViewById(R.id.detail_button1);
		detailBtn2 = (ImageView) navigation.findViewById(R.id.detail_button2);
		detailBtn3 = (ImageView) navigation.findViewById(R.id.detail_button3);
		detailBtn4 = (ImageView) navigation.findViewById(R.id.detail_button4);
		
		detailBtn1.setImageResource(R.drawable.css_detail_info);
		detailBtn2.setImageResource(R.drawable.css_detail_gallery);
		detailBtn3.setImageResource(R.drawable.css_detail_messages);
		detailBtn4.setImageResource(R.drawable.css_detail_map);
		
		detailBtnRolled1 = (ImageView) navigation_top.findViewById(R.id.detail_button1);
		detailBtnRolled2 = (ImageView) navigation_top.findViewById(R.id.detail_button2);
		detailBtnRolled3 = (ImageView) navigation_top.findViewById(R.id.detail_button3);
		detailBtnRolled4 = (ImageView) navigation_top.findViewById(R.id.detail_button4);
		
		detailBtnRolled1.setImageResource(R.drawable.css_detail_info);
		detailBtnRolled2.setImageResource(R.drawable.css_detail_gallery);
		detailBtnRolled3.setImageResource(R.drawable.css_detail_messages);
		detailBtnRolled4.setImageResource(R.drawable.css_detail_map);
	}
	
	public void setupButtons()
	{
		final Button useraction_pets = (Button) findViewById(R.id.useraction_pets);
		if(SexActivity.otherUser.myPet == 1)
			useraction_pets.setBackgroundResource(R.drawable.useraction_pets_hover);
		else
			useraction_pets.setBackgroundResource(R.drawable.useraction_pets);
			
		useraction_pets.refreshDrawableState();
		
		Button useraction_gift = (Button) findViewById(R.id.useraction_gift);
		useraction_gift.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				Bundle extras = new Bundle();
				extras.putString("usernickname", SexActivity.otherUser.nickname);
				
				Intent giftsIntent = new Intent(getBaseContext(), GiftActivity.class);
				giftsIntent.putExtras(extras);
	       		startActivity(giftsIntent);
			}
		});
		
		useraction_pets.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showConfirmPetsDialog();
			}
		});
		
		final ImageView big_kiss = (ImageView) findViewById(R.id.big_kiss);
		Button useraction_kiss = (Button) findViewById(R.id.useraction_kiss); 
		useraction_kiss.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v)
			{
				big_kiss.setVisibility(ImageView.VISIBLE);
				big_kiss.refreshDrawableState();
				
				Animation myFadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
				myFadeOutAnimation.setAnimationListener(new AnimationListener() 
				{
					@Override
					public void onAnimationEnd(Animation animation) 
					{
						big_kiss.setVisibility(ImageView.INVISIBLE);
						big_kiss.refreshDrawableState();
						
						SexActivity.executeTask(new setKissOrSlap(SexActivity.otherUser.id, "kiss"), "set slap action");
					}
 
					@Override
					public void onAnimationRepeat(Animation animation) 
					{
					}

					@Override
					public void onAnimationStart(Animation animation)
					{
						big_kiss.refreshDrawableState();
						
						audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
						mediaPlayerKiss.setVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
						
						switch (audioManager.getRingerMode()) 
						{
						    case AudioManager.RINGER_MODE_SILENT:
						    	mediaPlayerKiss.setVolume(0,0);
						        break;
						    case AudioManager.RINGER_MODE_VIBRATE:
						    	mediaPlayerKiss.setVolume(0,0);
						        break;
						}
						mediaPlayerKiss.start();
					}
				});
				
				big_kiss.startAnimation(myFadeOutAnimation);
			}
		});
		
		final ImageView big_slap = (ImageView) findViewById(R.id.big_slap);
		Button useraction_slap = (Button) findViewById(R.id.useraction_slap); 
		useraction_slap.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View v)
			{
				big_slap.setVisibility(ImageView.VISIBLE);
				big_slap.refreshDrawableState();
				
				Animation myFadeOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
				myFadeOutAnimation.setAnimationListener(new AnimationListener() 
				{
					@Override
					public void onAnimationEnd(Animation animation) 
					{
						big_slap.setVisibility(ImageView.INVISIBLE);
						big_slap.refreshDrawableState();
						
						SexActivity.executeTask(new setKissOrSlap(SexActivity.otherUser.id, "slap"), "set slap action");
						
					}

					@Override
					public void onAnimationRepeat(Animation animation) 
					{
					}

					@Override
					public void onAnimationStart(Animation animation) 
					{
						big_slap.refreshDrawableState();
					}
				});
				
				big_slap.startAnimation(myFadeOutAnimation);
			}
		});
			
		
		navigation_bottom.setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(overlayed == false)
				{
					int eventAction = event.getAction();
					switch(eventAction)
					{
		                case MotionEvent.ACTION_DOWN:
		                	disableIntroButtons();
		                	
		                	if(myprofile_active_content == null || myprofile_active_content == myprofile_settings_content)
		                	{
		                		detailBtn1.setImageResource(R.drawable.detail_button_info_hover);
		                		detailBtnRolled1.setImageResource(R.drawable.detail_button_info_hover);
		                		
		                		myprofile_active_content = myprofile_settings_content;
		                		setUserData();
		                	}
		                	animUp();
		                    break;
		            }
				}
				return false;
			}
		});
		
		navigation_top.setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) 
			{
				if(overlayed == false)
				{
					int eventAction = event.getAction();
					switch(eventAction){
		                case MotionEvent.ACTION_DOWN:
		                	map_content.setVisibility(View.GONE);
		                	enableIntroButtons();
		                	animDown();
		                	break;
	            }
				}
				return false;
			}
		});
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				UserDetailActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				UserDetailActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(), IntroActivity.class);
	       		startActivity(mainIntent);
			}
		});
		
		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
		if(SexActivity.otherUser.image != null && SexActivity.otherUser.image.equalsIgnoreCase("") == false)
		{
			//user_image_loading.setVisibility(ProgressBar.VISIBLE);
			
			//set user image
			String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_240 + "/" + SexActivity.otherUser.image;
			SexActivity.executeTask(new setImageFromUrl(userImageUrl, pickPhoto, "circle"), "load user image");
			
			pickPhoto.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					if(overlayed == false)
					{
						Bundle extras = new Bundle();
						extras.putInt("image_index", -1);
							
						Intent gallery = new Intent(getBaseContext(), GalleryuserActivity.class);
						gallery.putExtras(extras);
						startActivity(gallery);
					}
				}
			});
		}
		else
		{
			setNoPhotoImage(pickPhoto, SexActivity.otherUser.gender);
		}
		
		if(SexActivity.otherUser.cover_image != null && SexActivity.otherUser.cover_image.equalsIgnoreCase("") == false)
		{
			ImageView user_cover_image = (ImageView) findViewById(R.id.user_cover_image);
			String cover_image = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_COVER + "/" + SexActivity.otherUser.cover_image;
			SexActivity.executeTask(new setImageFromUrl(cover_image, user_cover_image), "load user image");
		}
	}
	
	public void showConfirmPetsDialog()
	{
		final Dialog confirmDialog = new Dialog(this);
		confirmDialog.setContentView(R.layout.dialog_confirm);
		if(SexActivity.otherUser.myPet == 1)
			confirmDialog.setTitle(getString(R.string.useraction_pets_out_confirm));
		else
			confirmDialog.setTitle(getString(R.string.useraction_pets_in_confirm));
	  	
	  	Button dialogConfirm = (Button) confirmDialog.findViewById(R.id.confirm_confirm);
	  	dialogConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				SexActivity.executeTask(new petUser(SexActivity.otherUser.id), "add/remove user from pets");
				try
	    		{
	    			if(isFinishing() == false)
	    				confirmDialog.cancel();
	    		}
	    		catch(Exception e)
	    		{
	    		}
			}	
		});
	  	
	  	Button dialogCancel = (Button) confirmDialog.findViewById(R.id.confirm_cancel);
	  	dialogCancel.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				try
	    		{
	    			if(isFinishing() == false)
	    				confirmDialog.cancel();
	    		}
	    		catch(Exception e)
	    		{
	    		}
			}	
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				confirmDialog.show();
		}
		catch(Exception e)
		{
		}
	}

	/* duplicated from sex activity .... */
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
			creditsLeftView.setText(SexActivity.me.credits + " " + getString(R.string.credits));
			
			// enable or hide purchase more if not enough credit	
			if (cost > SexActivity.me.credits) {
				purchaseMoreBlock.setVisibility(View.VISIBLE);		
				writeMessageBlock.setVisibility(View.GONE);
			} 
			else {
				purchaseMoreBlock.setVisibility(View.GONE);		
				writeMessageBlock.setVisibility(View.VISIBLE);
			}
		}
	}
	
	public void setUserMessages() 
	{
		updateNewMessages(userprofile_messages_content, costForResponse);
		
		final EditText sendmessage_text = (EditText) userprofile_messages_content.findViewById(R.id.sendmessage_text);
		Button sendmessage_button = (Button) userprofile_messages_content.findViewById(R.id.sendmessage_button);
		
		sendmessage_button.setOnClickListener(new View.OnClickListener() 
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
				
				if(sendmessage_text.getText().toString().equalsIgnoreCase("") == false)
				{
					SexActivity.executeTask(new newMessage(String.valueOf(SexActivity.otherUser.id), sendmessage_text.getText().toString()), "send message to user");
				    SexActivity.executeTask(new postMessageSync(SexActivity.otherUser.session, sendmessage_text), "post save");
				}
			}
		});
		
		sendmessage_text.setOnEditorActionListener(new OnEditorActionListener()
		{
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) 
			{
				if(actionId == EditorInfo.IME_ACTION_DONE) 
				{
					if(sendmessage_text.getText().toString().equalsIgnoreCase("") == false)
					{
						SexActivity.executeTask(new newMessage(String.valueOf(SexActivity.otherUser.id), sendmessage_text.getText().toString()), "send message to user");
					    SexActivity.executeTask(new postMessageSync(SexActivity.otherUser.session, sendmessage_text), "post save");
					}
				}
				return false;
			}
		});
		
		if(initMessagesLoad == false)
		{
			initMessagesLoad = true;
			
			//load messages
			userprofile_messages_content.setVisibility(View.GONE);
			SexActivity.executeTask(new loadOldMessages(SexActivity.otherUser.session, 1));
		}
	}
	
	public void setupNavigationButtons()
	{
		detailBtn1.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					detailBtn1.setImageResource(R.drawable.detail_button_info_hover);
					detailBtnRolled1.setImageResource(R.drawable.detail_button_info_hover);
					
					myprofile_active_content = myprofile_settings_content;
					disableIntroButtons();
					setUserData();
					animUp();
				}
			}
		});
	  	detailBtnRolled1.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					if(myprofile_active_content == null)
					{
						enableIntroButtons();
						animDown();
					}
					else
					{
						if(myprofile_active_content == myprofile_settings_content)
						{
							enableIntroButtons();
							animDown();
						}
						else
						{
							detailBtn1.setImageResource(R.drawable.detail_button_info_hover);
							detailBtnRolled1.setImageResource(R.drawable.detail_button_info_hover);
							
							detailBtn2.setImageResource(R.drawable.css_detail_gallery);
							detailBtnRolled2.setImageResource(R.drawable.css_detail_gallery);
	
							detailBtn3.setImageResource(R.drawable.css_detail_messages);
							detailBtnRolled3.setImageResource(R.drawable.css_detail_messages);
							
							detailBtn4.setImageResource(R.drawable.css_detail_map);
							detailBtnRolled4.setImageResource(R.drawable.css_detail_map);
							
							myprofile_active_content = myprofile_settings_content;
							setUserData();
							switchContents();
						}
					}
				}
			}
		});
	  	
	  	detailBtn2.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					detailBtn2.setImageResource(R.drawable.detail_button_gallery_hover);
					detailBtnRolled2.setImageResource(R.drawable.detail_button_gallery_hover);
					
					myprofile_active_content = myprofile_gallery_content;
					disableIntroButtons();
					setMyProfileGalleryClicks();
					animUp();
				}
			}
		});
	  	detailBtnRolled2.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					if(myprofile_active_content == null)
					{
						myprofile_active_content = myprofile_about_content;
						enableIntroButtons();
						animDown();
					}
					else
					{
						if(myprofile_active_content == myprofile_gallery_content)
						{
							enableIntroButtons();
							animDown();
						}
						else
						{
							detailBtn1.setImageResource(R.drawable.css_detail_info);
							detailBtnRolled1.setImageResource(R.drawable.css_detail_info);
							
							detailBtn2.setImageResource(R.drawable.detail_button_gallery_hover);
							detailBtnRolled2.setImageResource(R.drawable.detail_button_gallery_hover);
							
							detailBtn3.setImageResource(R.drawable.css_detail_messages);
							detailBtnRolled3.setImageResource(R.drawable.css_detail_messages);
							
							detailBtn4.setImageResource(R.drawable.css_detail_map);
							detailBtnRolled4.setImageResource(R.drawable.css_detail_map);
							
							myprofile_active_content = myprofile_gallery_content;
							setMyProfileGalleryClicks();
							switchContents();
						}
					}
				}
			}
		});
	  	
	  	detailBtn3.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					detailBtn3.setImageResource(R.drawable.detail_button_messages_hover);
					detailBtnRolled3.setImageResource(R.drawable.detail_button_messages_hover);
					
					myprofile_active_content = userprofile_messages_content;
					disableIntroButtons();
					setUserMessages();
					animUp();
				}
			}
		});
	  	detailBtnRolled3.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					if(myprofile_active_content == null)
					{
						map_content.setVisibility(View.GONE);
						myprofile_active_content = userprofile_messages_content;
						enableIntroButtons();
						animDown();
					}
					else
					{
						if(myprofile_active_content == userprofile_messages_content)
						{
							enableIntroButtons();
							animDown();
						}
						else
						{
							detailBtn1.setImageResource(R.drawable.css_detail_info);
							detailBtnRolled1.setImageResource(R.drawable.css_detail_info);
							
							detailBtn2.setImageResource(R.drawable.css_detail_gallery);
							detailBtnRolled2.setImageResource(R.drawable.css_detail_gallery);
							
							detailBtn3.setImageResource(R.drawable.detail_button_messages_hover);
							detailBtnRolled3.setImageResource(R.drawable.detail_button_messages_hover);
							
							detailBtn4.setImageResource(R.drawable.css_detail_map);
							detailBtnRolled4.setImageResource(R.drawable.css_detail_map);
							
							myprofile_active_content = userprofile_messages_content;
							setUserMessages();
							switchContents();
							
						}
					}
				}
			}
		});
	  	detailBtn4.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					map_content.setVisibility(View.VISIBLE);
					detailBtn4.setImageResource(R.drawable.detail_button_map_hover);
					detailBtnRolled4.setImageResource(R.drawable.detail_button_map_hover);
					
					myprofile_active_content = userprofile_map_content;
					disableIntroButtons();
					setUserMap();
					animUp();
				}
			}
		});
	  	detailBtnRolled4.setOnClickListener(new View.OnClickListener()
	  	{
			@Override
			public void onClick(View v) 
			{
				if(overlayed == false)
				{
					if(myprofile_active_content == null)
					{
						map_content.setVisibility(View.GONE);
						myprofile_active_content = userprofile_map_content;
						enableIntroButtons();
						animDown();
					}
					else
					{
						if(myprofile_active_content == userprofile_map_content)
						{
							map_content.setVisibility(View.GONE);
							enableIntroButtons();
							animDown();
						}
						else
						{
							map_content.setVisibility(View.VISIBLE);
							
							detailBtn1.setImageResource(R.drawable.css_detail_info);
							detailBtnRolled1.setImageResource(R.drawable.css_detail_info);
							
							detailBtn2.setImageResource(R.drawable.css_detail_gallery);
							detailBtnRolled2.setImageResource(R.drawable.css_detail_gallery);
							
							detailBtn3.setImageResource(R.drawable.css_detail_messages);
							detailBtnRolled3.setImageResource(R.drawable.css_detail_messages);
							
							detailBtn4.setImageResource(R.drawable.detail_button_map_hover);
							detailBtnRolled4.setImageResource(R.drawable.detail_button_map_hover);
							
							myprofile_active_content = userprofile_map_content;
							setUserMap();
							switchContents();
						}
					}
				}
			}
		});
	  	
	}
	
	public void setUserMap()
    {
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
        	SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
 
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(false);
 
            if(SexActivity.otherUser != null)
            {
            	showUsersMarkers(googleMap, SexActivity.otherUser);
            }
        }
    }
	
	public void showUsersMarkers(GoogleMap googleMap, User user) 
    {
		if(user != null)
		{
			BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
			
			if(user.gender == SexActivity.attributesGenderMan)
	  		{
				icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_man);
	  		}
	  		else if(user.gender == SexActivity.attributesGenderWoman)
	  		{
	  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_woman);
	  		}	
	  		else if(user.gender == SexActivity.attributesGenderPairStraight)
	  		{
	  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_straight);
	  		}
	  		else if(user.gender == SexActivity.attributesGenderPairLesbian)
	  		{
	  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_lesbian);
	  		}
	  		else if(user.gender == SexActivity.attributesGenderPairGay)
	  		{
	  			icon = BitmapDescriptorFactory.fromResource(R.drawable.marker_pair_gay);
	  		}
			
			if(user.hide_on_map == 0 && user.latitude != null && user.longitude != null)
			{
				googleMap.addMarker(new MarkerOptions()
					.position(new LatLng(user.latitude, user.longitude))
				    .icon(icon));
				
				LatLng latLng = new LatLng(user.latitude, user.longitude);
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
		        googleMap.animateCamera(CameraUpdateFactory.zoomTo(8));
			}
			else
			{
				Toast.makeText(getApplicationContext(), user.nickname + " " + getString(R.string.map_disabled), Toast.LENGTH_LONG).show();
			}
			
		}   
	}

	public void switchContents()
	{
		myprofile_active_content.setVisibility(RelativeLayout.VISIBLE);
		
		if(myprofile_active_content == myprofile_about_content)
		{
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			userprofile_map_content.setVisibility(RelativeLayout.GONE);
			userprofile_messages_content.setVisibility(RelativeLayout.GONE);
		}
		else if(myprofile_active_content == myprofile_settings_content)
		{
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			userprofile_map_content.setVisibility(RelativeLayout.GONE);
			userprofile_messages_content.setVisibility(RelativeLayout.GONE);
		}
		else if(myprofile_active_content == myprofile_gallery_content)
		{
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			userprofile_map_content.setVisibility(RelativeLayout.GONE);
			userprofile_messages_content.setVisibility(RelativeLayout.GONE);
		}
		else if(myprofile_active_content == userprofile_map_content)
		{
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			userprofile_messages_content.setVisibility(RelativeLayout.GONE);
		}
		else if(myprofile_active_content == userprofile_messages_content)
		{
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			userprofile_map_content.setVisibility(RelativeLayout.GONE);
			
			scrollToBottom();
		}
	}
	
	public void scrollToBottom()
	{
		scroll_view.post(new Runnable() 
		{
		    @Override
		    public void run() 
		    {
		    	scroll_view.fullScroll(View.FOCUS_DOWN);
		    } 
		});
	}
	
	public void animUp()
	{
		Animation animation = null;
		animation = new TranslateAnimation(0, 0, 0, topContent.getBaseline() - topContent.getHeight() - topContent.getTop());
		animation.setDuration(500);
		animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() 
        {
            public void onAnimationStart(Animation animation) 
            {
            	topContent.setEnabled(false);
            	if(myprofile_active_content == userprofile_messages_content)
            		scrollToBottom();
            	
            }

            public void onAnimationRepeat(Animation animation) 
            {
            }

            public void onAnimationEnd(Animation animation) 
            {
            	topContent.setVisibility(RelativeLayout.GONE);
            }
        });
        
        Animation animation2 = null;
        animation2 = new TranslateAnimation(0, 0, navigation.getBaseline(), (navigation.getBaseline() - navigation.getTop()) + topNav.getHeight()+1);
		animation2.setDuration(500);
        animation2.setFillEnabled(true);
        animation2.setFillAfter(true); 
        animation2.setAnimationListener(new AnimationListener() 
        {
            public void onAnimationStart(Animation animation) 
            {
            	//navigation_bottom_line.setVisibility(RelativeLayout.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) 
            {
            }

            public void onAnimationEnd(Animation animation) 
            {
            	myprofile_active_content.setVisibility(RelativeLayout.VISIBLE);
            	myprofile_rl_rolled.setVisibility(RelativeLayout.VISIBLE);
            	navigation.setVisibility(RelativeLayout.INVISIBLE);
            	//navigation_bottom_line.setVisibility(RelativeLayout.INVISIBLE);
            	
            	if(myprofile_active_content == userprofile_messages_content)
            		scrollToBottom();
            	
            }
        });
        
        topContent.startAnimation(animation);
        navigation.startAnimation(animation2);
	}
	
	public void animDown()
	{
		Animation animation = null;
		animation = new TranslateAnimation(0, 0, topContent.getBaseline() - topContent.getHeight() - topContent.getTop(),0);
		animation.setDuration(500);
		animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setAnimationListener(new AnimationListener() 
        {

            public void onAnimationStart(Animation animation) 
            {
            	topContent.setVisibility(RelativeLayout.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) 
            {
            }

            public void onAnimationEnd(Animation animation) 
            {
            	topContent.setEnabled(true);
            }
        });
        
        Animation animation2 = null;
        animation2 = new TranslateAnimation(0, 0, (navigation.getBaseline() - navigation.getTop()) + topNav.getHeight()+1, navigation.getBaseline() /*+navigation_bottom_line.getHeight()*/);
		animation2.setDuration(500);
        animation2.setFillEnabled(true);
        animation2.setFillAfter(true);
        animation2.setAnimationListener(new AnimationListener() 
        {

            public void onAnimationStart(Animation animation) 
            {
            	navigation_top.setVisibility(RelativeLayout.INVISIBLE);
            	navigation.setVisibility(RelativeLayout.VISIBLE);
            	//navigation_bottom_line.setVisibility(RelativeLayout.VISIBLE);
            }

            public void onAnimationRepeat(Animation animation) 
            {
            }

            public void onAnimationEnd(Animation animation) 
            {
            	myprofile_rl_rolled.setVisibility(RelativeLayout.INVISIBLE);
            	myprofile_active_content.setVisibility(RelativeLayout.GONE);
            	navigation_top.setVisibility(RelativeLayout.VISIBLE);
            	//navigation_bottom_line.setVisibility(RelativeLayout.INVISIBLE);
            	myprofile_active_content = null;
            	enableIntroButtons();
            }
        });
        
        Animation animation3 = null;
        animation3 = new TranslateAnimation(0, 0, myprofile_active_content.getBaseline(), getScreenHeight()-topNav.getHeight()-navigation.getHeight());
		animation3.setDuration(500);
        
		
        topContent.startAnimation(animation);
        navigation.startAnimation(animation2);
        myprofile_active_content.startAnimation(animation3);
	}
	
	public void disableIntroButtons()
	{
		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
	  	pickPhoto.setEnabled(false);
	  	navigation_bottom.setEnabled(false);
	  	navigation_top.setEnabled(true);
	  	
	  	ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				map_content.setVisibility(View.GONE);
				enableIntroButtons();
				animDown();
			}
		});
	  	
	}
	
	public void enableIntroButtons()
	{
		detailBtn1.setImageResource(R.drawable.css_detail_info);
		detailBtnRolled1.setImageResource(R.drawable.css_detail_info);
		
		detailBtn2.setImageResource(R.drawable.css_detail_gallery);
		detailBtnRolled2.setImageResource(R.drawable.css_detail_gallery);
		
		detailBtn3.setImageResource(R.drawable.css_detail_messages);
		detailBtnRolled3.setImageResource(R.drawable.css_detail_messages);
		
		detailBtn4.setImageResource(R.drawable.css_detail_map);
		detailBtnRolled4.setImageResource(R.drawable.css_detail_map);
		
		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
	  	pickPhoto.setEnabled(true);
	  	navigation_bottom.setEnabled(true);
	  	navigation_top.setEnabled(false);
	  	
	  	ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				UserDetailActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				LocalService.chatting = 0;
				UserDetailActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(), IntroActivity.class);
	       		startActivity(mainIntent);
			}
		});
	}
	
	public void setMyProfileGalleryClicks()
	{
		if(SexActivity.otherUser.image.equals("") == true)
		{
			ImageView pickPhotoMain = (ImageView) findViewById(R.id.pickphoto);
			setNoPhotoImage(pickPhotoMain, SexActivity.otherUser.gender);
		}
		
		boolean gotProfileImage = false;
		if(SexActivity.otherUser.image.equals("") == false)
		{
			Integer imageRes = myprofile_gallery_content.getResources().getIdentifier("gallery_image0", "id", this.getPackageName());
			if(imageRes != null)
        	{
				gotProfileImage = true;
				ImageView galleryPhoto = (ImageView) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_image_src);
				ProgressBar galleryLoading = (ProgressBar) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_loading);
				ImageView galleryLock = (ImageView) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_image_lock);
				if(galleryPhoto != null)
        		{
					galleryLock.setVisibility(ImageView.INVISIBLE);
					
					final String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_240 + "/" + SexActivity.otherUser.image;
					
					myprofile_gallery_content.findViewById(imageRes).setVisibility(RelativeLayout.VISIBLE);
					SexActivity.executeTask(new setImageFromUrl(userImageUrl, galleryPhoto, galleryLoading), "load user gallery image");
					
					galleryPhoto.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v) 
						{
							Bundle extras = new Bundle();
							extras.putInt("image_index", -1);
							
							Intent gallery = new Intent(getBaseContext(), GalleryuserActivity.class);
							gallery.putExtras(extras);
							startActivity(gallery);
						}
					});
        		}
        	}
		}
		
		if(SexActivity.otherUser.images.size()>0)
		{
			for(int i=0; i<SexActivity.otherUser.images.size(); i++)
			{
				final int image_index = i;
				
				int increment = (gotProfileImage == false) ? 0 : 1;
				
				Integer imageRes = myprofile_gallery_content.getResources().getIdentifier("gallery_image" + Integer.toString(i+increment), "id", this.getPackageName());
				if(imageRes != null)
	        	{
					ImageView galleryPhoto = (ImageView) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_image_src);
					ProgressBar galleryLoading = (ProgressBar) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_loading);
					ImageView galleryLock = (ImageView) myprofile_gallery_content.findViewById(imageRes).findViewById(R.id.gallery_image_lock);
					if(galleryPhoto != null)
	        		{
						galleryLock.setVisibility(ImageView.INVISIBLE);
						if(SexActivity.otherUser.images_passwords.size()<i || SexActivity.otherUser.images_passwords.get(i) == null || SexActivity.otherUser.images_passwords.get(i).equalsIgnoreCase("") == false)
						{
							//galleryLock.setVisibility(ImageView.VISIBLE);
							myprofile_gallery_content.findViewById(imageRes).setVisibility(RelativeLayout.VISIBLE);
							galleryLoading.setVisibility(ProgressBar.GONE);
							galleryPhoto.setImageResource(R.drawable.css_gallery_lock_large);
						}
						else
						{
							String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_148 + "/" + SexActivity.otherUser.images.get(i);
							
							if(SexActivity.otherUser.images_loaded.get(i) == false)
							{
								SexActivity.otherUser.images_loaded.set(i, true);
								myprofile_gallery_content.findViewById(imageRes).setVisibility(RelativeLayout.VISIBLE);
								SexActivity.executeTask(new setImageFromUrl(userImageUrl, galleryPhoto, galleryLoading), "load user gallery image");
							}
						}
						
						galleryPhoto.setOnClickListener(new View.OnClickListener()
						{
							@Override
							public void onClick(View v) 
							{
								Bundle extras = new Bundle();
								extras.putInt("image_index", image_index);
								
								Intent gallery = new Intent(getBaseContext(), GalleryuserActivity.class);
								gallery.putExtras(extras);
								startActivity(gallery);
							}
						});
	        		}
	        	}
			}
		}
		
		if(SexActivity.otherUser.images.size() < SexActivity.otherUser.maxGalleryImages)
		{
			for(int i = SexActivity.otherUser.images.size()+1 ; i<=SexActivity.otherUser.maxGalleryImages; i++)
			{
				Integer imageRes = myprofile_gallery_content.getResources().getIdentifier("gallery_image" + Integer.toString(i), "id", this.getPackageName());
				if(imageRes != null)
					myprofile_gallery_content.findViewById(imageRes).setVisibility(RelativeLayout.INVISIBLE);
				
			}
		}
	}

	public void setMyProfileAboutClicks()
	{
		//describe
		final EditText aboutInput = (EditText) myprofile_about_content.findViewById(R.id.myprofile_aboutme_input);
		if(SexActivity.otherUser.describe != null && SexActivity.otherUser.describe.equalsIgnoreCase("") == false)
		{
			aboutInput.setText(SexActivity.otherUser.describe);
		}
		
		//body
		if(SexActivity.otherUser.body != 0)
		{
			setBodyValue(SexActivity.otherUser.body);
		}
		
		//ethnicity 
		if(SexActivity.otherUser.ethnicity != 0)
		{
			setEthnicityValue(SexActivity.otherUser.ethnicity);
		}
		
		//loves
		setLovesValue(SexActivity.otherUser.loves);
	}
	
	public void setUserData()
	{
		//describe
		if(SexActivity.otherUser.describe != null && SexActivity.otherUser.describe.equalsIgnoreCase("") == false)
		{
			TextView describe = (TextView) myprofile_settings_content.findViewById(R.id.other_user_description);
			describe.setText(SexActivity.otherUser.describe);
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_description_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//age
		if(SexActivity.otherUser.age != 0)
		{
			TextView age = (TextView) myprofile_settings_content.findViewById(R.id.other_user_age);
			age.setText(SexActivity.otherUser.age + " " + getString(R.string.other_user_age_years));
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_age_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//gender
		if(SexActivity.otherUser.gender != 0)
		{
			TextView gender = (TextView) myprofile_settings_content.findViewById(R.id.other_user_gender);
			gender.setText(getGenderValue(SexActivity.otherUser.gender));
			
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_gender_rl);
			ImageView icon = (ImageView) rl.findViewById(R.id.icon_gender);
			
			if(SexActivity.otherUser.gender == SexActivity.attributesGenderMan)
				icon.setImageResource(R.drawable.otheruser_icon_gender_man);
			else if(SexActivity.otherUser.gender == SexActivity.attributesGenderWoman)
				icon.setImageResource(R.drawable.otheruser_icon_gender_woman);
			else if(SexActivity.otherUser.gender == SexActivity.attributesGenderPairStraight)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_straight);
			else if(SexActivity.otherUser.gender == SexActivity.attributesGenderPairLesbian)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_lesbian);
			else if(SexActivity.otherUser.gender == SexActivity.attributesGenderPairGay)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_gay);
			
			icon.refreshDrawableState();
		}
		else 
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_gender_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//looking for
		if(SexActivity.otherUser.iwant != 0)
		{
			TextView lookingfor = (TextView) myprofile_settings_content.findViewById(R.id.other_user_lookingfor);
			lookingfor.setText(getGenderValue(SexActivity.otherUser.iwant));
			
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_lookingfor_rl);
			ImageView icon = (ImageView) rl.findViewById(R.id.icon_lookingfor);
			
			if(SexActivity.otherUser.iwant == SexActivity.attributesGenderMan)
				icon.setImageResource(R.drawable.otheruser_icon_gender_man);
			else if(SexActivity.otherUser.iwant == SexActivity.attributesGenderWoman)
				icon.setImageResource(R.drawable.otheruser_icon_gender_woman);
			else if(SexActivity.otherUser.iwant == SexActivity.attributesGenderPairStraight)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_straight);
			else if(SexActivity.otherUser.iwant == SexActivity.attributesGenderPairLesbian)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_lesbian);
			else if(SexActivity.otherUser.iwant == SexActivity.attributesGenderPairGay)
				icon.setImageResource(R.drawable.otheruser_icon_gender_pair_gay);
			
			icon.refreshDrawableState();
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_lookingfor_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//loves
		if(SexActivity.otherUser.loves != 0)
		{
			TextView loves = (TextView) myprofile_settings_content.findViewById(R.id.other_user_loves);
			loves.setText(getLovesValue(SexActivity.otherUser.loves));
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_loves_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//body
		if(SexActivity.otherUser.body != 0)
		{
			TextView body = (TextView) myprofile_settings_content.findViewById(R.id.other_user_body);
			body.setText(getBodyValue(SexActivity.otherUser.body));
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_body_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//ethnicity
		if(SexActivity.otherUser.ethnicity != 0)
		{
			TextView ethnicity = (TextView) myprofile_settings_content.findViewById(R.id.other_user_ethnicity);
			ethnicity.setText(getEthnicityValue(SexActivity.otherUser.ethnicity));
		}
		else
		{
			RelativeLayout rl = (RelativeLayout) myprofile_settings_content.findViewById(R.id.other_user_ethnicity_rl);
			rl.setVisibility(RelativeLayout.GONE);
		}
		
		//block user
		/*
		TextView blockTxt = (TextView) myprofile_settings_content.findViewById(R.id.report_block);
		ImageView blockImg = (ImageView) myprofile_settings_content.findViewById(R.id.report_block_icon);
		if(SexActivity.otherUser.blockedToMe == 1)
		{
			blockTxt.setText(getString(R.string.other_user_blacklist_remove));
		}
		
		blockTxt.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SexActivity.executeTask(new blockAndUnblockUser(SexActivity.otherUser.id), "block or unblock user");
			}
		});
		blockImg.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				SexActivity.executeTask(new blockAndUnblockUser(SexActivity.otherUser.id), "block or unblock user");
			}
		});
		*/
		//reportUser
		TextView reportTxt = (TextView) myprofile_settings_content.findViewById(R.id.report_report);
		//ImageView reportImg = (ImageView) myprofile_settings_content.findViewById(R.id.report_report_icon);
		reportTxt.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showReportDialog();
			}
		});
		/*
		reportImg.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				showReportDialog();
			}
		});
		*/
	}
	
	public void showReportDialog()
	{
		final Dialog reportDialog = new Dialog(this);
		reportDialog.setContentView(R.layout.dialog_report);
		reportDialog.setTitle(getString(R.string.other_user_report));
	  	
	  	Button reportConfirm = (Button) reportDialog.findViewById(R.id.report_confirm);
	  	reportConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				String reason = "";
			  	RadioGroup reportReason = (RadioGroup) reportDialog.findViewById(R.id.report_input);
			  	int res = reportReason.getCheckedRadioButtonId();
			  	if(res == R.id.report_abuse)
			  	{
			  		reason = "report_abuse";
			  	}
			  	else if(res == R.id.report_fake)
			  	{
			  		reason = "report_fake";
			  	}
			  	if(res == R.id.report_abuse)
			  	{
			  		reason = "report_spam";
			  	}
			  	if(res == R.id.report_inappropriate)
			  	{
			  		reason = "report_inappropriate";
			  	}
			  	/*
				switch(reportReason.getCheckedRadioButtonId())
				{
					case R.id.report_abuse:
						reason = "report_abuse";
						break;
					case R.id.report_fake:
						reason = "report_fake";
						break;
					case R.id.report_spam:
						reason = "report_spam";
						break;
					case R.id.report_inappropriate:
						reason = "report_inappropriate";
						break;
						
				}
				*/
				if(reason.equals("") == false)
				{
					SexActivity.executeTask(new reportUser(SexActivity.otherUser.id, reason), "report user");
				}
				try
	    		{
	    			if(isFinishing() == false)
	    				reportDialog.cancel();
	    		}
	    		catch(Exception e)
	    		{
	    		}
			}	
		});
	  	
	  	Button reportCancel = (Button) reportDialog.findViewById(R.id.report_cancel);
	  	reportCancel.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				try
	    		{
	    			if(isFinishing() == false)
	    				reportDialog.cancel();
	    		}
	    		catch(Exception e)
	    		{
	    		}
			}
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				reportDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	public void changeBlockText()
	{
		/*
		TextView blockTxt = (TextView) myprofile_settings_content.findViewById(R.id.report_block);
		if(SexActivity.otherUser.blockedToMe == 1)
			blockTxt.setText(getString(R.string.other_user_blacklist_remove));
		else
			blockTxt.setText(getString(R.string.other_user_blacklist));
		*/
	}
	
	public void changePetText()
	{
		Button useraction_pets = (Button) findViewById(R.id.useraction_pets);
		if(SexActivity.otherUser.myPet == 1)
		{
			useraction_pets.setBackgroundResource(R.drawable.useraction_pets_hover);
			Toast.makeText(getApplicationContext(), SexActivity.otherUser.nickname + " " + getString(R.string.useraction_pets_in_toast), Toast.LENGTH_SHORT).show();
		}
		else
		{
			useraction_pets.setBackgroundResource(R.drawable.useraction_pets);
			Toast.makeText(getApplicationContext(), SexActivity.otherUser.nickname + " " + getString(R.string.useraction_pets_out_toast), Toast.LENGTH_SHORT).show();
		}		
		useraction_pets.refreshDrawableState();
	}
	
	public String getEthnicityValue(int value)
	{
		String returnStr = "";
		if(value != 0)
		{
			if(value == SexActivity.attributesEthnicityAsian)
	  		{
				returnStr = getString(R.string.ethnicity_asian);
	  		}
			else if(value == SexActivity.attributesEthnicityBlack)
	  		{
				returnStr = getString(R.string.ethnicity_black);
	  		}
			else if(value == SexActivity.attributesEthnicityOther)
	  		{
				returnStr = getString(R.string.ethnicity_other);
	  		}
			else if(value == SexActivity.attributesEthnicityLatino)
	  		{
				returnStr = getString(R.string.ethnicity_latino);
	  		}
			else if(value == SexActivity.attributesEthnicityWhite)
	  		{
				returnStr = getString(R.string.ethnicity_white);
	  		}
		}
		
		return returnStr;
	}
	
	public void setEthnicityValue(int value)
	{
		if(value != 0)
		{
			Button ethnicityInput = (Button) myprofile_about_content.findViewById(R.id.myprofile_looks_ethnicity_input);
			ethnicityInput.setText(getString(R.string.myprofile_looks_ethnicity_hint) + ": " + getEthnicityValue(value));
		}
	}
	
	public String getGenderValue(int value)
	{
		String returnStr = "";
		
		if(value != 0)
		{
			if(value == SexActivity.attributesGenderMan)
	  		{
				returnStr = getString(R.string.gender_man);
	  		}
	  		else if(value == SexActivity.attributesGenderWoman)
	  		{
	  			returnStr = getString(R.string.gender_woman);
	  		}	
	  		/*else if(value == SexActivity.attributesGenderPairStraight)
	  		{
	  			returnStr = getString(R.string.gender_pair_straight);
	  		}
	  		else if(value == SexActivity.attributesGenderPairLesbian)
	  		{
	  			returnStr = getString(R.string.gender_pair_lesbian);
	  		}
	  		else if(value == SexActivity.attributesGenderPairGay)
	  		{
	  			returnStr = getString(R.string.gender_pair_gay);
	  		}*/
		}
		
		return returnStr;
	}
	
	public void setGenderValue(int value, String part)
	{
		if(value != 0)
		{
			Button genderInput = null;
			if(part.equalsIgnoreCase("gender"))
				genderInput = (Button) myprofile_settings_content.findViewById(R.id.myprofile_gender_input);
			else
				genderInput = (Button) myprofile_settings_content.findViewById(R.id.myprofile_looking_for_input);
		
			String gender = getGenderValue(value);
			genderInput.setText(gender);
		}
	}
	
	public String getBodyValue(int value)
	{
		String returnStr = "";
		
		if(value == SexActivity.attributesBodySexy)
  		{
			returnStr = getString(R.string.body_sexy);
  		}
  		else if(value == SexActivity.attributesBodySporty)
  		{
  			returnStr = getString(R.string.body_sporty);
  		}	
  		else if(value == SexActivity.attributesBodySlender)
  		{
  			returnStr = getString(R.string.body_slender);
  		}
  		else if(value == SexActivity.attributesBodyJuicy)
  		{
  			returnStr = getString(R.string.body_juicy);
  		}
  		else if(value == SexActivity.attributesBodyMuscular)
  		{
  			returnStr = getString(R.string.body_muscle);
  		}
		return returnStr;
	}
	
	public void setBodyValue(int value)
	{
		Button bodyInput = (Button) myprofile_about_content.findViewById(R.id.myprofile_looks_body_input);
		bodyInput.setText(getBodyValue(value));
	}
	
	public String getLovesValue(int value)
	{
		String returnStr = "";
		/*
		if((SexActivity.attributesLovesMissionary & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_missionary);
		}
		
		if((SexActivity.attributesLoves69 & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_69);
		}
		
		if((SexActivity.attributesLovesDoggie & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_doggie);
		}
		
		if((SexActivity.attributesLovesBJ & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_bj);
		}
		
		if((SexActivity.attributesLovesPiss & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_piss);
		}
		
		if((SexActivity.attributesLovesSM & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_sm);
		}
		
		if((SexActivity.attributesLovesOrgy & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_orgy);
		}
		
		if((SexActivity.attributesLovesFetish & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_fetish);
		}
		
		if((SexActivity.attributesLovesHJ & value) != 0)
		{
			returnStr += ((returnStr.equalsIgnoreCase("")==false)? ", " : "") + getString(R.string.loves_hj);
		}
		*/
		return returnStr;
		
	}
	
	public void setLovesValue(int value)
	{
		String inputValue = getString(R.string.myprofile_what_ilove_hint);
		if(value != 0)
		{
			inputValue += getLovesValue(value);
		}
		
		Button lovesInput = (Button) myprofile_about_content.findViewById(R.id.myprofile_ilove_input);
		lovesInput.setText(inputValue);
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
	public int setNoPhotoImage(ImageView imageView, int gender)
	{
		int drawable = 0;
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
		
		if(drawable != 0)
			imageView.setImageResource(drawable);
		
		return drawable;
	}
	
	public void toastReportMessage()
	{
		Toast.makeText(getApplicationContext(), SexActivity.otherUser.nickname + " " + getString(R.string.report_toast), Toast.LENGTH_SHORT).show();
	}
	
	class loadOldMessages extends AsyncTask<String, Integer, String>
	{
		String user = "";
		int offset = 1;
		public loadOldMessages(String pUser, int pOffset)
		{
			user = pUser;
			offset = pOffset;
		}
		
		protected void onPostExecute(String result)
	    {
			hideLoadingDialog();
			userprofile_messages_content.setVisibility(View.VISIBLE);
			updateNewMessages(userprofile_messages_content, costForResponse);
			showOlderMessages(offset);
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpLoadOlderMessages(user, offset);
			return "ok";
		}

		@Override
		protected void onPreExecute() {
			showLoadingDialog();
		}
	}
	
	class reportUser extends AsyncTask<String, Integer, String>
	{
		int user = 0;
		String reason = "";
		public reportUser(int pUser, String pReason)
		{
			user = pUser;
			reason = pReason;
		}
		
		protected void onPostExecute(String result)
	    {
			toastReportMessage();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpReportUser(user, reason);
			return "ok";
		}
	}
	
	class blockAndUnblockUser extends AsyncTask<String, Integer, String>
	{
		int user;
		public blockAndUnblockUser(int pUser)
		{
			user = pUser;
		}
		
		protected void onPostExecute(String result)
	    {
			changeBlockText();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpBlockAndUnblock(user);
			return "ok";
		}
	}
	
	class setKissOrSlap extends AsyncTask<String, Integer, String>
	{
		int user;
		String action;
		
		public setKissOrSlap(int pUser, String pAction)
		{
			user = pUser;
			action = pAction;
		}
		
		protected void onPostExecute(String result)
	    {
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpKissOrSlap(user, action);
			return "ok";
		}
	}
	
	class petUser extends AsyncTask<String, Integer, String>
	{
		int user;
		public petUser(int pUser)
		{
			user = pUser;
		}
		
		protected void onPostExecute(String result)
	    {
			changePetText();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpPetUnPet(user);
			return "ok";
		}
	}
	
	
	class newMessage extends AsyncTask<String, Integer, String>
	{
		String toUser = "";
		String message = "";
		
		public newMessage(String pToUser, String pMessage)
		{
			super();
			toUser = pToUser + "@pike-apps.com";
			message = pMessage;
		}
		
		@Override
		protected String doInBackground(String... params) 
		{
			try
			{
				Message msg = new Message(toUser, Message.Type.chat);
				msg.setBody(message);
				connection.sendPacket(msg);
			}
			catch(Exception e)
			{
				
			}
			return null;
		}
	}
	
	class postMessageSync extends AsyncTask<String, Integer, Boolean>
	{
		EditText sendmessage_text = null;
		String toUser = "";
		String message = "";
		public String lastError = "";
		
		public postMessageSync(String pToUser, EditText sendMsg)
		{
			super();
			toUser = pToUser;
			message = sendMsg.getText().toString();
		
			sendmessage_text = sendMsg;
		}
			

		@Override
		protected void onPostExecute(Boolean result)
	    {			
			if(result == true)
			{
				showMessage(true, sendmessage_text.getText().toString(), 0, "now");
			    sendmessage_text.setText("");

			    // more from now, its free...
			    costForResponse = 0;
				updateNewMessages(userprofile_messages_content, costForResponse);				
			}
			else 
			{
				Toast.makeText(getApplicationContext(), lastError, Toast.LENGTH_SHORT).show();
			}
	    }

		@Override
		protected Boolean doInBackground(String... params) 
		{
			return saveMessage(this, toUser, message);
		}
	}
	
	
	class xmppInit extends AsyncTask<String, Integer, Boolean>
	{
		protected void onPostExecute(Boolean result)
	    {
			if(result == true)
				setMessagesListener();
			
	    }
		
		@Override
		protected Boolean doInBackground(String... params) 
		{
			return setupXmpp();
		}
	}
	
	
	public void setMessagesListener()
	{
		PacketFilter filter = new AndFilter(new PacketTypeFilter(Message.class), new FromContainsFilter(String.valueOf(SexActivity.otherUser.id) + "@pike-apps.com"));
		//PacketFilter filter2 = new AndFilter(new PacketTypeFilter(Message.class), new NotFilter(filter));
		
		userPacketListener = new PacketListener() 
		{
	        public void processPacket(Packet packet) 
	        {
	        	final Message message = (Message) packet;
                if(message.getBody() != null)  
                { 
                    //String fromName = StringUtils.parseBareAddress(message.getFrom());
                    //messages.add(message.getBody());
                    runOnUiThread(new Runnable() 
                    {                  
                        @Override
                        public void run() 
                        {
                           showMessage(false, message.getBody(), 0, "now");
                        }
                    });
                }
	        }
	    };
	    
	    connection.addPacketListener(userPacketListener, filter);
	    
	}
	
	public void showOlderMessages(final int offset)
	{
		int startIndex = Math.min(olderMessages.length(), maxMessagesPerLoad);
		if(offset <= 1)
		{
			for(int i=startIndex; i>=0; --i)
			{
				JSONObject temp = null;
				try 
				{
					temp = olderMessages.getJSONObject(i);
					showMessage(((temp.getInt("user_from") == SexActivity.me.id) ? true : false), temp.getString("message"), offset, temp.getString("date"));
				}
				catch (JSONException e) 
				{
				}
			}
		}
		else
		{
			for(int i=0; i<startIndex; i++)
			{
				JSONObject temp = null;
				try 
				{
					temp = olderMessages.getJSONObject(i);
					showMessage(((temp.getInt("user_from") == SexActivity.me.id) ? true : false), temp.getString("message"), offset, temp.getString("date"));
				}
				catch (JSONException e) 
				{
				}
			}
		}
		
		if(olderMessages.length() > maxMessagesPerLoad)
		{
			View showMore = (View) inflater.inflate(R.layout.include_users_showmore, null);
			final Button showMoreBtn = (Button) showMore.findViewById(R.id.showmore_btn);
			showMoreBtn.setText(getString(R.string.showolder_messages));
			showMoreBtn.setOnClickListener(new View.OnClickListener() 
			{
				@Override
				public void onClick(View v) 
				{
					showMoreBtn.setVisibility(Button.GONE);
					SexActivity.executeTask(new loadOldMessages(SexActivity.otherUser.session, offset+1));
				}
			});
			
			linear_layout.addView(showMore, 0);
		}
	}
	
	public void showMessage(boolean from_me, String message, int offset, String datetime)
	{
		View line = null;
		if(from_me == true)
			line = (View) inflater.inflate(R.layout.include_message_me, null);
		else
			line = (View) inflater.inflate(R.layout.include_message_user, null);
		
		ImageView user_image_src = (ImageView) line.findViewById(R.id.user_image_src);
		ProgressBar user_loading = (ProgressBar) line.findViewById(R.id.gallery_loading);
		
		if(from_me == true)
		{
			if(meBitmap == null && meDrawable == null)
			{
				if(SexActivity.me.image != null && SexActivity.me.image.equalsIgnoreCase("") == false)
				{
					user_loading.setVisibility(ProgressBar.VISIBLE);
					
					//set user image
					String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_60 + "/" + SexActivity.me.image;
					SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1, true), "load user image");
				}
				else
				{
					user_loading.setVisibility(ProgressBar.GONE);
					meDrawable = setNoPhotoImage(user_image_src, SexActivity.me.gender);
				}
			}
			else 
			{
				user_loading.setVisibility(ProgressBar.GONE);
				if(meDrawable != null)
					user_image_src.setImageResource(meDrawable);
				else
					user_image_src.setImageBitmap(meBitmap);
				
				user_image_src.refreshDrawableState();
			}
		}
		else
		{
			if(otherBitmap == null && otherDrawable == null)
			{
				if(SexActivity.otherUser.image != null && SexActivity.otherUser.image.equalsIgnoreCase("") == false)
				{
					user_loading.setVisibility(ProgressBar.VISIBLE);
					
					//set user image
					String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_60 + "/" + SexActivity.otherUser.image;
					SexActivity.executeTask(new setImageFromUrl(userImageUrl, user_image_src, user_loading, 1, false), "load user image");
				}
				else
				{
					user_loading.setVisibility(ProgressBar.GONE);
					otherDrawable = setNoPhotoImage(user_image_src, SexActivity.otherUser.gender);
				}
			}
			else
			{
				user_loading.setVisibility(ProgressBar.GONE);
				if(otherDrawable != null)
					user_image_src.setImageResource(otherDrawable);
				else
					user_image_src.setImageBitmap(otherBitmap);
				
				user_image_src.refreshDrawableState();
			}
		}
		
		TextView messageBody = (TextView) line.findViewById(R.id.message_body);
		messageBody.setText(message);
		 
		TextView messageDate = (TextView) line.findViewById(R.id.message_thread_date);
		
		if(datetime.equals("now") == false)
			messageDate.setText(datetime);
		else
			messageDate.setText(getNow());
		
		
		try
		{
			if(offset <= 1)
			{
				linear_layout.addView(line);
				scrollToBottom();
			}
			else
				linear_layout.addView(line, 0);
			
			
		}
		catch(Exception e)
		{
		}
	}
	
	public String getNow()
	{
		ArrayList<String> months = new ArrayList<String>();
		months.add("January");
		months.add("February");
		months.add("March");
		months.add("April");
		months.add("May");
		months.add("June");
		months.add("July");  
		months.add("August");
		months.add("September");
		months.add("October");
		months.add("November");
		months.add("December");
		
		Calendar c = Calendar.getInstance();
		int hours = c.get(Calendar.HOUR_OF_DAY);
		int minutes = c.get(Calendar.MINUTE);
		int day = c.get(Calendar.DAY_OF_MONTH);
		int month = c.get(Calendar.MONTH);
		
		return String.valueOf(hours) + ":" + ((String.valueOf(minutes).length()==1) ? "0" : "") + String.valueOf(minutes) + " " + months.get(month) + " " + String.valueOf(day);
		
	}
	
	public boolean setupXmpp()
	{
		prepareUser(SexActivity.otherUser.session);
		
		SASLAuthentication.supportSASLMechanism("PLAIN");
		ConnectionConfiguration config = new ConnectionConfiguration("pike-apps.com", 5222, "pike-apps.com");
		config.setSASLAuthenticationEnabled(false);
		config.setSelfSignedCertificateEnabled(true);
		config.setSendPresence(true); 
		
		connection = new XMPPConnection(config);
	    try 
	    {
	    	connection.connect();
			if(connection.isConnected() == true)
			{
				connection.login(String.valueOf(SexActivity.me.id), SexActivity.me.session);
				
				if(connection.isAuthenticated())
				{
					Presence presence = new Presence(Presence.Type.available);
					connection.sendPacket(presence);
		            
					return true;
				}
			}
		}
	    catch (XMPPException e1) 
		{
		}
	    
	    return false;
	}
	
	
	public void prepareUser(String someUser)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "xmpp_register_user"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(someUser)));
	        
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
	
	
	public Boolean saveMessage(postMessageSync instance, String pUser, String pMessage)
	{
		instance.lastError = "";
		
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "save_msg"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("to", pUser));
	        nameValuePairs.add(new BasicNameValuePair("msg", pMessage));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);

				if (json_data.opt("credits") != null) 
				{
					SexActivity.me.credits = json_data.getInt("credits");
				}
				
				if (json_data.opt("error") != null) 
				{
					instance.lastError = json_data.getString("error");
					return false;
				}
				
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
        	instance.lastError = e.getMessage();
        } 
		catch(Exception e)
		{
        	instance.lastError = e.getMessage();
		}
		
		return false;
	}
	
	
	class getUserData extends AsyncTask<String, Integer, String>
	{
		int id;
		public getUserData(int pId)
		{
			id = pId;
		}
		
		protected void onPostExecute(String result)
	    {
			hideLoadingDialog();
			proceedUserProfile();
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			httpGetOtherUserData(id);
			return "ok";
		}
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
	
	
	class setImageFromUrl extends AsyncTask<String, Integer, Bitmap>
	{
		String imageUrl = "";
		ImageView imageView = null;
		ProgressBar imageLoading = null; 
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		Animation animate = null;
		String crop = "";
		Boolean myBitmap = null;
		
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
		
		public setImageFromUrl(String url, ImageView view)
		{
			super();
			imageUrl = url;
			imageView = view;
			bmOptions.inSampleSize = 1;
		}
		
		public setImageFromUrl(BitmapFactory.Options pOption, String url, ImageView view)
		{
			super();
			imageUrl = url;
			imageView = view;
			bmOptions = pOption;
		}
		
	    public setImageFromUrl(Animation animation, Options pOption, String url, ImageView view) 
	    {
	    	super();
			imageUrl = url;
			imageView = view;
			bmOptions = pOption;
			animate = animation;
		}

		public setImageFromUrl(String userImageUrl, ImageView createProfile,String string) 
		{
			super();
			imageUrl = userImageUrl;
			imageView = createProfile;
			crop = string;
		}

		public setImageFromUrl(String url, ImageView view, ProgressBar loading, int sampleSize, boolean pMe) 
		{
			super();
			imageUrl = url;
			imageView = view;
			imageLoading = loading;
			bmOptions.inSampleSize = sampleSize;
			myBitmap = pMe;
		}

		protected void onPostExecute(Bitmap result)
	    {
	    	if(!isCancelled())
	    	{
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
		    			        paint.setShader(shader);

		    			Canvas c = new Canvas(circleBitmap);
		    			c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);

		    			imageView.setImageBitmap(circleBitmap);
		    		}
		    		else
		    		{
		    			imageView.setImageBitmap(result);
		    		}
		    		
		    		imageView.refreshDrawableState();
		    		imageView.setVisibility(ImageView.VISIBLE);
		    		
		    		if(myBitmap != null)
		    		{
		    			if(myBitmap == true)
		    				meBitmap = result;
		    			else
		    				otherBitmap = result;
		    			
		    		}	
		    		
		    		
		    		if(animate != null)
		    			imageView.startAnimation(animate);
		    		
			    }
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
	
	
	public void httpKissOrSlap(int pUser, String pAction)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", pAction));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(pUser)));
	        
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
	
	public void httpBlockAndUnblock(int pUser)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", (SexActivity.otherUser.blockedToMe == 1) ? "unblock_user" : "block_user"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(pUser)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1) 
				{
					if(SexActivity.otherUser.blockedToMe == 1)
						SexActivity.otherUser.blockedToMe = 0;
					else
						SexActivity.otherUser.blockedToMe = 1;
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
	
	public void httpPetUnPet(int pUser)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "pet_user"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(pUser)));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1) 
				{
					if(SexActivity.otherUser.myPet == 1)
						SexActivity.otherUser.myPet = 0;
					else
						SexActivity.otherUser.myPet = 1;
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
	
	public void httpLoadOlderMessages(String pUser, int pOffset)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "chat_thread"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("with", String.valueOf(pUser)));
	        nameValuePairs.add(new BasicNameValuePair("offset", String.valueOf(pOffset)));
	        nameValuePairs.add(new BasicNameValuePair("timezone", SexActivity.getTz())); 
	        
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				
				if(json_data.get("messages") != null && json_data.getJSONArray("messages").length()>0) 
				{
					olderMessages = json_data.getJSONArray("messages");
				}
				
				if (json_data.opt("cost") != null) {
					costForResponse = json_data.getInt("cost");
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
	
	public void httpReportUser(int pUser, String pReason)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "report_user"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(pUser)));
	        nameValuePairs.add(new BasicNameValuePair("reason", pReason));
	        
     		HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1) 
				{
					reported = true;
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
					SexActivity.otherUser = SexActivity.getOtherUserFromJsonObj(jUser);
					
					String country = getResources().getConfiguration().locale.getISO3Country();
					SexActivity.otherUser.metric = "metric";
			       	if(country.equalsIgnoreCase("USA") == true || country.equalsIgnoreCase("GBR") == true || country.equalsIgnoreCase("UK") == true)
			       		SexActivity.otherUser.metric = "imperial";
			       	
			       	if(jUser.opt("gps") != null && jUser.getString("gps").equals("") == false)
			       	{
			       		SexActivity.otherUser.latitude = jUser.getJSONObject("gps").getDouble("latitude");
			       		SexActivity.otherUser.longitude = jUser.getJSONObject("gps").getDouble("longitude");
			       	}
			       	
			       	if(jUser.opt("blocked") != null)
			       	{
			       		SexActivity.otherUser.blockedToMe = jUser.getInt("blocked");
			       	}
			       	
			       	if(jUser.opt("pets") != null)
			       	{
			       		SexActivity.otherUser.myPet = jUser.getInt("pets");
			       	}
				}
				else
				{
					LocalService.chatting = 0;
					UserDetailActivity.this.finish();
				}
			}
			else
			{
				LocalService.chatting = 0;
				UserDetailActivity.this.finish();
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
}