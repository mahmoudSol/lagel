package com.lagel.com.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.lagel.com.BuildConfig;
import com.lagel.com.R;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.main.activity.LoginOrSignupActivity;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.mqttchat.Database.CouchDbController;
import com.lagel.com.pojo_class.LogDevicePojo;
import com.lagel.com.pojo_class.LoginResponsePojo;
import com.lagel.com.pojo_class.sign_up_pojo.SignUpMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.ProgressBarHandler;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>LoginWithFacebook</h>
 * <p>
 *     This class is getting called when user Click on Login with facebook from Login screen(LoginActivity).
 *     In this class we do all operation regarding facebook. First of all we retrieve user all information
 *     using facebook graph api 2.0 once we get after that we call Facebook login api and pass user facebook
 *     id. if we get code 200 that means success then move to HomePageActivity or else call signUp api.
 * </p>
 * @since 04/04/17
 * @author 3Embed
 * @version 1.0
 */
public class LoginWithFacebook
{
    private static final String TAG =LoginWithFacebook.class.getSimpleName() ;
    //facebook variables
    private CallbackManager callbackManager;
    private String Fb_emailId="",Fb_firstName="",fb_lastName="",fb_pic="",facebookId="",fullName="",fb_accessToken="", currentLat ="", currentLng ="",address="",city="",countryCode="";
    private Activity mActivity;
    private ProgressBarHandler mProgressBar;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootview;
    private ProgressBar pBar_fbLogin;
    private ImageView iV_fbicon;
    private TextView tV_facebook;
    private FusedLocationService locationService;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;

    public LoginWithFacebook(Activity mActivity, RelativeLayout rL_rootview, ProgressBar pBar_fbLogin, ImageView iV_fbicon, TextView tV_facebook) {
        this.mActivity = mActivity;
        this.rL_rootview=rL_rootview;
        this.pBar_fbLogin=pBar_fbLogin;
        this.iV_fbicon=iV_fbicon;
        this.tV_facebook=tV_facebook;
        mSessionManager=new SessionManager(mActivity);
        currentLat=mSessionManager.getCurrentLat();
        currentLng=mSessionManager.getCurrentLng();
        permissionsArray =new String[]{ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION};
        runTimePermission=new RunTimePermission(mActivity, permissionsArray,false);
        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            city = CommonClass.getCityName(mActivity, Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            countryCode=CommonClass.getCountryCode(mActivity,Double.parseDouble(currentLat),Double.parseDouble(currentLng));
            //loginRequestApi();
        }
        mProgressBar=new ProgressBarHandler(mActivity);
        FacebookSdk.sdkInitialize(mActivity);
        callbackManager = CallbackManager.Factory.create();
        initializeFacebookSdk();
    }

    /**
     * <h>LoginFacebookSdk</h>
     * <p>
     *     This method is being called from onCreate method. this method is called
     *     when user click on LoginWithFacebook button. it contains three method onSuccess,
     *     onCancel and onError. if login will be successfull then success method will be
     *     called and in that method we obtain user all details like id, email, name, profile pic
     *     etc. onCancel method will be called if user click on facebook with login button and
     *     suddenly click back button. onError method will be called if any problem occurs like
     *     internet issue.
     * </p>
     */
    private void initializeFacebookSdk()
    {
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("LoginActivity", response.toString());

                        if (response.getError() == null) {
                            facebookId = object.optString("id");
                            Fb_emailId = object.optString("email");
                            Fb_firstName = object.optString("first_name");
                            fb_lastName = object.optString("last_name");
                            fb_pic = "https://graph.facebook.com/"+facebookId+"/picture?type=large";
                            fullName=Fb_firstName+" "+fb_lastName;
                            AccessToken token = AccessToken.getCurrentAccessToken();
                            if (token!=null)
                                fb_accessToken=token.getToken();
                            System.out.println(TAG+" "+"facebookId="+facebookId+" "+"accessToken="+token.getToken());

                            if (facebookId!=null && !facebookId.isEmpty())
                            {
                                LocationManager lm = (LocationManager)mActivity.getSystemService(Context.LOCATION_SERVICE);
                                boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                System.out.println(TAG+" "+"is location enabled="+ isLocationEnabled +" "+"is permission allowed="+runTimePermission.checkPermissions(permissionsArray));

                                facebookLoginProgress(true);
                                if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                                    getCurrentLocation();
                                else
                                    loginWithFbApi();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,email,gender, birthday,first_name,last_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.Loginfailed));
            }

