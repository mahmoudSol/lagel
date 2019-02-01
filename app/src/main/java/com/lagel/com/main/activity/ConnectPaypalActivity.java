package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.event_bus.BusProvider;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;
import com.lagel.com.pojo_class.verify_paypal_link.VerifyPaypalMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>ConnectPaypalActivity</h>
 * @since 20-Jul-17
 */
public class ConnectPaypalActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = ConnectPaypalActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootview,rL_apply;
    private EditText eT_yourName;
    private boolean isToSave;
    private ProgressBar progress_bar_save;
    private TextView tV_save;
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_paypal);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        initVariables();
    }

    /**
     * <h>InitVariables</h>
     * <p>
     *     In this method we used to initialize all variables.
     * </p>
     */
    private void initVariables()
    {
        mActivity=ConnectPaypalActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        mSessionManager=new SessionManager(mActivity);
        isToSave=false;
        BusProvider.getInstance().register(this);

        // root element
        rL_rootview= (RelativeLayout) findViewById(R.id.rL_rootview);
        rL_apply= (RelativeLayout) findViewById(R.id.rL_apply);
        rL_apply.setOnClickListener(this);
        progress_bar_save= (ProgressBar) findViewById(R.id.progress_bar_save);
        tV_save= (TextView) findViewById(R.id.tV_save);
        CommonClass.setViewOpacity(mActivity,rL_apply,102,R.color.status_bar_color);

        // set name
        eT_yourName= (EditText) findViewById(R.id.eT_yourName);
        eT_yourName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(eT_yourName.getText()))
                {
                    CommonClass.setViewOpacity(mActivity,rL_apply,255,R.color.status_bar_color);
                    isToSave=true;
                }
                else {
                    CommonClass.setViewOpacity(mActivity,rL_apply,102,R.color.status_bar_color);
                    isToSave=false;
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // set paypal verified name
        Intent intent = getIntent();
        if (intent!=null)
        {
            String payPalLink = intent.getStringExtra("payPalLink");
            System.out.println(TAG+" "+"paypalUrl="+payPalLink);

            if (payPalLink!=null && !payPalLink.isEmpty())
            {
                int index=payPalLink.lastIndexOf('/');
                System.out.println(TAG+" "+"paypal verified name="+payPalLink.substring(index+1,payPalLink.length()));
                String paypalVerifiedName=payPalLink.substring(index+1,payPalLink.length());
                if (!paypalVerifiedName.isEmpty())
                    eT_yourName.setText(paypalVerifiedName);
            }
        }

        // back button
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // get paypal
        RelativeLayout rL_getPaypal= (RelativeLayout) findViewById(R.id.rL_getPaypal);
        rL_getPaypal.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            // back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // get paypal
            case R.id.rL_getPaypal :
                Intent intent=new Intent(mActivity,WebViewActivity.class);
                intent.putExtra("browserLink", VariableConstants.GET_PAYPAL_LINK);
                intent.putExtra("actionBarTitle", getResources().getString(R.string.get_paypal));
                startActivity(intent);
                break;

            // save
            case R.id.rL_apply :
                if (isToSave)
                    linkPaypalApi();
                break;
        }
    }

    private void linkPaypalApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            progress_bar_save.setVisibility(View.VISIBLE);
            tV_save.setVisibility(View.GONE);
            final String paypalUrl=getResources().getString(R.string.connect_paypal_link)+eT_yourName.getText().toString();
            JSONObject request_datas = new JSONObject();
            try {
                request_datas.put("token", mSessionManager.getAuthToken());
                request_datas.put("paypalUrl", paypalUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.LINK_PAYPAL, OkHttp3Connection.Request_type.PUT, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    progress_bar_save.setVisibility(View.GONE);
                    tV_save.setVisibility(View.VISIBLE);
                    VerifyPaypalMainPojo verifyPaypalMainPojo;
                    Gson gson=new Gson();
                    verifyPaypalMainPojo=gson.fromJson(result,VerifyPaypalMainPojo.class);
                    switch (verifyPaypalMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            CommonClass.showSuccessSnackbarMsg(rL_rootview,verifyPaypalMainPojo.getMessage());
                            mSessionManager.setUserPayPal(paypalUrl);
                            BusProvider.getInstance().post(paypalUrl);
                            final Timer t = new Timer();
                            t.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // when the task active then close the activity
                                    t.cancel();
                                    Intent intent=new Intent();
                                    intent.putExtra("isPaypalVerified",true);
                                    intent.putExtra("payPalLink",paypalUrl);
                                    setResult(VariableConstants.PAYPAL_REQ_CODE,intent);
                                    onBackPressed();
                                }
                            }, 3000);
                            break;
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            break;
                        default:
                            CommonClass.showSnackbarMessage(rL_rootview,verifyPaypalMainPojo.getMessage());
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    progress_bar_save.setVisibility(View.GONE);
                    tV_save.setVisibility(View.VISIBLE);
                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootview,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed()
    {
        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    public void hideKeyboard()
    {
        try
        {
            InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(tV_save.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    /**
     * <h>ShowKeyboard</h>
     * <p>
     *     In this method we used to open device keypad when user
     *     click on search iocn and close when user click close
     *     search button.
     * </p>
     * @param flag This is integer valuew to open or close keypad
     */
    private void showKeyboard(int flag) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(flag,0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }
}
