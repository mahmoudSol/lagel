package com.lagel.com.main.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.lagel.com.BuildConfig;
import com.lagel.com.database.CategoryData;
import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas;
import com.squareup.otto.Subscribe;
import com.lagel.com.R;
import com.lagel.com.badgeView.Badge;
import com.lagel.com.badgeView.BadgeView;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;
import com.lagel.com.main.tab_fragments.ChatFrag;
import com.lagel.com.main.tab_fragments.HomeFrag;
import com.lagel.com.main.tab_fragments.ProfileFrag;
import com.lagel.com.main.tab_fragments.SocialFrag;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.mqttchat.Database.CouchDbController;
import com.lagel.com.mqttchat.Utilities.MqttEvents;
import com.lagel.com.mqttchat.Utilities.TimestampSorter;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.CustomBottomNavigationView;
import com.lagel.com.utility.DialogBox;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * <h>HomePageActivity</h>
 * <p>
 * In this class we have the bottom Navigation layout for various fragment transition.
 * </p>
 *
 * @since 3/31/2017
 */
public class HomePageActivity extends AppCompatActivity {
    private static final String TAG = HomePageActivity.class.getSimpleName();
    public VerifyLoginWithFacebook verifyLoginWithFacebook;
    private Activity mActivity;
    private SessionManager mSessionManager;
    public Fragment homeFrag, socialFarg, chatFarg, myProfileFrag;
    private boolean isToHighLightTab;
    private NotificationMessageDialog mNotificationMessageDialog;
    public CustomBottomNavigationView bottomNavigationView;
    public RelativeLayout rL_rootElement;
    private boolean isToStartActivity;


    private static final String PHRASE_CONFIG_KEY = "android_version";
 //    private static final String PHRASE_CONFIG_KEY = "update_categ";


