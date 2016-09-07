package com.carecloud.carepaylibray.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    private KeyboardHolderActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (KeyboardHolderActivity)getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(!isTablet()) {
            Log.v(LOG_TAG, "onCreateView() phone");
            view = inflater.inflate(R.layout.fragment_responsibility, container, false);
        } else {
            Log.v(LOG_TAG, "onCreateView() tablet");
            view = inflater.inflate(R.layout.fragment_responsibility_tablet, container, false);
        }

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        title.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
        title.setTextSize(20);
        title.setText(mActivity.getString(R.string.respons_title));
        setTypefaceFromAssets("fonts/GothamRnd-Medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(mActivity.getResources().getDrawable(R.drawable.icn_patient_mode_nav_back));
        mActivity.setSupportActionBar(toolbar);

        // set the typefaces
        setTypefaceFromAssets("fonts/Gotham-rounded-book.otf", (TextView) view.findViewById(R.id.respons_total_label));
        setTypefaceFromAssets("fonts/GothamRnd-Medium.otf", (TextView) view.findViewById(R.id.respons_total));
        setTypefaceFromAssets("fonts/ProximaNova-Reg.otf", (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setTypefaceFromAssets("fonts/ProximaNova-Reg.otf", (TextView) view.findViewById(R.id.respons_copay_label));
        setTypefaceFromAssets("fonts/Proxima_Nova_Semibold.otf", (TextView) view.findViewById(R.id.respons_prev_balance));
        setTypefaceFromAssets("fonts/Proxima_Nova_Semibold.otf", (TextView) view.findViewById(R.id.respons_copay));
        setTypefaceFromAssets("fonts/GothamRnd-Medium.otf", (Button) view.findViewById(R.id.respons_pay));

        return view;
    }


    /**
     * Detect is configuration of a tablet
     * @return True is tablet; false otherwise
     */
    public boolean isTablet() {
        int screenLayoutWidthSize = mActivity.getResources().getConfiguration().screenWidthDp;
        // detect smallest width size
        return  (screenLayoutWidthSize >= 600);
    }

    private void setTypefaceFromAssets(String pathToFontInAssets, TextView view) {
        Typeface typeface = Typeface.createFromAsset(mActivity.getAssets(), pathToFontInAssets);
        view.setTypeface(typeface);
    }

    /**
     * For tests
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        mActivity = activity;
    }
}