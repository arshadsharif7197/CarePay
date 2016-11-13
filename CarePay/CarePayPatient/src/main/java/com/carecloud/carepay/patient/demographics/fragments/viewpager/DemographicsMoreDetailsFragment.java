package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lsoco_user on 9/2/2016.
 * Screen for demographics onboarding confirmation
 */
public class DemographicsMoreDetailsFragment extends Fragment {

    private View                 view;
    private Button               gotoCarePay;
    private DemographicLabelsDTO globalLabelsDTO;
    private DemographicDTO       demographicDTO;
    private TextView             header;
    private TextView             subheader;
    private WorkflowServiceCallback confirmDemWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            PatientNavigationHelper.getInstance(getActivity()).navigateToWorkflow(workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fetch the global labels
        globalLabelsDTO = ((DemographicsActivity) getActivity()).getLabelsDTO();
        demographicDTO = ((DemographicsActivity) getActivity()).getModel();

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);
        initializeUIFields();

        return view;
    }

    private void initializeUIFields() {
        String label;
        header = (TextView) view.findViewById(R.id.moreDetailsHeading);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsAllSetHeader();
        header.setText(label);

        subheader = (TextView) view.findViewById(R.id.moreDetailsSubHeading);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsAllSetSubheader();
        subheader.setText(label);

        gotoCarePay = (Button) view.findViewById(R.id.demographicsGoToCarePayButton);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsAllSetGoButton();
        gotoCarePay.setText(label);
        gotoCarePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDemographicInformation(); // post the updates
            }
        });

        setTypefaces(view);
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), header);
        setProximaNovaRegularTypeface(getActivity(), subheader);
        setGothamRoundedMediumTypeface(getActivity(), gotoCarePay);
    }

    /**
     * Request to back-end for transition
     */
    public void confirmDemographicInformation() {
        DemographicPayloadDTO demographicPayloadDTO = new DemographicPayloadDTO();

        // obtain the updated models from the pager fragments
        DemographicAddressPayloadDTO addressModel = ((DemographicsActivity) getActivity()).getAddressModel();
        if (addressModel != null) {
            demographicPayloadDTO.setAddress(addressModel);
        }

        DemographicPersDetailsPayloadDTO detailsModel = ((DemographicsActivity) getActivity()).getDetailsDTO();
        if (detailsModel != null) {
            demographicPayloadDTO.setPersonalDetails(detailsModel);
        }

        DemographicIdDocPayloadDTO idDocPojo = ((DemographicsActivity) getActivity()).getIdDocModel();
        if (idDocPojo != null) { // add the doc
            List<DemographicIdDocPayloadDTO> idDocPayloadDTOs = new ArrayList<>();
            idDocPojo.setIdCountry("USA"); // to remove
            idDocPayloadDTOs.add(idDocPojo);
            demographicPayloadDTO.setIdDocuments(idDocPayloadDTOs);
        }

        List<DemographicInsurancePayloadDTO> insuranceModelList = ((DemographicsActivity) getActivity()).getInsuranceModelList();
        if (insuranceModelList != null) {
            demographicPayloadDTO.setInsurances(insuranceModelList);
        }

        // dynamic transition
        Gson gson = new Gson();
        String body = gson.toJson(demographicPayloadDTO);
        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getConfirmDemographics();
        WorkflowServiceHelper.getInstance().execute(transitionDTO, confirmDemWorkflowCallback, body);

    }
}
