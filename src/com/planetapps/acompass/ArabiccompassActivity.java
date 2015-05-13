package com.planetapps.acompass;

import java.util.Date;
import java.util.Locale;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.google.ads.AdRequest.ErrorCode;

import com.mobfox.sdk.BannerListener;
import com.mobfox.sdk.MobFoxView;
import com.mobfox.sdk.Mode;
import com.mobfox.sdk.RequestException;
import com.planetapps.qiblacompass.data.GlobalData;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;



/**
 * This class extends the SensorsActivity and is designed tie the CompassView and Sensors together.
 * 
 * @author remis haroon
 */
public class ArabiccompassActivity extends SensorsActivity implements BannerListener, AdListener{
	
	
	private static final String TAG = "QiblaCompassActivity";
	private static final String MY_AD_UNIT_ID = "a14f086c6528a8d";
	private static Boolean loc_fetch = false;
	//private static WakeLock wakeLock = null;
	
	// Mobfox starts
	//Include your own AdMob publisher id
	private final static String ADMOB_PUBLISHER_ID = "a1496ced2842262";
	private final static String MOBFOX_PUBLISHER_ID = "90396e4e91234f95b0f8d5a73c8fc29d";
	
    private final static int REFRESH_AD = 101;
	private final static long REFRESH_INTERVAL = 30000;
	

    private RelativeLayout layout;
    
	private ViewFlipper viewFlipper;
	private MobFoxView mobfoxView;
	private AdView adMobView;
	private AdRequest adMobRequest;		

	private Handler refreshHandler;
	private Looper refreshLooper;
	//mobfox ends
	
	
	
	
	int DialCode = 1;
	int BgCode = 9;
	private AdView adView;


	private static TextView text = null;
    private static View compassView = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        Log.i(TAG,"onCreate()");

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        

        
        setContentView(R.layout.main);
        
		layout = (RelativeLayout) findViewById(R.id.mobfoxContent);

		mobfoxView = new MobFoxView(this, "7563531fdb72202f348dd5f0393109ee", Mode.LIVE, true, true);
		
		mobfoxView.setBannerListener(this);
		mobfoxView.setVisibility(View.GONE);
//		mobfoxView.setOnClickListener(new OnClickListener(){
//
//			@Override
//			public void onClick(View v) {
//				updateHandler.post(new Runnable() {
//					public void run() {
//						TextView errorText = (TextView)findViewById(R.id.errorText);
//						errorText.setText("View clicked ad");
//						Log.v("MbFoxExample","OnClick");
//					}
//				});
//				
//			}});
		layout.addView(mobfoxView);
		
        Log.i(TAG, "Activity created");
        
        //new Airpush(getApplicationContext(),"34929","1325400684898556096",false,true,true);
        //turnGPSOn();
        
		
        // Create the adView
//        adView = new AdView(this, AdSize.BANNER, MY_AD_UNIT_ID);

        // Lookup your LinearLayout assuming it’s been given
        // the attribute android:id="@+id/mainLayout"
        //LinearLayout layout = (LinearLayout)findViewById(R.id.Mainlayout);
//        RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad);
        // Add the adView to it
//        layout.addView(adView);

        // Initiate a generic request to load it with an ad
//        adView.loadAd(new AdRequest());
        
        String label = getResources().getString(R.string.label);
        text = (TextView) findViewById(R.id.text);
        Typeface face=Typeface.createFromAsset(getAssets(), "fonts/LateefRegOT.ttf");
        text.setTypeface(face);        
        text.setTextSize(30);
        text.setText(label);
        compassView = findViewById(R.id.compass);
        
        SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        
        if (myPrefs != null)
        {
	        DialCode = myPrefs.getInt("DialCode", DialCode);
	        BgCode = myPrefs.getInt("BgCode", BgCode);
        }
        
