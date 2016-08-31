package com.carecloud.carepaylibray.customcomponents;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import com.carecloud.carepaylibrary.R;

/**
 * Created by Jahirul Bhuiyan on 8/29/2016.
 */
public class BlueButtonFill extends Button {

    Context context;
    public BlueButtonFill(Context context) {
        super(context);
        this.context=context;
    }

    public BlueButtonFill(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public BlueButtonFill(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BlueButtonFill(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        setTextColor( ContextCompat.getColor(context, R.color.white));
        setBackgroundResource(R.color.bright_cerulean);
        setTextSize(context.getResources().getDimension(R.dimen.button_textsize_medium));
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "GothamRounded-Medium.otf");
        setTypeface(tf);
    }
}
