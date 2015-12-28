package com.teenslane;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import org.w3c.dom.Text;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;

//import com.teenslane.util.IabHelper;
//import com.teenslane.util.IabResult;
//import com.teenslane.util.Inventory;
//import com.teenslane.util.Purchase;

public class MyprofileActivity extends SexActivity {
	/*
	 * item.startepack android.test.purchased
	 */
	static final String SKU_CREDITS = "item.credits_a";// "item.credits_a";
	static final String SKU_PREMIUM_MONTH = "item.premium_pack_half";// "item.premium_pack_half";
	static final String SKU_PREMIUM_YEAR = "item.premium_pack";

	static final String SKU_CREDITS_1000 = "item.tl_credits_1000";// "item.credits_a";
	static final String SKU_CREDITS_5000 = "item.tl_credits_5000";// "item.credits_a";
	
	static final int RC_REQUEST = 10001;
	static final String TAG = "IAB";
	//static final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtkktmRVERlaAMiy5QKsGQC4JTRpCWr73iGY9EkMFLQGzTxl/nodAM/AmpSLtdMgMLDPSF2WqdK2TfgmJEBvmaM0oKmsOb5uDLeZ7LgBZAld/Uo50nFF7nSuznMy22M+3msD47ZuRFXAcLUoOhBQO4vd2/v1pIKTJub02kgBV8NFXNododPkRAj3tQfYk2ARM6h1yJIP/AsUIr3LnVx9ye2dpYMGamhHcFW6quXx/JEQQJXSVW6yaRl9arzb4NbHXCLP8cwp+KIec8D5u2vN70rR+nGocJT+N6UdpEgt3mUb+MuGE1moSkezd6dhs+2PTHSqWB6d4k9+exRvO7zEZmQIDAQAB";
	//IabHelper mHelper;

	final int SELECT_PHOTO = 100;
	final int SELECT_PHOTO_GALLERY = 200;
	final int SELECT_PHOTO_COVER = 300;

	int blinked = 0;
	int maxBlinks = 25;
	boolean disableBlinks = false;

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
	
	RelativeLayout myprofile_rl_rolled = null;
	RelativeLayout myprofile_settings_content = null;
	RelativeLayout myprofile_about_content = null;
	RelativeLayout myprofile_gallery_content = null;
	RelativeLayout myprofile_premium_content = null;
	RelativeLayout navigation_top = null;
	RelativeLayout myprofile_active_content = null;

