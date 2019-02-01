package com.lagel.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lagel.com.BuildConfig;
import com.lagel.com.R;
import com.lagel.com.bean.UserBean;
import com.lagel.com.county_code_picker.Country;
import com.lagel.com.county_code_picker.DialogCountryList;
import com.lagel.com.county_code_picker.SetCountryCodeListener;
import com.lagel.com.database.CategoryData;
import com.lagel.com.database.CountryData;
import com.lagel.com.device_camera.HandleCameraEvents;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.main.LoginWithFacebook;
import com.lagel.com.main.SubmitRegistration;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.mqttchat.Database.CouchDbController;
import com.lagel.com.pojo_class.CloudData;
import com.lagel.com.pojo_class.EmailCheckPojo;
import com.lagel.com.pojo_class.LogDevicePojo;
import com.lagel.com.pojo_class.LoginResponsePojo;
import com.lagel.com.pojo_class.PhoneNoCheckPojo;
import com.lagel.com.pojo_class.UserNameCheckPojo;
import com.lagel.com.pojo_class.cloudinary_details_pojo.Cloudinary_Details_reponse;
import com.lagel.com.pojo_class.phone_otp_pojo.PhoneOtpMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CircleTransform;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.UploadToCloudinary;
import com.lagel.com.utility.VariableConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import co.simplecrop.android.simplecropimage.CropImage;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.lagel.com.utility.VariableConstants.TEMP_PHOTO_FILE_NAME;

/**
 * <h>LoginOrSignupActivity</h>
 * <p>
 * This activity class has been called from LandingActivity class.
 * In this class two button(Login and signup) is there on the top
 * of the screen.
 * </p>
 *
 * @since 15-May-17
 */
