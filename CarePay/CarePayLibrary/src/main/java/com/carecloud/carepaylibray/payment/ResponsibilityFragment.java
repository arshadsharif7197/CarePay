package com.carecloud.carepaylibray.payment;

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
import com.carecloud.carepaylibray.utils.Utility;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedBookTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Responsibility screen
 */
public class ResponsibilityFragment extends Fragment {

    private static final String LOG_TAG = ResponsibilityFragment.class.getSimpleName();
    AppCompatActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_responsibility, container, false);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.respons_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.respons_toolbar_title);
        setGothamRoundedMediumTypeface(mActivity, title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setTypefaces(view);

        return view;
    }

    /**
     * Helper to set the typefaces
     * @param view The parent view
     */
    private void setTypefaces(View view) {
        // set the typefaces
        setGothamRoundedBookTypeface(mActivity, (TextView) view.findViewById(R.id.respons_total_label));
        setGothamRoundedMediumTypeface(mActivity, (TextView) view.findViewById(R.id.respons_total));
        setProximaNovaRegularTypeface(mActivity, (TextView) view.findViewById(R.id.respons_prev_balance_label));
        setProximaNovaRegularTypeface(mActivity, (TextView) view.findViewById(R.id.respons_copay_label));
        setProximaNovaSemiboldTypeface(mActivity, (TextView) view.findViewById(R.id.respons_prev_balance));
        setProximaNovaSemiboldTypeface(mActivity, (TextView) view.findViewById(R.id.respons_copay));
        setGothamRoundedMediumTypeface(mActivity, (Button) view.findViewById(R.id.respons_pay));
    }

    /**
     * For tests
     *
     * @param activity The activity
     */
    public void setActivity(KeyboardHolderActivity activity) {
        mActivity = activity;
    }
}