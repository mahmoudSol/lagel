package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.lagel.com.R;
import com.lagel.com.county_code_picker.Country;
import com.lagel.com.database.CountryData;

import java.util.ArrayList;

public class CountrySearchActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_main);

        TextView text_cancel= (TextView) findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(mClearText);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);


        /*final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            setTitle(R.string.select_country2);
        }
        else
        {
            getSupportActionBar().setTitle(R.string.select_country2);
        }*/

        // Initializing Toolbar and setting it as the actionbar

        countryFragment();
    }

    final View.OnClickListener mClearText = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

           // setResult(RESULT_OK);
            finish();
        }
    };

    private void countryFragment()
    {
        Bundle args = new Bundle();
        args.putString("rubro","");
        CountrySearchFragment fragTransaction = new CountrySearchFragment();
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,fragTransaction);
        fragTransaction.setArguments(args);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onBackPressed() {
        //setResult(RESULT_OK);
         finish();

    }

}
