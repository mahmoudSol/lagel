package com.lagel.com.lalita.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Lalita Gill on 11/03/18.
 */

public class SqareIamgeVIew extends ImageView {

    public SqareIamgeVIew(Context context) {
        super(context);
    }

    public SqareIamgeVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SqareIamgeVIew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);
    }

}

