package com.lagel.com.main.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.database.DataBaseHelper;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.pojo_class.home_explore_pojo.ExplorePojoMain;
import com.lagel.com.service.CategoryReceiver;
import com.lagel.com.service.CategoryService;
import com.lagel.com.service.GPSService;
import com.lagel.com.service.ProductReceiver;
import com.lagel.com.service.ProductService;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Currency;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

/**
 * <h>SplashActivity</h>
 * <p>
 * This is launch screen i.e open first when user launch the app. It stays for
 * 3second then check if user is logged-in then go to HomeActivity or else go
 * to Landing Screen where we have option for login or signup.
 * </p>
 *
 * @author 3Embed
 * @version 1.0
 * @since 3/29/2017.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String TAG = SplashActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private static final String PREFERENCES_INITIALDATADB2 = "initialdata_01";
    private static final String PREFERENCE_PROPERTIESDB2 = "saveDBs_01";
    protected static SharedPreferences preferences2;
    DataBaseHelper helper;
    boolean dbLoaded = false;
    public static SplashActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);


        Log.e("Khaled", "TEST TEST TEST ****");


        mActivity = SplashActivity.this;
        mSessionManager = new SessionManager(mActivity);
        instance = this;

        if (!isGPSServiceRunning())
        {
            Intent i = new Intent(getBaseContext(), GPSService.class);
            startService(i);
        }

        if (!isCategoryServiceRunning())
        {
            Intent i = new Intent(getBaseContext(), CategoryService.class);
            startService(i);
        }


        if (!isServiceRunning())
        {
            Intent i = new Intent(getBaseContext(), ProductService.class);
            startService(i);
        }





       /* long INTERVAL= 1000*60*1;
        Intent intent = new Intent(this, ProductReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 234324243, intent, 0);
        //intent.putExtra(GPSReceiver.REPEAT_INTERVAL_KEY, 30 * 1000L);
        intent.putExtra(ProductReceiver.REPEAT_INTERVAL_KEY,INTERVAL);
        //intent.putExtra(BkgLocationServiceScheduleReceiver.TRIGGER_TIME_KEY, 5 * 1000L);
        sendBroadcast(intent);*/

        // Log.e("SPLASH", "" + android.util.Patterns.PHONE.matcher("90889898098098").matches());

        // change status bar color
        CommonClass.statusBarColor(mActivity);


        // get default currency
        getDefaultCurrency();
        // set language from sharedpref.
        //selectLanguage(mSessionManager.getLanguageCode());
        setLanguageAsPerCurrentLocal();

        // generating unique id from FCM
        String serialNumber = FirebaseInstanceId.getInstance().getId();
        //System.out.println(TAG + " " + "serial number=" + serialNumber);
        if (serialNumber != null && !serialNumber.isEmpty()) {
            mSessionManager.setDeviceId(serialNumber);
        }

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        //System.out.println(TAG + " " + "My Refreshed token: " + refreshedToken);
        if (refreshedToken != null && !refreshedToken.isEmpty())
            mSessionManager.setPushToken(refreshedToken);

        //System.out.println(TAG + "get push token=" + mSessionManager.getPushToken());

        // get bundle datas if notification comes in background
        Bundle bundle = getIntent().getExtras();
        //System.out.println(TAG + " " + "bundle=" + bundle);

        String notificationDatas = "";
        if (bundle != null) {
            notificationDatas = bundle.getString("body");
           // System.out.println(TAG + " " + "bundle notification datas=" + notificationDatas);
        }


        // Go to notification screen if any notification msg is there else Home Page
        if (notificationDatas != null && !notificationDatas.isEmpty())
            callNotificationClass(notificationDatas);
        else
            setTimerForScreen();
    }

    private void loadDB()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                super.run();
                try
                {
                    sleep(3000);
                    helper = new DataBaseHelper(SplashActivity.this);
                    helper.createDataBase();
                    helper.openDataBase();
                    // helper.close();
                    dbLoaded = true;


                    // insertar los datos
                    SplashActivity.this.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            handlerProgram.sendEmptyMessage(0);
                        }
                    });
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    protected Handler handlerProgram = new Handler() {
        //@Override
        public void handleMessage(Message msg) {

            try {
                startApp();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    protected void startApp() {

//        /finish();
        //Intent first = new Intent(this, MainActivity.class);
        //startActivity(first);
        //finish();
        //startActivity(new Intent(mActivity, HomePageActivity.class));
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");
    }

    /**
     * <h>CallNotificationClass</h>
     * <p>
     * In this method we used to receive the bundle datas when notification comes
     * from background. After that we used to send datas to Notification activity
     * class.
     * </p>
     *
     * @param notificationDatas The notification datas.
     */
    private void callNotificationClass(String notificationDatas) {
        if (notificationDatas != null && !notificationDatas.isEmpty()) {
            System.out.println(TAG + " " + "bundle=" + notificationDatas);
            Intent intent = new Intent(mActivity, NotificationActivity.class);
            intent.putExtra("notificationDatas", notificationDatas);
            intent.putExtra("isFromNotification", true);
            startActivity(intent);
            finish();
        }
    }

    /**
     * <h>SetTimerForScreen</h>
     * <p>
     * In this method we used to sleep screen for three second.
     * </p>
     */
    private void setTimerForScreen() {

        preferences2 = getSharedPreferences(PREFERENCES_INITIALDATADB2,Activity.MODE_PRIVATE);

        if (!preferences2.getBoolean(PREFERENCE_PROPERTIESDB2, false))
        {
                loadDB();
                accept(preferences2);
                getGuestPosts(1);
        }
        else
        {
            if (!mSessionManager.getIsUserLoggedIn()) {
                //Add to Background Service
                //logGuestInfo();
                getGuestPosts(1);

            } else {
                getUserPosts(0);
                //getGuestPosts(1);
            }



            /*int TIME_OUT = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mActivity, HomePageActivity.class));
                    finish();
                }
            }, TIME_OUT);*/

        }

    }

    private static void accept(SharedPreferences preferences) {
        preferences2.edit().putBoolean(PREFERENCE_PROPERTIESDB2, true).commit();
    }

    private static void deny(SharedPreferences preferences) {
        preferences2.edit().putBoolean(PREFERENCE_PROPERTIESDB2, false).commit();
    }

    private void getDefaultCurrency() {
        try {
            String countryIsoCode = Locale.getDefault().getCountry();
            mSessionManager.setCountryIso(countryIsoCode);
            System.out.println(TAG + " " + "locale iso cod=" + countryIsoCode);
            Locale locale = new Locale("EN", countryIsoCode);
            Currency currency = Currency.getInstance(locale);
            mSessionManager.setCurrency(String.valueOf(currency));
            System.out.println(TAG + " " + "currency=" + String.valueOf(currency));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectLanguage(String code) {
        Locale myLocale = new Locale(code);
        Locale.setDefault(myLocale);
        android.content.res.Configuration config = new android.content.res.Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void setLanguageAsPerCurrentLocal() {
        Locale current = getResources().getConfiguration().locale;
        Log.d("current locale language", current.getDisplayLanguage());
        if (current.getDisplayLanguage().equalsIgnoreCase("français") && mSessionManager.isFirstRun()) {
            selectLanguage(getString(R.string.french_language_code));
            mSessionManager.setLanguageCode(getString(R.string.french_language_code));
            mSessionManager.setFirstRunDone();
        } else if (current.getDisplayLanguage().equalsIgnoreCase("español") && mSessionManager.isFirstRun()) {
            selectLanguage(getString(R.string.spanish_language_code));
            mSessionManager.setLanguageCode(getString(R.string.spanish_language_code));
            mSessionManager.setFirstRunDone();
        } else {
            selectLanguage(mSessionManager.getLanguageCode());
        }
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ProductService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isGPSServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (GPSService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isCategoryServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CategoryService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * <h>GetGuestPosts</h>
     * <p>
     * In this method we used to call guest user api to get all posts.
     * </p>
     *
     * @param offset The page index
     */
    private void getGuestPosts(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            //  showProgressDialog("Loading...");
            JSONObject requestDatas = new JSONObject();
           // int limit = 40;
            int limit = 50;
            offset = limit * offset;

            try {
                String token=mSessionManager.getAuthToken();

                requestDatas.put("offset", offset);
                requestDatas.put("limit", limit);
                requestDatas.put("pushToken", mSessionManager.getPushToken());



            } catch (JSONException e) {
                e.printStackTrace();
            }

           // mProgressBar.setVisibility(View.GONE);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_GUEST_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                        ExplorePojoMain explorePojoMain;
                        Gson gson = new Gson();
                        explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);
                        AppController._dataLagel.addAll(explorePojoMain.getData());

                        startActivity(new Intent(mActivity, HomePageActivity.class));
                        finish();

                }

                @Override
                public void onError(String error, String user_tag) {
                 //   mProgressBar.setVisibility(View.GONE);
                    Log.e("TAG",error);
                }
            });
        }
        else
        {
            int TIME_OUT = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mActivity, HomePageActivity.class));
                    finish();
                }
            }, TIME_OUT);
        }
    }



    private void getUserPosts(int offset) {
        // lat = "18.575394";
        //lng = "-72.294708";

        if (CommonClass.isNetworkAvailable(mActivity)) {
            //   showProgressDialog("Loading...");
            JSONObject requestDatas = new JSONObject();
            int limit = 40;
            //   offset = limit * offset;
            offset = limit * offset;

            try {
                requestDatas.put("offset", offset);
                requestDatas.put("limit", limit);
                requestDatas.put("token", mSessionManager.getAuthToken());
                if (mSessionManager.getCurrentLat()!=null)
                {
                    String lat=mSessionManager.getCurrentLat();
                    requestDatas.put("latitude", lat);
                }


                if (mSessionManager.getCurrentLng()!=null)
                {
                    String lng=mSessionManager.getCurrentLng();
                    requestDatas.put("longitude", lng);
                }


                requestDatas.put("pushToken", mSessionManager.getPushToken());
                Log.i("TAG",""+requestDatas.toString());


            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_USER_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    if (result != null && !result.isEmpty())
                    {
                        ExplorePojoMain explorePojoMain;
                        Gson gson = new Gson();
                        explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);
                        if (explorePojoMain.getData()!=null)
                        {
                            AppController._dataLagel.addAll(explorePojoMain.getData());
                        }

                        startActivity(new Intent(mActivity, HomePageActivity.class));
                        finish();

                    }

                }

                @Override
                public void onError(String error, String user_tag) {
                    Log.i("TAG",error);

                    startActivity(new Intent(mActivity, HomePageActivity.class));
                    finish();
                }
            });
        } else {
            int TIME_OUT = 3000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(mActivity, HomePageActivity.class));
                    finish();
                }
            }, TIME_OUT);
        }
    }

}
