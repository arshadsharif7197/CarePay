package com.carecloud.carepaylibray.demographics.fragments.review;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.carecloud.carepaylibrary.R;
import com.carecloud.carepaylibray.appointments.activities.AppointmentsActivity;
import com.carecloud.carepaylibray.base.BaseServiceGenerator;
import com.carecloud.carepaylibray.consentforms.interfaces.ConsentActivity;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInfoPayloadModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadInsuranceModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadPersonalDetailsModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadResponseModel;
import com.carecloud.carepaylibray.demographics.services.DemographicService;
import com.carecloud.carepaylibray.utils.SystemUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.carecloud.carepaylibray.keyboard.KeyboardHolderActivity.LOG_TAG;
import static com.carecloud.carepaylibray.utils.SystemUtil.setGothamRoundedMediumTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaExtraboldTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaRegularTypeface;
import static com.carecloud.carepaylibray.utils.SystemUtil.setProximaNovaSemiboldTypeface;

public class ReviewFragment  extends Fragment implements View.OnClickListener {

    View view;
    Button testButton;
    TextView raceTextView, ethnicityTextView, companyTextView,planTextView,policyNumberTextView;
  DemographicPayloadResponseModel demographicPayloadResponseModel;
    ProgressBar demographicProgressBar;


    public static ReviewFragment newInstance() {
        return new ReviewFragment();
    }

    public ReviewFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_insurance_review, container, false);
        demographicProgressBar= (ProgressBar) view.findViewById(R.id.demographicReviewProgressBar);
        ethnicityTextView=(TextView)view.findViewById(R.id.reviewEthnicityTextView);
        raceTextView=(TextView)view.findViewById(R.id.reviewRaceTextView);
        planTextView=(TextView)view.findViewById(R.id.reviewPlanTextView);
        policyNumberTextView=(TextView)view.findViewById(R.id.reviewInsurancePolicyNoTextView);


        Toolbar toolbar = (Toolbar) view.findViewById(R.id.review_toolbar);
        TextView title = (TextView) toolbar.findViewById(R.id.review_toolbar_title);
        SystemUtil.setGothamRoundedMediumTypeface(getActivity(), title);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_patient_mode_nav_back));
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent = new Intent(getContext(), AppointmentsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                getActivity().finish();
            }
        });

        getDemographicInformation();
        initialiseUIFields();
        setTypefaces(view);
        return view;
    }
   private void getDemographicInformation() {
        demographicProgressBar.setVisibility(View.VISIBLE);
        DemographicService apptService = (new BaseServiceGenerator(getActivity())).createService(DemographicService.class); //, String token, String searchString
        Call<DemographicModel> call = apptService.fetchDemographics();
        call.enqueue(new Callback<DemographicModel>() {
            @Override
            public void onResponse(Call<DemographicModel> call, Response<DemographicModel> response) {
                DemographicModel demographicModel=response.body();
                if(demographicModel!=null) {

                    demographicPayloadResponseModel = demographicModel.getPayload();
                    demographicProgressBar.setVisibility(View.GONE);
                    Log.d("sdadad", "adasdasdasd");
                    if (demographicPayloadResponseModel != null) {
                        DemographicPayloadInfoModel demographics=demographicPayloadResponseModel.getDemographics();

                        if(demographics!=null) {
                            DemographicPayloadInfoPayloadModel payloadinfomodel=demographics.getPayload();

                            if (payloadinfomodel.getPersonalDetails() != null) {
                                DemographicPayloadPersonalDetailsModel demographicPayloadPersonalDetailsModel=payloadinfomodel.getPersonalDetails();
                                raceTextView.setText( demographicPayloadPersonalDetailsModel.getPrimaryRace());
                                ethnicityTextView.setText(demographicPayloadPersonalDetailsModel.getEthnicity());
                            } else
                                Log.v(LOG_TAG, "demographic personaldetail  model is null ");
                            if (payloadinfomodel.getInsurances() != null) {
                                for (DemographicPayloadInsuranceModel demographicPayloadInsuranceModel:demographics.getPayload().getInsurances()) {
                                    planTextView.setText( demographicPayloadInsuranceModel.getInsurancePlan());
                                    companyTextView.setText(demographicPayloadInsuranceModel.getInsuranceProvider());
                                    policyNumberTextView.setText(demographicPayloadInsuranceModel.getInsuranceMemberId());
                                }
                            } else
                                Log.v(LOG_TAG, "demographic insurance model is null");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<DemographicModel> call, Throwable t) {
            }
        });
    }

    private void initialiseUIFields() {

        testButton = (Button) view.findViewById(R.id.YesCorrectButton);
        testButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view ==testButton){
            Intent intent = new Intent(getActivity(), ConsentActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         if (item.getItemId()==android.R.id.home) {
            getActivity().onBackPressed();
                return true;
         }
                return false;
        }


    private void setTypefaces(View view) {
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewtitle));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewSubtitle));

        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.demographicSubTitle));
        setProximaNovaSemiboldTypeface(getActivity(), (TextView) view.findViewById(R.id.healthInsuranceSubTitle));


        //PN - extra Bold

        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanLabel));
        setProximaNovaExtraboldTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsuranceCardNoLabel));


        //PN-Regular
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewRaceTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewEthnicityTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewCompanyTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewPlanTextView));
        setProximaNovaRegularTypeface(getActivity(), (TextView) view.findViewById(R.id.reviewInsurancePolicyNoTextView));



        //GNRM
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.YesCorrectButton));
        setGothamRoundedMediumTypeface(getActivity(), (TextView) view.findViewById(R.id.needUpdateButton));

    }

}
