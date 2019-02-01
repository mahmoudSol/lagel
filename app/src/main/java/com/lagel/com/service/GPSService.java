package com.lagel.com.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import com.lagel.com.BuildConfig;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.main.activity.SplashActivity;
import com.lagel.com.rest.ApiException;
import com.lagel.com.rest.RestException;
import com.lagel.com.rest.RestServiceClient;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * @author DavidT
 *
 */
public class GPSService extends Service {
	public GPSService() {

		// TODO Auto-generated constructor stub
	}

	/**GPS Code**/
	//private double lat,lon;
	private HashMap hashMap ;
	private String currLatitude ="";
	private String currLongitude="";
	private LocationManager lm;
	private long lMinTime;
	private Location loc;
	private LocationListener locListenD;
	private float fMinDistance=1000.0f;
	private int mSatellites = 0;
	protected double dCurrLatitude,dCurrLongitude;


	public static final String ACTION_SYNC = "ACTION_SYNC";
	public static final String ACTION_CONNECTIVITY = "com.smartfleet.driver.ACTION_CONNECTIVITY";
	public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
	private long INTERVAL;
	//private static final long INTERVAL = 24 * 60 * 60 * 1000L; // 24hrs
	private JobSync jobSync;
	private static final String USER_AGENT = "Android App %s";
	private boolean isNetworkConnected = true;
	private PowerManager.WakeLock wakeLock;
	public static final String TAG = GPSService.class.getSimpleName();

	private HashMap dataGPS;


	private RestServiceClient restClient = null;
	Handler HN = new Handler();


	private FusedLocationService locationService;
	private String lat,lng;
	SplashActivity activity;


