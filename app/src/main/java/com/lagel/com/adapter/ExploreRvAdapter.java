package com.lagel.com.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.lagel.com.R;
import com.lagel.com.main.activity.LandingActivity;
import com.lagel.com.mqttchat.Activities.ChatMessageScreen;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.pojo_class.home_explore_pojo.ExploreResponseDatas;
import com.lagel.com.pojo_class.product_details_pojo.ProductDetailsMain;
import com.lagel.com.pojo_class.product_details_pojo.ProductResponseDatas;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.ProductItemClickListener;
import com.lagel.com.utility.SessionManager;
import com.lagel.com.utility.VariableConstants;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import static com.lagel.com.utility.ApiUrl.GET_POST_BY_ID_USER;

/**
 * <h>ExploreRvAdapter</h>
 * <p>
 * In class is called from ExploreFrag. In this recyclerview adapter class we used to inflate
 * single_row_images layout and shows the all post posted by logged-in user.
 * </p>
 *
 * @since 4/6/2017
 */
public class ExploreRvAdapter extends RecyclerView.Adapter<ExploreRvAdapter.MyViewHolder> {
    private static final String TAG = ExploreRvAdapter.class.getSimpleName();
    private Activity mActivity;
    private ArrayList<ExploreResponseDatas> arrayListExploreDatas;
    private final ProductItemClickListener animalItemClickListener;
    private SessionManager mSessionManager;
    private boolean clickButtonBusy = false;
    private Random mRandom = new Random();

