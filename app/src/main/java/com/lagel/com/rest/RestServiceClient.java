package com.lagel.com.rest;

import android.content.Context;
import android.util.Log;


import com.lagel.com.service.GeoPunchBean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class RestServiceClient {
	private static final int BUFFER_SIZE = 1024;
	private String userAgent;
	
	public RestServiceClient(String userAgent) {
		this.userAgent = userAgent;
	}

	public InputStream putBinaryJson(String url, String json, String token) throws RestException {
		try {
			
			HttpClient httpClient = createHttpClient();
			HttpPut request = new HttpPut(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            
            
            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }
 
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	
	public InputStream putBinaryJson(String url, String json) throws RestException {
		try {
			
			HttpPut request = new HttpPut(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            
            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }
 
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}

	public InputStream postBinary2(String url, String json) throws RestException {
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}

	public InputStream postForm(String url, GeoPunchBean gp) throws RestException {
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >();
			nameValuePairs.add(new BasicNameValuePair("reqtype", "geopunch"));
			nameValuePairs.add(new BasicNameValuePair("UserID", gp.getUserID()));
			nameValuePairs.add(new BasicNameValuePair("DateTime", gp.getDateTime()));
			nameValuePairs.add(new BasicNameValuePair("IMEI", gp.getIMEI()));
			nameValuePairs.add(new BasicNameValuePair("Lat", gp.getLat()));
			nameValuePairs.add(new BasicNameValuePair("Long", gp.getLong()));
			nameValuePairs.add(new BasicNameValuePair("StatusComm", gp.getStatusComm()));
			nameValuePairs.add(new BasicNameValuePair("StatusGeoMobile", gp.getStatusGeoMobile()));


			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}


	public InputStream postMultimedia(String url, GeoPunchBean gp) throws RestException {
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >();
			nameValuePairs.add(new BasicNameValuePair("reqtype", gp.getReqtype()));
			nameValuePairs.add(new BasicNameValuePair("UserID", gp.getUserID()));
			nameValuePairs.add(new BasicNameValuePair("file", gp.getFile()));
			nameValuePairs.add(new BasicNameValuePair("filename", gp.getFilename()));

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}

	public InputStream postBinaryForm(String url, GeoPunchBean gp) throws RestException {
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >();
			nameValuePairs.add(new BasicNameValuePair("reqtype", "forms"));
			nameValuePairs.add(new BasicNameValuePair("UserID", gp.getUserID()));
			nameValuePairs.add(new BasicNameValuePair("DateTime", gp.getDateTime()));
			nameValuePairs.add(new BasicNameValuePair("IMEI", gp.getIMEI()));
			nameValuePairs.add(new BasicNameValuePair("Lat", gp.getLat()));
			nameValuePairs.add(new BasicNameValuePair("Long", gp.getLong()));
			nameValuePairs.add(new BasicNameValuePair("StatusComm", gp.getStatusComm()));
			nameValuePairs.add(new BasicNameValuePair("CustomerID", gp.getCustomerID()));

			//nameValuePairs.add(new BasicNameValuePair("StatusGeoMobile", "1"));

			nameValuePairs.add(new BasicNameValuePair("ProductId", gp.getProductId()));
			nameValuePairs.add(new BasicNameValuePair("Qty", gp.getQty()));
			nameValuePairs.add(new BasicNameValuePair("Comments", gp.getComments()));
			nameValuePairs.add(new BasicNameValuePair("Price", gp.getPrice()));

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}

	public InputStream postBinaryGeoPunch(String url, GeoPunchBean gp) throws RestException {
		try {

			HttpPost request = new HttpPost(url);
			request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >();
			nameValuePairs.add(new BasicNameValuePair("reqtype", "geopunch"));
			nameValuePairs.add(new BasicNameValuePair("UserID", gp.getUserID()));
			nameValuePairs.add(new BasicNameValuePair("DateTime", gp.getDateTime()));
			nameValuePairs.add(new BasicNameValuePair("IMEI", gp.getIMEI()));
			nameValuePairs.add(new BasicNameValuePair("Lat", gp.getLat()));
			nameValuePairs.add(new BasicNameValuePair("Long", gp.getLong()));
			nameValuePairs.add(new BasicNameValuePair("StatusComm", gp.getStatusComm()));
			nameValuePairs.add(new BasicNameValuePair("StatusGeoMobile", gp.getStatusGeoMobile()));


			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream postBinaryLogin(String url, String user, String password) throws RestException {
		try {
			
            HttpPost request = new HttpPost(url);
            request.setHeader("Accept", "*/*");
			request.setHeader("Content-type", "application/x-www-form-urlencoded");

			List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >();
			nameValuePairs.add(new BasicNameValuePair("username", user));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			nameValuePairs.add(new BasicNameValuePair("reqtype", "login"));

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}

	public InputStream postBinaryEncriptUUID(String url, String json, String token) throws RestException {
		try {

            HttpPost request = new HttpPost(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);

            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }

            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}


	public InputStream getWhatToDo(String url, String token) throws RestException {
		try {
			
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            HttpClient httpClient = createHttpClient();
            //HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream getSyncPurchase(String url, String token) throws RestException {
		try {
			
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	


	
	public InputStream getValidateEmail(String url, String token) throws RestException {
		try {
			
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
         
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream getAgeGroup(String url, String token) throws RestException {
		try {
			
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            HttpClient httpClient = createHttpClient();
            //HttpClient httpClient = new DefaultHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream getCommon2(String url, String token, Context act) throws RestException {
		try {
		
			HttpClient httpClient = createHttpClient();
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            
            HttpResponse response = httpClient.execute(request);
            
			return getBinaryContent(response);
			
		} catch (javax.net.ssl.SSLHandshakeException ex) {			
			Log.e("", ""+ex.getMessage());
			throw new RestException("POST failed with the REST web service", ex);
		} catch (Exception e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	

	
	public InputStream getCommon(String url, String token) throws RestException {
		try {
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (Exception e) {
			throw new RestException("POST failed with the REST web service", e);
		}
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
	
	public static HttpClient createHttpClient(HttpParams params)
	{
	    //HttpParams params = new BasicHttpParams();
	    HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	    HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	    HttpProtocolParams.setUseExpectContinue(params, true);

	    SchemeRegistry schReg = new SchemeRegistry();
	    schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	    schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
	    ClientConnectionManager conMgr = new ThreadSafeClientConnManager(params, schReg);

	    return new DefaultHttpClient(conMgr, params);
	}
	
	public HttpClient getNewHttpClient() {
	    try {
	        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
	        trustStore.load(null, null);

	        SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
	        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

	        SchemeRegistry registry = new SchemeRegistry();
	        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
	        registry.register(new Scheme("https", sf, 443));

	        ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

	        return new DefaultHttpClient(ccm, params);
	    } catch (Exception e) {
	        return new DefaultHttpClient();
	    }
	}


	
	public InputStream getCommonFirstSeason(String url, String uuid) throws RestException {
		try {
			
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization",uuid);
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	
	public InputStream postBinaryAssesment(String url, String json, String token) throws RestException {
		try {
			
			HttpPost request = new HttpPost(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            request.setHeader("Authorization", token);
            
            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	
	public InputStream getBinaryJson(String url) throws RestException {
		try {
		    HttpGet httpget = new HttpGet(url);
		    httpget.setHeader("Accept", "*/*");
		    httpget.setHeader("Content-type", "application/json");
		    HttpClient httpClient = createHttpClient();
		    HttpResponse response =httpClient.execute(httpget);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("GET failed with the REST web service", e);
		}
	}

	
	public InputStream getBinaryJsonToken(String url, String token) throws RestException {
		try {
		    HttpGet httpget = new HttpGet(url);
		    httpget.setHeader("Accept", "*/*");
		    httpget.setHeader("Content-type", "application/json");
		    httpget.setHeader("Authorization", token);
		    HttpClient httpClient = createHttpClient();
		    HttpResponse response =httpClient.execute(httpget);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("GET failed with the REST web service", e);
		}
	}
	
	public InputStream getBinary(String url) throws RestException {
		try {
		    HttpGet httpget = new HttpGet(url);
		    HttpClient httpClient = createHttpClient();
		    HttpResponse response =httpClient.execute(httpget);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("GET failed with the REST web service", e);
		}
	}

	//dts
	public InputStream postBinaryJson(String url, String json) throws RestException {
		try {
			
			HttpResponse response =null;
			
            if (json!=null && !json.equals(""))
            {
            	//HttpClient client = new DefaultHttpClient();
            	HttpClient client = createHttpClient();
                HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000);
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
            	HttpPost post = new HttpPost(url);
    			JSONArray array = new JSONArray();
            	array.put(json);
            	
            	JSONArray jsonArray=new JSONArray();
            	jsonArray.put(json); //your current json
            	StringEntity entity=new StringEntity("data="+jsonArray);
            	post.setEntity(entity);
                String result = client.execute(post, responseHandler);
			}
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("GET failed with the REST web service", e);
		}
	}

	
	public InputStream postBinary(String url) throws RestException {
		try {
			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(new HttpPost(url));
			return getBinaryContent(response);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw new RestException("POST failed with the REST web service", e);
		}
	}


	
	public InputStream postBinary(String url, String json) throws RestException {
		try {
			
            HttpPost request = new HttpPost(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/json");
            
            if (json!=null && !json.equals(""))
            {
                StringEntity entity = new StringEntity(json, HTTP.UTF_8);
                request.setEntity(entity);
            }
 
            HttpClient httpClient = createHttpClient();
            HttpResponse response = httpClient.execute(request);

			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream postBinary(String url, InputStream is, String contentType) throws RestException {
		try {
			HttpPost request = new HttpPost(url);
			InputStreamEntity entity = new InputStreamEntity(is, is.available());
			entity.setContentType(contentType);
			request.setEntity(entity);
			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}		
	
	public String post(String url) throws RestException {
		try {
			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(new HttpPost(url));
			return getPlainContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
		
	public String post(String url, List<NameValuePair> formParams) throws RestException {
		try {
			HttpPost request = new HttpPost(url);
			setFormParams(request, formParams);
			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
			
			BufferedReader r = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		     int read;
		     char[] buf = new char[2048];
		     while ((read = r.read(buf)) != -1) {
		         String s = new String(buf);
		         return s;
		         //System.out.println(s.substring(0,read));
		     }
			
			return "";
			//return getPlainContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	
	public InputStream postBinary(String url, List<NameValuePair> formParams) throws RestException {
		try {
			HttpPost request = new HttpPost(url);
			setFormParams(request, formParams);

			HttpClient httpClient = createHttpClient();
			HttpResponse response = httpClient.execute(request);
		     return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
	

	protected void setFormParams(HttpEntityEnclosingRequestBase request, List<NameValuePair> formParams) throws UnsupportedEncodingException {
		if(request == null || formParams == null) return;
		
		UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(formParams, HTTP.UTF_8);
		request.addHeader(formEntity.getContentType());
		request.addHeader(formEntity.getContentEncoding());
		request.setEntity(formEntity);
	}
	
	protected String getPlainContent(HttpResponse response) throws RestException, IOException {
		// Check if server response is valid
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != HttpStatus.SC_OK) {
			throw new RestException("Error response from server: " + status.toString(), status.getStatusCode());
		}

		// Pull content stream from response
		HttpEntity entity = response.getEntity();
		InputStream inputStream = entity.getContent();

		ByteArrayOutputStream content = new ByteArrayOutputStream();

		// Read response into a buffered stream
		int readBytes = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((readBytes = inputStream.read(buffer)) != -1) {
			content.write(buffer, 0, readBytes);
		}

		// Return result from buffered stream
		return new String(content.toByteArray());
	}
	
	protected InputStream getBinaryContent(HttpResponse response) throws RestException, IOException {
		// Check if server response is valid
	
		StatusLine status = response.getStatusLine();
		if (status.getStatusCode() != HttpStatus.SC_OK) {
			throw new RestException("Error response from server: " + status.toString(), status.getStatusCode());
		}
		
		return response.getEntity().getContent();		
	}

	
	public InputStream getURL(String url) throws RestException {
		try {

			HttpClient httpClient = createHttpClient();
			HttpGet request = new HttpGet(url);
            request.setHeader("Accept", "*/*");
            request.setHeader("Content-type", "application/x-www-form-urlencoded");
            
            HttpResponse response = httpClient.execute(request);
			return getBinaryContent(response);
		} catch (IOException e) {
			throw new RestException("POST failed with the REST web service", e);
		}
	}
}

