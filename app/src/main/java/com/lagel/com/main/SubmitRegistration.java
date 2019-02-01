package com.lagel.com.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lagel.com.BuildConfig;
import com.lagel.com.R;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.mqttchat.Database.CouchDbController;
import com.lagel.com.pojo_class.LogDevicePojo;
import com.lagel.com.pojo_class.sign_up_pojo.SignUpMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>SubmitRegistration</h>
 * <p>
 * In this class we used to do registration for the new user.
 * </p>
 *
 * @since 08-Nov-17
 */
public class SubmitRegistration {
    private static final String TAG = SubmitRegistration.class.getSimpleName();
    private Activity mActivity;
    private String type = "", signupType = "", username = "", profilePicUrl = "", fullName = "", password = "", phoneNumber = "",
            email = "", googleToken = "", googleId = "", facebookId = "", accessToken = "", currentLat = "", currentLng = "", address = "", city = "", countrySname = "";
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private FusedLocationService locationService;
    private SessionManager mSessionManager;
    private ProgressBar pBar_submit;
    private TextView tV_sumit;


    private View rL_rootElement;
    private int typeRegister;

    public SubmitRegistration(Activity mActivity, ProgressBar pBar_submit, TextView tV_sumit, View rL_rootElement, String type, String signupType, String username, String profilePicUrl, String fullName, String password, String phoneNumber, String email, String googleToken, String googleId, String facebookId, String accessToken
            ,int typeRegister) {
        this.type = type;
        this.mActivity = mActivity;
        this.signupType = signupType;
        this.username = username;
        this.profilePicUrl = profilePicUrl;
        this.fullName = fullName;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.googleToken = googleToken;
        this.googleId = googleId;
        this.facebookId = facebookId;
        this.accessToken = accessToken;
        this.pBar_submit = pBar_submit;
        this.tV_sumit = tV_sumit;
        this.rL_rootElement = rL_rootElement;
        this.typeRegister=typeRegister;
        mSessionManager = new SessionManager(mActivity);
        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
    }

