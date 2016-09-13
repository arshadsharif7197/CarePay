package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.LicenseScannerFragment;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment {

    private static final String LOG_TAG             = DemographicsDocumentsFragment.class.getSimpleName();
    private static final int    MAX_INSURANCE_CARDS = 3;

    private FragmentManager            fm;
    private InsuranceScannerFragment[] insuranceFragment;
    private SwitchCompat               switchCompat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");

        View view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        insuranceFragment = new InsuranceScannerFragment[MAX_INSURANCE_CARDS];

        fm = getChildFragmentManager();

        // add license fragment
        LicenseScannerFragment licenseFragment = (LicenseScannerFragment) fm.findFragmentByTag("license");
        if (licenseFragment == null) {
            licenseFragment = new LicenseScannerFragment();
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, licenseFragment, "license").commit();

        // add first insurance fragment
        insuranceFragment[0] = (InsuranceScannerFragment) fm.findFragmentByTag("insurance1");
        if (insuranceFragment[0] == null) {
            insuranceFragment[0] = new InsuranceScannerFragment();
        }
        fm.beginTransaction().replace(R.id.demographicsDocsInsurance1, insuranceFragment[0], "insurance1")
                .commit();

        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demogr_insurance_switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                for (int i = 0; i < insuranceFragment.length; i++) {
                    InsuranceScannerFragment fragment = insuranceFragment[i];
                    if (fragment != null) {
                        if (on) { // show all insurance fragments
                            fm.beginTransaction().show(fragment).commit();
                        } else { // hide all insurance fragments
                            fm.beginTransaction().hide(fragment).commit();
                        }
                    }
                }
            }
        });
        switchCompat.setChecked(false);

        setTypefaces(view);

        return view;
    }

    /**
     * Helper to set the typeface to all textviews
     *
     * @param view The parent view
     */
    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_header_title));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_docs_header_subtitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demogr_insurance_switch));
    }
}