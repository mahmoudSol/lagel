package com.lagel.com.main.tab_fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.squareup.otto.Subscribe;
import com.lagel.com.R;
import com.lagel.com.adapter.SocialFragRvAdapter;
import com.lagel.com.event_bus.BusProvider;
import com.lagel.com.main.activity.DiscoverPeopleActivity;
import com.lagel.com.main.activity.HomePageActivity;
import com.lagel.com.main.activity.products.ProductDetailsActivity;
import com.lagel.com.pojo_class.ProfileFollowingCount;
import com.lagel.com.pojo_class.home_explore_pojo.ExploreLikedByUsersDatas;
import com.lagel.com.pojo_class.social_frag_pojo.SocialDatas;
import com.lagel.com.pojo_class.social_frag_pojo.SocialMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.ProductItemClickListener;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * <h>SocialFrag</h>
 * <p>
 * In this class we used to show product list of the user those who has been followed by
 * the logged-in user.
 * </p>
 * @since 3/31/2017
 */
public class SocialFrag extends Fragment implements View.OnClickListener
{
    private static final String TAG=SocialFrag.class.getSimpleName();
    private Activity mActivity;
    private ProgressBar mProgress_bar;
    private SessionManager sessionManager;
    private LinearLayout linear_no_friends;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<SocialDatas> arrayListNewsFeedDatas;
    private SocialFragRvAdapter newsFeedRvAdapter;
    private int pageIndex=0;
    private RecyclerView rV_newsFeed;
    private LinearLayoutManager layoutManager;
    private int itemPosition;