    public void submitRegistration() {
        registerFromEmailService();
        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
        boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
            getCurrentLocation();
       /* else
            registerFromEmailService();*/
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
                    currentLat = String.valueOf(currentLocation.getLatitude());
                    currentLng = String.valueOf(currentLocation.getLongitude());

                    if (isLocationFound(currentLat, currentLng)) {
                        address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        countrySname = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        //for make an address use countryname
                        String countryName = CommonClass.getCountryName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        if (address.isEmpty() || address == null) {
                            address = city + ", " + countryName;
                        }
                        // registerFromEmailService();
                    }
                }
            }
        }
        );
    }

    /**
     * In this method we used to check whether current currentLat and
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
     * <h>RegisterFromEmailService</h>
     * <p>
     * In this method we do api call for user registration.
     * </p>
     */
    private void registerFromEmailService() {
        final ProgressDialog dialog = new ProgressDialog(mActivity);
        dialog.setMessage("Registering...");
        dialog.show();
        if (CommonClass.isNetworkAvailable(mActivity)) {


            JSONObject request_Data = new JSONObject();
            try {

                if (typeRegister==1)
                {
                    request_Data.put("phoneNumber", email); // mandatory
                    request_Data.put("email", email+"@mail.com"); // mandatory
                    request_Data.put("username", username); // mandatory

                }
                else
                {
                    request_Data.put("phoneNumber", phoneNumber); // mandatory
                    request_Data.put("email", email); // mandatory
                    request_Data.put("username", username); // mandatory
                }

                googleToken = "1234";
                request_Data.put("signupType", signupType);   // mandatory

                request_Data.put("deviceType", VariableConstants.DEVICE_TYPE); // mandatory
                request_Data.put("pushToken", mSessionManager.getPushToken()); // mandatory
                request_Data.put("deviceId", mSessionManager.getDeviceId()); // mandatory
                request_Data.put("profilePicUrl", profilePicUrl);
                request_Data.put("fullName", fullName);
                request_Data.put("location", address);
                request_Data.put("city", city);
                request_Data.put("countrySname", countrySname);
                request_Data.put("latitude", currentLat);
                request_Data.put("longitude", currentLng);
                request_Data.put("password", password); // mandatory


                request_Data.put("googleToken", googleToken); // mandatory when sign up with google
                request_Data.put("googleId", googleId);   // mandatory when sign up with google
                request_Data.put("facebookId", facebookId);
                request_Data.put("accessToken", accessToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.SIGN_UP, OkHttp3Connection.Request_type.POST, request_Data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String tag) {
                    dialog.dismiss();
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
                            mSessionManager.setUserImage(profilePicUrl);
                            mSessionManager.setUserId(signUpMainPojo.getResponse().getUserId());
                            VariableConstants.IS_TO_SHOW_START_BROWSING = true;
                            initUserDetails(profilePicUrl, signUpMainPojo.getResponse().getMqttId(), email, signUpMainPojo.getResponse().getUsername(), signUpMainPojo.getResponse().getAuthToken());
                            // call this method to set device info to server
                            logDeviceInfo(signUpMainPojo.getResponse().getAuthToken());
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;
                        // error
                        default:
                            pBar_submit.setVisibility(View.GONE);
                            tV_sumit.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement, signUpMainPojo.getMessage());
                    }
                }

                @Override
                public void onError(String error, String tag) {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else {
            pBar_submit.setVisibility(View.GONE);
            tV_sumit.setVisibility(View.VISIBLE);
            CommonClass.showTopSnackBar(rL_rootElement, mActivity.getResources().getString(R.string.NoInternetAccess));
        }
    }

    /**
     * <h>LogDeviceInfo</h>
     * <p>
     * In this method we used to do api call to send device information like device name
     * model number, device id etc to server to log the the user with specific device.
     * </p>
     *
     * @param token The auth token for particular user
     */
    private void logDeviceInfo(String token) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            //deviceName, deviceId, deviceOs, modelNumber, appVersion
            final JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceName", Build.BRAND);
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("deviceOs", Build.VERSION.RELEASE);
                request_datas.put("modelNumber", Build.MODEL);
                request_datas.put("appVersion", BuildConfig.VERSION_NAME);
                request_datas.put("token", token);
                request_datas.put("deviceType", VariableConstants.DEVICE_TYPE);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOG_DEVICE, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "log device info=" + result);

                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);

                    LogDevicePojo logDevicePojo;
                    Gson gson = new Gson();
                    logDevicePojo = gson.fromJson(result, LogDevicePojo.class);

                    switch (logDevicePojo.getCode()) {
                        // success
                        case "200":
                            // set login with type
                            switch (type) {
                                // google sign up
                                case "googleSignUp":
                                    mSessionManager.setLoginWith("googleLogin");
                                    break;

                                // normal login
                                case "normalSignup":
                                    mSessionManager.setLoginWith("normalLogin");
                                    break;

                                case "fbSignUp":
                                    mSessionManager.setLoginWith("facebookLogin");
                                    break;
                            }

                            Intent intent = new Intent();
                            intent.putExtra("isToFinishLandingScreen", true);
                            intent.putExtra("isFromSignup", true);
                            mActivity.setResult(VariableConstants.LOGIN_SIGNUP_REQ_CODE, intent);
                            mActivity.finish();
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement, logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_submit.setVisibility(View.GONE);
                    tV_sumit.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showSnackbarMessage(rL_rootElement, mActivity.getResources().getString(R.string.NoInternetAccess));
    }


    /**
     * Initialization of the user details .
     */
    private void initUserDetails(String profile_Url, String userId, String email, String userName, String token) {
        CouchDbController db = AppController.getInstance().getDbController();
        Map<String, Object> map = new HashMap<>();
        if (profile_Url != null && !profile_Url.isEmpty()) {
            map.put("userImageUrl", profile_Url);
        } else {
            map.put("userImageUrl", "");
        }
        map.put("userIdentifier", email);
        map.put("userId", userId);
        map.put("userName", userName);
        map.put("apiToken", token);
        if (!db.checkUserDocExists(AppController.getInstance().getIndexDocId(), userId)) {
            String userDocId = db.createUserInformationDocument(map);
            db.addToIndexDocument(AppController.getInstance().getIndexDocId(), userId, userDocId);
        } else {
            db.updateUserDetails(db.getUserDocId(userId, AppController.getInstance().getIndexDocId()), map);
        }
        db.updateIndexDocumentOnSignIn(AppController.getInstance().getIndexDocId(), userId);
        AppController.getInstance().setSignedIn(true, userId, userName, email);
        AppController.getInstance().setSignStatusChanged(true);
    }
}
