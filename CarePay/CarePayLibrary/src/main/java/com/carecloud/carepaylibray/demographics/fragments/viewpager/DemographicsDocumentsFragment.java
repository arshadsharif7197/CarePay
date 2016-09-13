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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ZoomButton;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
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
    private InsuranceScannerFragment[] insuranceFragments;
    private SwitchCompat               switchCompat;
    private int insuranceCount = 0;
    private View   view;
    private Button addMoreButton;
    private Button nextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        Log.v(LOG_TAG, "onCreateView()");

        view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        insuranceFragments = new InsuranceScannerFragment[MAX_INSURANCE_CARDS];

        fm = getChildFragmentManager();

        // add license fragment
        LicenseScannerFragment licenseFragment = (LicenseScannerFragment) fm.findFragmentByTag("license");
        if (licenseFragment == null) {
            licenseFragment = new LicenseScannerFragment();
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, licenseFragment, "license").commit();

        // add first insurance fragment
        insuranceFragments[0] = (InsuranceScannerFragment) fm.findFragmentByTag("insurance1");
        if (insuranceFragments[0] == null) {
            insuranceFragments[0] = new InsuranceScannerFragment();
            insuranceFragments[0].setIndex(0);
        }
        fm.beginTransaction().replace(R.id.demographicsDocsInsurance1, insuranceFragments[0], "insurance1")
                .commit();
        insuranceCount = 1;

        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demogr_insurance_switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                for (InsuranceScannerFragment fragment : insuranceFragments) {
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

        // set add health info button
        addMoreButton = (Button) view.findViewById(R.id.demographicsAddMedInfoButton);
        addMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) { // add 2nd or 3rd card
                if (insuranceCount < MAX_INSURANCE_CARDS) {
                    String insuranceTag = "insurance2";
                    int insuranceFragId = R.id.demographicsDocsInsurance2;
                    if (insuranceCount == 2) {
                        insuranceTag = "insurance3";
                        insuranceFragId = R.id.demographicsDocsInsurance3;
                        // hide add health info button
                        showAddHealthButton(false);
                    }

                    // create the fragment
                    InsuranceScannerFragment fragment = (InsuranceScannerFragment) fm.findFragmentByTag(insuranceTag);
                    if (fragment == null) {
                        fragment = new InsuranceScannerFragment();
                        insuranceFragments[insuranceCount] = fragment;
                        fragment.setIndex(insuranceCount);
                        insuranceCount++;
                    }
                    fm.beginTransaction().replace(insuranceFragId, fragment, insuranceTag)
                            .addToBackStack(null)
                            .commit();

                    // scroll bottom
                    fm.executePendingTransactions();
                    ((ScrollView) view.findViewById(R.id.demographicsDocsScroll)).fullScroll(View.FOCUS_DOWN);
                }
            }
        });

        // set the fonts
        setTypefaces(view);

        // override next button function for this screen
        nextButton = (Button) view.findViewById(R.id.demographicsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsActivity) getActivity()).setCurrentItem(3, true);
            }
        });

        // disable next button
        enableNextButton(false);

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


    /**
     * Show/hides the button that adds more insurance cards
     *
     * @param isVisible Whether visible
     */
    public void showAddHealthButton(boolean isVisible) {
        if (!isVisible) {
            addMoreButton.setVisibility(View.GONE);
        } else {
            addMoreButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Enables disables 'Next' button
     *
     * @param enabled Whether enabled
     */
    public void enableNextButton(boolean enabled) {
        if (!enabled) {
            nextButton.setBackgroundColor(getResources().getColor(R.color.light_gray));
        } else {
            nextButton.setBackgroundColor(getResources().getColor(R.color.blue_cerulian));
        }
        nextButton.setEnabled(enabled);
    }
}