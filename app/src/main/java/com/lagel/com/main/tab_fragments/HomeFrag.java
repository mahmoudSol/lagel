package com.lagel.com.main.tab_fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.lagel.com.BuildConfig;
import com.lagel.com.R;
import com.lagel.com.adapter.ExploreRvAdapter;
import com.lagel.com.adapter.SearchPostsActAdap;
import com.lagel.com.county_code_picker.Country;
import com.lagel.com.database.CategoryData;
import com.lagel.com.database.CountryData;
import com.lagel.com.event_bus.BusProvider;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.lalita.SearchActivity;
import com.lagel.com.lalita.utils.server.interfaces.ApiCallbackListener;
import com.lagel.com.main.activity.Camera2Activity;
import com.lagel.com.main.activity.CameraActivity;
import com.lagel.com.main.activity.FilterActivity;
import com.lagel.com.main.activity.HomePageActivity;
import com.lagel.com.main.activity.LandingActivity;
import com.lagel.com.main.activity.LoginOrSignupActivity;
import com.lagel.com.main.activity.NotificationActivity;
import com.lagel.com.main.activity.SearchProductActivity;
import com.lagel.com.main.activity.products.ProductDetailsActivity;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.pojo_class.LogDevicePojo;
import com.lagel.com.pojo_class.UnseenNotifiactionCountPojo;
import com.lagel.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import com.lagel.com.pojo_class.home_explore_pojo.ExplorePojoMain;
import com.lagel.com.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas;
import com.lagel.com.pojo_class.recent_search_list_pojo.RecentSearchPojoMain;
import com.lagel.com.pojo_class.recent_search_list_pojo.SearchResponse;
import com.lagel.com.pojo_class.search_post_pojo.SuggestedPostPojoMain;
import com.lagel.com.pojo_class.search_post_pojo.SuggestedResponse;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.DialogBox;
import com.lagel.com.utility.HideShowScrollListener;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.ProductItemClickListener;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>HomeFrag</h>
 * <p>
 * This class is called from first tab of MainActivity class.
 * In this class we used to show the users all posts.
 * </p>
 *
 * @author 3Embed
 * @version 1.0
 * @since 3/31/2017
 */
public class HomeFrag extends Fragment implements View.OnClickListener, ProductItemClickListener, ApiCallbackListener {
    private String category = "", categoryValue = "", distance = "", postedWithin = "", minPrice = "", maxPrice = "", currency = "", currency_code = "",
            currentLatitude = "", currentLongitude = "", sortByText = "", sortBy = "", postedWithinText = "", address = "", filterAddress = "";
    private SessionManager mSessionManager;
    private Activity mActivity;
    private static final String TAG = HomeFrag.class.getSimpleName();
    //private ProgressBar mProgressBar;
    private ArrayList<ExploreResponseDatas> arrayListExploreDatas;
    private int index;
    private SwipeRefreshLayout mRefreshLayout;
    private boolean isFromSearch, isHomeFragVisible;
    private LinearLayout linear_filter;
    private ArrayList<String> arrayListFilter;
    private View view_filter_divider;
    private RelativeLayout rL_noProductFound, rL_no_internet, rL_action_bar, rL_sell_stuff,rL_noProductFoundLocation;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager gridLayoutManager;
    private ArrayList<ProductCategoryResDatas> aL_categoryDatas, allCategoryList;
    private String[] postedWithinArr, sortByArr;
    private int clickedItemPosition;
    private ExploreRvAdapter exploreRvAdapter;
    private FusedLocationService locationService;
    private String lat = "", lng = "";
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private TextView tV_notification_count;
    private BroadcastReceiver mBroadcastReceiver;
    private int notificationCount;
    private int fineLocResult, coarseLocResult;
    private boolean isFineLocDenied, isCoarseLocDenied;
    private ArrayList<Country> arrayListCountry;
    // load more variables
    private boolean first = true;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    private HorizontalScrollView scrollViewFilter;
    private int recycleviewPaddingTop;
    private HomePageActivity homePageActivity;

    private String flat = "", flng = "", flocation = "", fcity = "", fcountry = "";

    private AutoCompleteTextView act_search_posts;

    private AppEventsLogger logger;

    public ArrayList<String> searchList;
    public ArrayList<String> recentSearchList;
    public SearchPostsActAdap searchPostsActAdap;

    private TextView tV_Browse;
    private RelativeLayout searchBar;
    private ProgressDialog progressDialog;
    private String typeFilter="";
    private Lagel1Pref lagel1Pref;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dismissDialog();
        arrayListCountry = new ArrayList<>();
        lagel1Pref= new Lagel1Pref(getActivity());
        ArrayList<Country> data=getCountryCodeList();
        CountryData.instance(getActivity()).clearDataCountry();
        CountryData.instance(getActivity()).setInsertCountry(data);

        ArrayList<Country>  country=CountryData.instance(getActivity()).getAllCountry();

        mActivity = getActivity();
        homePageActivity = (HomePageActivity) mActivity;
        setNotificationBroadCast();
        index = 0;
        notificationCount = 0;
        isFromSearch = false;// load more variables
        mSessionManager = new SessionManager(mActivity);
        arrayListExploreDatas = new ArrayList<>();
        aL_categoryDatas = new ArrayList<>();
        allCategoryList = new ArrayList<>();
        postedWithinArr = new String[]{getResources().getString(R.string.the_last_24hr), getResources().getString(R.string.the_last_7day), getResources().getString(R.string.the_last_30day), getResources().getString(R.string.all_producs)};
        sortByArr = new String[]{getResources().getString(R.string.newest_first), getResources().getString(R.string.closest_first), getResources().getString(R.string.price_high_to_low), getResources().getString(R.string.price_low_to_high)};
        recentSearchList = new ArrayList<>();
        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
        fineLocResult = ContextCompat.checkSelfPermission(mActivity, ACCESS_FINE_LOCATION);
        coarseLocResult = ContextCompat.checkSelfPermission(mActivity, ACCESS_COARSE_LOCATION);
        getCategoriesService();

        FacebookSdk.sdkInitialize(mActivity);
        logger = AppEventsLogger.newLogger(mActivity);

