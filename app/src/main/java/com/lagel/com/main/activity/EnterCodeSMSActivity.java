package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
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

import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.pojo_class.ForgotPasswordPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

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
public class EnterCodeSMSActivity extends AppCompatActivity implements View.OnClickListener
{
    private Activity mActivity;
    private EditText eT_emailId,eT_loginUserName,eT_password,eT_password2;
    private static final String TAG=EnterCodeSMSActivity.class.getSimpleName();
    private TextView tV_send,tV_sms_number;
    private boolean isSendButtonEnabled;
    private ProgressBar mProgress_bar;
    private LinearLayout linear_rootElement,rL11_change_password;
    private RelativeLayout rL_change_password;
    private String value;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smscode_pass);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        Bundle b = getIntent().getExtras();
        value = ""; // or other values
        if(b != null)
        {
            value = b.getString("phone");
        }

        rL11_change_password= (LinearLayout) findViewById(R.id.rL11_change_password);
        rL_change_password= (RelativeLayout) findViewById(R.id.rL_change_password);
        rL_change_password.setOnClickListener(this);

        eT_loginUserName = (EditText) findViewById(R.id.eT_loginUserName);
        eT_loginUserName.setInputType(InputType.TYPE_CLASS_NUMBER);

        eT_password= (EditText) findViewById(R.id.eT_password);
        eT_password2= (EditText) findViewById(R.id.eT_password2);

        // request keyboard
        getWindow().setSoftInputMode (WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        initVariables();

        String message=getString(R.string.label_enter_sms_code_title)+" "+value;
        tV_sms_number.setText(message);
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to assign all the xml variables and data member like mActivity.
     * </p>
     */
    private void initVariables()
    {
        mActivity=EnterCodeSMSActivity.this;
        CommonClass.statusBarColor(mActivity);

        mProgress_bar= (ProgressBar) findViewById(R.id.progress_bar);
        linear_rootElement= (LinearLayout) findViewById(R.id.linear_rootElement);
        final RelativeLayout rL_send,rL_back_btn;
        rL_send= (RelativeLayout) findViewById(R.id.rL_send);
        rL_send.setOnClickListener(this);
        rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        tV_send= (TextView) findViewById(R.id.tV_send);
        tV_sms_number= (TextView) findViewById(R.id.sms_number);

        CommonClass.setViewOpacity(mActivity,rL_send,102,R.drawable.rect_purple_color_with_solid_shape);

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
                isSendButtonEnabled=true;
                CommonClass.setViewOpacity(mActivity,rL_send,204,R.drawable.rect_purple_color_with_solid_shape);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        eT_emailId.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (isSendButtonEnabled)
                        if (eT_emailId!=null && eT_emailId.getText().toString().length()>0)
                            verifiedOtp();
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

            case R.id.rL_change_password:

                validatePassword();
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
                    verifiedOtp();
                break;


        }
    }

    private void validatePassword()
    {
        if (eT_password.getText().toString().length()>0 && eT_password2.getText().toString().length()>0)
        {

            if (eT_password.getText().toString().equalsIgnoreCase(eT_password2.getText().toString()))
            {
                changePassword();

            }
            else {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(EnterCodeSMSActivity.this);
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
            AlertDialog.Builder builder1 = new AlertDialog.Builder(EnterCodeSMSActivity.this);
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

    }

    /**
     * <h>ResetPasswordApi</h>
     * <p>
     *     In this method we used to do api call for forgot password.
     * </p>
     */
    private void verifiedOtp() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);

            //{"otp":"9827","phoneNumber":"+50938234665"}
            JSONObject requestDatas = new JSONObject();
            try {
                requestDatas.put("phoneNumber", value);
                requestDatas.put("otp", eT_emailId.getText().toString());
            }catch (Exception ex)
            {
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.VERIFY_OTP, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
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
                            rL11_change_password.setVisibility(View.VISIBLE);


                            /*final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    //onBackPressed();
                                }
                            }, 3000);*/
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            if (forgotPasswordPojo!=null && forgotPasswordPojo.getMessage()!=null && forgotPasswordPojo.getMessage().length()==0)
                            {
                                CommonClass.showSnackbarMessage(linear_rootElement,getString(R.string.label_sms_error));
                                rL11_change_password.setVisibility(View.GONE);
                            }
                            else
                            {
                                CommonClass.showSnackbarMessage(linear_rootElement,forgotPasswordPojo.getMessage());
                                rL11_change_password.setVisibility(View.GONE);
                            }

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



    }


    private void changePassword() {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgress_bar.setVisibility(View.VISIBLE);
            tV_send.setVisibility(View.GONE);

            //{"otp":"9827","phoneNumber":"+50938234665","password":"12345","repeatPassword":"12345"}
            JSONObject requestDatas = new JSONObject();
            try {
                requestDatas.put("phoneNumber", value);
                requestDatas.put("password", eT_password.getText().toString());
                requestDatas.put("repeatPassword", eT_password.getText().toString());
                requestDatas.put("otp", eT_emailId.getText().toString());
            }catch (Exception ex)
            {
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.CHANGE_PASSWORD, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
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

                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    onBackPressed();
                                }
                            }, 3000);
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            if (forgotPasswordPojo!=null && forgotPasswordPojo.getMessage()!=null && forgotPasswordPojo.getMessage().length()==0)
                            {
                                CommonClass.showSnackbarMessage(linear_rootElement,getString(R.string.label_sms_error));
                                //rL11_change_password.setVisibility(View.GONE);
                            }
                            else
                            {
                                CommonClass.showSnackbarMessage(linear_rootElement,forgotPasswordPojo.getMessage());
                                //rL11_change_password.setVisibility(View.GONE);
                            }

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
}