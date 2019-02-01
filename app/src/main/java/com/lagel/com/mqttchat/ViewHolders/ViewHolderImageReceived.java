package com.lagel.com.mqttchat.ViewHolders;


import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lagel.com.R;
import com.lagel.com.mqttchat.AppController;
import com.lagel.com.mqttchat.Utilities.AdjustableImageView;
import com.lagel.com.mqttchat.Utilities.RingProgressBar;


/**
 * View holder for image received recycler view item
 */
public class ViewHolderImageReceived extends RecyclerView.ViewHolder {


//    public TextView senderName;


    public TextView time, date, fnf;


    public AdjustableImageView imageView;

    public ImageView download;

    public RingProgressBar progressBar;

    public ProgressBar progressBar2;

    public ImageView cancel;

    public ViewHolderImageReceived(View view) {
        super(view);

        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        date = (TextView) view.findViewById(R.id.date);
        imageView = (AdjustableImageView) view.findViewById(R.id.imgshow);

        time = (TextView) view.findViewById(R.id.ts);

        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        cancel = (ImageView) view.findViewById(R.id.cancel);
        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);


        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }
}
