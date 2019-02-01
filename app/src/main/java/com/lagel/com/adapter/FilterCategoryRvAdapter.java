package com.lagel.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lagel.com.R;
import com.lagel.com.lalita.utils.ObjectSerializer;
import com.lagel.com.pojo_class.product_category.ProductCategoryResDatas;
import com.lagel.com.utility.CommonClass;

import java.io.IOException;
import java.util.ArrayList;

/**
 * <h>FilterCategoryRvAdapter</h>
 * <p>
 * In class is called from FilterActivity class. In this recyclerview adapter class we used to inflate
 * single_row_filter_category layout and shows the product all category.
 * </p>
 *
 * @author 3Embed.
 * @version 1.0
 * @since 17-May-17
 */
public class FilterCategoryRvAdapter extends RecyclerView.Adapter<FilterCategoryRvAdapter.MyViewHolder> {
    private Activity mActivity;
    private ArrayList<ProductCategoryResDatas> aL_categoryDatas;
    private int selectedColor, unSelectedColor;
    private ArrayList<String> arrayListSelction;

    /**
     * <h>FilterCategoryRvAdapter</h>
     * <p>
     * This is simple constructor to initailize list datas and context.
     * </p>
     *
     * @param mActivity        The current context
     * @param aL_categoryDatas The list datas
     */
    public FilterCategoryRvAdapter(Activity mActivity, ArrayList<ProductCategoryResDatas> aL_categoryDatas) {
        this.mActivity = mActivity;
        this.aL_categoryDatas = aL_categoryDatas;
        selectedColor = ContextCompat.getColor(mActivity, R.color.status_bar_color);
        unSelectedColor = ContextCompat.getColor(mActivity, R.color.heading_color);
        arrayListSelction = new ArrayList<>();
        // load tasks from preference
        SharedPreferences prefs = mActivity.getSharedPreferences("ArrayListPref", Context.MODE_PRIVATE);

        try {
            arrayListSelction = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString("LIST_VALUE_FILTER", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * <h>OnCreateViewHolder</h>
     * <p>
     * In this method The adapter prepares the layout of the items by inflating the correct
     * layout for the individual data elements.
     * </p>
     *
     * @param parent   A ViewGroup is a special view that can contain other views (called children.)
     * @param viewType Within the getItemViewType method the recycler view determines which type should be used for data.
     * @return It returns an object of type ViewHolder per visual entry in the recycler view.
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_row_filter_category, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * <h>OnBindViewHolder</h>
     * <p>
     * In this method Every visible entry in a recycler view is filled with the
     * correct data model item by the adapter. Once a data item becomes visible,
     * the adapter assigns this data to the individual widgets which he inflated
     * earlier.
     * </p>
     *
     * @param holder   The referece of MyViewHolder class of current class.
     * @param position The position of particular item
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        String name;
        boolean isFilterCateSelected;
        final String seletedImage, unseletedImage;
        name = aL_categoryDatas.get(position).getName();


        // make first character of character is uppercase
        if (name != null && !name.isEmpty())

            /*String[] fullname=name.split(" ");
            String firstindx=fullname[0].substring(0,1).toUpperCase()+fullname[0].substring(1).toLowerCase();
            Log.d("1.indx",firstindx);
            String middleindx="";
            if(fullname.length>1){
                middleindx=fullname[1];
                Log.d("2.indx",middleindx);
                name=firstindx+" "+middleindx;
            }
            if(fullname.length>2) {
                String lastindx=fullname[2];
                lastindx=lastindx.substring(0,1).toUpperCase()+lastindx.substring(1).toLowerCase();
                Log.d("3.indx",lastindx);
                name=name+" "+lastindx;
            }*/
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        //aL_categoryDatas.get(position).setName(name);

        seletedImage = aL_categoryDatas.get(position).getActiveimage();
        unseletedImage = aL_categoryDatas.get(position).getDeactiveimage();
        isFilterCateSelected = aL_categoryDatas.get(position).isSelected();
        holder.tV_category.setTextColor(ContextCompat.getColor(mActivity, R.color.heading_color));
        if (arrayListSelction.contains(name)) {
            setCategoryImage(holder.iV_category, seletedImage, holder.tV_category, name, selectedColor);
        } else {
            setCategoryImage(holder.iV_category, unseletedImage, holder.tV_category, name, unSelectedColor);
        }
        // set values like category image and name
        if (isFilterCateSelected)
            setCategoryImage(holder.iV_category, seletedImage, holder.tV_category, name, selectedColor);
        else
            setCategoryImage(holder.iV_category, unseletedImage, holder.tV_category, name, unSelectedColor);

        // To select or unselect category
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isViewSelected = aL_categoryDatas.get(holder.getAdapterPosition()).isSelected();
                if (!isViewSelected) {
                    arrayListSelction.add(aL_categoryDatas.get(position).getName());

                    aL_categoryDatas.get(holder.getAdapterPosition()).setSelected(true);
                    setCategoryImage(holder.iV_category, seletedImage, holder.tV_category, aL_categoryDatas.get(holder.getAdapterPosition()).getName(), selectedColor);

                } else {

                    for (int i = 0; i < arrayListSelction.size(); i++) {
                        if (arrayListSelction.get(i).equals(aL_categoryDatas.get(position).getName())) {
                            arrayListSelction.remove(i);
                        }
                    }
                    aL_categoryDatas.get(holder.getAdapterPosition()).setSelected(false);
                    setCategoryImage(holder.iV_category, unseletedImage, holder.tV_category, aL_categoryDatas.get(holder.getAdapterPosition()).getName(), unSelectedColor);
                }
            }
        });
    }

    /**
     * <h>SetCategoryImage</h>
     * <p>
     * In this method we used to set category image for selected and unseleted sate.
     * </p>
     *
     * @param imageView The category image view.
     * @param url       The category image url.
     */
    private void setCategoryImage(ImageView imageView, String url, TextView tV_category, String categoryName, int color) {
        // set image
       /* if (url!=null && !url.isEmpty())
            Picasso.with(mActivity)
                    .load(url)
                    .placeholder(R.drawable.default_circle_img)
                    .error(R.drawable.default_circle_img)
                    .into(imageView);*/

        if (url != null && !url.isEmpty())
            Glide.with(mActivity)
                    .load(url)
                    //.placeholder(R.drawable.default_circle_img)
                    .error(R.drawable.default_circle_img)
                    .into(imageView);

        // set category name
        if (categoryName != null) {
            // make first character of character is uppercase
            categoryName = categoryName.substring(0, 1).toUpperCase() + categoryName.substring(1).toLowerCase();
            tV_category.setText(categoryName);
            tV_category.setTextColor(color);
        }
    }

    @Override
    public int getItemCount() {
        return aL_categoryDatas.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iV_category;
        private TextView tV_category;
        private View view;

        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setBackgroundResource(R.drawable.rect_border_color_with_stroke_shape);
            iV_category = (ImageView) itemView.findViewById(R.id.iV_category);
            iV_category.getLayoutParams().width = CommonClass.getDeviceWidth(mActivity) / 11;
            iV_category.getLayoutParams().height = CommonClass.getDeviceWidth(mActivity) / 11;
            tV_category = (TextView) itemView.findViewById(R.id.tV_category);
            tV_category.setTextColor(ContextCompat.getColor(mActivity, R.color.heading_color));
            view = itemView;
        }
    }

    public ArrayList<String> getCategorySelectedData() {
        return arrayListSelction;
    }

}
