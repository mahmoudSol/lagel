package com.lagel.com.main.tab_fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.lagel.com.R;
import com.lagel.com.adapter.ViewPagerAdapter;
import com.lagel.com.event_bus.BusProvider;
import com.lagel.com.main.activity.ConnectPaypalActivity;
import com.lagel.com.main.activity.DiscoverPeopleActivity;
import com.lagel.com.main.activity.EditProfileActivity;
import com.lagel.com.main.activity.HomePageActivity;
import com.lagel.com.main.activity.ProfileSettingActivity;
import com.lagel.com.main.activity.SelfFollowingActivity;
import com.lagel.com.main.activity.VerifyEmailIdActivity;
import com.lagel.com.main.activity.VerifyLoginWithFacebook;
import com.lagel.com.main.activity.VerifyLoginWithGplus;
import com.lagel.com.main.view_pager.my_profile_frag.FavouriteFrag;
import com.lagel.com.main.view_pager.my_profile_frag.SellingFrag;
import com.lagel.com.main.view_pager.my_profile_frag.SoldFrag;
import com.lagel.com.pojo_class.ProfileFollowingCount;
import com.lagel.com.pojo_class.profile_pojo.ProfilePojoMain;
import com.lagel.com.pojo_class.profile_pojo.ProfileResultDatas;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CircleTransform;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.PositionedCropTransformation;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <h>ProfileFrag</h>
 * <p>
 * In this class we show the user profile description like user name, user profile
 * users total posts, following count, follower count etc
 * </p>
 *
 * @since 3/31/2017
 */
