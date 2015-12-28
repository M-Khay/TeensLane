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
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GalleryuserActivity extends SexActivity implements OnClickListener
{
	static final int SWIPE_MIN_DISTANCE = 120;
    static final int SWIPE_MAX_OFF_PATH = 250;
    static final int SWIPE_THRESHOLD_VELOCITY = 70;
    boolean profile_image = false;
    
    GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    
    RelativeLayout gallery_rl_header;
	Button gallery_marker_wtf;
	Button gallery_marker_hot;
	ImageView fullscreenImage;
	Integer image_index;
	
	public ArrayList<String> known_passwords = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_galleryuser);
		
		SexActivity.reloadGallery = true;
		
		Bundle extras = getIntent().getExtras();
		image_index = extras.getInt("image_index");
		
		setupButtons();
		
		String image = "";
		if(image_index == -1)
		{
			profile_image = true;
			image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.otherUser.image;
		}
		else
		{
			profile_image = false;
			image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.otherUser.images.get(image_index);
		}
		
		gallery_rl_header = (RelativeLayout) findViewById(R.id.gallery_rl_header);
		gallery_marker_wtf = (Button) findViewById(R.id.gallery_marker_wtf);
		gallery_marker_hot = (Button) findViewById(R.id.gallery_marker_hot);
		
		fullscreenImage = (ImageView) findViewById(R.id.gallery_fullscreen_image);
		
		// Gesture detection
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() 
        {
            public boolean onTouch(View v, MotionEvent event) 
            {
                return gestureDetector.onTouchEvent(event);
            }
        };
		
		if(image != null && image.equalsIgnoreCase("") == false)
		{
			loadImage(image, image_index);
		}
	}

	public void loadImage(String image, Integer image_index)
	{
		TextView heading = (TextView) findViewById(R.id.header_heading);
		heading.setText(getString(R.string.teenslane_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.otherUser.images.size()) + ")");
		
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		
		if(profile_image == false)
		{
			if(SexActivity.otherUser.images_passwords.size()<image_index || SexActivity.otherUser.images_passwords.get(image_index) == null || SexActivity.otherUser.images_passwords.get(image_index).equalsIgnoreCase("") == false)
			{
				if(known_passwords.size()>image_index && known_passwords.get(image_index) != null && known_passwords.get(image_index).equals(SexActivity.otherUser.images_passwords.get(image_index)) == true)
				{
					fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
					SexActivity.executeTask(new setImageFromUrl(bmOptions, image, fullscreenImage), "load user fullscreen image");
				}
				else
				{
					drawPasswordDialog(image, image_index);
					fullscreenImage.setVisibility(ImageView.INVISIBLE);
					fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
					SexActivity.executeTask(new setImageFromUrl(bmOptions, image, fullscreenImage, false), "load user fullscreen image");
				}
			}
			else
			{
				fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
				SexActivity.executeTask(new setImageFromUrl(bmOptions, image, fullscreenImage), "load user fullscreen image");
			}
		}
		else 
		{
			fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
			SexActivity.executeTask(new setImageFromUrl(bmOptions, image, fullscreenImage), "load user fullscreen image");
		}
		
		fullscreenImage.setOnTouchListener(gestureListener);
		fullscreenImage.setOnClickListener(GalleryuserActivity.this); 
		
		fullscreenImage.setOnLongClickListener(new View.OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				if(gallery_rl_header.getVisibility() == RelativeLayout.VISIBLE)
				{
					gallery_rl_header.setVisibility(RelativeLayout.GONE);
					gallery_marker_wtf.setVisibility(Button.GONE);
					gallery_marker_hot.setVisibility(Button.GONE);
				}
				else
				{
					gallery_rl_header.setVisibility(RelativeLayout.VISIBLE);
					gallery_marker_wtf.setVisibility(Button.VISIBLE);
					gallery_marker_hot.setVisibility(Button.VISIBLE);
				}
				return false;
			}
		});
		
		reloadButtons(image_index);
	}
	
	public void reloadButtons(final int image_index)
	{
		if(image_index>=0 && SexActivity.otherUser.images_pure.size()>image_index)
		{
			gallery_marker_hot.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					SexActivity.executeTask(new rateImage(image_index, "hot", false), "rate image hot");
				}
			});
			
			gallery_marker_wtf.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					SexActivity.executeTask(new rateImage(image_index, "wtf", false), "rate image hot");
				}
			});
		}
		else
		{
			gallery_marker_hot.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					SexActivity.executeTask(new rateImage(0, "hot", true), "rate image hot");
				}
			});
			
			gallery_marker_wtf.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v) 
				{
					SexActivity.executeTask(new rateImage(0, "wtf", true), "rate image hot");
				}
			});
		}
	}
	
	class rateImage extends AsyncTask<String, Integer, String>
	{
		Integer imageIndex = null;
		String action = "";
		boolean profile = false;
		
		public rateImage(int pImageIndex, String pAction, boolean pProfile)
		{
			imageIndex = pImageIndex;
			action = pAction;
			profile = pProfile;
		}
		
		protected void onPostExecute(String result)
	    {
			if(result != null)
			{
				if(result.equals("success"))
				{
					if(action == "hot")
						Toast.makeText(getApplicationContext(), getString(R.string.rate_success) + " " + getString(R.string.feed_rate_hot), Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(getApplicationContext(), getString(R.string.rate_success) + " " + getString(R.string.feed_rate_wtf), Toast.LENGTH_SHORT).show();
					
				}
				else
					Toast.makeText(getApplicationContext(), getString(R.string.rate_voted_already), Toast.LENGTH_SHORT).show();
				
			}
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			return httpRateImage(imageIndex, action, profile);
		}
	}
	
	public String httpRateImage(int imageIndex, String action, boolean profile)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "rate_image_" + action));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("some_user", String.valueOf(SexActivity.otherUser.id)));
	        nameValuePairs.add(new BasicNameValuePair("image", ((profile == false) ? SexActivity.otherUser.images_pure.get(imageIndex).getString("id") : "0")));
	        
	        HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			
			String json = EntityUtils.toString(response.getEntity());
			
			if(json.equalsIgnoreCase("") == false)
			{
				JSONObject json_data = new JSONObject(json);
				if(json_data.getInt("success") == 1)
					return "success";
				else
					return "already_voted";
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
		return "already_voted";
	}
	
	public void setupButtons()
	{
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				GalleryuserActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				GalleryuserActivity.this.finish();
				SexActivity.menuReturn = true;
			}
		});
	}
	
	class MyGestureDetector extends SimpleOnGestureListener 
	{
		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) 
        {
        	try 
            {
                if(Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                	return false;
                
                final int fullscreenWidth = fullscreenImage.getWidth();///2;
                final int constWidth = fullscreenWidth;//fullscreenImage.getWidth();
                int leftOffset = fullscreenImage.getLeft();
                
                if(leftOffset!=0)
                {
                	if(leftOffset>0)
                		leftOffset = leftOffset-constWidth;
                	else
                		leftOffset = leftOffset+constWidth;
                }
                
                final int constLeft = leftOffset;
                
                // right to left swipe
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) 
                {
                	 Animation animation2 = new TranslateAnimation(constLeft, constLeft-constWidth, 0, 0);
                     AlphaAnimation alphaAmin = new AlphaAnimation(1, 0);
                	 
                     animation2.setDuration(250);
                     alphaAmin.setDuration(250);
                     
                     animation2.setAnimationListener(new AnimationListener() 
                     {

                         public void onAnimationStart(Animation animation) 
                         {
                         }

                         public void onAnimationRepeat(Animation animation) 
                         {
                         }

                         public void onAnimationEnd(Animation animation) 
                         {
                    	 	 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.otherUser.images.get(image_index+1);
                    		 if(image != null && image.equalsIgnoreCase("") == false)
                    		 {
                    			 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
                    			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
                        		 image_index++;
                        		 
                        		 TextView heading = (TextView) findViewById(R.id.header_heading);
                    			 heading.setText(getString(R.string.teenslane_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.otherUser.images.size()) + ")");
                        		 
                        		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
                            	 alphaAmin.setDuration(170);
                        		 
                        		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        		 bmOptions.inSampleSize = 1;
                        		
                        		 if(SexActivity.otherUser.images_passwords.size()<image_index || SexActivity.otherUser.images_passwords.get(image_index) == null || SexActivity.otherUser.images_passwords.get(image_index).equalsIgnoreCase("") == false)
                        		 {
                        			 if(known_passwords.size()>image_index && known_passwords.get(image_index) != null && known_passwords.get(image_index).equals(SexActivity.otherUser.images_passwords.get(image_index)) == true)
                        			 {
                        				 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
                        				 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
                        			 }
                        			 else
                        			 {
                        				 drawPasswordDialog(image, image_index);
                        				 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
                        				 fullscreenImage.setVisibility(ImageView.INVISIBLE);
                        				 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage, false), "load user fullscreen image");
                        			 }
                        		 }
                        		 else
                        		 {
                        			 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
                        			 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
                        		 }
                        		 
                        		 reloadButtons(image_index);
                    		 }
                         }
                     });
                     
                     if(image_index<SexActivity.otherUser.images.size()-1)
                     {
                    	 AnimationSet animSet = new AnimationSet(true);
                    	 animSet.addAnimation(animation2);
                    	 animSet.addAnimation(alphaAmin);
                    	 
                    	 fullscreenImage.startAnimation(animSet);
                     }
                }
                else if(e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
                {
                	Animation animation2 = new TranslateAnimation(constLeft, constLeft+constWidth, 0, 0);
                	AlphaAnimation alphaAmin = new AlphaAnimation(1, 0);
                	
                    animation2.setDuration(250);
                    alphaAmin.setDuration(250);
                    
            		animation2.setAnimationListener(new AnimationListener() 
                    {

                        public void onAnimationStart(Animation animation) 
                        {
                        }

                        public void onAnimationRepeat(Animation animation) 
                        {
                        }

                        public void onAnimationEnd(Animation animation) 
                        {
                        	 if(profile_image == true)
                        	 {
                        		 profile_image = false;
                        		 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.otherUser.image;
	                       		 if(image != null && image.equalsIgnoreCase("") == false)
	                       		 {
	                       			 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
	                       			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
	                           		 image_index--;
	                           		 
	                           		 TextView heading = (TextView) findViewById(R.id.header_heading);
	                           		 heading.setText(getString(R.string.teenslane_gallery) + " (0/" + String.valueOf(SexActivity.otherUser.images.size()) + ")");
	                           		 
	                           		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
	                           		 alphaAmin.setDuration(170);
	                           		 
	                           		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	                           		 bmOptions.inSampleSize = 1;
	                           		 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
	                       		 }
                        	 }
                        	 else
                        	 {
	                    		 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.otherUser.images.get(image_index-1);
	                       		 if(image != null && image.equalsIgnoreCase("") == false)
	                       		 {
	                       			 fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
	                       			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
	                           		 image_index--;
	                           		 
	                           		 TextView heading = (TextView) findViewById(R.id.header_heading);
	                           		 heading.setText(getString(R.string.teenslane_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.otherUser.images.size()) + ")");
	                           		 
	                           		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
	                           		 alphaAmin.setDuration(170);
	                           		 
	                           		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	                           		 bmOptions.inSampleSize = 1;
	                           		 if(SexActivity.otherUser.images_passwords.size()<image_index || SexActivity.otherUser.images_passwords.get(image_index) == null || SexActivity.otherUser.images_passwords.get(image_index).equalsIgnoreCase("") == false)
	                        		 {
	                           			if(known_passwords.size()>image_index && known_passwords.get(image_index) != null && known_passwords.get(image_index).equals(SexActivity.otherUser.images_passwords.get(image_index)) == true)
	                           			{
	                           				SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
	                           			}
	                           			else
	                           			{
	                           				drawPasswordDialog(image, image_index);
	                           				fullscreenImage.setVisibility(ImageView.INVISIBLE);
	                           				SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage, false), "load user fullscreen image");
	                           			}
	                        		 }
	                        		 else
	                        		 {	
	                        			SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
	                        		 }
	                       		 }
                        	 }
                        	 
                        	 reloadButtons(image_index);
                        }
                    });
                    
            		if(image_index>0 || (image_index == 0 && SexActivity.otherUser.image.equals("") == false))
            		{
            			if(image_index == 0)
                		{
                			//show profile image
            				profile_image = true;
                		}
            			else
            				profile_image = false;
            			
            			AnimationSet animSet = new AnimationSet(true);
	                   	animSet.addAnimation(animation2);
	                   	animSet.addAnimation(alphaAmin);
	                   	 
	                   	fullscreenImage.startAnimation(animSet);
            		}
                }
            }
            catch (Exception e) 
            {
            }
            return false;
        }

    }
	
	public void drawPasswordDialog(final String imageUrl, final Integer imageIndex)
	{
		//gallery_marker_wtf.setVisibility(Button.GONE);
		//gallery_marker_hot.setVisibility(Button.GONE);
		
		final Dialog passwordDialog = new Dialog(this);
		passwordDialog.setContentView(R.layout.dialog_enter_password);
		passwordDialog.setTitle(getString(R.string.set_set_pwd_title));
	  	
		
		//buttons
	  	Button dialogConfirm = (Button) passwordDialog.findViewById(R.id.set_pwd_confirm);
	  	dialogConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				EditText password = (EditText) passwordDialog.findViewById(R.id.set_pwd_input);
				if(password.getText().toString().equals(SexActivity.otherUser.images_passwords.get(imageIndex)) == true)
				{
					known_passwords.add(imageIndex, SexActivity.otherUser.images_passwords.get(imageIndex));
					fullscreenImage.refreshDrawableState();
					fullscreenImage.setScaleType(ScaleType.FIT_CENTER);
					fullscreenImage.setVisibility(ImageView.VISIBLE);
					
					//gallery_marker_wtf.setVisibility(Button.VISIBLE);
					//gallery_marker_hot.setVisibility(Button.VISIBLE);
					try
					{
						if(isFinishing() == false)
							passwordDialog.cancel();
					}
					catch(Exception e)
					{
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
				}
			}	
		});
	  	
	  	Button dialogCancel = (Button) passwordDialog.findViewById(R.id.set_pwd_cancel);
	  	dialogCancel.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				fullscreenImage.setImageResource(R.drawable.gallery_action_pwd);
				fullscreenImage.setScaleType(ScaleType.CENTER);
				fullscreenImage.refreshDrawableState();
				fullscreenImage.setVisibility(ImageView.VISIBLE);
				try
				{
					if(isFinishing() == false)
						passwordDialog.cancel();
				}
				catch(Exception e)
				{
				}
				//GalleryuserActivity.this.finish();
			}	
		});
	  	
	  	try
		{
			if(isFinishing() == false)
				passwordDialog.show();
		}
		catch(Exception e)
		{
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		
	}
}