    // Load more varaibles
    private int visibleThreshold=5, totalVisibleItem,totalItemCount;
    private boolean isLoadingRequired;
    private boolean isNewsFeedFragVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        sessionManager=new SessionManager(mActivity);
        arrayListNewsFeedDatas=new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_social, container, false);
        RelativeLayout rL_rootview = (RelativeLayout) view.findViewById(R.id.rL_rootview);
        mProgress_bar= (ProgressBar)view.findViewById(R.id.progress_bar_news_feed);
        rV_newsFeed = (RecyclerView) view.findViewById(R.id.rV_newsFeed);
        linear_no_friends= (LinearLayout) view.findViewById(R.id.linear_no_friends);
        linear_no_friends.setVisibility(View.GONE);
        RelativeLayout rL_find_friends= (RelativeLayout) view.findViewById(R.id.rL_find_friends);
        rL_find_friends.setOnClickListener(this);

        // swipe refresh
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.mSwipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);

        // set Recyclerview Adapter
        newsFeedRvAdapter=new SocialFragRvAdapter(mActivity,arrayListNewsFeedDatas, rL_rootview);
        layoutManager=new LinearLayoutManager(mActivity);
        rV_newsFeed.setLayoutManager(layoutManager);
        rV_newsFeed.setAdapter(newsFeedRvAdapter);

        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageIndex=0;
                arrayListNewsFeedDatas.clear();
                getNewsFeedDatas(pageIndex);
            }
        });

        // call news feed api to get datas
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            System.out.println(TAG+" "+"frag visible newsfeed="+isNewsFeedFragVisible);
            pageIndex=0;
            arrayListNewsFeedDatas.clear();
            mProgress_bar.setVisibility(View.VISIBLE);
            getNewsFeedDatas(pageIndex);
        }
        else CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        BusProvider.getInstance().register(this);
    }

    /**
     * <h>GetNewsFeedDatas</h>
     * <p>
     *     In this method we used to do api call to get all the social product.
     * </p>
     * @param offset The page index
     */
    private void getNewsFeedDatas(int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject request_datas = new JSONObject();

            int limit=20;
            offset=limit*offset;

            try {
                request_datas.put("token",sessionManager.getAuthToken());
                request_datas.put("offset",offset);
                request_datas.put("limit",offset);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.HOME, OkHttp3Connection.Request_type.POST, request_datas, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                    System.out.println(TAG+" "+"news feed res="+result);

                    if (result!=null && !result.isEmpty())
                    {
                        SocialMainPojo socialMainPojo;
                        Gson gson = new Gson();
                        socialMainPojo = gson.fromJson(result,SocialMainPojo.class);

                        switch (socialMainPojo.getCode())
                        {
                            // Success
                            case "200" :
                                mSwipeRefreshLayout.setRefreshing(false);
                                arrayListNewsFeedDatas.addAll(socialMainPojo.getData());
                                if (arrayListNewsFeedDatas!=null && arrayListNewsFeedDatas.size()>0)
                                {
                                    linear_no_friends.setVisibility(View.GONE);
                                    isLoadingRequired=arrayListNewsFeedDatas.size()>14;
                                    newsFeedRvAdapter.notifyDataSetChanged();

                                    // Load more
                                    rV_newsFeed.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                        @Override
                                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                            super.onScrolled(recyclerView, dx, dy);
                                            totalItemCount=layoutManager.getItemCount();
                                            totalVisibleItem=layoutManager.findLastVisibleItemPosition();

                                            if (isLoadingRequired && totalItemCount<=(totalVisibleItem+visibleThreshold))
                                            {
                                                pageIndex=pageIndex+1;
                                                getNewsFeedDatas(pageIndex);
                                                isLoadingRequired=false;
                                            }
                                        }
                                    });

                                    // Item click
                                    newsFeedRvAdapter.setItemClick(new ProductItemClickListener() {
                                        @Override
                                        public void onItemClick(int pos, ImageView imageView) {
                                            if (arrayListNewsFeedDatas.size() > pos) {
                                                itemPosition = pos;
                                                Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                                                intent.putExtra("productName", arrayListNewsFeedDatas.get(pos).getProductName());
                                                if (arrayListNewsFeedDatas.get(pos).getCategoryData()!=null)
                                                    intent.putExtra("category", arrayListNewsFeedDatas.get(pos).getCategoryData().get(0).getCategory());
                                                intent.putExtra("likes", arrayListNewsFeedDatas.get(pos).getLikes());
                                                intent.putExtra("likeStatus", arrayListNewsFeedDatas.get(pos).getLikeStatus());
                                                intent.putExtra("currency", arrayListNewsFeedDatas.get(pos).getCurrency());
                                                intent.putExtra("price", arrayListNewsFeedDatas.get(pos).getPrice());
                                                intent.putExtra("postedOn", arrayListNewsFeedDatas.get(pos).getPostedOn());
                                                intent.putExtra("image", arrayListNewsFeedDatas.get(pos).getMainUrl());
                                                intent.putExtra("thumbnailImageUrl", arrayListNewsFeedDatas.get(pos).getThumbnailImageUrl());
                                                intent.putExtra("likedByUsersArr", arrayListNewsFeedDatas.get(pos).getLikedByUsers());
                                                //intent.putExtra("description", arrayListNewsFeedDatas.get(pos).getDescription());
                                                intent.putExtra("condition", arrayListNewsFeedDatas.get(pos).getCondition());
                                                intent.putExtra("place", arrayListNewsFeedDatas.get(pos).getPlace());
                                                intent.putExtra("latitude", arrayListNewsFeedDatas.get(pos).getLatitude());
                                                intent.putExtra("longitude", arrayListNewsFeedDatas.get(pos).getLongitude());
                                                intent.putExtra("postedByUserName", arrayListNewsFeedDatas.get(pos).getMembername());
                                                intent.putExtra("postId", arrayListNewsFeedDatas.get(pos).getPostId());
                                                intent.putExtra("postsType", arrayListNewsFeedDatas.get(pos).getPostsType());
                                                intent.putExtra("clickCount", arrayListNewsFeedDatas.get(pos).getClickCount());
                                                intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));
                                                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
                                                SocialFrag.this.startActivityForResult(intent, VariableConstants.PRODUCT_DETAILS_REQ_CODE, options.toBundle());
                                            }
                                        }
                                    });
                                }
                                break;

                            // User and his followers have not posted anything
                            case "204" :
                                mSwipeRefreshLayout.setRefreshing(false);
                                if (arrayListNewsFeedDatas.size()==0)
                                    linear_no_friends.setVisibility(View.VISIBLE);
                                break;

                            // auth token expired
                            case "401" :
                                mSwipeRefreshLayout.setRefreshing(false);
                                CommonClass.sessionExpired(mActivity);
                                break;

                            // error
                            default:
                                mSwipeRefreshLayout.setRefreshing(false);
                                CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,socialMainPojo.getMessage());
                                break;
                        }
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mProgress_bar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);
                    CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,error);
                }
            });
        }
        else CommonClass.showSnackbarMessage(((HomePageActivity)mActivity).rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Subscribe
    public void getMessage(SocialDatas socialDatas)
    {
        if (socialDatas!=null && socialDatas.getPostId()!=null)
        {
            if (socialDatas.isToAddSocialData())
            {
                if (!isContainsId(socialDatas.getPostId()))
                {
                    arrayListNewsFeedDatas.add(0,socialDatas);
                    newsFeedRvAdapter.notifyDataSetChanged();
                }
            }
            else
            {
                for (int socialCount=0; socialCount<arrayListNewsFeedDatas.size();socialCount++)
                {
                    if (arrayListNewsFeedDatas.get(socialCount).getPostId().equals(socialDatas.getPostId()))
                    {
                        arrayListNewsFeedDatas.remove(socialCount);
                        newsFeedRvAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }
        }

        if (arrayListNewsFeedDatas.size()>0)
            linear_no_friends.setVisibility(View.GONE);
        else linear_no_friends.setVisibility(View.VISIBLE);

        setProfileFollingCount();
    }

    /**
     * <h>setProfileFollingCount</h>
     * <p>
     *     In this method we used to find out the following count from the list.
     * </p>
     */
    private void setProfileFollingCount()
    {
        //int count = 0;
        if (arrayListNewsFeedDatas.size()>0) {
            HashSet<String> uniqueSet = new HashSet<>();
            for (SocialDatas outerSocialData : arrayListNewsFeedDatas)
            {
                String membername = outerSocialData.getMembername();
                System.out.println(TAG+" "+"membername="+membername+" "+"logged in member name="+sessionManager.getUserName());
                if (!membername.equals(sessionManager.getUserName()))
                {
                    uniqueSet.add(membername);
                }
            }
            System.out.println(TAG + " " + "unique member count=" + uniqueSet.size());

            ProfileFollowingCount follingCountObj = new ProfileFollowingCount();
            follingCountObj.setFollowingCount(uniqueSet.size());
            BusProvider.getInstance().post(follingCountObj);
        }
    }

    /**
     * <h>IsContainsId</h>
     * <p>
     *     In this method we used to check whether the given post id is
     *     present or not in the current list.
     * </p>
     * @param postId the given post id of product
     * @return the boolean value
     */
    public boolean isContainsId(String postId) {
        boolean flag = false;
        for (SocialDatas object : arrayListNewsFeedDatas) {
            System.out.println(TAG+" "+"given post id="+postId+" "+"current post id="+object.getPostId());
            if (postId.equals(object.getPostId())) {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_find_friends :
                startActivity(new Intent(mActivity, DiscoverPeopleActivity.class));
                break;
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isNewsFeedFragVisible=isVisibleToUser;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(TAG+" "+"requestCode="+requestCode+" "+"resultCode="+resultCode+" "+"data="+data);

        if (data!=null) {
            switch (requestCode) {
                case VariableConstants.PRODUCT_DETAILS_REQ_CODE:
                    if (arrayListNewsFeedDatas.size()>itemPosition) {
                        String likesCount = data.getStringExtra("likesCount");
                        String likeStatus = data.getStringExtra("likeStatus");
                        ArrayList<ExploreLikedByUsersDatas> aL_likedByUsers = (ArrayList<ExploreLikedByUsersDatas>) data.getSerializableExtra("aL_likedByUsers");
                        arrayListNewsFeedDatas.get(itemPosition).setLikes(likesCount);
                        arrayListNewsFeedDatas.get(itemPosition).setLikeStatus(likeStatus);
                        arrayListNewsFeedDatas.get(itemPosition).setLikedByUsers(aL_likedByUsers);
                        newsFeedRvAdapter.notifyItemChanged(itemPosition);
                    }
                    break;
            }
        }
    }

    @Override
    public void onDestroy()
    {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();
    }
}
