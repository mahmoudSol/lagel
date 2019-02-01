package com.lagel.com.lalita.utils.server.interfaces;

import com.android.volley.VolleyError;

/**
 * Created by Lalita Gill  on 26/09/17.
 */

public interface ApiCallbackListener {
    void onResultCallback(String response, String flag);

    void onErrorCallback(VolleyError error);
}