            @Override
            public void onError(FacebookException error) {
                CommonClass.showSnackbarMessage(rL_rootview, error.toString());
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    public void getCurrentLocation()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
                @Override
                public void onUpdateLocation() {
                    Location currentLocation = locationService.receiveLocation();
                    if (currentLocation != null) {
                        currentLat = String.valueOf(currentLocation.getLatitude());
                        currentLng = String.valueOf(currentLocation.getLongitude());

                        if (isLocationFound(currentLat, currentLng)) {
                            mSessionManager.setCurrentLat(currentLat);
                            mSessionManager.setCurrentLng(currentLng);
                            address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            countryCode = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            loginWithFbApi();
                        }
                    }
                }
            }
            );
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * In this method we used to check whether current currentLat and
     * long has been received or not.
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * <h>LoginWithFbApi</h>
     * <p>
     *     This method is called from initializeFacebookSdk() after getting user complete
     *     information like facebook_id,name,email etc from Facebook login. In this method
     *     we call login api and pass facebook id which we got from facebook api. After
     *     login response we check whether This facebook id is register or not. if not
     *     then call facebook signup api.
     * </p>
     */
    private void loginWithFbApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            //mProgressBar.show();
            JSONObject requestDatas = new JSONObject();
            try {
                // loginType, pushToken, place, city, countrySname, latitude, longitude,facebookId, email
                requestDatas.put("loginType", VariableConstants.TYPE_FACEBOOK);
                requestDatas.put("pushToken",mSessionManager.getPushToken());
                requestDatas.put("place",address);
                requestDatas.put("city",city);
                requestDatas.put("countrySname",countryCode);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                requestDatas.put("facebookId", facebookId);
                requestDatas.put("email",Fb_emailId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "facebook login res=" + result);

                    LoginResponsePojo loginResponsePojo;
                    Gson gson=new Gson();
                    loginResponsePojo=gson.fromJson(result,LoginResponsePojo.class);

                    switch (loginResponsePojo.getCode())
                    {
                        // success
                        case "200" :
                            mProgressBar.hide();
                            mSessionManager.setmqttId(loginResponsePojo.getMqttId());
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setAuthToken(loginResponsePojo.getToken());
                            mSessionManager.setUserName(loginResponsePojo.getUsername());
                            mSessionManager.setUserImage(loginResponsePojo.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponsePojo.getUserId());
                            mSessionManager.setLoginWith("facebookLogin");
                            initUserDetails(loginResponsePojo.getProfilePicUrl(),loginResponsePojo.getMqttId(),Fb_emailId,Fb_firstName+" "+ fb_lastName,loginResponsePojo.getToken());
                            logDeviceInfo(loginResponsePojo.getToken());
                            break;

                        // User not found
                        case "204" :
                            doRegistration(fb_pic);
                            facebookLoginProgress(false);
                            break;

                        // Error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,loginResponsePojo.getMessage());
                            facebookLoginProgress(false);
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgressBar.hide();

                    facebookLoginProgress(false);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    private void doRegistration(String profilePicUrl) {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            JSONObject request_Data = new JSONObject();
            try {


                String googleToken = "1234";

                request_Data.put("phoneNumber", ""); // mandatory
                request_Data.put("email", Fb_emailId); // mandatory
                request_Data.put("username", this.random()); // mandatory
                request_Data.put("signupType", VariableConstants.TYPE_MANUAL);   // mandatory

                request_Data.put("deviceType", VariableConstants.DEVICE_TYPE); // mandatory
                request_Data.put("pushToken", mSessionManager.getPushToken()); // mandatory
                request_Data.put("deviceId", mSessionManager.getDeviceId()); // mandatory
                request_Data.put("profilePicUrl", profilePicUrl);
                request_Data.put("fullName", fullName);
                request_Data.put("location", address);
                request_Data.put("city", city);
                request_Data.put("countrySname", countryCode);
                request_Data.put("latitude", currentLat);
                request_Data.put("longitude", currentLng);
                request_Data.put("password", facebookId); // mandatory


                request_Data.put("googleToken", googleToken); // mandatory when sign up with google
                request_Data.put("googleId", "");   // mandatory when sign up with google
                request_Data.put("facebookId", facebookId);
                request_Data.put("accessToken", fb_accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.SIGN_UP, OkHttp3Connection.Request_type.POST, request_Data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String tag) {
                    Toast.makeText(mActivity, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    System.out.println(TAG + " " + "sign up response=" + result);
                    Log.e("Registartion RESPONSE", result);
                    /*
                     * handel response.*/
                    SignUpMainPojo signUpMainPojo;
                    Gson gson = new Gson();
                    signUpMainPojo = gson.fromJson(result, SignUpMainPojo.class);

                    switch (signUpMainPojo.getCode()) {

                        // success
                        case "200":
                            mSessionManager.setmqttId(signUpMainPojo.getResponse().getMqttId());
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setAuthToken(signUpMainPojo.getResponse().getAuthToken());
                            mSessionManager.setUserName(signUpMainPojo.getResponse().getUsername());
                            mSessionManager.setUserImage(fb_pic);
                            mSessionManager.setUserId(signUpMainPojo.getResponse().getUserId());
                            VariableConstants.IS_TO_SHOW_START_BROWSING = true;
                            initUserDetails(fb_pic, signUpMainPojo.getResponse().getMqttId(), Fb_emailId, signUpMainPojo.getResponse().getUsername(), signUpMainPojo.getResponse().getAuthToken());
                            // call this method to set device info to server
                            logDeviceInfo(signUpMainPojo.getResponse().getAuthToken());
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;
                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,signUpMainPojo.getMessage());
                            facebookLoginProgress(false);
                    }
                }

                @Override
                public void onError(String error, String tag) {
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                    facebookLoginProgress(false);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));

    }
    /**
     * Getting random number
     *
     * @return
     */

    public String random() {

        char[] chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9abcdefghijklmnopqrstuvwxyzlagel".toCharArray();
        StringBuilder sb1 = new StringBuilder();
        Random random1 = new Random();
        for (int i = 0; i < 8; i++) {
            char c1 = chars1[random1.nextInt(chars1.length)];
            sb1.append(c1);
        }
        String randomString = sb1.toString();

       /* Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt();
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }*/
        return randomString;
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     *     In this method we used to do api call to send device information like device name
     *     model number, device id etc to server to log the the user with specific device.
     * </p>
     * @param token The auth token for particular user
     */
    private void logDeviceInfo(String token)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
                request_datas.put("token",token);
                request_datas.put("deviceType",VariableConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_DEVICE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    facebookLoginProgress(false);
                    System.out.println(TAG+" "+"log device info="+result);

                    LogDevicePojo logDevicePojo;
                    Gson gson=new Gson();
                    logDevicePojo=gson.fromJson(result,LogDevicePojo.class);

                    switch (logDevicePojo.getCode())
                    {
                        // success
                        case "200" :
                            // Open Home page screen
                            facebookLoginProgress(false);
                            //mActivity.finish();

                            Intent intent = new Intent();
                            intent.putExtra("isToRefreshHomePage",true);
                            mActivity.setResult(VariableConstants.LANDING_REQ_CODE,intent);
                            mActivity.finish();
                            break;

                        // error
                        default:

                            CommonClass.showSnackbarMessage(rL_rootview,logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    facebookLoginProgress(false);
                    CommonClass.showSnackbarMessage(rL_rootview,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    public void fbOnActivityResult(int requestCode, int resultCode, Intent data)
    {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void loginWithFbWithSdk()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
            LoginManager.getInstance().logInWithReadPermissions(mActivity, Arrays.asList("public_profile", "user_friends","email"));
        else
            CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
    }

    private void facebookLoginProgress(boolean isVisible)
    {
        if (isVisible)
        {
            pBar_fbLogin.setVisibility(View.VISIBLE);
            iV_fbicon.setVisibility(View.GONE);
            tV_facebook.setVisibility(View.GONE);
        }
        else {
            pBar_fbLogin.setVisibility(View.GONE);
            iV_fbicon.setVisibility(View.VISIBLE);
            tV_facebook.setVisibility(View.VISIBLE);
        }
    }

    /*
    * Initialization of the user details .*/
    private void initUserDetails(String profile_Url,String userId,String email,String userName,String token)
    {
        CouchDbController db =AppController.getInstance().getDbController();
        Map<String, Object> map = new HashMap<>();
        if (profile_Url!=null&&!profile_Url.isEmpty())
        {
            map.put("userImageUrl",profile_Url);
        } else {
            map.put("userImageUrl","");
        }
        map.put("userIdentifier", email);
        map.put("userId",userId);
        map.put("userName",userName);
        map.put("apiToken",token);
        if(userId==null||userId.isEmpty())
        {
            Toast.makeText(mActivity,R.string.userIdtext,Toast.LENGTH_SHORT).show();
            return;
        }

        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(),userId))
        {
            String userDocId = db.createUserInformationDocument(map);
            db.addToIndexDocument(AppController.getInstance().getIndexDocId(),userId, userDocId);
        } else
        {
            db.updateUserDetails(db.getUserDocId(userId, AppController.getInstance().getIndexDocId()), map);
        }
        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(),userId);
        AppController.getInstance().setSignedIn(true,userId,userName, email);
        AppController.getInstance().setSignStatusChanged(true);
    }
}
