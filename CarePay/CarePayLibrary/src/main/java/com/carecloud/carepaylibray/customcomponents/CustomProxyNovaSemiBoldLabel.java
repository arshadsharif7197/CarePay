package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by harshal_patil on 22/09/16.
 */
public class CustomProxyNovaSemiBoldLabel extends TextView {

    public CustomProxyNovaSemiBoldLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    public CustomProxyNovaSemiBoldLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomProxyNovaSemiBoldLabel(Context context) {
        super(context);

        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_semibold.otf");
        setTypeface(tf);
    }
}