        //put 33161 as zip code
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);



        mActivity = HomePageActivity.this;
        isToStartActivity = true;
        getConfig();
        //FacebookSdk.sdkInitialize(mActivity);
        //share();
        //new RequestTask().execute("https://play.google.com/store/apps/details?id=com.yelo.com&hl=en");
        //ArrayList<ProductCategoryResDatas> lstData= CategoryData.instance(this).getAllCategory();
        // displayWelcomeMessage();
        settings();

    }

    private void settings()
    {
        isToHighLightTab = false;
        mSessionManager = new SessionManager(mActivity);
        mNotificationMessageDialog = new NotificationMessageDialog(mActivity);
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);

        // receive datas
        Intent intent = getIntent();
        if (intent != null) {
            boolean isFromSignup = intent.getBooleanExtra("isFromSignup", false);
            System.out.println(TAG + " " + "isFromSignup first=" + isFromSignup);

            if (isFromSignup)
                new DialogBox(mActivity).startBrowsingDialog();
        }
        initBottomNavView();
        /*
        * Updating the bagged count.*/
        // setChatCount(countUnreadChat());
    }

    @Override
    protected void onResume() {
        super.onResume();

        getConfig();

        isToStartActivity = true;
        AppController.getBus().register(this);
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey("openChat") && bundle.getBoolean("openChat")) {
                if (CommonClass.isBackFromChat) {
                    CommonClass.isBackFromChat = false;
                    View view = bottomNavigationView.findViewById(R.id.nav_chat);
                    view.performClick();
                }
            }
        }

        /*
        * Updating the bagged count.*/
      //  setChatCount(countUnreadChat());
    }

    @Override
    protected void onPause() {
        AppController.getBus().unregister(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    /**
     * <h>InitBottomNavView</h>
     * <p>
     * In this method we used to initialize BottomNavigationView variables.
     * </p>
     */
    private void initBottomNavView() {
        bottomNavigationView = (CustomBottomNavigationView) findViewById(R.id.navigation);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/roboto-regular.ttf");
        disableAllAnimation();
        bottomNavigationView.setTypeface(font);
        bottomNavigationView.setIconSize(26, 26);
        bottomNavigationView.setTextSize(12);

        homeFrag = new HomeFrag();
        socialFarg = new SocialFrag();
        chatFarg = new ChatFrag();
        myProfileFrag = new ProfileFrag();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        final Menu nav_menu = bottomNavigationView.getMenu();
        nav_menu.getItem(0).setIcon(R.drawable.tabbar_home_on);
        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        switch (item.getItemId()) {
                            // Home frag
                            case R.id.nav_home:
                                setBottomTabIcon(nav_menu, R.drawable.tabbar_home_on, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_off, R.drawable.tabbar_profile_off);
                                System.out.println(TAG + " " + "home frag=" + homeFrag.isAdded());
                                isToHighLightTab = true;

                                if (homeFrag.isAdded())
                                    transaction.show(homeFrag);
                                else
                                    transaction.add(R.id.frame_layout, homeFrag, "HomeFragment");
                                if (socialFarg.isAdded())
                                    transaction.hide(socialFarg);
                                if (chatFarg.isAdded())
                                    transaction.hide(chatFarg);
                                if (myProfileFrag.isAdded())
                                    transaction.hide(myProfileFrag);
                                break;

                            // Social
                            case R.id.nav_social:
                                if (mSessionManager.getIsUserLoggedIn()) {
                                    setBottomTabIcon(nav_menu, R.drawable.tabbar_home_off, R.drawable.tabbar_social_on, R.drawable.tab_bar_chat_off, R.drawable.tabbar_profile_off);
                                    isToHighLightTab = true;
                                    if (socialFarg.isAdded())
                                    {
                                        transaction.show(socialFarg);
                                        //Intent intent = new Intent(mActivity, VerifyPhoneNoActivity.class);
                                        //Intent intent = new Intent(mActivity, VerifyEmailIdActivity.class);
                                        //Intent intent = new Intent(mActivity, PhoneAuthActivity.class);
                                        //Intent intent = new Intent(mActivity, NumberVerificationActivity.class);
                                        //startActivity(intent);
                                        //isToStartActivity = false;
                                    }

                                    else
                                        transaction.add(R.id.frame_layout, socialFarg, "SocialFrag");
                                    if (chatFarg.isAdded())
                                        transaction.hide(chatFarg);
                                    if (myProfileFrag.isAdded())
                                        transaction.hide(myProfileFrag);
                                    if (homeFrag.isAdded())
                                    {
                                       // Intent intent = new Intent(mActivity, PhoneAuthActivity.class);
                                      //  startActivity(intent);
                                        transaction.hide(homeFrag);
                                    }

                                } else {

                                    if (isToStartActivity) {
                                        startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                        isToStartActivity = false;
                                        //Intent intent = new Intent(mActivity, PhoneAuthActivity.class);
                                        //startActivity(intent);
                                    }
                                }
                                break;

                            // Camera
                            case R.id.nav_camera:
                                //item.setIcon(R.drawable.tab_bar_camera_on);
                                isToHighLightTab = false;
                                if (mSessionManager.getIsUserLoggedIn()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        if (isToStartActivity) {
                                            startActivity(new Intent(mActivity, Camera2Activity.class));
                                            isToStartActivity = false;
                                        }
                                    } else {
                                        if (isToStartActivity) {
                                            isToStartActivity = false;
                                            startActivity(new Intent(mActivity, CameraActivity.class));
                                        }
                                    }
                                } else {
                                    if (isToStartActivity) {
                                        isToStartActivity = false;
                                        startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                    }
                                }
                                break;

                            // chat
                            case R.id.nav_chat:
                                if (mSessionManager.getIsUserLoggedIn()) {
                                    setBottomTabIcon(nav_menu, R.drawable.tabbar_home_off, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_on, R.drawable.tabbar_profile_off);
                                    isToHighLightTab = true;

                                    if (chatFarg.isAdded())
                                        transaction.show(chatFarg);
                                    else transaction.add(R.id.frame_layout, chatFarg);
                                    if (socialFarg.isAdded())
                                        transaction.hide(socialFarg);
                                    if (myProfileFrag.isAdded())
                                        transaction.hide(myProfileFrag);
                                    if (homeFrag.isAdded())
                                        transaction.hide(homeFrag);
                                } else {
                                    if (isToStartActivity) {
                                        startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                        isToStartActivity = false;
                                    }
                                }
                                break;

                            // My profile
                            case R.id.nav_profile:
                                if (mSessionManager.getIsUserLoggedIn()) {
                                    setBottomTabIcon(nav_menu, R.drawable.tabbar_home_off, R.drawable.tabbar_social_off, R.drawable.tab_bar_chat_off, R.drawable.tabbar_profile_on);
                                    isToHighLightTab = true;

                                    if (myProfileFrag.isAdded())
                                        transaction.show(myProfileFrag);
                                    else transaction.add(R.id.frame_layout, myProfileFrag);

                                    if (socialFarg.isAdded())
                                        transaction.hide(socialFarg);
                                    if (chatFarg.isAdded())
                                        transaction.hide(chatFarg);
                                    if (homeFrag.isAdded())
                                        transaction.hide(homeFrag);
                                } else {
                                    if (isToStartActivity) {
                                        isToStartActivity = false;
                                        startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                                    }
                                }
                                break;
                        }
                        transaction.commit();
                        return isToHighLightTab;
                    }
                });
        transaction.add(R.id.frame_layout, homeFrag);
        transaction.commit();
    }

    /**
     * <h>DisableAllAnimation</h>
     * <p>
     * In this method we used to disable auto-animation from bottom navigation view like
     * shifting of icons and text.
     * </p>
     */
    private void disableAllAnimation() {
        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);
    }

    /**
     * <h>SetChatCount</h>
     * <p>
     * In this method we used to set the badge count(unread chat count) on the
     * chat icon.
     * </p>
     */
    private Badge baage;

    public void setChatCount(int count) {
        if (mSessionManager.getIsUserLoggedIn()) {
            Log.d("dfgt", "" + count);
            if (baage != null) {
                baage.setBadgeNumber(count);
            } else {
                baage = new BadgeView(this)
                        .setBadgeNumber(count)
                        .setGravityOffset(14, 4, true)
                        .setBadgeTextSize(8, true)
                        .setBadgeTextColor(ContextCompat.getColor(this, R.color.white))
                        .setBadgeBackgroundColor(ContextCompat.getColor(this, R.color.pink_color))
                        .bindTarget(bottomNavigationView.getBottomNavigationItemView(3));
            }
        }
    }

    public Badge getBaage() {
        return baage;
    }

    /**
     * <h>SetBottomTabIcon</h>
     * <p>
     * In this method we used to set the selected and unselected icon of bottom navigation view.
     * </p>
     *
     * @param nav_menu    The Navigation Menu
     * @param homeIcon    First Tab i.e HomePage icon
     * @param socialIcon  The Second Tab i.e SocialPage icon
     * @param chatIcon    The Fourth Tab i.e Camera Page icon
     * @param profileIcon The Fifth Tab i.e Profile page icon
     */
    private void setBottomTabIcon(Menu nav_menu, int homeIcon, int socialIcon, int chatIcon, int profileIcon) {
        nav_menu.getItem(0).setIcon(homeIcon);
        nav_menu.getItem(1).setIcon(socialIcon);
        nav_menu.getItem(3).setIcon(chatIcon);
        nav_menu.getItem(4).setIcon(profileIcon);
    }


    @SuppressLint("RestrictedApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG + " " + "onactivity result " + "res code=" + resultCode + " " + "req code=" + requestCode + " " + "data=" + data);
        if (data != null) {
            switch (requestCode) {
                case VariableConstants.LANDING_REQ_CODE:
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage", false);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup", false);
                    System.out.println(TAG + " " + "isToRefreshHomePage=" + isToRefreshHomePage + " " + "isFromSignup=" + isFromSignup);
                    if (isToRefreshHomePage) {
                        isToStartActivity = false;
                        Intent intent = getIntent();
                        intent.putExtra("isFromSignup", isFromSignup);
                        startActivity(intent);
                        finish();
                    }
                    break;
            }

            if (verifyLoginWithFacebook != null)
                verifyLoginWithFacebook.fbOnActivityResult(requestCode, resultCode, data);

            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    /**
     * To fetch the chats from the couchdb, stored locally
     */
    @SuppressWarnings("unchecked")
    public int countUnreadChat() {
        int total_count = 0;
        if (mSessionManager.getIsUserLoggedIn()) {
            return total_count;
        }
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> map = db.getAllChatDetails(AppController.getInstance().getChatDocId());
        if (map != null) {
            ArrayList<String> receiverUidArray = (ArrayList<String>) map.get("receiverUidArray");
            ArrayList<String> receiverDocIdArray = (ArrayList<String>) map.get("receiverDocIdArray");
            ArrayList<Map<String, Object>> chats = new ArrayList<>();
            for (int i = 0; i < receiverUidArray.size(); i++) {
                chats.add(db.getParticularChatInfo(receiverDocIdArray.get(i)));
                Collections.sort(chats, new TimestampSorter());
            }
            for (int i = 0; i < chats.size(); i++) {
                int temp_count = parseData(chats.get(i));
                total_count = total_count + temp_count;
            }
        }
        return total_count;
    }

    /*
    *Collect unread count*/
    private int parseData(@NonNull Map<String, Object> chat_item) {
        int count = 0;
        boolean hasNewMessage = (Boolean) chat_item.get("hasNewMessage");
        if (hasNewMessage) {
            try {
                count = Integer.parseInt((String) chat_item.get("newMessageCount"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }

    @Subscribe
    public void getMessage(JSONObject object) {
        try {
            if (object.getString("eventName").equals(MqttEvents.Message.value + "/" + AppController.getInstance().getUserId()) || object.getString("eventName").equals(MqttEvents.OfferMessage.value + "/" + AppController.getInstance().getUserId())) {
                int count = getBaage().getBadgeNumber();
                count = count + 1;
                setChatCount(count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void getConfig()
    {
        // Get Remote Config instance.
        // [START get_remote_config_instance]
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        //String isActivo=mFirebaseRemoteConfig.getString(PHRASE_CONFIG_KEY);
        // Create a Remote Config Setting to enable developer mode, which you can use to increase
        // the number of fetches available per hour during development. See Best Practices in the
        // README for more information.
        // [START enable_dev_mode]
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchWelcome();
    }

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private void displayWelcomeMessage() {
        // [START get_config_values]

        int versionCode = BuildConfig.VERSION_CODE;

        String welcomeMessage = mFirebaseRemoteConfig.getString(PHRASE_CONFIG_KEY);
        if (welcomeMessage !=null)
        {
            int sVersion=Integer.parseInt(welcomeMessage);

            //  Toast.makeText(getApplicationContext(), ""+Integer.parseInt(welcomeMessage), Toast.LENGTH_LONG).show();

            if (versionCode<sVersion)
            {
                getMessage();
            }
        }
    }

    private void getMessage()
    {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.update_force_title)
                .setMessage(getString(R.string.update_foce))
                .setPositiveButton("Update",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                redirectStore("https://play.google.com/store/apps/details?id=com.lagel.com");
                            }
                        }).create();
        dialog.show();
    }

    private void redirectStore(String updateUrl) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void fetchWelcome() {
        //mWelcomeTextView.setText(mFirebaseRemoteConfig.getString(LOADING_PHRASE_CONFIG_KEY));

        long cacheExpiration = 0; // 1 hour in seconds.
        // If your app is using developer mode, cacheExpiration is set to 0, so each fetch will
        // retrieve values from the service.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        // [START fetch_config_with_callback]
        // cacheExpirationSeconds is set to cacheExpiration here, indicating the next fetch request
        // will use fetch data from the Remote Config service, rather than cached parameter values,
        // if cached parameter values are more than cacheExpiration seconds old.
        // See Best Practices in the README for more information.
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //Toast.makeText(MainActivity.this, "Fetch Succeeded",Toast.LENGTH_SHORT).show();

                            // After config data is successfully fetched, it must be activated before newly fetched
                            // values are returned.
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            //Toast.makeText(MainActivity.this, "Fetch Failed",Toast.LENGTH_SHORT).show();
                        }
                        displayWelcomeMessage();
                    }
                });
        // [END fetch_config_with_callback]
    }
}