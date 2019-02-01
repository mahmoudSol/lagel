package com.lagel.com.adapter;

import android.app.Activity;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lagel.com.utility.RoundedCornersTransform;
import com.squareup.picasso.Picasso;
import com.lagel.com.R;
import com.lagel.com.pojo_class.search_post_pojo.SearchPostDatas;
import com.lagel.com.utility.CircleTransform;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.ProductItemClickListener;

import java.util.ArrayList;

/**
 * Created by hello on 30-Jun-17.
 */
public class SearchPostsRvAdap extends RecyclerView.Adapter<SearchPostsRvAdap.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<SearchPostDatas> aL_searchedPosts;
    private ProductItemClickListener clickListener;


    public SearchPostsRvAdap(Activity mActivity, ArrayList<SearchPostDatas> aL_searchedPosts) {
        this.mActivity = mActivity;
        this.aL_searchedPosts = aL_searchedPosts;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_search_product,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        String productName,productImage,category;
        productName=aL_searchedPosts.get(position).getProductName();
        productImage=aL_searchedPosts.get(position).getMainUrl();
        category=aL_searchedPosts.get(position).getCategory();

        Log.d("imageUrl",productImage);

        // set Profile pic
        if (productImage!=null && !productImage.isEmpty())
            Picasso.with(mActivity)
                    .load(productImage)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .centerCrop()
                    .resize(CommonClass.getDeviceWidth(mActivity),CommonClass.getDeviceWidth(mActivity))
                    .into(holder.image);

        // set product name
        if (productName!=null && !productName.isEmpty())
            holder.tV_heading.setText(productName);

        // set category
        if (category!=null && !category.isEmpty())
            holder.tV_subHeading.setText(category);

        ViewCompat.setTransitionName(holder.image, aL_searchedPosts.get(position).getProductName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                    clickListener.onItemClick(holder.getAdapterPosition(),holder.image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return aL_searchedPosts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView tV_heading,tV_subHeading;
        View mView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            image= (ImageView) itemView.findViewById(R.id.image);
            image.getLayoutParams().width= CommonClass.getDeviceWidth(mActivity)/2;
            image.getLayoutParams().height= CommonClass.getDeviceWidth(mActivity)/2;
            tV_heading= (TextView) itemView.findViewById(R.id.tV_heading);
            tV_subHeading= (TextView) itemView.findViewById(R.id.tV_subHeading);
        }
    }
    public void setOnItemClick(ProductItemClickListener listener)
    {
        clickListener=listener;
    }
}
