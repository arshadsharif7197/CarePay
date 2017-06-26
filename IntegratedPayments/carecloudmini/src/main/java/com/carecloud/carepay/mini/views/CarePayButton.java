package com.carecloud.carepay.mini.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.carecloud.carepay.mini.R;
import com.electapps.fontviews.FontButton;

/**
 * Created by lmenendez on 6/23/17
 */

public class CarePayButton extends FontButton {

    private CharSequence textHolder;
    private View progress;

    public CarePayButton(Context context) {
        super(context);
    }

    public CarePayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarePayButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        if(!enabled){

            if(progress == null) {
                ViewParent viewParent = getParent();
                if (viewParent instanceof View) {
                    progress = ((View) viewParent).findViewById(R.id.progress_loading);
                }
            }

            if(progress != null) {
                progress.setVisibility(VISIBLE);
                textHolder = getText();
                setText(null);
            }

        }else if(textHolder != null){
            setText(textHolder);

            if(progress != null){
                progress.setVisibility(GONE);
            }
        }
    }

}