public class LoginOrSignupActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = LoginOrSignupActivity.class.getSimpleName();
    private Activity mActivity;
    private View login_views, signup_views,v_1_phone,v_1_email,v_1_phone1,v_1_email1;
    private RunTimePermission runTimePermission;
    private String[] permissionsArray;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private boolean isPictureTaken, isFromLocation, isAskLocationPermission;
    private FusedLocationService locationService;
    private String signUpType = "", googleToken = "", googleId = "", fbId = "", type = "", google_userImageUrl = "", fb_userImageUrl = "",
            fb_accessToken = "", currentLat = "", currentLng = "", address = "", otpCode = "", city = "", countryShortName = "";
    private boolean isUserRegistered, isPhoneRegistered, isEmailRegistered, isToStartActivity;

    // Login xml variables
    private EditText eT_loginUserName, eT_loginPassword,eT_loginUserName1,eT_firstName,eT_lastName;
    private RelativeLayout rL_do_login;
    private RelativeLayout rL_signup;
    private ImageView iV_login_userName_error, iV_login_password_error;

    // signup xml variables
    private EditText eT_userName, eT_fullName, eT_password, eT_mobileNo, eT_emailId, eT_password1, eT_password2;
    private boolean isLoginButtonEnabled, isSignUpButtonEnabled;
    private ProgressBar progress_bar_login, progress_bar_signup, progress_bar_ph, pBar_userName, pBar_email;
    private TextView tV_do_login, tV_signup, tV_by_signing_up,v_cod_country,v_cod_country1;
    private ImageView iV_profile_pic, iV_userName_error, iV_password_error, iV_error_ph, iV_error_email;
    private ArrayList<Country> arrayListCountry;
    private DialogCountryList dialogCountryList;
    private TextView tV_country_iso_no, tV_country_code;
    private ImageView iV_full_name, iV_user_name, iV_password, iV_phone_icon, iV_email, iv_edit_icon;
    private CheckBox checkboxSignUp;
    private String countryIsoNumber = "";
    private File mFile;
    private HandleCameraEvents mHandleCameraEvents;
    private TextView tV_signup_new;
    private ImageView iV_back,iV_back_icon1;
    private RelativeLayout rl_fb_login, rL_google_login,rL1_phone,rL1_email,rL_next,rL_next2,rL_next3,rL_next4,rL1_email1,rL1_phone1;
    private RelativeLayout rl_1,rl_2,rl_3,rl_4;
    private boolean isFromGplusLocation = false;
    private LoginWithFacebook loginWithFacebook;
    private ImageView iV_fbicon;
    private ProgressBar pBar_fbLogin;
    private TextView tV_facebook;
    private ProgressDialog progressDialog;
    //Google
    private GoogleApiClient mGoogleApiClient;
    private String countryCode = "", fullName = "", email = "", id = "", serverAuthCode = "", personPhotoUrl = "",sphone="";
    private ImageView iV_Gicon;
    private ProgressBar pBar_GLogin;
    private TextView tV_Google;
    private String randomUserName = "";
    private RelativeLayout rL_forgot_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_signup);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        selectPhone=true;

        mActivity = LoginOrSignupActivity.this;
        isToStartActivity = true;
        mSessionManager = new SessionManager(mActivity);

        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);

        fetchLocation();

        // location access permission.
        if (!runTimePermission.checkPermissions(permissionsArray)) {
            isAskLocationPermission = true;
            runTimePermission.requestPermission();
        }

        currentLat = mSessionManager.getCurrentLat();
        currentLng = mSessionManager.getCurrentLng();
        if (isLocationFound(currentLat, currentLng)) {
            address = CommonClass.getCompleteAddressString(mActivity, Double.parseDouble(currentLat), Double.parseDouble(currentLng));
            city = CommonClass.getCityName(mActivity, Double.parseDouble(currentLat), Double.parseDouble(currentLng));
            countryShortName = CommonClass.getCountryCode(mActivity, Double.parseDouble(currentLat), Double.parseDouble(currentLng));
        }


        // hide status bar
        CommonClass.statusBarColor(mActivity);
        initVariables();
        CommonClass.setViewOpacity(mActivity, rL_do_login, 204, R.drawable.oval_purple_color_with_solid_shape);

        rl_1.setVisibility(View.VISIBLE);
        rl_2.setVisibility(View.GONE);
        rl_3.setVisibility(View.GONE);
        rl_4.setVisibility(View.GONE);
    }

    private void initVariables() {
        isPictureTaken = false;
        arrayListCountry = new ArrayList<>();

        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);
        login_views = findViewById(R.id.login_views);
        signup_views = findViewById(R.id.signup_views);
        v_1_phone= (View)findViewById(R.id.v_1_phone);
        v_1_email= (View)findViewById(R.id.v_1_email);

        v_1_phone1= (View)signup_views.findViewById(R.id.v_1_phone1);
        v_1_email1= (View)signup_views.findViewById(R.id.v_1_email1);


        rL1_phone= (RelativeLayout)findViewById(R.id.rL1_phone);
        rL1_phone.setOnClickListener(this);
        v_cod_country = (TextView) findViewById(R.id.v_cod_country );
        v_cod_country1 = (TextView) signup_views.findViewById(R.id.v_cod_country1 );

        rL1_email= (RelativeLayout)findViewById(R.id.rL1_email);
        rL1_email.setOnClickListener(this);
        v_cod_country.setOnClickListener(this);
        v_cod_country1.setOnClickListener(this);

        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        RadioButton radio_login, radio_signup;
        radio_login = (RadioButton) findViewById(R.id.radio_login);
        radio_signup = (RadioButton) findViewById(R.id.radio_signup);

        // receiving flag value from last class
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        System.out.println(TAG + " " + "type=" + type);

        // initialize sigup xml variables
        eT_userName = (EditText) signup_views.findViewById(R.id.eT_userName);
        eT_fullName = (EditText) signup_views.findViewById(R.id.eT_fullName);
        eT_fullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        eT_emailId = (EditText) signup_views.findViewById(R.id.eT_emailId);
        iV_profile_pic = (ImageView) signup_views.findViewById(R.id.iV_profile_pic);
        iV_profile_pic.getLayoutParams().width = CommonClass.getDeviceWidth(mActivity) / 4;
        iV_profile_pic.getLayoutParams().height = CommonClass.getDeviceWidth(mActivity) / 4;
        iV_profile_pic.setOnClickListener(this);
        progress_bar_ph = (ProgressBar) signup_views.findViewById(R.id.pBar_ph);
        progress_bar_ph.setVisibility(View.GONE);
        pBar_userName = (ProgressBar) findViewById(R.id.pBar_userName);
        pBar_userName.setVisibility(View.GONE);
        pBar_email = (ProgressBar) findViewById(R.id.pBar_email);
        pBar_email.setVisibility(View.GONE);

        rL1_email1= (RelativeLayout)signup_views.findViewById(R.id.rL1_email1);
        rL1_email1.setOnClickListener(this);

        rL1_phone1= (RelativeLayout)signup_views.findViewById(R.id.rL1_phone1);
        rL1_phone1.setOnClickListener(this);


        eT_loginUserName1 = (EditText) signup_views.findViewById(R.id.eT_loginUserName1);
        eT_loginUserName1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // check mobile number to verify email address
                if (CommonClass.isValidEmail(eT_loginUserName1.getText().toString()))
                    emailCheckApi();


                if (selectPhone){
                    if (eT_loginUserName1!=null && eT_loginUserName1.getText().toString().length()>6)
                    {
                        if (sphone!=null && sphone.length()==0)
                        {
                            String xphone="";
                            String[] separated =v_cod_country1.getText().toString() .split("\\+");
                            if (separated !=null && separated .length>0)
                            {
                                xphone="+"+separated[1]+""+eT_loginUserName1.getText().toString();
                            }

                            phoneNumberCheckApi(xphone);
                        }
                        else {
                            String xphone="";
                            xphone=sphone+eT_loginUserName1.getText().toString();
                            phoneNumberCheckApi(xphone);
                        }
                    }


                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        eT_firstName= (EditText) signup_views.findViewById(R.id.eT_firstName);
        eT_lastName= (EditText) signup_views.findViewById(R.id.eT_lastName);

        // sign up icons
        iV_full_name = (ImageView) signup_views.findViewById(R.id.iV_full_name);
        iV_user_name = (ImageView) signup_views.findViewById(R.id.iV_user_name);
        iV_password = (ImageView) signup_views.findViewById(R.id.iV_password);
        iV_phone_icon = (ImageView) signup_views.findViewById(R.id.iV_phone_icon);
        iV_email = (ImageView) signup_views.findViewById(R.id.iV_email);
        iv_edit_icon = (ImageView) signup_views.findViewById(R.id.iv_edit_icon);
        iv_edit_icon.setVisibility(View.GONE);

        // sign up error icon
        iV_userName_error = (ImageView) signup_views.findViewById(R.id.iV_userName_error);
        iV_password_error = (ImageView) signup_views.findViewById(R.id.iV_password_error);
        iV_error_ph = (ImageView) signup_views.findViewById(R.id.iV_error_ph);
        iV_error_email = (ImageView) signup_views.findViewById(R.id.iV_error_email);


        findViewById(R.id.tV_click_here).setOnClickListener(this);


        // Here we check the type if it is login type then show login views else sign up
        switch (type) {
            case "login":
                radio_login.setChecked(true);
                radio_signup.setChecked(false);
                login_views.setVisibility(View.VISIBLE);
                signup_views.setVisibility(View.GONE);
                break;

            // normal signup
            case "normalSignup":
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);
                break;

            // google sign up
            case "googleSignUp":
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);

                // Google login
                String userFullName, email;
                userFullName = intent.getStringExtra("userFullName");
                google_userImageUrl = intent.getStringExtra("userImageUrl");
                email = intent.getStringExtra("email");
                googleId = intent.getStringExtra("id");
                googleToken = intent.getStringExtra("serverAuthCode");


                // set profile pic
                setSignUpXmlVar(google_userImageUrl, userFullName, email);

                signUpType = "3";
                break;

            case "fbSignUp":
                radio_login.setChecked(false);
                radio_signup.setChecked(true);
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);

                // facebook login
                String fbUserFullName, fbEmail, fbUserName;
                fbUserFullName = intent.getStringExtra("userFullName");
                fb_userImageUrl = intent.getStringExtra("userImageUrl");
                fbEmail = intent.getStringExtra("email");
                fbId = intent.getStringExtra("id");
                fbUserName = intent.getStringExtra("userName");
                fb_accessToken = intent.getStringExtra("fb_accessToken");

                // set values
                setSignUpXmlVar(fb_userImageUrl, fbUserFullName, fbEmail);

                signUpType = "1";

                Log.e(TAG, "Facebook Name: " + fbUserFullName + ", email: " + fbEmail
                        + ", Image: " + fb_userImageUrl + ", fb id: " + fbId + ", user name: " + fbUserName);

                break;
        }

        // Here we set login or sign up views visibility
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    // open login view
                    case R.id.radio_login:
                        login_views.setVisibility(View.VISIBLE);
                        signup_views.setVisibility(View.GONE);
                        break;

                    // open sign up view
                    case R.id.radio_signup:
                        if (type.equals("login"))
                            type = "normalSignup";
                        login_views.setVisibility(View.GONE);
                        signup_views.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        // Back button
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // Login button
        iV_login_userName_error = (ImageView) login_views.findViewById(R.id.iV_login_userName_error);
        iV_login_password_error = (ImageView) login_views.findViewById(R.id.iV_login_password_error);
        rL_do_login = (RelativeLayout) login_views.findViewById(R.id.rL_do_login);
        rL_do_login.setOnClickListener(this);
        tV_signup_new = (TextView) login_views.findViewById(R.id.tV_signup_new);
        tV_signup_new.setOnClickListener(this);
        iV_back = (ImageView) login_views.findViewById(R.id.iV_back_icon);
        iV_back.setOnClickListener(this);


        rl_fb_login = (RelativeLayout) login_views.findViewById(R.id.rL_fb_login);
        rl_fb_login.setOnClickListener(this);
        pBar_fbLogin = (ProgressBar) login_views.findViewById(R.id.pBar_fbLogin);
        iV_fbicon = (ImageView) login_views.findViewById(R.id.iV_fbicon);
        tV_facebook = (TextView) login_views.findViewById(R.id.tV_facebook);

        pBar_GLogin = (ProgressBar) login_views.findViewById(R.id.pBar_googleLogin);
        iV_Gicon = (ImageView) login_views.findViewById(R.id.iV_google_icon);
        tV_Google = (TextView) login_views.findViewById(R.id.tV_google);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        loginWithFacebook = new LoginWithFacebook(mActivity, rL_rootElement, pBar_fbLogin, iV_fbicon, tV_facebook);
        rL_google_login = (RelativeLayout) login_views.findViewById(R.id.rL_google_login);
        rL_google_login.setOnClickListener(this);


        tV_signup_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);
            }
        });

        iV_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // login progress bar
        progress_bar_login = (ProgressBar) login_views.findViewById(R.id.progress_bar_login);
        tV_do_login = (TextView) login_views.findViewById(R.id.tV_do_login);

        // Forgot password
        rL_forgot_password = (RelativeLayout) findViewById(R.id.rL_forgot_password);
        rL_forgot_password.setOnClickListener(this);

        loginButtonValidation();

        //////////////// sign up ////////////////////
        tV_country_iso_no = (TextView) signup_views.findViewById(R.id.tV_country_iso_no);
        tV_country_code = (TextView) signup_views.findViewById(R.id.tV_country_code);
        progress_bar_signup = (ProgressBar) signup_views.findViewById(R.id.progress_bar_signup);
        progress_bar_signup.setVisibility(View.GONE);
        rL_signup = (RelativeLayout) signup_views.findViewById(R.id.rL_signup);
        tV_signup = (TextView) signup_views.findViewById(R.id.tV_signup);
        tV_by_signing_up = (TextView) signup_views.findViewById(R.id.tV_by_signing_up);
        checkboxSignUp = (CheckBox) signup_views.findViewById(R.id.checkboxSignUp);

        iV_back_icon1 = (ImageView) signup_views.findViewById(R.id.iV_back_icon1);
        iV_back_icon1.setOnClickListener(this);



        rL_next= (RelativeLayout) signup_views.findViewById(R.id.rL_next);
        rL_next.setOnClickListener(this);

        rL_next2= (RelativeLayout) signup_views.findViewById(R.id.rL_next2);
        rL_next2.setOnClickListener(this);

        rL_next3= (RelativeLayout) signup_views.findViewById(R.id.rL_next3);
        rL_next3.setOnClickListener(this);

       // rL_next4= (RelativeLayout) signup_views.findViewById(R.id.rL_next4);
       // rL_next4.setOnClickListener(this);


        rl_1= (RelativeLayout) signup_views.findViewById(R.id.rl_1);
        rl_1.setOnClickListener(this);

        rl_2= (RelativeLayout) signup_views.findViewById(R.id.rl_2);
        rl_2.setOnClickListener(this);

        rl_3= (RelativeLayout) signup_views.findViewById(R.id.rl_3);
        rl_3.setOnClickListener(this);

        rl_4= (RelativeLayout) signup_views.findViewById(R.id.rl_4);
        rl_4.setOnClickListener(this);

        RelativeLayout rL_createAcc = (RelativeLayout) signup_views.findViewById(R.id.rL_createAcc);
        rL_createAcc.setOnClickListener(this);

        RelativeLayout rL_signup_phone = (RelativeLayout) signup_views.findViewById(R.id.rL_signup_phone);
        rL_signup_phone.setOnClickListener(this);



        signUpButtonValidation();

        // call method to get all country iso code
        //ArrayList<Country> data=getCountryCodeList();


        RelativeLayout rL_country_picker = (RelativeLayout) signup_views.findViewById(R.id.rL_country_picker);
        rL_country_picker.setOnClickListener(this);

        setTermsNconditions();

        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFile = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFile = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        mHandleCameraEvents = new HandleCameraEvents(mActivity, mFile);


    }

    @Override
    protected void onResume() {
        super.onResume();
        isToStartActivity = true;
    }

    /**
     * <h>SetSignUpXmlVar</h>
     * <p>
     * In this method we used to set the xml datas which we get
     * from the last activity like LandingACtivity class.
     * </p>
     *
     * @param imageUrl the profile image url
     * @param fullName The user full name
     * @param emailId  The user eamil-Id
     */
    private void setSignUpXmlVar(String imageUrl, String fullName, String emailId) {
        // set profile pic
        if (imageUrl != null && !imageUrl.isEmpty()) {
            System.out.println(TAG + " " + "fb_userImageUrl=" + fb_userImageUrl);
            Picasso.with(mActivity)
                    .load(imageUrl)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.add_photo)
                    .error(R.drawable.add_photo)
                    .into(iV_profile_pic);
        }

        // set full name
        if (fullName != null && !fullName.isEmpty()) {
            iV_full_name.setImageResource(R.drawable.name_on);
            eT_fullName.setText(fullName);
            eT_userName.requestFocus();
        }

        // email
        if (emailId != null && !emailId.isEmpty()) {
            iV_email.setImageResource(R.drawable.email_on);
            eT_emailId.setText(emailId);
            // email address validation api
            emailCheckApi();
        }
    }

    /**
     * <h>SetTermsNconditions</h>
     * <p>
     * In this method we used to set the terms and condition & privacy policy text at
     * bottom of the screen along with checkbox.
     * </p>
     */
    private void setTermsNconditions() {
        String terms, privacy;
        String s1 = getResources().getString(R.string.by_signing_up);
        String s2 = getResources().getString(R.string.termscondition);
        String s3 = getResources().getString(R.string.and);
        String s4 = getResources().getString(R.string.privacyPolicy);
        terms = getResources().getString(R.string.termsNconditionsUrl);
        privacy = getResources().getString(R.string.privacyPolicyUrl);

        String message = s1 + " " + s2 + " " + s3 + " " + s4;
        SpannableString spannableString = new SpannableString(message);
        spannableString.setSpan(new MyClickableSpan(true, terms), s1.length() + 1, s1.length() + s2.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new MyClickableSpan(true, privacy), s1.length() + 1 + s2.length() + 1 + s3.length() + 1, s1.length() + 1 + s2.length() + 1 + s3.length() + 1 + s4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_by_signing_up.setText(spannableString);
        tV_by_signing_up.setMovementMethod(LinkMovementMethod.getInstance());
        tV_by_signing_up.setHighlightColor(Color.BLACK);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("Lalita", "onConnectionFailed:" + connectionResult);

    }

    /**
     * <h>MyClickableSpan</h>
     * <p>
     * In this method we used to set the text clickable part by part seperately
     * like "Terms and Conditions" seperate and "Privacy Policy" seperate.
     * </p>
     */
    private class MyClickableSpan extends ClickableSpan {
        private boolean isUnderLine;
        private String setUrl;

        MyClickableSpan(boolean isUnderLine, String setUrl) {
            this.isUnderLine = isUnderLine;
            this.setUrl = setUrl;
        }

        @Override
        public void onClick(View widget) {
            Intent termsNconIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(setUrl));
            startActivity(termsNconIntent);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(isUnderLine);
            ds.setColor(ContextCompat.getColor(mActivity, R.color.landing_page_black));
            ds.setFakeBoldText(false);
        }
    }

    /**
     * <h>loginButtonValidation</h>
     * <p>
     * In this method we used to show login button more visible and clickable
     * when all the mandatory fields are filled.
     * </p>
     */
    private void loginButtonValidation() {
        // set Opacity to login button
        CommonClass.setViewOpacity(mActivity, rL_do_login, 102, R.drawable.oval_purple_color_with_solid_shape);


        eT_loginUserName1 = (EditText)signup_views.findViewById(R.id.eT_loginUserName1);
        eT_loginUserName1.setInputType(InputType.TYPE_CLASS_NUMBER);

        // user name validation
        eT_loginUserName = (EditText) login_views.findViewById(R.id.eT_loginUserName);
        eT_loginUserName.setInputType(InputType.TYPE_CLASS_NUMBER);
        eT_loginUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isLoginParamFilled()) {
                    isLoginButtonEnabled = true;
                    // set Opacity to login button
                    //CommonClass.setViewOpacity(mActivity, rL_do_login, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                    isLoginButtonEnabled = false;
                    // set Opacity to login button
                    //CommonClass.setViewOpacity(mActivity, rL_do_login, 100, R.drawable.oval_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Password
        eT_loginPassword = (EditText) findViewById(R.id.eT_loginPassword);
        eT_loginPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isLoginParamFilled()) {
                   // isLoginButtonEnabled = true;
                   // CommonClass.setViewOpacity(mActivity, rL_do_login, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                   // isLoginButtonEnabled = false;
                   // CommonClass.setViewOpacity(mActivity, rL_do_login, 100, R.drawable.oval_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Login Edit text next click event
        eT_loginUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (eT_loginUserName.getText().toString().isEmpty()) {
                        iV_login_userName_error.setVisibility(View.VISIBLE);
                        iV_login_userName_error.setImageResource(R.drawable.error_icon);
                        return true;
                    } else {
                        iV_login_userName_error.setVisibility(View.VISIBLE);
                        iV_login_userName_error.setImageResource(R.drawable.rightusername);
                        return false;
                    }
                }
                return false;
            }
        });


        // password
        eT_loginPassword.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (eT_loginPassword.getText().toString().isEmpty()) {
                        iV_login_password_error.setVisibility(View.VISIBLE);
                        iV_login_password_error.setImageResource(R.drawable.error_icon);
                    } else {
                        iV_login_password_error.setVisibility(View.VISIBLE);
                        iV_login_password_error.setImageResource(R.drawable.rightusername);
                    }

                    // if all the mandatory are filled then do login api call
                    if (isLoginButtonEnabled) {
                        showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);

                        isFromLocation = true;
                        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
                        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);

                        LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                        boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        System.out.println(TAG + " " + "is location enabled=" + isLocationEnabled + " " + "is permission allowed=" + runTimePermission.checkPermissions(permissionsArray));

                        //     progress_bar_login.setVisibility(View.VISIBLE);
                        //    tV_do_login.setVisibility(View.GONE);
                      /*  if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                            getCurrentLocation();
                        else
                            loginRequestApi();*/
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * <h>loginButtonValidation</h>
     * <p>
     * In this method we used to show sign up button more visible and clickable
     * when all the mandatory fields are filled.
     * </p>
     */

    private void signUpButtonValidation() {

        userNameCheckApi();
        // set Opacity to login button
        CommonClass.setViewOpacity(mActivity, rL_signup, 102, R.drawable.oval_purple_color_with_solid_shape);
        // user name
        eT_userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                if (eT_userName.getText().toString().contains(" ")) {
                    eT_userName.setText(eT_userName.getText().toString().replace(" ", ""));
                    eT_userName.setSelection(eT_userName.length());
                } else {
                    userNameCheckApi();
                    /*iV_userName_error.setVisibility(View.VISIBLE);
                    iV_userName_error.setImageResource(R.drawable.error_icon);
                    CommonClass.showTopSnackBar(rL_rootElement,"Invalid Username");*/
                }

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                    //isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    //CommonClass.setViewOpacity(mActivity, rL_signup, 102, R.drawable.oval_purple_color_with_solid_shape);
                }

                // change user icon
                String userName = eT_userName.getText().toString();
                if (userName.isEmpty())
                    iV_user_name.setImageResource(R.drawable.username_off);
                else iV_user_name.setImageResource(R.drawable.username_on);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // User Full name
        eT_fullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // change user name icon
                String fullName = eT_fullName.getText().toString();
                if (fullName.isEmpty())
                    iV_full_name.setImageResource(R.drawable.name_off);
                else iV_full_name.setImageResource(R.drawable.name_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_password2 = (EditText) signup_views.findViewById(R.id.eT_password2);
        eT_password1 = (EditText) signup_views.findViewById(R.id.eT_password1);
        eT_password1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);
                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 102, R.drawable.oval_purple_color_with_solid_shape);
                }

                // change user password icon
                String password = eT_password1.getText().toString();
                if (password.isEmpty())
                    iV_password.setImageResource(R.drawable.password_off);
                else iV_password.setImageResource(R.drawable.password_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        // Password
        /*eT_password = (EditText) signup_views.findViewById(R.id.eT_password);
        eT_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);
                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 102, R.drawable.oval_purple_color_with_solid_shape);
                }

                // change user password icon
                String password = eT_password.getText().toString();
                if (password.isEmpty())
                    iV_password.setImageResource(R.drawable.password_off);
                else iV_password.setImageResource(R.drawable.password_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        // Mobile number
        eT_mobileNo = (EditText) signup_views.findViewById(R.id.eT_mobileNo);
        /*eT_mobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                // call this method to verify mobile number
                phoneNumberCheckApi();

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,204,R.drawable.rect_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity,rL_signup,102,R.drawable.rect_purple_color_with_solid_shape);
                }

                // change user mobile icon
                String mobileNo=eT_mobileNo.getText().toString();
                if (mobileNo.isEmpty())
                    iV_phone_icon.setImageResource(R.drawable.mobileniumber_off);
                else iV_phone_icon.setImageResource(R.drawable.mobilenumber_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        // Email Id
        eT_emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println(TAG + " " + "isSignUpButtonEnabled=" + isSignUpButtonEnabled);

                // check mobile number to verify email address
                if (CommonClass.isValidEmail(eT_emailId.getText().toString()))
                    emailCheckApi();

                if (isSignUpParamFilled()) {
                    isSignUpButtonEnabled = true;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 204, R.drawable.oval_purple_color_with_solid_shape);
                } else {
                    isSignUpButtonEnabled = false;
                    // set Opacity to login button
                    CommonClass.setViewOpacity(mActivity, rL_signup, 102, R.drawable.oval_purple_color_with_solid_shape);
                }

                // change user mobile icon
                String emailId = eT_emailId.getText().toString();
                if (emailId.isEmpty())
                    iV_email.setImageResource(R.drawable.email_off);
                else iV_email.setImageResource(R.drawable.email_on);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // password
        eT_password1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    if (eT_password1.getText().toString().isEmpty()) {
                        iV_password_error.setVisibility(View.VISIBLE);
                        iV_password_error.setImageResource(R.drawable.error_icon);
                        CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_enter_password));
                        return true;
                    } else {
                        //iV_password_error.setVisibility(View.VISIBLE);
                        //iV_password_error.setImageResource(R.drawable.rightusername);
                        return false;
                    }
                }
                return false;
            }
        });

        // set password validation
        eT_password1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(eT_password1.getText())) {
                        iV_password_error.setVisibility(View.GONE);
                        //iV_password_error.setImageResource(R.drawable.rightusername);
                    } else {
                        iV_password_error.setVisibility(View.VISIBLE);
                        iV_password_error.setImageResource(R.drawable.error_icon);
                    }
                }
            }
        });
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

            if (arrayListCountry.size() > 0) {
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
            }
        }

        return  arrayListCountry;
    }

    /**
     * <h>SetCurrentCountryCode</h>
     * <p>
     * In this method we used to find the country iso number by giving its
     * iso code.
     * </p>
     *
     * @param isoCode The iso code of the country
     * @return it returns the country iso number e.g +91
     */
    private String setCurrentCountryCode(String isoCode) {
        String countryCode = "";
        for (Country country : arrayListCountry) {
            System.out.println(TAG + " " + "isoCode=" + isoCode + " " + "country.getName()=" + country.getName());
            if (country.getName().equals(isoCode)) {
                countryCode = country.getCode();
                return countryCode;
            }
        }
        return countryCode;
    }

    /**
     * <h>checkValidation</h>
     * <p>
     * In this method we used to check the mandatory field whether
     * it has been filled or not.
     * </p>
     */
    private boolean isLoginParamFilled() {
        boolean isValid;
        isValid = !eT_loginUserName.getText().toString().isEmpty()
                && CommonClass.isValidEmail(eT_loginUserName.getText().toString())
                && !eT_loginPassword.getText().toString().isEmpty()
                || CommonClass.ifValidNumber(eT_loginUserName.getText().toString())
                && !eT_loginPassword.getText().toString().isEmpty();

        return isValid;
    }

    private boolean isLoginParamEmail() {
        boolean isValid;
        isValid = !eT_loginUserName1.getText().toString().isEmpty() && CommonClass.isValidEmail(eT_loginUserName1.getText().toString());
        return isValid;
    }

    private boolean isLoginParamNumber() {
        boolean isValid;
        isValid = !eT_loginUserName1.getText().toString().isEmpty() && CommonClass.ifValidNumber(eT_loginUserName1.getText().toString());
        return isValid;
    }


    /**
     * <h>checkValidation</h>
     * <p>
     * In this method we used to check the mandatory field whether
     * it has been filled or not else call registation api.
     * </p>
     */
    private boolean isSignUpParamFilled() {
        boolean isValid;
        isValid = /*!eT_userName.getText().toString().isEmpty() &&*/
                !eT_password1.getText().toString().isEmpty() &&
                        !eT_emailId.getText().toString().isEmpty()
                        && CommonClass.isValidEmail(eT_emailId.getText().toString());
        return isValid;
    }

    private boolean selectPhone=true;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rL_next:


                if (eT_firstName.getText().toString().length()>0 && eT_lastName.getText().toString().length()>0)
                {
                    rl_1.setVisibility(View.GONE);
                    rl_2.setVisibility(View.VISIBLE);
                    rl_3.setVisibility(View.GONE);
                    rl_4.setVisibility(View.GONE);

                }
                else
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginOrSignupActivity.this);
                    builder1.setMessage(R.string.first_name_empty);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }

                break;

            case R.id.rL_next2:

                if (eT_loginUserName1!=null && eT_loginUserName1.getText().toString().length()>0 && eT_loginUserName1.getText().toString().length()>0 )
                {

                    //if ((eT_loginUserName1.getText().toString()!=null && eT_loginUserName.getText().toString().contains("@")))
                    if (!selectPhone)
                    {
                        if (isLoginParamEmail())
                        {
                            rl_1.setVisibility(View.GONE);
                            rl_2.setVisibility(View.GONE);
                            rl_3.setVisibility(View.VISIBLE);
                            rl_4.setVisibility(View.GONE);
                        }
                        else
                        {

                            AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginOrSignupActivity.this);
                            builder1.setMessage(R.string.label_emaiL_notvalid);
                            builder1.setCancelable(true);

                            builder1.setPositiveButton(
                                    "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });


                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                    }
                    else
                    {
                        rl_1.setVisibility(View.GONE);
                        rl_2.setVisibility(View.GONE);
                        rl_3.setVisibility(View.VISIBLE);
                        rl_4.setVisibility(View.GONE);


                        /*if ( isLoginParamNumber())
                        {
                            rl_1.setVisibility(View.GONE);
                            rl_2.setVisibility(View.GONE);
                            rl_3.setVisibility(View.VISIBLE);
                            rl_4.setVisibility(View.GONE);
                        }
                        else
                        {

                        }*/
                    }




                }
                else {

                    String message="";
                    if (!selectPhone)
                    {
                        message=getString(R.string.EmptyEmail);
                    }
                    else
                    {
                        message=getString(R.string.phone_name_empty);
                    }

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginOrSignupActivity.this);
                    builder1.setMessage(message);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                break;

            case R.id.rL_next3:

                if (eT_password1.getText().toString().length()>0 && eT_password2.getText().toString().length()>0)
                {

                    if (eT_password1.getText().toString().equalsIgnoreCase(eT_password2.getText().toString()))
                    {
                        rl_1.setVisibility(View.GONE);
                        rl_2.setVisibility(View.GONE);
                        rl_3.setVisibility(View.GONE);
                        rl_4.setVisibility(View.VISIBLE);

                    }
                    else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginOrSignupActivity.this);
                        builder1.setMessage(R.string.label_error_password);
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });


                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }

                }
                else {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginOrSignupActivity.this);
                    builder1.setMessage(R.string.EnterPW);
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });


                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }


                break;

           /* case R.id.rL_next4:

                rl_1.setVisibility(View.GONE);
                rl_2.setVisibility(View.GONE);
                rl_3.setVisibility(View.GONE);
                rl_4.setVisibility(View.GONE);
                break;*/

            case R.id.iV_back_icon1:

                if (rl_1.getVisibility()== login_views.VISIBLE)
                {
                    finish();
                    break;
                }
                else if (rl_2.getVisibility()== login_views.VISIBLE) {
                    iV_back_icon1.setVisibility(View.VISIBLE);

                    rl_1.setVisibility(View.VISIBLE);
                    rl_2.setVisibility(View.GONE);
                    rl_3.setVisibility(View.GONE);
                    rl_4.setVisibility(View.GONE);
                    break;
                }
                else if (rl_3.getVisibility()== login_views.VISIBLE) {
                    iV_back_icon1.setVisibility(View.VISIBLE);

                    rl_1.setVisibility(View.GONE);
                    rl_2.setVisibility(View.VISIBLE);
                    rl_3.setVisibility(View.GONE);
                    rl_4.setVisibility(View.GONE);
                    break;
                }
                else if (rl_4.getVisibility()== login_views.VISIBLE) {
                        iV_back_icon1.setVisibility(View.VISIBLE);

                        rl_1.setVisibility(View.GONE);
                        rl_2.setVisibility(View.GONE);
                        rl_3.setVisibility(View.VISIBLE);
                        rl_4.setVisibility(View.GONE);
                        break;
                    }


            case R.id.v_cod_country1:
                Intent intent1 = new Intent(mActivity, CountrySearchActivity.class);
                startActivityForResult(intent1, VariableConstants.SEARCH_COUNTRY1);

                break;



            case R.id.v_cod_country:
                Intent intent2 = new Intent(mActivity, CountrySearchActivity.class);
                startActivityForResult(intent2, VariableConstants.SEARCH_COUNTRY);

                break;

            case R.id.rL1_phone:
                v_1_phone.setBackgroundColor(Color.BLACK);
                v_1_email.setBackgroundColor(Color.parseColor("#959595"));
                v_cod_country.setVisibility(View.VISIBLE);
                eT_loginUserName.setHint(R.string.phone_label);
                eT_loginUserName.setText("");
                eT_loginUserName.setInputType(InputType.TYPE_CLASS_NUMBER);
                eT_loginPassword.setText("");
                selectPhone=true;
                break;

            case R.id.rL1_phone1:
                v_1_phone1.setBackgroundColor(Color.BLACK);
                v_1_email1.setBackgroundColor(Color.parseColor("#959595"));
                v_cod_country1.setVisibility(View.VISIBLE);
                eT_loginUserName1.setHint(R.string.phone_label);
                eT_loginUserName1.setText("");
                eT_loginUserName1.setInputType(InputType.TYPE_CLASS_NUMBER);
                selectPhone=true;
                break;

            case R.id.rL1_email1:
                v_1_phone1.setBackgroundColor(Color.parseColor("#959595"));
                v_1_email1.setBackgroundColor(Color.BLACK);
                v_cod_country1.setVisibility(View.GONE);
                eT_loginUserName1.setHint(R.string.email_label);
                eT_loginUserName1.setText("");
                eT_loginUserName1.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

                selectPhone=false;
                break;



            case R.id.rL1_email:
                v_1_phone.setBackgroundColor(Color.parseColor("#959595"));
                v_1_email.setBackgroundColor(Color.BLACK);
                v_cod_country.setVisibility(View.GONE);
                eT_loginUserName.setHint(R.string.email_label);
                eT_loginUserName.setText("");
                eT_loginUserName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                eT_loginPassword.setText("");
                selectPhone=false;
                break;

            // Back button
            case R.id.rL_back_btn:
                onBackPressed();
                break;

            // call login validation method
            case R.id.rL_do_login:
               // isLoginButtonEnabled=true;
               // if (isLoginButtonEnabled) {
                    isFromLocation = true;
                    permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
                    runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
                    showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);

                    LocationManager lm = (LocationManager) mActivity.getSystemService(Context.LOCATION_SERVICE);
                    boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    System.out.println(TAG + " " + "is location enabled=" + isLocationEnabled + " " + "is permission allowed=" + runTimePermission.checkPermissions(permissionsArray));
                    //  progress_bar_login.setVisibility(View.VISIBLE);
                    //   tV_do_login.setVisibility(View.GONE);
                   /* if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                        getCurrentLocation();
                    else*/

                    if (eT_loginUserName.getText().toString()!=null && eT_loginUserName.getText().toString().contains("@"))
                    {
                        loginRequestApi();
                    }
                    else
                    {
                        //getFilterPhone();
                        loginRequestPhoneApi();
                        Log.i("LOG","");
                    }



                    // loginRequestApi();
               // }
                break;

            // User profile pic
            case R.id.iV_profile_pic:
                System.out.println(TAG + " " + "profile pic clicked..");
                isFromLocation = false;
                permissionsArray = new String[]{CAMERA, WRITE_EXTERNAL_STORAGE};
                runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
                if (runTimePermission.checkPermissions(permissionsArray))
                    chooseImage();
                else {
                    runTimePermission.requestPermission();
                }
                break;

            //Register with phone
            case R.id.rL_signup_phone:
                Intent intent = new Intent(mActivity, PhoneAuthActivity.class);
                startActivity(intent);
                break;
            // Register user
            case R.id.rL_createAcc:
                isSignUpButtonEnabled=true;

                    eT_emailId.setText(eT_loginUserName1.getText().toString());
                    if (1==1) {
                        if (checkboxSignUp.isChecked()) {
                            if (isUserRegistered) {
                                CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.username_already_registered));
                            } else if (isPhoneRegistered) {
                                CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.phone_already_registered));
                            } else if (isEmailRegistered) {
                                CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.email_already_registered));
                            } else {
                                //generateOtp();

                                System.out.println(TAG + " " + "type=" + type);

                                switch (type) {
                                    // Normal sign up
                                    case "normalSignup":
                                        signUpType = VariableConstants.TYPE_MANUAL;
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        if (isPictureTaken) {
                                            getCloudinaryDetailsApi();
                                        } else {
                                            //openOtpScreen("");
                                            doRegistration("");
                                        }
                                        break;

                                    // Google sign up
                                    case "googleSignUp":
                                        signUpType = VariableConstants.TYPE_GOOGLE;
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        if (isPictureTaken) {
                                            getCloudinaryDetailsApi();
                                        } else {
                                            //openOtpScreen(google_userImageUrl);
                                            doRegistration(google_userImageUrl);
                                        }
                                        break;

                                    // Facebook signup
                                    case "fbSignUp":
                                        signUpType = VariableConstants.TYPE_FACEBOOK;
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        if (isPictureTaken) {
                                            getCloudinaryDetailsApi();
                                        } else {
                                            //openOtpScreen(fb_userImageUrl);
                                            doRegistration(fb_userImageUrl);
                                        }
                                        break;
                                }
                            }
                        } else
                            CommonClass.showTopSnackBar(rL_rootElement, getResources().getString(R.string.please_accept_termsNconditions));
                    }




                break;

            // open country picker dialog
            case R.id.rL_country_picker:
                if (dialogCountryList != null) {
                    showKeyboard(InputMethodManager.SHOW_FORCED);
                    dialogCountryList.showCountryCodePicker(new SetCountryCodeListener() {
                        @Override
                        public void getCode(String code, String name) {
                            showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);
                            countryIsoNumber = getResources().getString(R.string.plus) + code;
                            code = getResources().getString(R.string.plus) + code;
                            tV_country_iso_no.setText(code);
                            tV_country_code.setText(name);
                            eT_mobileNo.requestFocus();
                        }
                    });
                }
                break;

            // forgot password
            case R.id.rL_forgot_password:
                if (isToStartActivity) {
                    if (selectPhone)
                    {
                        //startActivity(new Intent(mActivity, ForgotPasswordActivity.class));
                        Intent intentFor = new Intent(mActivity, ForgotPasswordActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("key", 1); //Your id
                        intentFor.putExtras(b); //Put your id to your next Intent
                        startActivity(intentFor);

                    }
                    else
                    {
                        Intent intentFor = new Intent(mActivity, ForgotPasswordActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("key", 2); //Your id
                        intentFor.putExtras(b); //Put your id to your next Intent
                        startActivity(intentFor);
                    }

                    isToStartActivity = false;
                }
                break;
            case R.id.tV_click_here:
                login_views.setVisibility(View.VISIBLE);
                signup_views.setVisibility(View.GONE);
                break;
            case R.id.tV_signup_new:
            case R.id.iV_back_icon:
                login_views.setVisibility(View.GONE);
                signup_views.setVisibility(View.VISIBLE);

                break;
            case R.id.rL_fb_login:
                LoginManager.getInstance().logOut();
                isFromGplusLocation = false;
                loginWithFacebook.loginWithFbWithSdk();
                break;

            case R.id.rL_google_login:
                isFromGplusLocation = true;
                signInWithGoogle();
                break;
        }
    }

    private void validatePhone()
    {

    }

    private boolean found=false;
    private UserBean movie ;
    private void getFilterPhone()
    {
        FirebaseDatabase.getInstance().goOnline();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        //Query query = reference.child("lagel/data/").orderByChild("phone").equalTo("9999999");
        Query query = reference.child("lagel/data/").orderByChild("phone").equalTo(eT_loginUserName.getText().toString());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do with your result
                        Log.i("LOG","");

                        movie = issue.getValue(UserBean.class);

                        //movie = dataSnapshot.getValue(UserBean.class);
                        if (movie.getPassword().equals(eT_loginPassword.getText().toString())) {
                            found=true;
                            break;
                        }

                    }

                    if (found)
                    {
                        Log.i("LOG","Found");

                        loginRequestPhoneApi();
                    }
                    else
                    {
                        CommonClass.showTopSnackBar(rL_rootElement, "user not Found");

                    }
                }
                else {
                    CommonClass.showTopSnackBar(rL_rootElement, "user not Found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }
    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, VariableConstants.GOOGLE_LOGIN_REQ_CODE);
    }

    /**
     * <h>GenerateOtp</h>
     */
    private void generateOtp() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_signup.setVisibility(View.VISIBLE);
            tV_signup.setVisibility(View.GONE);

            System.out.println(TAG + " " + "country code=" + tV_country_iso_no.getText().toString());

            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("deviceId", mSessionManager.getDeviceId());
                request_datas.put("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.OTP, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    System.out.println(TAG + " " + "otp response=" + result);
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    Gson gson = new Gson();
                    PhoneOtpMainPojo otpMainPojo = gson.fromJson(result, PhoneOtpMainPojo.class);

                    // success
                    switch (otpMainPojo.getCode()) {
                        // success
                        case "200":
                            otpCode = otpMainPojo.getData();
                            System.out.println(TAG + " " + "type=" + type);

                            switch (type) {
                                // Normal sign up
                                case "normalSignup":
                                    signUpType = VariableConstants.TYPE_MANUAL;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen("");
                                    }
                                    break;

                                // Google sign up
                                case "googleSignUp":
                                    signUpType = VariableConstants.TYPE_GOOGLE;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(google_userImageUrl);
                                    }
                                    break;

                                // Facebook signup
                                case "fbSignUp":
                                    signUpType = VariableConstants.TYPE_FACEBOOK;
                                    if (isPictureTaken) {
                                        progress_bar_signup.setVisibility(View.VISIBLE);
                                        tV_signup.setVisibility(View.GONE);
                                        getCloudinaryDetailsApi();
                                    } else {
                                        openOtpScreen(fb_userImageUrl);
                                    }
                                    break;
                            }
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error like invalid phone number
                        case "500":
                            CommonClass.showSnackbarMessage(rL_rootElement, otpMainPojo.getError().getMessage());
                            break;

                        // other error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement, otpMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }

    private void doRegistration(String profilePicUrl) {
        // Hide Keypad
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

        String signUpPass = eT_password1.getText().toString();
        String phone=null;
        String[] separated =v_cod_country1.getText().toString() .split("\\+");
        if (separated !=null && separated .length>0)
        {
            phone="+"+separated[1]+""+eT_loginUserName1.getText().toString();
        }

        String signUpEmail ="";
        //String signUpUserName=v_cod_country1.getText().toString() +eT_loginUserName1.getText().toString() ;
        String signUpUserName="";


        String signUpFullName = eT_fullName.getText().toString();
        String signUpMobNo = "";  //countryIsoNumber+eT_mobileNo.getText().toString();
        //String signUpEmail = eT_emailId.getText().toString();

        signUpFullName=""+eT_firstName.getText().toString()+" "+eT_lastName.getText().toString();

        Integer typeRegister = 0;

        if (signUpType.equalsIgnoreCase(VariableConstants.TYPE_MANUAL))
        {
            //signUpUserName = phone;
            signUpUserName = randomUserName;

            if (selectPhone)
            {
                signUpEmail =phone;
            }
            else {
                signUpEmail =eT_loginUserName1.getText().toString();
            }

            if (eT_loginUserName1.getText().toString()!=null && eT_loginUserName1.getText().toString().contains("@"))
            {
                typeRegister = 0;
            } else {
                typeRegister = 1;
            }
        }
        else
        {
            signUpUserName = randomUserName;//eT_userName.getText().toString();
            signUpEmail = email;
            signUpFullName = fullName;
            signUpPass =  signUpType.equalsIgnoreCase(VariableConstants.TYPE_GOOGLE) ? googleId : fbId;
            signUpType = VariableConstants.TYPE_MANUAL;
        }

        SubmitRegistration mSubmitRegistration = new SubmitRegistration(mActivity, progress_bar_signup, tV_signup, rL_rootElement, type, signUpType, signUpUserName, profilePicUrl, signUpFullName, signUpPass, signUpMobNo, signUpEmail, googleToken, googleId, fbId, fb_accessToken,typeRegister);
        mSubmitRegistration.submitRegistration();
    }

    private void openOtpScreen(String profilePicUrl) {
        // stop progress bar
        progress_bar_signup.setVisibility(View.GONE);
        tV_signup.setVisibility(View.VISIBLE);

        // Hide Keypad
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        // call Number verification screen
        Intent intent = new Intent(mActivity, NumberVerificationActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("signupType", signUpType);
        intent.putExtra("username", eT_userName.getText().toString());
        intent.putExtra("profilePicUrl", profilePicUrl);
        intent.putExtra("fullName", eT_fullName.getText().toString());
        intent.putExtra("password", eT_password1.getText().toString());
        intent.putExtra("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
        intent.putExtra("email", eT_emailId.getText().toString());
        intent.putExtra("googleToken", googleToken);
        intent.putExtra("googleId", googleId);
        intent.putExtra("facebookId", fbId);
        intent.putExtra("accessToken", fb_accessToken);
        intent.putExtra("otpCode", otpCode);
        if (isToStartActivity) {
            startActivityForResult(intent, VariableConstants.NUMBER_VERIFICATION_REQ_CODE);
            isToStartActivity = false;
            System.out.println(TAG + " " + "sending mob no=" + countryIsoNumber + eT_mobileNo.getText().toString());
        }
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation() {
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

                            Log.e("Latitude_GET", "lat000: " + mSessionManager.getCurrentLat());
                            Log.e("Latitude_GET", "lng000: " + mSessionManager.getCurrentLng());

                            address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            countryShortName = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                            loginRequestApi();
                        }
                    }
                }
            }
            );
        } else {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
        }
    }

    private void fetchLocation() {
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

                            Log.e("Latitude_GET", "lat001: " + mSessionManager.getCurrentLat());
                            Log.e("Latitude_GET", "lng001: " + mSessionManager.getCurrentLng());
                        }
                    }
                }
            }
            );
        }
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
     * <h>SelectImage</h>
     * <p>
     * Using this method we open a pop-up. when
     * user click on profile image. it contains
     * thee field Take Photo,Choose from Gallery,
     * Cancel.
     * </p>
     */
    public void chooseImage() {
        final Dialog selectImgDialog = new Dialog(mActivity);
        selectImgDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        selectImgDialog.setContentView(R.layout.select_image_layout);

        // Take picture
        RelativeLayout rL_take_pic = (RelativeLayout) selectImgDialog.findViewById(R.id.rL_take_pic);
        rL_take_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgDialog.dismiss();
                mHandleCameraEvents.takePicture();
            }
        });

        // Choose Image from gallery
        RelativeLayout rL_select_pic = (RelativeLayout) selectImgDialog.findViewById(R.id.rL_select_pic);
        rL_select_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImgDialog.dismiss();
                mHandleCameraEvents.openGallery();
            }
        });

        // Remove profile pic
        RelativeLayout rL_remove_pic = (RelativeLayout) selectImgDialog.findViewById(R.id.rL_remove_pic);
        if (!isPictureTaken) {
            iv_edit_icon.setVisibility(View.GONE);
            rL_remove_pic.setVisibility(View.GONE);
        } else {
            iv_edit_icon.setVisibility(View.VISIBLE);
            rL_remove_pic.setVisibility(View.VISIBLE);
        }

        rL_remove_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeProfileImage();
                selectImgDialog.dismiss();
            }
        });

        selectImgDialog.show();
    }

    private void removeProfileImage() {
        isPictureTaken = false;
        iv_edit_icon.setVisibility(View.GONE);
        iV_profile_pic.setImageResource(R.drawable.add_photo);
    }

    /**
     * Called for showing Progress dialog
     */
    public void showProgressDialog(String text) {
        try{
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(text);
            progressDialog.setCancelable(true);
            progressDialog.show();
        }
        catch(Exception ex)
        {
            Log.e("Error",ex.getMessage());
        }
    }

    /**
     * Dismiss Progress dialog
     */

    public void dismissDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }



    private void loginRequestPhoneApi() {
        showProgressDialog("Connecting...");
        if (FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }

        //username, password
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDatas = new JSONObject();
            // loginType, pushToken, place, city, countrySname, latitude, longitude,username, password
            try {
                String phone="";
                String code_country=v_cod_country.getText().toString();
                String[] separated =code_country.split("\\+");
                if (separated !=null && separated .length>0)
                {
                    phone="+"+separated[1]+""+eT_loginUserName.getText().toString();
                }


                Log.i("TAG","");

                requestDatas.put("loginType", VariableConstants.TYPE_MANUAL);
                requestDatas.put("pushToken", mSessionManager.getPushToken());
                requestDatas.put("place", address);
                requestDatas.put("city", city);
                requestDatas.put("countrySname", countryShortName);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                //requestDatas.put("email", movie.getEmail());
                //requestDatas.put("username", movie.getEmail());
                //requestDatas.put("password", movie.getPassword());
                requestDatas.put("phone",phone );
                //requestDatas.put("username", eT_loginUserName.getText().toString());
                requestDatas.put("password", eT_loginPassword.getText().toString());
                requestDatas.put("googleId", "0101");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    dismissDialog();
                    LoginResponsePojo loginResponse;
                    Gson gson = new Gson();
                    loginResponse = gson.fromJson(result, LoginResponsePojo.class);

                    switch (loginResponse.getCode()) {
                        // Success
                        case "200":
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("normalLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(), loginResponse.getMqttId(), loginResponse.getEmail(), loginResponse.getUsername(), loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // Error
                        default:
                            dismissDialog();
                            progress_bar_login.setVisibility(View.GONE);
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement, loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    dismissDialog();
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
            CommonClass.showTopSnackBar(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
        }
    }

    private void loginRequestPhoneApiOLD() {
        showProgressDialog("Connecting...");
        if (FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }

        //username, password
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDatas = new JSONObject();
            // loginType, pushToken, place, city, countrySname, latitude, longitude,username, password
            try {
                requestDatas.put("loginType", VariableConstants.TYPE_PHONE);
                requestDatas.put("pushToken", mSessionManager.getPushToken());
                requestDatas.put("place", address);
                requestDatas.put("city", city);
                requestDatas.put("countrySname", countryShortName);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                requestDatas.put("email", movie.getEmail());
                requestDatas.put("username", movie.getEmail());
                requestDatas.put("password", movie.getPassword());
                requestDatas.put("googleId", "0101");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    dismissDialog();
                    LoginResponsePojo loginResponse;
                    Gson gson = new Gson();
                    loginResponse = gson.fromJson(result, LoginResponsePojo.class);

                    switch (loginResponse.getCode()) {
                        // Success
                        case "200":
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("normalLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(), loginResponse.getMqttId(), loginResponse.getEmail(), loginResponse.getUsername(), loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // Error
                        default:
                            dismissDialog();
                            progress_bar_login.setVisibility(View.GONE);
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement, loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    dismissDialog();
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
            CommonClass.showTopSnackBar(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
        }
    }


    /**
     * <h>LoginRequestApi</h>
     * <p>
     * This method is called when user click on Normal login button.
     * In this method we used to call login api through OkHttp3. After
     * getting response if the code is 200. Then we move to HomePageActivity.
     * </p>
     */
    private void loginRequestApi() {
        showProgressDialog("Connecting...");
        //username, password
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDatas = new JSONObject();
            // loginType, pushToken, place, city, countrySname, latitude, longitude,username, password
            try {
                requestDatas.put("loginType", VariableConstants.TYPE_MANUAL);
                requestDatas.put("pushToken", mSessionManager.getPushToken());
                requestDatas.put("place", address);
                requestDatas.put("city", city);
                requestDatas.put("countrySname", countryShortName);
                requestDatas.put("latitude", currentLat);
                requestDatas.put("longitude", currentLng);
                requestDatas.put("email", eT_loginUserName.getText().toString());
                requestDatas.put("username", eT_loginUserName.getText().toString());
                requestDatas.put("password", eT_loginPassword.getText().toString());
                requestDatas.put("googleId", "0101");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    dismissDialog();
                    LoginResponsePojo loginResponse;
                    Gson gson = new Gson();
                    loginResponse = gson.fromJson(result, LoginResponsePojo.class);

                    switch (loginResponse.getCode()) {
                        // Success
                        case "200":
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("normalLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(), loginResponse.getMqttId(), loginResponse.getEmail(), loginResponse.getUsername(), loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // Error
                        default:
                            dismissDialog();
                            progress_bar_login.setVisibility(View.GONE);
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement, loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    dismissDialog();
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else {
            progress_bar_login.setVisibility(View.GONE);
            tV_do_login.setVisibility(View.VISIBLE);
            CommonClass.showTopSnackBar(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
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
                    progress_bar_login.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "log device info=" + result);

                    LogDevicePojo logDevicePojo;
                    Gson gson = new Gson();
                    logDevicePojo = gson.fromJson(result, LogDevicePojo.class);

                    switch (logDevicePojo.getCode()) {
                        // success
                        case "200":
                            // Open Home page screen
                            //LandingActivity.mLandingActivity.finish();
                            //finish();

                            Intent intent = new Intent();
                            intent.putExtra("isToFinishLandingScreen", true);
                            setResult(VariableConstants.LOGIN_SIGNUP_REQ_CODE, intent);
                            finish();
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showSnackbarMessage(rL_rootElement, logDevicePojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_login.setVisibility(View.GONE);
                    tV_do_login.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>phoneNumberCheckApi</h>
     * <p>
     * In this method we used to do phone Number Check api
     * to find the given phone number is already exist or
     * not.
     * </p>
     */
    private void phoneNumberCheckApi(String phone) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_ph.setVisibility(View.VISIBLE);
            iV_error_ph.setVisibility(View.GONE);
            JSONObject request_param = new JSONObject();
            try {
                //request_param.put("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
                request_param.put("phoneNumber", phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PHONE_NUMBER_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "phoneNumber api res=" + result);

                    PhoneNoCheckPojo phoneNoCheckPojo;
                    Gson gson = new Gson();
                    phoneNoCheckPojo = gson.fromJson(result, PhoneNoCheckPojo.class);

                    switch (phoneNoCheckPojo.getCode()) {
                        // success
                        case "200":
                            isPhoneRegistered = false;
                            rL_next2.setVisibility(View.VISIBLE);
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            isPhoneRegistered = true;
                            rL_next2.setVisibility(View.GONE);
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement, phoneNoCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                }
            });
        }
    }

    private boolean phoneNumberCheckApi2(String phone) {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            progress_bar_ph.setVisibility(View.VISIBLE);
            iV_error_ph.setVisibility(View.GONE);
            JSONObject request_param = new JSONObject();
            try {
                //request_param.put("phoneNumber", countryIsoNumber + eT_mobileNo.getText().toString());
                request_param.put("phoneNumber", phone);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PHONE_NUMBER_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "phoneNumber api res=" + result);

                    PhoneNoCheckPojo phoneNoCheckPojo;
                    Gson gson = new Gson();
                    phoneNoCheckPojo = gson.fromJson(result, PhoneNoCheckPojo.class);

                    switch (phoneNoCheckPojo.getCode()) {
                        // success
                        case "200":
                            isPhoneRegistered = false;
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            isPhoneRegistered = true;
                            iV_error_ph.setVisibility(View.VISIBLE);
                            iV_error_ph.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement, phoneNoCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_ph.setVisibility(View.GONE);
                }
            });
        }

        return isPhoneRegistered;
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
        randomUserName = sb1.toString();

       /* Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt();
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }*/
        return randomUserName;
    }

    /**
     * <h>UserNameCheckApi</h>
     * <p>
     * In this method we used to do user name Check api
     * to find the given user name is already exist or
     * not.
     * </p>
     */
    private void userNameCheckApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            pBar_userName.setVisibility(View.VISIBLE);
            iV_userName_error.setVisibility(View.GONE);
            JSONObject request_param = new JSONObject();
            try {
                Log.e("Random NUmber", random());
                request_param.put("username", random());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.USER_NAME_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    pBar_userName.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "user name check api res=" + result);

                    UserNameCheckPojo userNameCheckPojo;
                    Gson gson = new Gson();
                    userNameCheckPojo = gson.fromJson(result, UserNameCheckPojo.class);

                    switch (userNameCheckPojo.getCode()) {
                        // success i.e user is not registered
                        case "200":
                            isUserRegistered = false;
                            rL_next2.setVisibility(View.VISIBLE);
                            iV_userName_error.setVisibility(View.VISIBLE);
                            iV_userName_error.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error like user is already registered
                        default:
                            isUserRegistered = true;
                            rL_next2.setVisibility(View.GONE);
                            iV_userName_error.setVisibility(View.VISIBLE);
                            iV_userName_error.setImageResource(R.drawable.error_icon);
                            userNameCheckApi();
                            CommonClass.showTopSnackBar(rL_rootElement, userNameCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_userName.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * <h>UserNameCheckApi</h>
     * <p>
     * In this method we used to do user name Check api
     * to find the given user name is already exist or
     * not.
     * </p>
     */
    private void emailCheckApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            pBar_email.setVisibility(View.VISIBLE);
            iV_error_email.setVisibility(View.GONE);
            JSONObject request_param = new JSONObject();
            try {
                //request_param.put("email", eT_emailId.getText().toString());
                request_param.put("email", eT_loginUserName1.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.EMAIL_ID_CHECK, OkHttp3Connection.Request_type.POST, request_param, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    pBar_email.setVisibility(View.GONE);
                    System.out.println(TAG + " " + "email is check api res=" + result);

                    EmailCheckPojo emailCheckPojo;
                    Gson gson = new Gson();
                    emailCheckPojo = gson.fromJson(result, EmailCheckPojo.class);

                    switch (emailCheckPojo.getCode()) {
                        // success i.e email is not registered
                        case "200":
                            rL_next2.setVisibility(View.VISIBLE);
                            isEmailRegistered = false;
                            iV_error_email.setVisibility(View.VISIBLE);
                            iV_error_email.setImageResource(R.drawable.rightusername);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;
                        // error like email is already registered
                        default:
                            isEmailRegistered = true;
                            rL_next2.setVisibility(View.GONE);
                            iV_error_email.setVisibility(View.VISIBLE);
                            iV_error_email.setImageResource(R.drawable.error_icon);
                            CommonClass.showTopSnackBar(rL_rootElement, emailCheckPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    pBar_email.setVisibility(View.GONE);
                }
            });
        }
    }

    /**
     * <h2>getCloud_details</h2>
     * <p>
     * Collecting the cloudinary details from the server..
     * </P>
     */
    private void getCloudinaryDetailsApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_data = new JSONObject();
            OkHttp3Connection.doOkHttp3Connection("", ApiUrl.GET_CLOUDINARY_DETAILS, OkHttp3Connection.Request_type.POST, request_data, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String tag) {

                    Cloudinary_Details_reponse response = new Gson().fromJson(result, Cloudinary_Details_reponse.class);
                    if (response.getCode().equals("200")) {
                        String cloudName, timestamp, apiKey, signature;
                        cloudName = response.getResponse().getCloudName();
                        timestamp = response.getResponse().getTimestamp();
                        apiKey = response.getResponse().getApiKey();
                        signature = response.getResponse().getSignature();

                        Bundle remaining_data = new Bundle();
                        remaining_data.putString("signature", signature);
                        remaining_data.putString("timestamp", timestamp);
                        remaining_data.putString("apiKey", apiKey);
                        remaining_data.putString("cloudName", cloudName);

                        postMedia(remaining_data);
                    } else {
                        progress_bar_signup.setVisibility(View.GONE);
                        tV_signup.setVisibility(View.VISIBLE);
                        CommonClass.showTopSnackBar(rL_rootElement, response.getMessage());
                    }
                }

                @Override
                public void onError(String error, String tag) {
                    progress_bar_signup.setVisibility(View.GONE);
                    tV_signup.setVisibility(View.VISIBLE);
                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showTopSnackBar(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }

    /**
     * <h>PostMedia</h>
     * <p>
     * In this method we are uploading file(Image or video) to the cloudinary server.
     * in Respose we will get Url. Once we get Url of the image then call registration
     * api.
     * </p>
     *
     * @param remaining_data The bundle of cloudinary details data
     */
    private void postMedia(Bundle remaining_data) {
        /*
         * Creating the path holder.*/
        CloudData cloudData = new CloudData();
        //cloudData.setPath(handleCameraEvent.getRealPathFromURI());
        cloudData.setPath(mFile.getAbsolutePath());
        cloudData.setVideo(false);

        new UploadToCloudinary(remaining_data, cloudData) {
            @Override
            public void callBack(Map resultData) {
                if (resultData != null) {
                    String main_url = (String) resultData.get("url");
                    System.out.println(TAG + " " + "main url=" + main_url);
                    //openOtpScreen(main_url);
                    doRegistration(main_url);
                    //getCurrentLocation(main_url);
                }
            }

            @Override
            public void errorCallBack(String error) {

            }
        };
    }

    /**
     * <h>ShowKeyboard</h>
     * <p>
     * In this method we used to open device keypad when user
     * click on search iocn and close when user click close
     * search button.
     * </p>
     *
     * @param flag This is integer valuew to open or close keypad
     */
    private void showKeyboard(int flag) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(flag, 0);
    }

    @Override
    public void onBackPressed() {

        if (rl_1!=null && rl_1.getVisibility()== login_views.VISIBLE)
        {
            finish();

        }
        else if (rl_2!=null && rl_2.getVisibility()== login_views.VISIBLE) {
            iV_back_icon1.setVisibility(View.VISIBLE);

            rl_1.setVisibility(View.VISIBLE);
            rl_2.setVisibility(View.GONE);
            rl_3.setVisibility(View.GONE);
            rl_4.setVisibility(View.GONE);

        }
        else if (rl_3!=null && rl_3.getVisibility()== login_views.VISIBLE) {
            iV_back_icon1.setVisibility(View.VISIBLE);

            rl_1.setVisibility(View.GONE);
            rl_2.setVisibility(View.VISIBLE);
            rl_3.setVisibility(View.GONE);
            rl_4.setVisibility(View.GONE);

        }
        else if (rl_4!=null && rl_4.getVisibility()== login_views.VISIBLE) {
            iV_back_icon1.setVisibility(View.VISIBLE);

            rl_1.setVisibility(View.GONE);
            rl_2.setVisibility(View.GONE);
            rl_3.setVisibility(View.VISIBLE);
            rl_4.setVisibility(View.GONE);

        }
        else {
            finish();
        }


        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        dismissDialog();
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();

            assert acct != null;
            Log.e(TAG, "display name: " + acct.getDisplayName());

            googleId = acct.getId();
            googleToken = acct.getId();
            fullName = acct.getDisplayName();

            Uri imageUri = acct.getPhotoUrl();
            if (imageUri != null)
                personPhotoUrl = imageUri.toString();

            email = acct.getEmail();
            id = acct.getId();
            String familyName = acct.getFamilyName();
            String givenName = acct.getGivenName();
            String idToken = acct.getIdToken();
            serverAuthCode = acct.getServerAuthCode();

            Log.e(TAG, "Name: " + fullName + ", email: " + email
                    + ", Image: " + personPhotoUrl + ", id: " + id + ", familyName: " + familyName + ", givenName: " + givenName + ", idToken: " + idToken + ", serverAuthCode: " + serverAuthCode);

            LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isLocationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            System.out.println(TAG + " " + "is location enabled=" + isLocationEnabled + " " + "is permission allowed=" + runTimePermission.checkPermissions(permissionsArray));

            googleLoginProgress(true);
           /* if (isLocationEnabled && runTimePermission.checkPermissions(permissionsArray))
                getCurrentLocation();
            else*/
            googleLoginApi();
        }
    }

    private void googleLoginProgress(boolean isVisible) {
        if (isVisible) {
            showProgressDialog("Connecting...");
            //  pBar_GLogin.setVisibility(View.VISIBLE);
            //  iV_Gicon.setVisibility(View.GONE);
            // tV_Google.setVisibility(View.GONE);
        } else {
            dismissDialog();
            pBar_GLogin.setVisibility(View.GONE);
            iV_Gicon.setVisibility(View.VISIBLE);
            tV_Google.setVisibility(View.VISIBLE);
        }
    }

    private void googleLoginApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            showProgressDialog("Please wait...");
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("loginType", VariableConstants.TYPE_GOOGLE);
                request_datas.put("pushToken", mSessionManager.getPushToken());
                request_datas.put("place", address);
                request_datas.put("city", city);
                request_datas.put("countrySname", countryCode);
                request_datas.put("latitude", currentLat);
                request_datas.put("longitude", currentLng);
                request_datas.put("googleId", id);
                request_datas.put("email", email);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LOGIN, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    dismissDialog();
                    System.out.println(TAG + " " + "google login res=" + result);
                    //progress_bar_login.setVisibility(View.GONE);
                    LoginResponsePojo loginResponse;
                    Gson gson = new Gson();
                    loginResponse = gson.fromJson(result, LoginResponsePojo.class);

                    switch (loginResponse.getCode()) {
                        // Success
                        case "200":
                            mSessionManager.setIsUserLoggedIn(true);
                            mSessionManager.setmqttId(loginResponse.getMqttId());
                            mSessionManager.setAuthToken(loginResponse.getToken());
                            mSessionManager.setUserName(loginResponse.getUsername());
                            mSessionManager.setUserImage(loginResponse.getProfilePicUrl());
                            mSessionManager.setUserId(loginResponse.getUserId());
                            mSessionManager.setLoginWith("googleLogin");
                            initUserDetails(loginResponse.getProfilePicUrl(), loginResponse.getMqttId(), email, loginResponse.getUsername(), loginResponse.getToken());
                            logDeviceInfo(loginResponse.getToken());
                            break;

                        // user not found
                        case "204":
                            if (isToStartActivity) {
                                isToStartActivity = false;
                                googleLoginProgress(false);

                                signUpType = VariableConstants.TYPE_GOOGLE;
                                progress_bar_signup.setVisibility(View.VISIBLE);
                                tV_signup.setVisibility(View.GONE);
                                doRegistration(google_userImageUrl);
                            }
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // Error
                        default:
                            //tV_do_login.setVisibility(View.VISIBLE);
                            CommonClass.showTopSnackBar(rL_rootElement, loginResponse.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    googleLoginProgress(false);

                    CommonClass.showTopSnackBar(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG + " " + "on activity result..." + " " + "requestCode=" + requestCode + " " + "result code=" + requestCode + "data=" + data);

        if (requestCode==VariableConstants.SEARCH_COUNTRY)
        {
            if (data!=null)
            {
                String returnValue = data.getStringExtra("code");
                v_cod_country.setText(returnValue);
                Log.i("TAG",returnValue);
            }
        }

        if (requestCode==VariableConstants.SEARCH_COUNTRY1)
        {
            if (data!=null)
            {
                String returnValue = data.getStringExtra("code");
                v_cod_country1.setText(returnValue);

                String[] separated =v_cod_country1.getText().toString() .split("\\+");
                if (separated !=null && separated .length>0)
                {
                    //sphone="+"+separated[1]+""+eT_loginUserName1.getText().toString();
                    sphone="+"+separated[1];
                }


                Log.i("TAG",returnValue);
            }
        }

        /*if (resultCode != RESULT_OK) {

            return;
        }*/
        Bitmap bitmap;
        if (data != null) {
            loginWithFacebook.fbOnActivityResult(requestCode, resultCode, data);
            switch (requestCode) {

                case VariableConstants.GOOGLE_LOGIN_REQ_CODE:
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    int statusCode = result.getStatus().getStatusCode();
                    Log.e("Status Code", "" + statusCode);
                    handleSignInResult(result);
                    break;


                // Gallery
                case VariableConstants.SELECT_GALLERY_IMG_REQ_CODE:
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        FileOutputStream fileOutputStream = new FileOutputStream(mFile);
                        mHandleCameraEvents.copyStream(inputStream, fileOutputStream);
                        fileOutputStream.close();
                        assert inputStream != null;
                        inputStream.close();
                        mHandleCameraEvents.startCropImage();

                    } catch (Exception e) {

                        Log.e(TAG, "Error while creating temp file", e);
                    }
                    break;

                // Camera
                case VariableConstants.CAMERA_CAPTURE:
                    mHandleCameraEvents.startCropImage();
                    break;

                // Crop
                case VariableConstants.PIC_CROP:

                    String path = data.getStringExtra(CropImage.IMAGE_PATH);
                    if (path == null) {
                        return;
                    }
                    isPictureTaken = true;
                    iv_edit_icon.setVisibility(View.VISIBLE);
                    bitmap = BitmapFactory.decodeFile(mFile.getPath());
                    iV_profile_pic.setImageBitmap(mHandleCameraEvents.getCircleBitmap(bitmap));
                    break;

                // To finish Current screen
                case VariableConstants.NUMBER_VERIFICATION_REQ_CODE:
                    boolean isToFinishLoginSignup = data.getBooleanExtra("isToFinishLoginSignup", true);
                    System.out.println(TAG + "isToFinishLoginSignup=" + isToFinishLoginSignup);
                    if (isToFinishLoginSignup) {
                        Intent intent = new Intent();
                        intent.putExtra("isToFinishLandingScreen", true);
                        intent.putExtra("isFromSignup", true);
                        setResult(VariableConstants.LOGIN_SIGNUP_REQ_CODE, intent);
                        finish();
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case VariableConstants.PERMISSION_REQUEST_CODE:

                System.out.println("grant result=" + grantResults.length);
                if (grantResults.length > 0) {
                    for (int count = 0; count < grantResults.length; count++) {
                        if (grantResults[count] != PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);
                    }

                    if (runTimePermission.checkPermissions(permissionsArray)) {
                        fetchLocation();
                        if (isFromLocation) {
                            getCurrentLocation();
                        } else if (isAskLocationPermission) {
                            Log.d("location permission", "granted");
                        } else
                            chooseImage();
                    }
                }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return true;
    }

    /*
    * Initialization of the user details .*/
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
        if (userId == null || userId.isEmpty()) {
            Toast.makeText(this, R.string.userIdtext, Toast.LENGTH_SHORT).show();
            return;
        }

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
