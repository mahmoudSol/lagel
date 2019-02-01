package com.lagel.com.main.activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.bean.UserBean;
import com.lagel.com.county_code_picker.SetCountryCodeListener;
import com.lagel.com.main.tab_fragments.HomeFrag;
import com.lagel.com.pojo_class.PhoneNoCheckPojo;
import com.lagel.com.pojo_class.sign_up_pojo.SignUpMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class PhoneAuthActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final String TAG = "PhoneAuthActivity";
    private String phoneNumberTest="1234567895";
    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private ViewGroup mPhoneNumberViews;
    //private ViewGroup mSignedInViews;

    private TextView mStatusText;
    private TextView mDetailText;

    private EditText mPhoneNumberField;
    private EditText mVerificationField;
    private RelativeLayout rL_rootElement;
    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;
    private Button mSignOutButton;
    private ImageView iv_edit_icon;
    private EditText eT_userName, eT_fullName, eT_password, eT_mobileNo, eT_emailId;
    private ProgressBar progress_bar_ph;
    private boolean isPhoneRegistered;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_phone_auth);
        setContentView(R.layout.sign_up_phone_fields);
        tV_by_signing_up = (TextView) findViewById(R.id.tV_by_signing_up);
        progress_bar_ph = (ProgressBar) findViewById(R.id.pBar_ph);
        setTermsNconditions();
        iv_edit_icon = (ImageView) findViewById(R.id.iv_edit_icon);
        iv_edit_icon.setVisibility(View.GONE);
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);

        eT_fullName = (EditText) findViewById(R.id.eT_fullName);
        eT_fullName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        eT_password = (EditText) findViewById(R.id.eT_password);

        findViewById(R.id.tV_click_here).setOnClickListener(this);

        mSessionManager = new SessionManager(this);
       // setData();
        //getFilter();
        // Restore instance state
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        // Assign views
        mPhoneNumberViews = findViewById(R.id.phone_auth_fields);
        //mSignedInViews = findViewById(R.id.signed_in_buttons);

        mStatusText = findViewById(R.id.status);
        mDetailText = findViewById(R.id.detail);

        mPhoneNumberField = findViewById(R.id.field_phone_number);
        mVerificationField = findViewById(R.id.field_verification_code);

        mStartButton = findViewById(R.id.button_start_verification);
        mVerifyButton = findViewById(R.id.button_verify_phone);
        mResendButton = findViewById(R.id.button_resend);
        //mSignOutButton = findViewById(R.id.sign_out_button);

        // Assign click listeners
        mStartButton.setOnClickListener(this);
        mVerifyButton.setOnClickListener(this);
        mResendButton.setOnClickListener(this);

        mVerifyButton.setVisibility(View.INVISIBLE);
        mResendButton.setVisibility(View.INVISIBLE);
        //mSignOutButton.setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;

                mVerifyButton.setVisibility(View.VISIBLE);
                mResendButton.setVisibility(View.VISIBLE);
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
               // updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
               // updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);
                mVerifyButton.setVisibility(View.VISIBLE);
                mResendButton.setVisibility(View.VISIBLE);
                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
               // updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]
    }

    private boolean found=false;
    private UserBean movie ;
    private void getFilter()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Query query = reference.child("lagel/data/").orderByChild("phone").equalTo("9999999");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        // do with your result
                        Log.i("LOG","");

                        movie = issue.getValue(UserBean.class);

                        //movie = dataSnapshot.getValue(UserBean.class);
                        if (movie.getPassword().equals("12345")) {
                            found=true;
                            break;
                        }

                    }

                    if (found)
                    {
                        Log.i("LOG","Found");
                    }
                    else
                    {
                        Log.i("LOG","Not Found");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void setData()
    {
        if (FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOnline();
        }

        FirebaseDatabase mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'users' node
        DatabaseReference mFirebaseDatabase = mFirebaseInstance.getReference("lagel/data/");
        String userId =  mFirebaseDatabase.push().getKey();

        UserBean userBean = new UserBean();
        userBean.setEmail(email);
        userBean.setName(fullName);
        userBean.setPassword(password);
        userBean.setPhone(phoneNumber);

        mFirebaseDatabase.child(userId).setValue(userBean);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("LOG",""+dataSnapshot.toString());
                // Get Post object and use the values to update the UI
                //User user = dataSnapshot.getValue(User.class);
                //Intent i = getIntent();
                //i.putExtra("rubro", rubro);
                // setResult(RESULT_OK, i);
                //Toast.makeText(InsercionActivity.this, "Se Guardo Nuevo Registro\na  ...", Toast.LENGTH_LONG).show();
                exit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("DTS", "loadPost:onCancelled", databaseError.toException());
            }


        };
        mFirebaseDatabase.addValueEventListener(postListener);

    }

    private void exit()
    {
        if (FirebaseDatabase.getInstance()!=null)
        {
            FirebaseDatabase.getInstance().goOffline();
        }

        finish();
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

        // [START_EXCLUDE]
        if (mVerificationInProgress && validatePhoneNumber()) {
            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
        }
        // [END_EXCLUDE]
    }
    // [END on_start_check_user]

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_VERIFY_IN_PROGRESS, mVerificationInProgress);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationInProgress = savedInstanceState.getBoolean(KEY_VERIFY_IN_PROGRESS);
    }


    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }
    // [END resend_verification]

    private String signupType="3";
    private SessionManager mSessionManager;
    private String signUpType = "", accessToken = "", googleId = "1234", email="dtol_1@gmail.com", username = "", password = "",
            facebookId = "", currentLat = "", currentLng = "", address = "", phoneNumber = "", fullName = "", profilePicUrl = "";

    private void registerInLagel()
    {
            if (CommonClass.isNetworkAvailable(PhoneAuthActivity.this)) {
                JSONObject request_Data = new JSONObject();


                char[] chars1 = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
                StringBuilder sb1 = new StringBuilder();
                Random random1 = new Random();
                for (int i = 0; i < 6; i++)
                {
                    char c1 = chars1[random1.nextInt(chars1.length)];
                    sb1.append(c1);
                }
                //String random_string = sb1.toString()+"@gmail.com";
                String random_string = sb1.toString();

                username=random_string;
                fullName=eT_fullName.getText().toString();
                password=eT_password.getText().toString();
                email=random_string+"@mail.com";
                phoneNumber=mPhoneNumberField.getText().toString();
                phoneNumber=phoneNumberTest;

                try {
                    request_Data.put("signupType",signupType);   // mandatory
                    request_Data.put("username", username); // mandatory
                    request_Data.put("deviceType", VariableConstants.DEVICE_TYPE); // mandatory
                    request_Data.put("pushToken",mSessionManager.getPushToken()); // mandatory
                    request_Data.put("deviceId",mSessionManager.getDeviceId()); // mandatory
                    request_Data.put("profilePicUrl", profilePicUrl);
                    request_Data.put("fullName",fullName);
                    request_Data.put("location",address);
                    request_Data.put("latitude", currentLat);
                    request_Data.put("longitude", currentLng);
                    request_Data.put("password", password); // mandatory
                    request_Data.put("phoneNumber",phoneNumber); // mandatory
                    request_Data.put("email",email); // mandatory
                    //request_Data.put("googleToken",googleToken); // mandatory when sign up with google
                    request_Data.put("googleToken","ya29.GlvvBPnlNioB1hN-F_aJIOhQKG8iy91yd0pt8M5z4E2ykapJbMlPgLHmPKwr2UHjtm54XPA2OgbYYVQqNy81hYphjMExFML4ampiMsGfM5rJ1jxBRCRgWEz5v-9V"); // mandatory when sign up with google
                    request_Data.put("googleId",googleId);   // mandatory when sign up with google
                    request_Data.put("facebookId",facebookId);
                    request_Data.put("accessToken",accessToken);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

                OkHttp3Connection.doOkHttp3Connection("", ApiUrl.SIGN_UP, OkHttp3Connection.Request_type.POST, request_Data, new OkHttp3Connection.OkHttp3RequestCallback() {
                    @Override
                    public void onSuccess(String result, String tag) {
                        System.out.println(TAG + " " + "sign up response=" + result);
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
                                VariableConstants.IS_TO_SHOW_START_BROWSING=true;
                                //initUserDetails(profilePicUrl,signUpMainPojo.getResponse().getMqttId(),email,signUpMainPojo.getResponse().getUsername(),signUpMainPojo.getResponse().getAuthToken());
                                // call this method to set device info to server
                                //logDeviceInfo(signUpMainPojo.getResponse().getAuthToken());
                                setData();
                                break;

                            // auth token expired
                        /*case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
*/
                            // error
                            default:
                                Log.i("LOG","Error");
                        }
                    }

                    @Override
                    public void onError(String error, String tag) {
                       Log.i("LOG",error);
                    }
                });
            }

    }

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Here call Api for Register
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // [START_EXCLUDE]
                           // updateUI(STATE_SIGNIN_SUCCESS, user);
                            registerInLagel();
                            // [END_EXCLUDE]
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
                           // updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    private void signOut() {
        mAuth.signOut();
        updateUI(STATE_INITIALIZED);
    }

    private void updateUI(int uiState) {
        updateUI(uiState, mAuth.getCurrentUser(), null);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            updateUI(STATE_SIGNIN_SUCCESS, user);
        } else {
            updateUI(STATE_INITIALIZED);
        }
    }

    private void updateUI(int uiState, FirebaseUser user) {
        updateUI(uiState, user, null);
    }

    private void updateUI(int uiState, PhoneAuthCredential cred) {
        updateUI(uiState, null, cred);
    }

    private void updateUI(int uiState, FirebaseUser user, PhoneAuthCredential cred) {
        switch (uiState) {
            case STATE_INITIALIZED:
                // Initialized state, show only the phone number field and start button
                enableViews(mStartButton, mPhoneNumberField);
                disableViews(mVerifyButton, mResendButton, mVerificationField);
                mDetailText.setText(null);
                break;
            case STATE_CODE_SENT:
                // Code sent state, show the verification field, the
                enableViews(mVerifyButton, mResendButton, mPhoneNumberField, mVerificationField);
                disableViews(mStartButton);
                mDetailText.setText(R.string.status_code_sent);
                break;
            case STATE_VERIFY_FAILED:
                // Verification has failed, show all options
                enableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_failed);
                break;
            case STATE_VERIFY_SUCCESS:
                // Verification has succeeded, proceed to firebase sign in
                disableViews(mStartButton, mVerifyButton, mResendButton, mPhoneNumberField,
                        mVerificationField);
                mDetailText.setText(R.string.status_verification_succeeded);

                // Set the verification text based on the credential
                if (cred != null) {
                    if (cred.getSmsCode() != null) {
                        mVerificationField.setText(cred.getSmsCode());
                    } else {
                        mVerificationField.setText(R.string.instant_validation);
                    }
                }

                break;
            case STATE_SIGNIN_FAILED:
                // No-op, handled by sign-in check
                mDetailText.setText(R.string.status_sign_in_failed);
                break;
            case STATE_SIGNIN_SUCCESS:
                // Np-op, handled by sign-in check
                break;
        }

        if (user == null) {
            // Signed out
            mPhoneNumberViews.setVisibility(View.VISIBLE);
            //mSignedInViews.setVisibility(View.GONE);

            mStatusText.setText(R.string.signed_out);
        } else {
            // Signed in
            //mPhoneNumberViews.setVisibility(View.GONE);
            //mSignedInViews.setVisibility(View.VISIBLE);

            enableViews(mPhoneNumberField, mVerificationField);
            mPhoneNumberField.setText(null);
            mVerificationField.setText(null);

            mStatusText.setText(R.string.signed_in);
            mDetailText.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();

        if (eT_fullName.getText().toString()!=null && eT_fullName.getText().toString().length()==0)
        {
            eT_fullName.setError("Enter FullName.");
            return false;
        }

        if (eT_password.getText().toString()!=null && eT_password.getText().toString().length()==0)
        {
            eT_password.setError("Enter Password.");
            return false;
        }



        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }


        return true;
    }

    private void enableViews(View... views) {
        for (View v : views) {
            v.setEnabled(true);
        }
    }

    private void disableViews(View... views) {
        for (View v : views) {
            v.setEnabled(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_start_verification:
                if (!validatePhoneNumber()) {
                    return;
                }

                String phoneNumber = mPhoneNumberField.getText().toString();
                phoneNumber =phoneNumberTest;
                phoneNumberCheckApi(phoneNumber);


                break;
            case R.id.button_verify_phone:
                String code = mVerificationField.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    mVerificationField.setError("Cannot be empty.");
                    return;
                }

                verifyPhoneNumberWithCode(mVerificationId, code);
                break;
            case R.id.button_resend:
                resendVerificationCode(mPhoneNumberField.getText().toString(), mResendToken);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.tV_click_here:


                Intent redirectIntent = new Intent(this, LoginOrSignupActivity.class);
                redirectIntent.putExtra("type", "login");
                startActivityForResult(redirectIntent, VariableConstants.LANDING_REQ_CODE);
                break;
        }
    }

    private void phoneNumberCheckApi(String phone) {
        if (CommonClass.isNetworkAvailable(this)) {
            progress_bar_ph.setVisibility(View.VISIBLE);

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
                            //iV_error_ph.setVisibility(View.VISIBLE);
                            //iV_error_ph.setImageResource(R.drawable.rightusername);
                            startPhoneNumberVerification(mPhoneNumberField.getText().toString());
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(PhoneAuthActivity.this);
                            break;

                        // error
                        default:
                            isPhoneRegistered = true;
                            //iV_error_ph.setVisibility(View.VISIBLE);
                            //iV_error_ph.setImageResource(R.drawable.error_icon);
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

    private TextView tV_by_signing_up;
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
        spannableString.setSpan(new PhoneAuthActivity.MyClickableSpan(true, terms), s1.length() + 1, s1.length() + s2.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new PhoneAuthActivity.MyClickableSpan(true, privacy), s1.length() + 1 + s2.length() + 1 + s3.length() + 1, s1.length() + 1 + s2.length() + 1 + s3.length() + 1 + s4.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tV_by_signing_up.setText(spannableString);
        tV_by_signing_up.setMovementMethod(LinkMovementMethod.getInstance());
        tV_by_signing_up.setHighlightColor(Color.BLACK);
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
            ds.setColor(ContextCompat.getColor(PhoneAuthActivity.this, R.color.landing_page_black));
            ds.setFakeBoldText(false);
        }
    }



    }