package com.carecloud.carepay.practice.library.payments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.base.BasePracticeActivity;
import com.carecloud.carepay.practice.library.checkin.filters.FilterDataDTO;
import com.carecloud.carepay.practice.library.customcomponent.TwoColumnPatientListView;
import com.carecloud.carepay.practice.library.customdialog.DateRangePickerDialog;
import com.carecloud.carepay.practice.library.customdialog.FilterDialog;
import com.carecloud.carepay.practice.library.models.FilterModel;
import com.carecloud.carepay.practice.library.payments.dialogs.FindPatientDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticeModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModePaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RefundDetailFragment;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.practice.library.payments.interfaces.ShamrockPaymentsCallback;
import com.carecloud.carepay.practice.library.util.PracticeUtil;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.common.ConfirmationCallback;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanCreateInterface;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentPayload;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
import com.carecloud.carepaylibray.payments.viewModel.PatientResponsibilityViewModel;
import com.carecloud.carepaylibray.utils.DateUtil;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.MixPanelUtil;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;
import com.squareup.timessquare.CalendarPickerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PaymentsActivity extends BasePracticeActivity implements FilterDialog.FilterDialogListener,
        PracticePaymentNavigationCallback, DateRangePickerDialog.DateRangePickerDialogListener,
        ShamrockPaymentsCallback, PaymentPlanEditInterface, PaymentPlanCreateInterface, SwipeRefreshLayout.OnRefreshListener {

    private PaymentsModel paymentsModel;
    private FilterModel filterModel;

    private Date startDate;
    private Date endDate;
    private boolean isFragmentAdded = false;
    private PaymentHistoryItem recentRefundItem;
    private String patientId;
    private boolean paymentMethodCancelled = false;
    private View filterTextView;
    private View filterTextViewOn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PatientResponsibilityViewModel patientResponsibilityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_practice_payment);

        filterModel = new FilterModel();
        paymentsModel = getConvertedDTO(PaymentsModel.class);
        // initialized payment Model for all fragments to Observe
        patientResponsibilityViewModel = new ViewModelProvider(this).get(PatientResponsibilityViewModel.class);
        patientResponsibilityViewModel.setPaymentsModel(paymentsModel);

        populateList();
        setUpUI();
    }

    private void setUpUI() {
        findViewById(R.id.practice_payment_find_patient).setOnClickListener(onFindPatientClick());
        findViewById(R.id.goBackTextview).setOnClickListener(view -> onBackPressed());
        findViewById(R.id.change_date_range).setOnClickListener(selectDateRange);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        setUpFilter();
    }

    private void setUpFilter() {
        filterTextView = findViewById(R.id.filterTextView);
        filterTextView.setOnClickListener(onFilterIconClick());
        filterTextViewOn = findViewById(R.id.filterTextViewOn);
        filterTextViewOn.setOnClickListener(onFilterIconClick());
        if (filterModel.areThereActiveFilters()) {
            filterTextViewOn.setVisibility(View.VISIBLE);
        } else {
            filterTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPostResume() {
        super.onPostResume();
        if (recentRefundItem != null) {
            if (recentRefundItem.getPayload() != null) {
                completeRefundProcess(recentRefundItem, paymentsModel);
            } else {
                displayPendingTransactionDialog(true);
            }
        }
        recentRefundItem = null;
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null) {

            List<PatientBalanceDTO> patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> providersSavedFilteredIds = getApplicationPreferences().getSelectedProvidersIds(practiceId, userId);
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);
            filterModel.setDoctors(addProviderOnProviderFilterList(paymentsModel, providersSavedFilteredIds));
            filterModel.setLocations(addLocationOnFilterList(paymentsModel, locationsSavedFilteredIds));
            filterModel.setPatients(addPatientOnFilterList(patientBalancesList));

            initializePatientListView();

            setTextViewById(R.id.practice_payment_in_office_count,
                    String.format(Locale.getDefault(), "%1s", patientBalancesList.size()));
        }


    }

    private void initializePatientListView() {
        TwoColumnPatientListView patientListView = findViewById(R.id.list_patients);
        patientListView.setPaymentsModel(paymentsModel);
        patientListView.setCallback(dto -> {
            if (!isFragmentAdded && getSupportFragmentManager().getBackStackEntryCount() < 1) {
                PatientBalanceDTO balancessDTO = (PatientBalanceDTO) dto;
                PatientModel patient = new PatientModel();
                patient.setPatientId(balancessDTO.getBalances().get(0).getMetadata().getPatientId());
                patientId = patient.getPatientId();
                getPatientBalanceDetails(patientId);
            }

        });
        applyFilter();
    }

    private void updateDateLabel() {
        if (null != startDate && null != endDate) {
            String practiceCountLabel = DateUtil.getFormattedDate(
                    startDate,
                    endDate,
                    Label.getLabel("today_label"),
                    Label.getLabel("tomorrow_label"),
                    Label.getLabel("this_month_label"),
                    Label.getLabel("next_days_label"),
                    true).toUpperCase(Locale.getDefault());
            setTextViewById(R.id.practice_payment_in_office_label, practiceCountLabel);
        }
    }

    private ArrayList<FilterDataDTO> addProviderOnProviderFilterList(PaymentsModel paymentsModel,
                                                                     Set<String> selectedProvidersIds) {
        ArrayList<FilterDataDTO> doctors = new ArrayList<>();
        for (ProviderDTO provider : paymentsModel.getPaymentPayload().getProviders()) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(provider.getId(), provider.getName(),
                    FilterDataDTO.FilterDataType.PROVIDER);
            if (doctors.indexOf(filterDataDTO) < 0) {
                if ((selectedProvidersIds != null) && selectedProvidersIds.contains(String.valueOf(provider.getId()))) {
                    filterDataDTO.setChecked(true);
                }
                doctors.add(filterDataDTO);
            }
        }

        return doctors;
    }

    private ArrayList<FilterDataDTO> addLocationOnFilterList(PaymentsModel paymentsModel,
                                                             Set<String> locationsSavedFilteredIds) {
        ArrayList<FilterDataDTO> locations = new ArrayList<>();

        for (LocationDTO location : paymentsModel.getPaymentPayload().getLocations()) {
            FilterDataDTO filterDataDTO = new FilterDataDTO(location.getId(), location.getName(),
                    FilterDataDTO.FilterDataType.LOCATION);
            if (locations.indexOf(filterDataDTO) < 0) {
                if ((locationsSavedFilteredIds != null) && locationsSavedFilteredIds
                        .contains(String.valueOf(location.getId()))) {
                    filterDataDTO.setChecked(true);
                }
                locations.add(filterDataDTO);
            }
        }

        return locations;
    }

    private ArrayList<FilterDataDTO> addPatientOnFilterList(List<PatientBalanceDTO> balances) {
        ArrayList<FilterDataDTO> patients = new ArrayList<>();
        Map<String, String> photoMap = PracticeUtil.getProfilePhotoMap(paymentsModel
                .getPaymentPayload().getPatientBalances());

        for (PatientBalanceDTO patientBalances : balances) {

            PatientModel patientModel = patientBalances.getDemographics().getPayload().getPersonalDetails();
            String patientId = getPatientId(patientBalances);
            FilterDataDTO filterDataDTO = new FilterDataDTO(patientId, patientModel.getFullName(),
                    FilterDataDTO.FilterDataType.PATIENT);

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
        return view -> {
            FilterDialog filterDialog = new FilterDialog(PaymentsActivity.this,
                    findViewById(R.id.activity_practice_payment), filterModel
            );

            filterDialog.showPopWindow();
        };
    }

    private View.OnClickListener onFindPatientClick() {
        return view -> {
            TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata()
                    .getPaymentsLinks().getFindPatient();
            FindPatientDialog findPatientDialog = FindPatientDialog.newInstance(transitionDTO,
                    Label.getLabel("practice_payments_filter_find_patient_by_name"));
            findPatientDialog.setListener(patient -> {
                patientId = patient.getPatientId();
                getPatientBalanceDetails(patientId);
            });
            displayDialogFragment(findPatientDialog, false);
        };
    }

    private void getPatientBalanceDetails(String patientId) {
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("patient_id", patientId);

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getPaymentsPatientBalances();
        getWorkflowServiceHelper().execute(transitionDTO, patientBalancesCallback, queryMap);

    }

    private WorkflowServiceCallback patientBalancesCallback = new WorkflowServiceCallback() {

        @Override
        public void onPreExecute() {
            isFragmentAdded = true;
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
            isFragmentAdded = false;
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = findViewById(R.id.list_patients);
        patientListView.applyFilter(filterModel);
    }

    @Override
    public void refreshData() {
        onRangeSelected(startDate, endDate);
    }

    @Override
    public void showFilterFlag(boolean areThereActiveFilters) {
        filterTextView.setVisibility(areThereActiveFilters ? View.GONE : View.VISIBLE);
        filterTextViewOn.setVisibility(areThereActiveFilters ? View.VISIBLE : View.GONE);
    }


    @Override
    protected void processExternalPayment(PaymentExecution execution, Intent data) {
        switch (execution) {
            case clover: {
                boolean isRefund = data.getBooleanExtra(CarePayConstants.CLOVER_REFUND_INTENT_FLAG, false);
                final String jsonPayload = data.getStringExtra(CarePayConstants.CLOVER_PAYMENT_SUCCESS_INTENT_DATA);
                if (jsonPayload != null) {
                    if (isRefund) {
                        Log.d("Process Refund Success", jsonPayload);
                        PaymentHistoryItem historyItem = DtoHelper.getConvertedDTO(PaymentHistoryItem.class, jsonPayload);
                        if (isVisible()) {
                            completeRefundProcess(historyItem, paymentsModel);
                        } else {
                            recentRefundItem = historyItem;
                        }
                    } else {
                        Gson gson = new Gson();
                        WorkflowDTO workflowDTO = gson.fromJson(jsonPayload, WorkflowDTO.class);
                        showPaymentConfirmation(workflowDTO);
                    }

                }
                break;
            }
            default:
                //nothing
        }
    }

    @Override
    protected void processExternalPaymentFailure(PaymentExecution paymentExecution, int resultCode) {
        if (resultCode == CarePayConstants.PAYMENT_RETRY_PENDING_RESULT_CODE) {
            //Display a success notification and do some cleanup
            displayPendingTransactionDialog(false);
        } else if (resultCode == CarePayConstants.REFUND_RETRY_PENDING_RESULT_CODE) {
            if (isVisible()) {
                displayPendingTransactionDialog(true);
            } else {
                recentRefundItem = new PaymentHistoryItem();
                recentRefundItem.setPayload(null);
            }
        }
    }

    private void displayPendingTransactionDialog(boolean isRefund) {
        PaymentQueuedDialogFragment dialogFragment = PaymentQueuedDialogFragment.newInstance(isRefund);
        DialogInterface.OnDismissListener dismissListener = dialog
                -> hidePaymentDistributionFragment(new UpdatePatientBalancesDTO());
        dialogFragment.setOnDismissListener(dismissListener);
        displayDialogFragment(dialogFragment, false);
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
        TwoColumnPatientListView patientListView = findViewById(R.id.list_patients);
        patientListView.setPaymentsModel(paymentsModel);
        setTextViewById(R.id.practice_payment_in_office_count,
                String.format(Locale.getDefault(), "%1s", paymentsModel.getPaymentPayload()
                        .getPatientBalances().size()));

    }

    private boolean isObject(PatientBalanceDTO paymentsPatientBalance,
                             UpdatePatientBalancesDTO updateBalance) {
        return paymentsPatientBalance.getDemographics().getMetadata().getUserId()
                .equals(updateBalance.getDemographics().getMetadata().getUserId())
                && paymentsPatientBalance.getBalances().get(0).getMetadata().getPatientId()
                .equals(updateBalance.getBalances().get(0).getMetadata().getPatientId());
    }


    @Override
    public void startPaymentProcess(PaymentsModel paymentsModel) {
        patientResponsibilityViewModel.setPaymentsModel(paymentsModel);
        PaymentDistributionFragment fragment = PaymentDistributionFragment.newInstance(paymentsModel);
        displayDialogFragment(fragment, true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFragmentAdded = false;
            }
        }, 2000);

    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
//                .newInstance(paymentsModel, amount);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
//                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
//            DialogFragment fragment = PracticeChooseCreditCardFragment
//                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
//            displayDialogFragment(fragment, false);
//        } else {
//            showAddCard(amount, paymentsModel);
//        }

    }

    @Override
    public void navigateToFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

