package com.lagel.com.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author DavidT
 *
 */
public class ProductService extends Service {
	public ProductService() {

		// TODO Auto-generated constructor stub
	}



	public static final String ACTION_SYNC = "ACTION_SYNC";
	public static final String ACTION_CONNECTIVITY = "com.smartfleet.driver.ACTION_CONNECTIVITY";
	public static final String EXTRA_NO_CONNECTIVITY = "noConnectivity";
	private long INTERVAL;
	//private static final long INTERVAL = 24 * 60 * 60 * 1000L; // 24hrs
	private JobSync jobSync;
	private static final String USER_AGENT = "Android App %s";
	private boolean isNetworkConnected = true;
	private PowerManager.WakeLock wakeLock;
	public static final String TAG = ProductService.class.getSimpleName();


	private RestServiceClient restClient = null;
	Handler HN = new Handler();


	SplashActivity activity;


	@Override
	public void onCreate() {
		super.onCreate();
		String userAgent = String.format(USER_AGENT, getAppVersion(ProductService.this));
		this.restClient = new RestServiceClient(userAgent);
		INTERVAL= 1000*60*60*2;  //2 hour
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
		Intent intent = new ActionIntent(action, this, ProductService.class);
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
				logGuestInfo();
				//getData();

				if (Common.haveInternet(ProductService.this))
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


	private void logGuestInfo()
	{
		try {
			JSONObject jsonObject = new JSONObject();

			jsonObject.put("deviceName", Build.BRAND);
			jsonObject.put("deviceId", mSessionManager.getDeviceId());
			jsonObject.put("deviceOs", Build.VERSION.RELEASE);
			jsonObject.put("modelNumber", Build.MODEL);
			jsonObject.put("appVersion", BuildConfig.VERSION_NAME);
			//{"offset":0,"limit":10,"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6Njg0MywibmFtZSI6Inl4b2VjeWV0IiwiYWNjZXNzS2V5IjoiNjIyMiIsImlhdCI6MTUyNTgyNTg3NiwiZXhwIjoxNTMxMDA5ODc2fQ.MNIqiF-no9MsB8m-9op0Dj_DNyzMdAO8B5azoiJCYqI","latitude":"18.5945443","longitude":"-72.3074343","pushToken":"eEmFq0_Q3JA:APA91bHN6f5OsDiz9BZsG2uK9XrogQVQOzSVRMvwXK5cc3598SCDJGC7Icyyeagtr1GYHlNm0lFOeKn8S8ciSVKrqls30uFN_xajb-25zOc8jD12JHfDSK1m1cqKh07hbf5BDrkXDKZy"}
			sendGuestInfo(this, jsonObject.toString());

		}catch (Exception e) {
			Log.e("Not data shared", e.toString());
		}
	}

	private void getData()
	{
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("offset", "0");
			jsonObject.put("limit", "10");
			//jsonObject.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6Njg0MywibmFtZSI6Inl4b2VjeWV0IiwiYWNjZXNzS2V5IjoiNjIyMiIsImlhdCI6MTUyNTgyNTg3NiwiZXhwIjoxNTMxMDA5ODc2fQ.MNIqiF-no9MsB8m-9op0Dj_DNyzMdAO8B5azoiJCYqI");
			jsonObject.put("token", mSessionManager.getAuthToken());
			jsonObject.put("longitude", "-72.3074343");
			jsonObject.put("latitude", "18.5945443");
			//jsonObject.put("CategoryName", "fashion & accessories");
			jsonObject.put("productName", "Clarinet");
			jsonObject.put("pushToken", "eEmFq0_Q3JA:APA91bHN6f5OsDiz9BZsG2uK9XrogQVQOzSVRMvwXK5cc3598SCDJGC7Icyyeagtr1GYHlNm0lFOeKn8S8ciSVKrqls30uFN_xajb-25zOc8jD12JHfDSK1m1cqKh07hbf5BDrkXDKZy");
			//jsonObject.put("pushToken", mSessionManager.getPushToken());


			//{"offset":0,"limit":10,"token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6Njg0MywibmFtZSI6Inl4b2VjeWV0IiwiYWNjZXNzS2V5IjoiNjIyMiIsImlhdCI6MTUyNTgyNTg3NiwiZXhwIjoxNTMxMDA5ODc2fQ.MNIqiF-no9MsB8m-9op0Dj_DNyzMdAO8B5azoiJCYqI","latitude":"18.5945443","longitude":"-72.3074343","pushToken":"eEmFq0_Q3JA:APA91bHN6f5OsDiz9BZsG2uK9XrogQVQOzSVRMvwXK5cc3598SCDJGC7Icyyeagtr1GYHlNm0lFOeKn8S8ciSVKrqls30uFN_xajb-25zOc8jD12JHfDSK1m1cqKh07hbf5BDrkXDKZy"}
			sendGPS(this, jsonObject.toString());

		}catch (Exception e) {
				Log.e("Not data shared", e.toString());
			}

	}

