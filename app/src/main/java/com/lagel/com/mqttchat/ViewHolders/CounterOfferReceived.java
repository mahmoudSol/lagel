package com.lagel.com.mqttchat.ViewHolders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.lagel.com.R;
import com.lagel.com.mqttchat.AppController;

/**
 * @since  9/11/2017.
 */
public class CounterOfferReceived extends RecyclerView.ViewHolder
{
    public TextView offerPrice,acceptOffer,counterOffer;
    public CounterOfferReceived(View itemView)
    {
        super(itemView);
        offerPrice=(TextView)itemView.findViewById(R.id.offerPrice);
        offerPrice.setTypeface(AppController.getInstance().getRobotoMediumFont());
        acceptOffer=(TextView)itemView.findViewById(R.id.acceptOffer);
        acceptOffer.setTypeface(AppController.getInstance().getRobotoMediumFont());
        counterOffer=(TextView)itemView.findViewById(R.id.counterOffer);
        counterOffer.setTypeface(AppController.getInstance().getRobotoMediumFont());
        TextView sent_offer_view=(TextView)itemView.findViewById(R.id.sent_offer_view);
        sent_offer_view.setTypeface(AppController.getInstance().getRobotoMediumFont());
    }
}