//    @Override
//    public void onPaymentPlanAmount(PaymentsModel paymentsModel,
//                                    PendingBalanceDTO selectedBalance,
//                                    double amount) {
//        //Not used anymore
//    }


    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment
//                .newInstance(paymentsModel, amount);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(PracticeModePaymentPlanEditFragment.class.getName());
        if (fragment instanceof PracticeModePaymentPlanEditFragment) {
            ((PracticeModePaymentPlanEditFragment) fragment).showDialog();
            ((PracticeModePaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
            return;
        }

        fragment = getSupportFragmentManager()
                .findFragmentByTag(PracticeModePaymentPlanFragment.class.getName());
        if (fragment instanceof PracticeModePaymentPlanFragment) {
            ((PracticeModePaymentPlanFragment) fragment).showDialog();
            ((PracticeModePaymentPlanFragment) fragment).replacePaymentMethod(creditCard);
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        Gson gson = new Gson();
        PaymentsModel paymentsModel = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
        UpdatePatientBalancesDTO updatePatientBalance = null;
        if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
            PatientBalanceDTO balance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
            String patientBalance = gson.toJson(balance);
            updatePatientBalance = gson.fromJson(patientBalance,
                    UpdatePatientBalancesDTO.class);
        }
        hidePaymentDistributionFragment(updatePatientBalance);
        onRangeSelected(new Date(), new Date());
    }

    private void hidePaymentDistributionFragment(UpdatePatientBalancesDTO updatePatientBalance) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.dismiss();
        }
        if (updatePatientBalance != null) {
            updatePatientBalance(updatePatientBalance);
        }
    }

    @Override
    public void onPayLaterClicked(PendingBalanceDTO pendingBalanceDTO) {

    }

    @Override
    public UserPracticeDTO getPracticeInfo(PaymentsModel paymentsModel) {
        if (paymentsModel != null && !paymentsModel.getPaymentPayload().getUserPractices().isEmpty()) {
            return paymentsModel.getPaymentPayload().getUserPractices().get(0);
        }
        return null;
    }

    @Override
    public void onPaymentCashFinished() {
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
    }

    @Nullable
    @Override
    public String getAppointmentId() {
        return null;
    }

    @Nullable
    @Override
    public AppointmentDTO getAppointment() {
        return null;
    }

    @Override
    public void onCashSelected(PaymentsModel paymentsModel) {
        //TODO handle this from practice mode
    }

    @Override
    public void completeRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel) {
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        RefundDetailFragment fragment = RefundDetailFragment.newInstance(historyItem, paymentsModel);
        displayDialogFragment(fragment, true);

    }

    private void popBackStackImmediate(String className) {
        getSupportFragmentManager().popBackStackImmediate(className, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private View.OnClickListener selectDateRange = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            DateRangePickerDialog dialog = DateRangePickerDialog.newInstance(
                    Label.getLabel("date_range_picker_dialog_title"),
                    Label.getLabel("date_range_picker_dialog_close"),
                    true,
                    startDate,
                    endDate,
                    DateRangePickerDialog.getPreviousSixMonthCalendar(),
                    DateRangePickerDialog.getNextSixMonthCalendar(),
                    PaymentsActivity.this,
                    CalendarPickerView.SelectionMode.RANGE.name());
            displayDialogFragment(dialog, false);
        }
    };

    @Override
    public void onRangeSelected(Date start, Date end) {
        updateList(start, end);
    }

    @Override
    public void onDateRangeCancelled() {
        //Not Implemented
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        //Not Implemented
    }

    private String getFormattedDate(Date date) {
        DateUtil dateUtil = DateUtil.getInstance();

        if (date == null) {
            dateUtil = dateUtil.setToCurrent();
        } else {
            dateUtil = dateUtil.setDate(date);
        }

        return dateUtil.toStringWithFormatYyyyDashMmDashDd();
    }

    private WorkflowServiceCallback getChangeDateCallback(final Date start, final Date end) {
        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {
                showProgressDialog();
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                if (swipeRefreshLayout.isRefreshing())
                    swipeRefreshLayout.setRefreshing(false);
                hideProgressDialog();
                startDate = start;
                endDate = end;

                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                updateDateLabel();
                populateList();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                swipeRefreshLayout.setRefreshing(false);
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();

        if (scheduledPayment.getMetadata().getOneTimePaymentId() == null &&
                !paymentsModel.getPaymentPayload().getScheduledOneTimePayments().isEmpty()) {
            scheduledPayment = paymentsModel.getPaymentPayload().getScheduledOneTimePayments().get(0);
        }

        List<ScheduledPaymentModel> scheduledPaymentModels = this.paymentsModel.getPaymentPayload()
                .getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId()
                    .equals(scheduledPayment.getMetadata().getOneTimePaymentId())) {
                scheduledPaymentModels.remove(scheduledPayment);
                break;
            }
        }
        scheduledPaymentModels.add(scheduledPayment);
        completePaymentProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().toStringWithFormatMmSlashDdSlashYyyy());
        showSuccessToast(message);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_payments_scheduled), 1);
    }

    @Override
    public void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO,
                                                       ScheduledPaymentPayload scheduledPaymentPayload) {
        showSuccessToast(String.format(
                Label.getLabel("payment.oneTimePayment.scheduled.delete.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPaymentPayload.getAmount()),
                DateUtil.getInstance()
                        .setDateRaw(scheduledPaymentPayload.getPaymentDate())
                        .toStringWithFormatMmSlashDdSlashYyyy()));
        completePaymentProcess(workflowDTO);

        MixPanelUtil.incrementPeopleProperty(getString(R.string.count_scheduled_payments_deleted), 1);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
        if (workflowDTO != null) {
            PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            IntegratedPatientPaymentPayload payload = paymentsModel.getPaymentPayload()
                    .getPatientPayments().getPayload();
            if (!payload.getProcessingErrors().isEmpty() && payload.getTotalPaid() == 0D) {
                StringBuilder builder = new StringBuilder();
                for (IntegratedPatientPaymentPayload.ProcessingError processingError : payload
                        .getProcessingErrors()) {
                    builder.append(processingError.getError());
                    builder.append("\n");
                }
                int last = builder.lastIndexOf("\n");
                builder.replace(last, builder.length(), "");
                new CustomMessageToast(this, builder.toString(),
                        CustomMessageToast.NOTIFICATION_TYPE_ERROR).show();
                onDismissPaymentMethodDialog(paymentsModel);
            } else {
                PaymentConfirmationFragment confirmationFragment = PaymentConfirmationFragment
                        .newInstance(workflowDTO, isOneTimePayment);
                displayDialogFragment(confirmationFragment, false);

                if (isOneTimePayment) {
                    MixPanelUtil.incrementPeopleProperty(getString(R.string.count_one_time_payments_completed), 1);
                }
            }
        }
    }

    private void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        if (paymentMethodCancelled) {
            paymentMethodCancelled = false;
            return;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        //TODO: Delete this when refactor. This code is not used anymore
//        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
//                .newInstance(paymentsModel, paymentPlanPostModel);
//        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {//no changes to plan
            popBackStackImmediate(PaymentDistributionFragment.class.getName());
            return;
        }
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onPaymentPlanCanceled(WorkflowDTO workflowDTO, boolean isDeleted) {
        String message = Label.getLabel("payment.cancelPaymentPlan.success.banner.text");
        if (isDeleted) {
            message = Label.getLabel("payment.deletePaymentPlan.success.banner.text");
        }
        showSuccessToast(message);
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        getPatientBalanceDetails(patientId);
    }

    @Override
    public void showCancelPaymentPlanConfirmDialog(ConfirmationCallback confirmationCallback, boolean isDeleted) {
        //TODO: Delete this when refactor. This code is not used anymore
//        String title = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.title");
//        String message = Label.getLabel("payment.cancelPaymentPlan.confirmation.popup.message");
//        if (isDeleted) {
//            title = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.title");
//            message = Label.getLabel("payment.deletePaymentPlan.confirmation.popup.message");
//        }
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ConfirmDialogFragment confirmDialogFragment = ConfirmDialogFragment.newInstance(title, message);
//        confirmDialogFragment.setCallback(confirmationCallback);
//        String tag = confirmDialogFragment.getClass().getName();
//        confirmDialogFragment.show(ft, tag);
    }

    @Override
    public DTO getDto() {
        return paymentsModel;
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        //nothing to do with the updated payment plan because this is not on the main screen
        popBackStackImmediate(PaymentDistributionFragment.class.getName());
        populateList();
    }

//    public void showPaymentPlanDetail(final PaymentsModel paymentsModel,
//                                      PaymentPlanDTO paymentPlan,
//                                      boolean isCompleted) {
//        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
//                .newInstance(paymentsModel, paymentPlan, isCompleted);
//        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
////                showPaymentPlanDashboard(paymentsModel);
//            }
//        });
//        displayDialogFragment(fragment, true);
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
    }

    @Override
    public void addFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        //NA
    }

    @Override
    public void onRefresh() {
        updateList(startDate, endDate);
    }

    private void updateList(Date start, Date end) {
        Map<String, String> queryMap = new HashMap<>();
        if (null != start)
            queryMap.put("start_date", getFormattedDate(start));
       // else
            //  queryMap.put("start_date", DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());
        if (null != end)
            queryMap.put("end_date", getFormattedDate(end));
      //  else
            //  queryMap.put("end_date",  DateUtil.getInstance().setToCurrent().toStringWithFormatYyyyDashMmDashDd());

        String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
        String userId = getApplicationMode().getUserPracticeDTO().getUserId();
        Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);

        if (locationsSavedFilteredIds != null && !locationsSavedFilteredIds.isEmpty()) {
            queryMap.put("location_ids", StringUtil.getListAsCommaDelimitedString(locationsSavedFilteredIds));
        }

        TransitionDTO transitionDTO = paymentsModel.getPaymentsMetadata().getPaymentsTransitions().getPracticePayments();
        getWorkflowServiceHelper().execute(transitionDTO, getChangeDateCallback(start, end), queryMap);
        updateDateLabel();
    }
}
