package com.lagel.com.main.view_pager.search_product;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.adapter.SearchPostsRvAdap;
import com.lagel.com.get_current_location.FusedLocationReceiver;
import com.lagel.com.get_current_location.FusedLocationService;
import com.lagel.com.main.activity.SearchProductActivity;
import com.lagel.com.main.activity.products.ProductDetailsActivity;
import com.lagel.com.pojo_class.PostProductDatas;
import com.lagel.com.pojo_class.search_post_pojo.SearchPostDatas;
import com.lagel.com.pojo_class.search_post_pojo.SearchPostMainPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.DialogBox;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.ProductItemClickListener;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.SpacesItemDecoration;
import com.lagel.com.utility.VariableConstants;
import org.json.JSONObject;
import java.util.ArrayList;

/**
 * <h>PostsFrag</h>
 * <p>
 *     In this class we used to show the list of searched products.
 * </p>
 * @since 18-May-17
 */
public class PostsFrag extends Fragment
{
    private Activity mActivity;
    private SessionManager mSessionManager;
    private static final String TAG=PostsFrag.class.getSimpleName();
    private RelativeLayout rL_rootElement;
    private RecyclerView rV_search_post;
    private boolean isPostFrag;
    private int page_index=0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DialogBox mDialogBox;
    private FusedLocationService locationService;
    private String latitude="",longitude="";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mSessionManager=new SessionManager(mActivity);
        latitude=((SearchProductActivity)mActivity).latitude;
        longitude=((SearchProductActivity)mActivity).longitude;
        if(!isLocationFound(latitude,longitude)){
            getCurrentLocation();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.frag_people,container,false);
        rL_rootElement= (RelativeLayout) view.findViewById(R.id.rL_rootElement);
        rV_search_post= (RecyclerView) view.findViewById(R.id.rV_search_people);
        mSwipeRefreshLayout= (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.pink_color);
        mDialogBox=new DialogBox(mActivity);


