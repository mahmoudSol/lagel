package com.lagel.com.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.lagel.com.R;
import com.lagel.com.event_bus.BusProvider;
import com.lagel.com.main.activity.UserLikesActivity;
import com.lagel.com.main.tab_fragments.SocialFrag;
import com.lagel.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import com.lagel.com.pojo_class.likedPosts.LikedPostResponseDatas;
import com.lagel.com.pojo_class.social_frag_pojo.SocialDatas;
import com.lagel.com.utility.ApiCall;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CircleTransform;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.ProductItemClickListener;
import com.lagel.com.utility.SessionManager;
import java.util.ArrayList;
import java.util.Currency;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * <h></h>
 * <p>
 *     In class is called from SocialFrag. In this recyclerview adapter class we used to inflate
 *     single_row_home_news_feed layout and shows the all posted products.
 * </p>
 * @since 4/6/2017
 */
public class SocialFragRvAdapter extends RecyclerView.Adapter<SocialFragRvAdapter.MyViewHolder>
{
    private Activity mActivity;
    private ArrayList<SocialDatas> arrayListNewsFeedDatas;
    private static final String TAG= SocialFrag.class.getSimpleName();
    private SessionManager mSessionManager;
    private ApiCall apiCall;
    private RelativeLayout rL_rootview;
    private ProductItemClickListener clickListener;

    /**
     * <h>CurrencyRvAdap</h>
     * <p>
     *     This is simple constructor to initailize list datas and context.
     * </p>
     * @param mActivity The current context
     * @param arrayListNewsFeedDatas The list datas
     */
    public SocialFragRvAdapter(Activity mActivity, ArrayList<SocialDatas> arrayListNewsFeedDatas, RelativeLayout rL_rootview) {
        this.mActivity = mActivity;
        this.arrayListNewsFeedDatas = arrayListNewsFeedDatas;
        this.rL_rootview=rL_rootview;
        mSessionManager=new SessionManager(mActivity);
        apiCall=new ApiCall(mActivity);
    }

