package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;

import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by harshal_patil on 9/22/2016.
 */
public class CustomGothamRoundedBookLabel extends TextView {

    Context context;
    public CustomGothamRoundedBookLabel(Context context) {
        super(context);
        this.context=context;
    }

    public CustomGothamRoundedBookLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomGothamRoundedBookLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/gotham_rounded_book.otf");
        setTypeface(tf);
    }
}