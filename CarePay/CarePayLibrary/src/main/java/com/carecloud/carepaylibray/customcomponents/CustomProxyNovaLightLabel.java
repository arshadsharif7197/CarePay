package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;

/**
 * Created by harshal_patil on 10/3/2016.
 * @Deprecated use CarePayTextView
 */
@Deprecated
public class CustomProxyNovaLightLabel extends TextView {

    Context context;
    public CustomProxyNovaLightLabel(Context context) {
        super(context);
        this.context=context;
    }

    public CustomProxyNovaLightLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public CustomProxyNovaLightLabel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(layoutParams);
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/proximanova_light.otf");
        this.setTypeface(tf);
       // this.setGravity(TEXT_ALIGNMENT_CENTER);
        if (Build.VERSION.SDK_INT < 23) {
            this.setTextAppearance(context, R.style.DefaultTextAppearance);
        } else{
            this.setTextAppearance(R.style.DefaultTextAppearance);
        }
        this.setClickable(false);
    }
}