        // pull to refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                page_index=0;
                mSwipeRefreshLayout.setRefreshing(false);
                searchProductApi(((SearchProductActivity) mActivity).postText,page_index);
            }
        });


        searchPost();
        return view;
    }

    /**
     * <h>SearchPost</h>
     * <p>
     *     In this method we used to do api call for each text changes.
     * </p>
     */
    private void searchPost()
    {

        ((SearchProductActivity)mActivity).act_search_posts.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    if (isPostFrag) {
                        ((SearchProductActivity) mActivity).postText = ((SearchProductActivity) mActivity).act_search_posts.getText().toString();
                        page_index=0;
                        searchProductApi(((SearchProductActivity) mActivity).act_search_posts.getText().toString(),page_index);
                        ((SearchProductActivity)mActivity).act_search_posts.clearFocus();
                        InputMethodManager in = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        if(((SearchProductActivity)mActivity).act_search_posts.isFocused()) {
                            in.hideSoftInputFromWindow(((SearchProductActivity) mActivity).act_search_posts.getWindowToken(), 0);
                        }
                    }
                    return true;
                }
                return false;
            }
        });


        /*((SearchProductActivity)mActivity).eT_search_users.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (isPostFrag) {
                    System.out.println(TAG + " " + "text entered=" + ((SearchProductActivity) mActivity).eT_search_users.getText().toString()+" "+"s="+s);
                    ((SearchProductActivity) mActivity).postText = ((SearchProductActivity) mActivity).eT_search_users.getText().toString();
                    page_index=0;
                    searchProductApi(((SearchProductActivity) mActivity).eT_search_users.getText().toString(),page_index);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/

        ((SearchProductActivity)mActivity).act_search_posts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (isPostFrag) {
                    System.out.println(TAG + " " + "text searched=" + ((SearchProductActivity) mActivity).searchList.get(i));
                    ((SearchProductActivity) mActivity).postText = ((SearchProductActivity) mActivity).searchList.get(i);
                    page_index=0;
                    searchProductApi(((SearchProductActivity) mActivity).searchList.get(i),page_index);
                }
                InputMethodManager in = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(((SearchProductActivity)mActivity).act_search_posts.isFocused()) {
                    in.hideSoftInputFromWindow(((SearchProductActivity) mActivity).act_search_posts.getWindowToken(), 0);
                }
            }
        });

    }

    /**
     * <h>SearchProductApi</h>
     * <p>
     *     In this method we used to do api call(using method Http get method) and get all post.
     * </p>
     * @param searchText The character
     * @param offset The page index
     */
    private void searchProductApi(final String searchText, int offset)
    {
        if (CommonClass.isNetworkAvailable(mActivity))
        {
            int limit=20;
            offset=limit*offset;
            System.out.println(TAG+" "+"offset="+offset+" "+"searched text="+searchText);
            mDialogBox.showProgressDialog(getString(R.string.searching));

            String URL = ApiUrl.SEARCH_POST + searchText+"?offset="+offset+"&limit="+limit+"&token="+mSessionManager.getAuthToken()+"&latitude="+latitude+"&longitude="+longitude;
            //System.out.println(TAG+" "+"url="+URL);

            OkHttp3Connection.doOkHttp3Connection(TAG, URL, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    System.out.println(TAG+" "+"search product api res="+result);
                    SearchPostMainPojo searchPostMainPojo;
                    Gson gson=new Gson();
                    searchPostMainPojo=gson.fromJson(result,SearchPostMainPojo.class);

                    switch (searchPostMainPojo.getCode())
                    {
                        // success
                        case "200" :
                            if (searchPostMainPojo.getData()!=null && !searchPostMainPojo.getData().isEmpty())
                            {
                                /* staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
                                staggeredGridLayoutManager.setGapStrategy(0);
                                rV_search_post.setLayoutManager(staggeredGridLayoutManager);

                                // Set spacing the recycler view items
                                int spanCount = 2; // 2 columns
                                int spacing = 10; // 50px
                                rV_search_post.addItemDecoration(new SpacesItemDecoration(spanCount, spacing));

                                */

                                final ArrayList<SearchPostDatas> aL_searchedPosts=searchPostMainPojo.getData();

                                GridLayoutManager mLinearLayoutManager=new GridLayoutManager(mActivity,2);
                                rV_search_post.setLayoutManager(mLinearLayoutManager);
                                // set post adapter
                                SearchPostsRvAdap searchPostsRvAdap=new SearchPostsRvAdap(mActivity,aL_searchedPosts);
                                rV_search_post.setAdapter(searchPostsRvAdap);
                                searchPostsRvAdap.notifyDataSetChanged();



                                // listen item click
                                searchPostsRvAdap.setOnItemClick(new ProductItemClickListener() {
                                    @Override
                                    public void onItemClick(int pos, ImageView imageView) {
                                        Intent intent = new Intent(mActivity, ProductDetailsActivity.class);
                                        intent.putExtra("productName", aL_searchedPosts.get(pos).getProductName());
                                        intent.putExtra("category", aL_searchedPosts.get(pos).getCategory());
                                        intent.putExtra("likes", aL_searchedPosts.get(pos).getLikes());
                                        intent.putExtra("likeStatus", "");
                                        intent.putExtra("currency", aL_searchedPosts.get(pos).getCurrency());
                                        intent.putExtra("price", aL_searchedPosts.get(pos).getPrice());
                                        intent.putExtra("postedOn", aL_searchedPosts.get(pos).getPostedOn());
                                        intent.putExtra("image",aL_searchedPosts.get(pos).getMainUrl());
                                        intent.putExtra("thumbnailImageUrl",aL_searchedPosts.get(pos).getThumbnailImageUrl());
                                        intent.putExtra("likedByUsersArr",aL_searchedPosts.get(pos).getLikedByUsers());
                                        intent.putExtra("description","");
                                        intent.putExtra("condition","");
                                        intent.putExtra("place",aL_searchedPosts.get(pos).getPlace());
                                        intent.putExtra("latitude",aL_searchedPosts.get(pos).getLatitude());
                                        intent.putExtra("longitude",aL_searchedPosts.get(pos).getLongitude());
                                        intent.putExtra("postedByUserName",aL_searchedPosts.get(pos).getUsername());
                                        intent.putExtra("postId",aL_searchedPosts.get(pos).getPostId());
                                        intent.putExtra("postsType",aL_searchedPosts.get(pos).getPostsType());
                                        intent.putExtra("followRequestStatus","");
                                        intent.putExtra("clickCount","");
                                        intent.putExtra("memberProfilePicUrl","");
                                        intent.putExtra(VariableConstants.EXTRA_ANIMAL_IMAGE_TRANSITION_NAME, ViewCompat.getTransitionName(imageView));

                                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, imageView, ViewCompat.getTransitionName(imageView));
                                        startActivity(intent, options.toBundle());
                                    }
                                });
                            }
                            mDialogBox.progressBarDialog.dismiss();
                            break;

                        // auth token expired
                        case "401" :
                            CommonClass.sessionExpired(mActivity);
                            mDialogBox.progressBarDialog.dismiss();
                            break;

                        // no data
                        case "204" :
                            SearchPostsRvAdap searchPostsRvAdap=new SearchPostsRvAdap(mActivity,new ArrayList<SearchPostDatas>());
                            GridLayoutManager mLinearLayoutManager=new GridLayoutManager(mActivity,2);
                            rV_search_post.setLayoutManager(mLinearLayoutManager);
                            rV_search_post.setAdapter(searchPostsRvAdap);
                            searchPostsRvAdap.notifyDataSetChanged();
                            mDialogBox.progressBarDialog.dismiss();
                            break;

                        // auth token expired
                        case "422" :
                            mDialogBox.progressBarDialog.dismiss();
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    mDialogBox.progressBarDialog.dismiss();
                    CommonClass.showTopSnackBar(rL_rootElement,error);
                }
            });
        }
        else CommonClass.showTopSnackBar(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isPostFrag=isVisibleToUser;
    }

    private void getCurrentLocation()
    {
        locationService=new FusedLocationService(mActivity, new FusedLocationReceiver() {
            @Override
            public void onUpdateLocation() {
                Location currentLocation=locationService.receiveLocation();
                if (currentLocation!=null)
                {
                    latitude=String.valueOf(currentLocation.getLatitude());
                    longitude=String.valueOf(currentLocation.getLongitude());

                    System.out.println(TAG+" "+"lat="+latitude+" "+"lng="+longitude);

                    if (isLocationFound(latitude,longitude))
                    {
                        mSessionManager.setCurrentLat(latitude);
                        mSessionManager.setCurrentLng(longitude);
                    }
                }
            }
        }
        );
    }

    private boolean isLocationFound(String lat,String lng) {
        return !(lat == null || lat.isEmpty()) && !(lng == null || lng.isEmpty());
    }
}
