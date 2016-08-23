package com.carecloud.carepayandroid.responsibility;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * Builds dynamically the layout
 */
public class ResponsibilityLayoutRenderer {

    private Context mContext;

    public ResponsibilityLayoutRenderer(Context context) {
        mContext = context;
    }

    public View createLayout() {
        // scroll view
        ScrollView root = new ScrollView(mContext);
        root.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                          FrameLayout.LayoutParams.MATCH_PARENT));

        // main layout container
        LinearLayout mainLl = new LinearLayout(mContext);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                                                                           FrameLayout.LayoutParams.MATCH_PARENT);
        llParams.setMargins(10, 10, 10, 10);
        mainLl.setLayoutParams(llParams);
        mainLl.setOrientation(LinearLayout.VERTICAL);

        // components // todo automatize
        TextView tvDoctor = new TextView(mContext);
        LinearLayout.LayoutParams tvDoctorLayoutParams
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                                LinearLayout.LayoutParams.WRAP_CONTENT);
        tvDoctorLayoutParams.setMargins(0, 10, 0, 0);
        tvDoctor.setLayoutParams(tvDoctorLayoutParams);
        tvDoctor.setAllCaps(true);
        tvDoctor.setGravity(Gravity.CENTER);
        tvDoctor.setText("DR. JOHN SMITH");

        // build hierarchy
        mainLl.addView(tvDoctor);
        root.addView(mainLl);

        return root;
    }
}
