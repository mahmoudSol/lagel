package com.lagel.com.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.lagel.com.R;
import com.lagel.com.pojo_class.report_product_pojo.ReportProductDatas;
import com.lagel.com.utility.SessionManager;

import java.util.ArrayList;

/**
 * <h>ReportItemRvAdapter</h>
 * <p>
 * This class is called from ReportProductActivity class. In this recyclerview adapter class we used to inflate
 * single_row_report_product layout and shows the report reason of the product.
 * </p>
 *
 * @since 13-Jun-17
 */
public class ReportItemRvAdapter extends RecyclerView.Adapter<ReportItemRvAdapter.MyViewHolder> {
    private Activity mActivity;
    private SessionManager mSessionManager;
    private ArrayList<ReportProductDatas> arrayListReportItem;
    private int mSelectedItem = -1;
    private boolean isFromUser = false;

    /**
     * <p>
     * This is simple constructor to initailize list datas and context.
     * </p>
     *
     * @param mActivity           The current context
     * @param arrayListReportItem The list datas
     */
    public ReportItemRvAdapter(Activity mActivity, ArrayList<ReportProductDatas> arrayListReportItem, boolean isFromUser) {
        this.isFromUser = isFromUser;
        this.mActivity = mActivity;
        this.arrayListReportItem = arrayListReportItem;
        mSessionManager = new SessionManager(mActivity);
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
        View view = LayoutInflater.from(mActivity).inflate(R.layout.single_row_report_product, parent, false);
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
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (position == mSelectedItem) {
            arrayListReportItem.get(position).setItemSelected(true);
            holder.radio_reportReason.setChecked(true);
            holder.rL_reasonDesc.setVisibility(View.VISIBLE);
        } else {
            arrayListReportItem.get(position).setItemSelected(false);
            holder.radio_reportReason.setChecked(false);
            holder.rL_reasonDesc.setVisibility(View.GONE);
        }

        // show user report reason given by user
        holder.eT_description.setText(arrayListReportItem.get(position).getReportReasonByUser());

        // save the report reason into array
        holder.eT_description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                arrayListReportItem.get(holder.getAdapterPosition()).setReportReasonByUser(holder.eT_description.getText().toString());
            }
        });
        Log.e("Report", "" + arrayListReportItem.get(position).getReportReason());

        if (!isFromUser) {
            if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It's prohibited on lagel")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_one));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It's offensive to me")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_two));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It's not a real post")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_three));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It's a duplicate post")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_four));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It's in the wrong category")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_five));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It may be wrong")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_six));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("It may be stolen")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_seven));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Other")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_eight));
            } else {
                holder.radio_reportReason.setText(arrayListReportItem.get(position).getReportReason());
            }

        } else {

            if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Missed our meeting")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_one));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Not respond to messages")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_two));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Sold me something broken")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_three));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Inappropriate/Inapproprié")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_four));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Possible Scammer")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_five));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Their items may be stolen")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_six));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Incident at MeetUp")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_user_item_seven));
            } else if (arrayListReportItem.get(position).getReportReason().trim().equalsIgnoreCase("Other")) {
                holder.radio_reportReason.setText(mActivity.getResources().getString(R.string.report_item_eight));
            } else {
                holder.radio_reportReason.setText(arrayListReportItem.get(position).getReportReason());
            }

        }
    }

    /**
     * Return the size of your dataset
     *
     * @return the total number of rows
     */
    @Override
    public int getItemCount() {
        return arrayListReportItem.size();
    }

    /**
     * Recycler View item variables
     */
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        RadioButton radio_reportReason;
        RelativeLayout rL_reasonDesc;
        EditText eT_description;

        public MyViewHolder(View itemView) {
            super(itemView);
            radio_reportReason = (RadioButton) itemView.findViewById(R.id.radio_reportReason);
            radio_reportReason.setOnClickListener(this);
            rL_reasonDesc = (RelativeLayout) itemView.findViewById(R.id.rL_reasonDesc);
            eT_description = (EditText) itemView.findViewById(R.id.eT_description);
        }

        @Override
        public void onClick(View v) {
            mSelectedItem = getAdapterPosition();
            notifyItemRangeChanged(0, arrayListReportItem.size());
        }
    }
}
