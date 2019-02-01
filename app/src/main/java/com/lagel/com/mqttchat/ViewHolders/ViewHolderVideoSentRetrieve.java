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


public class ViewHolderVideoSentRetrieve extends RecyclerView.ViewHolder
{
    public TextView senderName, time, date, fnf;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;//, blocked;


    public AdjustableImageView thumbnail;
    public ImageView download, cancel;


    public ProgressBar progressBar2;

    public RingProgressBar progressBar;

    public ViewHolderVideoSentRetrieve(View view) {
        super(view);


        // senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

//        blocked = (ImageView) view.findViewById(R.id.blocked);
        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        date = (TextView) view.findViewById(R.id.date);
        clock = (ImageView) view.findViewById(R.id.clock);
        thumbnail = (AdjustableImageView) view.findViewById(R.id.vidshow);


        cancel = (ImageView) view.findViewById(R.id.cancel);

        progressBar2 = (ProgressBar) view.findViewById(R.id.progress2);
        progressBar = (RingProgressBar) view.findViewById(R.id.progress);


        download = (ImageView) view.findViewById(R.id.download);
        fnf = (TextView) view.findViewById(R.id.fnf);

        Typeface tf = AppController.getInstance().getRobotoCondensedFont();
        time.setTypeface(tf, Typeface.ITALIC);

        date.setTypeface(tf, Typeface.ITALIC);
        fnf.setTypeface(tf, Typeface.NORMAL);
    }
}
