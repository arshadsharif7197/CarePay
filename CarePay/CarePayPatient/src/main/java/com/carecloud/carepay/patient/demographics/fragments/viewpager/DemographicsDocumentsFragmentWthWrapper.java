package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.demographics.fragments.scanner.DocumentScannerFragment;
import com.carecloud.carepay.patient.demographics.fragments.scanner.IdDocScannerFragment;
import com.carecloud.carepay.patient.demographics.fragments.scanner.InsuranceScannerFragment;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityInsurancesDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityItemInsuranceDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.utils.SystemUtil;

import java.util.ArrayList;
import java.util.List;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsDocumentsFragmentWthWrapper extends Fragment
        implements DocumentScannerFragment.NextAddRemoveStatusModifier {

    private FragmentManager                        fm;
    private View                                   view;
    private ScrollView                             detailsScrollView;
    private FrameLayout                            idCardContainer;
    private TextView                               multipleInsClickable;
    private Button                                 nextButton;
    private DemographicIdDocPayloadDTO             demPayloadIdDocDTO;
    private List<DemographicInsurancePayloadDTO>   insuranceDTOsList;
    private DemographicInsurancePayloadDTO         insuranceModel1;
    private DemographicInsurancePayloadDTO         insuranceModel2;
    private DemographicInsurancePayloadDTO         insuranceModel3;
    private DemographicMetadataEntityIdDocsDTO     idDocsMetaDTO;
    private DemographicMetadataEntityInsurancesDTO insurancesMetaDTO;
    private DemographicLabelsDTO                   globalLabelsMetaDTO;
    private TextView                               header;
    private TextView                               subheader;
    private SwitchCompat                           switchCompat;
    private TextView                               idTypeClickable;
    private TextView                               idDocTypeLabel;
    private String[]                               docTypes;
    //    private InsuranceWrapperCollection             wrapperCollection;
    private LinearLayout                           insContainersWrapper;
    private InsuranceWrapperCollection1            wrapperCollection1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        // fetch the global labels
        globalLabelsMetaDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();
        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_documents, container, false);
        // fetch the models
        getPayloadDTOs();

        // fetch the scroll view
        detailsScrollView = (ScrollView) view.findViewById(R.id.demographicsDocsScroll);

        initializeUIFields();

        return view;
    }

    private void initializeUIFields() {
        getOptions();

        setButtons();

        // set the fragment
        setCardContainers();

        String label;
        // set primary views on parent fragment (ie, all views except sub-fragments)
        header = (TextView) view.findViewById(R.id.demographicsDocsHeaderTitle);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.demographicsDocsHeaderSubtitle);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSubheader();
        subheader.setText(label);

        final String labelCancel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();

        idDocTypeLabel = (TextView) view.findViewById(R.id.demogrDocTypeLabel);
        label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectIdType();
        idDocTypeLabel.setText(label);

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setText(label);
        final String titleSelIdDoc = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        idTypeClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SystemUtil.showChooseDialog(getActivity(),
                                            docTypes, titleSelIdDoc, labelCancel,
                                            idTypeClickable,
                                            new SystemUtil.OnClickItemCallback() {
                                                @Override
                                                public void executeOnClick(TextView destination, String selectedOption) {
                                                    demPayloadIdDocDTO.setIdType(selectedOption);
                                                    showCard(idCardContainer, true);
                                                }
                                            });
            }
        });

        setSwitch();

        // set the fonts
        setTypefaces();

        // hide add card button
        showAddCardButton(false);
    }

    private void getOptions() {
        if (idDocsMetaDTO == null) {
            docTypes = new String[1];
            docTypes[0] = CarePayConstants.NOT_DEFINED;

            return;
        }
        // init doc types
        List<String> docTypesStrings = new ArrayList<>();
        for (MetadataOptionDTO o : idDocsMetaDTO.properties.items.identityDocument.properties.identityDocumentType.options) {
            docTypesStrings.add(o.getLabel());
        }
        docTypes = docTypesStrings.toArray(new String[0]);
    }

    private void getPayloadDTOs() {
        demPayloadIdDocDTO = ((DemographicsActivity) getActivity()).getIdDocModel();
        insuranceDTOsList = ((DemographicsActivity) getActivity()).getInsuranceModelList();

        if (demPayloadIdDocDTO == null) {
            demPayloadIdDocDTO = new DemographicIdDocPayloadDTO();
        }

        if (insuranceDTOsList == null) {
            insuranceDTOsList = new ArrayList<>();
        }
    }

    private void setButtons() {
        String label;

        // next button
        nextButton = (Button) view.findViewById(R.id.demographicsDocsNextButton);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsNext();
        nextButton.setText(label);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DemographicsActivity) getActivity()).setIdDocModel(demPayloadIdDocDTO);
                // clear the list
                insuranceDTOsList.clear();
                // add non trivial insurance models
                if (isInsuaranceNonTrivial(insuranceModel1)) {
                    insuranceDTOsList.add(insuranceModel1);
                }
                if (isInsuaranceNonTrivial(insuranceModel2)) {
                    insuranceDTOsList.add(insuranceModel2);
                }
                if (isInsuaranceNonTrivial(insuranceModel3)) {
                    insuranceDTOsList.add(insuranceModel3);
                }
                // set the list in activity
                ((DemographicsActivity) getActivity()).setInsuranceModelList(insuranceDTOsList);
                // move to next tab
                ((DemographicsActivity) getActivity()).setCurrentItem(3, true);
            }
        });

        // add button
        multipleInsClickable = (TextView) view.findViewById(R.id.demographicsDocumentsMultipleInsClickable);
        label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsMultiInsLabel();
        multipleInsClickable.setText(label);
        multipleInsClickable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View buttonView) {
                wrapperCollection1.add(new DemographicInsurancePayloadDTO());
            }
        });
    }

    private boolean isInsuaranceNonTrivial(DemographicInsurancePayloadDTO insModel) {
        return insModel != null &&
                insModel.getInsurancePlan() != null &&
                insModel.getInsuranceProvider() != null &&
                insModel.getInsuranceMemberId() != null;
    }

    private void setCardContainers() {

        // fetch nested fragments containers
        idCardContainer = (FrameLayout) view.findViewById(R.id.demographicsDocsLicense);

        fm = getChildFragmentManager();
        // add license fragment
        IdDocScannerFragment idDocFragment = (IdDocScannerFragment) fm.findFragmentByTag("license");
        if (idDocFragment == null) {
            idDocFragment = new IdDocScannerFragment();
            idDocFragment.setButtonsStatusCallback(this);
            idDocFragment.setModel(demPayloadIdDocDTO); // set the model
            idDocFragment.setIdDocsMetaDTO(idDocsMetaDTO == null ? null : idDocsMetaDTO.properties.items.identityDocument);
        }
        fm.beginTransaction().replace(R.id.demographicsDocsLicense, idDocFragment, "license").commit();

        insContainersWrapper = (LinearLayout) view.findViewById(R.id.demographicsDocsInsHoldersContainer);
        createInsuranceFragments(insContainersWrapper);
    }

    private void createInsuranceFragments(LinearLayout insContainersWrapper) {
        DemographicMetadataEntityItemInsuranceDTO metadataInsuranceDTO
                = (insurancesMetaDTO == null ? null : insurancesMetaDTO.properties.items.insurance);
        wrapperCollection1 = new InsuranceWrapperCollection1((AppCompatActivity) getActivity(),
                                                             insContainersWrapper,
                                                             metadataInsuranceDTO);
        wrapperCollection1.addAll(insuranceDTOsList);
    }

    private void showCard(FrameLayout cardContainer, boolean isVisible) {
        if (isVisible) {
            cardContainer.setVisibility(View.VISIBLE);
        } else {
            cardContainer.setVisibility(View.GONE);
        }
    }

    private void setTypefaces() {
        Context context = getActivity();
        setProximaNovaRegularTypeface(context, idDocTypeLabel);
        setProximaNovaSemiboldTypeface(context, idTypeClickable);

        setGothamRoundedMediumTypeface(context, header);
        setProximaNovaRegularTypeface(context, subheader);
        setProximaNovaRegularTypeface(context, switchCompat);
        setGothamRoundedMediumTypeface(context, multipleInsClickable);
        setGothamRoundedMediumTypeface(context, nextButton);
    }

    private void setSwitch() {
        // set the switch
        fm.executePendingTransactions();
        switchCompat = (SwitchCompat) view.findViewById(R.id.demographicsDocumentsInsuranceSwitch);

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean on) {
                insContainersWrapper.setVisibility(on ? View.VISIBLE : View.GONE);
            }
        });
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsDocumentsSwitchLabel();
        switchCompat.setText(label);
        SystemUtil.hideSoftKeyboard(getActivity());
    }

    public void setIdDocsMetaDTO(DemographicMetadataEntityIdDocsDTO idDocsMetaDTO) {
        this.idDocsMetaDTO = idDocsMetaDTO;
    }

    public void setInsurancesMetaDTO(DemographicMetadataEntityInsurancesDTO insurancesMetaDTO) {
        this.insurancesMetaDTO = insurancesMetaDTO;
    }

    @Override
    public void showAddCardButton(boolean isVisible) {
            multipleInsClickable.setVisibility(isVisible ? View.VISIBLE : View.GONE);
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

    class InsuranceWrapper1 {

        private InsuranceScannerFragment wrapperScannerFragment;
        private LinearLayout             holderWrapperView;

        public InsuranceWrapper1(AppCompatActivity context,
                                 DemographicMetadataEntityItemInsuranceDTO metadata,
                                 DemographicInsurancePayloadDTO payload,
                                 LinearLayout parentView,
                                 OnClickRemoveListener clickListener) {
            // create the container with both fragment and remove button
            holderWrapperView = new LinearLayout(context);
            holderWrapperView.setOrientation(LinearLayout.VERTICAL);

            FrameLayout fragHolder = new FrameLayout(context);
            int fragHolderId = View.generateViewId();
            fragHolder.setId(fragHolderId);
            holderWrapperView.addView(fragHolder, 0);

            TextView removeClickable = new TextView(context);
            removeClickable.setLayoutParams(new ActionBarOverlayLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                                    ViewGroup.LayoutParams.WRAP_CONTENT));
            removeClickable.setGravity(Gravity.CENTER);
            removeClickable.setClickable(true);
            removeClickable.setText("Remove");
            removeClickable.setTextSize(14);
            removeClickable.setTextColor(ContextCompat.getColor(context, R.color.harvard_crimson));
            clickListener.setInsuranceWrapper(this);
            removeClickable.setOnClickListener(clickListener);
            holderWrapperView.addView(removeClickable, 1);

            // add the fragment
            FragmentManager fm = context.getSupportFragmentManager();
            wrapperScannerFragment = new InsuranceScannerFragment();
            wrapperScannerFragment.setInsuranceMetadataDTO(metadata);
            wrapperScannerFragment.setInsuranceDTO(payload);
            fm.beginTransaction().replace(fragHolderId, wrapperScannerFragment).commit();

            parentView.addView(holderWrapperView);
        }

        public DemographicInsurancePayloadDTO getWrapperPayloadDTO() {
            return wrapperScannerFragment.getInsuranceDTO();
        }
    }

    class InsuranceWrapperCollection1 {

        private static final int MAX_CARDS = 3;
        private AppCompatActivity wrapperContext;
        private LinearLayout      parent;
        private List<InsuranceWrapper1> wrappers = new ArrayList<>();
        private DemographicMetadataEntityItemInsuranceDTO metadata;

        public InsuranceWrapperCollection1(AppCompatActivity context,
                                           LinearLayout parent,
                                           DemographicMetadataEntityItemInsuranceDTO metadata) {
            this.wrapperContext = context;
            this.parent = parent;
            this.metadata = metadata;
        }

        public void add(DemographicInsurancePayloadDTO payloadDTO) {
            int count = wrappers.size();
            if (count < MAX_CARDS) {
                InsuranceWrapper1 insuranceWrapper = new InsuranceWrapper1(wrapperContext,
                                                                           metadata,
                                                                           payloadDTO,
                                                                           parent,
                                                                           new OnClickRemoveListener(InsuranceWrapperCollection1.this));
                wrappers.add(insuranceWrapper);
                count++;
            }
            if (count >= MAX_CARDS) {
                showAddCardButton(false);
            }
        }

        public void addAll(List<DemographicInsurancePayloadDTO> payloadDTOs) {
            for (DemographicInsurancePayloadDTO payloadDTO : payloadDTOs) {
                add(payloadDTO);
            }
        }

        public void remove(InsuranceWrapper1 insuranceWrapper) {
            parent.removeView(insuranceWrapper.holderWrapperView);
            wrappers.remove(insuranceWrapper);
        }
    }

    class OnClickRemoveListener implements View.OnClickListener {

        private InsuranceWrapper1           insuranceWrapper;
        private InsuranceWrapperCollection1 wrapperCollection;

        public OnClickRemoveListener(InsuranceWrapperCollection1 wrapperCollection) {
            this.wrapperCollection = wrapperCollection;
        }

        @Override
        public void onClick(View v) {
            Log.v(DemographicsDocumentsFragmentWthWrapper.class.getSimpleName(), "remove clicked");
            wrapperCollection.remove(insuranceWrapper);
            showAddCardButton(true);
        }

        public void setInsuranceWrapper(InsuranceWrapper1 insuranceWrapper) {
            this.insuranceWrapper = insuranceWrapper;
        }
    }
}