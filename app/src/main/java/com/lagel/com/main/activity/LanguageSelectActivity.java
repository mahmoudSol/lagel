package com.lagel.com.main.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lagel.com.R;
import com.lagel.com.utility.SessionManager;

public class LanguageSelectActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private SessionManager mSessionManager;
    private Activity mActivity;
    private RadioButton rb_eg, rb_fr, rb_es, rb_zh;
    private RadioGroup rg_language;
    private String language_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_select);

        mActivity = LanguageSelectActivity.this;

        mSessionManager = new SessionManager(mActivity);

        // radio button for language
        rb_eg = (RadioButton) findViewById(R.id.rb_eg);
        rb_fr = (RadioButton) findViewById(R.id.rb_ar);
        rb_es = (RadioButton) findViewById(R.id.rb_es);
        rg_language = (RadioGroup) findViewById(R.id.rg_language);

        // intial checked button for current language
        currentLanguage(mSessionManager.getLanguageCode());

        rg_language.setOnCheckedChangeListener(this);

        RelativeLayout rL_back_btn = (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        RelativeLayout rL_done = (RelativeLayout) findViewById(R.id.rL_apply);
        rL_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSessionManager.setLanguageCode(language_code);
                Intent i = new Intent(mActivity, SplashActivity.class);
                // set the new task and clear flags
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.rb_eg:
                language_code = getString(R.string.default_language_code);
                break;
            case R.id.rb_ar:
                language_code = getString(R.string.french_language_code);
                break;
            case R.id.rb_es:
                language_code = getString(R.string.spanish_language_code);
                break;
        }
    }

    public void currentLanguage(String code) {
        switch (code) {
            case "en":
                rb_eg.setChecked(true);
                break;
            case "fr":
                rb_fr.setChecked(true);
                break;
            case "es":
                rb_es.setChecked(true);
                break;
        }
    }
}
