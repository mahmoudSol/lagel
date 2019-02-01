package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.pojo_class.ForgotPasswordPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import co.simplecrop.android.simplecropimage.CropImage;

/**
 * <h>ForgotPasswordActivity</h>
 * <p>
 *     This class is called from Login screen. In this class we used to find the
 *     forgotten password using user email-id.
 * </p>
 * @since 4/5/2017
 * @author 3Embed
 * @version 1.0
 */
public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private EditText eT_emailId,eT_loginUserName;
    private static final String TAG=ForgotPasswordActivity.class.getSimpleName();
    private TextView tV_send,v_cod_country;
    private boolean isSendButtonEnabled;
    private ProgressBar mProgress_bar;
    private LinearLayout linear_rootElement;
    private int value;
    private boolean selectPhone;
    private View login_views, signup_views,v_1_phone,v_1_email;
    private RelativeLayout rL1_email,rL1_phone,rL_sms_code;
    private String sphone;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        selectPhone=true;
        Bundle b = getIntent().getExtras();
        value = -1; // or other values
        if(b != null)
        {
            value = b.getInt("key");
        }

        v_cod_country = (TextView) findViewById(R.id.v_cod_country );
        v_cod_country.setOnClickListener(this);

        v_1_phone= (View)findViewById(R.id.v_1_phone);
        rL1_email= (RelativeLayout)findViewById(R.id.rL1_email);
        rL1_email.setOnClickListener(this);


        v_1_email= (View)findViewById(R.id.v_1_email);
        rL1_phone= (RelativeLayout)findViewById(R.id.rL1_phone);
        rL1_phone.setOnClickListener(this);

        rL_sms_code= (RelativeLayout)findViewById(R.id.rL_sms_code);
        rL_sms_code.setOnClickListener(this);

        eT_loginUserName = (EditText) findViewById(R.id.eT_loginUserName);
        eT_loginUserName.setInputType(InputType.TYPE_CLASS_NUMBER);
        v_cod_country= (TextView)findViewById(R.id.v_cod_country);
        // request keyboard
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to assign all the xml variables and data member like mActivity.
     * </p>
     */
    private void initVariables()
    {
        mActivity=ForgotPasswordActivity.this;
        CommonClass.statusBarColor(mActivity);

        mProgress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);
        final RelativeLayout rL_send,rL_back_btn;
        rL_send= (RelativeLayout) findViewById(R.id.rL_send);
        rL_send.setOnClickListener(this);
        rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        tV_send= (TextView) findViewById(R.id.tV_send);

        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);
        CommonClass.setViewOpacity(mActivity,rL_sms_code,102,R.drawable.rect_purple_color_with_solid_shape);

        // EditText Email Address
        eT_emailId= (EditText) findViewById(R.id.eT_loginUserName);
        eT_emailId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                String email=eT_emailId.getText().toString();
                if (!selectPhone)
                {
                    if (!email.isEmpty() && CommonClass.isValidEmail(email))
                    {
                        isSendButtonEnabled=true;
                        CommonClass.setViewOpacity(mActivity,rL_send,204,R.drawable.rect_purple_color_with_solid_shape);

                        CommonClass.setViewOpacity(mActivity,rL_sms_code,204,R.drawable.rect_purple_color_with_solid_shape);
                    }
                    else
                    {
                        //isSendButtonEnabled=false;
                        isSendButtonEnabled=true;
                        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);
                        CommonClass.setViewOpacity(mActivity,rL_sms_code,102,R.drawable.rect_purple_color_with_solid_shape);

                    }
                }
                else
                {
                    isSendButtonEnabled=true;
                    CommonClass.setViewOpacity(mActivity,rL_sms_code,204,R.drawable.rect_purple_color_with_solid_shape);
                    CommonClass.setViewOpacity(mActivity,rL_send,204,R.drawable.rect_purple_color_with_solid_shape);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_emailId.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (isSendButtonEnabled)
                        if (eT_emailId.getText().toString()!=null && eT_emailId.getText().toString().length()>0)
                            resetPasswordApi();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_sms_code:
                Intent intent2 = new Intent(mActivity, EnterCodeSMSActivity.class);
                Bundle b = new Bundle();
                b.putString("phone", sphone+""+eT_loginUserName.getText().toString()); //Your id
                intent2.putExtras(b); //Put your id to your next Intent
                startActivityForResult(intent2, 0);

                break;

            case R.id.v_cod_country:
                Intent intent1 = new Intent(mActivity, CountrySearchActivity.class);
                startActivityForResult(intent1, VariableConstants.SEARCH_COUNTRY);
                break;

            // back button click
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // call reset password api
            case R.id.rL_send :
                if (isSendButtonEnabled)
                    resetPasswordApi();
                break;


            case R.id.rL1_phone:
                v_1_phone.setBackgroundColor(Color.BLACK);
                v_1_email.setBackgroundColor(Color.parseColor("#959595"));
                v_cod_country.setVisibility(View.VISIBLE);
                eT_loginUserName.setHint(R.string.phone_label);
                eT_loginUserName.setText("");
                eT_loginUserName.setInputType(InputType.TYPE_CLASS_NUMBER);
                selectPhone=true;
                rL_sms_code.setVisibility(View.VISIBLE);
                break;

            case R.id.rL1_email:
                v_1_phone.setBackgroundColor(Color.parseColor("#959595"));
                v_1_email.setBackgroundColor(Color.BLACK);
                v_cod_country.setVisibility(View.GONE);
                eT_loginUserName.setHint(R.string.email_label);
                eT_loginUserName.setText("");
                eT_loginUserName.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                rL_sms_code.setVisibility(View.GONE);
                selectPhone=false;
                break;

        }
    }

    /**
     * <h>ResetPasswordApi</h>
     * <p>
     *     In this method we used to do api call for forgot password.
     * </p>
     */
    private void resetPasswordApi() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);
            JSONObject requestDatas = new JSONObject();

            try {
                if (selectPhone) {

                    if (sphone!=null && sphone.length()>0)
                    {

                    }
                    else
                    {
                        String[] separated =v_cod_country.getText().toString() .split("\\+");
                        if (separated !=null && separated .length>0)
                        {
                            sphone="+"+separated[1];
                        }
                    }

                    String sPhone=sphone+""+eT_emailId.getText().toString();
                    requestDatas.put("type", "1");
                    requestDatas.put("phoneNumber", sPhone);

                }
                else
                {
                    requestDatas.put("type", "0");
                    requestDatas.put("email", eT_emailId.getText().toString());
                }

                //requestDatas.put("email", eT_emailId.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.RESET_PASSWORD, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"reset password res="+result);
                    mProgress_bar.setVisibility(View.GONE);
                    tV_send.setVisibility(View.VISIBLE);
                    ForgotPasswordPojo forgotPasswordPojo;
                    Gson gson=new Gson();
                    forgotPasswordPojo=gson.fromJson(result,ForgotPasswordPojo.class);

                    switch (forgotPasswordPojo.getCode())
                    {
                        // success
                        case "200" :
                            mProgress_bar.setVisibility(View.GONE);
                            CommonClass.showSuccessSnackbarMsg(linear_rootElement,forgotPasswordPojo.getMessage());

                            /*final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    onBackPressed();
                                }
                            }, 3000);*/
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            CommonClass.showSnackbarMessage(linear_rootElement,forgotPasswordPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    CommonClass.showSnackbarMessage(linear_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(linear_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed()
    {
        showKeyboard(InputMethodManager.HIDE_IMPLICIT_ONLY);
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    private void showKeyboard(int flag)
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(flag,0);
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

                String[] separated =v_cod_country.getText().toString() .split("\\+");
                if (separated !=null && separated .length>0)
                {
                    sphone="+"+separated[1];
                }

                Log.i("TAG",returnValue);
            }
        }
    }
}