    /**
     * <h>OnCreateViewHolder</h>
     * <p>
     *     In this method The adapter prepares the layout of the items by inflating the correct
     *     layout for the individual data elements.
     * </p>
     * @param parent A ViewGroup is a special view that can contain other views (called children.)
     * @param viewType Within the getItemViewType method the recycler view determines which type should be used for data.
     * @return It returns an object of type ViewHolder per visual entry in the recycler view.
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(mActivity).inflate(R.layout.single_row_home_news_feed,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * <h>OnBindViewHolder</h>
     * <p>
     *     In this method Every visible entry in a recycler view is filled with the
     *     correct data model item by the adapter. Once a data item becomes visible,
     *     the adapter assigns this data to the individual widgets which he inflated
     *     earlier.
     * </p>
     * @param holder The referece of MyViewHolder class of current class.
     * @param position The position of particular item
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position)
    {
        String time,productName,categoryName="",currency,price, profilePic,productImage,viewCount,membername;
        final String likeCount,likeStatus,postId;

        time=arrayListNewsFeedDatas.get(position).getPostedOn();
        productName=arrayListNewsFeedDatas.get(position).getProductName();
        if (arrayListNewsFeedDatas.get(position).getCategoryData()!=null)
        categoryName=arrayListNewsFeedDatas.get(position).getCategoryData().get(0).getCategory();
        currency=arrayListNewsFeedDatas.get(position).getCurrency();
        price=arrayListNewsFeedDatas.get(position).getPrice();
        profilePic=arrayListNewsFeedDatas.get(position).getMemberProfilePicUrl();
        productImage=arrayListNewsFeedDatas.get(position).getMainUrl();
        likeCount =arrayListNewsFeedDatas.get(position).getLikes();
        viewCount=arrayListNewsFeedDatas.get(position).getClickCount();
        likeStatus =arrayListNewsFeedDatas.get(position).getLikeStatus();
        postId=arrayListNewsFeedDatas.get(position).getPostId();
        membername=arrayListNewsFeedDatas.get(position).getMembername();

        // User name
        //if (userName!=null)
           // holder.tV_userName.setText(userName);

        // user profile image
        if (profilePic!=null && !profilePic.isEmpty())
            Picasso.with(mActivity)
                    .load(profilePic)
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.default_circle_img)
                    .error(R.drawable.default_circle_img)
                    .into(holder.iV_profilePic);

        // Posted on
        if (time!=null && !time.isEmpty())
            holder.tV_time.setText(CommonClass.getTimeDifference(time));

        // product image
        try {
            if (productImage!=null && !productImage.isEmpty())
            Glide.with(mActivity)
                    .load(productImage)
                    .asBitmap()
                    .placeholder(R.color.add_title)
                    .error(R.color.add_title)
                    .into(holder.iV_productImage);
        } catch (OutOfMemoryError | IllegalArgumentException e) {
            e.printStackTrace();
        }

        // set currency
        if (currency!=null && !currency.isEmpty())
        {
            Currency c  = Currency.getInstance(currency);
            currency=c.getSymbol();
            System.out.println(TAG+" "+"symbol="+currency);
            holder.tV_currency.setText(currency);
        }

        // set Price
        if (price!=null && !price.isEmpty())
            holder.tV_price.setText(price);

        // set product name
        if (productName!=null && !productName.isEmpty())
        {
            productName=productName.substring(0,1).toUpperCase()+productName.substring(1).toLowerCase();
            holder.tV_productname.setText(membername);
        }

        // set product's category name
        if (categoryName!=null && !categoryName.isEmpty())
        {
            holder.tV_categoryName.setText(productName);
        }

        // set like status
        if (likeStatus!=null && likeStatus.equals("1"))
        {
            holder.iV_like_icon.setImageResource(R.drawable.like_icon_on);
            holder.tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
            holder.rL_like.setBackgroundResource(R.drawable.rect_pink_color_with_stroke_shape);
        }
        else
        {
            holder.iV_like_icon.setImageResource(R.drawable.like_icon_off);
            holder.tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.hide_button_border_color));
            holder.rL_like.setBackgroundResource(R.drawable.rect_gray_color_with_with_stroke_shape);
        }

        // set total view count
        if (viewCount!=null && !viewCount.isEmpty())
            holder.tV_view_count.setText(viewCount);

        // set like total count
        if (likeCount!=null && !likeCount.isEmpty())
            holder.tV_like_count.setText(likeCount);

        // To show liked By Users
        final ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers=arrayListNewsFeedDatas.get(position).getLikedByUsers();
        inflateUserLikes(likeCount,aL_likedByUsers,holder.linear_likedByUsers);

        // Like product
        holder.rL_like.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (CommonClass.isNetworkAvailable(mActivity))
                {
                    String mLikesCount=arrayListNewsFeedDatas.get(holder.getAdapterPosition()).getLikes();
                    String mLikesStatus=arrayListNewsFeedDatas.get(holder.getAdapterPosition()).getLikeStatus();

                    int mLikeCount=0;
                    if (mLikesCount !=null && !mLikesCount.isEmpty())
                        mLikeCount=Integer.parseInt(mLikesCount);

                    // unlike
                    if (mLikesStatus!=null && mLikesStatus.equals("1"))
                    {
                        addFavouriteData(holder.getAdapterPosition(),false);

                        // remove my self
                        if (aL_likedByUsers.size()>0)
                        {
                            for (int likeCount=0;likeCount<aL_likedByUsers.size();likeCount++)
                            {
                                if (aL_likedByUsers.get(likeCount).getLikedByUsers().equals(mSessionManager.getUserName()))
                                {
                                    aL_likedByUsers.remove(likeCount);
                                }
                            }
                        }

                        mLikeCount-=1;
                        inflateUserLikes(mLikeCount+"",aL_likedByUsers,holder.linear_likedByUsers);

                        holder.tV_like_count.setText(String.valueOf(mLikeCount));
                        //mLikesStatus = "0";
                        arrayListNewsFeedDatas.get(holder.getAdapterPosition()).setLikeStatus("0");
                        arrayListNewsFeedDatas.get(holder.getAdapterPosition()).setLikes(mLikeCount+"");

                        holder.iV_like_icon.setImageResource(R.drawable.like_icon_off);
                        holder.tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.hide_button_border_color));
                        holder.rL_like.setBackgroundResource(R.drawable.rect_gray_color_with_with_stroke_shape);

                        apiCall.likeProductApi(ApiUrl.UNLIKE_PRODUCT,postId);
                    }

                    // like
                    else {
                        // set event bus to add favourite data
                        addFavouriteData(holder.getAdapterPosition(),true);

                        // add myself
                        ExploreLikedByUsersDatas likedByUsersDatas=new ExploreLikedByUsersDatas();
                        likedByUsersDatas.setLikedByUsers(mSessionManager.getUserName());
                        likedByUsersDatas.setProfilePicUrl(mSessionManager.getUserImage());

                        //..handle first time like..//
                        if(aL_likedByUsers.size()>0 && mLikeCount==0){
                            if(aL_likedByUsers.get(0).getName().equals("")){
                                aL_likedByUsers.remove(0);
                            }
                        }
                        //..handle end..//

                        aL_likedByUsers.add(0,likedByUsersDatas);
                        mLikeCount+=1;
                        inflateUserLikes(mLikeCount+"",aL_likedByUsers,holder.linear_likedByUsers);

                        holder.tV_like_count.setText(String.valueOf(mLikeCount));
                        //mLikesStatus = "1";
                        arrayListNewsFeedDatas.get(holder.getAdapterPosition()).setLikeStatus("1");
                        arrayListNewsFeedDatas.get(holder.getAdapterPosition()).setLikes(mLikeCount+"");

                        holder.iV_like_icon.setImageResource(R.drawable.like_icon_on);
                        holder.tV_like_count.setTextColor(ContextCompat.getColor(mActivity,R.color.pink_color));
                        holder.rL_like.setBackgroundResource(R.drawable.rect_pink_color_with_stroke_shape);

                        apiCall.likeProductApi(ApiUrl.LIKE_PRODUCT,postId);
                    }

                    System.out.println(TAG+" "+"mLike count="+mLikeCount);
                }
                else CommonClass.showSnackbarMessage(rL_rootview,mActivity.getResources().getString(R.string.NoInternetAccess));
            }
        });

        // Get user all likes
        holder.iV_three_dots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mActivity, UserLikesActivity.class);
                intent.putExtra("postId",arrayListNewsFeedDatas.get(holder.getAdapterPosition()).getPostId());
                intent.putExtra("postType",arrayListNewsFeedDatas.get(holder.getAdapterPosition()).getPostsType());
                mActivity.startActivity(intent);
            }
        });

        ViewCompat.setTransitionName(holder.iV_productImage, arrayListNewsFeedDatas.get(position).getProductName());

        // click on item view
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener!=null)
                clickListener.onItemClick(holder.getAdapterPosition(),holder.iV_productImage);
            }
        });
    }

    /**
     * <h>InflateUserLikes</h>
     * <p>
     *     In this method we used to inflate the user likes list into
     *     LinearLayout horizontally.
     * </p>
     */
    private void inflateUserLikes(String likesCount,ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers,LinearLayout linear_followed_images)
    {
        int mLikeCount=0;
        if (likesCount!=null && !likesCount.isEmpty())
            mLikeCount=Integer.parseInt(likesCount);

        if (mLikeCount>0)
        {
            if (aL_likedByUsers!=null && aL_likedByUsers.size()>0)
            {
                linear_followed_images.removeAllViews();
                for (int likedCount=0;likedCount<aL_likedByUsers.size();likedCount++)
                {
                    LayoutInflater layoutInflater= (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View followedView=layoutInflater.inflate(R.layout.single_row_images,null);
                    ImageView viewPagerItem_image = (ImageView)followedView.findViewById(R.id.iV_image);
                    viewPagerItem_image.setBackgroundColor(ContextCompat.getColor(mActivity,R.color.white));
                    viewPagerItem_image.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/9;
                    viewPagerItem_image.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/9;
                    viewPagerItem_image.setImageResource(R.drawable.default_circle_img);

                    String likedUserImg=aL_likedByUsers.get(likedCount).getProfilePicUrl();
                    //viewPagerItem_image.setX(getResources().getDimension(R.dimen.dim));
                    if (likedUserImg != null && !likedUserImg.isEmpty()) {
                        Picasso.with(mActivity)
                                .load(likedUserImg)
                                .placeholder(R.drawable.default_circle_img)
                                .error(R.drawable.default_circle_img)
                                .transform(new CircleTransform())
                                .into(viewPagerItem_image);
                    }
                    linear_followed_images.addView(followedView);
                }
            }
        }
        else linear_followed_images.removeAllViews();
    }

    /**
     * <h>AddFavouriteData</h>
     * <p>
     *     In this method we used to make a Liked pojo class instance and set all required param.
     *     Once we done it then just pass one event to pass the liked image datas to the Fav frag.
     * </p>
     * @param position The position of image liked image
     */
    private void addFavouriteData(int position,boolean isToAdd)
    {
        LikedPostResponseDatas likedPostCategoryDatas=new LikedPostResponseDatas();
        likedPostCategoryDatas.setToAddLikedItem(isToAdd);
        likedPostCategoryDatas.setMainUrl(arrayListNewsFeedDatas.get(position).getMainUrl());
        likedPostCategoryDatas.setProductName(arrayListNewsFeedDatas.get(position).getProductName());
        likedPostCategoryDatas.setLikes(arrayListNewsFeedDatas.get(position).getLikes());
        likedPostCategoryDatas.setLikeStatus(arrayListNewsFeedDatas.get(position).getLikeStatus());
        likedPostCategoryDatas.setCurrency(arrayListNewsFeedDatas.get(position).getCurrency());
        likedPostCategoryDatas.setPrice(arrayListNewsFeedDatas.get(position).getPrice());
        likedPostCategoryDatas.setPostedOn(arrayListNewsFeedDatas.get(position).getPostedOn());
        likedPostCategoryDatas.setThumbnailImageUrl(arrayListNewsFeedDatas.get(position).getThumbnailImageUrl());
        likedPostCategoryDatas.setLikedByUsers(arrayListNewsFeedDatas.get(position).getLikedByUsers());
        //likedPostCategoryDatas.setDescription(arrayListNewsFeedDatas.get(position).getDescription());
        likedPostCategoryDatas.setCondition(arrayListNewsFeedDatas.get(position).getCondition());
        likedPostCategoryDatas.setPlace(arrayListNewsFeedDatas.get(position).getPlace());
        likedPostCategoryDatas.setLatitude(arrayListNewsFeedDatas.get(position).getLatitude());
        likedPostCategoryDatas.setLongitude(arrayListNewsFeedDatas.get(position).getLongitude());
        likedPostCategoryDatas.setPostId(arrayListNewsFeedDatas.get(position).getPostId());
        likedPostCategoryDatas.setPostsType(arrayListNewsFeedDatas.get(position).getPostsType());
        likedPostCategoryDatas.setContainerWidth(arrayListNewsFeedDatas.get(position).getContainerWidth());
        likedPostCategoryDatas.setContainerHeight(arrayListNewsFeedDatas.get(position).getContainerHeight());
        BusProvider.getInstance().post(likedPostCategoryDatas);
    }

    /**
     * Return the size of your dataset
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListNewsFeedDatas.size();
    }

    /**
     * <h>MyViewHolder</h>
     * <p>
     *     In this class we used to declare and assign the xml variables.
     * </p>
     */
    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView iV_profilePic,iV_productImage,iV_like_icon,iV_three_dots;
        TextView tV_userName,tV_time,tV_currency,tV_price,tV_like_count,tV_view_count,tV_productname,tV_categoryName;
        LinearLayout linear_likedByUsers;
        RelativeLayout rL_like;
        private View mView;

        MyViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            iV_profilePic= (ImageView)itemView.findViewById(R.id.iV_profilePic);
            iV_profilePic.getLayoutParams().width=CommonClass.getDeviceWidth(mActivity)/8;
            iV_profilePic.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity)/8;
            iV_productImage= (ImageView)itemView.findViewById(R.id.iV_productImage);
            iV_productImage.getLayoutParams().height=CommonClass.getDeviceWidth(mActivity);
            iV_like_icon= (ImageView)itemView.findViewById(R.id.like_item_icon);
            iV_three_dots= (ImageView)itemView.findViewById(R.id.iV_followed_list);
            tV_userName= (TextView)itemView.findViewById(R.id.tV_userName);
            tV_time= (TextView)itemView.findViewById(R.id.tV_time);
            tV_currency= (TextView)itemView.findViewById(R.id.tV_currency);
            tV_price= (TextView)itemView.findViewById(R.id.tV_price);
            tV_like_count= (TextView)itemView.findViewById(R.id.tV_like_count);
            tV_view_count= (TextView)itemView.findViewById(R.id.tV_view_count);
            linear_likedByUsers= (LinearLayout)itemView.findViewById(R.id.linear_followed_images);
            tV_productname= (TextView) itemView.findViewById(R.id.tV_productname);
            tV_categoryName= (TextView) itemView.findViewById(R.id.tV_categoryName);
            rL_like= (RelativeLayout) itemView.findViewById(R.id.relative_like_product);
        }
    }
    public void setItemClick(ProductItemClickListener listener)
    {
        clickListener=listener;
    }
}
