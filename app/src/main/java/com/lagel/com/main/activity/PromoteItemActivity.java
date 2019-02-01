package com.lagel.com.main.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.suresh.innapp_purches.InAppConstants;
import com.suresh.innapp_purches.Inn_App_billing.BillingProcessor;
import com.suresh.innapp_purches.Inn_App_billing.TransactionDetails;
import com.lagel.com.BusEventManager.FutureUpdated;
import com.lagel.com.R;
import com.lagel.com.adapter.PromoteItemRvAdapter;
import com.lagel.com.aleret.CircleProgressDialog;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.pojo_class.promote_item_pojo.PromoteItemData;
import com.lagel.com.pojo_class.promote_item_pojo.PromoteItemPojo;
import com.lagel.com.utility.ApiUrl;
import com.lagel.com.utility.CommonClass;
import com.lagel.com.utility.OkHttp3Connection;
import com.lagel.com.utility.SessionManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
/**
 * <h>PromoteItemActivity</h>
 * <p>
 *     In this class we used to show the list of promote item.
 * </p>
 * @since 30-Aug-17
 */
public class PromoteItemActivity extends AppCompatActivity implements View.OnClickListener,BillingProcessor.IBillingHandler
{
    private static final String TAG = PromoteItemActivity.class.getSimpleName();
    private Activity mActivity;
    private SessionManager mSessionManager;
    private RelativeLayout rL_rootElement;
    private ProgressBar mProgressBar;
    private RecyclerView rV_promotePlan;
    private static BillingProcessor bp;
    private ProgressDialog progressDialog;
    private InAppConstants.Purchase_item mpurchase_item;
    private ArrayList<PromoteItemData> arrayListPromoteItem;
    private Dialog circleDialog;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promote_item);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        initVariables();
    }

    private void initVariables()
    {
        circleDialog=CircleProgressDialog.getInstance().get_Circle_Progress_bar(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Wait..");
        // receiving datas from last class
        String price,productName,productImage;
        Intent intent = getIntent();
        price=intent.getStringExtra("price");
        productName=intent.getStringExtra("productName");
        productImage=intent.getStringExtra("productImage");
        postId =intent.getStringExtra("productId");
        mActivity = PromoteItemActivity.this;
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mSessionManager = new SessionManager(mActivity);
        rL_rootElement= (RelativeLayout) findViewById(R.id.rL_rootElement);
        rV_promotePlan= (RecyclerView) findViewById(R.id.rV_promotePlan);
        findViewById(R.id.rL_apply).setOnClickListener(this);
        bp=new BillingProcessor(this, InAppConstants.base64EncodedPublicKey,mSessionManager.getUserId(),this);

        // set product desc like image, name and price
        ImageView iV_productImage= (ImageView) findViewById(R.id.iV_productImage);
        if (productImage!=null && !productImage.isEmpty())
            Picasso.with(mActivity)
                    .load(productImage)
                    .resize(CommonClass.getDeviceWidth(mActivity)/3,CommonClass.getDeviceWidth(mActivity)/3)
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image)
                    .into(iV_productImage);

        // set price
        TextView tV_productprice= (TextView) findViewById(R.id.tV_productprice);
        if (price!=null && !price.isEmpty())
            tV_productprice.setText(price);

        // set name
        TextView tV_productName= (TextView) findViewById(R.id.tV_productName);
        if (productName!=null && !productName.isEmpty())
            tV_productName.setText(productName);

        // Back button
        RelativeLayout rL_back_btn= (RelativeLayout) findViewById(R.id.rL_back_btn);
        rL_back_btn.setOnClickListener(this);
        promotionPlansApi();
    }

    /**
     * <h>PromotionPlansApi</h>
     * <h>
     *     In this method we used to do api call to get all promote plans list.
     * </h>
     */
    private void promotionPlansApi()
    {
        if (CommonClass.isNetworkAvailable(mActivity)) {
            mProgressBar.setVisibility(View.VISIBLE);
            String promotionPlansUrl = ApiUrl.PROMOTE_PLANS + "?token=" + mSessionManager.getAuthToken();
            OkHttp3Connection.doOkHttp3Connection(TAG, promotionPlansUrl, OkHttp3Connection.Request_type.GET, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag) {
                    mProgressBar.setVisibility(View.GONE);
                    System.out.println(TAG+" "+"promote plan res="+result);

                    PromoteItemPojo promoteItemPojo;
                    Gson gson = new Gson();
                    promoteItemPojo = gson.fromJson(result,PromoteItemPojo.class);

                    switch (promoteItemPojo.getCode())
                    {
                        case "200" :
                            arrayListPromoteItem = promoteItemPojo.getData();
                            if (arrayListPromoteItem!=null && arrayListPromoteItem.size()>0)
                            {
                                PromoteItemRvAdapter promoteItemRvAdapter = new PromoteItemRvAdapter(mActivity,arrayListPromoteItem);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
                                rV_promotePlan.setLayoutManager(linearLayoutManager);
                                rV_promotePlan.setAdapter(promoteItemRvAdapter);
                            }
                            break;
                    }
                }

                @Override
                public void onError(String error, String user_tag) {

                }
            });
        }
        else CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.rL_back_btn :
                onBackPressed();
                break;
            case R.id.rL_apply:
                collectSeletedItem();
                break;
        }
    }

    /*
     *buying the data details. */
    private void buy_Item(InAppConstants.Purchase_item purchase_item)
    {
        this.mpurchase_item=purchase_item;
        progressDialog.show();
        bp=new BillingProcessor(this, InAppConstants.base64EncodedPublicKey,mSessionManager.getUserId(),this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(!(bp != null && bp.handleActivityResult(requestCode, resultCode, data)))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onProductPurchased(String productId, TransactionDetails details)
    {
        Log.d("Item_purchesed", "String postId" + productId + " " + details.toString());
        if(bp.consumePurchase(productId))
        {
            purchase_Item(productId);
            bp.release();
        }
    }

    @Override
    public void onPurchaseHistoryRestored()
    {
        bp.release();
    }

    @Override
    public void onBillingError(int errorCode, Throwable error)
    {
        Toast.makeText(this, R.string.purchaset_error,Toast.LENGTH_SHORT).show();
        /*
         * Releasing the service.*/
        bp.release();
    }
    @Override
    public void onBillingInitialized()
    {
        if(progressDialog!=null)
        {
            progressDialog.dismiss();
        }
        if(mpurchase_item!=null) {
            /*
            *For testing purpose only.*/
            bp.purchase(this, mpurchase_item.getKey());
        }
    }
    /*
     * current selected item*/
    private void collectSeletedItem()
    {
        String planId="";
        for(PromoteItemData data:arrayListPromoteItem)
        {
            if(data.isItemSelected())
            {
                planId=data.getPlanId();
                break;
            }
        }
        if(planId.isEmpty())
        {
            Toast.makeText(this, R.string.Error_on_plandetaisl,Toast.LENGTH_SHORT).show();
            return;
        }
        /*
         *buying the item. */
        buy_Item(InAppConstants.Purchase_item.getPurchaseItem(planId));
    }
    /*
     *Purchase item.*/
    private void purchase_Item(String playItemId)
    {
        InAppConstants.Purchase_item item=InAppConstants.Purchase_item.getPlanId(playItemId);
        if(item==null)
        {
            Toast.makeText(this, R.string.Stored_item_error,Toast.LENGTH_SHORT).show();
            return;
        }
        if (CommonClass.isNetworkAvailable(mActivity)) {
            circleDialog.show();
            String promotionPlansUrl = ApiUrl.PURCHASED_PLAN+"/"+item.getId()+"/"+ postId;
            OkHttp3Connection.doOkHttp3Connection("", promotionPlansUrl, OkHttp3Connection.Request_type.POST, new JSONObject(), new OkHttp3Connection.OkHttp3RequestCallback() {
                @Override
                public void onSuccess(String result, String user_tag)
                {
                    if(circleDialog!=null)
                    {
                        circleDialog.dismiss();
                    }
                    try
                    {
                        parseResult(result);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                        CommonClass.showSnackbarMessage(rL_rootElement,getString(R.string.parse_exception_text));
                    }
                }
                @Override
                public void onError(String error, String user_tag)
                {
                    if(circleDialog!=null)
                    {
                        circleDialog.dismiss();
                    }
                    CommonClass.showSnackbarMessage(rL_rootElement,error);
                }
            });
        } else
        {
            CommonClass.showSnackbarMessage(rL_rootElement,getResources().getString(R.string.NoInternetAccess));
        }
    }

    /*
     *Parsing the result data.*/
    public void parseResult(String response) throws JSONException
    {
        JSONObject jsonObject=new JSONObject(response);
        String code=jsonObject.getString("code");
        if(code.equals("200"))
        {
            AppController.getBus().post(new FutureUpdated(postId,true));
            PromoteItemActivity.this.finish();
        }else
        {
            String message=jsonObject.getString("message");
            CommonClass.showSnackbarMessage(rL_rootElement,message);
        }
    }

}