public class ProfileFrag extends Fragment implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ProfileFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private Activity mActivity;
    private String profilePicUrl = "", fullName = "", userName = "", email = "", phoneNumber = "", website = "", bio = "", googleVerified = "", facebookVerified = "", emailVerified = "", paypalUrl = "", followingCount = "";
    private VerifyLoginWithGplus verifyLoginWithGplus;
    private SellingFrag sellingFrag;
    private SoldFrag soldFrag;
    private FavouriteFrag favouriteFrag;
    private LinearLayout linear_load_profile;

    // Declare xml variables
    private ProgressBar progress_bar_profile;
    private ImageView iV_profile_pic;
    private ImageView iv_background, iV_email_icon, iV_google_icon, iV_fbicon, iV_paypal_icon;
    private TextView tV_posts_count, tV_follower_count, tV_following_count, tV_fullName, tV_bio, tV_website, tV_postsText;
    private ViewPager profileViewPager;
    private RelativeLayout rL_edit_people, rL_verify_paypal;
    private boolean isProfileFragVisible;
    private GoogleApiClient mGoogleApiClient;
    private AppBarLayout appBarLayout;
    private RatingBar ratingBar;
    private String avgRating, ratedBy;
    private TextView tv_ratedBy;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
        mSessionManager = new SessionManager(mActivity);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(mActivity.getResources().getString(R.string.servers_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage(getActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_profile, container, false);
        progress_bar_profile = (ProgressBar) view.findViewById(R.id.progress_bar_profile);
        appBarLayout = (AppBarLayout) view.findViewById(R.id.appBarLayout);
        appBarLayout.setVisibility(View.GONE);

        // profile pic
        iV_profile_pic = (ImageView) view.findViewById(R.id.iV_profile_pic);
        iv_background = (ImageView) view.findViewById(R.id.iv_background);
        iV_profile_pic.getLayoutParams().width = CommonClass.getDeviceWidth(mActivity) / 4;
        iV_profile_pic.getLayoutParams().height = CommonClass.getDeviceWidth(mActivity) / 4;
        CoordinatorLayout coordinate_rootView = (CoordinatorLayout) view.findViewById(R.id.coordinate_rootView);

        // User name and full name
        tV_fullName = (TextView) view.findViewById(R.id.tV_fullName);
        tV_website = (TextView) view.findViewById(R.id.tV_website);
        tV_website.setOnClickListener(this);
        tV_bio = (TextView) view.findViewById(R.id.tV_bio);

        ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
        tv_ratedBy = (TextView) view.findViewById(R.id.tV_ratedBy);
        // verification ioon like email, google plus , facebook etc

        // Facebook verification
        ProgressBar pBar_fbVerify = (ProgressBar) view.findViewById(R.id.pBar_fbVerify);
        iV_fbicon = (ImageView) view.findViewById(R.id.iV_fbicon);
        iV_paypal_icon = (ImageView) view.findViewById(R.id.iV_paypal_icon);
        iV_paypal_icon.setOnClickListener(this);
        ((HomePageActivity) (mActivity)).verifyLoginWithFacebook = new VerifyLoginWithFacebook(mActivity, coordinate_rootView, pBar_fbVerify, iV_fbicon, facebookVerified);
        iV_fbicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (facebookVerified == null || !facebookVerified.equals("1"))
                    ((HomePageActivity) (mActivity)).verifyLoginWithFacebook.loginWithFbWithSdk();
            }
        });

        // Google plus verification
        ProgressBar pBar_gPlusVerify = (ProgressBar) view.findViewById(R.id.pBar_gPlusVerify);
        iV_google_icon = (ImageView) view.findViewById(R.id.iV_google_icon);
        verifyLoginWithGplus = new VerifyLoginWithGplus(mActivity, mGoogleApiClient, coordinate_rootView, pBar_gPlusVerify, iV_google_icon, googleVerified);
        iV_google_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLoginWithGplus.signInWithGoogle(ProfileFrag.this, null);
            }
        });

        // Email verification
        iV_email_icon = (ImageView) view.findViewById(R.id.iV_email_icon);
        iV_email_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, VerifyEmailIdActivity.class));
            }
        });

        // set profile pic
        String profilePic = mSessionManager.getUserImage();
        if (profilePic != null && !profilePic.isEmpty()) {
            Picasso.with(mActivity)
                    .load(profilePic)
                    .placeholder(R.drawable.default_profile_image)
                    .error(R.drawable.default_profile_image)
                    .transform(new CircleTransform())
                    .into(iV_profile_pic);

            Glide.with(mActivity)
                    .load(profilePic)
                    .transform(new PositionedCropTransformation((mActivity), 0, 0))
                    .placeholder(R.drawable.default_profile_cover_pic)
                    .error(R.drawable.default_profile_cover_pic)
                    .into(iv_background);
        }

        // Full name
        fullName = mSessionManager.getUserName();
        Log.d("username111", fullName);
        if (fullName != null)
            tV_fullName.setText(fullName);

        // number of posts
        tV_posts_count = (TextView) view.findViewById(R.id.tV_posts_count);
        tV_postsText = (TextView) view.findViewById(R.id.tV_postsText);
        tV_follower_count = (TextView) view.findViewById(R.id.tV_follower_count);
        tV_following_count = (TextView) view.findViewById(R.id.tV_following_count);

        // Following
        RelativeLayout rL_following = (RelativeLayout) view.findViewById(R.id.rL_following);
        rL_following.setOnClickListener(this);

        // Follower
        RelativeLayout rL_follower = (RelativeLayout) view.findViewById(R.id.rL_follower);
        rL_follower.setOnClickListener(this);

        // Edit profile
        rL_edit_people = (RelativeLayout) view.findViewById(R.id.rL_edit_people);

        // verify paypal
        rL_verify_paypal = (RelativeLayout) view.findViewById(R.id.rL_verify_paypal);
        rL_verify_paypal.setOnClickListener(this);

        // setting
        RelativeLayout rL_setting = (RelativeLayout) view.findViewById(R.id.rL_setting);
        rL_setting.setOnClickListener(this);

        // Discover people
        RelativeLayout rL_discovery_people = (RelativeLayout) view.findViewById(R.id.rL_discovery_people);
        rL_discovery_people.setOnClickListener(this);

        // view pager
        profileViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        profileViewPager.setOffscreenPageLimit(2);
        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(profileViewPager);

        System.out.println(TAG + " " + "frag visible profile=" + isProfileFragVisible);

        // Re-Load profile datas
        linear_load_profile = (LinearLayout) view.findViewById(R.id.linear_load_profile);
        linear_load_profile.setVisibility(View.GONE);
        linear_load_profile.setOnClickListener(this);

        // Load profile datas
        getUserProfileDatas();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println(TAG + " " + "onActivityCreated profile");
    }

    /**
     * <h>GetUserProfileDatas</h>
     * <p>
     * In this method we do api call to get users profile complete description
     * Once we receive all datas then set all values to the respective fields.
     * </p>
     */
    private void getUserProfileDatas() {
        // token, limit, offset, membername
        if (CommonClass.isNetworkAvailable(mActivity)) {
            linear_load_profile.setVisibility(View.GONE);
            progress_bar_profile.setVisibility(View.VISIBLE);
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("limit", "20");
                request_datas.put("offset", "0");
                request_datas.put("membername", mSessionManager.getUserName());

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.USER_PROFILE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    linear_load_profile.setVisibility(View.GONE);
                    progress_bar_profile.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "my profile res=" + result);

                    ProfilePojoMain profilePojoMain;
                    Gson gson = new Gson();
                    profilePojoMain = gson.fromJson(result, ProfilePojoMain.class);

                    switch (profilePojoMain.getCode()) {
                        // Success
                        case "200":
                            appBarLayout.setVisibility(View.VISIBLE);
                            // Set my profile fields like image, name etc
                            ArrayList<ProfileResultDatas> profileResponseDatas = profilePojoMain.getData();
                            if (profileResponseDatas != null && profileResponseDatas.size() > 0) {
                                ProfileResultDatas profileResultDatas = profileResponseDatas.get(0);
                                String noOfPost, followersCount;
                                int int_postCount = 0;
                                profilePicUrl = profileResultDatas.getProfilePicUrl();
                                noOfPost = profileResultDatas.getPosts();
                                if (noOfPost != null && !noOfPost.isEmpty())
                                    int_postCount = Integer.parseInt(noOfPost);

                                if (int_postCount > 1)
                                    tV_postsText.setText(getResources().getString(R.string.posts));
                                else tV_postsText.setText(getResources().getString(R.string.post));

                                followersCount = profileResultDatas.getFollowers();
                                followingCount = profileResultDatas.getFollowing();
                                fullName = profileResultDatas.getFullName();
                                email = profileResultDatas.getEmail();
                                userName = profileResultDatas.getUsername();
                                phoneNumber = profileResultDatas.getPhoneNumber();
                                website = profileResultDatas.getWebsite();
                                bio = profileResultDatas.getBio();
                                googleVerified = profileResultDatas.getGoogleVerified();
                                facebookVerified = profileResultDatas.getFacebookVerified();
                                emailVerified = profileResultDatas.getEmailVerified();
                                paypalUrl = profileResultDatas.getPaypalUrl();
                                avgRating = profileResultDatas.getAvgRating();
                                ratedBy = profileResponseDatas.get(0).getRatedBy();
                                // Set profile pic
                                if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                                    Picasso.with(mActivity)
                                            .load(profilePicUrl)
                                            .placeholder(R.drawable.default_profile_image)
                                            .error(R.drawable.default_profile_image)
                                            .transform(new CircleTransform())
                                            .into(iV_profile_pic);

                                    // set profile pic as cover pic
                                    Glide.with(mActivity)
                                            .load(profilePicUrl)
                                            .transform(new PositionedCropTransformation((mActivity), 0, 0))
                                            .placeholder(R.drawable.default_profile_cover_pic)
                                            .error(R.drawable.default_profile_cover_pic)
                                            .into(iv_background);
                                }

                                // set rating
                                if (avgRating != null) {
                                    ratingBar.setRating(Float.parseFloat(avgRating));
                                }
                                if (ratedBy != null) {
                                    ratedBy = "(" + ratedBy + ")";
                                    tv_ratedBy.setText(ratedBy);
                                }

                                // set number of posts
                                if (noOfPost != null)
                                    tV_posts_count.setText(noOfPost);

                                // Followers count
                                if (followersCount != null)
                                    tV_follower_count.setText(followersCount);

                                // Following count
                                if (followingCount != null)
                                    tV_following_count.setText(followingCount);

                                // Full name
                                // set username rather than Fullname
                                Log.d("username112", userName);
                                if (userName != null)
                                    tV_fullName.setText(userName);

                                // Edit profile
                                rL_edit_people.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent editProfileIntent = new Intent(mActivity, EditProfileActivity.class);
                                        editProfileIntent.putExtra("profilePicUrl", profilePicUrl);
                                        editProfileIntent.putExtra("username", userName);
                                        editProfileIntent.putExtra("fullName", fullName);
                                        editProfileIntent.putExtra("email", email);
                                        editProfileIntent.putExtra("phoneNumber", phoneNumber);
                                        editProfileIntent.putExtra("website", website);
                                        editProfileIntent.putExtra("bio", bio);
                                        ProfileFrag.this.startActivityForResult(editProfileIntent, VariableConstants.PROFILE_REQUEST_CODE);
                                    }
                                });

                                // set google verified
                                if (googleVerified != null && googleVerified.equals("1"))
                                    iV_google_icon.setImageResource(R.drawable.gplus_verified_icon);

                                // set facebook verified
                                if (facebookVerified != null && facebookVerified.equals("1"))
                                    iV_fbicon.setImageResource(R.drawable.facebook_verified_icon);

                                // set email verified
                                if (emailVerified != null && emailVerified.equals("1"))
                                    iV_email_icon.setImageResource(R.drawable.email_verified_icon);

                                // set payal verified
                                if (paypalUrl != null && !paypalUrl.equals("0")) {
                                    iV_paypal_icon.setVisibility(View.VISIBLE);
                                    rL_verify_paypal.setVisibility(View.GONE);
                                } else {
                                    iV_paypal_icon.setVisibility(View.GONE);
                                    rL_verify_paypal.setVisibility(View.VISIBLE);
                                }

                                // set bio
                                if (bio != null && !bio.isEmpty()) {
                                    tV_bio.setVisibility(View.VISIBLE);
                                    tV_bio.setText(bio);
                                } else tV_bio.setVisibility(View.GONE);

                                // set website
                                if (website != null && !website.isEmpty()) {
                                    tV_website.setVisibility(View.VISIBLE);
                                    tV_website.setText(website);
                                } else tV_website.setVisibility(View.GONE);
                            }

                            sellingFrag = SellingFrag.newInstance(mSessionManager.getUserName(), true);
                            soldFrag = SoldFrag.newInstance(mSessionManager.getUserName(), true);
                            favouriteFrag = FavouriteFrag.newInstance(mSessionManager.getUserName());

                            setupViewPager();
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                         //   CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, profilePojoMain.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    linear_load_profile.setVisibility(View.VISIBLE);
                    progress_bar_profile.setVisibility(View.GONE);
                 //   CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, error);
                }
            });
        } else {
            linear_load_profile.setVisibility(View.VISIBLE);
            progress_bar_profile.setVisibility(View.GONE);
            CommonClass.showSnackbarMessage(((HomePageActivity) mActivity).rL_rootElement, getResources().getString(R.string.NoInternetAccess));
        }
    }

    /**
     * <h>SetupViewPager</h>
     * <p>
     * In this method we used to set viewpager with respective fragments.
     * </p>
     */
    private void setupViewPager() {
        if (isAdded()) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            adapter.addFragment(sellingFrag, getResources().getString(R.string.selling));
            adapter.addFragment(soldFrag, getResources().getString(R.string.sold));
            adapter.addFragment(favouriteFrag, getResources().getString(R.string.favourites));
            profileViewPager.setAdapter(adapter);
        }
    }

    @Override
    public void onClick(View v) {
        int mFollowingCount = 0;
        Intent intent;
        switch (v.getId()) {
            // Profile setting
            case R.id.rL_setting:
                intent = new Intent(mActivity, ProfileSettingActivity.class);
                intent.putExtra("payPalLink", paypalUrl);
                ProfileFrag.this.startActivityForResult(intent, VariableConstants.PAYPAL_REQ_CODE);
                break;

            // Discover people
            case R.id.rL_discovery_people:
                if (followingCount != null && !followingCount.isEmpty())
                    mFollowingCount = Integer.parseInt(followingCount);
                intent = new Intent(mActivity, DiscoverPeopleActivity.class);
                intent.putExtra("followingCount", mFollowingCount);
                startActivityForResult(intent, VariableConstants.FOLLOW_COUNT_REQ_CODE);
                break;

            // Following
            case R.id.rL_following:
                if (followingCount != null && !followingCount.isEmpty())
                    mFollowingCount = Integer.parseInt(followingCount);
                intent = new Intent(mActivity, SelfFollowingActivity.class);
                intent.putExtra("title", getResources().getString(R.string.Following));
                intent.putExtra("isFollower", false);
                intent.putExtra("followingCount", mFollowingCount);
                startActivityForResult(intent, VariableConstants.FOLLOW_COUNT_REQ_CODE);
                break;

            // Follower
            case R.id.rL_follower:
                if (followingCount != null && !followingCount.isEmpty())
                    mFollowingCount = Integer.parseInt(followingCount);
                intent = new Intent(mActivity, SelfFollowingActivity.class);
                intent.putExtra("title", getResources().getString(R.string.Followers));
                intent.putExtra("isFollower", true);
                intent.putExtra("followingCount", mFollowingCount);
                startActivityForResult(intent, VariableConstants.FOLLOW_COUNT_REQ_CODE);
                break;

            // verify paypal
            case R.id.rL_verify_paypal:
                intent = new Intent(mActivity, ConnectPaypalActivity.class);
                ProfileFrag.this.startActivityForResult(intent, VariableConstants.PAYPAL_REQ_CODE);
                break;

            // open website link
            case R.id.tV_website:
                System.out.println(TAG + " " + "website=" + website);

                if (website != null && !website.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + website));
                    startActivity(browserIntent);
                }
                break;

            // re open paypal for update
            case R.id.iV_paypal_icon:
                System.out.println(TAG + " " + "send paypal url=" + paypalUrl);
                intent = new Intent(mActivity, ConnectPaypalActivity.class);
                intent.putExtra("payPalLink", paypalUrl);
                ProfileFrag.this.startActivityForResult(intent, VariableConstants.PAYPAL_REQ_CODE);
                break;

            // Reload profile data again
            case R.id.linear_load_profile:
                getUserProfileDatas();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println(TAG + " " + "profile onactivity result...");
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            switch (requestCode) {
                case VariableConstants.PROFILE_REQUEST_CODE:
                    profilePicUrl = data.getStringExtra("profilePicUrl");
                    userName = data.getStringExtra("username");
                    fullName = data.getStringExtra("fullName");
                    bio = data.getStringExtra("bio");
                    website = data.getStringExtra("website");
                    System.out.println(TAG + " " + "profile pic=" + profilePicUrl + " " + "user name=" + userName + " " + "full name=" + fullName);

                    // Set profile pic
                    if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                        // profile pic
                        Picasso.with(mActivity)
                                .load(profilePicUrl)
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .transform(new CircleTransform())
                                .into(iV_profile_pic);

                        // set profile pic as cover pic
                        Glide.with(mActivity)
                                .load(profilePicUrl)
                                .transform(new PositionedCropTransformation((mActivity), 0, 0))
                                .placeholder(R.drawable.default_profile_cover_pic)
                                .error(R.drawable.default_profile_cover_pic)
                                .into(iv_background);
                    } else {
                        iV_profile_pic.setImageResource(R.drawable.default_profile_image);
                        iv_background.setImageResource(R.drawable.default_profile_cover_pic);
                    }

                    // Full name
                    // set username rathar than fullname
                    Log.d("username113", userName);
                    if (userName != null)
                        tV_fullName.setText(userName);

                    // set bio
                    if (bio != null && !bio.isEmpty()) {
                        tV_bio.setVisibility(View.VISIBLE);
                        tV_bio.setText(bio);
                    }

                    // set website
                    if (website != null && !website.isEmpty()) {
                        tV_website.setVisibility(View.VISIBLE);
                        tV_website.setText(website);
                    }
                    break;

                case VariableConstants.GOOGLE_LOGIN_REQ_CODE:
                    verifyLoginWithGplus.onActivityResult(data);
                    break;

                // Paypal verified link
                case VariableConstants.PAYPAL_REQ_CODE:
                    boolean isPaypalVerified = data.getBooleanExtra("isPaypalVerified", false);
                    paypalUrl = data.getStringExtra("payPalLink");
                    if (isPaypalVerified) {
                        iV_paypal_icon.setVisibility(View.VISIBLE);
                        rL_verify_paypal.setVisibility(View.GONE);
                    }
                    break;

                // following count
                case VariableConstants.FOLLOW_COUNT_REQ_CODE:
                    int intFollowCount = data.getIntExtra("followingCount", 0);
                    System.out.println(TAG + " " + "followingCount=" + intFollowCount);
                    if (intFollowCount >= 0) {
                        followingCount = String.valueOf(intFollowCount);
                        tV_following_count.setText(followingCount);
                    }
                    break;
            }
        }
    }

    // This methos is notified when any following count changes from Social Frag
    @Subscribe
    public void getMessage(ProfileFollowingCount follingCountObj) {
        if (follingCountObj != null && follingCountObj.getFollowingCount() >= 0) {
            System.out.println(TAG + " " + "followingCount=" + follingCountObj.getFollowingCount());
            followingCount = String.valueOf(follingCountObj.getFollowingCount());
            tV_following_count.setText(followingCount);
        }
    }

    // This method is notified when paypal link verified from Connect paypal screen
    @Subscribe
    public void getMessage(String getPaypalUrl) {
        System.out.println(TAG + " " + "get paypal url=" + getPaypalUrl);
        if (getPaypalUrl != null) {
            paypalUrl = getPaypalUrl;
            iV_paypal_icon.setVisibility(View.VISIBLE);
            rL_verify_paypal.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void getMessage(ProfileResultDatas getProfileResultDatas) {
        if (getProfileResultDatas != null) {
            profilePicUrl = getProfileResultDatas.getProfilePicUrl();
            userName = getProfileResultDatas.getUsername();
            fullName = getProfileResultDatas.getFullName();
            bio = getProfileResultDatas.getBio();
            website = getProfileResultDatas.getWebsite();
            System.out.println(TAG + " " + "profile pic=" + profilePicUrl + " " + "user name=" + userName + " " + "full name=" + fullName);

            // Set profile pic
            if (profilePicUrl != null && !profilePicUrl.isEmpty()) {
                // profile pic
                Picasso.with(mActivity)
                        .load(profilePicUrl)
                        .placeholder(R.drawable.default_profile_image)
                        .error(R.drawable.default_profile_image)
                        .transform(new CircleTransform())
                        .into(iV_profile_pic);

                // set profile pic as cover pic
                Glide.with(mActivity)
                        .load(profilePicUrl)
                        .transform(new PositionedCropTransformation((mActivity), 0, 0))
                        .placeholder(R.drawable.default_profile_cover_pic)
                        .error(R.drawable.default_profile_cover_pic)
                        .into(iv_background);
            } else {
                iV_profile_pic.setImageResource(R.drawable.default_profile_image);
                iv_background.setImageResource(R.drawable.default_profile_cover_pic);
            }

            // Full name
            Log.d("username114", userName);
            if (userName != null)
                tV_fullName.setText(userName);

            // set bio
            if (bio != null && !bio.isEmpty()) {
                tV_bio.setVisibility(View.VISIBLE);
                tV_bio.setText(bio);
            }

            // set website
            if (website != null && !website.isEmpty()) {
                tV_website.setVisibility(View.VISIBLE);
                tV_website.setText(website);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        isProfileFragVisible = isVisibleToUser;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
