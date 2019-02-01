package com.lagel.com.service;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public final class Common {
	
	private Common() { }
	public static final String KEY_ROW_STATIONLIST = "STATIONLIST";
	public static final String TAG = "Billing";
	public static int timeOutSeg_=120*60; //descomentar
	public static int timeOutSeg=6000;
	public static int timeOutMilSeg=timeOutSeg*1000;

	public static boolean IsEmail(String email)
    {
		email=email.toLowerCase();
        Boolean result = false;

        String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9]+[a-z0-9-]*[a-z0-9]+";
        //String regex = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

        if (email.matches(regex))
            result = true;
        else
            result = false;

        return result;
    }
	
	public static Date convertSringtoDate(String data)
	{
		Date newDate= new Date();
		SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
		try {
			newDate = curFormater.parse(data);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newDate;
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
	

	
	public static boolean haveInternet(Context ctx){

    	NetworkInfo info=(NetworkInfo)( (ConnectivityManager)ctx.getSystemService(ctx.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

    	if(info==null || !info.isConnected()){ 
    		return false; 
    	} 
    	if(info.isRoaming()){ 
    	//here is the roaming option you can change it if you want to disable internet while roaming, just return false 
    		return true; 
    	} 
    	
    	return true; 
    }		
	
	public static void ErrorDialog(Context ctx, String text)
	{
		AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
  	  	alertDialog.setTitle("Smartfleet");
			alertDialog.setMessage(text);
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			      public void onClick(DialogInterface dialog, int which) {
			    	  dialog.dismiss();
			      } });
			
			alertDialog.show();
		
	}
	
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
    
    public static boolean isTablet2(Context context) {
	    TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
            return true;
        }else{
            return false;
        }
    	
    }
    
    public static boolean wifiConnected(Context context)
    {
	    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
	    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    
	    if (mWifi!=null && mWifi.isConnected())
	    {
	        //if wifi connected
	    	Log.i("", "Wifi Connected");
	    	return true;
	    }
	    else
	    	return false;


    }
    
    public static int diferenciaEnDias2(Date fechaMayor, Date fechaMenor) {
		long diferenciaEn_ms = fechaMayor.getTime() - fechaMenor.getTime();
		long dias = diferenciaEn_ms / (1000 * 60 * 60 * 24);
		return (int) dias;
	}
    
    public static String getLeftCero(int i)
	   {
		   String left=null;
		   
		   if (i<10)
		   {
			   left="0"+i;
		   }
		   else
		   {
			   left=""+i;
		   }
		   
		   
		   return left;
	   }
    

    

    public static boolean isAirplaneModeOn(Context context){
    	boolean value= Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,0)  !=  0 ;
    //	if (value==true) return false;
    	//else return true;
    	return value;
	 }
    
    

    

    public static String encrypt2(String strvalue, String deltaDate)
    {
    	String Encrypt2 = "";
    	String LowerAlpha    ="abcdefghijklmnopqrstuvwxyz";
    	String LowerSub      ="vwxyzefghijklrstuabcdmnopq";
    	String UpperAlpha    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    	String UpperSub      = "GHABIPCDEFQRSTJKLMNOUXYZVW";
    	String base64 = null;
    	int i;		
    	String newStr="";String strEncrypt = "";String strLetter="";

    	if (strvalue.equals(""))
    	{
    		
    	}
    	 
    	//Create a new string value from original value joining 2 sets of 4 consecutive digits
    	strvalue = ConcatKey(strvalue,deltaDate);
    	for (int lngi=0;lngi<strvalue.length();lngi++)
    	{
    		char[] charArrayStrLetter = strvalue.toCharArray();
    		//strLetter =strvalue.substring(lngi, 1);
    		strLetter = String.valueOf(charArrayStrLetter, lngi, 1);
    		
    		char a = strLetter.charAt(0);
    		int a_codepoint = (int)a;
    		
    		if (a_codepoint>=65 && a_codepoint<=90)
    		{
    			for (int lngE=1;lngE<UpperAlpha.length();lngE++)
    			{
					char[] charArrayUpperAlpha = UpperAlpha.toCharArray();
					String valueUpperAlpha = String.valueOf(charArrayUpperAlpha, lngE-1, 1);
    				
    				if  (valueUpperAlpha.equalsIgnoreCase(strLetter))
    				{
    					char[] charArrayUpperSub = UpperSub.toCharArray();
    					String strEncrypt_1 = String.valueOf(charArrayUpperSub, lngE-1, 1);
    					strEncrypt = strEncrypt +strEncrypt_1;
    					break;
    				}
    			}
    		}
    		
    		
    		else if (a_codepoint>=97 && a_codepoint<=122)
			{
				for (int lngE3=1;lngE3< LowerAlpha.length();lngE3++)
				{
					char[] charArrayLowerAlpha = LowerAlpha.toCharArray();
					String valueLowerAlpha = String.valueOf(charArrayLowerAlpha, lngE3-1, 1);
					
					if  (valueLowerAlpha.equalsIgnoreCase(strLetter))
    				{
    					char[] charArrayLowerSub = LowerSub.toCharArray();
    					String strEncrypt_2 = String.valueOf(charArrayLowerSub, lngE3-1, 1);
    					strEncrypt = strEncrypt +strEncrypt_2;
    					break;
    				}
				}
			}
			else
			{
				strEncrypt = strEncrypt + strLetter;
			}
    		
			
    	}


    		for (int lngii=1;lngii<=strEncrypt.length();lngii++)
			{
				char[] charArrayStrEncrypt = strEncrypt.toCharArray();
				char b = String.valueOf(charArrayStrEncrypt, lngii-1, 1).charAt(0);
	    		int b_codepoint = (int)b;
	    		b_codepoint=b_codepoint+13;
	    		
	    		String x=null;
	    		int value = b_codepoint;
	    		byte[] sendBytes = new byte[1];
	    		sendBytes[0] = (byte)value; //set char of byte
	            int rep = (int) sendBytes[0] & 0xFF;
	            try {
	            	x= new String(sendBytes, Charset.forName("Cp1252"));
	    			System.out.println("REP = " + x);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    		
				Encrypt2 = Encrypt2 + x ;
			}
	
    	
    	
    	byte[] data;
		try {
		
			data = Encrypt2.getBytes("UTF-8");
			base64 = Base64.encodeToString(data, Base64.DEFAULT);
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
		//Log.e("base64 ", base64);
		
    	return 	base64;
    }
    
    private static String ConcatKey(String value1, String deltaDate)
    {
    	String firstCadena=null;
    	String secondCadena=null;
    	String reportDate=null;
    	try
    	{
        	//DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        	char[] charArrayFirstCadena = value1.toCharArray();
        	firstCadena = String.valueOf(charArrayFirstCadena, 7-1, 4);

        	char[] charArraySecondCadena = value1.toCharArray();
        	//secondCadena=value1.substring(20, 4);
        	secondCadena = String.valueOf(charArraySecondCadena, 20-1, 4);
        	
        	Date today = Calendar.getInstance().getTime();

        	//reportDate = df.format(today);
    		
    	}catch(Exception e)
    	{
    		Log.e(" Error ",e.getMessage());
    	}
    	
    	return firstCadena+secondCadena+deltaDate;
    	//return firstCadena+secondCadena+reportDate;
    }
    

    

    public static String getDateDevice()
    {
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    Date now = new Date();
		String serverDateTime = df.format(now);
		
		return serverDateTime ;
    }
    
    //2015-01-26 15:04:56"
    public static Date getDateStringDelta(String dateString) {
		    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    Date value = null;
		    try {
		        value = formatter.parse(dateString);

		    } catch (Exception e) {
		        e.printStackTrace();
		    }

		    
		    return value;
	}
	 
	 
    private static double restar2Fechas(Date date1, Date date2)
	 {
		 long result=0;
		 double dresult=0;
		 long l1 = date1.getTime(); 
		 long l2 = date2.getTime(); 
		 
		 double d1=(double)l1;
		 double d2=(double)l2;
		 Log.i(" Total Hours : ",""+(l1 - l2)/3600000);
		 dresult=(d1 - d2)/3600000;
		 return round(dresult,0);
	 }
    
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static float getDeltaHour(String serverDateTime)
    {
    	Date now = new Date();
		Date serverDate=Common.getDateStringDelta(serverDateTime);
		double ldiference=Common.restar2Fechas(serverDate,now);
		return (float)ldiference;
    }
    
    public static String getDetaDate(float fDateDeltaServer)
    {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(now.getTime());
		cal.add(Calendar.HOUR_OF_DAY, (int)fDateDeltaServer);
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sDateDeltaServer = df.format(cal.getTime());
		return sDateDeltaServer ;
    }

	public static String getAppVersion(Context context) {
		String appVersion = "";
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			appVersion = info.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return appVersion;
	}
  
}
