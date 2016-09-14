package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibray.demographics.fragments.scanner.LicenseScannerFragment;

import static com.carecloud.carepaylibray.utils.Utility.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.Utility.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment implements DocumentScannerFragment.NextAddRemoveStatusModifier {

    private static final String LOG_TAG             = DemographicsDocumentsFragment.class.getSimpleName();
    private static final int    MAX_INSURANCE_CARDS = 3;

    private FragmentManager fm;
    private View            view;
    private ScrollView      detailsScrollView;
    private SwitchCompat    switchCompat;
    private FrameLayout     insCardContainer1;
    private FrameLayout     insCardContainer2;
    private FrameLayout     insCardContainer3;
    private boolean         isSecondCardAdded;
    private boolean         isThirdCarAdded;
    private Button          addCardButton;
    private Button          removeCardButton;
    private Button          nextButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        // fetch the scroll view
        detailsScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        // fetch nested fragments containers
        insCardContainer1 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance1);
        insCardContainer2 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance2);
        insCardContainer3 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance3);

        isSecondCardAdded = false;
        isThirdCarAdded = false;

        fm = getChildFragmentManager();
        // add license fragment
        LicenseScannerFragment licenseFragment = (LicenseScannerFragment) fm.findFragmentByTag("license");
        if (licenseFragment == null) {
            licenseFragment = new LicenseScannerFragment();
            licenseFragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, licenseFragment, "license").commit();

        // add insurance fragments
        InsuranceScannerFragment insuranceFragment;
        insuranceFragment = (InsuranceScannerFragment) fm.findFragmentByTag("insurance1");
        if (insuranceFragment == null) {
            insuranceFragment = new InsuranceScannerFragment();
            insuranceFragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance1, insuranceFragment, "insurance1")
                .commit();

        insuranceFragment = (InsuranceScannerFragment) fm.findFragmentByTag("insurance2");
        if (insuranceFragment == null) {
            insuranceFragment = new InsuranceScannerFragment();
            insuranceFragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance2, insuranceFragment, "insurance2")
                .commit();

        insuranceFragment = (InsuranceScannerFragment) fm.findFragmentByTag("insurance3");
        if (insuranceFragment == null) {
            insuranceFragment = new InsuranceScannerFragment();
            insuranceFragment.setButtonsStatusCallback(this);
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance3, insuranceFragment, "insurance3")
                .commit();

        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demogr_insurance_switch);
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                showInsuranceCard(insCardContainer1, on);
                if (isSecondCardAdded) {
                    showInsuranceCard(insCardContainer2, on);
                } else {
                    // if not added yet, hide the container
                    showInsuranceCard(insCardContainer2, false);
                }
                if (isThirdCarAdded) {
                    showInsuranceCard(insCardContainer3, on);
                } else {
                    // if not added yet, hide the container
                    showInsuranceCard(insCardContainer3, false);
                }
                scrollToBottom();
            }
        });
        switchCompat.setChecked(false);

        // set add health info button
        addCardButton = (Button) view.findViewById(R.id.demographicsAddMedInfoButton);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
            }
        });

        removeCardButton = (Button) view.findViewById(R.id.demographicsRemoveMedInfoButton);
        removeCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        // set the fonts
        setTypefaces(view);

        // override next button function for this screen
        nextButton = (Button) view.findViewById(R.id.demographicsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // move to next tab
                ((DemographicsActivity) getActivity()).setCurrentItem(3, true);
            }
        });

        // hide add/remove buttons
        showAddCardButton(false);
        showRemoveCardButton(false);
        enableRemoveCardButton(false);

        // disable next button
        enableNextButton(false);

        return view;
    }

    /**
     * Toggles visible/invisible a container of an insurance card details
     *
     * @param cardContainer The container
     * @param isVisible     Whether visible
     */
    private void showInsuranceCard(FrameLayout cardContainer, boolean isVisible) {
        if (isVisible) {
            cardContainer.setVisibility(View.VISIBLE);
        } else {
            cardContainer.setVisibility(View.GONE);
        }
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

    @Override
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            addCardButton.setVisibility(View.GONE);
        } else {
            addCardButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showRemoveCardButton(boolean isVisible) {
        if (!isVisible) {
            removeCardButton.setVisibility(View.GONE);
        } else {
            removeCardButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void enableRemoveCardButton(boolean isEnabled) {
        if(isEnabled) {
            // enable if at least one extra card was added
            if(isSecondCardAdded || isThirdCarAdded) {
                removeCardButton.setEnabled(true);
                removeCardButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.blue_cerulian));
            } else {
                removeCardButton.setEnabled(false);
                removeCardButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.slateGray));
            }
        } else {
            removeCardButton.setEnabled(false);
            removeCardButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.slateGray));
        }
    }

    @Override
    public void enableNextButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void scrollToBottom() {
        detailsScrollView.fullScroll(View.FOCUS_DOWN);
    }
}