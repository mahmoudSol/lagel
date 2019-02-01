package com.lagel.com.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.mqttchat.AppController;

/**
 * @since 9/20/2017.
 */

public class PaypalLinkShared extends RecyclerView.ViewHolder
{
    public PaypalLinkShared(View itemView)
    {
        super(itemView);
        TextView sent_offer_view=(TextView)itemView.findViewById(R.id.sent_offer_view);
        sent_offer_view.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
