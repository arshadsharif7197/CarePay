package com.carecloud.carepay.practice.library.payments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

public class PaymentsActivity extends BasePracticeActivity implements FilterDialog.FilterDialogListener, PracticePaymentNavigationCallback {

    private PaymentsModel paymentsModel;
    private FilterModel filter;

    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_payment);

        filter = new FilterModel();
        paymentsModel = getConvertedDTO(PaymentsModel.class);

        setLabels();
        populateList();
    }

    private void setLabels() {
        practicePaymentsFilter = Label.getLabel("practice_payments_filter");
        practicePaymentsFilterFindPatientByName = Label.getLabel("practice_payments_filter_find_patient_by_name");
        practicePaymentsFilterClearFilters = Label.getLabel("practice_payments_filter_clear_filters");
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null
                && paymentsModel.getPaymentPayload().getPatientBalances().size() > 0) {

            List<PatientBalanceDTO> patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            filter.setDoctors(addProviderOnProviderFilterList(paymentsModel));
            filter.setLocations(addLocationOnFilterList(paymentsModel));
            filter.setPatients(addPatientOnFilterList(patientBalancesList));

            initializePatientListView();

            setTextViewById(R.id.practice_payment_in_office_count,
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
                PatientBalanceDTO balancessDTO = (PatientBalanceDTO) dto;
                PatientModel patient = new PatientModel();
                patient.setPatientId(balancessDTO.getBalances().get(0).getMetadata().getPatientId());

                getPatientBalanceDetails(patient);

//                showResponsibilityDialog((PatientBalanceDTO) dto);
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

    private ArrayList<FilterDataDTO> addPatientOnFilterList(List<PatientBalanceDTO> balances) {
        ArrayList<FilterDataDTO> patients = new ArrayList<>();
        Map<String, String> photoMap = PracticeUtil.getProfilePhotoMap(paymentsModel.getPaymentPayload().getPatientBalances());

        for (PatientBalanceDTO patientBalances : balances) {

            PatientModel patientModel = patientBalances.getDemographics().getPayload().getPersonalDetails();
            String patientId = getPatientId(patientBalances);
            FilterDataDTO filterDataDTO = new FilterDataDTO(patientId, patientModel.getFullName(), FilterDataDTO.FilterDataType.PATIENT);

            if (StringUtil.isNullOrEmpty(filterDataDTO.getImageURL())) {
                filterDataDTO.setImageURL(photoMap.get(filterDataDTO.getId()));
            }

            if (patients.indexOf(filterDataDTO) < 0) {
                patients.add(filterDataDTO);
            }

        }

        return patients;
    }

    private String getPatientId(PatientBalanceDTO patientBalances) {
        List<PendingBalanceDTO> balances = patientBalances.getBalances();
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
                        transitionDTO,
                        Label.getLabel("practice_payments_filter_find_patient_by_name"));
                setFindPatientOnItemClickedListener(findPatientDialog);
                findPatientDialog.show();
            }
        };
    }

    private void setFindPatientOnItemClickedListener(FindPatientDialog findPatientDialog) {
        findPatientDialog.setClickedListener(new FindPatientDialog.OnItemClickedListener() {
            @Override
            public void onItemClicked(PatientModel patient) {
                getPatientBalanceDetails(patient);
            }
        });
    }

    private void getPatientBalanceDetails(PatientModel patient) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patient.getPatientId());

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);

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
            if (patientDetails != null) {
                //need to add these as they do not return in this call
                patientDetails.getPaymentPayload().setLocations(paymentsModel.getPaymentPayload().getLocations());
                patientDetails.getPaymentPayload().setLocationIndex(paymentsModel.getPaymentPayload().getLocationIndex());
                patientDetails.getPaymentPayload().setProviders(paymentsModel.getPaymentPayload().getProviders());
                patientDetails.getPaymentPayload().setProviderIndex(paymentsModel.getPaymentPayload().getProviderIndex());
                startPaymentProcess(patientDetails);
            }

        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
        }
    };

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.applyFilter(filter);
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    Gson gson = new Gson();
                    PaymentsModel paymentsModel = gson.fromJson(jsonPayload, PaymentsModel.class);
                    showPaymentConfirmation(paymentsModel);


                }
                break;
            }
            default:
                //nothing
        }
    }


    private void updatePatientBalance(UpdatePatientBalancesDTO updateBalance) {
        ListIterator<PatientBalanceDTO> iterator = paymentsModel.getPaymentPayload().getPatientBalances().listIterator();
        while (iterator.hasNext()) {
            PatientBalanceDTO patientBalanceDTO = iterator.next();
            if (isObject(patientBalanceDTO, updateBalance)) {
                try {
                    double pendingResponsibility = Double.parseDouble(updateBalance.getPendingRepsonsibility());
                    if (pendingResponsibility == 0d) {
                        iterator.remove();
                    } else {
                        patientBalanceDTO.setPendingRepsonsibility(updateBalance.getPendingRepsonsibility());
                        patientBalanceDTO.setBalances(updateBalance.getBalances());
                    }
                    break;
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }

        //reload list data
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.setPaymentsModel(paymentsModel);

    }

    private boolean isObject(PatientBalanceDTO paymentsPatientBalance, UpdatePatientBalancesDTO updateBalance) {
        return paymentsPatientBalance.getDemographics().getMetadata().getUserId().equals(updateBalance.getDemographics().getMetadata().getUserId()) &&
                paymentsPatientBalance.getBalances().get(0).getMetadata().getPatientId().equals(updateBalance.getBalances().get(0).getMetadata().getPatientId());
    }


    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsModel));

        PaymentDistributionFragment fragment = new PaymentDistributionFragment();
        fragment.setArguments(args);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());

    }

    @Override
    public void startPartialPayment(double owedAmount) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod, double amount, PaymentsModel paymentsModel) {
//        boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment.newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
        } else {
            showAddCard(amount, paymentsModel);
        }

    }

    @Override
    public void onPaymentPlanAction() {
        PatientPaymentPlanFragment fragment = new PatientPaymentPlanFragment();

        Bundle args = new Bundle();
        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_CREDIT_CARD_INFO, paymentsDTOString);
        fragment.setArguments(args);

        //TODO show payment plan frag as dialog I think
    }

    @Override
    public void showPaymentConfirmation(PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);

        PaymentConfirmationFragment confirmationFragment = new PaymentConfirmationFragment();
        confirmationFragment.setArguments(args);
        confirmationFragment.show(getSupportFragmentManager(), confirmationFragment.getClass().getSimpleName());
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        Gson gson = new Gson();
        Bundle args = new Bundle();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, paymentsDTOString);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        DialogFragment fragment = new PracticeAddNewCreditCardFragment();
        fragment.setArguments(args);
