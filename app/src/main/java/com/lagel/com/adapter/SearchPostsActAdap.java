package com.lagel.com.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.lagel.com.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by embed on 20/1/18.
 */

public class SearchPostsActAdap extends BaseAdapter implements Filterable {

    ArrayList<String> data;
    Context context;
    ArrayList<String> mStringFilterList;
    ValueFilter valueFilter;
    private final String recent_search_header="Recent";

    public SearchPostsActAdap(ArrayList<String> data, Context context) {
        this.data = data;
        this.context = context;
        this.mStringFilterList = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        @SuppressLint("ViewHolder") View v = LayoutInflater.from(context).inflate(R.layout.single_row_search_posts, null);

        TextView tv_postName = (TextView) v.findViewById(R.id.tv_postname);
        tv_postName.setText(data.get(i));

        if(tv_postName.getText().toString().equals(recent_search_header)){
            tv_postName.setTypeface(null, Typeface.BOLD_ITALIC);
            View view_divider = v.findViewById(R.id.view_divider);
            view_divider.setVisibility(View.GONE);
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public void setList(ArrayList<String> list) {
        data = list;
        notifyDataSetChanged();
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List filterList = new ArrayList();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        filterList.add(mStringFilterList.get(i));
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            data = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