	@Override
	public void onCreate() {
		super.onCreate();
		String userAgent = String.format(USER_AGENT, getAppVersion(GPSService.this));
		this.restClient = new RestServiceClient(userAgent);
		INTERVAL= 1000*60*20;  //20 min
		initJobs();
		jobSync.start();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void initJobs() {
		PendingIntent intentSync = getActionIntent(ACTION_SYNC);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		jobSync = new JobSync(alarmManager, intentSync, INTERVAL);
	}

	private PendingIntent getActionIntent(String action) {
		Intent intent = new ActionIntent(action, this, GPSService.class);
		return PendingIntent.getService(this, 0, intent, 0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		activity = SplashActivity.instance;
		if (activity!=null)
		{
			mSessionManager = new SessionManager(activity);
		}


		if (intent == null) {
			Log.w(TAG, "null intent supplied. service restarted.");
		} else if (ACTION_SYNC.equals(intent.getAction())) {
			Log.v(TAG, "service sync command is started");
			new Thread(jobSync, "HolderName sync job").start();

		} else if (ACTION_CONNECTIVITY.equals(intent.getAction())) {
			boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			updateNetworkConnectionState(noConnectivity);
		}

		super.onStartCommand(intent, startId, startId);
		return START_STICKY;
	}

	private void updateNetworkConnectionState(boolean noConnectivity) {
		if (!isNetworkConnected && !noConnectivity) {
			Log.v(TAG, "network connection is restored");
			jobSync.start();
		} else if (noConnectivity) {
			Log.v(TAG, "network connection is lost");
			jobSync.stop();
		} isNetworkConnected = !noConnectivity;
	}


	private class JobSync extends Job {
		public JobSync(AlarmManager am, PendingIntent pi, long interval) {
			super (am, pi, interval);
		}

		@Override
		public void run() {
			try {
				getLock().acquire();
				getCurrentLocation();
				//getData();

				if (Common.haveInternet(GPSService.this))
				{

					Log.e("DTS", "New Toast on UI Thread");
				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				getLock().release();
			}
		}


	};


	private SessionManager mSessionManager;
	private void getCurrentLocation() {
		locationService = new FusedLocationService(activity , new FusedLocationReceiver() {
			@Override
			public void onUpdateLocation() {
				if (locationService!=null)
				{
					Location currentLocation = locationService.receiveLocation();
					if (currentLocation != null) {
						lat = String.valueOf(currentLocation.getLatitude());
						lng = String.valueOf(currentLocation.getLongitude());

						if (isLocationFound(lat, lng)) {
							if (mSessionManager!=null)
							{
								mSessionManager.setCurrentLat(lat);
								mSessionManager.setCurrentLng(lng);
							}
						}
					}
					else
					{
						Log.i("TAG","");
					}
				}

			}
		}
		);
	}

	private boolean isLocationFound(String lat, String lng) {
		return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
	}

	private InputStream runPostForm(String url,GeoPunchBean gp) throws ApiException, FileNotFoundException {
		try {
			InputStream isRaw = restClient.postBinaryForm(url,gp);
			return isRaw;
		} catch (RestException e) {
			throw new ApiException("Failed to run GetAllView" , e);
		}
	}


	private InputStream runPostMultimedia(String url,GeoPunchBean gp) throws ApiException, FileNotFoundException {
		try {
			InputStream isRaw = restClient.postMultimedia(url,gp);
			return isRaw;
		} catch (RestException e) {
			throw new ApiException("Failed to run GetAllView" , e);
		}
	}

	public final boolean isInternetOn()
	{
		ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET
		if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED )
		{
			// MESSAGE TO SCREEN FOR TESTING (IF REQ)
			//Toast.makeText(this, connectionType + � connected�, Toast.LENGTH_SHORT).show();
			return true;
		}
		else if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED
				||  connec.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED  )
		{
			return false;
		}

		return false;
	}



	public Boolean handleForm(InputStream is) throws IOException, JSONException {
		String result = null,id,success=null;
		boolean value=false;
		//{"success":"Record Inserted!","status":1}



		result = Common.convertStreamToString(is);
		Log.i("Result save assesment ", result);

		JSONObject root = new JSONObject(result);

		if (!root.isNull("success") &&  root.getString("success")!=null)
		{
			success=root.getString("success");

			//FormularioDatabase.instance(OfflineService.this).deleteByID(""+idPK);

		}

		return value;
	}

	public Boolean handleFormFile(InputStream is) throws IOException, JSONException {
		String result = null,id,success=null;
		boolean value=false;
		//{"success":"Record Inserted!","status":1}

		result = Common.convertStreamToString(is);
		Log.i("Result save assesment ", result);

		JSONObject root = new JSONObject(result);

		if (!root.isNull("success") &&  root.getString("success")!=null)
		{
			success=root.getString("success");

			//FormularioFileDatabase.instance(OfflineService.this).deleteByID(""+idPKFile);

		}

		return value;
	}


	private InputStream runPostPushGeoPunch(String url,GeoPunchBean gp) throws ApiException, FileNotFoundException {
		try {
			InputStream isRaw = restClient.postBinaryGeoPunch(url,gp);
			return isRaw;
		} catch (RestException e) {
			throw new ApiException("Failed to run GetAllView" , e);
		}
	}




	private InputStream runPostPush(String url,String json) throws ApiException, FileNotFoundException {
		try {
			InputStream isRaw = restClient.postBinary2(url,json);
			return isRaw;
		} catch (RestException e) {
			throw new ApiException("Failed to run GetAllView" , e);
		}
	}

	public static String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	private String getAppVersion(Context context) {
		String appVersion = "";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			appVersion = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return appVersion;
	}



	synchronized private PowerManager.WakeLock getLock() {
		if (wakeLock == null) {
			PowerManager mgr = (PowerManager) getSystemService(Context.POWER_SERVICE);
			wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		}
		return wakeLock;
	}

	protected boolean haveInternet(){
		NetworkInfo info=(NetworkInfo)( (ConnectivityManager)getSystemService( Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (info == null || !info.isConnected()) return false;
		if (info.isRoaming()) return true;
		return true;
	}


	public Boolean handleGPS2(InputStream is) throws IOException, JSONException {
		String result = null,id,success=null;
		boolean value=false;
		//{"user":{"id":"2","login":"test","fname":"Admin","lname":"DD","datetime":"2016-10-13 00:00:00","status":"0"}}

		result = Common.convertStreamToString(is);
		Log.i("Result save assesment ", result);

		JSONObject root = new JSONObject(result);

		if (!root.isNull("success") &&  root.getString("success")!=null)
		{
			success=root.getString("success");

			//GPSDatabase.instance(ProductService.this).deleteByID(""+idPKFile);

		}

		return value;
	}



	private class DisplayToast implements Runnable {

		String TM = "";

		public DisplayToast(String toast){
			TM = toast;
		}

		public void run(){
			Toast.makeText(getApplicationContext(), TM, Toast.LENGTH_SHORT).show();
		}
	}
}