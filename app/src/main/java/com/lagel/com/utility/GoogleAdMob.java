package com.lagel.com.utility;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.lagel.com.R;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <h>GoogleAdMob</h>
 * <p>
 *     In this class we used to initialize google mob ad. for every 4 minute.
 * </p>
 * @since 02-Sep-17.
 */
public class GoogleAdMob
{
    private Context mContext;
    private InterstitialAd mInterstitialAd;
    private SessionManager mSessionManager;
    private static final String TAG = GoogleAdMob.class.getSimpleName();

    public GoogleAdMob(Context mContext) {
        this.mContext = mContext;
        mSessionManager = new SessionManager(mContext);
        initializeInterstitialAdMob();
    }

    /**
     * <h>InitializeInterstitialAdMob</h>
     * <p>
     *     In this method we used to initialize MobileAds and show the ad.
     *     Once Interstitial screen will be closed by the used then again
     *     call showAdMob method from setAdListener.
     * </p>
     */
    private void initializeInterstitialAdMob()
    {
        showAdMob();
        mInterstitialAd = new InterstitialAd(mContext);
        mInterstitialAd.setAdUnitId(mContext.getResources().getString(R.string.adMobInterstialId));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener()
        {
            @Override
            public void onAdClosed()
            {
                super.onAdClosed();
                if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded())
                {
                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    showAdMob();
                }
            }
        });
    }

    /**
     * <h>ShowAdMob</h>
     * <p>
     *     In this method we used to set timer for 4-min once it completes then
     *     show interstitial google mob ad.
     * </p>
     */
    private void showAdMob()
    {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        System.out.println(TAG+" "+"is user logged in="+mSessionManager.getIsUserLoggedIn()+" "+"is in forground="+isAppIsInBackground(mContext));
                        if (mSessionManager.getIsUserLoggedIn() && !isAppIsInBackground(mContext)) {
                            if (mInterstitialAd.isLoaded()) {
                                mInterstitialAd.show();
                            } else {
                                mInterstitialAd.loadAd(new AdRequest.Builder().build());
                                System.out.println(TAG + " " + "The interstitial wasn't loaded yet.");
                            }
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 400000);
    }

    /**
     * Method checks if the app is in background or not
     */
    private boolean isAppIsInBackground(Context context)
    {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
