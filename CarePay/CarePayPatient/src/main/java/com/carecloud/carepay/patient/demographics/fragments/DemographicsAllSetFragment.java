package com.carecloud.carepay.patient.demographics.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.base.PatientNavigationHelper;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.fragments.CheckInDemographicsBaseFragment;

import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;

public class DemographicsAllSetFragment extends CheckInDemographicsBaseFragment {

    private View                 view;
    private Button               gotoCarePay;
    private DemographicLabelsDTO globalLabelsDTO;
    private DemographicDTO       demographicDTO;
    private TextView             header;
    private WorkflowServiceCallback confirmDemWorkflowCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PatientNavigationHelper.navigateToWorkflow(getActivity(), workflowDTO);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
            Log.e(getActivity().getString(com.carecloud.carepaylibrary.R.string.alert_title_server_error), exceptionMessage);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fetch the global labels
//        globalLabelsDTO = ((NewDemographicsActivity) getActivity()).getLabelsDTO();
//        demographicDTO = ((NewDemographicsActivity) getActivity()).getModel();

        // create the view
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);
        initializeUIFields();

        return view;
    }

    @Override
    protected boolean passConstraints(View view) {
        return true;
    }

    @Override
    protected int getContentId() {
        return R.layout.fragment_demographics_moredetails;
    }

    @Override
    protected DemographicDTO updateDemographicDTO(View view) {
        return null;
    }

    private void initializeUIFields() {
        String label;
        header = (TextView) view.findViewById(R.id.moreDetailsHeading);
        label = globalLabelsDTO == null ? CarePayConstants.NOT_DEFINED : globalLabelsDTO.getDemographicsAllSetHeader();
        header.setText(label);

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
        setGothamRoundedMediumTypeface(getActivity(), gotoCarePay);
    }

    /**
     * Request to back-end for transition
     */
    public void confirmDemographicInformation() {
        DemographicPayloadDTO demographicPayloadDTO = new DemographicPayloadDTO();

        // obtain the updated models from the pager fragments
//        DemographicAddressPayloadDTO addressModel = ((NewDemographicsActivity) getActivity()).getAddressModel();
//        if (addressModel != null) {
//            demographicPayloadDTO.setAddress(addressModel);
//        }
//
//        PatientModel detailsModel = ((NewDemographicsActivity) getActivity()).getDetailsDTO();
//        if (detailsModel != null) {
//            demographicPayloadDTO.setPersonalDetails(detailsModel);
//        }
//
//        DemographicIdDocPayloadDTO idDocPojo = ((NewDemographicsActivity) getActivity()).getIdDocModel();
//        if (idDocPojo != null && idDocPojo.getIdType()!=null) { // add the doc
//            demographicPayloadDTO.setIdDocument(idDocPojo);
//        }
//
//        List<DemographicInsurancePayloadDTO> insuranceModelList = ((NewDemographicsActivity) getActivity()).getInsuranceModelList();
//        if (insuranceModelList != null) {
//            demographicPayloadDTO.setInsurances(insuranceModelList);
//        }
//
//        Map<String, String> queries = new HashMap<>();
//
//        // dynamic transition
//        Gson gson = new Gson();
//        String body = gson.toJson(demographicPayloadDTO);
//        TransitionDTO transitionDTO = demographicDTO.getMetadata().getTransitions().getConfirmDemographics();
//
//        getWorkflowServiceHelper().execute(transitionDTO, confirmDemWorkflowCallback, body,queries,getWorkflowServiceHelper().getPreferredLanguageHeader());

    }
}