	ImageView helperArrow = null;
	boolean forceGifts = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myprofile);

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		// Log.d(TAG, "Creating IAB helper.");
		//mHelper = new IabHelper(this, base64EncodedPublicKey);

		// enable debug logging (for a production application, you should set
		// this to false).
		//mHelper.enableDebugLogging(true);

		// Start setup. This is asynchronous and the specified listener
		// will be called once setup completes.
		// Log.d(TAG, "Starting setup.");
		/*mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			public void onIabSetupFinished(IabResult result) {
				// Log.d(TAG, "Setup finished.");
				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					complain(getString(R.string.update_google));
					return;
				}

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null)
					return;

				// IAB is fully set up. Now, let's get an inventory of stuff we
				// own.
				// Log.d(TAG, "Setup successful. Querying inventory.");
				mHelper.queryInventoryAsync(mGotInventoryListener);
			}
		});
		*/
		SexActivity.executeTask(new getUserData(), "load user data");
	}

	@Override
	protected void onResume() {
		try {
			super.onResume();
			if (SexActivity.menuReturn == true) {
				SexActivity.menuReturn = false;

				MyprofileActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(),
						IntroActivity.class);
				startActivity(mainIntent);
			} else if (SexActivity.reloadGallery == true) {
				SexActivity.reloadGallery = false;
				setMyProfileGalleryClicks();
			}
		} catch (Exception e) {
		}
	}

	// Listener that's called when we finish querying the items and
	// subscriptions we own
	/*
	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
		public void onQueryInventoryFinished(IabResult result,
				Inventory inventory) {
			// Log.d(TAG, "Query inventory finished.");

			// Have we been disposed of in the meantime? If so, quit.
			//if (mHelper == null)
				//return;

			// Is it a failure?
			/*
			 * if (result.isFailure()) { complain("Failed to query inventory: "
			 * + result); return; }
			 * 
			 * Log.d(TAG, "Query inventory was successful.");
			 */
			/*
			 * Check for items we own. Notice that for each purchase, we check
			 * the developer payload to see if it's correct! See
			 * verifyDeveloperPayload().
			 */

			// Do we have the premium upgrade?
			//Purchase item_credits = inventory.getPurchase(SKU_CREDITS_1000);
			//Purchase item_premium_half = inventory.getPurchase(SKU_CREDITS_5000);
	
			//Purchase item_premium_full = inventory.getPurchase(SKU_CREDITS_1000);

			//if (item_credits != null)
				//mHelper.consumeAsync(item_credits, mConsumeFinishedListener);
			//if (item_premium_half != null)
				//mHelper.consumeAsync(item_premium_half, mConsumeFinishedListener);
			/*if (item_premium_full != null)
				mHelper.consumeAsync(item_premium_full,
						mConsumeFinishedListener);*/

			// Boolean mIsPremium = (premiumPurchase != null &&
			// verifyDeveloperPayload(premiumPurchase));
			// Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" :
			// "NOT PREMIUM"));

			// Do we have the infinite gas plan?
			// Purchase infiniteGasPurchase =
			// inventory.getPurchase(SKU_PREMIUM_YEAR);
			// mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM_YEAR),
			// mConsumeFinishedListener);
			// mSubscribedToInfiniteGas = (infiniteGasPurchase != null
			// &&verifyDeveloperPayload(infiniteGasPurchase));
			// Log.d(TAG, "User " + (mSubscribedToInfiniteGas ? "HAS" :
			// "DOES NOT HAVE")
			// + " infinite gas subscription.");
			// if (mSubscribedToInfiniteGas) mTank = TANK_MAX;
			/*
			 * Purchase gasPurchase = inventory.getPurchase(SKU_PREMIUM_MONTH);
			 * if (gasPurchase != null &&
			 * verifyDeveloperPayload(inventory.getPurchase(SKU_PREMIUM_YEAR)))
			 * { Log.d(TAG, "We have gas. Consuming it.");
			 * mHelper.consumeAsync(inventory.getPurchase(SKU_PREMIUM_YEAR),
			 * mConsumeFinishedListener); return; }
			 */
			// Check for gas delivery -- if we own gas, we should fill up the
			// tank immediately
			// Purchase gasPurchase = inventory.getPurchase(SKU_GAS);
			// if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
			// Log.d(TAG, "We have gas. Consuming it.");
			// mHelper.consumeAsync(inventory.getPurchase(SKU_GAS),
			// mConsumeFinishedListener);
			// return;
			// }

			// updateUi();
			// setWaitScreen(false);
			// Log.d(TAG,
			// "Initial inventory query finished; enabling main UI.");
		//}
	//};

	@Override
	public void onDestroy() {
		super.onDestroy();

		// very important:
		// Log.d(TAG, "Destroying helper.");
		/*
		try {
			if (mHelper != null) {
				mHelper.dispose();
				mHelper = null;
			}
		} catch (Exception e) {
		}
		*/
	}

	public void proceedAccountAbortion() {
		LocalService.chatting = 0;
		SexActivity.me = new User();
		SexActivity.otherUser = new User();

		MyprofileActivity.this.finish();
		Intent mainIntent = new Intent(getBaseContext(), LoadActivity.class);
		startActivity(mainIntent);
	}

	public void proceedMyProfile() {
		setLayouts();
		setupButtons();
		setupNavigationButtons();

		Bundle extras = getIntent().getExtras();

		if (extras != null && extras.isEmpty() == false) {
			if (extras.containsKey("premium")) {
				detailBtn4
						.setImageResource(R.drawable.detail_button_diamond_hover);
				detailBtnRolled4
						.setImageResource(R.drawable.detail_button_diamond_hover);

				myprofile_active_content = myprofile_premium_content;
				disableIntroButtons();
				setMyProfilePremiumClicks();
				updatePremiumFeatures();
				animUp();
			}
		}
	}

	public void setLayouts() {
		if (SexActivity.me.images.size() > 0) {
			for (int i = 0; i < SexActivity.me.images.size(); i++) {
				SexActivity.me.images_loaded.set(i, false);
			}
		}

		TextView heading = (TextView) findViewById(R.id.header_heading);
		heading.setText(getString(R.string.myprofile_heading));

		// get layouts for anim and so
		topNav = (RelativeLayout) findViewById(R.id.myprofile_rl_header);
		topContent = (RelativeLayout) findViewById(R.id.myprofile_rl_intro);
		navigation = (RelativeLayout) findViewById(R.id.myprofile_rl_navigation);
		navigation_bottom = (RelativeLayout) navigation.findViewById(R.id.navigation_bottom);
		
		myprofile_rl_rolled = (RelativeLayout) findViewById(R.id.myprofile_rl_rolled);
		navigation_top = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.navigation_top);

		myprofile_settings_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_settings_content);
		myprofile_about_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_about_content);
		myprofile_gallery_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_gallery_content);
		myprofile_premium_content = (RelativeLayout) myprofile_rl_rolled.findViewById(R.id.myprofile_premium_content);

		// change navigation buttons
		detailBtn1 = (ImageView) navigation.findViewById(R.id.detail_button1);
		detailBtn2 = (ImageView) navigation.findViewById(R.id.detail_button2);
		detailBtn3 = (ImageView) navigation.findViewById(R.id.detail_button3);
		detailBtn4 = (ImageView) navigation.findViewById(R.id.detail_button4);
		
		detailBtn1.setImageResource(R.drawable.css_detail_settings);
		detailBtn2.setImageResource(R.drawable.css_detail_info);
		detailBtn3.setImageResource(R.drawable.css_detail_gallery);
		detailBtn4.setImageResource(R.drawable.css_detail_diamond);

		detailBtnRolled1 = (ImageView) navigation_top.findViewById(R.id.detail_button1);
		detailBtnRolled2 = (ImageView) navigation_top.findViewById(R.id.detail_button2);
		detailBtnRolled3 = (ImageView) navigation_top.findViewById(R.id.detail_button3);
		detailBtnRolled4 = (ImageView) navigation_top.findViewById(R.id.detail_button4);

		detailBtnRolled1.setImageResource(R.drawable.css_detail_settings);
		detailBtnRolled2.setImageResource(R.drawable.css_detail_info);
		detailBtnRolled3.setImageResource(R.drawable.css_detail_gallery);
		detailBtnRolled4.setImageResource(R.drawable.css_detail_diamond);
		
		helperArrow = (ImageView) findViewById(R.id.helper_arrow);
		if (SexActivity.me.nickname.equalsIgnoreCase("") == true)
			blink(helperArrow);
		else
			helperArrow.setVisibility(ImageView.GONE);
	}

	public void setupButtons() 
	{
		navigation_bottom.setOnTouchListener(new View.OnTouchListener() 
		{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int eventAction = event.getAction();
				switch (eventAction) {
				case MotionEvent.ACTION_DOWN:
					disableIntroButtons();

					if (myprofile_active_content == null
							|| myprofile_active_content == myprofile_settings_content) {
						detailBtn1
								.setImageResource(R.drawable.detail_button_settings_hover);
						detailBtnRolled1
								.setImageResource(R.drawable.detail_button_settings_hover);

						myprofile_active_content = myprofile_settings_content;
						setMyProfileSettingsClicks();
					}
					animUp();
					break;
				}
				return false;
			}
		});

		navigation_top.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int eventAction = event.getAction();
				switch (eventAction) {
				case MotionEvent.ACTION_DOWN:
					enableIntroButtons();
					animDown();
					break;
				}
				return false;
			}
		});
		
		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyprofileActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(),
						IntroActivity.class);
				startActivity(mainIntent);
			}
		});

		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyprofileActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(),
						IntroActivity.class);
				startActivity(mainIntent);
			}
		});

		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
		if (SexActivity.me.image != null && SexActivity.me.image.equalsIgnoreCase("") == false) 
		{
			// set user image
			String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_SIZE_240 + "/" + SexActivity.me.image;
			SexActivity.executeTask(new setImageFromUrl(userImageUrl, pickPhoto, "circle"), "load user image");
			
		}
		else 
		{
			setNoPhotoImage(pickPhoto, SexActivity.me.gender);
		}

		TextView update_profile_button = (TextView) findViewById(R.id.update_image_button);
		update_profile_button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
			}
		});
		
		if(SexActivity.me.cover_image != null && SexActivity.me.cover_image.equalsIgnoreCase("") == false)
		{
			ImageView cover_image = (ImageView) findViewById(R.id.cover_image);
			String userImageUrl = SexActivity.REST_SERVER_NAME + SexActivity.REST_IMAGE_COVER + "/" + SexActivity.me.cover_image;
			SexActivity.executeTask(new setImageFromUrl(userImageUrl, cover_image), "load user image");
		}
		
		TextView update_cover_button = (TextView) findViewById(R.id.update_cover_button);
		update_cover_button.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				startActivityForResult(photoPickerIntent, SELECT_PHOTO_COVER);
			}
		});
	}

	public void setupNavigationButtons() 
	{
		detailBtn1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detailBtn1.setImageResource(R.drawable.detail_button_settings_hover);
				detailBtnRolled1.setImageResource(R.drawable.detail_button_settings_hover);

				myprofile_active_content = myprofile_settings_content;
				disableIntroButtons();
				setMyProfileSettingsClicks();
				animUp();
			}
		});

		detailBtnRolled1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myprofile_active_content == null) {
					enableIntroButtons();
					animDown();
				} else {
					if (myprofile_active_content == myprofile_settings_content) {
						enableIntroButtons();
						animDown();
					} else {
						detailBtn1.setImageResource(R.drawable.detail_button_settings_hover);
						detailBtnRolled1.setImageResource(R.drawable.detail_button_settings_hover);

						detailBtn2.setImageResource(R.drawable.css_detail_info);
						detailBtnRolled2.setImageResource(R.drawable.css_detail_info);

						detailBtn3.setImageResource(R.drawable.css_detail_gallery);
						detailBtnRolled3.setImageResource(R.drawable.css_detail_gallery);

						detailBtn4.setImageResource(R.drawable.css_detail_diamond);
						detailBtnRolled4.setImageResource(R.drawable.css_detail_diamond);

						myprofile_active_content = myprofile_settings_content;
						setMyProfileSettingsClicks();
						switchContents();
					}
				}
			}
		});

		detailBtn2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detailBtn2.setImageResource(R.drawable.detail_button_info_hover);
				detailBtnRolled2.setImageResource(R.drawable.detail_button_info_hover);

				myprofile_active_content = myprofile_about_content;
				disableIntroButtons();
				setMyProfileAboutClicks();
				animUp();
			}
		});

		detailBtnRolled2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myprofile_active_content == null) {
					myprofile_active_content = myprofile_about_content;
					enableIntroButtons();
					animDown();
				} else {
					if (myprofile_active_content == myprofile_about_content) {
						enableIntroButtons();
						animDown();
					} else {
						detailBtn1.setImageResource(R.drawable.css_detail_settings);
						detailBtnRolled1.setImageResource(R.drawable.css_detail_settings);

						detailBtn2.setImageResource(R.drawable.detail_button_info_hover);
						detailBtnRolled2.setImageResource(R.drawable.detail_button_info_hover);

						detailBtn3.setImageResource(R.drawable.css_detail_gallery);
						detailBtnRolled3.setImageResource(R.drawable.css_detail_gallery);

						detailBtn4.setImageResource(R.drawable.css_detail_diamond);
						detailBtnRolled4.setImageResource(R.drawable.css_detail_diamond);

						myprofile_active_content = myprofile_about_content;
						setMyProfileAboutClicks();
						switchContents();
					}
				}
			}
		});

		detailBtn3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detailBtn3
						.setImageResource(R.drawable.detail_button_gallery_hover);
				detailBtnRolled3
						.setImageResource(R.drawable.detail_button_gallery_hover);

				myprofile_active_content = myprofile_gallery_content;
				disableIntroButtons();
				setMyProfileGalleryClicks();
				animUp();
			}
		});

		detailBtnRolled3.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myprofile_active_content == null) {
					myprofile_active_content = myprofile_gallery_content;
					enableIntroButtons();
					animDown();
				} else {
					if (myprofile_active_content == myprofile_gallery_content) {
						enableIntroButtons();
						animDown();
					} else {
						detailBtn1
								.setImageResource(R.drawable.css_detail_settings);
						detailBtnRolled1
								.setImageResource(R.drawable.css_detail_settings);

						detailBtn2.setImageResource(R.drawable.css_detail_info);
						detailBtnRolled2
								.setImageResource(R.drawable.css_detail_info);

						detailBtn3
								.setImageResource(R.drawable.detail_button_gallery_hover);
						detailBtnRolled3
								.setImageResource(R.drawable.detail_button_gallery_hover);

						detailBtn4
								.setImageResource(R.drawable.css_detail_diamond);
						detailBtnRolled4
								.setImageResource(R.drawable.css_detail_diamond);

						myprofile_active_content = myprofile_gallery_content;
						setMyProfileGalleryClicks();
						switchContents();
					}
				}
			}
		});

		detailBtn4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				detailBtn4
						.setImageResource(R.drawable.detail_button_diamond_hover);
				detailBtnRolled4
						.setImageResource(R.drawable.detail_button_diamond_hover);

				myprofile_active_content = myprofile_premium_content;
				disableIntroButtons();
				setMyProfilePremiumClicks();
				updatePremiumFeatures();
				animUp();
			}
		});

		detailBtnRolled4.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myprofile_active_content == null) {
					myprofile_active_content = myprofile_premium_content;
					enableIntroButtons();
					animDown();
				} else {
					if (myprofile_active_content == myprofile_premium_content) {
						enableIntroButtons();
						animDown();
					} else {
						detailBtn1
								.setImageResource(R.drawable.css_detail_settings);
						detailBtnRolled1
								.setImageResource(R.drawable.css_detail_settings);

						detailBtn2.setImageResource(R.drawable.css_detail_info);
						detailBtnRolled2
								.setImageResource(R.drawable.css_detail_info);

						detailBtn3
								.setImageResource(R.drawable.css_detail_gallery);
						detailBtnRolled3
								.setImageResource(R.drawable.css_detail_gallery);

						detailBtn4
								.setImageResource(R.drawable.detail_button_diamond_hover);
						detailBtnRolled4
								.setImageResource(R.drawable.detail_button_diamond_hover);

						myprofile_active_content = myprofile_premium_content;
						setMyProfilePremiumClicks();
						updatePremiumFeatures();
						switchContents();
					}
				}
			}
		});
	}

	public void switchContents() {
		myprofile_active_content.setVisibility(RelativeLayout.VISIBLE);

		if (myprofile_active_content == myprofile_about_content) {
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			myprofile_premium_content.setVisibility(RelativeLayout.GONE);
		} else if (myprofile_active_content == myprofile_settings_content) {
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
			myprofile_premium_content.setVisibility(RelativeLayout.GONE);
		} else if (myprofile_active_content == myprofile_gallery_content) {
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_premium_content.setVisibility(RelativeLayout.GONE);
		} else if (myprofile_active_content == myprofile_premium_content) {
			myprofile_about_content.setVisibility(RelativeLayout.GONE);
			myprofile_settings_content.setVisibility(RelativeLayout.GONE);
			myprofile_gallery_content.setVisibility(RelativeLayout.GONE);
		}
	}

	public void animUp() {
		
		helperArrow.setVisibility(ImageView.GONE);
		blinked = maxBlinks;
		disableBlinks = true;

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
		animation2 = new TranslateAnimation(0, 0, navigation.getBaseline(),(navigation.getBaseline() - navigation.getTop())+ topNav.getHeight() + 1);
		animation2.setDuration(500);
		animation2.setFillEnabled(true);
		animation2.setFillAfter(true);
		animation2.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) 
			{
			}

			public void onAnimationRepeat(Animation animation) 
			{
			}

			public void onAnimationEnd(Animation animation) 
			{
				myprofile_active_content.setVisibility(RelativeLayout.VISIBLE);
				myprofile_rl_rolled.setVisibility(RelativeLayout.VISIBLE);
				navigation.setVisibility(RelativeLayout.INVISIBLE);
			}
		});

		topContent.startAnimation(animation);
		navigation.startAnimation(animation2);

	}

	public void animDown() 
	{
		Animation animation = null;
		animation = new TranslateAnimation(0, 0, topContent.getBaseline() - topContent.getHeight() - topContent.getTop(), 0);
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
		animation2 = new TranslateAnimation(0, 0, (navigation.getBaseline() - navigation.getTop()) + topNav.getHeight() + 1, navigation.getBaseline());
		
		animation2.setDuration(500);
		animation2.setFillEnabled(true);
		animation2.setFillAfter(true);
		animation2.setAnimationListener(new AnimationListener() 
		{

			public void onAnimationStart(Animation animation) 
			{
				navigation_top.setVisibility(RelativeLayout.INVISIBLE);
				navigation.setVisibility(RelativeLayout.VISIBLE);
			}

			public void onAnimationRepeat(Animation animation) 
			{
			}

			public void onAnimationEnd(Animation animation) 
			{
				myprofile_rl_rolled.setVisibility(RelativeLayout.INVISIBLE);
				myprofile_active_content.setVisibility(RelativeLayout.GONE);
				navigation_top.setVisibility(RelativeLayout.VISIBLE);
				myprofile_active_content = null;
				enableIntroButtons();
			}
		});

		Animation animation3 = null;
		animation3 = new TranslateAnimation(0, 0, myprofile_active_content.getBaseline(), getScreenHeight() - topNav.getHeight() - navigation.getHeight());
		animation3.setDuration(500);

		topContent.startAnimation(animation);
		navigation.startAnimation(animation2);
		myprofile_active_content.startAnimation(animation3);
	}

	public void disableIntroButtons() {
		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
		pickPhoto.setEnabled(false);
		navigation_bottom.setEnabled(false);
		navigation_top.setEnabled(true);

		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View v) {
				enableIntroButtons();
				animDown();
			}
		});

		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_detail_save);
		menuButton.refreshDrawableState();
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.nickname.equalsIgnoreCase("") == true
						|| SexActivity.me.gender == 0
						|| SexActivity.me.iwant == 0
						|| SexActivity.me.dob.equalsIgnoreCase("") == true) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_save_error),
							Toast.LENGTH_LONG).show();
				} else {
					if (SexActivity.me.age < 13 || SexActivity.me.age > 19)
						Toast.makeText(getApplicationContext(),
								getString(R.string.myprofile_save_error_aged),
								Toast.LENGTH_LONG).show();
					else {
						// check terms
						if (SexActivity.me.terms_agreed != 1)
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.myprofile_save_error_terms),
									Toast.LENGTH_LONG).show();
						else {
							Toast.makeText(getApplicationContext(),
									getString(R.string.myprofile_save_success),
									Toast.LENGTH_LONG).show();
							enableIntroButtons();
							animDown();
						}
					}
				}

				// save text inputs afterwards
				EditText nicknameInput = (EditText) myprofile_settings_content
						.findViewById(R.id.myprofile_nickname_input);
				if (nicknameInput.getText().toString().equalsIgnoreCase("") == false) {
					SexActivity.executeTask(new saveUserStringTask("nickname",
							nicknameInput.getText().toString()),
							"save nickname");
					SexActivity.me.nickname = nicknameInput.getText()
							.toString();
				}

				EditText aboutInput = (EditText) myprofile_about_content
						.findViewById(R.id.myprofile_aboutme_input);
				if (aboutInput.getText().toString().equalsIgnoreCase("") == false) {
					SexActivity.executeTask(new saveUserDataStringTask(
							"describe", aboutInput.getText().toString()),
							"save describe");
					SexActivity.me.describe = aboutInput.getText().toString();
				}
			}
		});
	}

	public void enableIntroButtons() 
	{
		detailBtn1.setImageResource(R.drawable.css_detail_settings);
		detailBtnRolled1.setImageResource(R.drawable.css_detail_settings);

		detailBtn2.setImageResource(R.drawable.css_detail_info);
		detailBtnRolled2.setImageResource(R.drawable.css_detail_info);

		detailBtn3.setImageResource(R.drawable.css_detail_gallery);
		detailBtnRolled3.setImageResource(R.drawable.css_detail_gallery);

		ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
		pickPhoto.setEnabled(true);
		navigation_bottom.setEnabled(true);
		navigation_top.setEnabled(false);

		ImageView backButton = (ImageView) findViewById(R.id.header_back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyprofileActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(),
						IntroActivity.class);
				startActivity(mainIntent);
			}
		});

		ImageView menuButton = (ImageView) findViewById(R.id.header_menu_button);
		menuButton.setImageResource(R.drawable.css_button_menu);
		menuButton.refreshDrawableState();
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MyprofileActivity.this.finish();
				Intent mainIntent = new Intent(getBaseContext(),
						IntroActivity.class);
				startActivity(mainIntent);
			}
		});
	}

	public void setMyProfileGalleryClicks() 
	{
		Button pickPhoto = (Button) myprofile_gallery_content
				.findViewById(R.id.gallery_picker);
		pickPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (SexActivity.me.images.size() < SexActivity.me.maxGalleryImages) {
					Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
					photoPickerIntent.setType("image/*");
					startActivityForResult(photoPickerIntent,
							SELECT_PHOTO_GALLERY);
				} else
					Toast.makeText(getApplicationContext(),
							getString(R.string.teenslane_max_images),
							Toast.LENGTH_SHORT).show();
			}
		});

		if (SexActivity.me.image.equals("") == true) {
			ImageView pickPhotoMain = (ImageView) findViewById(R.id.pickphoto);
			setNoPhotoImage(pickPhotoMain, SexActivity.me.gender);
		}

		if (SexActivity.me.images.size() > 0) {
			for (int i = 0; i < SexActivity.me.images.size(); i++) {
				final int image_index = i;

				Integer imageRes = myprofile_gallery_content.getResources()
						.getIdentifier(
								"gallery_image" + Integer.toString(i + 1),
								"id", this.getPackageName());
				if (imageRes != null) {
					ImageView galleryPhoto = (ImageView) myprofile_gallery_content
							.findViewById(imageRes).findViewById(
									R.id.gallery_image_src);
					ProgressBar galleryLoading = (ProgressBar) myprofile_gallery_content
							.findViewById(imageRes).findViewById(
									R.id.gallery_loading);
					ImageView galleryLock = (ImageView) myprofile_gallery_content
							.findViewById(imageRes).findViewById(
									R.id.gallery_image_lock);
					if (galleryPhoto != null) {
						galleryLock.setVisibility(ImageView.INVISIBLE);
						if (SexActivity.me.images_passwords.size() < i
								|| SexActivity.me.images_passwords.get(i) == null
								|| SexActivity.me.images_passwords.get(i)
										.equalsIgnoreCase("") == false)
							galleryLock.setVisibility(ImageView.VISIBLE);

						final String userImageUrl = SexActivity.REST_SERVER_NAME
								+ SexActivity.REST_IMAGE_SIZE_148
								+ "/"
								+ SexActivity.me.images.get(i);

						if (SexActivity.me.images_loaded.get(i) == false) {
							SexActivity.me.images_loaded.set(i, true);
							myprofile_gallery_content.findViewById(imageRes)
									.setVisibility(RelativeLayout.VISIBLE);
							SexActivity.executeTask(
									new setImageFromUrl(userImageUrl,
											galleryPhoto, galleryLoading),
									"load user gallery image");
						}

						galleryPhoto
								.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Bundle extras = new Bundle();
										extras.putInt("image_index",
												image_index);

										Intent gallery = new Intent(
												getBaseContext(),
												GalleryActivity.class);
										gallery.putExtras(extras);
										startActivity(gallery);
									}
								});
					}
				}
			}
		}

		if (SexActivity.me.images.size() < SexActivity.me.maxGalleryImages) {
			for (int i = SexActivity.me.images.size() + 1; i <= SexActivity.me.maxGalleryImages; i++) {
				Integer imageRes = myprofile_gallery_content.getResources()
						.getIdentifier("gallery_image" + Integer.toString(i),
								"id", this.getPackageName());
				// Log.d("image res:", "gallery_image" + Integer.toString(i));
				if (imageRes != null)
					myprofile_gallery_content.findViewById(imageRes)
							.setVisibility(RelativeLayout.INVISIBLE);

			}
		}
	}

	public void setMyProfileAboutClicks() {
		// describe
		final EditText aboutInput = (EditText) myprofile_about_content
				.findViewById(R.id.myprofile_aboutme_input);
		if (SexActivity.me.describe != null
				&& SexActivity.me.describe.equalsIgnoreCase("") == false) {
			aboutInput.setText(SexActivity.me.describe);
		}
		aboutInput.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if (aboutInput.getText().toString().equalsIgnoreCase("") == false) {
						SexActivity.executeTask(new saveUserDataStringTask(
								"describe", aboutInput.getText().toString()),
								"save describe");
						SexActivity.me.describe = aboutInput.getText()
								.toString();
					}
				}
				return false;
			}
		});

		// body
		Button bodyInput = (Button) myprofile_about_content
				.findViewById(R.id.myprofile_looks_body_input);
		if (SexActivity.me.body != 0) {
			setBodyValue(SexActivity.me.body);
		}
		bodyInput.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawBodyDialog();
			}
		});

		// ethnicity
		Button ethnicityInput = (Button) myprofile_about_content
				.findViewById(R.id.myprofile_looks_ethnicity_input);
		if (SexActivity.me.ethnicity != 0) {
			setEthnicityValue(SexActivity.me.ethnicity);
		}
		ethnicityInput.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawEthnicityDialog();
			}
		});

		// loves
		Button lovesInput = (Button) myprofile_about_content
				.findViewById(R.id.myprofile_ilove_input);
		setLovesValue(SexActivity.me.loves);
		lovesInput.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawLovesDialog();
			}
		});
	}

	public void setMyProfileSettingsClicks() 
	{
		setGenderValue(SexActivity.me.gender, "gender");
		setGenderValue(SexActivity.me.iwant, "iwant");

		Button dobInputButton = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_dob_input);
		if (SexActivity.me.dob != null
				&& SexActivity.me.dob.equalsIgnoreCase("") == false) {
			dobInputButton.setText(SexActivity.me.dob);
		}

		final EditText nicknameInput = (EditText) myprofile_settings_content
				.findViewById(R.id.myprofile_nickname_input);
		if (SexActivity.me.nickname != null
				&& SexActivity.me.nickname.equalsIgnoreCase("") == false) {
			nicknameInput.setText(SexActivity.me.nickname);
		}
		nicknameInput.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE
						|| actionId == EditorInfo.IME_ACTION_GO
						|| actionId == EditorInfo.IME_ACTION_NEXT
						|| actionId == EditorInfo.IME_ACTION_SEND) {
					if (nicknameInput.getText().toString().equalsIgnoreCase("") == false) {
						SexActivity.executeTask(
								new saveUserStringTask("nickname",
										nicknameInput.getText().toString()),
								"save nickname");
						SexActivity.me.nickname = nicknameInput.getText()
								.toString();
					}
				}
				return false;
			}
		});

		Button genderInput = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_gender_input);
		genderInput.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawGenderDialog();
			}
		});

		Button lookingforInput = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_looking_for_input);
		lookingforInput.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawLookingforDialog();
			}
		});

		dobInputButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				drawDobDialog();
			}
		});

		final RadioButton hideOnMap = (RadioButton) myprofile_settings_content
				.findViewById(R.id.myprofile_incognito_input_map);
		final Button hideOnMapBtn = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_incognito_top);
		if (SexActivity.me.hide_on_map == 1) {
			hideOnMap.setChecked(true);
		}
		hideOnMapBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.hide_on_map == 1) {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_map", "0"), "save hide on map");
					SexActivity.me.hide_on_map = 0;
					hideOnMap.setChecked(false);
				} else {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_map", "1"), "save hide on map");
					SexActivity.me.hide_on_map = 1;
					hideOnMap.setChecked(true);
				}
			}
		});
		hideOnMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.hide_on_map == 1) {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_map", "0"), "save hide on map");
					SexActivity.me.hide_on_map = 0;
					hideOnMap.setChecked(false);
				} else {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_map", "1"), "save hide on map");
					SexActivity.me.hide_on_map = 1;
					hideOnMap.setChecked(true);
				}
			}
		});

		final RadioButton hideOnSearch = (RadioButton) myprofile_settings_content
				.findViewById(R.id.myprofile_incognito_input_search);
		final Button hideOnSearchBtn = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_incognito_bottom);
		if (SexActivity.me.hide_on_search == 1) {
			hideOnSearch.setChecked(true);
		}
		hideOnSearchBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.hide_on_search == 1) {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_search", "0"), "save hide on search");
					SexActivity.me.hide_on_search = 0;
					hideOnSearch.setChecked(false);
				} else {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_search", "1"), "save hide on search");
					SexActivity.me.hide_on_search = 1;
					hideOnSearch.setChecked(true);
				}
			}
		});
		hideOnSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.hide_on_search == 1) {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_search", "0"), "save hide on search");
					SexActivity.me.hide_on_search = 0;
					hideOnSearch.setChecked(false);
				} else {
					SexActivity.executeTask(new saveUserStringTask(
							"hide_on_search", "1"), "save hide on search");
					SexActivity.me.hide_on_search = 1;
					hideOnSearch.setChecked(true);
				}
			}
		});

		RadioButton termsRadio = (RadioButton) myprofile_settings_content
				.findViewById(R.id.myprofile_terms_input);
		if (SexActivity.me.terms_agreed == 1) {
			termsRadio = (RadioButton) myprofile_settings_content
					.findViewById(R.id.myprofile_terms_input);
			TextView termsText = (TextView) myprofile_settings_content
					.findViewById(R.id.myprofile_terms_input_text);

			termsRadio.setVisibility(RadioButton.INVISIBLE);
			termsText.setVisibility(TextView.INVISIBLE);
		} else {
			termsRadio.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SexActivity.executeTask(new saveUserStringTask(
							"terms_agreed", "1"), "save terms");
					SexActivity.me.terms_agreed = 1;
				}
			});
		}

		TextView termsLink = (TextView) myprofile_settings_content
				.findViewById(R.id.myprofile_terms_link);
		termsLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(getString(R.string.myprofile_terms_url)));
				startActivity(browserIntent);
			}
		});

		Button saveMyData = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_save_input);
		saveMyData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (SexActivity.me.nickname.equalsIgnoreCase("") == true
						|| SexActivity.me.gender == 0
						|| SexActivity.me.iwant == 0
						|| SexActivity.me.dob.equalsIgnoreCase("") == true) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_save_error),
							Toast.LENGTH_LONG).show();
				} else {
					if (SexActivity.me.age < 13 || SexActivity.me.age > 19)
						Toast.makeText(getApplicationContext(),
								getString(R.string.myprofile_save_error_aged),
								Toast.LENGTH_LONG).show();
					else {
						// check terms
						if (SexActivity.me.terms_agreed != 1)
							Toast.makeText(
									getApplicationContext(),
									getString(R.string.myprofile_save_error_terms),
									Toast.LENGTH_LONG).show();
						else {
							Toast.makeText(getApplicationContext(),
									getString(R.string.myprofile_save_success),
									Toast.LENGTH_LONG).show();
							enableIntroButtons();
							animDown();
						}
					}
				}

				// save text inputs afterwards
				EditText nicknameInput = (EditText) myprofile_settings_content
						.findViewById(R.id.myprofile_nickname_input);
				if (nicknameInput.getText().toString().equalsIgnoreCase("") == false) {
					SexActivity.executeTask(new saveUserStringTask("nickname",
							nicknameInput.getText().toString()),
							"save nickname");
					SexActivity.me.nickname = nicknameInput.getText()
							.toString();
				}

				EditText aboutInput = (EditText) myprofile_about_content
						.findViewById(R.id.myprofile_aboutme_input);
				if (aboutInput.getText().toString().equalsIgnoreCase("") == false) {
					SexActivity.executeTask(new saveUserDataStringTask(
							"describe", aboutInput.getText().toString()),
							"save describe");
					SexActivity.me.describe = aboutInput.getText().toString();
				}
			}
		});

		Button deleteAccount = (Button) myprofile_settings_content
				.findViewById(R.id.myprofile_delete_input);
		deleteAccount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showConfirmDeleteDialog();
			}
		});
	}

	public void setMyProfilePremiumClicks() {
		TextView credits = (TextView) myprofile_premium_content
				.findViewById(R.id.credits);
		credits.setText(String.valueOf(SexActivity.me.credits) + " "
				+ getString(R.string.credits));

		Button buy_credits = (Button) myprofile_premium_content
				.findViewById(R.id.buy_credits_100);
		buy_credits.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SexActivity.executeTask(new order(1), "order");
				//mHelper.launchPurchaseFlow(MyprofileActivity.this,
				//		SKU_CREDITS_1000, RC_REQUEST, mPurchaseFinishedListener,
				//		"p1");
			}
		});

		Button buy_pp_half = (Button) myprofile_premium_content
				.findViewById(R.id.buy_credits_500);
		buy_pp_half.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SexActivity.executeTask(new order(2), "order");
				//mHelper.launchPurchaseFlow(MyprofileActivity.this,
				//		SKU_CREDITS_5000, RC_REQUEST, mPurchaseFinishedListener,
				//		"p2");
			}
		});
		/*
		Button buy_pp_full = (Button) myprofile_premium_content
				.findViewById(R.id.buy_credits_1000);
		buy_pp_full.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mHelper.launchPurchaseFlow(MyprofileActivity.this,
						SKU_CREDITS_1000, RC_REQUEST,
						mPurchaseFinishedListener, "p3");
			}
		});
		*/
		/* premium feature purchase */
		((Button) myprofile_premium_content
				.findViewById(R.id.buy_premium_feature_feed_viewed))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						drawPremiumFeatureDialog(SexActivity.feature_feed_view);
					}
				});

		((Button) myprofile_premium_content
				.findViewById(R.id.buy_premium_feature_pets_added))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						drawPremiumFeatureDialog(SexActivity.feature_pets_added);
					}
				});

		((Button) myprofile_premium_content
				.findViewById(R.id.buy_premium_feature_pets_removed))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						drawPremiumFeatureDialog(SexActivity.feature_pets_removed);
					}
				});
	}

	public void updatePremiumFeatures() {
		TextView feedViewStatus = (TextView) findViewById(R.id.premium_feature_feed_viewed_status_text_value);

		if (SexActivity.me.isFeatureActive(SexActivity.feature_feed_view)) {
			feedViewStatus
					.setText(getString(R.string.premium_feature_active)
							+ " "
							+ SexActivity.me
									.getFeatureExpiration(SexActivity.feature_feed_view));
		} else {
			feedViewStatus
					.setText(getString(R.string.premium_feature_inactive));
		}

		TextView petsAddedStatus = (TextView) findViewById(R.id.premium_feature_pets_added_status_text_value);

		if (SexActivity.me.isFeatureActive(SexActivity.feature_pets_added)) {
			petsAddedStatus
					.setText(getString(R.string.premium_feature_active)
							+ " "
							+ SexActivity.me
									.getFeatureExpiration(SexActivity.feature_pets_added));
		} else {
			petsAddedStatus
					.setText(getString(R.string.premium_feature_inactive));
		}

		TextView petsRemovedStatus = (TextView) findViewById(R.id.premium_feature_pets_removed_status_text_value);

		if (SexActivity.me.isFeatureActive(SexActivity.feature_pets_removed)) {
			petsRemovedStatus
					.setText(getString(R.string.premium_feature_active)
							+ " "
							+ SexActivity.me
									.getFeatureExpiration(SexActivity.feature_pets_removed));
		} else {
			petsRemovedStatus
					.setText(getString(R.string.premium_feature_inactive));
		}
	}

	public void drawPremiumFeatureDialog(final int featureId) {
		final Dialog premiumDialog = new Dialog(this);
		premiumDialog.setContentView(R.layout.dialog_feature_purchase);

		final RadioButton optionMonth = (RadioButton) premiumDialog
				.findViewById(R.id.option_month);
		final RadioButton optionYear = (RadioButton) premiumDialog
				.findViewById(R.id.option_year);

		if (featureId == SexActivity.feature_feed_view) {
			premiumDialog
					.setTitle(getString(R.string.premium_feature_feed_viewed));
			optionMonth
					.setText(getString(R.string.premium_feature_feed_viewed_month));
			optionYear
					.setText(getString(R.string.premium_feature_feed_viewed_year));
		} else if (featureId == SexActivity.feature_pets_added) {
			premiumDialog
					.setTitle(getString(R.string.premium_feature_pets_added));
			optionMonth
					.setText(getString(R.string.premium_feature_pets_added_month));
			optionYear
					.setText(getString(R.string.premium_feature_pets_added_year));
		} else if (featureId == SexActivity.feature_pets_removed) {
			premiumDialog
					.setTitle(getString(R.string.premium_feature_pets_removed));
			optionMonth
					.setText(getString(R.string.premium_feature_pets_removed_month));
			optionYear
					.setText(getString(R.string.premium_feature_pets_removed_year));
		} else {
			return;
		}

		Button dialogConfirm = (Button) premiumDialog
				.findViewById(R.id.purchase_confirm);
		dialogConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				unlockFeature pf = new unlockFeature();
				pf.dialog = premiumDialog;
				pf.featureId = featureId;
				pf.duration = optionMonth.isChecked() ? "month" : "year";
				SexActivity.executeTask(pf, "purchase feature");
			}
		});

		if (!isFinishing())
			premiumDialog.show();
	}

	public void showConfirmDeleteDialog() {
		final Dialog confirmDialog = new Dialog(this);
		confirmDialog.setContentView(R.layout.dialog_confirm);
		confirmDialog.setTitle(getString(R.string.myprofile_delete_account));

		TextView confirm_heading = (TextView) confirmDialog
				.findViewById(R.id.confirm_heading);
		confirm_heading.setText(getString(R.string.delete_confirm));

		Button dialogConfirm = (Button) confirmDialog
				.findViewById(R.id.confirm_confirm);
		dialogConfirm.setText(getString(R.string.delete_confirm_yes));
		dialogConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SexActivity.executeTask(new deleteUser(), "delete account");
			}
		});

		Button dialogCancel = (Button) confirmDialog
				.findViewById(R.id.confirm_cancel);
		dialogCancel.setText(getString(R.string.delete_confirm_no));
		dialogCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if (isFinishing() == false)
						confirmDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				confirmDialog.show();
		} catch (Exception e) {
		}
	}

	public static int getOrientation(Context context, Uri photoUri) {
		/* it's on the external media. */
		Cursor cursor = context.getContentResolver().query(photoUri,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION },
				null, null, null);

		if (cursor.getCount() != 1) {
			return -1;
		}

		cursor.moveToFirst();
		return cursor.getInt(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent imageReturnedIntent) 
	{
		if (requestCode == SELECT_PHOTO || requestCode == SELECT_PHOTO_GALLERY || requestCode == SELECT_PHOTO_COVER)
		{
			super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

			switch (requestCode) {
			case SELECT_PHOTO_COVER:
				if (resultCode == RESULT_OK) 
				{
					try 
					{
						Uri selectedImage = imageReturnedIntent.getData();
						InputStream imageStream = getContentResolver().openInputStream(selectedImage);

						BitmapFactory.Options bfOptions = new BitmapFactory.Options();
						bfOptions.inSampleSize = 2;

						Bitmap userImageOriginal = BitmapFactory.decodeStream(
								imageStream, null, bfOptions);
						Bitmap userImageScaled = null;
						Bitmap userImage = null;

						int originalWidth = 0;
						int originalHeight = 0;
						int newWidth = 0;
						int newHeight = 0;

						if (userImageOriginal != null) {
							originalWidth = userImageOriginal.getWidth();
							originalHeight = userImageOriginal.getHeight();
							int boundWidth = 640;
							int boundHeight = 480;

							if (originalHeight > boundHeight
									|| originalWidth > boundWidth) {
								// first check if we need to scale width
								if (originalWidth > boundWidth) {
									newWidth = boundWidth;
									newHeight = (newWidth * originalHeight)
											/ originalWidth;
								}

								// then check if we need to scale even with the
								// new height
								if (newHeight > boundHeight) {
									newHeight = boundHeight;
									newWidth = (newHeight * originalWidth)
											/ originalHeight;
								}

								if (newHeight != 0 && newWidth != 0) {
									// create new - scaled
									userImageScaled = Bitmap
											.createScaledBitmap(
													userImageOriginal,
													newWidth, newHeight, false);

									// recycle old crap
									if (userImageScaled != userImageOriginal) {
										userImageOriginal.recycle();
										userImageOriginal = null;
									}
								} else {
									// no rescale needed
									userImageScaled = userImageOriginal;
									newWidth = originalWidth;
									newHeight = originalHeight;
								}
							} else {
								// no rescale needed
								userImageScaled = userImageOriginal;
								newWidth = originalWidth;
								newHeight = originalHeight;
							}

							if (userImageScaled != null) {
								int orientation = getOrientation(
										getApplicationContext(), selectedImage);
								if (orientation == -1 && orientation == 0) {
									userImage = userImageScaled;
								} else {
									Matrix matrix = new Matrix();
									matrix.postRotate(orientation);
									userImage = Bitmap.createBitmap(
											userImageScaled, 0, 0, newWidth,
											newHeight, matrix, true);

									if (userImage != userImageScaled) {
										userImageScaled.recycle();
										userImageScaled = null;
									}
								}
							}
						}

						if (userImage != null) {
							
							ImageView cover_image = (ImageView) findViewById(R.id.cover_image);
							cover_image.setImageBitmap(userImage);
							cover_image.refreshDrawableState();

							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							userImage.compress(Bitmap.CompressFormat.JPEG, 80,
									stream);
							byte[] byte_arr = stream.toByteArray();
							String image_str = Base64.encodeToString(byte_arr,Base64.DEFAULT);

							SexActivity.executeTask(new saveUserStringTask("save_cover_image", image_str), "save cover image");
						}
					} catch (FileNotFoundException e) {
					} catch (Exception e) {
					}
				}
				break;
			case SELECT_PHOTO_GALLERY:
				if (resultCode == RESULT_OK) {
					try {
						Uri selectedImage = imageReturnedIntent.getData();
						InputStream imageStream = getContentResolver()
								.openInputStream(selectedImage);

						BitmapFactory.Options bfOptions = new BitmapFactory.Options();
						bfOptions.inSampleSize = 2;

						Bitmap userImageOriginal = BitmapFactory.decodeStream(
								imageStream, null, bfOptions);
						Bitmap userImageScaled = null;
						Bitmap userImage = null;

						int originalWidth = 0;
						int originalHeight = 0;
						int newWidth = 0;
						int newHeight = 0;

						if (userImageOriginal != null) {
							originalWidth = userImageOriginal.getWidth();
							originalHeight = userImageOriginal.getHeight();
							int boundWidth = 640;
							int boundHeight = 480;

							if (originalHeight > boundHeight
									|| originalWidth > boundWidth) {
								// first check if we need to scale width
								if (originalWidth > boundWidth) {
									newWidth = boundWidth;
									newHeight = (newWidth * originalHeight)
											/ originalWidth;
								}

								// then check if we need to scale even with the
								// new height
								if (newHeight > boundHeight) {
									newHeight = boundHeight;
									newWidth = (newHeight * originalWidth)
											/ originalHeight;
								}

								if (newHeight != 0 && newWidth != 0) {
									// create new - scaled
									userImageScaled = Bitmap
											.createScaledBitmap(
													userImageOriginal,
													newWidth, newHeight, false);

									// recycle old crap
									if (userImageScaled != userImageOriginal) {
										userImageOriginal.recycle();
										userImageOriginal = null;
									}
								} else {
									// no rescale needed
									userImageScaled = userImageOriginal;
									newWidth = originalWidth;
									newHeight = originalHeight;
								}
							} else {
								// no rescale needed
								userImageScaled = userImageOriginal;
								newWidth = originalWidth;
								newHeight = originalHeight;
							}

							if (userImageScaled != null) {
								int orientation = getOrientation(
										getApplicationContext(), selectedImage);
								if (orientation == -1 && orientation == 0) {
									userImage = userImageScaled;
								} else {
									Matrix matrix = new Matrix();
									matrix.postRotate(orientation);
									userImage = Bitmap.createBitmap(
											userImageScaled, 0, 0, newWidth,
											newHeight, matrix, true);

									if (userImage != userImageScaled) {
										userImageScaled.recycle();
										userImageScaled = null;
									}
								}
							}
						}

						int imageIndex = 1;
						if (SexActivity.me.images.size() > 0) {
							imageIndex = SexActivity.me.images.size() + 1;
						}

						Integer imageRes = myprofile_gallery_content
								.getResources().getIdentifier(
										"gallery_image"
												+ Integer.toString(imageIndex),
										"id", this.getPackageName());
						if (imageRes != null && userImage != null) {
							ImageView galleryPhoto = (ImageView) myprofile_gallery_content
									.findViewById(imageRes).findViewById(
											R.id.gallery_image_src);
							ProgressBar galleryLoading = (ProgressBar) myprofile_gallery_content
									.findViewById(imageRes).findViewById(
											R.id.gallery_loading);
							ImageView galleryLock = (ImageView) myprofile_gallery_content
									.findViewById(imageRes).findViewById(
											R.id.gallery_image_lock);
							if (galleryPhoto != null) {
								galleryLoading
										.setVisibility(ProgressBar.INVISIBLE);
								galleryLock.setVisibility(ImageView.INVISIBLE);

								myprofile_gallery_content
										.findViewById(imageRes).setVisibility(
												RelativeLayout.VISIBLE);

								galleryPhoto.setImageBitmap(userImage);
								galleryPhoto.refreshDrawableState();

								SexActivity.me.images.add(String
										.valueOf(imageRes));
								SexActivity.me.images_passwords.add("");
								SexActivity.me.images_loaded.add(false);

								ByteArrayOutputStream stream = new ByteArrayOutputStream();
								userImage.compress(Bitmap.CompressFormat.JPEG,
										80, stream);
								byte[] byte_arr = stream.toByteArray();
								String image_str = Base64.encodeToString(
										byte_arr, Base64.DEFAULT);

								SexActivity.executeTask(
										new saveUserGalleryTask(galleryPhoto,
												image_str, imageIndex - 1),
										"save gallery image");
							}
						}
					} catch (FileNotFoundException e) {
					} catch (Exception e) {
					}
				}
				break;

			case SELECT_PHOTO:
				if (resultCode == RESULT_OK) {
					try {
						Uri selectedImage = imageReturnedIntent.getData();
						InputStream imageStream = getContentResolver()
								.openInputStream(selectedImage);

						BitmapFactory.Options bfOptions = new BitmapFactory.Options();
						bfOptions.inSampleSize = 2;

						Bitmap userImageOriginal = BitmapFactory.decodeStream(
								imageStream, null, bfOptions);
						Bitmap userImageScaled = null;
						Bitmap userImage = null;

						int originalWidth = 0;
						int originalHeight = 0;
						int newWidth = 0;
						int newHeight = 0;

						if (userImageOriginal != null) {
							originalWidth = userImageOriginal.getWidth();
							originalHeight = userImageOriginal.getHeight();
							int boundWidth = 640;
							int boundHeight = 480;

							if (originalHeight > boundHeight
									|| originalWidth > boundWidth) {
								// first check if we need to scale width
								if (originalWidth > boundWidth) {
									newWidth = boundWidth;
									newHeight = (newWidth * originalHeight)
											/ originalWidth;
								}

								// then check if we need to scale even with the
								// new height
								if (newHeight > boundHeight) {
									newHeight = boundHeight;
									newWidth = (newHeight * originalWidth)
											/ originalHeight;
								}

								if (newHeight != 0 && newWidth != 0) {
									// create new - scaled
									userImageScaled = Bitmap
											.createScaledBitmap(
													userImageOriginal,
													newWidth, newHeight, false);

									// recycle old crap
									if (userImageScaled != userImageOriginal) {
										userImageOriginal.recycle();
										userImageOriginal = null;
									}
								} else {
									// no rescale needed
									userImageScaled = userImageOriginal;
									newWidth = originalWidth;
									newHeight = originalHeight;
								}
							} else {
								// no rescale needed
								userImageScaled = userImageOriginal;
								newWidth = originalWidth;
								newHeight = originalHeight;
							}

							if (userImageScaled != null) {
								int orientation = getOrientation(
										getApplicationContext(), selectedImage);
								if (orientation == -1 && orientation == 0) {
									userImage = userImageScaled;
								} else {
									Matrix matrix = new Matrix();
									matrix.postRotate(orientation);
									userImage = Bitmap.createBitmap(
											userImageScaled, 0, 0, newWidth,
											newHeight, matrix, true);

									if (userImage != userImageScaled) {
										userImageScaled.recycle();
										userImageScaled = null;
									}
								}
							}
						}

						if (userImage != null) 
						{
							Bitmap circleBitmap = Bitmap.createBitmap(userImage.getWidth(), userImage.getHeight(), Bitmap.Config.ARGB_8888);

			    			BitmapShader shader = new BitmapShader(userImage,  TileMode.CLAMP, TileMode.CLAMP);
			    			Paint paint = new Paint();
			    			paint.setFlags(Paint.ANTI_ALIAS_FLAG); 
	    			        paint.setShader(shader);

			    			Canvas c = new Canvas(circleBitmap);
			    			c.drawCircle(userImage.getWidth()/2, userImage.getHeight()/2, 240, paint);

			    			// no need anymore
			    			//userImage.recycle();
			    			
							ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
							pickPhoto.setImageBitmap(circleBitmap);
							pickPhoto.refreshDrawableState();

							/*
							TextView textPick_A = (TextView) findViewById(R.id.text_pickphoto_a);
							textPick_A.setVisibility(TextView.INVISIBLE);

							TextView textPick_B = (TextView) findViewById(R.id.text_pickphoto_b);
							textPick_B.setVisibility(TextView.INVISIBLE);
							*/
							ByteArrayOutputStream stream = new ByteArrayOutputStream();
							userImage.compress(Bitmap.CompressFormat.JPEG, 80,
									stream);
							byte[] byte_arr = stream.toByteArray();
							String image_str = Base64.encodeToString(byte_arr,
									Base64.DEFAULT);

							SexActivity.executeTask(new saveUserStringTask(
									"save_image", image_str), "save image");
						}
					} catch (FileNotFoundException e) {
					} catch (Exception e) {
					}
				}
				break;
			}
		} else {
			//if (mHelper == null)
				//return;

			// Pass on the activity result to the helper for handling
			/*
			if (!mHelper.handleActivityResult(requestCode, resultCode,
					imageReturnedIntent)) {
				// not handled, so handle it ourselves (here's where you'd
				// perform any handling of activity results not related to
				// in-app
				// billing...
				super.onActivityResult(requestCode, resultCode,
						imageReturnedIntent);
			}
			*/
			/*
			 * else { Log.d(TAG, "onActivityResult handled by IABUtil."); }
			 */
		}
	}

	/** Verifies the developer payload of a purchase. */
	
	//boolean verifyDeveloperPayload(Purchase p) {
		// String payload = p.getDeveloperPayload();

		/*
		 * TODO: verify that the developer payload of the purchase is correct.
		 * It will be the same one that you sent when initiating the purchase.
		 * 
		 * WARNING: Locally generating a random string when starting a purchase
		 * and verifying it here might seem like a good approach, but this will
		 * fail in the case where the user purchases an item on one device and
		 * then uses your app on a different device, because on the other device
		 * you will not have access to the random string you originally
		 * generated.
		 * 
		 * So a good developer payload has these characteristics:
		 * 
		 * 1. If two different users purchase an item, the payload is different
		 * between them, so that one user's purchase can't be replayed to
		 * another user.
		 * 
		 * 2. The payload must be such that you can verify it even when the app
		 * wasn't the one who initiated the purchase flow (so that items
		 * purchased by the user on one device work on other devices owned by
		 * the user).
		 * 
		 * Using your own server to store and verify developer payloads across
		 * app installations is recommended.
		 */

		//return true;
	//}

	// Callback for when a purchase is finished
	/*
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			// Log.d(TAG, "Purchase finished: " + result + ", purchase: " +
			// purchase);

			// if we were disposed of in the meantime, quit.
			//if (mHelper == null)
				//return;

			if (result.isFailure()) {
				// suppress response
				if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED)
					return;

				complain(getString(R.string.google_purchase_error));
				return;
			}
			/*
			 * if (!verifyDeveloperPayload(purchase)) {
			 * complain("Error purchasing. Authenticity verification failed.");
			 * return; }
			 */
			// Log.d(TAG, "Purchase successful.");

			/*
			if (purchase.getSku().equals(SKU_CREDITS_1000)) {
				// bought 1/4 tank of gas. So consume it.
				// Log.d(TAG, "Purchase is gas. Starting gas consumption.");
				SexActivity.executeTask(new order(1), "order");
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			} else if (purchase.getSku().equals(SKU_CREDITS_5000)) {
				// bought the premium upgrade!
				// Log.d(TAG,
				// "Purchase is premium upgrade. Congratulating user.");
				// alert("Thank you for upgrading to premium!");
				// Boolean mIsPremium = true;
				SexActivity.executeTask(new order(2), "order");
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
			}/* else if (purchase.getSku().equals(SKU_CREDITS_1000)) {
				// bought the infinite gas subscription
				// Log.d(TAG, "Infinite gas subscription purchased.");
				// alert("Thank you for subscribing to infinite gas!");
				SexActivity.executeTask(new order(3), "order");
				// mHelper.consumeAsync(purchase, mConsumeFinishedListener);
				// mSubscribedToInfiniteGas = true;
			}*/
		//}
	//};
	// Called when consumption is complete
	/*
	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
		public void onConsumeFinished(Purchase purchase, IabResult result) {
			// Log.d(TAG, "Consumption finished. Purchase: " + purchase +
			// ", result: " + result);

			// if we were disposed of in the meantime, quit.
			//if (mHelper == null)
				//return;

			// We know this is the "gas" sku because it's the only one we
			// consume,
			// so we don't check which sku was consumed. If you have more than
			// one
			// sku, you probably should check...
			if (result.isSuccess()) {
				// successfully consumed, so we apply the effects of the item in
				// our
				// game world's logic, which in our case means filling the gas
				// tank a bit
				// Log.d(TAG, "Consumption successful. Provisioning.");
				// mTank = mTank == TANK_MAX ? TANK_MAX : mTank + 1;
				// saveData();
				// alert("You filled 1/4 tank. Your tank is now /4 full!");
			}
			/*
			 * else { complain("Error while consuming: " + result); }
			 */
			// updateUi();
			// setWaitScreen(false);
			// Log.d(TAG, "End consumption flow.");
		//}
	//};

	void blink(final ImageView element) {
		if (disableBlinks == true) {
			element.setVisibility(ImageView.INVISIBLE);
		} else {
			if (blinked < maxBlinks) {
				final Handler handler = new Handler();
				new Thread(new Runnable() {
					@Override
					public void run() {
						int timeToBlink = 100; // in milissegunds
						try {
							Thread.sleep(timeToBlink);
						} catch (Exception e) {
						}
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (element.getVisibility() == ImageView.VISIBLE)
									element.setVisibility(ImageView.INVISIBLE);
								else
									element.setVisibility(ImageView.VISIBLE);

								blinked++;
								blink(element);
							}
						});
					}
				}).start();
			} else
				element.setVisibility(ImageView.VISIBLE);

		}
	}
	
	public void saveCoverImage(String image)
	{
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method","set_user_cover_image"));
			nameValuePairs.add(new BasicNameValuePair("session",SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("image", image));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME + SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());
			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.getInt("success") == 1) {
					JSONObject jUser = json_data.getJSONObject("userdata");
					SexActivity.me = SexActivity.getUserFromJsonObj(jUser);

					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_image_save_success),
							Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_image_save_error),
							Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(),
						getString(R.string.myprofile_image_save_error),
						Toast.LENGTH_SHORT).show();

		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void saveUserImage(String image) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method",
					"set_user_image"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("image", image));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());
			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.getInt("success") == 1) {
					JSONObject jUser = json_data.getJSONObject("userdata");
					SexActivity.me = SexActivity.getUserFromJsonObj(jUser);

					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_image_save_success),
							Toast.LENGTH_SHORT).show();
				} else
					Toast.makeText(getApplicationContext(),
							getString(R.string.myprofile_image_save_error),
							Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(),
						getString(R.string.myprofile_image_save_error),
						Toast.LENGTH_SHORT).show();

		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void saveUserValue(String key, String value) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method",
					"set_user_value"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("key", key));
			nameValuePairs.add(new BasicNameValuePair(key, value));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());

			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.getInt("success") == 1) {
					JSONObject jUser = json_data.getJSONObject("userdata");
					SexActivity.me = SexActivity.getUserFromJsonObj(jUser);
				} else
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_error_later),
							Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_error_later),
						Toast.LENGTH_SHORT).show();

		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void saveUserGalleryImageValue(String image, final int imageIndex) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method",
					"set_user_gallery_image"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("image", image));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());
			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.getInt("success") == 1) {
					SexActivity.me.images.set(imageIndex,
							json_data.getString("image"));
					SexActivity.me.images_passwords.set(imageIndex, "");

					Integer imageRes = myprofile_gallery_content.getResources()
							.getIdentifier(
									"gallery_image"
											+ Integer.toString(imageIndex + 1),
									"id", this.getPackageName());
					if (imageRes != null) {
						ImageView galleryPhoto = (ImageView) myprofile_gallery_content
								.findViewById(imageRes).findViewById(
										R.id.gallery_image_src);
						ProgressBar galleryLoading = (ProgressBar) myprofile_gallery_content
								.findViewById(imageRes).findViewById(
										R.id.gallery_loading);
						ImageView galleryLock = (ImageView) myprofile_gallery_content
								.findViewById(imageRes).findViewById(
										R.id.gallery_image_lock);
						if (galleryPhoto != null) {
							galleryLock.setVisibility(ImageView.INVISIBLE);

							final String userImageUrl = SexActivity.REST_SERVER_NAME
									+ "/"
									+ SexActivity.me.images.get(imageIndex);

							if (SexActivity.me.images_loaded.get(imageIndex) == false) {
								SexActivity.me.images_loaded.set(imageIndex,
										true);
								myprofile_gallery_content
										.findViewById(imageRes).setVisibility(
												RelativeLayout.VISIBLE);
								SexActivity.executeTask(new setImageFromUrl(
										userImageUrl, galleryPhoto,
										galleryLoading),
										"load user gallery image");
							}

							galleryPhoto
									.setOnClickListener(new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											Bundle extras = new Bundle();
											extras.putInt("image_index",
													imageIndex);

											Intent gallery = new Intent(
													getBaseContext(),
													GalleryActivity.class);
											gallery.putExtras(extras);
											startActivity(gallery);
										}
									});
						}
					}
				}
			}
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void saveUserDataValue(String key, String value) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method",
					"set_user_data_value"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("key", key));
			nameValuePairs.add(new BasicNameValuePair(key, value));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());

			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.getInt("success") == 1) {
					JSONObject jUser = json_data.getJSONObject("userdata");
					SexActivity.me = SexActivity.getUserFromJsonObj(jUser);
				} else
					Toast.makeText(getApplicationContext(),
							getString(R.string.toast_error_later),
							Toast.LENGTH_SHORT).show();

			} else
				Toast.makeText(getApplicationContext(),
						getString(R.string.toast_error_later),
						Toast.LENGTH_SHORT).show();

		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public void drawDobDialog() {
		final Dialog dateDialog = new Dialog(this);
		dateDialog.setContentView(R.layout.dialog_date);
		dateDialog.setTitle(getString(R.string.myprofile_dob_hint));

		Button dateConfirm = (Button) dateDialog
				.findViewById(R.id.date_confirm);
		dateConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DatePicker dobInput = (DatePicker) dateDialog
						.findViewById(R.id.date_input);
				dobInput.clearFocus();
				String dobValue = dobInput.getDayOfMonth() + "."
						+ (dobInput.getMonth() + 1) + "." + dobInput.getYear();

				Button dobInputButton = (Button) myprofile_settings_content
						.findViewById(R.id.myprofile_dob_input);
				dobInputButton.setText(dobValue);

				SexActivity.executeTask(
						new saveUserStringTask("dob", dobValue), "save dob");
				SexActivity.me.dob = dobValue;
				SexActivity.me.setUserDob();
				try {
					if (isFinishing() == false)
						dateDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		// set values
		if (SexActivity.me.dob_day != 0 && SexActivity.me.dob_year != 0) {
			DatePicker dobInput = (DatePicker) dateDialog
					.findViewById(R.id.date_input);
			dobInput.updateDate(SexActivity.me.dob_year,
					SexActivity.me.dob_month - 1, SexActivity.me.dob_day);
		}

		try {
			if (isFinishing() == false)
				dateDialog.show();
		} catch (Exception e) {
		}

	}

	public void drawLookingforDialog() {
		final Dialog genderDialog = new Dialog(this);
		genderDialog.setContentView(R.layout.dialog_gender);
		genderDialog.setTitle(getString(R.string.myprofile_looking_for_hint));

		// user gender
		if (SexActivity.me.iwant != 0) {
			if (SexActivity.me.iwant == SexActivity.attributesGenderMan) {
				RadioButton radioLookingfor = (RadioButton) genderDialog
						.findViewById(R.id.gender_man);
				radioLookingfor.setChecked(true);
			} else if (SexActivity.me.iwant == SexActivity.attributesGenderWoman) {
				RadioButton radioLookingfor = (RadioButton) genderDialog
						.findViewById(R.id.gender_woman);
				radioLookingfor.setChecked(true);
			}/* else if (SexActivity.me.iwant == SexActivity.attributesGenderPairStraight) {
				RadioButton radioLookingfor = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_straight);
				radioLookingfor.setChecked(true);
			} else if (SexActivity.me.iwant == SexActivity.attributesGenderPairLesbian) {
				RadioButton radioLookingfor = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_lesbian);
				radioLookingfor.setChecked(true);
			} else if (SexActivity.me.iwant == SexActivity.attributesGenderPairGay) {
				RadioButton radioLookingfor = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_gay);
				radioLookingfor.setChecked(true);
			}*/
		}

		Button genderConfirm = (Button) genderDialog
				.findViewById(R.id.gender_confirm);
		genderConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int radioLookingforValue = 0;
				RadioGroup radioLookingfor = (RadioGroup) genderDialog
						.findViewById(R.id.gender_input);
				int lookinfor = radioLookingfor.getCheckedRadioButtonId();
				if(lookinfor == R.id.gender_man)
				{
					radioLookingforValue = SexActivity.attributesGenderMan;
				}
				else if(lookinfor == R.id.gender_woman)
				{
					radioLookingforValue = SexActivity.attributesGenderWoman;
				}
				/*
				switch (radioLookingfor.getCheckedRadioButtonId()) {
				case R.id.gender_man:
					radioLookingforValue = SexActivity.attributesGenderMan;
					break;
				case R.id.gender_woman:
					radioLookingforValue = SexActivity.attributesGenderWoman;
					break;
				case R.id.gender_pair_straight:
					radioLookingforValue = SexActivity.attributesGenderPairStraight;
					break;
				case R.id.gender_pair_lesbian:
					radioLookingforValue = SexActivity.attributesGenderPairLesbian;
					break;
				case R.id.gender_pair_gay:
					radioLookingforValue = SexActivity.attributesGenderPairGay;
					break;
				}
					*/
				if (radioLookingforValue != 0) {
					setGenderValue(radioLookingforValue, "iwant");
					SexActivity
							.executeTask(
									new saveUserStringTask("iwant", String
											.valueOf(radioLookingforValue)),
									"save iwant");
					SexActivity.me.iwant = radioLookingforValue;
				}

				try {
					if (isFinishing() == false)
						genderDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				genderDialog.show();
		} catch (Exception e) {
		}
	}

	public void setEthnicityValue(int value) {
		if (value != 0) {
			Button ethnicityInput = (Button) myprofile_about_content
					.findViewById(R.id.myprofile_looks_ethnicity_input);

			if (SexActivity.attributesEthnicityAsian == value) {
				ethnicityInput
						.setText(getString(R.string.myprofile_looks_ethnicity_hint)
								+ ": " + getString(R.string.ethnicity_asian));
			} else if (SexActivity.attributesEthnicityBlack == value) {
				ethnicityInput
						.setText(getString(R.string.myprofile_looks_ethnicity_hint)
								+ ": " + getString(R.string.ethnicity_black));
			} else if (SexActivity.attributesEthnicityOther == value) {
				ethnicityInput
						.setText(getString(R.string.myprofile_looks_ethnicity_hint)
								+ ": " + getString(R.string.ethnicity_other));
			} else if (SexActivity.attributesEthnicityLatino == value) {
				ethnicityInput
						.setText(getString(R.string.myprofile_looks_ethnicity_hint)
								+ ": " + getString(R.string.ethnicity_latino));
			} else if (SexActivity.attributesEthnicityWhite == value) {
				ethnicityInput
						.setText(getString(R.string.myprofile_looks_ethnicity_hint)
								+ ": " + getString(R.string.ethnicity_white));
			}
		}
	}

	public void setGenderValue(int value, String part) {
		if (value != 0) {
			Button genderInput = null;
			if (part.equalsIgnoreCase("gender"))
				genderInput = (Button) myprofile_settings_content
						.findViewById(R.id.myprofile_gender_input);
			else
				genderInput = (Button) myprofile_settings_content
						.findViewById(R.id.myprofile_looking_for_input);

			if (value == SexActivity.attributesGenderMan) {
				genderInput.setText(getString(R.string.gender_man));
			} else if (value == SexActivity.attributesGenderWoman) {
				genderInput.setText(getString(R.string.gender_woman));
			}/* else if (value == SexActivity.attributesGenderPairStraight) {
				genderInput.setText(getString(R.string.gender_pair_straight));
			} else if (value == SexActivity.attributesGenderPairLesbian) {
				genderInput.setText(getString(R.string.gender_pair_lesbian));
			} else if (value == SexActivity.attributesGenderPairGay) {
				genderInput.setText(getString(R.string.gender_pair_gay));
			}*/
		}
	}

	public void setBodyValue(int value) {
		Button bodyInput = (Button) myprofile_about_content
				.findViewById(R.id.myprofile_looks_body_input);

		if (SexActivity.attributesBodySexy == value) {
			bodyInput.setText(getString(R.string.body_sexy));
		} else if (SexActivity.attributesBodySporty == value) {
			bodyInput.setText(getString(R.string.body_sporty));
		} else if (SexActivity.attributesBodySlender == value) {
			bodyInput.setText(getString(R.string.body_slender));
		} else if (SexActivity.attributesBodyJuicy == value) {
			bodyInput.setText(getString(R.string.body_juicy));
		} else if (SexActivity.attributesBodyMuscular == value) {
			bodyInput.setText(getString(R.string.body_muscle));
		}
	}

	public void setLovesValue(int value) {
		/*
		String inputValue = getString(R.string.myprofile_what_ilove_hint);
		if (value != 0) {
			inputValue = "";
			if ((SexActivity.attributesLovesMissionary & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_missionary);
			}

			if ((SexActivity.attributesLoves69 & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_69);
			}

			if ((SexActivity.attributesLovesDoggie & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_doggie);
			}

			if ((SexActivity.attributesLovesBJ & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_bj);
			}

			if ((SexActivity.attributesLovesPiss & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_piss);
			}

			if ((SexActivity.attributesLovesSM & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_sm);
			}

			if ((SexActivity.attributesLovesOrgy & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_orgy);
			}

			if ((SexActivity.attributesLovesFetish & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_fetish);
			}

			if ((SexActivity.attributesLovesHJ & value) != 0) {
				inputValue += ((inputValue.equalsIgnoreCase("") == false) ? ", "
						: "")
						+ getString(R.string.loves_hj);
			}
			
		}
		*/
		//Button lovesInput = (Button) myprofile_about_content
		//		.findViewById(R.id.myprofile_ilove_input);
		//lovesInput.setText(inputValue);
	}

	public void drawBodyDialog() {
		final Dialog bodyDialog = new Dialog(this);
		bodyDialog.setContentView(R.layout.dialog_body);
		bodyDialog.setTitle(getString(R.string.myprofile_looks_body_hint));

		if (SexActivity.me.body != 0) {
			if (SexActivity.me.body == SexActivity.attributesBodySexy) {
				RadioButton radioBody = (RadioButton) bodyDialog
						.findViewById(R.id.body_sexy);
				radioBody.setChecked(true);
			} else if (SexActivity.me.body == SexActivity.attributesBodySporty) {
				RadioButton radioBody = (RadioButton) bodyDialog
						.findViewById(R.id.body_sporty);
				radioBody.setChecked(true);
			} else if (SexActivity.me.body == SexActivity.attributesBodySlender) {
				RadioButton radioBody = (RadioButton) bodyDialog
						.findViewById(R.id.body_slender);
				radioBody.setChecked(true);
			} else if (SexActivity.me.body == SexActivity.attributesBodyJuicy) {
				RadioButton radioBody = (RadioButton) bodyDialog
						.findViewById(R.id.body_juicy);
				radioBody.setChecked(true);
			} else if (SexActivity.me.body == SexActivity.attributesBodyMuscular) {
				RadioButton radioBody = (RadioButton) bodyDialog
						.findViewById(R.id.body_muscle);
				radioBody.setChecked(true);
			}
		}

		Button bodyConfirm = (Button) bodyDialog
				.findViewById(R.id.body_confirm);
		bodyConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int bodyValue = 0;
				RadioGroup body = (RadioGroup) bodyDialog
						.findViewById(R.id.body_input);
				int lookinbody = body.getCheckedRadioButtonId();
				if(lookinbody == R.id.body_sexy)
				{
					bodyValue = SexActivity.attributesBodySexy;
				}
				else if(lookinbody == R.id.body_sexy)
				{
					bodyValue = SexActivity.attributesBodySexy;
				}
				else if(lookinbody == R.id.body_sporty)
				{
					bodyValue = SexActivity.attributesBodySporty;
				}
				else if(lookinbody == R.id.body_slender)
				{
					bodyValue = SexActivity.attributesBodySlender;
				}
				else if(lookinbody == R.id.body_juicy)
				{
					bodyValue = SexActivity.attributesBodyJuicy;
				}
				else if(lookinbody == R.id.body_muscle)
				{
					bodyValue = SexActivity.attributesBodyMuscular;
				}
				/*
				switch (body.getCheckedRadioButtonId()) {
				case R.id.body_sexy:
					bodyValue = SexActivity.attributesBodySexy;
					break;
				case R.id.body_sporty:
					bodyValue = SexActivity.attributesBodySporty;
					break;
				case R.id.body_slender:
					bodyValue = SexActivity.attributesBodySlender;
					break;
				case R.id.body_juicy:
					bodyValue = SexActivity.attributesBodyJuicy;
					break;
				case R.id.body_muscle:
					bodyValue = SexActivity.attributesBodyMuscular;
					break;
				}
				*/
				if (bodyValue != 0) {
					setBodyValue(bodyValue);
					SexActivity.executeTask(new saveUserDataStringTask("body",
							String.valueOf(bodyValue)), "save body");
					SexActivity.me.body = bodyValue;
				}

				try {
					if (isFinishing() == false)
						bodyDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				bodyDialog.show();
		} catch (Exception e) {
		}
	}

	public void drawLovesDialog() {
		final Dialog lovesDialog = new Dialog(this);
		lovesDialog.setContentView(R.layout.dialog_loves);
		lovesDialog.setTitle(getString(R.string.myprofile_what_ilove_hint));

		final Button loves_missionary = (Button) lovesDialog
				.findViewById(R.id.loves_missionary);
		if ((SexActivity.attributesLovesMissionary & SexActivity.me.loves) != 0) {
			loves_missionary
					.setBackgroundResource(R.drawable.loves_missionary_hover);
		}
		loves_missionary.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesMissionary & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesMissionary;
					loves_missionary
							.setBackgroundResource(R.drawable.loves_missionary_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesMissionary;
					loves_missionary
							.setBackgroundResource(R.drawable.loves_missionary);
				}
			}
		});

		final Button loves_69 = (Button) lovesDialog
				.findViewById(R.id.loves_69);
		if ((SexActivity.attributesLoves69 & SexActivity.me.loves) != 0) {
			loves_69.setBackgroundResource(R.drawable.loves_69_hover);
		}
		loves_69.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLoves69 & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLoves69;
					loves_69.setBackgroundResource(R.drawable.loves_69_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLoves69;
					loves_69.setBackgroundResource(R.drawable.loves_69);
				}
			}
		});

		final Button loves_doggie = (Button) lovesDialog
				.findViewById(R.id.loves_doggie);
		if ((SexActivity.attributesLovesDoggie & SexActivity.me.loves) != 0) {
			loves_doggie.setBackgroundResource(R.drawable.loves_doggie_hover);
		}
		loves_doggie.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesDoggie & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesDoggie;
					loves_doggie
							.setBackgroundResource(R.drawable.loves_doggie_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesDoggie;
					loves_doggie.setBackgroundResource(R.drawable.loves_doggie);
				}
			}
		});

		final Button loves_bj = (Button) lovesDialog
				.findViewById(R.id.loves_bj);
		if ((SexActivity.attributesLovesBJ & SexActivity.me.loves) != 0) {
			loves_bj.setBackgroundResource(R.drawable.loves_bj_hover);
		}
		loves_bj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesBJ & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesBJ;
					loves_bj.setBackgroundResource(R.drawable.loves_bj_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesBJ;
					loves_bj.setBackgroundResource(R.drawable.loves_bj);
				}
			}
		});

		final Button loves_piss = (Button) lovesDialog
				.findViewById(R.id.loves_piss);
		if ((SexActivity.attributesLovesPiss & SexActivity.me.loves) != 0) {
			loves_piss.setBackgroundResource(R.drawable.loves_piss_hover);
		}
		loves_piss.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesPiss & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesPiss;
					loves_piss
							.setBackgroundResource(R.drawable.loves_piss_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesPiss;
					loves_piss.setBackgroundResource(R.drawable.loves_piss);
				}
			}
		});

		final Button loves_sm = (Button) lovesDialog
				.findViewById(R.id.loves_sm);
		if ((SexActivity.attributesLovesSM & SexActivity.me.loves) != 0) {
			loves_sm.setBackgroundResource(R.drawable.loves_sm_hover);
		}
		loves_sm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesSM & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesSM;
					loves_sm.setBackgroundResource(R.drawable.loves_sm_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesSM;
					loves_sm.setBackgroundResource(R.drawable.loves_sm);
				}
			}
		});

		final Button loves_orgy = (Button) lovesDialog
				.findViewById(R.id.loves_orgy);
		if ((SexActivity.attributesLovesOrgy & SexActivity.me.loves) != 0) {
			loves_orgy.setBackgroundResource(R.drawable.loves_orgy_hover);
		}
		loves_orgy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesOrgy & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesOrgy;
					loves_orgy
							.setBackgroundResource(R.drawable.loves_orgy_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesOrgy;
					loves_orgy.setBackgroundResource(R.drawable.loves_orgy);
				}
			}
		});

		final Button loves_fetish = (Button) lovesDialog
				.findViewById(R.id.loves_fetish);
		if ((SexActivity.attributesLovesFetish & SexActivity.me.loves) != 0) {
			loves_fetish.setBackgroundResource(R.drawable.loves_fetish_hover);
		}
		loves_fetish.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesFetish & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesFetish;
					loves_fetish
							.setBackgroundResource(R.drawable.loves_fetish_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesFetish;
					loves_fetish.setBackgroundResource(R.drawable.loves_fetish);
				}
			}
		});

		final Button loves_hj = (Button) lovesDialog
				.findViewById(R.id.loves_hj);
		if ((SexActivity.attributesLovesHJ & SexActivity.me.loves) != 0) {
			loves_hj.setBackgroundResource(R.drawable.loves_hj_hover);
		}
		loves_hj.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((SexActivity.attributesLovesHJ & SexActivity.me.loves) == 0) {
					SexActivity.me.loves += SexActivity.attributesLovesHJ;
					loves_hj.setBackgroundResource(R.drawable.loves_hj_hover);
				} else {
					SexActivity.me.loves -= SexActivity.attributesLovesHJ;
					loves_hj.setBackgroundResource(R.drawable.loves_hj);
				}
			}
		});

		Button lovesConfirm = (Button) lovesDialog
				.findViewById(R.id.loves_confirm);
		lovesConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setLovesValue(SexActivity.me.loves);
				SexActivity.executeTask(new saveUserDataStringTask("loves",
						String.valueOf(SexActivity.me.loves)), "save describe");

				try {
					if (isFinishing() == false)
						lovesDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				lovesDialog.show();
		} catch (Exception e) {
		}
	}

	public int loveSelected(ArrayList<String> values, String value) {
		if (values != null && values.size() > 0) {
			for (int i = 0; i < values.size(); i++) {
				if (value.equalsIgnoreCase(values.get(i)) == true)
					return i;

			}
			return 100;
		} else
			return 100;
	}

	public void drawEthnicityDialog() {
		final Dialog ethnicityDialog = new Dialog(this);
		ethnicityDialog.setContentView(R.layout.dialog_ethnicity);
		ethnicityDialog
				.setTitle(getString(R.string.myprofile_looks_ethnicity_hint));

		if (SexActivity.me.ethnicity != 0) {
			if (SexActivity.me.ethnicity == SexActivity.attributesEthnicityAsian) {
				RadioButton radioEthnicity = (RadioButton) ethnicityDialog
						.findViewById(R.id.ethnicity_asian);
				radioEthnicity.setChecked(true);
			} else if (SexActivity.me.ethnicity == SexActivity.attributesEthnicityBlack) {
				RadioButton radioEthnicity = (RadioButton) ethnicityDialog
						.findViewById(R.id.ethnicity_black);
				radioEthnicity.setChecked(true);
			} else if (SexActivity.me.ethnicity == SexActivity.attributesEthnicityWhite) {
				RadioButton radioEthnicity = (RadioButton) ethnicityDialog
						.findViewById(R.id.ethnicity_white);
				radioEthnicity.setChecked(true);
			} else if (SexActivity.me.ethnicity == SexActivity.attributesEthnicityLatino) {
				RadioButton radioEthnicity = (RadioButton) ethnicityDialog
						.findViewById(R.id.ethnicity_latino);
				radioEthnicity.setChecked(true);
			} else if (SexActivity.me.ethnicity == SexActivity.attributesEthnicityOther) {
				RadioButton radioEthnicity = (RadioButton) ethnicityDialog
						.findViewById(R.id.ethnicity_other);
				radioEthnicity.setChecked(true);
			}
		}

		Button ethnicityConfirm = (Button) ethnicityDialog
				.findViewById(R.id.ethnicity_confirm);
		ethnicityConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int ethnicityValue = 0;
				RadioGroup ethnicity = (RadioGroup) ethnicityDialog
						.findViewById(R.id.ethnicity_input);
				int lookingeth = ethnicity.getCheckedRadioButtonId();
				if(lookingeth == R.id.ethnicity_asian)
				{
					ethnicityValue = SexActivity.attributesEthnicityAsian;
				}
				else if(lookingeth == R.id.ethnicity_black)
				{
					ethnicityValue = SexActivity.attributesEthnicityBlack;
				}
				else if(lookingeth == R.id.ethnicity_other)
				{
					ethnicityValue = SexActivity.attributesEthnicityOther;
				}
				else if(lookingeth == R.id.ethnicity_white)
				{
					ethnicityValue = SexActivity.attributesEthnicityWhite;
				}
				else if(lookingeth == R.id.ethnicity_latino)
				{
					ethnicityValue = SexActivity.attributesEthnicityLatino;
				}
				/*
				switch (ethnicity.getCheckedRadioButtonId()) {
				case R.id.ethnicity_asian:
					ethnicityValue = SexActivity.attributesEthnicityAsian;
					break;
				case R.id.ethnicity_black:
					ethnicityValue = SexActivity.attributesEthnicityBlack;
					break;
				case R.id.ethnicity_other:
					ethnicityValue = SexActivity.attributesEthnicityOther;
					break;
				case R.id.ethnicity_white:
					ethnicityValue = SexActivity.attributesEthnicityWhite;
					break;
				case R.id.ethnicity_latino:
					ethnicityValue = SexActivity.attributesEthnicityLatino;
					break;
				}
				*/
				if (ethnicityValue != 0) {
					setEthnicityValue(ethnicityValue);
					SexActivity.executeTask(new saveUserDataStringTask(
							"ethnicity", String.valueOf(ethnicityValue)),
							"save ethnicity");
					SexActivity.me.ethnicity = ethnicityValue;
				}

				try {
					if (isFinishing() == false)
						ethnicityDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				ethnicityDialog.show();
		} catch (Exception e) {
		}
	}

	public void drawGenderDialog() {
		final Dialog genderDialog = new Dialog(this);
		genderDialog.setContentView(R.layout.dialog_gender);
		genderDialog.setTitle(getString(R.string.myprofile_gender_hint));

		// user gender
		if (SexActivity.me.gender != 0) {
			if (SexActivity.me.gender == SexActivity.attributesGenderMan) {
				RadioButton radioIam = (RadioButton) genderDialog
						.findViewById(R.id.gender_man);
				radioIam.setChecked(true);
			} else if (SexActivity.me.gender == SexActivity.attributesGenderWoman) {
				RadioButton radioIam = (RadioButton) genderDialog
						.findViewById(R.id.gender_woman);
				radioIam.setChecked(true);
			}/* else if (SexActivity.me.gender == SexActivity.attributesGenderPairStraight) {
				RadioButton radioIam = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_straight);
				radioIam.setChecked(true);
			} else if (SexActivity.me.gender == SexActivity.attributesGenderPairLesbian) {
				RadioButton radioIam = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_lesbian);
				radioIam.setChecked(true);
			} else if (SexActivity.me.gender == SexActivity.attributesGenderPairGay) {
				RadioButton radioIam = (RadioButton) genderDialog
						.findViewById(R.id.gender_pair_gay);
				radioIam.setChecked(true);
			}
			*/
		}

		Button genderConfirm = (Button) genderDialog
				.findViewById(R.id.gender_confirm);
		genderConfirm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int iAmValue = 0;
				RadioGroup radioIam = (RadioGroup) genderDialog
						.findViewById(R.id.gender_input);
				int im = radioIam.getCheckedRadioButtonId();
				if(im == R.id.gender_man)
				{
					iAmValue = SexActivity.attributesGenderMan;
				}
				else if(im == R.id.gender_woman)
				{
					iAmValue = SexActivity.attributesGenderWoman;
				}
						/*
				switch (radioIam.getCheckedRadioButtonId()) {
				case R.id.gender_man:
					iAmValue = SexActivity.attributesGenderMan;
					break;
				case R.id.gender_woman:
					iAmValue = SexActivity.attributesGenderWoman;
					break;
				case R.id.gender_pair_straight:
					iAmValue = SexActivity.attributesGenderPairStraight;
					break;
				case R.id.gender_pair_lesbian:
					iAmValue = SexActivity.attributesGenderPairLesbian;
					break;
				case R.id.gender_pair_gay:
					iAmValue = SexActivity.attributesGenderPairGay;
					break;
				}
				*/
				if (iAmValue != 0) {
					setGenderValue(iAmValue, "gender");
					SexActivity.executeTask(new saveUserStringTask("gender",
							String.valueOf(iAmValue)), "save gender");
					SexActivity.me.gender = iAmValue;

					ImageView pickPhoto = (ImageView) findViewById(R.id.pickphoto);
					if (pickPhoto != null
							&& (SexActivity.me.image == null || SexActivity.me.image
									.equalsIgnoreCase("") == true)) {
						setNoPhotoImage(pickPhoto, SexActivity.me.gender);
					}
				}

				try {
					if (isFinishing() == false)
						genderDialog.cancel();
				} catch (Exception e) {
				}
			}
		});

		try {
			if (isFinishing() == false)
				genderDialog.show();
		} catch (Exception e) {
		}
	}

	public void setNoPhotoImage(ImageView imageView, int gender) 
	{
		/*
		TextView textPick_A = (TextView) findViewById(R.id.text_pickphoto_a);
		if (textPick_A != null
				&& textPick_A.getVisibility() == TextView.INVISIBLE)
			textPick_A.setVisibility(TextView.VISIBLE);

		TextView textPick_B = (TextView) findViewById(R.id.text_pickphoto_b);
		if (textPick_B != null
				&& textPick_B.getVisibility() == TextView.INVISIBLE)
			textPick_B.setVisibility(TextView.VISIBLE);
		*/
		if (gender == SexActivity.attributesGenderMan) {
			imageView.setImageResource(R.drawable.myprofile_nophoto_man);
		} else if (gender == SexActivity.attributesGenderWoman) {
			imageView.setImageResource(R.drawable.myprofile_nophoto_woman);
		} else if (gender == SexActivity.attributesGenderPairStraight) {
			imageView
					.setImageResource(R.drawable.myprofile_nophoto_pairmanwomain);
		} else if (gender == SexActivity.attributesGenderPairLesbian) {
			imageView.setImageResource(R.drawable.myprofile_nophoto_pairwoman);
		} else if (gender == SexActivity.attributesGenderPairGay) {
			imageView.setImageResource(R.drawable.myprofile_nophoto_pairman);
		}
	}

	private class saveUserStringTask extends AsyncTask<String, Integer, String> {
		public String key = "";
		public String value = "";

		public saveUserStringTask(String pKey, String pValue) {
			super();
			key = pKey;
			value = pValue;
		}

		protected void onPostExecute(String result) {
		}

		@Override
		protected String doInBackground(String... params) {
			if (key.equals("save_image") == true)
				saveUserImage(value);
			else if(key.equals("save_cover_image") == true)
				saveCoverImage(value);
			else
				saveUserValue(key, value);

			return "ok";
		}
	}

	private class saveUserDataStringTask extends
			AsyncTask<String, Integer, String> {
		public String key = "";
		public String value = "";

		public saveUserDataStringTask(String pKey, String pValue) {
			super();
			key = pKey;
			value = pValue;
		}

		protected void onPostExecute(String result) {
		}

		@Override
		protected String doInBackground(String... params) {
			saveUserDataValue(key, value);
			return "ok";
		}
	}

	private class saveUserGalleryTask extends
			AsyncTask<String, Integer, String> {
		public ImageView view = null;
		public String image_str = "";
		public int image_index;

		public saveUserGalleryTask(ImageView pView, String pImage,
				int pImageIndex) {
			super();
			view = pView;
			image_str = pImage;
			image_index = pImageIndex;
		}

		protected void onPostExecute(String result) {
			String userImageUrl = SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_IMAGE_SIZE_148 + "/"
					+ SexActivity.me.images.get(image_index);
			SexActivity.executeTask(new setImageFromUrl(userImageUrl, view),
					"load user gallery image");
		}

		@Override
		protected String doInBackground(String... params) {
			saveUserGalleryImageValue(image_str, image_index);
			return "ok";
		}

	}

	class getUserData extends AsyncTask<String, Integer, String> {
		protected void onPostExecute(String result) {
			proceedMyProfile();
		}

		@Override
		protected String doInBackground(String... params) {
			httpGetUserData();
			return "ok";
		}
	}

	class deleteUser extends AsyncTask<String, Integer, Boolean> {
		protected void onPostExecute(Boolean result) {
			if (result == true)
				proceedAccountAbortion();
			else
				Toast.makeText(getApplicationContext(),
						getString(R.string.delete_error), Toast.LENGTH_LONG)
						.show();

		}

		@Override
		protected Boolean doInBackground(String... params) {
			return httpDeleteUser();
		}
	}

	class unlockFeature extends AsyncTask<String, Integer, Boolean> {
		public int featureId;
		public String duration = "month";
		public String lastError = "";
		public Dialog dialog;

		protected void onPostExecute(Boolean result) {
			if (!isFinishing())
				hideLoadingDialog();

			if (result == true) {
				if (dialog != null && !isFinishing())
					dialog.cancel();

				updatePremiumFeatures();
				TextView credits = (TextView) myprofile_premium_content
						.findViewById(R.id.credits);
				credits.setText(String.valueOf(SexActivity.me.credits) + " "
						+ getString(R.string.credits));
			} else {
				Toast.makeText(getApplicationContext(), lastError,
						Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean success = httpUnlockFeature(this, featureId, duration);
			if (success)
				httpGetUserData();
			return success;
		}

		@Override
		protected void onPreExecute() {
			if (!isFinishing())
				showLoadingDialog();
		}
	}

	public Boolean httpUnlockFeature(unlockFeature instance, int feature,
			String duration) {
		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method",
					"unlock_feature"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("feature", String
					.valueOf(feature)));
			nameValuePairs.add(new BasicNameValuePair("duration", duration));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);
			String json = EntityUtils.toString(response.getEntity());

			instance.lastError = "";

			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.opt("credits") != null) {
					SexActivity.me.credits = json_data.getInt("credits");
					return true;
				} else if (json_data.opt("error") != null) {
					instance.lastError = json_data.getString("error");
					return false;
				}
			}
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}

		return true;
	}

	public Boolean httpOrder(Integer pOrder) {
		if (pOrder == null)
			return false;

		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					SexActivity.httpTimeout);
			HttpConnectionParams.setSoTimeout(httpParameters,
					SexActivity.soTimeout);

			HttpClient httpclient = new DefaultHttpClient(httpParameters);
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("method", "order"));
			nameValuePairs.add(new BasicNameValuePair("session",
					SexActivity.me.session));
			nameValuePairs.add(new BasicNameValuePair("token",
					SexActivity.APP_TOKEN));
			nameValuePairs.add(new BasicNameValuePair("essence",
					SexActivity.APP_ESSENCE));
			nameValuePairs.add(new BasicNameValuePair("order", String
					.valueOf(pOrder)));

			HttpPost httpPost = new HttpPost(SexActivity.REST_SERVER_NAME
					+ SexActivity.REST_SCRIPT);
			httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
			HttpResponse response = httpclient.execute(httpPost);

			String json = EntityUtils.toString(response.getEntity());
			// Log.d("json", json);
			if (json.equalsIgnoreCase("") == false) {
				JSONObject json_data = new JSONObject(json);
				if (json_data.opt("credits") != null
						&& json_data.opt("premium") != null) {
					SexActivity.me.credits = json_data.getInt("credits");
					SexActivity.me.premium = json_data.getInt("premium");
				}
			}
		} catch (UnsupportedEncodingException e) {
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		} catch (Exception e) {
		}

		return true;
	}

	class order extends AsyncTask<String, Integer, Boolean> {
		Integer order = null;

		public order(Integer pOrder) {
			super();
			order = pOrder;
		}

		protected void onPostExecute(Boolean result) {
			TextView credits = (TextView) myprofile_premium_content
					.findViewById(R.id.credits);
			credits.setText(String.valueOf(SexActivity.me.credits) + " "
					+ getString(R.string.credits));
		}

		@Override
		protected Boolean doInBackground(String... params) {
			return httpOrder(order);
		}
	}

	void complain(String message) {
		Log.e("IAB", "**** SL Error: " + message);
		alert("Error: " + message);
	}

	void alert(String message) {
		AlertDialog.Builder bld = new AlertDialog.Builder(this);
		bld.setMessage(message);
		bld.setNeutralButton("OK", null);
		Log.d("IAB", "Showing alert dialog: " + message);
		try {
			if (isFinishing() == false)
				bld.create().show();
		} catch (Exception e) {
		}

	}
}
