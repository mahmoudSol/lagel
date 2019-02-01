package com.lagel.com.mqttchat.ViewHolders;

/**
 * Created by moda on 08/08/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.lagel.com.R;
import com.lagel.com.mqttchat.Utilities.SlackLoadingView;


/**
 * View holder for the loading more results item in recycler view
 */

public class ViewHolderLoading extends RecyclerView.ViewHolder {


    public SlackLoadingView slack;


    public ViewHolderLoading(View view) {
        super(view);


        slack = (SlackLoadingView) view.findViewById(R.id.slack);


    }
}
