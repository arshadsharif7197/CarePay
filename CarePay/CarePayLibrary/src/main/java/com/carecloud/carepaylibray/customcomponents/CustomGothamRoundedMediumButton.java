package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by arpit_jain1 on 10/13/2016.
 */
public class CustomGothamRoundedMediumButton extends Button {

    Context context;
    public CustomGothamRoundedMediumButton(Context context) {
        super(context);
        this.context=context;
    }

    public CustomGothamRoundedMediumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomGothamRoundedMediumButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    /**
     * Method to set typeface for button.
     */
    private void init() {
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/gotham_rounded_medium.otf");
        this.setTypeface(tf);
    }
}