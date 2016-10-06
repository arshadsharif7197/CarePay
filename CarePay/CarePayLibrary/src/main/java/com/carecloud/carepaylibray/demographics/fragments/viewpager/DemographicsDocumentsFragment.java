package com.carecloud.carepaylibray.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.carecloud.carepaylibray.demographics.models.DemIdDocPayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemInsurancePayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemPayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoPayloadModel;

import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragment extends Fragment implements DocumentScannerFragment.NextAddRemoveStatusModifier {

    private static final String LOG_TAG = DemographicsDocumentsFragment.class.getSimpleName();
    private FragmentManager                        fm;
    private View                                   view;
    private ScrollView                             detailsScrollView;
    private SwitchCompat                           switchCompat;
    private FrameLayout                            insCardContainer1;
    private FrameLayout                            insCardContainer2;
    private FrameLayout                            insCardContainer3;
    private LicenseScannerFragment                 licenseFragment;
    private InsuranceScannerFragment               insuranceFragment;
    private InsuranceScannerFragment      extraInsuranceFrag1;
    private InsuranceScannerFragment      extraInsuranceFrag2;
    private boolean                       isSecondCardAdded;
    private boolean                       isThirdCardAdded;
    private Button                        addCardButton;
    private Button                        nextButton;
    private DemIdDocPayloadPojo           demPayloadIdDocPojo;
    private List<DemInsurancePayloadPojo> insuranceModelList;
    private DemPayloadPojo                payload;
    private DemInsurancePayloadPojo       insuranceModel1;
    private DemInsurancePayloadPojo       insuranceModel2;
    private DemInsurancePayloadPojo       insuranceModel3;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);

        // fetch the scroll view
        detailsScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        setButtons();
        setCardContainers();
        setSwitch();

        // set the fonts
        setTypefaces(view);

        // hide add card button
        showAddCardButton(false);

        // disable next button
