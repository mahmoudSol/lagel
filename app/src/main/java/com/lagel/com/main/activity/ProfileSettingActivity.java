package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.DialogBox;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

/**
 * <h>ProfileSettingActivity</h>
 * <p>
 *     In this class we used to show severeal options to the user like finding friends,
 *     edit profile, logout etc
 * </p>
 * @since 4/11/2017
 */
public class ProfileSettingActivity extends Activity implements View.OnClickListener
{
    private DialogBox dialogBox;
    private Activity mActivity;
    private SessionManager mSessionManager;
    private NotificationMessageDialog mNotificationMessageDialog;
    private String payPalLink="";
    private boolean isPaypalVerified,isToStartActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setting);
        overridePendingTransition(R.anim.slide_up, R.anim.stay );

        mActivity = ProfileSettingActivity.this;
        isToStartActivity = true;

        // receive datas from last class
        Intent intent = getIntent();
        if (intent!=null)
            payPalLink= intent.getStringExtra("payPalLink");

        mNotificationMessageDialog=new NotificationMessageDialog(mActivity);
        CommonClass.statusBarColor(mActivity);
        mSessionManager=new SessionManager(mActivity);
        dialogBox=new DialogBox(mActivity);
        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        TextView tV_logout = (TextView) findViewById(R.id.tV_logout);
        tV_logout.setOnClickListener(this);

        // edit profile
        RelativeLayout rL_edit_people= (RelativeLayout) findViewById(R.id.rL_edit_people);
        rL_edit_people.setOnClickListener(this);

        // find facebook friends
        RelativeLayout rL_fb_friends= (RelativeLayout) findViewById(R.id.rL_fb_friends);
        rL_fb_friends.setOnClickListener(this);

        // find contect friends
        RelativeLayout rL_contact_friends= (RelativeLayout) findViewById(R.id.rL_contact_friends);
        rL_contact_friends.setOnClickListener(this);

        // My payemnt
        RelativeLayout rL_my_payment= (RelativeLayout) findViewById(R.id.rL_my_payment);
        rL_my_payment.setOnClickListener(this);

        //Language Support
        RelativeLayout rL_language= (RelativeLayout) findViewById(R.id.rL_language);
        rL_language.setOnClickListener(this);

        // Report problem
        RelativeLayout rL_report_problem= (RelativeLayout) findViewById(R.id.rL_report_problem);
        rL_report_problem.setOnClickListener(this);

        // privacy policy
        RelativeLayout rL_privacy= (RelativeLayout) findViewById(R.id.rL_privacy);
        rL_privacy.setOnClickListener(this);

        // Terms and condition
        RelativeLayout rL_termsNcondition= (RelativeLayout) findViewById(R.id.rL_termsNcondition);
        rL_termsNcondition.setOnClickListener(this);

        // set app version
        TextView tV_version= (TextView) findViewById(R.id.tV_version);
        try {
            String versionName = getResources().getString(R.string.version)+" "+mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), 0).versionName;
            if (!versionName.isEmpty())
            tV_version.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        isToStartActivity = true;
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
    public void onBackPressed()
    {
        Intent intent = new Intent();
        intent.putExtra("isPaypalVerified",isPaypalVerified);
        intent.putExtra("payPalLink",payPalLink);
        setResult(VariableConstants.PAYPAL_REQ_CODE,intent);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            // Log out
            case R.id.tV_logout :
                dialogBox.showLogoutAlert();
                break;

            // Back button
            case R.id.rL_back_btn :
                onBackPressed();
                break;

            // edit profile
            case R.id.rL_edit_people :
                if (isToStartActivity) {
                    startActivity(new Intent(mActivity, EditProfileActivity.class));
                    isToStartActivity = false;
                }
                break;

            // find friends from facebook
            case R.id.rL_fb_friends :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, FacebookFriendsActivity.class);
                    startActivity(intent);
                    isToStartActivity = false;
                }
                break;

            // find friends from contects
            case R.id.rL_contact_friends :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, PhoneContactsActivity.class);
                    startActivity(intent);
                    isToStartActivity = false;
                }
                break;

            // connect paypal
            case R.id.rL_my_payment :
                if (isToStartActivity) {
                    intent = new Intent(mActivity, ConnectPaypalActivity.class);
                    intent.putExtra("payPalLink", payPalLink);
                    startActivityForResult(intent, VariableConstants.PAYPAL_REQ_CODE);
                    isToStartActivity = false;
                }
                break;

            // report problem
            case R.id.rL_report_problem :
                CommonClass.sendEmail(mActivity,getResources().getString(R.string.report_email),mSessionManager.getUserFullName(),getResources().getString(R.string.problem_on_yelo));
                break;

            // privacy policy
            case R.id.rL_privacy :
                if (isToStartActivity) {
                String privacyLink = getResources().getString(R.string.privacyPolicyUrl);
                if (!privacyLink.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyLink));
                    startActivity(browserIntent);
                    isToStartActivity = false;
                }}
                break;

            // Terms and condition
            case R.id.rL_termsNcondition :
                if (isToStartActivity) {
                String termsNcondition = getResources().getString(R.string.termsNconditionsUrl);
                if (!termsNcondition.isEmpty()) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(termsNcondition));
                    startActivity(browserIntent);
                    isToStartActivity = false;
                }
                }
                break;

            //Language Support
            case R.id.rL_language :
                if(isToStartActivity){
                    startActivity(new Intent(mActivity,LanguageSelectActivity.class));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null)
        {
            switch (requestCode)
            {
                // Paypal verified link
                case VariableConstants.PAYPAL_REQ_CODE :
                    isPaypalVerified=data.getBooleanExtra("isPaypalVerified",false);
                    payPalLink= data.getStringExtra("payPalLink");
                    break;
            }
        }
    }
}