    public ExploreRvAdapter(Activity mActivity, ArrayList<ExploreResponseDatas> arrayListExploreDatas, ProductItemClickListener animalItemClickListener) {
        this.mActivity = mActivity;
        this.arrayListExploreDatas = arrayListExploreDatas;
        this.animalItemClickListener = animalItemClickListener;
        mSessionManager = new SessionManager(mActivity);
       // System.out.println(TAG + " " + "al size in adap=" + arrayListExploreDatas);
        WindowManager wm = (WindowManager) mActivity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.single_row_explore, parent, false);
        int height = parent.getMeasuredHeight() / 3;
        //view.setMinimumHeight(height);
        return new MyViewHolder(view);
    }

    // Custom method to get a random number between a range
    protected int getRandomIntInRange(int max, int min) {
        return mRandom.nextInt((max - min) + min) + min;
    }


    @Override
    public long getItemId(int position) {
        ExploreResponseDatas e = arrayListExploreDatas.get(position);
        return e.getPostId().hashCode();
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ExploreResponseDatas eRDates = arrayListExploreDatas.get(position);

        String price = eRDates.getCurrency() + " " + eRDates.getPrice();
        holder.currencyTV.setText(price);
        holder.categoryTV.setText(eRDates.getProductName());

        //String postedImageUrl = exploreResponseDatas.getMainUrl();

        //String postedImageUrl = eRDates.getThumbnailImageUrl();
        String postedImageUrl = eRDates.getBetterQualityThumbnailImageUrl();

        String containerHeight = eRDates.getContainerHeight();
        String containerWidth = eRDates.getContainerWidth();
        String isPromoted = eRDates.getIsPromoted();

        int deviceHalfWidth = CommonClass.getDeviceWidth(mActivity) / 2;
        int setHeight = 0;

        if (containerWidth != null && !containerWidth.isEmpty())
            setHeight = (Integer.parseInt(containerHeight) * deviceHalfWidth) / (Integer.parseInt(containerWidth));

        //System.out.println (TAG + " set height=" + setHeight);
        //..for reduce the big image hieght..//
        if (setHeight > 720)
            setHeight = 720;

        // holder.iV_explore_img.getLayoutParams().height = getRandomIntInRange(350, 150);

        // holder.iV_explore_img.getLayoutParams().height = setHeight;

        //System.out.println(TAG + " " + "containerHeight=" + containerHeight + " " + "set height=" + setHeight + " " + "device half height=" + " " + CommonClass.getDeviceWidth(mActivity) + " " + CommonClass.getDeviceWidth(mActivity) / 2);

        // product image
        try {
            Glide.with(mActivity)
                    .load(postedImageUrl)

                    .override(300, 300)
                   // .override(120, 150)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.color.image_bg_color)
                    .error(R.color.image_bg_color)
                    .into(holder.iV_explore_img);
        } catch (OutOfMemoryError | IllegalArgumentException | NullPointerException e) {
            e.printStackTrace();
        }


        ViewCompat.setTransitionName(holder.iV_explore_img, eRDates.getProductName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animalItemClickListener.onItemClick(holder.getAdapterPosition(), holder.iV_explore_img);
            }
        });

        holder.chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clickButtonBusy) return;
                clickButtonBusy = true;
                if (mSessionManager.getIsUserLoggedIn()) {
                    // Check if we have member mqtt, if not request it.
                    if (eRDates.getMemberMqttId() == null || eRDates.getMemberMqttId().isEmpty()){
                        requestDataAndInitiateChat(eRDates.getPostId());
                    }else{
                        initiateChat(eRDates.getMemberMqttId(), eRDates.getPostedByUserName(), eRDates.getMemberProfilePicUrl(), eRDates.getPostId());
                    }

                    clickButtonBusy = false;
                } else{
                    mActivity.startActivity(new Intent(mActivity, LandingActivity.class));
                    clickButtonBusy = false;
                }

            }
        });

        if (eRDates.getPostedByUserName() != null && eRDates.getPostedByUserName().equals(mSessionManager.getUserName())){
            holder.chatBtn.setVisibility(View.INVISIBLE);
        }else{
            holder.chatBtn.setVisibility(View.VISIBLE);
        }

        // show featured tag with product
        if (isPromoted != null && !isPromoted.isEmpty()) {
            if (!isPromoted.equals("0")) {
                holder.rL_featured.setVisibility(View.VISIBLE);
            } else holder.rL_featured.setVisibility(View.GONE);
        } else holder.rL_featured.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return arrayListExploreDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView iV_explore_img;
        private RelativeLayout rL_featured;
        TextView currencyTV, categoryTV;
        TextView chatBtn;


        MyViewHolder(View itemView) {
            super(itemView);
            currencyTV = (TextView) itemView.findViewById(R.id.currencyTV);
            categoryTV = (TextView) itemView.findViewById(R.id.categoryTV);
            chatBtn = (TextView) itemView.findViewById(R.id.chatBtn);
            iV_explore_img = (ImageView) itemView.findViewById(R.id.iV_image);
            rL_featured = (RelativeLayout) itemView.findViewById(R.id.rL_featured);
            rL_featured.setVisibility(View.GONE);
        }
    }


    // Sends request to api to get missing data to initiate chat.
    private void requestDataAndInitiateChat (final String postId){
        if (CommonClass.isNetworkAvailable(mActivity)) {
            JSONObject requestDats = new JSONObject();
            try {
                requestDats.put("token", mSessionManager.getAuthToken());
                requestDats.put("postId", postId);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            OkHttp3Connection.doOkHttp3Connection(TAG, ApiUrl.GET_POST_BY_ID_USER, OkHttp3Connection.Request_type.POST, requestDats, new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {

                    ProductDetailsMain productDetailsMain;
                    Gson gson = new Gson();
                    productDetailsMain = gson.fromJson(result, ProductDetailsMain.class);

                    switch (productDetailsMain.getCode()) {
                        // success
                        case "200":
                            ProductResponseDatas productResponse = productDetailsMain.getData().get(0);
                            String receiverMqttId = productResponse.getMemberMqttId();
                            String membername = productResponse.getMembername();
                            String memberProfilePicUrl = productResponse.getMemberProfilePicUrl();
                            initiateChat(receiverMqttId, membername, memberProfilePicUrl, postId);
                            break;

                        // auth token expired
                        case "401":
                            CommonClass.sessionExpired(mActivity);
                            break;

                        // error
                        default:
                            break;
                    }

                    clickButtonBusy = false;
                }

                @Override
                public void onError(String error, String user_tag) {
                    clickButtonBusy = false;
                    Toast.makeText(mActivity, error, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(mActivity, mActivity.getResources().getString(R.string.NoInternetAccess), Toast.LENGTH_SHORT).show();
            clickButtonBusy = false;
        }

    }
    // Starts a chat session.
    private void initiateChat(String receiverMqttId, String membername, String memberProfilePicUrl, String postId) {
        String doucumentId = AppController.getInstance().findDocumentIdOfReceiver(receiverMqttId, postId);
        boolean isChatNotExist;
        if (doucumentId.isEmpty()) {
            doucumentId = null;
            isChatNotExist = true;
        } else {
            isChatNotExist = false;
            AppController.getInstance().getDbController().updateChatDetails(doucumentId, membername, memberProfilePicUrl);
        }
        Intent intent;
        intent = new Intent(mActivity, ChatMessageScreen.class);
        intent.putExtra("isChatNotExist", isChatNotExist);
        intent.putExtra("productId", postId);
        intent.putExtra("receiverUid", receiverMqttId);
        intent.putExtra("receiverName", membername);
        intent.putExtra("documentId", doucumentId);
        intent.putExtra("receiverIdentifier", AppController.getInstance().getUserIdentifier());
        intent.putExtra("receiverImage", memberProfilePicUrl);
        intent.putExtra("colorCode", AppController.getInstance().getColorCode(1 % 19));
        intent.putExtra("isFromOfferPage", false);
        mActivity.startActivity(intent);
    }
}
