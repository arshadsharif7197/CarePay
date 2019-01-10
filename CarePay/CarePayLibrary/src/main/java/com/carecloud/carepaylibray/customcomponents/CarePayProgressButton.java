package com.carecloud.carepaylibray.customcomponents;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewParent;

import com.carecloud.carepaylibrary.R;

/**
 * @author pjohnson on 1/8/19.
 */
public class CarePayProgressButton extends CarePayButton {

    private CharSequence textHolder;
    private View progress;

    public CarePayProgressButton(Context context) {
        super(context);
    }

    public CarePayProgressButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CarePayProgressButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setProgressEnabled(boolean enabled) {
        if (enabled) {
            if (progress == null) {
                ViewParent viewParent = getParent();
                if (viewParent instanceof View) {
                    progress = ((View) viewParent).findViewById(R.id.progress_loading);
                }
            }

            if (progress != null) {
                progress.setVisibility(VISIBLE);
                textHolder = getText();
//                setText(null);
            }

        } else if (textHolder != null) {
            setText(textHolder);
            if (progress != null) {
                progress.setVisibility(GONE);
            }
        }
    }

    public void finish(){
        if (progress == null) {
            ViewParent viewParent = getParent();
            if (viewParent instanceof View) {
                progress = ((View) viewParent).findViewById(R.id.progress_loading);
            }
        }
        if (progress != null) {
            progress.setVisibility(GONE);
        }
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        ViewParent viewParent = getParent();
        if (progress == null && viewParent instanceof View) {
            progress = ((View) viewParent).findViewById(R.id.progress_loading);
        }

        if (progress != null) {
            ((View) viewParent).setVisibility(visibility);
        }
    }

}

