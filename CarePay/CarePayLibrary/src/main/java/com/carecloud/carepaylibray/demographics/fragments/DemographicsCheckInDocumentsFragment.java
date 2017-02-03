package com.carecloud.carepaylibray.demographics.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.entities.DemographicMetadataEntityIdDocsDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general.MetadataOptionDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;

import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by lsoco_user on 9/2/2016.
 * Demographics documents scanning (driver's license and insurance card)
 */
public class DemographicsCheckInDocumentsFragment extends Fragment {

    private View                                   view;
    private DemographicIdDocPayloadDTO             demPayloadIdDocDTO;
    private DemographicMetadataEntityIdDocsDTO     idDocsMetaDTO;
    private TextView                               idTypeClickable;
    private TextView                               idDocTypeLabel;
    private TextView                               header;
    private String[]                               docTypes;

    DemographicsCheckInDocumentsFragmentListener activityCallback;

    public interface DemographicsCheckInDocumentsFragmentListener {
        void initializeIdDocScannerFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            activityCallback = (DemographicsCheckInDocumentsFragmentListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement DemographicsCheckInDocumentsFragmentListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_documents_check_in, container, false);

        initializeUIFields();

        return view;
    }

    private void initializeUIFields() {
        demPayloadIdDocDTO = DtoHelper.getConvertedDTO(DemographicIdDocPayloadDTO.class, getArguments());
        idDocsMetaDTO = DtoHelper.getConvertedDTO(DemographicMetadataEntityIdDocsDTO.class, getArguments());
        DemographicLabelsDTO globalLabelsMetaDTO = DtoHelper.getConvertedDTO(DemographicLabelsDTO.class, getArguments());

        getOptions();

        // set the fragment
        activityCallback.initializeIdDocScannerFragment();


        // set primary views on parent fragment (ie, all views except sub-fragments)
        header = (TextView) view.findViewById(R.id.documentsTitleLabel);
        String label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsIdentityText().toUpperCase();
        header.setText(label);


        final String labelCancel = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsCancelLabel();

        idDocTypeLabel = (TextView) view.findViewById(R.id.demogrDocTypeLabel);
        label = idDocsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsTitleSelectIdType();
        idDocTypeLabel.setText(label);

        // fetch nested fragments containers
        final FrameLayout idCardContainer = (FrameLayout) view.findViewById(R.id.demographicsDocsLicense);

        idTypeClickable = (TextView) view.findViewById(R.id.demogrDocTypeClickable);
        if(StringUtil.isNullOrEmpty(demPayloadIdDocDTO.getIdType())){
            label = globalLabelsMetaDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsMetaDTO.getDemographicsChooseLabel();
        } else {
            label = demPayloadIdDocDTO.getIdType();
            showCard(idCardContainer, true);
        }

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


        // set the fonts
        setTypefaces();
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

        setProximaNovaSemiboldTypeface(context, header);
    }

    public DemographicIdDocPayloadDTO getDemPayloadIdDocDTO() {
        return demPayloadIdDocDTO;
    }
}