	public static void sendGuestInfo(Context ctx, String json){
		String response =null,status=null,data=null,message=null;
		boolean emailExists=false;
		try {
			String url = ApiUrl.LOG_GUEST;
			postBinaryAssesment(url,json);
			if (response!=null) {

			}
			//Log.e("DTS", response);
		}catch(Exception e)
		{
			Log.e("DTS", "Error: " + e.getMessage());
		}

	}


	public static void sendGPS(Context ctx, String json){
		String response =null,status=null,data=null,message=null;
		boolean emailExists=false;
		try {
			//String url ="https://lagel.net/api/allPosts/users/m";
			String url ="https://lagel.net/api/getProductsByName";
			postBinaryAssesment(url,json);
			if (response!=null) {

			}
			//Log.e("DTS", response);
		}catch(Exception e)
		{
			Log.e("DTS", "Error: " + e.getMessage());
		}

	}

	//public static InputStream postBinaryAssesment(String url, String json)
	public static void postBinaryAssesment(String url, String json)
	{
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/json");
			request.setHeader("Authorization", "Basic YmFzaWNBdXRoOiZqbm8tQDhhej13U28qTkhZVkdwRl5BUT80eW4zNlp2VzVUb1VDVU4rWEdPdUM/c3ojU0Ukb3hYVmJ3UUdQfDNXRnlqY1RBajJTSVJRbkxFfHZvXi18LUFUVjVGWlVmMio1QTNPaXV8X0VPTW1HPT0maUFwelFMM1I3SEhRaj9qdGIwbWMybVQkWSVJc3Jncnh2ZWxkI1peZzMtdWxefDB4QUlUZ2FuSXVGMjNKMHdhU2E2ejZhUF8rJURlNUxxdHVZJnB0eD9xaFp5U0VDZHlFXio0Ul5iKmhGalEtOT9jQ1NKTmZST3p6dEVZYlJ5Tj1TcUR5aGhwelNtbVB8RWI=");

			if (json!=null && !json.equals(""))
			{
				StringEntity entity = new StringEntity(json, HTTP.UTF_8);
				request.setEntity(entity);
			}

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);

			String responseString = convertStreamToString(getBinaryContent(response));
			Log.i("TAG",""+responseString);
			//return getBinaryContent(response);


		}
		catch (IOException e)
		{
			//throw new RestException("POST failed with the REST web service", e);
			e.getMessage();
		}

		//return null;
	}

	protected static InputStream getBinaryContent(HttpResponse response) throws IOException {
		// Check if server response is valid

		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != HttpStatus.SC_OK) {
			//throw new RestException("Error response from server: " + status.toString(), status.getStatusCode());
		}

		return response.getEntity().getContent();
	}

	public static HttpClient createHttpClient()
	{
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

		return new DefaultHttpClient(conMgr, params);
	}

	private SessionManager mSessionManager;



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