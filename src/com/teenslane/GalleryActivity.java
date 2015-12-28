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
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class GalleryActivity extends SexActivity implements OnClickListener
{
	static final int SWIPE_MIN_DISTANCE = 120;
    static final int SWIPE_MAX_OFF_PATH = 250;
    static final int SWIPE_THRESHOLD_VELOCITY = 70;
    boolean profile_image = false;
    
    GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    
    RelativeLayout gallery_rl_header;
	RelativeLayout gallery_user_actions;
	Button gallery_marker_wtf;
	Button gallery_marker_hot;
	ImageView fullscreenImage;
	Integer image_index;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		
		SexActivity.reloadGallery = true;
		
		Bundle extras = getIntent().getExtras();
		image_index = extras.getInt("image_index");
		
		setupButtons();
		
		String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.me.images.get(image_index);
		
		gallery_rl_header = (RelativeLayout) findViewById(R.id.gallery_rl_header);
		gallery_user_actions = (RelativeLayout) findViewById(R.id.gallery_user_actions);
		gallery_marker_wtf = (Button) findViewById(R.id.gallery_marker_wtf);
		gallery_marker_hot = (Button) findViewById(R.id.gallery_marker_hot);
		gallery_marker_wtf.setVisibility(Button.GONE);
		gallery_marker_hot.setVisibility(Button.GONE);
		
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
		heading.setText(getString(R.string.teenslane_my_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.me.images.size()) + ")");
		
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		
		SexActivity.executeTask(new setImageFromUrl(bmOptions, image, fullscreenImage), "load user fullscreen image");
		setDeleteImageAction(image, false);
		setPasswordAction(image, image_index);
		
		fullscreenImage.setOnTouchListener(gestureListener);
		fullscreenImage.setOnClickListener(GalleryActivity.this); 
		
		fullscreenImage.setOnLongClickListener(new View.OnLongClickListener() 
		{
			@Override
			public boolean onLongClick(View v) 
			{
				if(gallery_rl_header.getVisibility() == RelativeLayout.VISIBLE)
				{
					gallery_rl_header.setVisibility(RelativeLayout.GONE);
					gallery_user_actions.setVisibility(RelativeLayout.GONE);
					//gallery_marker_wtf.setVisibility(Button.GONE);
					//gallery_marker_hot.setVisibility(Button.GONE);
				}
				else
				{
					gallery_rl_header.setVisibility(RelativeLayout.VISIBLE);
					gallery_user_actions.setVisibility(RelativeLayout.VISIBLE);
					//gallery_marker_wtf.setVisibility(Button.VISIBLE);
					//gallery_marker_hot.setVisibility(Button.VISIBLE);
				}
				return false;
			}
		});
	}
	
	public void setupButtons()
	{
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				GalleryActivity.this.finish();
			}
		});
		
		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				SexActivity.menuReturn = true;
				GalleryActivity.this.finish();
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
                    	 	 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.me.images.get(image_index+1);
                    		 if(image != null && image.equalsIgnoreCase("") == false)
                    		 {
                    				try 
	                				{
	                					ExifInterface exif;
	                					exif = new ExifInterface(image);
	                					Log.d("orientation: ", exif.getAttribute(ExifInterface.TAG_ORIENTATION).toString());
	                					
	                				}
	                				catch (IOException e) 
	                				{
	                				}
                    			 
                    			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
                        		 image_index++;
                        		 
                        		 TextView heading = (TextView) findViewById(R.id.header_heading);
                    			 heading.setText(getString(R.string.teenslane_my_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.me.images.size()) + ")");
                        		 
                        		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
                            	 alphaAmin.setDuration(170);
                        		 
                        		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        		 bmOptions.inSampleSize = 1;
                        		 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
                        		 setDeleteImageAction(image, false);
                        		 setPasswordAction(image, image_index);
                    		 }
                         }
                     });
                     
                     if(image_index<SexActivity.me.images.size()-1)
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
                        		 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.me.image;
	                       		 if(image != null && image.equalsIgnoreCase("") == false)
	                       		 {
	                       			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
	                           		 image_index--;
	                           		 
	                           		 TextView heading = (TextView) findViewById(R.id.header_heading);
	                           		 heading.setText(getString(R.string.teenslane_my_gallery) + " (0/" + String.valueOf(SexActivity.me.images.size()) + ")");
	                           		 
	                           		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
	                           		 alphaAmin.setDuration(170);
	                           		 
	                           		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	                           		 bmOptions.inSampleSize = 1;
	                           		 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
	                           		 setDeleteImageAction(image, true);
	                           		 disablePasswordAction();
	                       		 }
                        	 }
                        	 else
                        	 {
	                    		 String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.me.images.get(image_index-1);
	                       		 if(image != null && image.equalsIgnoreCase("") == false)
	                       		 {
	                       			 fullscreenImage.setVisibility(ImageView.INVISIBLE);
	                           		 image_index--;
	                           		 
	                           		 TextView heading = (TextView) findViewById(R.id.header_heading);
	                           		 heading.setText(getString(R.string.teenslane_my_gallery) + " (" + String.valueOf(image_index+1) + "/" + String.valueOf(SexActivity.me.images.size()) + ")");
	                           		 
	                           		 AlphaAnimation alphaAmin = new AlphaAnimation(0, 1);
	                           		 alphaAmin.setDuration(170);
	                           		 
	                           		 BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	                           		 bmOptions.inSampleSize = 1;
	                           		 SexActivity.executeTask(new setImageFromUrl(alphaAmin, bmOptions, image, fullscreenImage), "load user fullscreen image");
	                           		 setDeleteImageAction(image, false);
	                           		 setPasswordAction(image, image_index);
	                       		 }
                        	 }
                        }
                    });
                    
            		if(image_index>0 || (image_index == 0 && SexActivity.me.image.equals("") == false))
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
	
	public void setPasswordAction(final String imageUrl, final Integer imageIndex)
	{
		Button gallery_action_lock = (Button) findViewById(R.id.gallery_action_lock);
		gallery_action_lock.setVisibility(Button.VISIBLE);
		gallery_action_lock.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawPasswordDialog(imageUrl, imageIndex);
			}
		});
	}
	
	public void disablePasswordAction()
	{
		Button gallery_action_lock = (Button) findViewById(R.id.gallery_action_lock);
		gallery_action_lock.setVisibility(Button.GONE);
	}
	
	public void setDeleteImageAction(final String imageUrl, final boolean profile)
	{
		Button gallery_action_delete = (Button) findViewById(R.id.gallery_action_delete);
		gallery_action_delete.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				drawConfirmDialog(imageUrl, profile);
			}
		});
	}
	
	public void drawPasswordDialog(final String imageUrl, final Integer imageIndex)
	{
		final Dialog passwordDialog = new Dialog(this);
		passwordDialog.setContentView(R.layout.dialog_password);
		passwordDialog.setTitle(getString(R.string.set_pwd_title));
	  	
		//current pwd
		Button dialogDelete = (Button) passwordDialog.findViewById(R.id.set_pwd_delete);
		TextView currentPwd = (TextView) passwordDialog.findViewById(R.id.current_pwd);
		if(SexActivity.me.images_passwords.size()<imageIndex || SexActivity.me.images_passwords.get(imageIndex) == null || SexActivity.me.images_passwords.get(imageIndex).equalsIgnoreCase("") == true)
		{
			dialogDelete.setVisibility(Button.GONE);
			currentPwd.setVisibility(TextView.INVISIBLE);
		}
		else
		{
			currentPwd.setVisibility(TextView.VISIBLE);
			currentPwd.setText(getString(R.string.set_pwd_heading) + " " + SexActivity.me.images_passwords.get(imageIndex));
			
			dialogDelete.setVisibility(Button.VISIBLE);
			dialogDelete.setOnClickListener(new View.OnClickListener() 
		  	{
				@Override
				public void onClick(View v) 
				{
					SexActivity.executeTask(new pwdImageTask(imageUrl, ""), "password image");
					try
					{
						if(isFinishing() == false)
							passwordDialog.cancel();
					}
					catch(Exception e)
					{
					}
				}	
			});
		}
		
		//buttons
	  	Button dialogConfirm = (Button) passwordDialog.findViewById(R.id.set_pwd_confirm);
	  	dialogConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				EditText password = (EditText) passwordDialog.findViewById(R.id.set_pwd_input);
				SexActivity.executeTask(new pwdImageTask(imageUrl, password.getText().toString()), "password image");
				try
				{
					if(isFinishing() == false)
						passwordDialog.cancel();
				}
				catch(Exception e)
				{
				}
			}	
		});
	  	
	  	Button dialogCancel = (Button) passwordDialog.findViewById(R.id.set_pwd_cancel);
	  	dialogCancel.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				try
				{
					if(isFinishing() == false)
						passwordDialog.cancel();
				}
				catch(Exception e)
				{
				}
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
	
	public void drawConfirmDialog(final String imageUrl, final boolean profile)
	{
		final Dialog confirmDialog = new Dialog(this);
		confirmDialog.setContentView(R.layout.dialog_confirm);
		confirmDialog.setTitle(getString(R.string.delete_confirm_title));
	  	
	  	Button dialogConfirm = (Button) confirmDialog.findViewById(R.id.confirm_confirm);
	  	dialogConfirm.setOnClickListener(new View.OnClickListener() 
	  	{
			@Override
			public void onClick(View v) 
			{
				SexActivity.executeTask(new deleteImageTask(imageUrl, profile), "delete image");
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
	
	public void passwordImage(String imageUrl, String imagePwd)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "set_image_pwd"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("image", imageUrl));
	        nameValuePairs.add(new BasicNameValuePair("pwd", imagePwd));
	        
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
	
	public void deleteImage(String imageUrl, boolean bProfile)
	{
		try 
     	{
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters, SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters, SexActivity.soTimeout);
			
			HttpClient httpclient = new DefaultHttpClient(httpParameters);  
	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	     	
	        nameValuePairs.add(new BasicNameValuePair("method", "delete_user_image"));
	        nameValuePairs.add(new BasicNameValuePair("session", SexActivity.me.session));
	        nameValuePairs.add(new BasicNameValuePair("token", SexActivity.APP_TOKEN));
	        nameValuePairs.add(new BasicNameValuePair("essence", SexActivity.APP_ESSENCE));
	        nameValuePairs.add(new BasicNameValuePair("image", imageUrl));
	        nameValuePairs.add(new BasicNameValuePair("profile", String.valueOf(bProfile)));
	        
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

	private class pwdImageTask extends AsyncTask<String, Integer, String>
	{
		public String image = "";
		public String pwd = "";
		
	    public pwdImageTask(String pImage, String pPwd)
	    {
	    	super();
	    	image = pImage;
	    	pwd = pPwd;
		}

		
	    protected void onPostExecute(String result)
	    {
	    	
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			passwordImage(image, pwd);
			return "ok";
		}
	}
	
	private class deleteImageTask extends AsyncTask<String, Integer, String>
	{
		public String image = "";
		public boolean profile = false;
		
	    public deleteImageTask(String pImage, boolean bProfile)
	    {
	    	super();
	    	image = pImage;
	    	profile = bProfile;
		}

		
	    protected void onPostExecute(String result)
	    {
	    	if(SexActivity.me.images.size()>0)
	    	{
	    		image_index = 0;
	    		String image = SexActivity.REST_SERVER_NAME + "/" + SexActivity.me.images.get(image_index);
	    		loadImage(image, image_index);
	    	}
	    	else
	    	{
	    		GalleryActivity.this.finish();
	    	}
	    }
		
		@Override
		protected String doInBackground(String... params) 
		{
			deleteImage(image, profile);
			return "ok";
		}
	}
	
	@Override
	public void onClick(View v) 
	{
		
	}
}