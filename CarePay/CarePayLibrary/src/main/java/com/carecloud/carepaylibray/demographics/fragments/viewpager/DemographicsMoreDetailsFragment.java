package com.carecloud.carepaylibray.demographics.fragments.viewpager;


import android.content.Context;
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

import com.carecloud.carepay.service.library.BaseServiceGenerator;
import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicAddressPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicInsurancePayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicIdDocPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPersDetailsPayloadDTO;
import com.carecloud.carepaylibray.demographics.services.DemographicService;

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
public class DemographicsMoreDetailsFragment extends Fragment implements View.OnClickListener {
    View view;
    String[] getUpdateItemList;
    Button gotoCarePay;
    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_demographics_moredetails, container, false);
        getUpdateItemList = getResources().getStringArray(R.array.UpdatesMode);
        setTypefaces(view);

        gotoCarePay = (Button) view.findViewById(R.id.demographicsGoToCarePayButton);
        onClick(view);

        return view;
    }

    @Override
    public void onClick(View view) {
        gotoCarePay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDemographicInformation(); // post the updates
            }
        });
    }

    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsHeading));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.moreDetailsSubHeading));
        setGothamRoundedMediumTypeface(getActivity(),(Button)view.findViewById(R.id.demographicsGoToCarePayButton));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.placeHolderIconTextViewId));
    }

    public void confirmDemographicInformation() {
        DemographicPayloadDTO demographicPayloadDTO = new DemographicPayloadDTO();

//        List<DemographicPayloadUpdateDTO> updates = new ArrayList<>();
//        demographicPayloadDTO.setUpdates(updates);

        // obtain the updated models from the pager fragments
        DemographicAddressPayloadDTO addressModel = ((DemographicsActivity)getActivity()).getAddressModel();
        if(addressModel != null) {
            demographicPayloadDTO.setAddress(addressModel);
        }

        DemographicPersDetailsPayloadDTO detailsModel = ((DemographicsActivity)getActivity()).getDetailsDTO();
        if(detailsModel != null) {
            demographicPayloadDTO.setPersonalDetails(detailsModel);
        }

        DemographicIdDocPayloadDTO idDocPojo = ((DemographicsActivity)getActivity()).getIdDocModel();
        if(idDocPojo != null) { // add the doc
            List<DemographicIdDocPayloadDTO> idDocPayloadDTOs = new ArrayList<>();
            idDocPojo.setIdCountry("USA"); // to remove
            idDocPayloadDTOs.add(idDocPojo);
            demographicPayloadDTO.setIdDocuments(idDocPayloadDTOs);
        }

        List<DemographicInsurancePayloadDTO> insuranceModelList = ((DemographicsActivity)getActivity()).getInsuranceModelList();
        if(insuranceModelList != null) {
            demographicPayloadDTO.setInsurances(insuranceModelList);
        }

        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<ResponseBody> call = apptService.confirmDemographicInformation(demographicPayloadDTO);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Intent appointmentIntent = new Intent(context, AppointmentsActivity.class);
                startActivity(appointmentIntent);
                getActivity().finish();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.d(LOG_TAG, "demogr post failed", throwable);
            }
        });
    }
}
