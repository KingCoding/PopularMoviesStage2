
package com.example.lan.samuel_dsldevice.popularmoviesstage1;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by owner on 03/11/2015.
 */
public class CustomedScrollView extends ScrollView {

    public CustomedScrollView(Context context) {
        super(context);
    }

    public CustomedScrollView(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public CustomedScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        //scrollView.scrollTo(0, scrollView.getBottom()/2);
        if (!(t == getBottom()/2 || t == getBottom()/2-1 ||t == getBottom()/2+1)) {
            scrollTo(0, getBottom()/2);
        }
    }
}