//        enableNextButton(false); // TODO: 9/27/2016 uncomment

        return view;
    }

    private void getTheModels() {
        payload = ((DemographicsActivity) getActivity()).getDemographicInfoPayloadModel();
        if (payload != null) {
            demPayloadIdDocPojo = payload.getIdDocument();
            insuranceModelList = payload.getInsurances();
        }
    }

    public DemIdDocPayloadPojo getDemPayloadIdDocPojo() {
        return demPayloadIdDocPojo;
    }

    public List<DemInsurancePayloadPojo> getInsuranceModelList() {
        return insuranceModelList;
    }

    /**
     * Helper to set the buttons
     */
    private void setButtons() {
        // next button
        nextButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsActivity) getActivity()).setDemPayloadIdDocPojo(demPayloadIdDocPojo);
                DemInsurancePayloadPojo model = new DemInsurancePayloadPojo();
                // clear the list
                insuranceModelList.clear();
                // add non trivial insurance models
                if (isInsuaranceNonTrivial(insuranceModel1)) {
                    insuranceModelList.add(insuranceModel1);
                }
                if (isInsuaranceNonTrivial(insuranceModel2)) {
                    insuranceModelList.add(insuranceModel2);
                }
                if (isInsuaranceNonTrivial(insuranceModel3)) {
                    insuranceModelList.add(insuranceModel3);
                }
                // set the list in activity
                ((DemographicsActivity)getActivity()).setInsuranceModelList(insuranceModelList);
                // move to next tab
                ((DemographicsActivity) getActivity()).setCurrentItem(3, true);
            }
        });

        // add button
        addCardButton = (Button) view.findViewById(R.id.demographicsAddMedInfoButton);
        addCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                if (!isSecondCardAdded) {
                    isSecondCardAdded = true;
                    showInsuranceCard(insCardContainer2, true);
                } else if (!isThirdCardAdded) {
                    isThirdCardAdded = true;
                    showInsuranceCard(insCardContainer3, true);
                    showAddCardButton(false);
                }
                scrollToBottom();
            }
        });
    }

    private boolean isInsuaranceNonTrivial(DemInsurancePayloadPojo insModel) {
        return insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    /**
     * Helper to create the nested fragments containing the insurance card details
     */
    private void setCardContainers() {
        // fetch the models
        getTheModels();

        // fetch nested fragments containers
        insCardContainer1 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance1);
        insCardContainer2 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance2);
        insCardContainer3 = (FrameLayout) view.findViewById(R.id.demographicsDocsInsurance3);

        isSecondCardAdded = false;
        isThirdCardAdded = false;

        fm = getChildFragmentManager();
        // add license fragment
        licenseFragment = (LicenseScannerFragment) fm.findFragmentByTag("license");
        if (demPayloadIdDocPojo == null) {
            demPayloadIdDocPojo = new DemIdDocPayloadPojo();
        }
        if (licenseFragment == null) {
            licenseFragment = new LicenseScannerFragment();
            licenseFragment.setButtonsStatusCallback(this);
            licenseFragment.setModel(demPayloadIdDocPojo); // set the model
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, licenseFragment, "license").commit();

        // add insurance fragments
        insuranceModel1 = getInsuranceModelAtIndex(0);
        if (insuranceModel1 == null) {
            insuranceModel1 = new DemInsurancePayloadPojo();
        }
        insuranceFragment = (InsuranceScannerFragment) fm.findFragmentByTag("insurance1");
        if (insuranceFragment == null) {
            insuranceFragment = new InsuranceScannerFragment();
            insuranceFragment.setButtonsStatusCallback(this);
            insuranceFragment.setModel(insuranceModel1); // set the model (if avail)
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance1, insuranceFragment, "insurance1")
                .commit();

        insuranceModel2 = getInsuranceModelAtIndex(1);
        if (insuranceModel2 == null) {
            insuranceModel2 = new DemInsurancePayloadPojo();
        } else {
            isSecondCardAdded = true;
        }
        extraInsuranceFrag1 = (InsuranceScannerFragment) fm.findFragmentByTag("insurance2");
        if (extraInsuranceFrag1 == null) {
            extraInsuranceFrag1 = new InsuranceScannerFragment();
            extraInsuranceFrag1.setButtonsStatusCallback(this);
            extraInsuranceFrag1.setModel(insuranceModel2); // set the model (if avail)
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance2, extraInsuranceFrag1, "insurance2")
                .commit();

        insuranceModel3 = getInsuranceModelAtIndex(2);
        if (insuranceModel3 == null) {
            insuranceModel3 = new DemInsurancePayloadPojo();
        } else {
            isThirdCardAdded = true;
        }
        extraInsuranceFrag2 = (InsuranceScannerFragment) fm.findFragmentByTag("insurance3");
        if (extraInsuranceFrag2 == null) {
            extraInsuranceFrag2 = new InsuranceScannerFragment();
            extraInsuranceFrag2.setButtonsStatusCallback(this);
            extraInsuranceFrag2.setModel(insuranceModel3); // set the model (if avail)
        }
        fm.beginTransaction()
                .replace(R.id.demographicsDocsInsurance3, extraInsuranceFrag2, "insurance3")
                .commit();
    }

    private DemInsurancePayloadPojo getInsuranceModelAtIndex(int i) {
        DemInsurancePayloadPojo model = null;
        if (insuranceModelList != null) {
            int numOfInsurances = insuranceModelList.size();
            if (numOfInsurances > i) { // check if the list has an item at index i
                model = insuranceModelList.get(i);
            }
        }
        return model;
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
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicsInsuranceSwitch));
        setGothamRoundedMediumTypeface(getActivity(), addCardButton);
        setGothamRoundedMediumTypeface(getActivity(), nextButton);
    }

    @Override
    public void showAddCardButton(boolean isVisible) {
        if (!isVisible) {
            addCardButton.setVisibility(View.GONE);
        } else {
            if (!isThirdCardAdded) { // show only if there is another add possibility
                addCardButton.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void enableNextButton(boolean isEnabled) {
        nextButton.setEnabled(isEnabled);
    }

    @Override
    public void scrollToBottom() {
        View bottomView = view.findViewById(R.id.demographicsDocsBottomView);
        detailsScrollView.scrollTo(0, bottomView.getBottom());
    }


    /**
     * Helper to set the switch
     */
    private void setSwitch() {
        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demographicsInsuranceSwitch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                showInsuranceCard(insCardContainer1, on);
                if (isSecondCardAdded) {
                    showInsuranceCard(insCardContainer2, on);
                } else {
                    showInsuranceCard(insCardContainer2, false);
                }
                if (isThirdCardAdded) {
                    showInsuranceCard(insCardContainer3, on);
                } else {
                    showInsuranceCard(insCardContainer3, false);
                }
                showAddCardButton(on && !isThirdCardAdded);
            }
        });
//        switchCompat.setChecked(false);
    }
}