//        navigateToFragment(fragment, true);
        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void completePaymentProcess(UpdatePatientBalancesDTO updatePatientBalancesDTO) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager.findFragmentByTag(PaymentDistributionFragment.class.getSimpleName());
        if(fragment!=null) {
            fragment.dismiss();
        }
        updatePatientBalance(updatePatientBalancesDTO);
    }

    @Override
    public void cancelPaymentProcess(PaymentsModel paymentsModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager.findFragmentByTag(PaymentDistributionFragment.class.getSimpleName());
        if(fragment!=null) {
            fragment.showDialog();
        }
    }

    @Override
    public void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems, AddPaymentItemFragment.AddItemCallback callback) {

        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(simpleChargeItems));

        AddPaymentItemFragment fragment = new AddPaymentItemFragment();
        fragment.setCallback(callback);
        if (simpleChargeItems != null && !simpleChargeItems.isEmpty()) {
            fragment.setArguments(args);
        }

        fragment.show(getSupportFragmentManager(), fragment.getClass().getSimpleName());
    }

    @Override
    public void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback, BalanceItemDTO balanceItem, SimpleChargeItem chargeItem) {
        PaymentDistributionEntryFragment entryFragment = new PaymentDistributionEntryFragment();
        if (balanceItem != null) {
            entryFragment.setBalanceItem(balanceItem);
        }
        if (chargeItem != null) {
            entryFragment.setChargeItem(chargeItem);
        }

        entryFragment.setCallback(callback);
        entryFragment.show(getSupportFragmentManager(), entryFragment.getClass().getSimpleName());
    }


}
