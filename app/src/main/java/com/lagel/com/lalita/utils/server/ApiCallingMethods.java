package com.lagel.com.lalita.utils.server;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.lagel.com.lalita.utils.server.interfaces.ApiCallbackListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Lalita Gill on 26/9/2017.
 */

public class ApiCallingMethods {
    /**
     * This method is called when we need to hit service in POST method.
     *
     * @param listKey
     * @param listValue
     * @param url
     * @param context
     * @param listener
     */

    public static void requestForPost(final List<String> listKey, final List<String> listValue, String url, Context context, final ApiCallbackListener listener, final String flag) {
        // SSLCertificate.allowAllSSL();
        SSLCertificates.nuke();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    if (listener != null) {
                        listener.onResultCallback(response, flag);
                    }
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (listener != null)
                                listener.onErrorCallback(error);
                        } catch (Exception f) {
                            f.printStackTrace();
                        }
                    }
                }

        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();

                for (int i = 0; i < listKey.size(); i++) {
                    map.put(listKey.get(i), listValue.get(i));
                }
                return map;
            }
        };


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                50000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    /**
     * This method is called when we want to hit web service in GET method
     *
     * @param url
     * @param context
     * @param listener
     */
    public static void requestForGet(String url, Context context, final ApiCallbackListener listener, final String flag) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (listener != null) {
                    listener.onResultCallback(response, flag);
                }

            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        if (listener != null)
                            listener.onErrorCallback(error);
                    }
                }
        );
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 4, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
