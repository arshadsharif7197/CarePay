package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

/**
 * Created by harshal_patil on 22/09/16.
 * @Deprecated use CarePayTextView
 */
@Deprecated
public class CustomProxyNovaSemiBoldLabel extends TextView {
    private Context context;
    public CustomProxyNovaSemiBoldLabel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        init();
    }

    public CustomProxyNovaSemiBoldLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomProxyNovaSemiBoldLabel(Context context) {
        super(context);
        this.context=context;
        init();
    }

    private void init() {
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_semibold.otf");
        this.setTypeface(tf);
        //this.setGravity(TEXT_ALIGNMENT_CENTER);
        if (Build.VERSION.SDK_INT < 23) {
            this.setTextAppearance(context, R.style.DefaultTextAppearanceProxyNovaSemiBoldLabel);
        } else{
            this.setTextAppearance(R.style.DefaultTextAppearanceProxyNovaSemiBoldLabel);
        }
        this.setClickable(false);
    }
}