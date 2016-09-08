package com.carecloud.carepaylibray.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setTypefaceFromAssets;


/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

//    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (KeyboardHolderActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        setTypefaceFromAssets(mActivity, "fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(mActivity, R.drawable.icn_patient_mode_nav_back));
        mActivity.setSupportActionBar(toolbar);

        // set the typefaces
        setTypefaceFromAssets(mActivity, "fonts/gotham_rounded_book.otf", (TextView) view.findViewById(R.id.respons_total_label));
        setTypefaceFromAssets(mActivity, "fonts/gotham_rounded_medium.otf", (TextView) view.findViewById(R.id.respons_total));
        setTypefaceFromAssets(mActivity, "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setTypefaceFromAssets(mActivity, "fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.respons_copay_label));
        setTypefaceFromAssets(mActivity, "fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.respons_prev_balance));
        setTypefaceFromAssets(mActivity, "fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.respons_copay));
        setTypefaceFromAssets(mActivity, "fonts/gotham_rounded_medium.otf", (Button) view.findViewById(R.id.respons_pay));

        return view;
    }

    /**
     * For tests
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        mActivity = activity;
    }
}