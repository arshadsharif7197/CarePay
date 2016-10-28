package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

/**
 * Created by harshal_patil on 9/22/2016.
 */
public class CustomProxyNovaRegularLabel extends TextView {

    Context context;
    public CustomProxyNovaRegularLabel(Context context) {
        super(context);
        this.context=context;
    }

    public CustomProxyNovaRegularLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomProxyNovaRegularLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_regular.otf");
        this.setTypeface(tf);
        //this.setGravity(TEXT_ALIGNMENT_CENTER);
        if (Build.VERSION.SDK_INT < 23) {
            this.setTextAppearance(context, R.style.DefaultTextAppearanceProxyNovaRegularLabel);
        } else{
            this.setTextAppearance(R.style.DefaultTextAppearanceProxyNovaRegularLabel);
        }
        this.setClickable(false);
    }
}