        // to see notification count
        unseenNotificationCountApi();
    }

    private void setNotificationBroadCast() {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    String jsonMessageResponse = intent.getStringExtra("jsonMessage");
                    //System.out.println(TAG + " " + "message=" + message + " " + "jsonMessageResponse=" + jsonMessageResponse + " " + "notificationCount=" + notificationCount);
                    unseenNotificationCountApi();
                }
            }
        };
    }

    @Override
    public void onResume() {
        fetchLocation();

        mRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mRefreshLayout.setRefreshing(true);
                if (CommonClass.isNetworkAvailable(mActivity)) {

                    hideKeyboard();
                    if (!act_search_posts.getText().toString().trim().equals(""))
                        act_search_posts.setText("");
                    rL_no_internet.setVisibility(View.GONE);
                    arrayListExploreDatas.clear();
                    exploreRvAdapter.notifyDataSetChanged();
                    index = 0;
                    if (isFromSearch)
                    {
                        String text=android.text.TextUtils.join("|", arrayListFilter);

                        if (!lagel1Pref.getFilter())
                        {
                            searchProductApi(text, index);
                        }
                        else {
                            searchProductsApi1(index);
                        }
                    }
                    else {
                        if (mSessionManager.getIsUserLoggedIn()) {

                            getUserPosts(index);

                        } else
                        {
                            getGuestPosts(index);
                        }
                    }
                } else {
                    rL_no_internet.setVisibility(View.VISIBLE);
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

        super.onResume();
    }

    private void fetchLocation() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {

                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    if (currentLocation != null) {
                        lat = String.valueOf(currentLocation.getLatitude());
                        lng = String.valueOf(currentLocation.getLongitude());

                        if (isLocationFound(lat, lng)) {
                            mSessionManager.setCurrentLat(lat);
                            mSessionManager.setCurrentLng(lng);

                            Log.e("Latitude_GET", "lat003: " + mSessionManager.getCurrentLat());
                            Log.e("Latitude_GET", "lng003: " + mSessionManager.getCurrentLng());
                        }
                    }
                }
            }
            );
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        recycleviewPaddingTop = mRecyclerView.getPaddingTop();
        BusProvider.getInstance().register(this);
        LocalBroadcastManager.getInstance(mActivity).registerReceiver(mBroadcastReceiver, new IntentFilter(Config.PUSH_NOTIFICATION));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        BusProvider.getInstance().unregister(this);
        LocalBroadcastManager.getInstance(mActivity).unregisterReceiver(mBroadcastReceiver);
    }

    /**
     * Called for showing Progress dialog
     */
    public void showProgressDialog(String text) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(text);
        progressDialog.setCancelable(true);
        // progressDialog.show();
    }

    /**
     * Dismiss Progress dialog
     */

    public void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_home, container, false);
        // mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar_home);
        // mProgressBar.setVisibility(View.GONE);

        view_filter_divider = view.findViewById(R.id.view_filter_divider);
        view_filter_divider.setVisibility(View.GONE);
        rL_no_internet = (RelativeLayout) view.findViewById(R.id.rL_no_internet);
        rL_no_internet.setVisibility(View.GONE);

        rL_action_bar = (RelativeLayout) view.findViewById(R.id.rL_action_bar);
        scrollViewFilter = (HorizontalScrollView) view.findViewById(R.id.scrollViewFilter);

        tV_notification_count = (TextView) view.findViewById(R.id.tV_notification_count);
        tV_notification_count.setVisibility(View.GONE);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rV_home);
        gridLayoutManager = new StaggeredGridLayoutManager(3, 1);
        gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setItemAnimator(null);
        exploreRvAdapter = new ExploreRvAdapter(getActivity(), arrayListExploreDatas, this);
        mRecyclerView.setAdapter(exploreRvAdapter);

        linear_filter = (LinearLayout) view.findViewById(R.id.linear_filter);
        rL_noProductFound = (RelativeLayout) view.findViewById(R.id.rL_noProductFound);
        rL_noProductFound.setVisibility(View.GONE);

        rL_noProductFoundLocation= (RelativeLayout) view.findViewById(R.id.rL_noProductFoundLocation);
        rL_noProductFoundLocation.setVisibility(View.GONE);
        searchBar = (RelativeLayout) view.findViewById(R.id.searchBar);

        // Set spacing the recycler view items
        int spanCount = 2; // 2 columns
        int spacing = 10; // 50px
        // mRecyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

        // Pull to refresh
        mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary,R.color.google_bg_color);
        mRefreshLayout.setProgressViewOffset(false, 100, 200);

        if (!mSessionManager.getIsUserLoggedIn()) {
            //Add to Background Service
            //logGuestInfo();

        } else {
            //  getUserPosts(0);
        }

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (CommonClass.isNetworkAvailable(mActivity)) {

                    hideKeyboard();
                    if (!act_search_posts.getText().toString().trim().equals(""))
                        act_search_posts.setText("");
                    rL_no_internet.setVisibility(View.GONE);
                    arrayListExploreDatas.clear();
                    exploreRvAdapter.notifyDataSetChanged();
                    index = 0;
                    if (isFromSearch)
                    {
                        String text=android.text.TextUtils.join("|", arrayListFilter);

                        //for bar
                        //if (!isFromSearch)
                        String sTypeFilter=typeFilter;
                        //if (sTypeFilter!=null && sTypeFilter.length()==0)

                        if (!lagel1Pref.getFilter())
                        {
                            searchProductApi(text, index);
                        }
                        else {
                            //
                            //for Filter
                            searchProductsApi1(index);
                        }

                    }

                    else {
                        // check is user is logged in or not if its login then show according to location if not then show all posts.
                        if (mSessionManager.getIsUserLoggedIn()) {

                            getUserPosts(index);
                            //getGuestPosts(index);

                            /*if (isLocationFound(lat, lng))
                                getUserPosts(index);
                            else getCurrentLocation();*/


                        } else
                        {
                            getGuestPosts(index);
                        }
                    }
                } else {
                    rL_no_internet.setVisibility(View.VISIBLE);
                    mRefreshLayout.setRefreshing(false);
                }
            }
        });

        // Sell your stuff
        RelativeLayout rL_notification, rL_filter, rL_search;
        rL_sell_stuff = (RelativeLayout) view.findViewById(R.id.rL_sell_stuff);
        rL_sell_stuff.setOnClickListener(this);
        rL_notification = (RelativeLayout) view.findViewById(R.id.rL_notification);
        rL_notification.setOnClickListener(this);
        rL_filter = (RelativeLayout) view.findViewById(R.id.rL_filter);
        rL_filter.setOnClickListener(this);
        rL_search = (RelativeLayout) view.findViewById(R.id.rL_search);
        rL_search.setOnClickListener(this);

        //New design work
        act_search_posts = (AutoCompleteTextView) view.findViewById(R.id.act_search_posts);
        tV_Browse = (TextView) view.findViewById(R.id.tV_browse);
        tV_Browse.setOnClickListener(this);
        view.findViewById(R.id.tV_invite).setOnClickListener(this);
        searchList = new ArrayList<>();

        act_search_posts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lagel1Pref.setFilter(false);
                Intent filterIntent = new Intent(getActivity(), SearchActivity.class);
                filterIntent.putParcelableArrayListExtra("LIST", arrayListExploreDatas);

                startActivityForResult(filterIntent, VariableConstants.FILTER_REQUEST_SEARCH);
                getActivity().overridePendingTransition(0, 0);
            }
        });

        act_search_posts.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {

                try {
                    act_search_posts.showDropDown();
                    act_search_posts.requestFocus();

                    if (act_search_posts.getText().toString().isEmpty() && recentSearchList.size() > 0) {

                        searchPostsActAdap = new SearchPostsActAdap(recentSearchList, mActivity);
                        act_search_posts.setAdapter(searchPostsActAdap);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        act_search_posts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("Suggested", charSequence.toString());

                if (isLocationFound(lat, lng)) {
                    if (act_search_posts.getText().toString().trim().length() > 0) {
                        getSuggestionPosts(ApiUrl.SUGGESTED_POSTS, charSequence.toString());
                    }
                } else {
                    Log.i("","");
                }

                Log.i("","");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        act_search_posts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!searchList.isEmpty() && searchList.get(i).length() > 0) {
                    searchProductApi(searchList.get(i), index);
                    addSearchKeyWordHistory(ApiUrl.ADD_KEYWORD_IN_SEARCH_HISTORY, searchList.get(i));
                }
            }
        });

        if (CommonClass.isNetworkAvailable(mActivity)) {
            rL_no_internet.setVisibility(View.GONE);
            index = 0;
            arrayListExploreDatas.clear();
            exploreRvAdapter.notifyDataSetChanged();

            if (mSessionManager.getIsUserLoggedIn()) {
                dismissDialog();
                getUserPosts2(index);

            } else {
                getGuestPosts2(index);
            }
        } else rL_no_internet.setVisibility(View.VISIBLE);

        //..location update on startup if user login
        if (mSessionManager.getIsUserLoggedIn()) {
            Log.e("aassaa","Called");
            getLocationOnLaunch();
            getUserSearchHistory(ApiUrl.GET_USER_SEARCH_HISTORY, mSessionManager.getUserName(), mSessionManager.getAuthToken());
        }

        return view;
    }

    public void getSuggestionPosts(String apiUrl, String productName) {
        if (CommonClass.isNetworkAvailable(getActivity())) {
            /*JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("productName", productName);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/

            apiUrl = apiUrl + "?productName=" + productName + "&latitude=" + lat + "&longitude=" + lng + "&username=" + mSessionManager.getUserName();


            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    Log.e("Result Suggestion", result);
                    SuggestedPostPojoMain suggestedPostPojoMain;
                    try {
                        Gson gson = new Gson();
                        suggestedPostPojoMain = gson.fromJson(result, SuggestedPostPojoMain.class);


                        switch (suggestedPostPojoMain.getCode()) {
                            case "200":
                                ArrayList<SuggestedResponse> responses = null;
                                ArrayList<String> responsesRes = null;
                                recentSearchList = new ArrayList<>();
                                try {
                                    searchList.clear();
                                    responses = suggestedPostPojoMain.getData();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                                Log.e("Suggested", responses.size() + "");
                                for (int i = 0; i < responses.size(); i++) {
                                    searchList.add(responses.get(i).getProductName());
                                }

                                if (responsesRes != null) {
                                    for (int i = 0; i < responsesRes.size(); i++) {
                                        searchList.add(responsesRes.get(i).replace("hashTag : ", ""));
                                    }
                                }
                                if (suggestedPostPojoMain.getSearchHistory() != null && suggestedPostPojoMain.getSearchHistory().size() > 0) {
                                    recentSearchList.add(getString(R.string.recent_search));
                                    for (int i = 0; i < suggestedPostPojoMain.getSearchHistory().size(); i++) {
                                        recentSearchList.add(suggestedPostPojoMain.getSearchHistory().get(i).replace("hashTag : ", ""));
                                    }
                                }
                                if (recentSearchList.size() > 0) {
                                    searchList.addAll(recentSearchList);
                                }


                                if (searchPostsActAdap!=null && searchPostsActAdap.getFilter()!=null
                                        && act_search_posts!=null)
                                {
                                    searchPostsActAdap.getFilter().filter(act_search_posts.getText().toString());
                                }


                                searchPostsActAdap = new SearchPostsActAdap(searchList, mActivity);
                                act_search_posts.setAdapter(searchPostsActAdap);
                                //  act_search_posts.showDropDown();
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    Log.d("SuggestedPosts", error);
                }
            });

        }
    }

    public void getUserSearchHistory(String apiUrl, String name, String token) {
        if (CommonClass.isNetworkAvailable(getActivity())) {

            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("name", name);
                request_datas.put("token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    try {
                        CommonClass.showLog("---- response" + result);
                        RecentSearchPojoMain recentSearchPojoMain;
                        Gson gson = new Gson();
                        recentSearchPojoMain = gson.fromJson(result, RecentSearchPojoMain.class);

                        switch (recentSearchPojoMain.getCode()) {
                            case 200:
                                ArrayList<SearchResponse> responses = recentSearchPojoMain.getData();

                                if (responses.get(0).getSearchHistory() != null) {
                                    ArrayList<String> responsesArr = (ArrayList<String>) responses.get(0).getSearchHistory();
                                    recentSearchList.add(getString(R.string.recent_search));
                                    for (int i = 0; i < responsesArr.size(); i++) {
                                        recentSearchList.add(responsesArr.get(i).replace("hashTag : ", ""));
                                    }
                                }

                                CommonClass.showLog("recent search " + recentSearchList.size());
                        }
                    }
                    catch (Exception e){}
                }

                @Override
                public void onError(String error, String user_tag) {
                    Log.d("SuggestedPosts", error);
                }
            });

        }
    }

    public void addSearchKeyWordHistory(String apiUrl, String name) {
        if (CommonClass.isNetworkAvailable(getActivity())) {

            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("searchKey", name);
                request_datas.put("searchType", "1");
                request_datas.put("name", mSessionManager.getUserName());
                request_datas.put("token", mSessionManager.getAuthToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, apiUrl, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    CommonClass.showLog("---- add key response" + result);


                }

                @Override
                public void onError(String error, String user_tag) {
                    Log.d("add key word", error);
                }
            });

        }
    }


    /**
     * <h>UnseenNotificationCountApi</h>
     * <p>
     * In this method we used to do api call to get total unseen notification count.
     * </p>
     */
    private void unseenNotificationCountApi() {
        if (CommonClass.isNetworkAvailable(mActivity) && mSessionManager.getIsUserLoggedIn()) {
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String unseenNotificationCountUrl = ApiUrl.UNSEEN_NOTIFICATION_COUNT + "?token=" + mSessionManager.getAuthToken();
            OkHttp3Connection.doOkHttp3Connection(TAG, unseenNotificationCountUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    //System.out.println(TAG + " " + "unseen notification count res=" + result);

                    UnseenNotifiactionCountPojo unseenNotifiactionCountPojo;
                    Gson gson = new Gson();
                    unseenNotifiactionCountPojo = gson.fromJson(result, UnseenNotifiactionCountPojo.class);

                    switch (unseenNotifiactionCountPojo.getCode()) {
                        case "200":
                            //System.out.println(TAG + " " + "Notification count=" + unseenNotifiactionCountPojo.getData());
                            notificationCount = unseenNotifiactionCountPojo.getData();
                            if (notificationCount > 0) {
                                tV_notification_count.setVisibility(View.VISIBLE);
                                tV_notification_count.setText(String.valueOf(notificationCount));
                            } else tV_notification_count.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
    }

    /**
     * <h>GetCategoriesService</h>
     * <p>
     * This method is called from onCreate() method of the current class.
     * In this method we used to call the getCategories api using okHttp3.
     * Once we get the data we show that list in recyclerview.
     * </p>
     */
    private void getCategoriesService() {

        ArrayList<ProductCategoryResDatas> lstData= CategoryData.instance(getActivity()).getAllCategory();

        aL_categoryDatas.addAll(lstData);
        allCategoryList.addAll(lstData);


       /* if (CommonClass.isNetworkAvailable(mActivity)) {
            String url = ApiUrl.GET_CATEGORIES + "?lan=" + mSessionManager.getLanguageCode();

            OkHttp3Connection.doOkHttp3Connection(TAG, url, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "get category res=" + result);



                    ProductCategoryMainPojo categoryMainPojo;
                    Gson gson = new Gson();
                    categoryMainPojo = gson.fromJson(result, ProductCategoryMainPojo.class);

                    switch (categoryMainPojo.getCode()) {
                        // success
                        case "200":
                            if (categoryMainPojo.getData() != null && categoryMainPojo.getData().size() > 0) {
                                aL_categoryDatas.addAll(categoryMainPojo.getData());
                                allCategoryList.addAll(categoryMainPojo.getData());
                            }
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                }
            });
        }*/
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation() {
        locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation = locationService.receiveLocation();
                if (currentLocation != null) {
                    lat = String.valueOf(currentLocation.getLatitude());
                    lng = String.valueOf(currentLocation.getLongitude());

                    //System.out.println(TAG + " " + "lat=" + lat + " " + "lng=" + lng);

                    if (isLocationFound(lat, lng)) {
                        mSessionManager.setCurrentLat(lat);
                        mSessionManager.setCurrentLng(lng);
                        index = 0;
                        arrayListExploreDatas.clear();
                        exploreRvAdapter.notifyDataSetChanged();
                        if (mSessionManager.getIsUserLoggedIn()) {
                            getUserPosts(index);
                            //getGuestPosts(index);
                        } else {
                            getGuestPosts(index);
                        }
                    }
                }
            }
        }
        );
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     * In this method we used to do api call to send device information like device name
     * model number, device id etc to server to log the the user with specific device.
     * </p>
     */
    private void logGuestInfo() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            if (rL_no_internet != null)
                rL_no_internet.setVisibility(View.GONE);
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_GUEST, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    //System.out.println(TAG + " " + "log guest res=" + result);

                    LogDevicePojo logDevicePojo;
                    Gson gson = new Gson();
                    logDevicePojo = gson.fromJson(result, LogDevicePojo.class);

                    switch (logDevicePojo.getCode()) {
                        // success
                        case "200":
                            // Open Home page screen
                            //startActivity(new Intent(mActivity,HomePageActivity.class));
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                       //     CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                //    CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            if (rL_no_internet != null)
                rL_no_internet.setVisibility(View.VISIBLE);
        }
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
            //mRefreshLayout.setRefreshing(true);

            rL_no_internet.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();
            int limit = 40;
            offset = limit * offset;
            startTime = System.currentTimeMillis();
            Log.e("LOG","Initial Time : "+startTime);

            try {
                requestDatas.put("offset", offset);
                requestDatas.put("limit", limit);
                requestDatas.put("pushToken", mSessionManager.getPushToken());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //mProgressBar.setVisibility(View.GONE);
         /*   List<String> listKey = new ArrayList<>();
            List<String> listValue = new ArrayList<>();

            listKey.add("offset");
            listKey.add("limit");
            listKey.add("pushToken");

            listValue.add("" + offset);
            listValue.add("" + limit);
            listValue.add("" + mSessionManager.getPushToken());

            ApiCallingMethods.requestForPost(listKey, listValue, ApiUrl.GET_GUEST_ALL_POSTS, getActivity(), this, "USERPOST");

*/
            //System.out.println(TAG + " " + "offset in guest post api=" + offset + " " + "lat=" + lat + " " + "lng=" + lng);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_GUEST_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    finishTime = System.currentTimeMillis();

                    long diff = finishTime - startTime;
                    int timeInSeconds = (int)diff / 1000;
                    int hours, minutes, seconds;
                    hours = timeInSeconds / 3600;
                    timeInSeconds = timeInSeconds - (hours * 3600);
                    minutes = timeInSeconds / 60;
                    timeInSeconds = timeInSeconds - (minutes * 60);
                    seconds = timeInSeconds;
                    Log.e("LOG","Final Time : "+finishTime);

                    String diffTime = (hours<10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " second";
                    Log.e("LOG","Time : "+diffTime);

                    //mProgressBar.setVisibility(View.GONE);
                    //System.out.println(TAG + " " + "get explore guest res=" + result);
                    if (result != null && !result.isEmpty())
                        responseHandler(result,false);
                }

                @Override
                public void onError(String error, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);

                    finishTime = System.currentTimeMillis();

                    long diff = finishTime - startTime;
                    int timeInSeconds = (int)diff / 1000;
                    int hours, minutes, seconds;
                    hours = timeInSeconds / 3600;
                    timeInSeconds = timeInSeconds - (hours * 3600);
                    minutes = timeInSeconds / 60;
                    timeInSeconds = timeInSeconds - (minutes * 60);
                    seconds = timeInSeconds;
                    Log.e("LOG","Final Time : "+finishTime);

                    String diffTime = (hours<10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " second";
                    Log.e("LOG","Time : "+diffTime);

                    mRefreshLayout.setRefreshing(false);
                //    CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }


    private void getUserPosts2(int offset) {
        //AppController.getDataLagel();

        responseHandler(null,true);
    }


    private void getGuestPosts2(int offset) {
        //AppController.getDataLagel();

        responseHandler(null,true);
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     *
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat, String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>GetUserPosts</h>
     * <p>
     * In this method we used to do call getUserPosts api. And get all posts
     * in response. Once we get all post then show that in recyclerview.
     * </p>
     *
     * @param offset The pagination
     */

    long startTime,finishTime;
    private void getUserPosts(int offset) {
      //    lat = "18.575394";
      //    lng = "-72.294708";

        if (CommonClass.isNetworkAvailable(mActivity)) {
            rL_no_internet.setVisibility(View.GONE);
            //   showProgressDialog("Loading...");
            JSONObject requestDatas = new JSONObject();
            int limit = 40;
            //   offset = limit * offset;
            offset = limit * offset;
            startTime = System.currentTimeMillis();
            Log.e("LOG","Start Time : "+startTime);
            try {
                requestDatas.put("offset", offset);
                requestDatas.put("limit", limit);
                requestDatas.put("token", mSessionManager.getAuthToken());

                if (!mSessionManager.getCurrentLat().equalsIgnoreCase(""))
                {
                    lat=mSessionManager.getCurrentLat();
                    requestDatas.put("latitude", lat);
                }

                if (!mSessionManager.getCurrentLng().equalsIgnoreCase(""))
                {
                    lng=mSessionManager.getCurrentLng();
                    requestDatas.put("longitude", lng);
                }

                requestDatas.put("pushToken", mSessionManager.getPushToken());
                Log.e("Latitude_GET", "lat: " + lat);
                Log.e("Latitude_GET", "lng: " + lng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //System.out.println(TAG + " " + "offset in user post api=" + offset + " " + "lat=" + lat + " " + "lng=" + lng);

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_USER_ALL_POSTS, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    finishTime = System.currentTimeMillis();

                    long diff = finishTime - startTime;
                    int timeInSeconds = (int)diff / 1000;
                    int hours, minutes, seconds;
                    hours = timeInSeconds / 3600;
                    timeInSeconds = timeInSeconds - (hours * 3600);
                    minutes = timeInSeconds / 60;
                    timeInSeconds = timeInSeconds - (minutes * 60);
                    seconds = timeInSeconds;
                    Log.e("LOG","Final Time : "+finishTime);

                    String diffTime = (hours<10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " second";
                    Log.e("LOG","Time : "+diffTime);


                    dismissDialog();
                    //mProgressBar.setVisibility(View.GONE);
                    //System.out.println(TAG + " " + "get explore res=" + result);

                    Log.e("HomePage", "AAPI: "+result);
                    if (result != null && !result.isEmpty())
                        responseHandler(result,false);


                    //new DialogBox(mActivity).localCampaignDialog("shobhit","","","http://dev.yelo-app.xyz/public/defaultImg.png","title","message","");
                }

                @Override
                public void onError(String error, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);
                    finishTime = System.currentTimeMillis();
                    Log.e("LOG","Final Time : "+finishTime);

                    long diff = finishTime - startTime;
                    int timeInSeconds = (int)diff / 1000;
                    int hours, minutes, seconds;
                    hours = timeInSeconds / 3600;
                    timeInSeconds = timeInSeconds - (hours * 3600);
                    minutes = timeInSeconds / 60;
                    timeInSeconds = timeInSeconds - (minutes * 60);
                    seconds = timeInSeconds;

                    String diffTime = (hours<10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + " seconds";
                    Log.e("LOG","Time : "+diffTime);
                    rL_noProductFound.setVisibility(View.VISIBLE);
                    dismissDialog();
                    mRefreshLayout.setRefreshing(false);
                 //   CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    private void searchProductApi(final String searchText, int offset) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            hideKeyboard();
            arrayListExploreDatas.clear();
            exploreRvAdapter.notifyDataSetChanged();
            //mProgressBar.setVisibility(View.VISIBLE);
            int limit = 40;
            offset = limit * offset;
            //System.out.println(TAG + " " + "offset=" + offset + " " + "searched text=" + searchText);
            //String searchText2=act_search_posts.getText().toString();
            //searchText2="shoes";
            String URL = ApiUrl.SEARCH_POST + searchText + "?offset=" + offset + "&limit=" + limit + "&token=" +
                    mSessionManager.getAuthToken() + "&latitude=" + lat + "&longitude=" + lng;
            //System.out.println(TAG+" "+"url="+URL);

            OkHttp3Connection.doOkHttp3Connection(TAG, URL, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);
                    arrayListExploreDatas.clear();
                    exploreRvAdapter.notifyDataSetChanged();
                    System.out.println(TAG + " " + "search product api res=" + result);
                    Gson gson = new Gson();
                    ExplorePojoMain obj = gson.fromJson(result, ExplorePojoMain.class);

                    switch (obj.getCode()) {
                        // success
                        case "200":
                            mRefreshLayout.setRefreshing(false);
                            if (obj.getData() != null && !obj.getData().isEmpty()) {
                                responseHandler(result,false);
                            } else {
                                rL_noProductFound.setVisibility(View.VISIBLE);
                                rL_noProductFoundLocation.setVisibility(View.GONE);
                            }
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            rL_noProductFound.setVisibility(View.VISIBLE);
                            rL_noProductFoundLocation.setVisibility(View.GONE);
                            break;

                        // no data
                        case "204":
                          /*  Toast.makeText(mActivity, "No Data", Toast.LENGTH_SHORT).show();
                            SearchPostsRvAdap searchPostsRvAdap = new SearchPostsRvAdap(mActivity, new ArrayList<SearchPostDatas>());
                            GridLayoutManager mLinearLayoutManager = new GridLayoutManager(mActivity, 3);
                            mRecyclerView.setLayoutManager(mLinearLayoutManager);
                            mRecyclerView.setAdapter(searchPostsRvAdap);
                            searchPostsRvAdap.notifyDataSetChanged();
                            rL_noProductFound.setVisibility(View.VISIBLE);*/
                            mRefreshLayout.setRefreshing(false);
                            //System.out.println(TAG + " " + "no more product=" + explorePojoMain.getMessage());
                            if (arrayListExploreDatas.size() == 0) {
                                rL_noProductFound.setVisibility(View.VISIBLE);
                                rL_noProductFoundLocation.setVisibility(View.GONE);
                            }
                            break;

                        // auth token expired
                        case "422":
                            rL_noProductFound.setVisibility(View.VISIBLE);
                            rL_noProductFoundLocation.setVisibility(View.GONE);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                    rL_noProductFound.setVisibility(View.VISIBLE);
                    rL_noProductFoundLocation.setVisibility(View.GONE);
                    CommonClass.showTopSnackBar(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else
            CommonClass.showTopSnackBar(((HomePageActivity) mActivity).rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>ResponseHandler</h>
     * <p>
     * This method is called from onSuccess of getUserPosts(). In this method
     * we used to handle the above api response like if we get the error code
     * 200 then we do futher process or else show error message.
     * </p>
     *
     * @param result The response of the allPosts api
     */
    private boolean _firstTime=false;
    private void responseHandler(String result,boolean firstTime) {

        _firstTime=firstTime;
        if (firstTime)
        {
            mRefreshLayout.setRefreshing(false);
            arrayListExploreDatas.addAll(AppController.getDataLagel());
           // mRefreshLayout.setRefreshing(true);
            //Add Data

            //arrayListExploreDatas.addAll(explorePojoMain.getData());
            //Log.e("User Post", "Called with lat & long " + lat + "   " + lng + " List Size is " + arrayListExploreDatas.size());

            if (arrayListExploreDatas != null && arrayListExploreDatas.size() > 0) {
                isLoading = arrayListExploreDatas.size() < 35;

                rL_noProductFound.setVisibility(View.GONE);
                rL_noProductFoundLocation.setVisibility(View.GONE);
                exploreRvAdapter.notifyDataSetChanged();

                mRecyclerView.addOnScrollListener(new HideShowScrollListener() {
                    @Override
                    public void onHide() {
                        hideViews();
                    }

                    @Override
                    public void onShow() {
                        showViews();
                    }

                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);

                        if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                            // Do something
                            Log.i("LOG","");
                        } else if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                            // Do something
                            Log.i("LOG","");

                            int[] firstVisibleItemPositions = new int[3];
                            totalItemCount = gridLayoutManager.getItemCount();
                            lastVisibleItem = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                            if (lastVisibleItem == -1)
                                isLoading = true;

                            if (_firstTime)
                            {
                                isLoading=false;
                                dismissDialog();
                            }

                            if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                System.out.println(TAG + " " + "home page load more...");
                                isLoading = true;
                                mRefreshLayout.setRefreshing(true);
                                index = index + 1;
                                if (isFromSearch)
                                {
                                    //searchProductsApi(index);
                                    String text=android.text.TextUtils.join("|", arrayListFilter);
                                    searchProductApi(text, index);

                                }

                                else {
                                    // check is user is logged in or not if its login then show according to location if not then show all posts.
                                    if (mSessionManager.getIsUserLoggedIn())
                                    {
                                        getUserPosts(index);
                                        //getGuestPosts(index);
                                        mRefreshLayout.setRefreshing(false);
                                    }
                                    else {
                                        getGuestPosts(index);
                                    }
                                }
                            }


                        } else {
                            // Do something
                            Log.i("LOG","");
                        }
                    }

                    @Override
                    public void onScrolled() {

                    }
                });
            }
            else
            {
                Log.i("TAG","");
                rL_noProductFoundLocation.setVisibility(View.VISIBLE);
            }

        }
        else
        {

            Log.e("HomePage", "API: "+result);
            //dismissDialog();
            ExplorePojoMain explorePojoMain;
            Gson gson = new Gson();
            explorePojoMain = gson.fromJson(result, ExplorePojoMain.class);


            switch (explorePojoMain.getCode()) {
                // success
                case "200":
                    mRefreshLayout.setRefreshing(false);
                    //Add Data

                    arrayListExploreDatas.addAll(explorePojoMain.getData());
                    //Log.e("User Post", "Called with lat & long " + lat + "   " + lng + " List Size is " + arrayListExploreDatas.size());

                    if (arrayListExploreDatas != null && arrayListExploreDatas.size() > 0) {
                        isLoading = arrayListExploreDatas.size() < 35;
                        //
                        // System.out.println(TAG + " " + "home page set isLoading=" + isLoading);

                        rL_noProductFound.setVisibility(View.GONE);
                        rL_noProductFoundLocation.setVisibility(View.GONE);
                        exploreRvAdapter.notifyDataSetChanged();



                        mRecyclerView.addOnScrollListener(new HideShowScrollListener() {
                            @Override
                            public void onHide() {
                                hideViews();
                            }

                            @Override
                            public void onShow() {
                                showViews();
                            }

                            @Override
                            public void onScrolled() {
                                int[] firstVisibleItemPositions = new int[3];
                                totalItemCount = gridLayoutManager.getItemCount();
                                lastVisibleItem = gridLayoutManager.findLastVisibleItemPositions(firstVisibleItemPositions)[0];

                                if (lastVisibleItem == -1)
                                    isLoading = true;

                                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                    isLoading = true;
                                    mRefreshLayout.setRefreshing(true);

                                    /*Handler mHandler = new Handler();
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            mRefreshLayout.setRefreshing(false);
                                        }
                                    }, 3000);*/

                                    index = index + 1;

                                    String stypeFilter=typeFilter;
                                    if (isFromSearch)
                                    {
                                        //searchProductsApi(index);
                                        String text=android.text.TextUtils.join("|", arrayListFilter);
                                        //searchProductApi(text, index);
                                        searchProductsApi1(index);

                                    }

                                    else {
                                        // check is user is logged in or not if its login then show according to location if not then show all posts.
                                        if (mSessionManager.getIsUserLoggedIn())
                                            getUserPosts(index);
                                            //getGuestPosts(index);
                                        else
                                        {
                                            getGuestPosts(index);
                                        }



                                    }
                                }
                            }
                        });
                    }
                    break;

                // No more content
                case "204":
                    mRefreshLayout.setRefreshing(false);
                    System.out.println(TAG + " " + "no more product=" + explorePojoMain.getMessage());
                    if (arrayListExploreDatas.size() == 0) {
                        rL_noProductFound.setVisibility(View.VISIBLE);
                        rL_noProductFoundLocation.setVisibility(View.GONE);

                    }
                    break;

                // auth token expired
                case "401":
                    mRefreshLayout.setRefreshing(false);
                    CommonClass.sessionExpired(mActivity);
                    break;

                // Error
                default:
                    mRefreshLayout.setRefreshing(false);
                 //   CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, explorePojoMain.getMessage());
                    break;
            }
        }
    }

    private void hideViews() {
        scrollViewFilter.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        rL_action_bar.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        searchBar.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        mRefreshLayout.animate().translationY(-rL_action_bar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

        homePageActivity.bottomNavigationView.animate().translationY(homePageActivity.bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        rL_sell_stuff.animate().translationY(homePageActivity.bottomNavigationView.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();

    }

    private void showViews() {
        scrollViewFilter.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        rL_action_bar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        searchBar.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        mRefreshLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        homePageActivity.bottomNavigationView.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        rL_sell_stuff.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Open explore notification
            case R.id.rL_notification:
                if (mSessionManager.getIsUserLoggedIn()) {
                    Intent intent = new Intent(mActivity, NotificationActivity.class);
                    startActivityForResult(intent, VariableConstants.IS_NOTIFICATION_SEEN_REQ_CODE);
                } else
                    HomeFrag.this.startActivityForResult(new Intent(mActivity, LandingActivity.class), VariableConstants.LANDING_REQ_CODE);
                break;

            // Filter
            case R.id.rL_filter:
                Intent filterIntent = new Intent(mActivity, FilterActivity.class);
                filterIntent.putExtra("aL_categoryDatas", aL_categoryDatas);
                filterIntent.putExtra("address", address);
                filterIntent.putExtra("distance", distance);
                filterIntent.putExtra("sortBy", sortBy);
                filterIntent.putExtra("postedWithin", postedWithin);
                filterIntent.putExtra("currency_code", currency_code);
                filterIntent.putExtra("currency_symbol", currency);
                filterIntent.putExtra("minPrice", minPrice);
                filterIntent.putExtra("maxPrice", maxPrice);
                filterIntent.putExtra("userLat", currentLatitude);
                filterIntent.putExtra("userLng", currentLongitude);
                HomeFrag.this.startActivityForResult(filterIntent, VariableConstants.FILTER_REQUEST_CODE);
                break;

            // sell your stuff
            case R.id.rL_sell_stuff:
                if (mSessionManager.getIsUserLoggedIn()) {
                    //startActivity(new Intent(mActivity, CameraActivity.class));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        startActivity(new Intent(mActivity, Camera2Activity.class));
                    else
                        startActivity(new Intent(mActivity, CameraActivity.class));
                } else {
                    Intent redirectIntent = new Intent(mActivity, LoginOrSignupActivity.class);
                    redirectIntent.putExtra("type", "normalSignup");
                    HomeFrag.this.startActivityForResult(redirectIntent, VariableConstants.LANDING_REQ_CODE);
                }
                break;

            // Search screen
            case R.id.rL_search:
                startActivity(new Intent(mActivity, SearchProductActivity.class));
                break;

            //open share dialog
            case R.id.tV_invite:
                shareDialog();
                break;
            case R.id.tV_browse:

                if (act_search_posts.getText().toString().trim().length() > 0)
                    searchProductApi(act_search_posts.getText().toString().trim(), index);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(TAG + " " + "onactivity resultt " + "res code=" + resultCode + " " + "req code=" + requestCode + " " + "data=" + data);
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {

                case VariableConstants.FILTER_REQUEST_SEARCH:
                    allCategoryList.clear();
                    allCategoryList.addAll((ArrayList<ProductCategoryResDatas>) data.getSerializableExtra("aL_categoryDatas"));
                    aL_categoryDatas.addAll(allCategoryList);

                    if (currentLatitude.isEmpty()) {
                        locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                            @Override
                            public void onUpdateLocation() {
                                Location currentLocation = locationService.receiveLocation();
                                if (currentLocation != null) {
                                    lat = String.valueOf(currentLocation.getLatitude());
                                    lng = String.valueOf(currentLocation.getLongitude());


                                    Geocoder geocoder;
                                    List<Address> addresses;
                                    geocoder = new Geocoder(getActivity(), Locale.getDefault());

                                    try {
                                        addresses = geocoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                        //   String addre = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                        String city = addresses.get(0).getLocality();
                                        // String state = addresses.get(0).getAdminArea();
                                        //String country = addresses.get(0).getCountryName();
                                        //String postalCode = addresses.get(0).getPostalCode();
                                        //String knownName = addresses.get(0).getFeatureName(); // On
                                        address = city;

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    System.out.println(TAG + " " + "latitude" + lat + " " + "longitude=" + lng + "  City =" + address);
                                    currentLatitude = lat;
                                    currentLongitude = lng;
                                    arrayListExploreDatas.clear();
                                    exploreRvAdapter.notifyDataSetChanged();
                                    isFromSearch = false;
                                    setFilterDatasList();
                                }
                            }
                        });
                    } else {
                        arrayListExploreDatas.clear();
                        exploreRvAdapter.notifyDataSetChanged();
                        isFromSearch = true;
                        setFilterDatasList();

                    }


                    // getUserPosts(0);
                    // set for filter
       /*             aL_categoryDatas.clear();
                    aL_categoryDatas.addAll((ArrayList<ProductCategoryResDatas>) data.getSerializableExtra("aL_categoryDatas"));
                    category = data.getStringExtra("category");
                    distance = data.getStringExtra("distance");
                    sortBy = data.getStringExtra("sortBy");
                    postedWithin = data.getStringExtra("postedWithin");
                    minPrice = data.getStringExtra("minPrice");
                    maxPrice = data.getStringExtra("maxPrice");
                    currency = data.getStringExtra("currency");
                    currency_code = data.getStringExtra("currency_code");
                    currentLatitude = data.getStringExtra("currentLatitude");
                    currentLongitude = data.getStringExtra("currentLongitude");
                    address = data.getStringExtra("address");
                    filterAddress = data.getStringExtra("address");
                    postedWithinText = data.getStringExtra("postedWithinText");
                    sortByText = data.getStringExtra("sortByText");*/

                    break;

                case VariableConstants.FILTER_REQUEST_CODE:
                    try {

                        aL_categoryDatas.clear();
                        aL_categoryDatas.addAll((ArrayList<ProductCategoryResDatas>) data.getSerializableExtra("aL_categoryDatas"));
                        category = data.getStringExtra("category");
                        distance = data.getStringExtra("distance");
                        sortBy = data.getStringExtra("sortBy");
                        postedWithin = data.getStringExtra("postedWithin");
                        minPrice = data.getStringExtra("minPrice");
                        maxPrice = data.getStringExtra("maxPrice");
                        currency = data.getStringExtra("currency");
                        currency_code = data.getStringExtra("currency_code");
                        currentLatitude = data.getStringExtra("currentLatitude");
                        currentLongitude = data.getStringExtra("currentLongitude");
                        address = data.getStringExtra("address");
                        filterAddress = data.getStringExtra("address");
                        postedWithinText = data.getStringExtra("postedWithinText");
                        sortByText = data.getStringExtra("sortByText");
                        typeFilter = data.getStringExtra("typeFilter");

                        arrayListExploreDatas.clear();
                        exploreRvAdapter.notifyDataSetChanged();
                        isFromSearch = true;

                        setFilterDatasList();
                    } catch (Exception s) {
                    }

                    break;

                // set for product details
                case VariableConstants.PRODUCT_DETAILS_REQ_CODE:

                    String followRequestStatus = data.getStringExtra("followRequestStatus");
                    System.out.println(TAG + " " + "followRequestStatus=" + followRequestStatus);
                    String likesCount = data.getStringExtra("likesCount");
                    String likeStatus = data.getStringExtra("likeStatus");
                    String clickCount = data.getStringExtra("clickCount");
                    ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers = (ArrayList<ExploreLikedByUsersDatas>) data.getSerializableExtra("aL_likedByUsers");
                    if (followRequestStatus != null && !followRequestStatus.isEmpty()) {
                        if (arrayListExploreDatas.size() > clickedItemPosition) {
                            arrayListExploreDatas.get(clickedItemPosition).setFollowRequestStatus(followRequestStatus);
                            arrayListExploreDatas.get(clickedItemPosition).setLikes(likesCount);
                            arrayListExploreDatas.get(clickedItemPosition).setLikeStatus(likeStatus);
                            arrayListExploreDatas.get(clickedItemPosition).setClickCount(clickCount);
                            arrayListExploreDatas.get(clickedItemPosition).setLikedByUsers(aL_likedByUsers);
                        }
                    }
                    break;

                // coming from Notification
                case VariableConstants.IS_NOTIFICATION_SEEN_REQ_CODE:
                    boolean isNotificationSeen = data.getBooleanExtra("isNotificationSeen", false);
                    if (isNotificationSeen) {
                        notificationCount = 0;
                        tV_notification_count.setVisibility(View.GONE);
                    }
                    break;

                // Location
                case VariableConstants.REQUEST_CHECK_SETTINGS:
                    switch (resultCode) {
                        case Activity.RESULT_CANCELED:
                            index = 0;
                            arrayListExploreDatas.clear();
                            exploreRvAdapter.notifyDataSetChanged();
                            //getUserPosts(index);
                            getGuestPosts(index);
                            break;
                    }
                    break;

                // call user post api
                case VariableConstants.LANDING_REQ_CODE:
                    boolean isToRefreshHomePage = data.getBooleanExtra("isToRefreshHomePage", false);
                    boolean isFromSignup = data.getBooleanExtra("isFromSignup", false);
                    System.out.println(TAG + " " + "isToRefreshHomePage=" + isToRefreshHomePage + " " + "isFromSignup=" + isFromSignup);
                    if (isToRefreshHomePage) {
                        index = 0;
                        arrayListExploreDatas.clear();
                        exploreRvAdapter.notifyDataSetChanged();
                        //mProgressBar.setVisibility(View.VISIBLE);
                        exploreRvAdapter.notifyDataSetChanged();

                        if (runTimePermission.checkPermissions(permissionsArray)) {
                            getCurrentLocation();
                        } else {
                            requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                        }

                        // open start browsering screen
                        if (isFromSignup)
                            new DialogBox(mActivity).startBrowsingDialog();
                    }
                    break;
            }
        }
    }

    @Subscribe
    public void getMessage(ExploreResponseDatas setExploreResponseDatas) {
        if (setExploreResponseDatas != null) {
            // add item
            if (!isContainsId(setExploreResponseDatas.getPostId())) {
                arrayListExploreDatas.add(0, setExploreResponseDatas);
                exploreRvAdapter.notifyDataSetChanged();
            }
            // remove item
            else {
                if (arrayListExploreDatas.size() > 0 && setExploreResponseDatas.isToRemoveHomeItem()) {
                    for (int homeItemCount = 0; homeItemCount < arrayListExploreDatas.size(); homeItemCount++) {
                        if (setExploreResponseDatas.getPostId().equals(arrayListExploreDatas.get(homeItemCount).getPostId())) {
                            arrayListExploreDatas.remove(clickedItemPosition);
                            exploreRvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        if (arrayListExploreDatas.size() > 0)
            rL_noProductFound.setVisibility(View.GONE);
        else rL_noProductFound.setVisibility(View.VISIBLE);
    }

    /**
     * <h>IsContainsId</h>
     * <p>
     * In this method we used to check whether the given post id is
     * present or not in the current list.
     * </p>
     *
     * @param postId the given post id of product
     * @return the boolean value
     */
    public boolean isContainsId(String postId) {
        boolean flag = false;
        for (ExploreResponseDatas object : arrayListExploreDatas) {
            System.out.println(TAG + " " + "given post id=" + postId + " " + "current post id=" + object.getPostId());
            if (postId.equals(object.getPostId())) {
                flag = true;
            }
        }
        return flag;
    }

    // private void

    /**
     * <h>SetFilterDatasList</h>
     * <p>
     * In this method we used to set the all filter datas like categories(Baby abd child, electronics etc),
     * posted within(The last 24hr) etc into list.
     * </p>
     */
    private void setFilterDatasList() {
        int unSelectedCount = 0;
        dismissDialog();
        /////////////////////////////
        if (aL_categoryDatas != null && aL_categoryDatas.size() > 0) {
            category = "";
            categoryValue = "";
            for (ProductCategoryResDatas productCategoryResDatas : aL_categoryDatas) {
                if (productCategoryResDatas.isSelected()) {
                    String name = productCategoryResDatas.getName();
                    if (name!=null && name.length()>1)
                    {
                        category += "^" + name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                        categoryValue += "," + name;
                    }
                } else {
                    unSelectedCount += 1;
                }
            }

            if (unSelectedCount == aL_categoryDatas.size()) {
                category = "";
                categoryValue = "";
            }
            System.out.println(TAG + " " + "unselected count=" + unSelectedCount);
        }

        // remove comma
        if (!categoryValue.isEmpty())
            categoryValue = categoryValue.substring(1);

        System.out.println(TAG + " " + "categoryValue=" + categoryValue);

        // remove first character
        if (category != null && !category.isEmpty())
            category = category.substring(1);

        System.out.println(TAG + " " + "selected category=" + category);
        ////////////////////
        String[] category_arr;
        if (category != null && !category.isEmpty()) {
            category_arr = category.split("\\^");
        } else category_arr = new String[]{};

        // create empty arraylist
        arrayListFilter = new ArrayList<>();

        //set category
        Collections.addAll(arrayListFilter, category_arr);

        System.out.println(TAG + " " + "arrayListFilter size=" + arrayListFilter.size());

        // add distance
        if (distance != null && !distance.isEmpty() && !distance.equals("0"))
            arrayListFilter.add(distance + " " + getResources().getString(R.string.km));

        // add sorted by
        if (sortByText != null && !sortByText.isEmpty())
            arrayListFilter.add(sortByText);

        // add posted within
        if (postedWithinText != null && !postedWithinText.isEmpty())
            arrayListFilter.add(postedWithinText);

        if (minPrice != null && !minPrice.isEmpty())
            arrayListFilter.add(getResources().getString(R.string.min_price) + " " + currency + minPrice);

        if (maxPrice != null && !maxPrice.isEmpty())
            arrayListFilter.add(getResources().getString(R.string.max_price) + " " + currency + maxPrice);

        //..add Address within
        if (filterAddress != null && !filterAddress.isEmpty())
            arrayListFilter.add(filterAddress);

        inflateFilterDatas();
    }

    /**
     * <h>InflateFilterDatas</h>
     * <p>
     * In this method we used to inflate the the filtered data list like distance, price etc on the top
     * of the screen.
     * </p>
     */
    private void inflateFilterDatas() {
        System.out.println(TAG + " " + "array list filter size=" + arrayListFilter.size());
        if (arrayListFilter != null && arrayListFilter.size() > 0) {
            view_filter_divider.setVisibility(View.INVISIBLE);
            linear_filter.removeAllViews();
            LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
            linear_filter.removeAllViews();
            for (int postCount = 0; postCount < arrayListFilter.size(); postCount++) {
                final View view = layoutInflater.inflate(R.layout.single_row_selected_filter_list, null, false);
                TextView tV_filter = (TextView) view.findViewById(R.id.tV_filter);
                tV_filter.setText(arrayListFilter.get(postCount));
                ImageView tV_delete = (ImageView) view.findViewById(R.id.tV_delete);

                if (arrayListFilter.get(postCount).length() > 30) {
                    tV_filter.setWidth(CommonClass.getDeviceWidth(mActivity) / 2);
                }

                final int finalPostCount = postCount;
                tV_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (arrayListFilter.size() > 0) {
                            String deletedValue = arrayListFilter.get(finalPostCount);
                            System.out.println(TAG + " " + "filter value=" + deletedValue);

                            // remove category value
                            if (aL_categoryDatas.size() > 0) {
                                for (int i = 0; i < aL_categoryDatas.size(); i++) {
                                    System.out.println(TAG + " get cate name=" + aL_categoryDatas.get(i).getName());
                                    if (aL_categoryDatas.get(i).getName().equalsIgnoreCase(deletedValue)) {
                                        System.out.println(TAG + "removed=" + aL_categoryDatas.get(i).getName());
                                        aL_categoryDatas.get(i).setSelected(false);
                                    }
                                }
                            }

                            // remove distance value
                            if (deletedValue.contains(getResources().getString(R.string.km)))
                                distance = "";

                            // remove min price
                            if (deletedValue.contains(getResources().getString(R.string.min_price)))
                                minPrice = "";

                            // remove max price
                            if (deletedValue.contains(getResources().getString(R.string.max_price)))
                                maxPrice = "";

                            // remove address
                            if (deletedValue.contains(filterAddress))
                                filterAddress = "";

                            // remove posted within value
                            if (postedWithinArr.length > 0) {
                                for (String post : postedWithinArr) {
                                    if (post.equalsIgnoreCase(deletedValue)) {
                                        postedWithinText = "";
                                        postedWithin = "";
                                    }
                                }
                            }

                            // remove sort by value
                            if (sortByArr.length > 0) {
                                for (String sort : sortByArr) {
                                    if (sort.equalsIgnoreCase(deletedValue)) {
                                        sortBy = "";
                                        sortByText = "";
                                    }
                                }
                            }

                            arrayListFilter.remove(finalPostCount);
                            linear_filter.removeView(view);
                            setFilterDatasList();
                            //inflateFilterDatas();
                        }
                    }
                });
                linear_filter.addView(view);
            }
            //..add padding while filter shown..//
            mRecyclerView.setPadding(0, recycleviewPaddingTop + recycleviewPaddingTop, 0, 0);
        } else {
            linear_filter.removeAllViews();
            mRecyclerView.setPadding(0, recycleviewPaddingTop, 0, 0);
        }
        //call filter api
        arrayListExploreDatas.clear();
        exploreRvAdapter.notifyDataSetChanged();
        //mRefreshLayout.setRefreshing(true);
        index = 0;

        if (arrayListFilter.size() > 0) {
            // searchProductsApi(index);
            //StringBuilder sb = new StringBuilder();
            /*for (int i = 0; i > arrayListFilter.size(); i++) {
                sb.append(arrayListFilter.get(i).toString());
            }*/

            String text=android.text.TextUtils.join("|", arrayListFilter);
            //dts
            String sTypeFilter=typeFilter;
            //if (sTypeFilter!=null && sTypeFilter.length()==0)
            if (!lagel1Pref.getFilter())
            {
                searchProductApi(text,0);
            }
            else
            {
                searchProductsApi1(40);
                //searchProductApi(text,0);
            }

            //searchProductApi(0);
        }
        else {
            isFromSearch = false;

            // check is user is logged in or not if its login then show according to location if not then show all posts.
            if (mSessionManager.getIsUserLoggedIn()) {
                System.out.println(TAG + " " + "lat in refreshing=" + lat + " " + "lng=" + lng);
                if (isLocationFound(lat, lng))
                    getUserPosts(index);
                    //getGuestPosts(1);
                else getCurrentLocation();
            } else getGuestPosts2(index);
        }
    }

    /**
     * <h>SearchProductsApi</h>
     * <p>
     * In this method we do api call for searching product based on filtering
     * like on category, min price, max price etc.
     * </p>
     */
    private void searchProductsApi1(int offset) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            rL_no_internet.setVisibility(View.GONE);
            int limit = 40;
            offset = limit * offset;
            JSONObject request_datas = new JSONObject();
            try {
                //currency_code="";

                if (currency_code!=null && currency_code.equals("All"))
                {
                    request_datas.put("currency", "");
                }
                else
                {
                    request_datas.put("currency", currency_code);
                }

                request_datas.put("distance", distance);
                request_datas.put("latitude", currentLatitude);
                request_datas.put("limit", limit);
                request_datas.put("location", address);
                request_datas.put("longitude", currentLongitude);
                request_datas.put("maxPrice", maxPrice);
                request_datas.put("minPrice", minPrice);
                //request_datas.put("offset", offset);
                request_datas.put("offset", 0);
                request_datas.put("postedWithin", postedWithin);
                request_datas.put("searchKey", categoryValue);
                request_datas.put("sortBy", sortBy);
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("pushToken", mSessionManager.getPushToken());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String urls=ApiUrl.SEARCH_FILTER;
            Log.i("LOG",urls);
            //System.out.println(TAG + " " + "offset in search api=" + offset + " Lat= " + currentLatitude + " Lng= " + currentLongitude + " Address=" + address);

            OkHttp3Connection.doOkHttp3Connection(TAG, urls, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);

                    System.out.println(TAG + " " + "product search res=" + result);
                    if (result != null && !result.isEmpty())
                        responseHandler(result,false);
                }

                @Override
                public void onError(String error, String user_tag) {
                    //mProgressBar.setVisibility(View.GONE);
                    mRefreshLayout.setRefreshing(false);
                 //   CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            mRefreshLayout.setRefreshing(false);
            rL_no_internet.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isHomeFragVisible = isVisibleToUser;
    }

    @Override
    public void onItemClick(int pos, ImageView imageView) {
        System.out.println(TAG + " " + "pos=" + pos + " " + "list size=" + arrayListExploreDatas.size());
        if (arrayListExploreDatas.size() > pos) {
            // Log viewed_content on facebook

            Bundle params = new Bundle();
            params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "USD");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, "product");
            params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, "\"" + arrayListExploreDatas.get(pos).getPostId() + "\"");

            logger.logEvent(AppEventsConstants.EVENT_NAME_VIEWED_CONTENT,
                    Double.parseDouble(arrayListExploreDatas.get(pos).getPrice()),
                    params);

            // Show Product

            clickedItemPosition = pos;
            Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
            intent.putExtra("productName", arrayListExploreDatas.get(pos).getProductName());

            String str= arrayListExploreDatas.get(pos).getMainUrl();

            if(str.length()>50) {
                if(str.charAt(4)=='s')
                    str = str.substring(0, 50) + "w_300/q_auto:good/" + str.substring(50);
                else
                    str = str.substring(0, 49) + "w_300/q_auto:good/" + str.substring(49);
            }

            intent.putExtra("image", str);

            if (arrayListExploreDatas.get(pos).getCategoryData() != null && arrayListExploreDatas.get(pos).getCategoryData().size() > 0)
                intent.putExtra("category", arrayListExploreDatas.get(pos).getCategoryData().get(0).getCategory());
            intent.putExtra("likes", arrayListExploreDatas.get(pos).getLikes());
            intent.putExtra("likeStatus", arrayListExploreDatas.get(pos).getLikeStatus());
            intent.putExtra("currency", arrayListExploreDatas.get(pos).getCurrency());
            intent.putExtra("price", arrayListExploreDatas.get(pos).getPrice());
            intent.putExtra("postedOn", arrayListExploreDatas.get(pos).getPostedOn());
            intent.putExtra("thumbnailImageUrl", arrayListExploreDatas.get(pos).getThumbnailImageUrl());
            intent.putExtra("likedByUsersArr", arrayListExploreDatas.get(pos).getLikedByUsers());
            intent.putExtra("description", arrayListExploreDatas.get(pos).getDescription());
            intent.putExtra("condition", arrayListExploreDatas.get(pos).getCondition());
            intent.putExtra("place", arrayListExploreDatas.get(pos).getPlace());
            intent.putExtra("latitude", arrayListExploreDatas.get(pos).getLatitude());
            intent.putExtra("longitude", arrayListExploreDatas.get(pos).getLongitude());
            intent.putExtra("postedByUserName", arrayListExploreDatas.get(pos).getPostedByUserName());
            intent.putExtra("postId", arrayListExploreDatas.get(pos).getPostId());
            intent.putExtra("postsType", arrayListExploreDatas.get(pos).getPostsType());
            intent.putExtra("followRequestStatus", arrayListExploreDatas.get(pos).getFollowRequestStatus());
            intent.putExtra("clickCount", arrayListExploreDatas.get(pos).getClickCount());
            intent.putExtra("negotiable", arrayListExploreDatas.get(pos).getNegotiable());
            intent.putExtra("memberProfilePicUrl", arrayListExploreDatas.get(pos).getMemberProfilePicUrl());
            intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
            System.out.println(TAG + " " + "followRequestStatus=" + arrayListExploreDatas.get(pos).getFollowRequestStatus());
            HomeFrag.this.startActivityForResult(intent, VariableConstants.PRODUCT_DETAILS_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println(TAG + " " + "on request permission result called...");
        switch (requestCode) {
            case VariableConstants.PERMISSION_REQUEST_CODE:
                System.out.println("grant result=" + grantResults.length);
                if (grantResults.length > 0) {
                    for (int count = 0; count < grantResults.length; count++) {
                        if (grantResults[count] != PackageManager.PERMISSION_GRANTED)
                            allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted=" + runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray)) {
                        getCurrentLocation();
                    }
                }
        }
    }

    /**
     * <h>ShowErrorMessage</h>
     * <p>
     * In this method we used to show dialog for error message if
     * the user denies any permissions.
     * </p>
     *
     * @param permissionName the permission name
     */
    public void allowPermissionAlert(final String permissionName) {
        final Dialog errorMessageDialog = new Dialog(mActivity);
        errorMessageDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        errorMessageDialog.setContentView(R.layout.dialog_permission_denied);
        errorMessageDialog.getWindow().setGravity(Gravity.BOTTOM);
        errorMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        errorMessageDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        errorMessageDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView tV_permission_name = (TextView) errorMessageDialog.findViewById(R.id.tV_permission_name);
        String deniedPermission;
        if (permissionName.contains("CAMERA")) {
            deniedPermission = mActivity.getResources().getString(R.string.camera);
        } else if (permissionName.contains("WRITE_EXTERNAL_STORAGE")) {
            deniedPermission = mActivity.getResources().getString(R.string.external_storage);
        } else if (permissionName.contains("ACCESS_COARSE_LOCATION")) {
            deniedPermission = mActivity.getResources().getString(R.string.coarse_location);
        } else if (permissionName.contains("ACCESS_FINE_LOCATION")) {
            deniedPermission = mActivity.getResources().getString(R.string.fine_location);
        } else if (permissionName.contains("READ_CONTACTS")) {
            deniedPermission = mActivity.getResources().getString(R.string.read_contact);
        } else deniedPermission = permissionName;
        deniedPermission = mActivity.getResources().getString(R.string.please_allow_the_permission) + " " + deniedPermission + " " + mActivity.getResources().getString(R.string.by_clicking_on_allow_button);
        tV_permission_name.setText(deniedPermission);

        // Cancel
        TextView tV_cancel = (TextView) errorMessageDialog.findViewById(R.id.tV_cancel);
        tV_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (permissionName.contains("ACCESS_COARSE_LOCATION") && coarseLocResult == PackageManager.PERMISSION_DENIED) {
                    System.out.println(TAG + " " + "location fine denied...");
                    isFineLocDenied = true;
                }

                if (permissionName.contains("ACCESS_FINE_LOCATION") && fineLocResult == PackageManager.PERMISSION_DENIED) {
                    System.out.println(TAG + " " + "location coarse denied...");
                    isCoarseLocDenied = true;
                }

                System.out.println(TAG + " " + "fineLocResult denied=" + isFineLocDenied + " " + "coarseLocResult denied=" + isCoarseLocDenied);
                if (isCoarseLocDenied && isFineLocDenied) {
                    index = 0;
                    lat = lng = "";
                    arrayListExploreDatas.clear();
                    exploreRvAdapter.notifyDataSetChanged();
                    //getUserPosts(index);
                    getGuestPosts(index);
                }

                errorMessageDialog.dismiss();
            }
        });

        // allow
        RelativeLayout rL_allow = (RelativeLayout) errorMessageDialog.findViewById(R.id.rL_allow);
        rL_allow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (mActivity.shouldShowRequestPermissionRationale(permissionName))
                        requestPermissions(permissionsArray, VariableConstants.PERMISSION_REQUEST_CODE);
                }
                errorMessageDialog.dismiss();
            }
        });
        errorMessageDialog.show();
    }

    public void sendLocationOnStartUp() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDatas = new JSONObject();
            // Token, place, city, countrySname, latitude, longitude
            try {
                requestDatas.put("token", mSessionManager.getAuthToken());
                requestDatas.put("latitude", flat);
                requestDatas.put("longitude", flng);
                requestDatas.put("location", flocation);
                requestDatas.put("city", fcity);
                requestDatas.put("countrySname", fcountry);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.USER_LOCATION, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    Log.d(TAG + " response", result);
                }

                @Override
                public void onError(String error, String user_tag) {
                    CommonClass.showTopSnackBar(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            CommonClass.showTopSnackBar(((HomePageActivity) mActivity).rL_rootElement, getResources().getString(R.string.NoInternetAccess));
        }
    }

    //..for update location every time on launch
    private void getLocationOnLaunch() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    if (currentLocation != null) {
                        flat = String.valueOf(currentLocation.getLatitude());
                        flng = String.valueOf(currentLocation.getLongitude());

                        System.out.println(TAG + " " + "flat=" + flat + " " + "flng=" + flng);

                        if (isLocationFound(flat, flng)) {
                            mSessionManager.setCurrentLat(flat);
                            mSessionManager.setCurrentLng(flng);
                            flocation = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            fcity = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            fcountry = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d("city,country", fcity + ", " + fcountry + ", " + flocation);

                            if (flocation!=null && (flocation.isEmpty() || flocation == null)) {
                                flocation = fcity + ", " + fcountry;
                            }
                            if (fcity!=null && (!fcity.isEmpty() && fcity != null)) {
                                if(first == true) {
                                    sendLocationOnStartUp();
                                    first = false;
                                }
                            }
                        }
                    }
                }
            }
            );
        } else {
        }
    }

    /**
     * For result
     *
     * @param response
     * @param flag
     */
    @Override
    public void onResultCallback(String response, String flag) {
        Log.e("Response", response);

    }

    /**
     * For erro
     *
     * @param error
     */
    @Override
    public void onErrorCallback(VolleyError error) {
        Log.e("Error", error.toString());

    }

    private void shareDialog() {

        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
        sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_share_dialog) + " " + "https://play.google.com/store/apps/details?id=com.lagel.com&hl=en");
        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_via)));
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * <h>GetCountryCodeList</h>
     * <p>
     * In this method we used to get the all country iso code and number into
     * list. And find the user current country iso code and number and set
     * before the mobile number.
     * </p>
     */
    private ArrayList<Country> getCountryCodeList() {
        String[] country_array = getResources().getStringArray(R.array.countryCodes);
        if (country_array.length > 0) {
            for (String aCountry_array : country_array) {
                try {
                    String[] getCountryList;
                    getCountryList = aCountry_array.split(",");
                    String countryCode, countryName,countryFullName;
                    countryCode = getCountryList[0];
                    countryName = getCountryList[1];
                    Log.e("Tag",countryName);
                    countryFullName = getCountryList[2];
                    Country country = new Country();
                    country.setCode(countryCode.trim());
                    country.setName(countryName.trim());
                    country.setFullname(countryFullName.trim());
                    arrayListCountry.add(country);

                }catch(Exception ex)
                {
                    Log.e("TAG",ex.getMessage());
                }
            }

            /*if (arrayListCountry.size() > 0) {
                dialogCountryList = new DialogCountryList(mActivity, arrayListCountry);
                //String countryIsoCode = Locale.getDefault().getCountry();
                String countryIsoCode = mSessionManager.getCountryIso();
                if (countryIsoCode != null && !countryIsoCode.isEmpty()) {
                    String countryIsoNo = setCurrentCountryCode(countryIsoCode);
                    countryIsoNumber = getResources().getString(R.string.plus) + countryIsoNo;
                    System.out.println(TAG + " " + "countryIsoNumber=" + countryIsoNumber);
                    tV_country_iso_no.setText(countryIsoNumber);
                    tV_country_code.setText(countryIsoCode);
                }
            }*/
        }

        return  arrayListCountry;
    }


    public void addTime() throws InterruptedException {
        //Here I am using a Handler to perform the refresh
        // action after some time to show some fake time
        // consuming task is being performed.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //EveryTime when this method is called
                // time at that instant will be added to
                // the ArrayList. This is just to populate
                // the recycler view.

                //setRefreshing(false) method is called to stop
                // the refreshing animation view.
                if(mRefreshLayout.isRefreshing())
                    mRefreshLayout.setRefreshing(false);
            }
        },4000);

    }
}
