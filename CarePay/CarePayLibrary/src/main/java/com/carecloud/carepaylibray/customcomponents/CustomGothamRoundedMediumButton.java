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

    /**
     * Constructor to initialise Custom Gotham Rounded Medium Button
     *
     * @param context application context
     */
    public CustomGothamRoundedMediumButton(Context context) {
        super(context);
        this.context=context;
    }

    /**
     * Constructor to initialise Custom Gotham Rounded Medium Button
     *
     * @param context application context
     * @param attrs A collection of attributes, as found associated with a tag in an XML document
     */
    public CustomGothamRoundedMediumButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    /**
     * Constructor to initialise Custom Gotham Rounded Medium Button
     *
     * @param context application context
     * @param attrs A collection of attributes, as found associated with a tag in an XML document
     * @param defStyleAttr A collection of style attributes
     */
    public CustomGothamRoundedMediumButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    /**
     * Method to set typeface for button
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