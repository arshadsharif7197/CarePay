package com.carecloud.carepaylibray.fragments;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity;


/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

//    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    AppCompatActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        setTypefaceFromAssets("fonts/gotham_rounded_medium.otf", title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // set the typefaces
        setTypefaceFromAssets("fonts/gotham_rounded_book.otf", (TextView) view.findViewById(R.id.respons_total_label));
        setTypefaceFromAssets("fonts/gotham_rounded_medium.otf", (TextView) view.findViewById(R.id.respons_total));
        setTypefaceFromAssets("fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setTypefaceFromAssets("fonts/proximanova_regular.otf", (TextView) view.findViewById(R.id.respons_copay_label));
        setTypefaceFromAssets("fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.respons_prev_balance));
        setTypefaceFromAssets("fonts/proximanova_semibold.otf", (TextView) view.findViewById(R.id.respons_copay));
        setTypefaceFromAssets("fonts/gotham_rounded_medium.otf", (Button) view.findViewById(R.id.respons_pay));

        return view;
    }

    private void setTypefaceFromAssets(String pathToFontInAssets, TextView view) {
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), pathToFontInAssets);
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