package com.carecloud.carepay.practice.library.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.ResponsibilityDialog;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.WorkflowServiceHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepaylibray.demographicsettings.models.DemographicsSettingsPersonalDetailsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.LocationDTO;
import com.carecloud.carepaylibray.payments.models.PatienceBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PatientDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PaymentsPatientBalancessDTO;
import com.carecloud.carepaylibray.payments.models.ProviderDTO;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PaymentsActivity extends BasePracticeActivity implements FilterDialog.FilterCallBack {

    private PaymentsLabelDTO paymentsLabel;
    private PaymentsModel paymentsModel;
    private FilterModel filter;

    private String practiceCheckinFilterDoctorsLabel;
    private String practiceCheckinFilterLocationsLabel;
    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_payment);
        setNavigationBarVisibility();

        filter = new FilterModel();
        paymentsModel = getConvertedDTO(PaymentsModel.class);

        setLabels();
        populateList();
    }

    private void setLabels() {
        if (paymentsModel != null) {
            paymentsLabel = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
            if (paymentsLabel != null) {

                setViewTextById(R.id.practice_payment_title, paymentsLabel.getPracticePaymentsHeader());
                setViewTextById(R.id.practice_payment_go_back, paymentsLabel.getPracticePaymentsBackLabel());
                setViewTextById(R.id.practice_payment_find_patient, paymentsLabel.getPracticePaymentsFindPatientLabel());
                setViewTextById(R.id.practice_payment_filter_label, paymentsLabel.getPracticePaymentsFilter());
                setViewTextById(R.id.practice_payment_in_office_label, paymentsLabel.getPracticePaymentsInOffice());

                practiceCheckinFilterDoctorsLabel = paymentsLabel.getPracticePaymentsFilterDoctors();
                practiceCheckinFilterLocationsLabel = paymentsLabel.getPracticePaymentsFilterLocations();
                practicePaymentsFilter = paymentsLabel.getPracticePaymentsFilter();
                practicePaymentsFilterFindPatientByName = paymentsLabel.getPracticePaymentsFilterFindPatientByName();
                practicePaymentsFilterClearFilters = paymentsLabel.getPracticePaymentsFilterClearFilters();
            }
        }
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null
                && paymentsModel.getPaymentPayload().getPatientBalances().size() > 0) {

            List<PaymentsPatientBalancessDTO> patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            filter.setDoctors(addProviderOnProviderFilterList(paymentsModel));
            filter.setLocations(addLocationOnFilterList(paymentsModel));
            filter.setPatients(addPatientOnFilterList(patientBalancesList));

            initializePatientListView();

            setViewTextById(R.id.practice_payment_in_office_count,
                    String.format(Locale.getDefault(), "%1s", patientBalancesList.size()));
        }

        findViewById(R.id.practice_payment_find_patient).setOnClickListener(onFindPatientClick());
        findViewById(R.id.practice_payment_filter_label).setOnClickListener(onFilterIconClick());
        findViewById(R.id.practice_payment_go_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void initializePatientListView() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setPaymentsModel(paymentsModel);
        patientListView.setCallback(new TwoColumnPatientListView.TwoColumnPatientListViewListener() {
            @Override
            public void onPatientTapped(Object dto) {
                PaymentsPatientBalancessDTO paymentsPatientBalancessDTO = (PaymentsPatientBalancessDTO) dto;

                ResponsibilityDialog responsibilityDialog = new ResponsibilityDialog(getContext(), paymentsModel, paymentsPatientBalancessDTO);
                responsibilityDialog.show();
            }
        });
    }

    private ArrayList<FilterDataDTO> addProviderOnProviderFilterList(PaymentsModel paymentsModel) {
        ArrayList<FilterDataDTO> doctors = new ArrayList<>();

        for (ProviderDTO provider : paymentsModel.getPaymentPayload().getProviders()) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(provider.getId(), provider.getName(), FilterDataDTO.FilterDataType.PROVIDER);
            if (doctors.indexOf(filterDataDTO) < 0) {
                doctors.add(filterDataDTO);
            }
        }

        return doctors;
    }

    private ArrayList<FilterDataDTO> addLocationOnFilterList(PaymentsModel paymentsModel) {
        ArrayList<FilterDataDTO> locations = new ArrayList<>();

        for (LocationDTO location : paymentsModel.getPaymentPayload().getLocations()) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(location.getId(), location.getName(), FilterDataDTO.FilterDataType.LOCATION);
            if (locations.indexOf(filterDataDTO) < 0) {
                locations.add(filterDataDTO);
            }
        }

        return locations;
    }

    private ArrayList<FilterDataDTO> addPatientOnFilterList(List<PaymentsPatientBalancessDTO> balances) {
        ArrayList<FilterDataDTO> patients = new ArrayList<>();

        for (PaymentsPatientBalancessDTO patientBalances : balances) {

            DemographicsSettingsPersonalDetailsPayloadDTO personalDetails
                    = patientBalances.getDemographics().getPayload().getPersonalDetails();
            String fullName = personalDetails.getFirstName() + " " + personalDetails.getLastName();
            String patientId = getPatientId(patientBalances);
            FilterDataDTO filterDataDTO = new FilterDataDTO(patientId, fullName, FilterDataDTO.FilterDataType.PATIENT);

            if (patients.indexOf(filterDataDTO) < 0) {
                patients.add(filterDataDTO);
            }
        }

        return patients;
    }

    private String getPatientId(PaymentsPatientBalancessDTO patientBalances) {
        List<PatienceBalanceDTO> balances = patientBalances.getBalances();
        if (balances.isEmpty()) {
            return null;
        }

        return balances.get(0).getMetadata().getPatientId();
    }

    @NonNull
    private View.OnClickListener onFilterIconClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FilterDialog filterDialog = new FilterDialog(PaymentsActivity.this,
                        findViewById(R.id.activity_practice_payment), filter,
                        practiceCheckinFilterDoctorsLabel, practiceCheckinFilterLocationsLabel,
                        practicePaymentsFilter, practicePaymentsFilterFindPatientByName,
                        practicePaymentsFilterClearFilters);

                filterDialog.showPopWindow();
            }
        };
    }

    private View.OnClickListener onFindPatientClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getFindPatient();

                FindPatientDialog findPatientDialog = new FindPatientDialog(PaymentsActivity.this,
                        transitionDTO, paymentsLabel.getPracticePaymentsDetailDialogCloseButton(),
                        paymentsLabel.getPracticePaymentsFindPatientLabel());
                setOnItemClickedListener(findPatientDialog);
                findPatientDialog.show();
            }
        };
    }

    private void setOnItemClickedListener(FindPatientDialog findPatientDialog) {
        findPatientDialog.setClickedListener(new FindPatientDialog.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientDTO patient) {
                Map<String, String> queryMap = new HashMap<>();
                queryMap.put("practice_mgmt", getApplicationMode().getUserPracticeDTO().getPracticeMgmt());
                queryMap.put("practice_id", getApplicationMode().getUserPracticeDTO().getPracticeId());
                queryMap.put("patient_id", patient.getPatientId());

                TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
                getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);
            }
        });
    }

    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();

            PaymentsModel patientDetails = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO.toString());

            if (patientDetails != null && patientDetails.getPaymentPayload().getPatientBalances() != null && !patientDetails.getPaymentPayload().getPatientBalances().isEmpty()) {
                PaymentsPatientBalancessDTO paymentsPatientBalancessDTO = patientDetails.getPaymentPayload().getPatientBalances().get(0);

                ResponsibilityDialog responsibilityDialog = new ResponsibilityDialog(getContext(), paymentsModel, paymentsPatientBalancessDTO);
                responsibilityDialog.show();
            }
            else
            {
                Toast.makeText(getContext(), "Patient has no balance", Toast.LENGTH_LONG).show() ;
            }
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
        }
    };

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.applyFilter(filter);
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data){
        switch (execution){
            case clover:{
                //TODO get the updated Patient Object and look throught the original list to update balance or remove
                break;
            }
            default:
                //nothing
                return;
        }
    }

}
