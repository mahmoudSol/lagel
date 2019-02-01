package com.lagel.com.main.activity.products;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.lagel.com.R;
import com.lagel.com.fcm_push_notification.Config;
import com.lagel.com.fcm_push_notification.NotificationMessageDialog;
import com.lagel.com.fcm_push_notification.NotificationUtils;

/**
 * <h>ProductsMapActivity</h>
 * <p>
 *     In this class we used to set the google map of the particular product location
 *     with center marker position.
 * </p>
 * @since 01-Jun-17
 * @version 1.0
 * @author 3Embed
 */
public class ProductsMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener
{
    private String latitude="",longitude="",place="";
    private NotificationMessageDialog mNotificationMessageDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_map);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);

        Activity activity=ProductsMapActivity.this;
        mNotificationMessageDialog=new NotificationMessageDialog(activity);

        // receiving datas from last activity
        Intent intent=getIntent();
        // latitude,longitude
        latitude=intent.getStringExtra("latitude");
        longitude=intent.getStringExtra("longitude");
        place=intent.getStringExtra("place");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
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
    public void onMapReady(GoogleMap map) {
        LatLng productLocation = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(productLocation, 11));

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.add_marker);
        /*map.addMarker(new MarkerOptions()
                .title(place)
                .icon(icon)
                .position(productLocation));*/
        map.addCircle(new CircleOptions()
        .center(productLocation).radius(7000)
                .fillColor(getResources().getColor(R.color.map_circle)))
                .setStrokeWidth(0.0f);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
        }
    }
}