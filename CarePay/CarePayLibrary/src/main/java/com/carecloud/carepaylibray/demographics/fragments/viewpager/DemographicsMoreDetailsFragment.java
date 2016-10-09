package com.carecloud.carepaylibray.demographics.fragments.viewpager;

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

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.demographics.activities.DemographicsActivity;
import com.carecloud.carepaylibray.demographics.models.DemAddressPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemInsurancePayloadPojo;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemIdDocPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemPersDetailsPayloadDto;
import com.carecloud.carepaylibray.demographics.models.DemUpdateDto;
import com.carecloud.carepaylibray.demographics.services.DemographicService;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;

/**
 * Created by lsoco_user on 9/2/2016.
 * Screen for demographics onboarding confirmation
 */
public class DemographicsMoreDetailsFragment extends Fragment implements View.OnClickListener {
    View view;
    String[] getUpdateItemList;
    Button gotoCarePay;


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
                DemographicModel model = ((DemographicsActivity)getActivity()).getModel();
                confirmDemographicInformation(); // post the updates
                /*
                Intent appointmentIntent = new Intent(getActivity(), AppointmentsActivity.class);
                startActivity(appointmentIntent);
                getActivity().finish();*/
//                Log.v(LOG_TAG, "demogr_check_point");
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
        // TODO: 9/29/2016 add progress

        // TODO: 10/9/2016 remove
//        DemAddressPayloadDto demographicPayloadAddressModel = new DemAddressPayloadDto();
//        demographicPayloadAddressModel.setAddress1("5200 Blue legun dr");
//        demographicPayloadAddressModel.setAddress2("#800 Lejeune");
//        demographicPayloadAddressModel.setCity("Miami");
//        demographicPayloadAddressModel.setState("FL");
//        demographicPayloadAddressModel.setZipcode("33127");
//        demographicPayloadAddressModel.setPhone("18007654222");
//
//        DemPersDetailsPayloadDto demographicPayloadPersonalDetailsModel = new DemPersDetailsPayloadDto();
//        demographicPayloadPersonalDetailsModel.setFirstName("Jahirul");
//        demographicPayloadPersonalDetailsModel.setMiddleName("I");
//        demographicPayloadPersonalDetailsModel.setLastName("Bhuiyan");
//        demographicPayloadPersonalDetailsModel.setDateOfBirth("02/11/1983");
//        demographicPayloadPersonalDetailsModel.setPrimaryRace("Asian");
//        demographicPayloadPersonalDetailsModel.setEthnicity("White");
//        demographicPayloadPersonalDetailsModel.setPreferredLanguage("English");
//
//        DemInsurancePayloadPojo demographicPayloadInsuranceModel = new DemInsurancePayloadPojo();
//        demographicPayloadInsuranceModel.setInsuranceMemberId("2513515464");
//        demographicPayloadInsuranceModel.setInsurancePlan("Aetna");
//        demographicPayloadInsuranceModel.setInsuranceProvider("Aetna Select");
//        List<DemInsurancePayloadPojo> insurances = new ArrayList<>();
//        insurances.add(demographicPayloadInsuranceModel);
//        // second card
//        DemInsurancePayloadPojo demographicPayloadInsuranceModel2 = new DemInsurancePayloadPojo();
//        demographicPayloadInsuranceModel2.setInsuranceMemberId("999999999999");
//        demographicPayloadInsuranceModel2.setInsurancePlan("Elect Choice EPO");
//        demographicPayloadInsuranceModel2.setInsuranceProvider("BlueCross Blue Shield");
//        insurances.add(demographicPayloadInsuranceModel2);
//        // third card
//        DemInsurancePayloadPojo demographicPayloadInsuranceModel3 = new DemInsurancePayloadPojo();
//        demographicPayloadInsuranceModel3.setInsuranceMemberId("4444444444");
//        demographicPayloadInsuranceModel3.setInsurancePlan("Aetna Value Network HMO");
//        demographicPayloadInsuranceModel3.setInsuranceProvider("GHI");
//        insurances.add(demographicPayloadInsuranceModel3);

        DemPayloadDto demPayloadDto = new DemPayloadDto();
//        demPayloadDto.setAddress(demographicPayloadAddressModel);
//        demPayloadDto.setPersonalDetails(demographicPayloadPersonalDetailsModel);
//        demPayloadDto.setIdDocument(demographicPayloadDriversLicenseModel);
//        demPayloadDto.setInsurances(insurances);

        List<DemUpdateDto> updates = new ArrayList<>();
        demPayloadDto.setUpdates(updates);

        /*DemographicModel demographicPostModel = new DemographicModel();
        demographicPostModel.setPayload(demPayloadDto);*/

        // TODO: 9/29/2016 progress

        // obtain the updated models from the pager fragments
        DemAddressPayloadDto addressModel = ((DemographicsActivity)getActivity()).getAddressModel();
        if(addressModel != null) {
            demPayloadDto.setAddress(addressModel);
        }

        DemPersDetailsPayloadDto detailsModel = ((DemographicsActivity)getActivity()).getDetailsModel();
        if(detailsModel != null) {
            demPayloadDto.setPersonalDetails(detailsModel);
        }

        DemIdDocPayloadDto idDocPojo = ((DemographicsActivity)getActivity()).getDemPayloadIdDocPojo();
        if(idDocPojo != null) {
            demPayloadDto.setIdDocument(idDocPojo);
        }

        List<DemInsurancePayloadPojo> insuranceModelList = ((DemographicsActivity)getActivity()).getInsuranceModelList();
        if(insuranceModelList != null) {
            demPayloadDto.setInsurances(insuranceModelList);
        }

        // TODO: 10/9/2016 uncomment (just testing now)
//        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
//        Call<ResponseBody> call = apptService.confirmDemographicInformation(demPayloadDto);
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                // TODO: 9/29/2016 progress
//                Log.d(LOG_TAG, "demogr post succeeded");
//
//                Intent appointmentIntent = new Intent(getActivity(), AppointmentsActivity.class);
//                startActivity(appointmentIntent);
//                getActivity().finish();
//            }
//
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                Log.d(LOG_TAG, "demogr post failed", t);
//                // TODO: 9/29/2016 progres
//            }
//        });
    }
}
