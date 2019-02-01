package com.lagel.com.main.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationSettingsResult;
import com.lagel.com.R;
import com.lagel.com.county_code_picker.Country;
import com.lagel.com.database.CountryData;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class CountrySearchFragment extends Fragment {
    private TextView tv_title1;
    private String rubro="";
    RecyclerView recyclerView;
    private CountryAdapter groupAdapter;
    private RelativeLayout rl_search;
    private EditText searchEditText;
    private Button menu_cancel,clear_txt;
    private RelativeLayout rl_main_screen;
    private View view;

    public CountrySearchFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.country_fragment,container,false);
        settingData(view);
        //String text=getFacebookHashKey(getActivity());


        /*if (getArguments()!=null)
        {
            rubro = getArguments().getString("rubro");
        }

        rl_main_screen= (RelativeLayout) view.findViewById(R.id.rl_main_screen);
        //rl_main_screen.setBackgroundResource(R.drawable.fondo1);

        //Utils.changeTitle(rubro,getActivity());
        settingData(view);*/



        setSearch(view);

        return view;
    }

    private void settingData(View view)
    {
        try {
            recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
            ArrayList<Country> country= CountryData.instance(getActivity()).getAllCountry();
            getData(country);
            //resultBean_ = InsideDatabase.instance(getActivity()).getAll();

        }catch (Exception e)
        {
            Log.e("DTS",e.getMessage());
        }
    }



   private void getData(ArrayList<Country> resultBean)
    {
        groupAdapter = new CountryAdapter(getActivity(), resultBean, this,rubro);
        //recyclerViewTeams.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(groupAdapter);
    }



   /* public void refreshList(int pos)
    {
        try {
            resultBean_ = InsideDatabase.instance(getActivity()).groupFavorites();
            groupAdapter = new MatchAdapter(getActivity(), resultBean_, this, rubro);
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
            recyclerView.setAdapter(groupAdapter);
        }catch(Exception e)
        {
            Log.e("error",e.getMessage());
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos si el resultado de la segunda actividad es "RESULT_CANCELED".
        if (resultCode == getActivity().RESULT_CANCELED) {
            // Si es así mostramos mensaje de cancelado por pantalla.

        } else {
            // De lo contrario, recogemos el resultado de la segunda actividad.

        }


    }


    private void setSearch(View view)
    {
        rl_search= (RelativeLayout) view.findViewById(R.id.rl_search);
        rl_search.setEnabled(false);
        menu_cancel= (Button) view.findViewById(R.id.menu_cancel);

        clear_txt= (Button)view.findViewById(R.id.clear_txt);
        clear_txt.setOnClickListener(mClearText);

        searchEditText = (EditText)view.findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                //if (s!=null && s.length()>0)
                //  callVolley(s.toString());
                if  (s!=null && s.length()>0)
                {
                    try {
                        ArrayList<Country> country = CountryData.instance(getActivity()).getCountryFilter(s.toString());
                        getData(country);
                    }catch (Exception ex)
                    {
                        Log.e("Error",ex.getMessage());

                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
              //  menu_cancel.setVisibility(View.VISIBLE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
            }

        });

        Button menu_cancel= (Button)view.findViewById(R.id.menu_cancel);
        menu_cancel.setOnClickListener(mCancelListener);


    }


    final View.OnClickListener mCancelListener = new View.OnClickListener() {

        @Override
        public void onClick(View view2) {
            menu_cancel.setVisibility(View.GONE);
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
            searchEditText.setText("");

            ArrayList<Country> country= CountryData.instance(getActivity()).getAllCountry();
            getData(country);

           // settingData(view);
        }
    };


    public void getCountry(String code)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("code",code );
        getActivity().setResult(Activity.RESULT_OK, resultIntent);

        Log.i("TAG",""+code);
        //getActivity().setResult(RESULT_OK);
        getActivity().finish();
    }

    final View.OnClickListener mClearText = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            searchEditText.setText("");
            ArrayList<Country> country= CountryData.instance(getActivity()).getAllCountry();
            getData(country);
        }
    };




}
