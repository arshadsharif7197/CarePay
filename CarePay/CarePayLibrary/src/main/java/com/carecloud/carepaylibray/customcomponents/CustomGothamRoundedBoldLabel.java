package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by harshal_patil on 9/22/2016.
 */
public class CustomGothamRoundedBoldLabel extends TextView {

    Context context;
    public CustomGothamRoundedBoldLabel(Context context) {
        super(context);
        this.context=context;
    }

    public CustomGothamRoundedBoldLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomGothamRoundedBoldLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/gotham_rounded_bold.otf");
        setTypeface(tf);
    }
}