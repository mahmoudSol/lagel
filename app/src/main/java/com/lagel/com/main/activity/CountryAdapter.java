package com.lagel.com.main.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.county_code_picker.Country;

import java.util.ArrayList;


public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.ViewHolder> {
    private final CountrySearchFragment countrySearchFragment;
    private final Context context;
    ArrayList<Country> beanData;
    private String option;
    public CountryAdapter(Context context, ArrayList<Country> beanData, CountrySearchFragment partidosFragment, String option) {
        this.context = context;
        this.countrySearchFragment = partidosFragment;
        this.beanData=beanData;
        this.option=option;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(viewType, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
         final Country bean = beanData.get(position );

            holder.rl_main_screen1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String data=bean.getName()+"+"+bean.getCode();
                    countrySearchFragment.getCountry(data);
                }
            });

            holder.text_fecha.setText(bean.getFullname());
            holder.text_code.setText("+"+bean.getCode()+"   ");



    }

    private int selectedItem = 0;

    @Override
    public int getItemCount() {
        return beanData.size() ;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.country_list;
    }

    Bitmap b = null;

    //

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView text_fecha,text_code ;
        public RelativeLayout rl_main_screen1;

        public ViewHolder(View itemView) {

            super(itemView);

            text_fecha = (TextView) itemView.findViewById(R.id.text_fecha);
            text_code = (TextView) itemView.findViewById(R.id.text_code );
            rl_main_screen1 = (RelativeLayout) itemView.findViewById(R.id.rl_main_screen1 );


        }


    }



}
