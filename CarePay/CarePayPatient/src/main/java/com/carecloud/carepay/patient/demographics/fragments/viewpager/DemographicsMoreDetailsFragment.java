package com.carecloud.carepay.patient.demographics.fragments.viewpager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.patient.demographics.activities.DemographicsActivity;
import com.carecloud.carepay.patient.demographics.services.DemographicService;
import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepay.patient.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.constants.CarePayConstants;
import com.carecloud.carepaylibray.demographics.dtos.metadata.labels.DemographicLabelsDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by lsoco_user on 9/2/2016.
 * Screen for demographics onboarding confirmation
 */
public class DemographicsMoreDetailsFragment extends Fragment {

    private View                 view;
    private Button               gotoCarePay;
    private DemographicLabelsDTO globalLabelsDTO;
    private TextView             header;
    private TextView             subheader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fetch the global labels
        globalLabelsDTO = ((DemographicsActivity)getActivity()).getLabelsDTO();

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

        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<ResponseBody> call = apptService.confirmDemographicInformation(demographicPayloadDTO);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent appointmentIntent = new Intent(getActivity(), AppointmentsActivity.class);
                startActivity(appointmentIntent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e(LOG_TAG, "Confirm demographics POST failed.", throwable);
            }
        });
    }
}
