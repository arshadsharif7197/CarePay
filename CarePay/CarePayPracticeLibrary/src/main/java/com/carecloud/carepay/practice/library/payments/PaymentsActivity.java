package com.carecloud.carepay.practice.library.payments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.carecloud.carepay.practice.library.payments.dialogs.IntegratedPaymentsChooseDeviceFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentDetailsFragmentDialog;
import com.carecloud.carepay.practice.library.payments.dialogs.PaymentQueuedDialogFragment;
import com.carecloud.carepay.practice.library.payments.dialogs.PracticePaymentPlanDetailsDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.AddPaymentItemFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PatientModePaymentPlanEditFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionEntryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentDistributionFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentHistoryFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PaymentPlanDashboardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeAddNewCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeEditOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModeAddToExistingPaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeModePaymentPlanFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeOneTimePaymentFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentHistoryDetailFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentMethodDialogFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanAddCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanChooseCreditCardFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanConfirmationFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanListFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanPaymentMethodFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticePaymentPlanTermsFragment;
import com.carecloud.carepay.practice.library.payments.fragments.PracticeValidPlansFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RefundDetailFragment;
import com.carecloud.carepay.practice.library.payments.fragments.RefundProcessFragment;
import com.carecloud.carepay.practice.library.payments.interfaces.PaymentPlanDashboardInterface;
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
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.PatientModel;
import com.carecloud.carepaylibray.customcomponents.CustomMessageToast;
import com.carecloud.carepaylibray.customdialogs.LargeAlertDialog;
import com.carecloud.carepaylibray.interfaces.DTO;
import com.carecloud.carepaylibray.payments.fragments.PaymentConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanConfirmationFragment;
import com.carecloud.carepaylibray.payments.fragments.PaymentPlanEditFragment;
import com.carecloud.carepaylibray.payments.interfaces.PaymentPlanEditInterface;
import com.carecloud.carepaylibray.payments.models.IntegratedPatientPaymentPayload;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentCreditCardsPayloadDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsMethodsDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ScheduledPaymentModel;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.history.PaymentHistoryItem;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentExecution;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanPostModel;
import com.carecloud.carepaylibray.payments.models.updatebalance.UpdatePatientBalancesDTO;
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
        ShamrockPaymentsCallback, PaymentPlanEditInterface, PaymentPlanDashboardInterface {

    private PaymentsModel paymentsModel;
    private FilterModel filter;

    private String practicePaymentsFilter;
    private String practicePaymentsFilterFindPatientByName;
    private String practicePaymentsFilterClearFilters;

    private Date startDate;
    private Date endDate;

    private PaymentHistoryItem recentRefundItem;
    private boolean paymentMethodCancelled = false;

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

    private void setLabels() {
        practicePaymentsFilter = Label.getLabel("practice_payments_filter");
        practicePaymentsFilterFindPatientByName = Label.getLabel("practice_payments_filter_find_patient_by_name");
        practicePaymentsFilterClearFilters = Label.getLabel("practice_payments_filter_clear_filters");
    }

    private void populateList() {
        if (paymentsModel != null && paymentsModel.getPaymentPayload().getPatientBalances() != null) {

            List<PatientBalanceDTO> patientBalancesList = paymentsModel.getPaymentPayload().getPatientBalances();

            String practiceId = getApplicationMode().getUserPracticeDTO().getPracticeId();
            String userId = getApplicationMode().getUserPracticeDTO().getUserId();
            Set<String> providersSavedFilteredIds = getApplicationPreferences().getSelectedProvidersIds(practiceId, userId);
            Set<String> locationsSavedFilteredIds = getApplicationPreferences().getSelectedLocationsIds(practiceId, userId);
            filter.setDoctors(addProviderOnProviderFilterList(paymentsModel, providersSavedFilteredIds));
            filter.setLocations(addLocationOnFilterList(paymentsModel, locationsSavedFilteredIds));
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
        findViewById(R.id.change_date_range).setOnClickListener(selectDateRange);
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
            showErrorNotification(exceptionMessage);
        }
    };

    @Override
    public void applyFilter() {
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
        patientListView.applyFilter(filter);
    }

    @Override
    public void refreshData() {
        onRangeSelected(startDate, endDate);
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
        DialogInterface.OnDismissListener dismissListener = new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                hidePaymentDistributionFragment(new UpdatePatientBalancesDTO());
            }
        };
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
        TwoColumnPatientListView patientListView = (TwoColumnPatientListView) findViewById(R.id.list_patients);
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
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(paymentsModel));
        PaymentDistributionFragment fragment = new PaymentDistributionFragment();
        fragment.setArguments(args);
        displayDialogFragment(fragment, true);

    }

    @Override
    public void onPartialPaymentClicked(double owedAmount, PendingBalanceDTO selectedBalance) {

    }

    @Override
    public void onPayButtonClicked(double amount, PaymentsModel paymentsModel) {
        PracticePaymentMethodDialogFragment fragment = PracticePaymentMethodDialogFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentMethodAction(PaymentsMethodsDTO selectedPaymentMethod,
                                      double amount,
                                      PaymentsModel paymentsModel) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            DialogFragment fragment = PracticeChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), amount);
            displayDialogFragment(fragment, false);
        } else {
            showAddCard(amount, paymentsModel);
        }

    }

    @Override
    public void onPaymentPlanAction(final PaymentsModel paymentsModel) {
        PendingBalanceDTO selectedBalance = paymentsModel.getPaymentPayload()
                .getPatientBalances().get(0).getBalances().get(0);
        PracticeModePaymentPlanFragment fragment = PracticeModePaymentPlanFragment
                .newInstance(paymentsModel, selectedBalance);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);

        String[] params = {getString(R.string.param_practice_id),
                getString(R.string.param_balance_amount),
                getString(R.string.param_is_add_existing)};
        Object[] values = {selectedBalance.getMetadata().getPracticeId(),
                selectedBalance.getPayload().get(0).getAmount(),
                false};

        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_started), params, values);
    }

    @Override
    public void onPaymentPlanAmount(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    double amount) {
        //Not used anymore
    }


    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO) {
        showPaymentConfirmation(workflowDTO, false);
    }

    @Override
    public void showAddCard(double amount, PaymentsModel paymentsModel) {
        PracticeAddNewCreditCardFragment fragment = PracticeAddNewCreditCardFragment
                .newInstance(paymentsModel, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onCreditCardSelected(PaymentCreditCardsPayloadDTO creditCard) {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentByTag(PatientModePaymentPlanEditFragment.class.getName());
        if (fragment != null && fragment instanceof PatientModePaymentPlanEditFragment) {
            ((PaymentPlanEditFragment) fragment).replacePaymentMethod(creditCard);
            return;
        }

        fragment = getSupportFragmentManager()
                .findFragmentByTag(PracticeModePaymentPlanFragment.class.getName());
        if (fragment != null && fragment instanceof PracticeModePaymentPlanFragment) {
            ((PracticeModePaymentPlanFragment) fragment).replacePaymentMethod(creditCard);
            return;
        }
    }

    @Override
    public void completePaymentProcess(WorkflowDTO workflowDTO) {
        Gson gson = new Gson();
        PaymentsModel paymentsModel = gson.fromJson(workflowDTO.toString(), PaymentsModel.class);
        PatientBalanceDTO balance = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
        String patientBalance = gson.toJson(balance);
        UpdatePatientBalancesDTO updatePatientBalance = gson.fromJson(patientBalance,
                UpdatePatientBalancesDTO.class);

        hidePaymentDistributionFragment(updatePatientBalance);
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
    public void lookupChargeItem(List<SimpleChargeItem> simpleChargeItems,
                                 AddPaymentItemFragment.AddItemCallback callback) {
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE, gson.toJson(simpleChargeItems));
        AddPaymentItemFragment fragment = new AddPaymentItemFragment();
        fragment.setCallback(callback);
        if (simpleChargeItems != null && !simpleChargeItems.isEmpty()) {
            fragment.setArguments(args);
        }

        displayDialogFragment(fragment, false);
    }

    @Override
    public void showAmountEntry(PaymentDistributionEntryFragment.PaymentDistributionAmountCallback callback,
                                BalanceItemDTO balanceItem, SimpleChargeItem chargeItem) {
        PaymentDistributionEntryFragment entryFragment = new PaymentDistributionEntryFragment();
        if (balanceItem != null) {
            entryFragment.setBalanceItem(balanceItem);
        }
        if (chargeItem != null) {
            entryFragment.setChargeItem(chargeItem);
        }

        entryFragment.setCallback(callback);
        displayDialogFragment(entryFragment, false);
    }

    @Override
    public void showPaymentPlanDashboard(PaymentsModel paymentsModel) {
        PaymentPlanDashboardFragment fragment = PaymentPlanDashboardFragment.newInstance(paymentsModel);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onDetailCancelClicked(PaymentsModel paymentsModel) {
        startPaymentProcess(paymentsModel);
    }

    @Override
    public void onDismissPaymentMethodDialog(PaymentsModel paymentsModel) {
        if(paymentMethodCancelled){
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
    public void onCashSelected(PaymentsModel paymentsModel) {
        //TODO handle this from practice mode
    }

    @Override
    public void showPaymentHistory(PaymentsModel paymentsModel) {
        PaymentHistoryFragment fragment = PaymentHistoryFragment.newInstance(paymentsModel);
        displayDialogFragment(fragment, false);
    }


    @Override
    public void onDismissPaymentHistory() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void displayHistoryItemDetails(PaymentHistoryItem item,
                                          PaymentsModel paymentsModel) {
        PracticePaymentHistoryDetailFragment fragment = PracticePaymentHistoryDetailFragment
                .newInstance(item, paymentsModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void startRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel) {
        RefundProcessFragment fragment = RefundProcessFragment.newInstance(historyItem, paymentsModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void completeRefundProcess(PaymentHistoryItem historyItem, PaymentsModel paymentsModel) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);

        RefundDetailFragment fragment = RefundDetailFragment.newInstance(historyItem, paymentsModel);
        displayDialogFragment(fragment, false);

    }

    @Override
    public void displayPaymentPlansList(final PaymentsModel paymentsModel) {
        PracticePaymentPlanListFragment fragment = PracticePaymentPlanListFragment
                .newInstance(paymentsModel, getPracticeInfo(paymentsModel).getPracticeId());
        fragment.setDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                showPaymentHistory(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onPaymentPlanSelected(final PaymentsModel paymentsModel,
                                      PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        displayDialogFragment(fragment, false);
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
        Map<String, String> queryMap = new HashMap<>();
        queryMap.put("start_date", getFormattedDate(start));
        queryMap.put("end_date", getFormattedDate(end));

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
            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                hideProgressDialog();
                startDate = start;
                endDate = end;

                paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
                updateDateLabel();
                populateList();
            }

            @Override
            public void onFailure(String exceptionMessage) {
                hideProgressDialog();
                showErrorNotification(exceptionMessage);
            }
        };
    }

    @Override
    public void showChooseDeviceList(PaymentsModel paymentsModel, double paymentAmount) {
        IntegratedPaymentsChooseDeviceFragment fragment = IntegratedPaymentsChooseDeviceFragment
                .newInstance(paymentsModel, paymentAmount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void dismissChooseDeviceList(double amount, PaymentsModel paymentsModel) {
        onPayButtonClicked(amount, paymentsModel);
    }

    @Override
    public void onMakeOneTimePayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO) {
        PracticeOneTimePaymentFragment fragment = PracticeOneTimePaymentFragment.newInstance(paymentsModel,
                0, paymentPlanDTO);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartOneTimePayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paymentMethodCancelled = true;
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);

        String[] params = {getString(com.carecloud.carepaylibrary.R.string.param_practice_id),
                getString(com.carecloud.carepaylibrary.R.string.param_payment_plan_id),
                getString(com.carecloud.carepaylibrary.R.string.param_payment_plan_amount)};
        Object[] values = {
                paymentPlanDTO.getMetadata().getPracticeId(),
                paymentPlanDTO.getMetadata().getPaymentPlanId(),
                paymentPlanDTO.getPayload().getAmount()};
        MixPanelUtil.logEvent(getString(R.string.event_paymentplan_onetime_payment), params, values);
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          final PaymentsModel paymentsModel,
                                          final PaymentPlanDTO paymentPlanDTO,
                                          boolean onlySelectMode,
                                          final Date paymentDate) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanDTO,
                            onlySelectMode, paymentDate);
            fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (paymentDate == null) {
                        onStartOneTimePayment(paymentsModel, paymentPlanDTO);
                    } else {
                        onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
                    }
                }
            });
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        }
    }

    @Override
    public void onAddPaymentPlanCard(final PaymentsModel paymentsModel,
                                     final PaymentPlanDTO paymentPlanDTO,
                                     boolean onlySelectMode,
                                     final Date paymentDate) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, paymentPlanDTO, onlySelectMode, paymentDate);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (paymentDate == null) {
                    onStartOneTimePayment(paymentsModel, paymentPlanDTO);
                } else {
                    onScheduleOneTimePayment(paymentsModel, paymentPlanDTO, paymentDate);
                }
            }
        });
        fragment.setChangePaymentMethodListener(new LargeAlertDialog.LargeAlertInterface() {
            @Override
            public void onActionButton() {
                PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                        .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
                displayDialogFragment(fragment, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onScheduleOneTimePayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO, Date paymentDate) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, false, paymentDate);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                paymentMethodCancelled = true;
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        ScheduledPaymentModel scheduledPayment = paymentsModel.getPaymentPayload()
                .getScheduledPaymentModel();
        List<ScheduledPaymentModel> scheduledPaymentModels = this.paymentsModel.getPaymentPayload().getScheduledOneTimePayments();
        for (ScheduledPaymentModel scheduledPaymentModel : scheduledPaymentModels) {
            if (scheduledPaymentModel.getMetadata().getOneTimePaymentId().equals(scheduledPayment.getMetadata().getOneTimePaymentId())) {
                scheduledPaymentModels.remove(scheduledPayment);
                break;
            }
        }
        scheduledPaymentModels.add(scheduledPayment);
        completePaymentProcess(workflowDTO);

        DateUtil.getInstance().setDateRaw(scheduledPayment.getPayload().getPaymentDate());
        String message = String.format(Label.getLabel("payment.oneTimePayment.schedule.success"),
                StringUtil.getFormattedBalanceAmount(scheduledPayment.getPayload().getAmount()),
                DateUtil.getInstance().getDateAsDayShortMonthDayOrdinal());
        showSuccessToast(message);

    }

    @Override
    public void showDeleteScheduledPaymentConfirmation(WorkflowDTO workflowDTO) {
        completePaymentProcess(workflowDTO);
    }

    @Override
    public void showPaymentConfirmation(WorkflowDTO workflowDTO, boolean isOneTimePayment) {
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
        }
    }

    @Override
    public void onStartPaymentPlan(PaymentsModel paymentsModel, PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDismissPaymentPlan(PaymentsModel paymentsModel) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        PaymentDistributionFragment fragment = (PaymentDistributionFragment) fragmentManager
                .findFragmentByTag(PaymentDistributionFragment.class.getName());
        if (fragment != null) {
            fragment.showDialog();
        }
    }

    @Override
    public void onSelectPaymentPlanMethod(PaymentsMethodsDTO selectedPaymentMethod,
                                          PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel,
                                          boolean onlySelectMode) {
        if (paymentsModel.getPaymentPayload().getPatientCreditCards() != null
                && !paymentsModel.getPaymentPayload().getPatientCreditCards().isEmpty()) {
            PracticePaymentPlanChooseCreditCardFragment fragment = PracticePaymentPlanChooseCreditCardFragment
                    .newInstance(paymentsModel, selectedPaymentMethod.getLabel(), paymentPlanPostModel);
            displayDialogFragment(fragment, false);
        } else {
            onAddPaymentPlanCard(paymentsModel, paymentPlanPostModel, onlySelectMode);
        }
    }

    @Override
    public void onAddPaymentPlanCard(PaymentsModel paymentsModel,
                                     PaymentPlanPostModel paymentPlanPostModel,
                                     boolean onlySelectMode) {
        PracticePaymentPlanAddCreditCardFragment fragment = PracticePaymentPlanAddCreditCardFragment
                .newInstance(paymentsModel, (PaymentPlanDTO) null, onlySelectMode);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onDisplayPaymentPlanTerms(PaymentsModel paymentsModel,
                                          PaymentPlanPostModel paymentPlanPostModel) {
        PracticePaymentPlanTermsFragment fragment = PracticePaymentPlanTermsFragment
                .newInstance(paymentsModel, paymentPlanPostModel);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSubmitPaymentPlan(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PracticePaymentPlanConfirmationFragment fragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_CREATE);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void displayBalanceDetails(PaymentsModel paymentsModel,
                                      PendingBalancePayloadDTO paymentLineItem,
                                      PendingBalanceDTO selectedBalance) {
        PaymentDetailsFragmentDialog dialog = PaymentDetailsFragmentDialog
                .newInstance(paymentsModel, selectedBalance.getPayload().get(0), false);
        displayDialogFragment(dialog, false);
    }

    @Override
    public void onEditPaymentPlan(PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PatientModePaymentPlanEditFragment fragment = PatientModePaymentPlanEditFragment
                .newInstance(paymentsModel, paymentPlanDTO);
        displayDialogFragment(fragment, true);
    }

    @Override
    public void onPaymentPlanEdited(WorkflowDTO workflowDTO) {
        PaymentsModel paymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
        if (paymentsModel.getPaymentPayload().getPatientPaymentPlans().isEmpty()) {
            //no changes to plan
            return;
        }
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_EDIT);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onPaymentPlanAddedExisting(WorkflowDTO workflowDTO) {
        getSupportFragmentManager().popBackStackImmediate(PaymentDistributionFragment.class.getName(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
        PracticePaymentPlanConfirmationFragment confirmationFragment = PracticePaymentPlanConfirmationFragment
                .newInstance(workflowDTO, getApplicationMode().getUserPracticeDTO(),
                        PaymentPlanConfirmationFragment.MODE_ADD);
        displayDialogFragment(confirmationFragment, false);
    }

    @Override
    public void onDismissEditPaymentPlan(final PaymentsModel paymentsModel, PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlanDTO, false);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onAddBalanceToExistingPlan(PaymentsModel paymentsModel,
                                           PendingBalanceDTO selectedBalance,
                                           double amount) {
        PracticeValidPlansFragment fragment = PracticeValidPlansFragment
                .newInstance(paymentsModel, selectedBalance, amount);
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onSelectedPlanToAdd(PaymentsModel paymentsModel,
                                    PendingBalanceDTO selectedBalance,
                                    PaymentPlanDTO selectedPlan,
                                    double amount) {
        //Not used anymore
    }

    @Override
    public void onEditPaymentPlanPaymentMethod(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO) {
        PracticePaymentPlanPaymentMethodFragment fragment = PracticePaymentPlanPaymentMethodFragment
                .newInstance(paymentsModel, paymentPlanDTO, true);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void onStartEditScheduledPayment(final PaymentsModel paymentsModel, final PaymentPlanDTO paymentPlanDTO, ScheduledPaymentModel scheduledPaymentModel) {
        PracticeEditOneTimePaymentFragment fragment = PracticeEditOneTimePaymentFragment.newInstance(paymentsModel, 0, paymentPlanDTO, scheduledPaymentModel);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDetail(paymentsModel, paymentPlanDTO, false);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public DTO getDto() {
        return paymentsModel;
    }

    @Override
    public void completePaymentPlanProcess(WorkflowDTO workflowDTO) {
        //nothing to do with the updated payment plan because this is not on the main screen
        populateList();
    }

    @Override
    public void onAddBalanceToExistingPlan(final PaymentsModel paymentsModel, PaymentPlanDTO paymentPlan) {
        PracticeModeAddToExistingPaymentPlanFragment fragment = PracticeModeAddToExistingPaymentPlanFragment
                .newInstance(paymentsModel, paymentPlan);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }

    @Override
    public void showPaymentPlanDetail(final PaymentsModel paymentsModel,
                                      PaymentPlanDTO paymentPlan,
                                      boolean isCompleted) {
        PracticePaymentPlanDetailsDialogFragment fragment = PracticePaymentPlanDetailsDialogFragment
                .newInstance(paymentsModel, paymentPlan, isCompleted);
        fragment.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                showPaymentPlanDashboard(paymentsModel);
            }
        });
        displayDialogFragment(fragment, false);
    }
}
