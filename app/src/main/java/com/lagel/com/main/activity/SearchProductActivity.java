package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.adapter.ExploreRvAdapter;
import com.lagel.com.adapter.SearchPostsActAdap;
import com.lagel.com.adapter.ViewPagerAdapter;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.main.view_pager.search_product.PeoplesFrag;
import com.lagel.com.main.view_pager.search_product.PostsFrag;
import com.lagel.com.pojo_class.profile_pojo.ProfilePojoMain;
import com.lagel.com.pojo_class.search_post_pojo.SuggestedPostPojoMain;
import com.lagel.com.pojo_class.search_post_pojo.SuggestedResponse;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <h>SearchProductActivity</h>
 * <p>
 *     This class is called from HomePage Frag class. In this class we used to set two
 *     tab first one is Posts Tab to search a product from the given post from data base
 *     and second tab is to search the registered user.
 * </p>
 * @since 18-May-17
 */
public class SearchProductActivity extends AppCompatActivity implements View.OnClickListener
{
    public EditText eT_search_users;
    private static final String TAG=SearchProductActivity.class.getSimpleName();
    public String postText="",peopleText="",latitude="",longitude="";
    private NotificationMessageDialog mNotificationMessageDialog;
    public AutoCompleteTextView act_search_posts;
    private SessionManager mSessionManager;
    public SearchPostsActAdap searchPostsActAdap;
    public ArrayList<String> searchList;
    private FusedLocationService locationService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );
        mSessionManager=new SessionManager(this);
        getCurrentLocation();
        initVariables();
    }



    /**
     * <h>initVariables</h>
     * <p>
     *     This method is being called from onCreate() of the same class. In this
     *     method we used to initliaze all variables.
     * </p>
     */
    private void initVariables()
    {
        Activity mActivity = SearchProductActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);

        RelativeLayout rL_close= (RelativeLayout) findViewById(R.id.rL_close);
        rL_close.setOnClickListener(this);
        eT_search_users= (EditText) findViewById(R.id.eT_search_users);
        eT_search_users.setHint(getResources().getString(R.string.search_post));

        act_search_posts=(AutoCompleteTextView) findViewById(R.id.act_search_posts);

        searchList=new ArrayList<>();

        searchPostsActAdap =new SearchPostsActAdap(searchList,mActivity);
        act_search_posts.setAdapter(searchPostsActAdap);
        act_search_posts.setThreshold(1);

        act_search_posts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("Suggested",charSequence.toString());
                if(isLocationFound(latitude,longitude)){
                    getSuggestionPosts(ApiUrl.SUGGESTED_POSTS,charSequence.toString());
                }else {
                    getCurrentLocation();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ViewPager viewpager= (ViewPager)findViewById(R.id.viewpager);
        TabLayout tabs= (TabLayout)findViewById(R.id.tabs);
        setupViewPager(viewpager);
        tabs.setupWithViewPager(viewpager);

        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position)
            {
                System.out.println(TAG+" "+"position="+position);
                if (position==0) {
                    eT_search_users.setVisibility(View.GONE);
                    act_search_posts.setVisibility(View.VISIBLE);
                    act_search_posts.setHint(getResources().getString(R.string.search_post));
                    act_search_posts.setText(postText);
                   /* eT_search_users.setHint(getResources().getString(R.string.search_post));
                    eT_search_users.setText(postText);*/
                }
                else {
                    act_search_posts.setVisibility(View.GONE);
                    eT_search_users.setVisibility(View.VISIBLE);
                    eT_search_users.setHint(getResources().getString(R.string.search_people));
                    eT_search_users.setText(peopleText);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mNotificationMessageDialog.mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PostsFrag(),getResources().getString(R.string.posts));
        adapter.addFragment(new PeoplesFrag(),getResources().getString(R.string.people));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_close :
                onBackPressed();
                break;
        }
    }

    public void getSuggestionPosts(String apiUrl,String productName){
        if (CommonClass.isNetworkAvailable(this)) {
            /*JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("productName", productName);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            apiUrl=apiUrl+"?productName="+productName+"&latitude="+latitude+"&longitude="+longitude;



            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    SuggestedPostPojoMain suggestedPostPojoMain;
                    Gson gson=new Gson();
                    suggestedPostPojoMain=gson.fromJson(result,SuggestedPostPojoMain.class);

                    switch (suggestedPostPojoMain.getCode()){
                        case  "200" :
                            searchList.clear();
                            ArrayList<SuggestedResponse> responses=suggestedPostPojoMain.getData();
                            Log.d("Suggested",responses.size()+"");
                            for(int i=0;i<responses.size();i++){
                                searchList.add(responses.get(i).getProductName());
                            }
                            //searchPostsActAdap.notifyDataSetChanged();
                            searchPostsActAdap.getFilter().filter(act_search_posts.getText().toString());
                    }

                }

                @Override
                public void onError(String error, String user_tag) {
                    Log.d("SuggestedPosts",error);
                }
            });

        }
    }

    private void getCurrentLocation()
    {
        locationService=new FusedLocationService(this, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation=locationService.receiveLocation();
                if (currentLocation!=null)
                {
                    latitude=String.valueOf(currentLocation.getLatitude());
                    longitude=String.valueOf(currentLocation.getLongitude());

                    System.out.println(TAG+" "+"lat="+latitude+" "+"lng="+longitude);

                    if (isLocationFound(latitude,longitude))
                    {
                        mSessionManager.setCurrentLat(latitude);
                        mSessionManager.setCurrentLng(longitude);
                    }
                }
            }
        }
        );
    }

    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }
}
