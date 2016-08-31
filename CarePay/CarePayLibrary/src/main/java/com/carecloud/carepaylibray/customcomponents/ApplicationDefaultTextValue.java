package com.carecloud.carepaylibray.customcomponents;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

/**
 * Created by Jahirul Bhuiyan on 8/29/2016.
 */
public class ApplicationDefaultTextValue extends TextView {

    Context context;
    public ApplicationDefaultTextValue(Context context) {
        super(context);
        this.context=context;
    }

    public ApplicationDefaultTextValue(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public ApplicationDefaultTextValue(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ApplicationDefaultTextValue(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context=context;
        init();
    }

    private void init() {
        setTextColor( ContextCompat.getColor(context, R.color.valuetext_default_textcolor));
        setTextSize(context.getResources().getDimension(R.dimen.textview_default_textsize));
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "ProximaNova-Sbold.otf");
        setTypeface(tf);
    }
}