        if (DialCode != 0 && BgCode != 0)
        {
	        changeBg(BgCode);
			View compassView = findViewById(R.id.compass);
			GlobalData.setDialCode(DialCode);
			compassView.refreshDrawableState();
        }
    }
    

    
   
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onDestroy() {
    	//turnGPSOff();
    	super.onDestroy();
    	

    	
    	Log.i(TAG,"onDestroy()");
    	System.out.println("DialCode >"+DialCode+" BgCode >"+BgCode);
    	SharedPreferences myPrefs = this.getSharedPreferences("myPrefs", MODE_WORLD_READABLE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putInt("DialCode", DialCode);
        prefsEditor.putInt("BgCode", BgCode);
        prefsEditor.commit();        
        
        finish();
        System.runFinalizersOnExit(true);
        System.exit(0);
        Log.i(TAG, "Activity destroyed");
    }
    
    
    
//    private void turnGPSOff(){
//        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
//
//        if(provider.contains("gps")){ //if gps is enabled
//            final Intent poke = new Intent();
//            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
//            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
//            poke.setData(Uri.parse("3")); 
//            sendBroadcast(poke);
//        }
//    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void onStart() {
    	super.onStart();
    	Log.i(TAG,"onStart()");
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void onStop() {
    	super.onStop();
    	Log.i(TAG,"onStop()");
    }
    
    /**
     * {@inheritDoc}
     */
	@Override
	public void onResume() {
		super.onResume();
	
		Log.i(TAG,"onResume()");
		
		//wakeLock.acquire();
	}
    
    /**
     * {@inheritDoc}
     */
	@Override
	public void onPause() {
		super.onPause();
	
		Log.i(TAG,"onPause()");
		
		//wakeLock.release();
	}
    
	public void noAdFound() {
		Log.i(TAG,"noAdFound()");
	}

	public void bannerLoadFailed(RequestException e) {
		Log.i(TAG,"bannerLoadFailed()");
	}




	
	
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void onSensorChanged(SensorEvent evt) {
        super.onSensorChanged(evt);

//        if (evt.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
//        {
//            Toast.makeText(QiblacompassActivity.this, "Magnetic Sensor Unreliable, Please Re-Calibrate",Toast.LENGTH_SHORT).show();
//        }

        if (    evt.sensor.getType() == Sensor.TYPE_ACCELEROMETER || 
                evt.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD
        ) {
        	//Tell the compass to update it's graphics
            if (compassView!=null) compassView.postInvalidate();
        }

        //Update the direction text
        //updateText(GlobalData.getBearing());
        //text.setFadingEdgeLength(2);
        //text.setHighlightColor(Color.DKGRAY);
        //text.setTextColor(0xFFD700);
        
        //text.setVerticalFadingEdgeEnabled(true);
        //Typeface face=Typeface.createFromAsset(getAssets(), "fonts/ArabDances.ttf"); 
        //text.setTypeface(face);         
        //text.setText("Qibla Compass");
        
        //System.out.println("locality >>>> "+GlobalData.getLocality());
        if (loc_fetch == false && GlobalData.getKaabadistance() != 0)
        {
        	final Geocoder gcd = new Geocoder(this, Locale.getDefault());
        	  new Thread(new Runnable() {
        		    public void run() {
//                        try {
//                        	Location currentLocation = GlobalData.getCurrentLocation();
//                        	
//        					System.out.println("locality .....");
//        					//Geocoder gcd = new Geocoder(this, Locale.getDefault());
//        					List<Address> addresses = gcd.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
//        					if (addresses.size() > 0) 
//        					{
//        					    System.out.println(addresses.get(0).getLocality());
//        					    GlobalData.setLocality(addresses.get(0).getLocality());
//        					}
//        				} catch (Exception e) {
//        					// TODO Auto-generated catch block
//        					e.printStackTrace();
//        				}        	
//                        if (GlobalData.getLocality() != "")
//                        {
//                        	//updateHeaderText();
//                        	mHandler.post(mUpdateResults);
//                        }
        		    	mHandler.post(mUpdateResults);
        		    	
        		    }
        		  }).start();
        }
        loc_fetch = true;
    }
    
    
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//		String text = (String)msg.obj;
//		
//		//call setText here
//		}

    final Handler mHandler = new Handler();
    // Create runnable for posting
    final Runnable mUpdateResults = new Runnable() {
        public void run() {

           // this is the function which changes some Control Value so Wrap this function inside //handler
        	//text.setText(GlobalData.getKaabadistance() + "kms from "+GlobalData.getLocality());
            String kaaba = getResources().getString(R.string.kaaba);
            text = (TextView) findViewById(R.id.text);
            Typeface face=Typeface.createFromAsset(getAssets(), "fonts/LateefRegOT.ttf");
            text.setTypeface(face);        
            text.setTextSize(30);
            text.setText(kaaba + GlobalData.getKaabadistance());

        }
    }; 
    

    
    @SuppressWarnings("unused")
	private static void updateText(float bearing) {
        int range = (int) (bearing / (360f / 16f)); 
        String  dirTxt = "";
        if (range == 15 || range == 0) dirTxt = "N"; 
        else if (range == 1 || range == 2) dirTxt = "NE"; 
        else if (range == 3 || range == 4) dirTxt = "E"; 
        else if (range == 5 || range == 6) dirTxt = "SE";
        else if (range == 7 || range == 8) dirTxt= "S"; 
        else if (range == 9 || range == 10) dirTxt = "SW"; 
        else if (range == 11 || range == 12) dirTxt = "W"; 
        else if (range == 13 || range == 14) dirTxt = "NW";
        text.setText(""+((int) bearing)+((char)176)+" "+dirTxt);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
      super.onCreateOptionsMenu(menu);
      
      //MenuItem item = menu.add("Painting");
      //item.setIcon(R.drawable.paint);
      
      //item = menu.add("Photos");
      //item.setIcon(R.drawable.photos);
//      SubMenu subBg = menu.addSubMenu("Change Background");
//      SubMenu subDial = menu.addSubMenu("Change Dial");
//      //subScience.setIcon(R.drawable.science);
//      // Now, inflate our submenu.
//     // MenuInflater inflater = new MenuInflater(this);
//      //inflater.inflate(R.menu.menu, subScience);
//      // Programatically, add one item to the submenu.
//      subBg.add("Arch Gate");
//      subBg.add("PureWhite");
//      subBg.add("Biege");
//      subBg.add("Red");
//      subBg.add("Chrome");
//      subBg.add("Blue Art");
//      subBg.add("Violet");
//      subBg.add("Wood");
//      subBg.add("Glow");
//      subBg.add("Cool Blue");
//      subBg.add("Orange Dots");
//      
//      
//      subDial.add("Persain");
//      subDial.add("Eastern");
//      subDial.add("Golden");
//      subDial.add("Modern");
//      subDial.add("Calligraphy");
//      subDial.add("Calligraphy 2");
//      subDial.add("Simple Blue");
//      subDial.add("Simple Blue 2");
//      subDial.add("Middle East");
      // Return true so that the menu gets displayed.
      return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {

      if (item.hasSubMenu() == false)
      {
        // For this demo, lets just give back what
        // we found.
    	  System.out.println("item.getTitle >"+item.getTitle());
    	  
    	  if(item.getTitle() == "PureWhite")
    	  {
	    	  View mainLayout = findViewById(R.id.Mainlayout); 
	    	  mainLayout.setBackgroundResource(R.drawable.bg);
	    	  BgCode = 2;
    	  }
    	  
   	  
//        AlertDialog.Builder dialogBuilder = new   AlertDialog.Builder(this);
//    
//        dialogBuilder.setMessage(" You selected " + item.getTitle());
//        dialogBuilder.setCancelable(true);
//        dialogBuilder.create().show();
      }
      
      // Consume the selection event.
      return true;
    }
      
    public void changeBg(int bgCode)
    {
  	  System.out.println("bgCode >"+bgCode);
	  
  	  if(bgCode == 1)
  	  {
	    	  View mainLayout = findViewById(R.id.Mainlayout); 
	    	  mainLayout.setBackgroundResource(R.drawable.bg);
	    	  BgCode = 1;
  	  }
    	
    }
    
    
    //public class GeoUtils {
    	 
    	   
    	 
        public static double MILLION = 1000000;    
     
     
     
        /**
     
         * Computes the bearing in degrees between two points on Earth.
     
         *
     
         * @param lat1 Latitude of the first point
     
         * @param lon1 Longitude of the first point
     
         * @param lat2 Latitude of the second point
     
         * @param lon2 Longitude of the second point
     
         * @return Bearing between the two points in degrees. A value of 0 means due
     
         *         north.
     
         */
     
        public static double bearing(double lat1, double lon1, double lat2, double lon2) {
     
            double lat1Rad = Math.toRadians(lat1);
     
            double lat2Rad = Math.toRadians(lat2);
     
            double deltaLonRad = Math.toRadians(lon2 - lon1);
     
     
     
            double y = Math.sin(deltaLonRad) * Math.cos(lat2Rad);
     
            double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad)
     
                    * Math.cos(deltaLonRad);
     
            return radToBearing(Math.atan2(y, x));
     
        }
     
       
     
        /**
     
         * Computes the bearing in degrees between two points on Earth.
     
         *
     
         * @param p1 First point
     
         * @param p2 Second point
     
         * @return Bearing between the two points in degrees. A value of 0 means due
     
         *         north.
     
         */
     
//        public static double bearing(GeoPoint p1, GeoPoint p2) {
//     
//            double lat1 = p1.getLatitudeE6() / MILLION;
//     
//            double lon1 = p1.getLongitudeE6() / MILLION;
//     
//            double lat2 = p2.getLatitudeE6() / MILLION;
//     
//            double lon2 = p2.getLongitudeE6() / MILLION;
//     
//     
//     
//            return bearing(lat1, lon1, lat2, lon2);
//     
//        }
     
       
     
       
     
        /**
     
         * Converts an angle in radians to degrees
     
         */
     
        public static double radToBearing(double rad) {
     
            return (Math.toDegrees(rad) + 360) % 360;
     
        }





		public void adClicked() {
			// TODO Auto-generated method stub
			
		}





		public void onDismissScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}





		public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
			// TODO Auto-generated method stub
			
		}





		public void onLeaveApplication(Ad arg0) {
			// TODO Auto-generated method stub
			
		}





		public void onPresentScreen(Ad arg0) {
			// TODO Auto-generated method stub
			
		}





		public void onReceiveAd(Ad arg0) {
			// TODO Auto-generated method stub
			
		}





		public void bannerLoadSucceeded() {
			// TODO Auto-generated method stub
			
		}
     
     
    
    
}