package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.IntentCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.google.gson.Gson;
import com.lagel.com.Face_book_manger.Facebook_login;
import com.lagel.com.Face_book_manger.Facebook_share_mamager;
import com.lagel.com.R;
import com.lagel.com.Twiter_manager.TweetManger;
import com.lagel.com.Uploader.FileUploader;
import com.lagel.com.Uploader.ProductImageDatas;
import com.lagel.com.Uploader.UploadedCallback;
import com.lagel.com.adapter.PostProductImagesRvAdap;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.pojo_class.PostProductDatas;
import com.lagel.com.pojo_class.PostProductMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CapturedImage;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.DialogBox;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.RunTimePermission;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * <h>PostProductActivity</h>
 * <p>
 * This class is getting called from Camera screen. In this class first of all
 * we used to receive the images from previous class and show on the top of
 * the screen horizontally. Apart from that we have option to fill field like
 * product name, description, category etc. then we send all these to our server.
 * </p>
 *
 * @author 3Embed
 * @version 1.0
 * @since 5/3/2017
 */
public class PostProductActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = PostProductActivity.class.getSimpleName();
    private Activity mActivity;
    private TextView tV_category, tV_condition, tV_currency, tV_currency_symbol, tV_current_location;
    private ProgressBar progress_bar_location;
    private FusedLocationService locationService;
    private String[] permissionsArray;
    private RunTimePermission runTimePermission;
    private RelativeLayout rL_rootElement;
    private String lat = "", lng = "", negotiable = "0", city = "", countryCode = "";
    private SessionManager mSessionManager;
    private EditText eT_title, eT_description, eT_price;
    private ArrayList<String> arrayListImgPath;
    private ArrayList<Integer> rotationAngles;
    private ArrayList<CapturedImage> mCapturedImageData = new ArrayList<>();
    private NotificationMessageDialog mNotificationMessageDialog;
    private boolean isToPostItem, isTwitterSharingOn, isFacebookSharingOn, isInstagramSharingOn;
    private DialogBox mDialogBox;
    private Facebook_login facebook_login;
    private CallbackManager callbackManager;
    private Facebook_share_mamager facebook_share_mamager;
    private TextView btn_cancel;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebook_login = new Facebook_login(this);
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_post_product);
        facebook_share_mamager = Facebook_share_mamager.getInstance();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        initializeVariable();
    }

    /**
     * <h>InitializeVariable</h>
     * <p>
     * In this method we used to initialize the xml data member and other variables.
     * </p>
     */
    private void initializeVariable() {
        mActivity = PostProductActivity.this;
        mDialogBox = new DialogBox(mActivity);
        isToPostItem = true;
        mNotificationMessageDialog = new NotificationMessageDialog(mActivity);
        mSessionManager = new SessionManager(mActivity);
        CommonClass.statusBarColor(mActivity);

        // Receiving datas from Camera activity class
        Intent intent = getIntent();
        arrayListImgPath = intent.getStringArrayListExtra("arrayListImgPath");
        rotationAngles = intent.getIntegerArrayListExtra("rotationAngles");
        CapturedImage image;
        for (int i = 0; i < rotationAngles.size(); i++) {
            image = new CapturedImage();
            image.setImagePath(arrayListImgPath.get(i));
            image.setRotateAngle(rotationAngles.get(i));
            mCapturedImageData.add(image);
        }

        // add more images text
        TextView tV_add_more_image = (TextView) findViewById(R.id.tV_add_more_image);

        // set adpter for horizontal images
        if (arrayListImgPath != null && arrayListImgPath.size() > 0) {
            switch (arrayListImgPath.size()) {
                // text to show add 4 more image
                case 1:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_4_more_img));
                    break;

                // text to show add 3 more image
                case 2:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_3_more_img));
                    break;

                // text to show add 3 more image
                case 3:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_2_more_img));
                    break;

                // text to show add 3 more image
                case 4:
                    tV_add_more_image.setText(getResources().getString(R.string.add_upto_1_more_img));
                    break;

                // hide text since it reached to the max limit
                case 5:
                    tV_add_more_image.setVisibility(View.GONE);
                    break;
            }

            PostProductImagesRvAdap imagesHorizontalRvAdap = new PostProductImagesRvAdap(mActivity, mCapturedImageData, tV_add_more_image);
            RecyclerView rV_cameraImages = (RecyclerView) findViewById(R.id.rV_cameraImages);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false);
            rV_cameraImages.setLayoutManager(linearLayoutManager);
            rV_cameraImages.setAdapter(imagesHorizontalRvAdap);
        }

        // find current location
        rL_rootElement = (RelativeLayout) findViewById(R.id.rL_rootElement);
        progress_bar_location = (ProgressBar) findViewById(R.id.progress_bar_location);
        tV_current_location = (TextView) findViewById(R.id.tV_current_location);
        TextView tV_change_loc = (TextView) findViewById(R.id.tV_change_loc);
        tV_change_loc.setOnClickListener(this);
        permissionsArray = new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION};
        runTimePermission = new RunTimePermission(mActivity, permissionsArray, false);
        if (runTimePermission.checkPermissions(permissionsArray)) {
            getCurrentLocation();
        } else {
            runTimePermission.requestPermission();
        }

        // Back button
        RelativeLayout rL_back_btn, rL_product_category, rL_conditions, rL_currency;
        rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);

        // title
        eT_title = (EditText) findViewById(R.id.eT_title);

        // Description
        eT_description = (EditText) findViewById(R.id.eT_description);

        // price
        eT_price = (EditText) findViewById(R.id.eT_price);

        //cancel button
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(this);

        // switch negotiable
        SwitchCompat switch_negotiable = (SwitchCompat) findViewById(R.id.switch_negotiable);
        switch_negotiable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    negotiable = "1";
                else negotiable = "0";
            }
        });

        // category
        rL_product_category = (RelativeLayout) findViewById(R.id.rL_product_category);
        rL_product_category.setOnClickListener(this);
        tV_category = (TextView) findViewById(R.id.tV_category);

        // condition
        rL_conditions = (RelativeLayout) findViewById(R.id.rL_conditions);
        rL_conditions.setOnClickListener(this);
        tV_condition = (TextView) findViewById(R.id.tV_condition);

        // currency
        rL_currency = (RelativeLayout) findViewById(R.id.rL_currency);
        rL_currency.setOnClickListener(this);
        tV_currency = (TextView) findViewById(R.id.tV_currency);
        tV_currency_symbol = (TextView) findViewById(R.id.tV_currency_symbol);

        // set default currency
        //setDefaultCurrency();

        // post product
        RelativeLayout rL_postProduct = (RelativeLayout) findViewById(R.id.rL_postProduct);
        rL_postProduct.setOnClickListener(this);

        // share on twitter
        SwitchCompat switch_twitter = (SwitchCompat) findViewById(R.id.switch_twitter);
        switch_twitter.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (TweetManger.getInstance().isUserLoggedIn()) {
                        isTwitterSharingOn = true;
                    } else {
                        isTwitterSharingOn = false;
                        TweetManger.getInstance().doLogin(VariableConstants.TWEETER_REQUEST_CODE, mActivity, null);
                    }
                } else isTwitterSharingOn = false;
            }
        });

        // Share on Facebook
        SwitchCompat switch_fb = (SwitchCompat) findViewById(R.id.switch_fb);
        switch_fb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkForPermission();
                } else isFacebookSharingOn = false;
            }
        });

        // Share on Instagram
        isInstagramSharingOn = false;
        SwitchCompat switch_instagram = (SwitchCompat) findViewById(R.id.switch_instagram);
        switch_instagram.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkAppinstall("com.instagram.android")) {
                        isInstagramSharingOn = true;
                    } else {
                        System.out.println(TAG + " Instagram not install");
                        isInstagramSharingOn = false;
                    }
                } else isInstagramSharingOn = false;
            }
        });
    }

    private boolean checkAppinstall(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }


    private void checkForPermission() {
        String permission[] = new String[]{"publish_actions"};
        facebook_login.ask_PublishPermission(callbackManager, permission, new Facebook_login.Facebook_callback() {
            @Override
            public void success(String id) {
                isFacebookSharingOn = true;
            }

            @Override
            public void error(String error) {
                isFacebookSharingOn = false;
            }

            @Override
            public void cancel(String cancel) {
                isFacebookSharingOn = false;
            }
        });
    }

    @Override
    protected void onResume() {
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

    /**
     * <h>SetDefaultCurrency</h>
     * <p>
     * In this method we used to find the current country currency.
     * </p>
     */
    private void setDefaultCurrency() {

        try {
            Map<Currency, Locale> map = CommonClass.getCurrencyLocaleMap();
            Currency currency = Currency.getInstance(mSessionManager.getCurrency());
            String symbol = currency.getSymbol(map.get(currency));
            System.out.println(TAG + " " + "currency=" + currency + " symbol" + symbol);
            if (symbol != null && !symbol.isEmpty()) {
                tV_currency.setText(String.valueOf(currency));
                tV_currency_symbol.setText(symbol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * In this method we find current location using FusedLocationApi.
     * in this we have onUpdateLocation() method in which we check if
     * its not null then We call guest user api.
     */
    private void getCurrentLocation() {
        progress_bar_location.setVisibility(View.VISIBLE);
        locationService = new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation = locationService.receiveLocation();
                if (currentLocation != null) {
                    lat = String.valueOf(currentLocation.getLatitude());
                    lng = String.valueOf(currentLocation.getLongitude());

                    if (isLocationFound(lat, lng)) {
                        System.out.println(TAG + " " + "lat=" + lat + " " + "lng=" + lng);
                        mSessionManager.setCurrentLat(lat);
                        mSessionManager.setCurrentLng(lng);
                        String address = CommonClass.getCompleteAddressString(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        System.out.println(TAG + " " + "complete address=" + address);

                        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=+" + lat + "+,+" + lng + "+&location_type=ROOFTOP&result_type=street_address&key=" + getResources().getString(R.string.google_map_api_key);

                        // call google map api
                        if (address.isEmpty())
                            new RequestTask().execute(url);

                        if (!address.isEmpty()) {
                            progress_bar_location.setVisibility(View.GONE);
                            tV_current_location.setVisibility(View.VISIBLE);
                            tV_current_location.setText(address);
                        }
                        city = CommonClass.getCityName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        countryCode = CommonClass.getCountryCode(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude());
                        tV_current_location.setText(city + ", " + CommonClass.getCountryName(mActivity, currentLocation.getLatitude(), currentLocation.getLongitude()));
                    }
                }
            }
        }
        );
    }

    public String getPhoneNumber(String s) {
        String modifiedString = s;
        char[] temp = s.toCharArray();
        String value = "";


        for (int i = 0; i < temp.length - 1; i++) {
            /*if(Character.toString(temp[i]).matches("[a-zA-z]{1}") ){
                value="";
            }else{
                value+=Character.toString(temp[i]);

            }*/

            if (Character.isDigit(temp[i])) {
                value += Character.toString(temp[i]);
            }
        }
        if (value.length() > 7 && value.length() < 13) {
            value = value.trim();
            for (int i = 0; i < value.length() - 1; i++) {
                try {
                    int a = modifiedString.indexOf(value.charAt(i));
                    if (a != -1)
                        modifiedString = modifiedString.replace(modifiedString.charAt(a), '\u0020');
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // modifiedString = modifiedString.replace(" ","");
        }

        return modifiedString;
    }


    private class RequestTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    responseString = out.toString();
                    out.close();
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                e.printStackTrace();
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
            progress_bar_location.setVisibility(View.GONE);
            tV_current_location.setVisibility(View.VISIBLE);

            System.out.println(TAG + " " + "address res=" + result);
            if (result != null) {
                try {
                    JSONObject reader = new JSONObject(result);
                    JSONArray sys = reader.getJSONArray("results");

                    String city = "";
                    String country = "";

                    JSONObject zero = sys.getJSONObject(0);
                    JSONArray address_components = zero.getJSONArray("address_components");

                    for (int i = 0; i < address_components.length(); i++) {
                        JSONObject zero2 = address_components.getJSONObject(i);
                        String long_name = zero2.getString("long_name");
                        JSONArray mtypes = zero2.getJSONArray("types");
                        String Type = mtypes.getString(0);

                        if (TextUtils.isEmpty(long_name) == false || !long_name.equals(null) || long_name.length() > 0 || long_name != "") {
                            if (Type.equalsIgnoreCase("locality")) {
                                // Address2 = Address2 + long_name + ", ";
                                city = long_name;
                            } else if (Type.equalsIgnoreCase("country")) {
                                country = long_name;
                            }
                        }

                        // JSONArray mtypes = zero2.getJSONArray("types");
                        // String Type = mtypes.getString(0);
                        // Log.e(Type,long_name);
                    }


                    String formatted_address = sys.getJSONObject(0).getString("formatted_address");
                    String city_country = city + ", " + country;
                    System.out.println(TAG + " " + "formatted_address=" + formatted_address);
                    if (formatted_address != null && !formatted_address.isEmpty())
                        tV_current_location.setText(city_country);

                } catch (JSONException e) {
                    e.printStackTrace();

                    //..if error occurs to find address then we show city..

                    progress_bar_location.setVisibility(View.GONE);
                    tV_current_location.setVisibility(View.VISIBLE);
                    String city = CommonClass.getCityName(mActivity, Double.parseDouble(lat), Double.parseDouble(lng));
                    String country = CommonClass.getCountryName(mActivity, Double.parseDouble(lat), Double.parseDouble(lng));
                    city = city.substring(0, 1).toUpperCase() + "" + city.substring(1);
                    country = country.substring(0, 1).toUpperCase() + "" + country.substring(1);
                    String address = city + ", " + country;
                    tV_current_location.setText(address);
                }
            }

        }
    }

    /**
     * In this method we used to check whether current lat and
     * long has been received or not.
     *
     * @param lat The current latitude
     * @param lng The current longitude
     * @return boolean flag true or false
     */
    private boolean isLocationFound(String lat, String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }

    /**
     * validation either all the mandatory fields are filled or not
     * if it is filled then call api method to post the product.
     */
    private void checkPostProduct() {
        // String a = getPhoneNumber(eT_title.getText().toString());
        //Toast.makeText(mActivity, a, Toast.LENGTH_SHORT).show();
        // String b=eT_title.getText().toString().replace(a,"");
        // title
        if (eT_title.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_add_title));

            // desription
        else if (eT_description.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_add_description));

      /*  else if(getPhoneNumber(eT_description.getText().toString())!=null)
            CommonClass.showSnackbarMessage(rL_rootElement,getString(R.string.phone_number_not_allowed_in_desc));
*/
            // product category
        else if (tV_category.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_select_product_cate));

            // condition
        else if (tV_condition.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_select_condition));

            // currency
        else if (tV_currency.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_select_currency));

            // price
        else if (eT_price.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_enter_price));

            // location
        else if (tV_current_location.getText().toString().isEmpty())
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.please_enter_location));
        else {
            System.out.println(TAG + " " + "post clicked" + " " + "1");
            if (isToPostItem) {
                //progress_bar_post.setVisibility(View.VISIBLE);
                mDialogBox.showProgressDialog(mActivity.getResources().getString(R.string.posting));
                //tV_post.setVisibility(View.GONE);
                System.out.println(TAG + " " + "post clicked" + " " + "2");
                uploadImages(arrayListImgPath, rotationAngles);
                isToPostItem = false;
            }
        }
    }

    /*
     *Uploading the image in cloudinary. */
    private void uploadImages(ArrayList<String> list, ArrayList<Integer> rotationAnagles) {
        // Reduce the Bitmap size
        try {
            Bitmap bitmap;
            ByteArrayOutputStream outputStream;
            for (int i = 0; i < list.size(); i++) {
                bitmap = BitmapFactory.decodeFile(list.get(i));
                outputStream = new ByteArrayOutputStream();

                //..for samsung device handle image angle
                if (Build.MANUFACTURER.equalsIgnoreCase("Samsung")) {
                    bitmap = rotate(bitmap, 90);
                } else {
                    bitmap = rotate(bitmap, rotationAnagles.get(i));
                }

                System.out.println(TAG + " " + "rotate angle==" + rotationAnagles.get(i));
                if (bitmap != null) {
                    bitmap = CommonClass.getResizedBitmap(bitmap, 1920);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream);
                }
                try {
                    FileOutputStream file_out = new FileOutputStream(new File(list.get(i)));
                    file_out.write(outputStream.toByteArray());
                    file_out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            System.out.println(TAG + " " + "post clicked" + " " + "4");
            // Upload Images to cloudinary

            FileUploader.getFileUploader(getApplicationContext()).UploadMultiple(list, new UploadedCallback() {
                @Override
                public void onSuccess(List<ProductImageDatas> data_list, List<ProductImageDatas> failed_list) {

                    System.out.println(TAG + " " + "post clicked" + " " + "5");
                    for (ProductImageDatas productImageDatas : data_list) {
                        System.out.println(TAG + " " + "main url=" + productImageDatas.getMainUrl() + " " + "thumb nail url=" + productImageDatas.getThumbnailUrl() + " " + "width=" + productImageDatas.getWidth() + " " + "height=" + productImageDatas.getHeight() + " " + "message=" + productImageDatas.getMessage());
                    }
                    if (data_list.size() > 0) {
                        postProductApi(data_list);
                    }
                }

                @Override
                public void onError(String error) {

                    if (mDialogBox.progressBarDialog != null)
                        mDialogBox.progressBarDialog.dismiss();
                    Log.d("fdsf324", " " + error);
                }
            });
        } catch (OutOfMemoryError e) {
            if (mDialogBox.progressBarDialog != null)
                mDialogBox.progressBarDialog.dismiss();
            CommonClass.showSnackbarMessage(rL_rootElement, getString(R.string.failedToUpload));
        }
    }


    // change locale
    private void changeLocal(boolean isForChanged) {
        Resources res = mActivity.getApplicationContext().getResources();
        String current_local = mSessionManager.getLanguageCode();
        Configuration config = new Configuration();

        if (!current_local.equals("en") && !isForChanged) {
            Locale locale = new Locale("en");
            Locale.setDefault(locale);
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        } else if (isForChanged) {
            Locale locale = new Locale(current_local);
            Locale.setDefault(locale);
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }

    }

    /**
     * <h>PostProductApi</h>
     * <p>
     * In this method we used to do api call to post the product to our product.
     * </p>
     *
     * @param data_list The list containing the image description like main image, thumbnail image, width and height
     */
    private void postProductApi(List<ProductImageDatas> data_list) {
        //token, type(0 : photo, 1 : video), mainUrl, thumbnailUrl, containerHeight, containerWidth, title,description, hashTags, location, latitude, longitude, userCoordinates, category, subCategory, price, 
        // currency, productName, thumbnailUrl1, imageUrl1, containerHeight1, containerWidth1, imageUrl2, thumbnailUrl2, containerHeight2, containerWidth2, imageUrl3, thumbnailUrl3, containerHeight3,
        // containerWidth3, imageUrl4, thumbnailUrl4, containerHeight4, containerWidth4, tagProduct, tagProductCoordinates, negotiable, condition

        /*{
    category = Other;
    city = Bengaluru;
    cloudinaryPublicId = t1rtjzy9dasxgq0cw0de;
    cloudinaryPublicId1 = joswipn5eryjehlvygcl;
    cloudinaryPublicId2 = wnes7xbnepd6ym4ljjxo;
    cloudinaryPublicId3 = aeutu4hs7m4ej9dmqx7y;
    cloudinaryPublicId4 = fzfwp6q1bsemtif2ywmy;
    condition = "New(Never Used)";
    containerHeight = 388;
    containerHeight1 = 326;
    containerHeight2 = 400;
    containerHeight3 = 384;
    containerHeight4 = 1136;
    containerWidth = 379;
    containerWidth1 = 451;
    containerWidth2 = 368;
    containerWidth3 = 384;
    containerWidth4 = 640;
    countrySname = IN;
    currency = INR;
    description = Testing;
    imageCount = 5;
    imageUrl1 = "https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1509468002/joswipn5eryjehlvygcl.jpg";
    imageUrl2 = "https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1509468005/wnes7xbnepd6ym4ljjxo.jpg";
    imageUrl3 = "https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1509468008/aeutu4hs7m4ej9dmqx7y.jpg";
    imageUrl4 = "https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1509468011/fzfwp6q1bsemtif2ywmy.jpg";
    latitude = "13.028609";
    location = "10th Cross Road, RT Nagar, Bengaluru, Karnataka 560024, India";
    longitude = "77.589495";
    mainUrl = "https://res.cloudinary.com/dxaxmyifi/image/upload/fl_progressive:steep/v1509468002/t1rtjzy9dasxgq0cw0de.jpg";
    negotiable = 1;
    postId = "";
    price = 5400;
    productName = Hello;
    subCategory = "";
    tagProduct = "";
    tagProductCoordinates = "";
    thumbnailImageUrl = "https://res.cloudinary.com/dxaxmyifi/image/upload/w_150,h_150/v1509468002/t1rtjzy9dasxgq0cw0de.jpg";
    thumbnailUrl1 = "";
    thumbnailUrl2 = "";
    thumbnailUrl3 = "";
    thumbnailUrl4 = "";
    token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpZCI6MjAxMiwibmFtZSI6Im1haWwiLCJhY2Nlc3NLZXkiOiI5MDc5IiwiaWF0IjoxNTA5NDYzOTg3LCJleHAiOjE1MTQ2NDc5ODd9.s_gkTcXZ7496Aw0Dr1VrfedyTNFSmVCyy2c6-tL1nA0";
    type = 0;
}*/

        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDatas = new JSONObject();
            try {
                // token
                requestDatas.put("token", mSessionManager.getAuthToken());

                // type 0 for image & 1 for video
                requestDatas.put("type", "0");

                // main url
                requestDatas.put("mainUrl", data_list.get(0).getMainUrl());
                requestDatas.put("thumbnailUrl", data_list.get(0).getThumbnailUrl());
                requestDatas.put("thumbnailImageUrl", data_list.get(0).getThumbnailUrl());
                requestDatas.put("containerHeight", data_list.get(0).getHeight());
                requestDatas.put("containerWidth", data_list.get(0).getWidth());
                requestDatas.put("cloudinaryPublicId", data_list.get(0).getPublic_id());


                // title with removed phone number
                String title = null;
                try {
                    title = eT_title.getText().toString();
                    title = getPhoneNumber(eT_title.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                requestDatas.put("title", title);

                //productName
                requestDatas.put("productName", title);

                // description with phone number removal
                String descr = getPhoneNumber(eT_description.getText().toString().trim());

                requestDatas.put("description", descr);


                // hash tag
                requestDatas.put("hashTags", "");
                if (mDialogBox.progressBarDialog != null)
                    mDialogBox.progressBarDialog.dismiss();
                // location
                requestDatas.put("city", city);
                requestDatas.put("countrySname", countryCode);
                requestDatas.put("location", tV_current_location.getText().toString());
                requestDatas.put("latitude", lat);
                requestDatas.put("longitude", lng);
                Log.e("Latitude_POST", "" + lat);
                Log.e("Longitude_POST", "" + lng);

                // user co-ordinate
                requestDatas.put("userCoordinates", "");

                //category
                requestDatas.put("category", tV_category.getText().toString());

                // sub category
                requestDatas.put("subCategory", "");

                // price
                requestDatas.put("price", eT_price.getText().toString());

                // currency
                requestDatas.put("currency", tV_currency.getText().toString());

                // total number of images
                requestDatas.put("imageCount", data_list.size());

                // Second Image
                if (data_list.size() > 1) {
                    requestDatas.put("thumbnailUrl1", data_list.get(1).getThumbnailUrl());
                    requestDatas.put("imageUrl1", data_list.get(1).getMainUrl());
                    requestDatas.put("containerHeight1", data_list.get(1).getHeight());
                    requestDatas.put("containerWidth1", data_list.get(1).getWidth());
                    requestDatas.put("cloudinaryPublicId1", data_list.get(1).getPublic_id());

                } else {
                    requestDatas.put("thumbnailUrl1", "");
                    requestDatas.put("imageUrl1", "");
                    requestDatas.put("containerHeight1", "");
                    requestDatas.put("containerWidth1", "");
                    requestDatas.put("cloudinarydPublicId1", "");
                }

                // Third Image
                if (data_list.size() > 2) {
                    requestDatas.put("imageUrl2", data_list.get(2).getMainUrl());
                    requestDatas.put("thumbnailUrl2", data_list.get(2).getThumbnailUrl());
                    requestDatas.put("containerHeight2", data_list.get(2).getHeight());
                    requestDatas.put("containerWidth2", data_list.get(2).getWidth());
                    requestDatas.put("cloudinaryPublicId2", data_list.get(2).getPublic_id());


                } else {
                    requestDatas.put("imageUrl2", "");
                    requestDatas.put("thumbnailUrl2", "");
                    requestDatas.put("containerHeight2", "");
                    requestDatas.put("containerWidth2", "");
                    requestDatas.put("cloudinarydPublicId2", "");
                }

                // Fourth Image
                if (data_list.size() > 3) {
                    requestDatas.put("imageUrl3", data_list.get(3).getMainUrl());
                    requestDatas.put("thumbnailUrl3", data_list.get(3).getThumbnailUrl());
                    requestDatas.put("containerHeight3", data_list.get(3).getHeight());
                    requestDatas.put("containerWidth3", data_list.get(3).getWidth());
                    requestDatas.put("cloudinaryPublicId3", data_list.get(3).getPublic_id());

                } else {
                    requestDatas.put("imageUrl3", "");
                    requestDatas.put("thumbnailUrl3", "");
                    requestDatas.put("containerHeight3", "");
                    requestDatas.put("containerWidth3", "");
                    requestDatas.put("cloudinarydPublicId3", "");
                }

                // Fifth Image
                if (data_list.size() > 4) {
                    requestDatas.put("imageUrl4", data_list.get(4).getMainUrl());
                    requestDatas.put("thumbnailUrl4", data_list.get(4).getThumbnailUrl());
                    requestDatas.put("containerHeight4", data_list.get(4).getHeight());
                    requestDatas.put("containerWidth4", data_list.get(4).getWidth());
                    requestDatas.put("cloudinaryPublicId4", data_list.get(4).getPublic_id());

                } else {
                    requestDatas.put("imageUrl4", "");
                    requestDatas.put("thumbnailUrl4", "");
                    requestDatas.put("containerHeight4", "");
                    requestDatas.put("containerWidth4", "");
                    requestDatas.put("cloudinarydPublicId4", "");
                }

                // tag product
                requestDatas.put("tagProduct", "");

                // tag product co-ordinate
                requestDatas.put("tagProductCoordinates", "");

                // negotiable
                requestDatas.put("negotiable", negotiable);

                // condition
                requestDatas.put("condition", tV_condition.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.PRODUCT, OkHttp3Connection.Request_type.POST, requestDatas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    //progress_bar_post.setVisibility(View.GONE);
                    if (mDialogBox.progressBarDialog != null)
                        mDialogBox.progressBarDialog.dismiss();
                    //tV_post.setVisibility(View.VISIBLE);
                    isToPostItem = true;

                    Gson gson = new Gson();
                    PostProductMainPojo postProductMainPojo = gson.fromJson(result, PostProductMainPojo.class);

                    switch (postProductMainPojo.getCode()) {
                        // success
                        case "200":
                            PostProductDatas postProductDatas = postProductMainPojo.getData().get(0);
                            if (postProductDatas != null) {
                                String postId = postProductDatas.getPostId();
                                String productName = postProductDatas.getProductName();
                                if (isTwitterSharingOn)
                                    share_On_Twitter(postId, productName);

                                if (isFacebookSharingOn) {
                                    String post_url = getResources().getString(R.string.share_item_base_url) + postId;
                                    shareOnFacebook(post_url, postProductDatas.getMainUrl(), productName, postProductDatas.getDescription());
                                }

                                if (isInstagramSharingOn) {
                                    shareOnInsta(arrayListImgPath.get(0), productName);
                                }

                            }
                            new DialogBox(mActivity).postedSuccessDialog();
                            //deleteAllCapturedImages(arrayListImgPath);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        //error
                        default:
                            CommonClass.showSnackbarMessage(rL_rootElement, postProductMainPojo.getMessage());
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    isToPostItem = true;
                    //progress_bar_post.setVisibility(View.GONE);
                    if (mDialogBox.progressBarDialog != null)
                        mDialogBox.progressBarDialog.dismiss();
                    //tV_post.setVisibility(View.VISIBLE);
                    CommonClass.showSnackbarMessage(rL_rootElement, error);
                }
            });
        } else
            CommonClass.showSnackbarMessage(rL_rootElement, getResources().getString(R.string.NoInternetAccess));
    }

    private void shareOnFacebook(String link, String thumbnail, String name, String description) {
        facebook_share_mamager.shareLinkOnFacebook(link, thumbnail, name, description, new Facebook_share_mamager.Share_callback() {
            @Override
            public void onSucess_share() {
                System.out.println(TAG + " " + "facebook successfully shared");
            }

            @Override
            public void onError(String error) {
                System.out.println(TAG + " " + "facebook successfully failed");
            }
        });
    }

    /**
     * <h2>share_On_Twitter</h2>
     * <p>
     * Sharing the data in twitter by crating the
     * link.
     * </P>
     */
    public void share_On_Twitter(String postId, String caption) {
        if (caption == null) {
            caption = "";
        } else {
            caption = "@" + caption;
        }
        String post_url = getResources().getString(R.string.share_item_base_url) + postId;
        Log.d("daste", "" + post_url);
        TweetManger.getInstance().updateStatus(caption, post_url, new TweetManger.TweetSuccess() {
            @Override
            public void onSuccess() {
                System.out.println(TAG + " " + "twitter successfully shared");
                //Toast.makeText(Home_container.this, R.string.share_text, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed() {
                System.out.println(TAG + " " + "twitter successfully failed");
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    /*
    Sharign the data in instagram. */
    private void shareOnInsta(String mediaPath, String capsion) {

        Log.d("insta image path", mediaPath);

        String type = "image/*";
        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TITLE, capsion);
        share.setPackage("com.instagram.android");

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));

    }

    /**
     * <h>deleteAllCapturedImages</h>
     * <p>
     * In this method we used to delete all the captured images which is taken by the camera.
     * </p>
     *
     * @param mList The list containing the images
     */
    public void deleteAllCapturedImages(ArrayList<String> mList) {
        try {
            File f;
            boolean deleted = false;

            for (int i = 0; i < mList.size(); i++) {

                f = new File(mList.get(i));
                if (f.exists()) {
                    f.delete();
                    deleted = true;
                }
            }

            if (deleted) {
                MediaScannerConnection.scanFile(this, new String[]{Environment.getExternalStorageDirectory().toString()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    /*
                     *   (non-Javadoc)
                     * @see android.media.MediaScannerConnection.OnScanCompletedListener#onScanCompleted(java.lang.String, android.net.Uri)
                     */
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            // back button
            case R.id.rL_back_btn:
                if (isToPostItem)
                    onBackPressed();
                break;

            // category
            case R.id.rL_product_category:
                if (isToPostItem) {
                    intent = new Intent(mActivity, ProductCategoryActivity.class);
                    startActivityForResult(intent, VariableConstants.CATEGORY_REQUEST_CODE);
                }
                break;

            // conditions
            case R.id.rL_conditions:
                if (isToPostItem) {
                    intent = new Intent(mActivity, PostConditionsActivity.class);
                    startActivityForResult(intent, VariableConstants.CONDITION_REQUEST_CODE);
                }
                break;

            // currency
            case R.id.rL_currency:
                if (isToPostItem) {
                    intent = new Intent(mActivity, CurrencyListActivity.class);
                    startActivityForResult(intent, VariableConstants.CURRENCY_REQUEST_CODE);
                }
                break;

            // change location
            case R.id.tV_change_loc:
                if (isToPostItem) {
                    intent = new Intent(mActivity, ChangeLocationActivity.class);
                    startActivityForResult(intent, VariableConstants.CHANGE_LOC_REQ_CODE);
                }
                break;

            // post product
            case R.id.rL_postProduct:
                checkPostProduct();
                break;

            case R.id.btn_cancel:
                Intent intent1 = new Intent(mActivity, HomePageActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mActivity.startActivity(intent1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // for facebook
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            System.out.print("abc");
        } else if (requestCode == VariableConstants.TWEETER_REQUEST_CODE) {
            // for twitter
            if (resultCode == RESULT_OK) {
                if (TweetManger.getInstance().isUserLoggedIn()) {
                    isTwitterSharingOn = true;
                }
            }
        } else if (data != null) {
            switch (requestCode) {
                // category name
                case VariableConstants.CATEGORY_REQUEST_CODE:
                    String categoryName = data.getStringExtra("categoryName");
                    if (categoryName != null) {
                        tV_category.setText(categoryName);
                        eT_price.requestFocus();
                    }
                    break;

                // condition
                case VariableConstants.CONDITION_REQUEST_CODE:
                    String condition = data.getStringExtra("condition");
                    if (condition != null) {
                        tV_condition.setText(condition);
                        eT_price.requestFocus();
                    }
                    break;

                // currency symbol
                case VariableConstants.CURRENCY_REQUEST_CODE:
                    String cuurency_code = data.getStringExtra("cuurency_code");
                    String currency_symbol = data.getStringExtra("currency_symbol");

                    // set currency cde eg. Inr
                    if (cuurency_code != null)
                        tV_currency.setText(cuurency_code);

                    // set currency symbol eg. $
                    if (currency_symbol != null)
                        tV_currency_symbol.setText(currency_symbol);
                    break;

                // selected location
                case VariableConstants.CHANGE_LOC_REQ_CODE:
                    String placeName = data.getStringExtra("address");
                    lat = data.getStringExtra("lat");
                    lng = data.getStringExtra("lng");
                    Double latDouble = Double.parseDouble(lat);
                    Double longDouble = Double.parseDouble(lng);


                    System.out.println(TAG + " " + "place name=" + placeName + " " + "lat=" + lat + " " + "lng=" + lng);
                    if (placeName != null && !placeName.isEmpty()) {
                        tV_current_location.setText(CommonClass.getCityName(mActivity, latDouble, longDouble) + " " + CommonClass.getCityName(mActivity, latDouble, longDouble));
                    } else {
                        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=+" + lat + "+,+" + lng + "+&location_type=ROOFTOP&result_type=street_address&key=" + getResources().getString(R.string.google_map_api_key);
                        // call google map api
                        new RequestTask().execute(url);

                    }
                    double latitude, longitude;
                    latitude = Double.parseDouble(lat);
                    longitude = Double.parseDouble(lng);
                    city = CommonClass.getCityName(mActivity, latitude, longitude);
                    countryCode = CommonClass.getCountryCode(mActivity, latitude, longitude);

                    Log.d("updated city", city);
                    Log.d("updated country code", countryCode);
                    break;


            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case VariableConstants.PERMISSION_REQUEST_CODE:
                System.out.println("grant result=" + grantResults.length);
                if (grantResults.length > 0) {
                    for (int count = 0; count < grantResults.length; count++) {
                        if (grantResults[count] != PackageManager.PERMISSION_GRANTED)
                            runTimePermission.allowPermissionAlert(permissions[count]);

                    }
                    System.out.println("isAllPermissionGranted=" + runTimePermission.checkPermissions(permissionsArray));
                    if (runTimePermission.checkPermissions(permissionsArray)) {
                        getCurrentLocation();
                    }
                }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }


    public Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

}
