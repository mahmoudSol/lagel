package com.lagel.com.lalita.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.lalita.SearchActivity;
import com.lagel.com.lalita.utils.ObjectSerializer;
import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas;
import com.lagel.com.utility.VariableConstants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lalita Gill on 12/02/18.
 */

public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> audioDataList;
    private boolean shouldVisible = false;


    public SearchAdapter(Context context, ArrayList<String> listMediaData) {
        this.context = context;
        this.audioDataList = listMediaData;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_list, parent, false);
        return new SearchAdapter.ViewHolder(view);

    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return audioDataList.size();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final SearchAdapter.ViewHolder viewHolder = (SearchAdapter.ViewHolder) holder;
        viewHolder.textViewSearchName.setText("" + audioDataList.get(position));
        // Log.e("Value", "" + shouldVisible);

        if (shouldVisible) {
            viewHolder.imageViewCross.setVisibility(View.VISIBLE);
        } else {
            viewHolder.imageViewCross.setVisibility(View.GONE);
        }

        viewHolder.imageViewCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                audioDataList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, audioDataList.size());
                SharedPreferences prefs = context.getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();

                try {
                    editor.putString("LIST", ObjectSerializer.serialize(audioDataList));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                editor.commit();
            }
        });

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSearchName;
        ImageView imageViewCross;


        public ViewHolder(final View itemView) {
            super(itemView);
            textViewSearchName = (TextView) itemView.findViewById(R.id.textSearchResult);
            imageViewCross = (ImageView) itemView.findViewById(R.id.imageViewCross);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductCategoryResDatas datas = new ProductCategoryResDatas();
                    datas.setSelected(true);
                    datas.setName(audioDataList.get(getAdapterPosition()));

                    ArrayList<ProductCategoryResDatas> aL_categoryDatas = new ArrayList<>();
                    aL_categoryDatas.add(datas);
                    Intent filterIntent = new Intent();
                    filterIntent.putExtra("aL_categoryDatas", aL_categoryDatas);
                    filterIntent.putExtra("distance", "");
                    filterIntent.putExtra("currentLatitude", "");
                    filterIntent.putExtra("currentLongitude", "");
                    filterIntent.putExtra("address", "");
                    filterIntent.putExtra("sortBy", "");
                    filterIntent.putExtra("postedWithin", "");
                    filterIntent.putExtra("minPrice", "");
                    filterIntent.putExtra("maxPrice", "");
                    filterIntent.putExtra("currency_code", "");
                    filterIntent.putExtra("currency", "");
                    filterIntent.putExtra("postedWithinText", "");
                    filterIntent.putExtra("sortByText", "");

                    ((SearchActivity) context).setResult(VariableConstants.FILTER_REQUEST_SEARCH, filterIntent);
                    ((SearchActivity) context).finish();
                }
            });
        }
    }

    /**
     * Showing cross icon
     *
     * @param shouldVisible
     */

    public void showCrossIcon(boolean shouldVisible) {
        this.shouldVisible = shouldVisible;


        this.notifyDataSetChanged();
    }

}






