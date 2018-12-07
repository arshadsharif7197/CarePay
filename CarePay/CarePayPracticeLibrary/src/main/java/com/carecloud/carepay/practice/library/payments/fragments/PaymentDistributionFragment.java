package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.adapter.PaymentDistributionAdapter;
import com.carecloud.carepay.practice.library.payments.adapter.PopupPickLocationAdapter;
import com.carecloud.carepay.practice.library.payments.adapter.PopupPickProviderAdapter;
import com.carecloud.carepay.practice.library.payments.adapter.PopupPickerAdapter;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerPayments;
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerWindow;
import com.carecloud.carepay.practice.library.payments.interfaces.AddPaymentItemCallback;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.WorkflowServiceCallback;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.base.models.UserAuthPermissions;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentPlanDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPlanLineItem;
import com.carecloud.carepaylibray.retail.models.RetailBillingPerson;
import com.carecloud.carepaylibray.retail.models.RetailItemDto;
import com.carecloud.carepaylibray.retail.models.RetailItemOptionChoiceDto;
import com.carecloud.carepaylibray.retail.models.RetailItemPayload;
import com.carecloud.carepaylibray.retail.models.RetailLineItemMetadata;
import com.carecloud.carepaylibray.retail.models.RetailOrderItem;
import com.carecloud.carepaylibray.retail.models.RetailPostModelOrder;
import com.carecloud.carepaylibray.retail.models.RetailSelectedOption;
import com.carecloud.carepaylibray.retail.models.RetailProductsModel;
import com.carecloud.carepaylibray.utils.BounceHelper;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by lmenendez on 3/14/17
 */

public class PaymentDistributionFragment extends BaseDialogFragment
        implements PaymentDistributionAdapter.PaymentDistributionCallback, PopupPickerAdapter.PopupPickCallback,
        AddPaymentItemCallback, PaymentDistributionEntryFragment.PaymentDistributionAmountCallback,
        BounceHelper.BounceHelperListener, PopupPickerPayments.PaymentPopupListener {

    private TextView balanceTextView;
    private TextView paymentTotalTextView;
    private TextView unAppliedTextView;

    private View unappliedLayout;

    private NestedScrollView scrollView;
    private RecyclerView balanceDetailsRecycler;
    private View newChargesLayout;
    private RecyclerView newChargesRecycler;
    private Button payButton;
    private View emptyBalanceLayout;
    private View actionButton;
    private View retailChargesLayout;
    private RecyclerView retailChargesRecycler;

    private BounceHelper balanceViewSwipeHelper;
    private BounceHelper chargeViewSwipeHelper;
    private BounceHelper retailViewSwipeHelper;

    private PopupPickerWindow locationPickerWindow;
    private PopupPickerWindow providerPickerWindow;
    private PopupPickerPayments paymentsPickerWindow;

    private PaymentsModel paymentsModel;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();
    private List<BalanceItemDTO> chargeItems = new ArrayList<>();
    private List<BalanceItemDTO> retailItems = new ArrayList<>();

    private PracticePaymentNavigationCallback callback;

    private double paymentAmount;
    private double balanceAmount;
    private double chargesAmount;
    private double retailAmount;
    private double overPaymentAmount;
    private double unappliedCredit = 0D;
    private double originalUnapplied = 0D;

    private NumberFormat currencyFormatter;

    private List<LocationDTO> locations;
    private List<ProviderDTO> providers;
    private LocationDTO defaultLocation;
    private ProviderDTO defaultProvider;

    private boolean hasPaymentError = false;
    private UserAuthPermissions authPermissions;

    private DemographicPayloadDTO patientDemographics;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            callback = (PracticePaymentNavigationCallback) context;
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached context must implement PracticePaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US);

        Bundle args = getArguments();
        if (args != null) {
            Gson gson = new Gson();
            String paymentPayload = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentPayload, PaymentsModel.class);
            authPermissions = paymentsModel.getPaymentPayload().getUserAuthModel().getUserAuthPermissions();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_payment_distribution, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle icicle) {
        setupButtons(view);

        balanceTextView = (TextView) view.findViewById(R.id.balance_value);
        unAppliedTextView = (TextView) view.findViewById(R.id.unapplied_value);

        scrollView = (NestedScrollView) view.findViewById(R.id.nested_scroller);

        unappliedLayout = view.findViewById(R.id.unapplied_layout);

        RecyclerView.LayoutManager balanceLayoutManager = new LinearLayoutManager(getContext());
        balanceDetailsRecycler = (RecyclerView) view.findViewById(R.id.balances_recycler);
        balanceDetailsRecycler.setLayoutManager(balanceLayoutManager);
        balanceDetailsRecycler.addOnScrollListener(scrollListener);

        balanceViewSwipeHelper = new BounceHelper(this);
        ItemTouchHelper balanceTouchHelper = new ItemTouchHelper(balanceViewSwipeHelper);
        balanceTouchHelper.attachToRecyclerView(balanceDetailsRecycler);


        newChargesLayout = view.findViewById(R.id.new_charges_layout);
        RecyclerView.LayoutManager chargesLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        newChargesRecycler = (RecyclerView) view.findViewById(R.id.new_charges_recycler);
        newChargesRecycler.setLayoutManager(chargesLayoutManager);
        newChargesRecycler.addOnScrollListener(scrollListener);

        chargeViewSwipeHelper = new BounceHelper(this);
        ItemTouchHelper chargesTouchHelper = new ItemTouchHelper(chargeViewSwipeHelper);
        chargesTouchHelper.attachToRecyclerView(newChargesRecycler);

        retailChargesLayout = view.findViewById(R.id.retail_charges_layout);
        RecyclerView.LayoutManager retailLayoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false);
        retailChargesRecycler = (RecyclerView) view.findViewById(R.id.retail_charges_recycler);
        retailChargesRecycler.setLayoutManager(retailLayoutManager);
        retailChargesRecycler.addOnScrollListener(scrollListener);

        retailViewSwipeHelper = new BounceHelper(this);
        ItemTouchHelper retailTouchHelper = new ItemTouchHelper(retailViewSwipeHelper);
        retailTouchHelper.attachToRecyclerView(retailChargesRecycler);

        emptyBalanceLayout = view.findViewById(R.id.empty_balance_layout);
        TextView emptyMessage = (TextView) view.findViewById(R.id.no_payment_message);
        emptyMessage.setText(Label.getLabel("payment_balance_empty_payment_screen"));
        emptyMessage.setVisibility(View.GONE);

        setInitialValues(view);
        setAdapter();
        setupPickerWindows();
        setDefaultProviderLocation();

        View.OnClickListener clearViews = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPickers();
                clearLastSwipeView();
            }
        };
        View container = view.findViewById(R.id.container_main);
        container.setSoundEffectsEnabled(false);
        container.setOnClickListener(clearViews);
        emptyBalanceLayout.setOnClickListener(clearViews);
    }

    private void setupToolbar(View view, String titleString) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if (toolbar != null) {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(titleString);
        }
    }

    private void setupButtons(View view) {
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        View addButton = view.findViewById(R.id.add_item_button);
        View addButtonEmpty = view.findViewById(R.id.add_item_button_empty);
        View.OnClickListener addItem = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.lookupChargeItem(paymentsModel.getPaymentPayload().getSimpleChargeItems(),
                        PaymentDistributionFragment.this);
                hideDialog();
            }
        };
        addButton.setOnClickListener(addItem);
        addButtonEmpty.setOnClickListener(addItem);

        Button paymentPlanButton = (Button) view.findViewById(R.id.payment_left_button);
        paymentPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showPaymentPlanDashboard(paymentsModel);
                hideDialog();
            }
        });

        payButton = (Button) view.findViewById(R.id.payment_pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateBalanceItems()) {
                    distributeAmountOverBalanceItems(paymentAmount);

                    generatePaymentsModel();
                    if (!hasPaymentError) {
                        callback.onPayButtonClicked(round(paymentAmount + chargesAmount + retailAmount), paymentsModel);
                        hideDialog();
                    }
                }
            }
        });

        paymentTotalTextView = (TextView) view.findViewById(R.id.payment_value);
        paymentTotalTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAmountEntry(PaymentDistributionFragment.this,
                        null, null);
                hideDialog();
            }
        });
        actionButton = view.findViewById(R.id.action_button);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (paymentsPickerWindow.isShowing()) {
                    paymentsPickerWindow.dismiss();
                    view.setSelected(false);
                } else {
                    clearPickers();
                    int offset = view.getWidth() / 2 - paymentsPickerWindow.getWidth() / 2;
                    paymentsPickerWindow.showAsDropDown(view, offset, 3);
                    view.setSelected(true);
                }
            }
        });

        payButton.setEnabled(authPermissions.canMakePayment);
        addButton.setEnabled(authPermissions.canAddCharges);
        addButtonEmpty.setEnabled(authPermissions.canAddCharges);
    }

    private void setInitialValues(View view) {
        if (paymentsModel != null) {
            balanceAmount = calculateTotalBalance();
            paymentAmount = balanceAmount;

            setCurrency(balanceTextView, balanceAmount);
            updatePaymentAmount();

            PatientBalanceDTO patientBalanceDTO = null;
            if (!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()) {
                patientBalanceDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
            }

            if (patientBalanceDTO != null) {
                patientDemographics = patientBalanceDTO.getDemographics().getPayload();
                String patientNameString = patientDemographics.getPersonalDetails().getFullName();

                patientNameString = StringUtil.capitalize(patientNameString);
                setupToolbar(view, StringUtil.getLabelForView(patientNameString));

                try {
                    unappliedCredit = Double.parseDouble(patientBalanceDTO.getUnappliedCredit()) * -1;
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }

            if (unappliedCredit > 0D) {
                originalUnapplied = unappliedCredit;
                setCurrency(unAppliedTextView, unappliedCredit);
            } else {
                unappliedLayout.setVisibility(View.GONE);
            }

            setMaxAmounts();
            getPaymentPlansInformation();
        }
    }

    private void getPaymentPlansInformation() {
        if (hasBalance()) {
            for (BalanceItemDTO balanceItem : paymentsModel.getPaymentPayload().getPatientBalances()
                    .get(0).getBalances().get(0).getPayload().get(0).getDetails()) {
                String balanceItemId = String.valueOf(balanceItem.getId());
                balanceItem.setAmountInPaymentPlan(0.0);
                for (PaymentPlanDTO paymentPlan : paymentsModel.getPaymentPayload().getPatientPaymentPlans()) {
                    for (PaymentPlanLineItem lineItem : paymentPlan.getPayload().getLineItems()) {
                        if (balanceItemId.equals(lineItem.getTypeId())) {
                            if (balanceItem.getAmountInPaymentPlan() > 0.0) {
                                balanceItem.setInMoreThanOnePaymentPlan(true);
                            }
                            balanceItem.setAmountInPaymentPlan(SystemUtil.safeSubtract(
                                    SystemUtil.safeAdd(balanceItem.getAmountInPaymentPlan(), lineItem.getAmount()),
                                    lineItem.getAmountPaid()));
                        }
                    }
                }
            }
        }
    }

    private void setAdapter() {
        if (balanceDetailsRecycler.getAdapter() == null) {
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), balanceItems,
                    this, PaymentDistributionAdapter.PaymentRowType.BALANCE);
            balanceDetailsRecycler.setAdapter(adapter);
        } else {
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) balanceDetailsRecycler.getAdapter();
            adapter.setBalanceItems(balanceItems);
            adapter.notifyDataSetChanged();
        }

        if (newChargesRecycler.getAdapter() == null) {
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), chargeItems,
                    this, PaymentDistributionAdapter.PaymentRowType.NEW_CHARGE);
            newChargesRecycler.setAdapter(adapter);
        } else {
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) newChargesRecycler.getAdapter();
            adapter.setBalanceItems(chargeItems);
            adapter.notifyDataSetChanged();
        }

        if (retailChargesRecycler.getAdapter() == null) {
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), retailItems,
                    this, PaymentDistributionAdapter.PaymentRowType.RETAIL);
            retailChargesRecycler.setAdapter(adapter);
        } else {
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) retailChargesRecycler.getAdapter();
            adapter.setBalanceItems(retailItems);
            adapter.notifyDataSetChanged();
        }

        if (balanceItems.isEmpty() && chargeItems.isEmpty() && retailItems.isEmpty()) {
            emptyBalanceLayout.setVisibility(View.VISIBLE);
            paymentTotalTextView.setClickable(false);
        } else {
            if (balanceItems.isEmpty()) {
                balanceDetailsRecycler.setVisibility(View.GONE);
                paymentTotalTextView.setClickable(false);
            } else {
                balanceDetailsRecycler.setVisibility(View.VISIBLE);
                paymentTotalTextView.setClickable(true);
            }
            emptyBalanceLayout.setVisibility(View.GONE);
        }
    }

    private void scrollAdapterToItem(BalanceItemDTO balanceItemDTO) {
        int position;
        int locationY = -1;
        position = balanceItems.indexOf(balanceItemDTO);
        if (position > 0) {
            locationY = (int) balanceDetailsRecycler.getChildAt(position).getY();
        }
        if (locationY < 0) {
            position = chargeItems.indexOf(balanceItemDTO);
            if (position > 0) {
                locationY = (int) newChargesRecycler.getChildAt(position).getY();
            }
        }
        if (locationY < 0) {
            position = retailItems.indexOf(balanceItemDTO);
            if (position > 0) {
                locationY = (int) retailChargesRecycler.getChildAt(position).getY();
            }
        }
        if (locationY < 0) {
            //not found
            return;
        }

        Log.d("RecyclerView", "Scroll to Position: " + position + " at: " + locationY);
        scrollView.smoothScrollTo(0, locationY + 10);

    }

    private void setupPickerWindows() {
        locationPickerWindow = new PopupPickerWindow(getContext());
        locations = paymentsModel.getPaymentPayload().getLocations();
        PopupPickLocationAdapter locationAdapter = new PopupPickLocationAdapter(getContext(), locations, this);
        locationPickerWindow.setAdapter(locationAdapter);

        providerPickerWindow = new PopupPickerWindow(getContext());
        providers = paymentsModel.getPaymentPayload().getProviders();
        PopupPickProviderAdapter providerAdapter = new PopupPickProviderAdapter(getContext(), providers, this);
        providerPickerWindow.setAdapter(providerAdapter);

        boolean hasHistory = !paymentsModel.getPaymentPayload()
                .getTransactionHistory().getPaymentHistoryList().isEmpty();
        paymentsPickerWindow = new PopupPickerPayments(getContext(),
                callback.getPracticeInfo(paymentsModel),
                this,
                hasHistory);
    }

    private double calculateTotalBalance() {
        double total = 0D;
        for (PatientBalanceDTO patientBalance : paymentsModel.getPaymentPayload().getPatientBalances()) {
            for (PendingBalanceDTO pendingBalance : patientBalance.getBalances()) {
                for (PendingBalancePayloadDTO pendingBalancePayload : pendingBalance.getPayload()) {
                    total = round(total + pendingBalancePayload.getAmount());
                    switch (pendingBalancePayload.getType()) {
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:
                        case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE: {
                            double amount = pendingBalancePayload.getAmount();
                            BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
                            balanceItemDTO.setBalance(amount);
                            balanceItemDTO.setAmount(amount);
                            balanceItemDTO.setDescription(pendingBalancePayload.getType());

                            balanceItems.add(balanceItemDTO);
                            break;
                        }
                        default: {
                            balanceItems.addAll(pendingBalancePayload.getDetails());
                        }
                    }
                }
            }
        }

        return total;
    }

    private void setMaxAmounts() {
        for (BalanceItemDTO balanceItemDTO : balanceItems) {
            balanceItemDTO.setMaxAmount(balanceItemDTO.getBalance());
        }
    }

    private void resetInitialAmounts() {
        for (BalanceItemDTO balanceItemDTO : balanceItems) {
            balanceItemDTO.setBalance(balanceItemDTO.getMaxAmount());
        }
        unappliedCredit = originalUnapplied;
    }

    private void setDefaultProviderLocation() {
        if (paymentsModel.getPaymentPayload().getPatientBalances().isEmpty() ||
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().isEmpty()) {
            return;
        }
        String patientID = paymentsModel.getPaymentPayload().getPatientBalances().get(0)
                .getBalances().get(0).getMetadata().getPatientId();
        String locationID = null;
        String providerID = null;
        for (LocationIndexDTO locationIndex : paymentsModel.getPaymentPayload().getLocationIndex()) {
            for (String locationPatientID : locationIndex.getPatientIds()) {
                if (locationPatientID.equals(patientID)) {
                    locationID = locationIndex.getId();
                    break;
                }
            }
        }
        for (ProviderIndexDTO providerIndex : paymentsModel.getPaymentPayload().getProviderIndex()) {
            for (String providerPatientID : providerIndex.getPatientIds()) {
                if (providerPatientID.equals(patientID)) {
                    providerID = providerIndex.getId();
                    break;
                }
            }
        }

        if (locationID != null) {
            for (LocationDTO location : locations) {
                if (locationID.equals(location.getId().toString())) {
                    defaultLocation = location;
                    break;
                }
            }
        } else if (locations.size() == 1) {
            defaultLocation = locations.get(0);
        }

        if (providerID != null) {
            for (ProviderDTO provider : providers) {
                if (providerID.equals(provider.getId().toString())) {
                    defaultProvider = provider;
                    break;
                }
            }
        } else if (providers.size() == 1) {
            defaultProvider = providers.get(0);
        }
    }

    private void modifyLineItem(BalanceItemDTO balanceItem, ProviderDTO updateProvider,
                                LocationDTO updateLocation, Double updateAmount) {
        if (updateAmount != null) {
            double difference;
            double currentAmount = balanceItem.getBalance();

            difference = round(currentAmount - updateAmount);

            if (paymentAmount <= balanceAmount && unappliedCredit > 0) {
                if (difference > unappliedCredit) {
                    difference = round(difference - unappliedCredit);
                    unappliedCredit = 0;
                } else {
                    unappliedCredit = round(unappliedCredit - difference);
                }

            }
            balanceItem.setBalance(updateAmount);
            paymentAmount = round(paymentAmount - difference);
            updatePaymentAmount();

        }

        if (updateProvider != null) {
            balanceItem.setProvider(updateProvider);
            balanceItem.setProviderId(updateProvider.getId());
        }

        if (updateLocation != null) {
            balanceItem.setLocation(updateLocation);
            balanceItem.setLocationId(updateLocation.getId());
        }

        int index = balanceItems.indexOf(balanceItem);
        if (index >= 0) {
            balanceDetailsRecycler.getAdapter().notifyItemChanged(index);
            return;
        }
        index = chargeItems.indexOf(balanceItem);
        if (index >= 0) {
            newChargesRecycler.getAdapter().notifyItemChanged(index);
            return;
        }
        index = retailItems.indexOf(balanceItem);
        if (index >= 0) {
            retailChargesRecycler.getAdapter().notifyItemChanged(index);
        }
    }


    private void updatePaymentAmount() {
        if (paymentAmount >= balanceAmount) {
            paymentTotalTextView.setBackgroundResource(R.drawable.bg_green_border_trans);
            paymentTotalTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.emerald));
        } else {
            paymentTotalTextView.setBackgroundResource(R.drawable.bg_orange_border_trans);
            paymentTotalTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.lightning_yellow));
        }

        double totalAmount = round(paymentAmount + chargesAmount + retailAmount);
        payButton.setEnabled(totalAmount > 0 && authPermissions.canMakePayment);

        setCurrency(paymentTotalTextView, totalAmount);
    }

    private void setCurrency(TextView textView, double amount) {
        textView.setText(currencyFormatter.format(amount));
    }

    private void setChargeLayoutVisibility() {
        newChargesLayout.setVisibility(chargeItems.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setRetailLayoutVisibility() {
        retailChargesLayout.setVisibility(retailItems.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            clearLastSwipeView();
            clearPickers();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    @Override
    public void startNewSwipe() {
        clearPickers();
    }

    @Override
    public void pickProvider(View view, BalanceItemDTO balanceItem) {
        boolean isShowing = providerPickerWindow.isShowing();
        clearPickers();
        clearLastSwipeView();
        if (!isShowing) {
            int offset = view.getWidth() / 2 - providerPickerWindow.getWidth() / 2;
            providerPickerWindow.setBalanceItemDTO(balanceItem);
            providerPickerWindow.showAsDropDown(view, offset, 10);
        }
    }

    @Override
    public void pickProvider(ProviderDTO provider, BalanceItemDTO balanceItem) {
        clearPickers();
        modifyLineItem(balanceItem, provider, null, null);
    }

    @Override
    public void pickLocation(View view, BalanceItemDTO balanceItem) {
        boolean isShowing = locationPickerWindow.isShowing();
        clearPickers();
        clearLastSwipeView();
        if (!isShowing) {
            int offset = view.getWidth() / 2 - locationPickerWindow.getWidth() / 2;
            locationPickerWindow.setBalanceItemDTO(balanceItem);
            locationPickerWindow.showAsDropDown(view, offset, 10);
        }
    }

    @Override
    public void pickLocation(LocationDTO location, BalanceItemDTO balanceItem) {
        clearPickers();
        modifyLineItem(balanceItem, null, location, null);
    }

    @Override
    public void editAmount(double amount, BalanceItemDTO balanceItem) {
        modifyLineItem(balanceItem, null, null, amount);
    }

    @Override
    public void pickAmount(BalanceItemDTO balanceItem) {
        callback.showAmountEntry(this, balanceItem, null);
        hideDialog();
    }

    @Override
    public void removeCharge(BalanceItemDTO chargeItem) {
        chargeItems.remove(chargeItem);
        chargesAmount = round(chargesAmount - chargeItem.getBalance());
        updatePaymentAmount();
        setAdapter();
        setChargeLayoutVisibility();
    }

    @Override
    public void removeRetailItem(BalanceItemDTO retailItem) {
        retailItems.remove(retailItem);
        retailAmount = round(retailAmount - retailItem.getBalance());
        updatePaymentAmount();
        setAdapter();
        setRetailLayoutVisibility();
    }

    @Override
    public void addNewCharge(double amount, SimpleChargeItem chargeItem) {
        showDialog();
        BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
        balanceItemDTO.setNewCharge(true);
        balanceItemDTO.setId(chargeItem.getId());
        balanceItemDTO.setDescription(chargeItem.getDescription());
        balanceItemDTO.setAmount(amount);
        balanceItemDTO.setBalance(amount);
        balanceItemDTO.setMaxAmount(amount);
        balanceItemDTO.setResponsibilityType(chargeItem.getResponsibilityType());
        if (defaultProvider != null) {
            balanceItemDTO.setProvider(defaultProvider);
        }
        if (defaultLocation != null) {
            balanceItemDTO.setLocation(defaultLocation);
        }
        chargeItems.add(balanceItemDTO);
        chargesAmount = round(chargesAmount + amount);
        updatePaymentAmount();
        setAdapter();
        setChargeLayoutVisibility();
    }

    @Override
    public void onDismissEntryDialog() {
        showDialog();
    }

    @Override
    public void addChargeItem(SimpleChargeItem chargeItem) {
        callback.showAmountEntry(this, null, chargeItem);
        hideDialog();
    }

    @Override
    public void addRetailItem(RetailItemDto retailItem) {
        callback.showRetailItemOptions(retailItem, this);
        hideDialog();
    }

    @Override
    public void addRetailItemWithOptions(RetailItemDto retailItemDto, int quantity, Map<Integer, RetailItemOptionChoiceDto> selectedOptions) {
        double basePrice = retailItemDto.getPrice();
        double priceModification = retailItemDto.getPriceModification(selectedOptions);

        double amount = round(quantity * (basePrice + priceModification));
        BalanceItemDTO retailItem = new BalanceItemDTO();
        retailItem.setAmount(amount);
        retailItem.setBalance(amount);
        retailItem.setMaxAmount(amount);
        retailItem.setDescription(retailItemDto.getName());
        retailItem.setRetailItem(true);

        RetailItemPayload retailPayload = new RetailItemPayload();
        retailPayload.setQuantity(quantity);
        retailPayload.setRetailItemDto(retailItemDto);
        retailPayload.setSelectedOptions(selectedOptions);
        retailItem.setRetailPayload(retailPayload);

        if (defaultProvider != null) {
            retailItem.setProvider(defaultProvider);
        }
        if (defaultLocation != null) {
            retailItem.setLocation(defaultLocation);
        }
        retailItems.add(retailItem);
        retailAmount = round(retailAmount + amount);
        updatePaymentAmount();
        setAdapter();
        setRetailLayoutVisibility();
        showDialog();
    }

    @Override
    public void onDismissAddItemFragment() {
        showDialog();
    }

    @Override
    public void applyNewDistributionAmount(double amount) {
        showDialog();
        paymentAmount = amount;
        chargesAmount = 0D;
        retailAmount = 0D;
        updatePaymentAmount();

        resetInitialAmounts();
        balanceItems.clear();
        chargeItems.clear();
        retailItems.clear();
        calculateTotalBalance();

        distributeAmountOverBalanceItems(amount);

        setAdapter();
        setChargeLayoutVisibility();
        setRetailLayoutVisibility();
    }

    @Override
    public void applyAmountToBalanceItem(double amount, BalanceItemDTO balanceItem) {
        showDialog();
        modifyLineItem(balanceItem, null, null, amount);
        setAdapter();
    }


    private void clearPickers() {
        providerPickerWindow.dismiss();
        locationPickerWindow.dismiss();
        paymentsPickerWindow.dismiss();
        actionButton.setSelected(false);
    }

    private void clearLastSwipeView() {
        balanceViewSwipeHelper.clearLastSwipeView();
        chargeViewSwipeHelper.clearLastSwipeView();
        retailViewSwipeHelper.clearLastSwipeView();
    }


    private void distributeAmountOverBalanceItems(double amount) {

        for (BalanceItemDTO balanceItem : balanceItems) {
            double balance = balanceItem.getBalance();
            if (balance <= 0) {//this should be skipped and cleared
                balanceItem.setBalance(0);
            } else {
                if (amount >= balance) {
                    amount = round(amount - balance);
                } else {
                    balance = amount;
                    balanceItem.setBalance(balance);
                    if (amount > 0) {
                        amount = round(amount - balance);
                    }
                }
            }
        }

        if (amount > 0) {//there is some amount left over for the payment
            overPaymentAmount = amount;
        }
    }

    private boolean validateBalanceItems() {
        boolean isValid = true;
        BalanceItemDTO firstInvalidItem = null;
        for (BalanceItemDTO balanceItem : balanceItems) {
            if (!isValidItem(balanceItem)) {
                isValid = false;
                if (firstInvalidItem == null) {
                    firstInvalidItem = balanceItem;
                }
            }
        }

        for (BalanceItemDTO chargeItem : chargeItems) {
            if (!isValidItem(chargeItem)) {
                isValid = false;
                if (firstInvalidItem == null) {
                    firstInvalidItem = chargeItem;
                }
            }
        }

        for (BalanceItemDTO retailItem : retailItems) {
            if (!isValidItem(retailItem)) {
                isValid = false;
                if (firstInvalidItem == null) {
                    firstInvalidItem = retailItem;
                }
            }
        }

        if (!isValid) {
            setAdapter();
        }

        if (firstInvalidItem != null) {
            scrollAdapterToItem(firstInvalidItem);
        }
        return isValid;
    }

    private boolean isValidItem(BalanceItemDTO balanceItem) {
        boolean isValid = true;
        if (balanceItem.getBalance() > 0) {
            if (balanceItem.getLocation() == null || balanceItem.getLocation().getId() == null) {
                isValid = false;
                if (balanceItem.getLocation() != null) {
                    balanceItem.getLocation().setError(true);
                }
            }
            if (balanceItem.getProvider() == null || balanceItem.getProvider().getId() == null) {
                isValid = false;
                if (balanceItem.getProvider() != null) {
                    balanceItem.getProvider().setError(true);
                }
            }
        }
        return isValid;
    }

    private void generatePaymentsModel() {
        hasPaymentError = false;
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount(round(paymentAmount + chargesAmount + retailAmount));
        for (BalanceItemDTO balanceItemDTO : balanceItems) {
            addPaymentObject(balanceItemDTO, postModel);
        }
        for (BalanceItemDTO balanceItemDTO : chargeItems) {
            addPaymentObject(balanceItemDTO, postModel);
        }

        if (!retailItems.isEmpty()) {
            generateRetailOrder(postModel);
        }

        if (overPaymentAmount > 0) {
            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(overPaymentAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");
            if (defaultProvider != null) {
                paymentLineItem.setProviderID(defaultProvider.getGuid());
            }
            if (defaultLocation != null) {
                paymentLineItem.setLocationID(defaultLocation.getGuid());
            }
            postModel.addLineItem(paymentLineItem);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);

    }

    private void addPaymentObject(BalanceItemDTO balanceItem, IntegratedPaymentPostModel postModel) {
        if (balanceItem.getBalance() > 0) {
            IntegratedPaymentLineItem lineItem = new IntegratedPaymentLineItem();
            lineItem.setDescription(balanceItem.getDescription());
            lineItem.setAmount(balanceItem.getAmount());
            if (balanceItem.getResponsibilityType() != null) {
                //this is a responsibility item
                lineItem.setItemType(balanceItem.getResponsibilityType());
            } else if (balanceItem.isNewCharge()) {
                lineItem.setItemType(IntegratedPaymentLineItem.TYPE_NEWCHARGE);
                lineItem.setId(balanceItem.getId().toString());
            } else if (balanceItem.getId() != null) {
                lineItem.setItemType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                lineItem.setId(balanceItem.getId().toString());
            }

            if (balanceItem.getLocation() != null) {
                lineItem.setLocationID(balanceItem.getLocation().getGuid());
            }

            if (balanceItem.getProvider() != null) {
                lineItem.setProviderID(balanceItem.getProvider().getGuid());
            }

            double paymentAmount = balanceItem.getBalance();
            if (paymentAmount > balanceItem.getMaxAmount()) {
                overPaymentAmount = round(overPaymentAmount
                        + round(paymentAmount - balanceItem.getMaxAmount()));
                paymentAmount = balanceItem.getMaxAmount();
            }
            lineItem.setAmount(paymentAmount);

            postModel.addLineItem(lineItem);
        } else if (balanceItem.getBalance() < 0) {
            SystemUtil.showErrorToast(getContext(), Label.getLabel("negative_payment_amount_error"));
            hasPaymentError = true;
        }
    }

    private void generateRetailOrder(IntegratedPaymentPostModel postModel){
        RetailPostModelOrder retailOrder = new RetailPostModelOrder();
        double subtotal = 0D;
        double total = 0D;
        for (BalanceItemDTO balanceItemDTO : retailItems) {
            RetailItemDto retailItemDto = balanceItemDTO.getRetailPayload().getRetailItemDto();

            RetailOrderItem orderItem = new RetailOrderItem();
            orderItem.setName(retailItemDto.getName());
            orderItem.setSku(retailItemDto.getSku());

            double amount = round(retailItemDto.getPrice() +
                    retailItemDto.getPriceModification(balanceItemDTO.
                            getRetailPayload().getSelectedOptions()));
            int quantity = balanceItemDTO.getRetailPayload().getQuantity();
            subtotal = round(subtotal + (quantity * amount));
            total = round(total + (quantity * amount));//todo add tax amount & maybe shipping

            orderItem.setPrice(amount);
            orderItem.setQuantity(quantity);

            Map<Integer, RetailItemOptionChoiceDto> selectedOptions = balanceItemDTO.getRetailPayload().getSelectedOptions();
            for (int i=0; i<retailItemDto.getOptions().size(); i++) {
                if(selectedOptions.containsKey(i)) {
                    RetailSelectedOption selectedOption = new RetailSelectedOption();
                    selectedOption.setName(retailItemDto.getOptions().get(i).getName());
                    selectedOption.setValue(selectedOptions.get(i).getName());

                    orderItem.getSelectedOptions().add(selectedOption);
                }
            }

            retailOrder.getItems().add(orderItem);

            IntegratedPaymentLineItem retailPaymentLineItem = new IntegratedPaymentLineItem();
            retailPaymentLineItem.setAmount(round(amount * quantity));
            retailPaymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_RETAIL);
            retailPaymentLineItem.setLocationID(balanceItemDTO.getLocation().getGuid());
            retailPaymentLineItem.setProviderID(balanceItemDTO.getProvider().getGuid());
            retailPaymentLineItem.setRetailMetadata(new RetailLineItemMetadata());

            StringBuilder descriptionBuilder = new StringBuilder(retailItemDto.getName());
            if(!orderItem.getSelectedOptions().isEmpty()){
                descriptionBuilder.append(" -");
                for(RetailSelectedOption option : orderItem.getSelectedOptions()){
                    descriptionBuilder.append(" ")
                            .append(option.getName())
                            .append(": ")
                            .append(option.getValue())
                            .append(",");
                }
                descriptionBuilder.deleteCharAt(descriptionBuilder.length()-1);
            }
            if(quantity > 1){
                if(orderItem.getSelectedOptions().isEmpty()){
                    descriptionBuilder.append(" - ");
                }else{
                    descriptionBuilder.append(", ");
                }
                descriptionBuilder.append("Qty: ").append(quantity);
            }

            retailPaymentLineItem.setDescription(descriptionBuilder.toString());
            postModel.addLineItem(retailPaymentLineItem);
        }


        retailOrder.setSubTotal(subtotal);
        retailOrder.setTotal(total);

        if(patientDemographics != null){
            RetailBillingPerson billingPerson = retailOrder.getBillingPerson();
            billingPerson.setName(patientDemographics.getPersonalDetails().getFullName());
            billingPerson.setStreet(patientDemographics.getAddress().getAddress1());
            if (!StringUtil.isNullOrEmpty(patientDemographics.getAddress().getAddress2())) {
                billingPerson.setStreet(billingPerson.getStreet() + " " +
                        patientDemographics.getAddress().getAddress2());
            }
            billingPerson.setCity(patientDemographics.getAddress().getCity());
            billingPerson.setStateCode(patientDemographics.getAddress().getState());
            billingPerson.setPostalCode(patientDemographics.getAddress().getZipcode());
            billingPerson.setPhone(patientDemographics.getAddress().getPhone());
        }

        postModel.getMetadata().setOrder(retailOrder);
    }

    private static double round(double amount) {
        return (double) Math.round(amount * 100) / 100;
    }

    private boolean hasBalance() {
        return paymentsModel.getPaymentPayload().getPatientBalances().size() > 0
                && paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().size() > 0
                && paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getPayload().size() > 0;
    }

    @Override
    public void onHistoryAction() {
        callback.showPaymentHistory(paymentsModel);
        actionButton.setSelected(false);
        hideDialog();
    }

    @Override
    public void onAddChargeAction() {
        callback.lookupChargeItem(paymentsModel.getPaymentPayload().getSimpleChargeItems(),
                PaymentDistributionFragment.this);
        actionButton.setSelected(false);
        hideDialog();
    }

    @Override
    public void onAddRetailAction() {
        actionButton.setSelected(false);
        hideDialog();
        if (paymentsModel.getPaymentPayload().getRetailProducts().getProducts().getItems().isEmpty()) {
            UserPracticeDTO practiceDTO = callback.getPracticeInfo(paymentsModel);
            Map<String, String> queryMap = new HashMap<>();
            queryMap.put("practice_id", practiceDTO.getPracticeId());
            queryMap.put("practice_mgmt", practiceDTO.getPracticeMgmt());

            TransitionDTO getProducts = paymentsModel.getPaymentsMetadata().getPaymentsLinks().getProducts();
            getWorkflowServiceHelper().execute(getProducts, getRetailProductsCallback, queryMap);
        } else {
            callback.showRetailItems(paymentsModel, PaymentDistributionFragment.this);
        }
    }


    private WorkflowServiceCallback getRetailProductsCallback = new WorkflowServiceCallback() {
        @Override
        public void onPreExecute() {
            showProgressDialog();
        }

        @Override
        public void onPostExecute(WorkflowDTO workflowDTO) {
            hideProgressDialog();
            PaymentsModel retailPaymentsModel = DtoHelper.getConvertedDTO(PaymentsModel.class, workflowDTO);
            RetailProductsModel retailProductsModel = retailPaymentsModel.getPaymentPayload().getRetailProducts();
            paymentsModel.getPaymentPayload().setRetailProducts(retailProductsModel);
            callback.showRetailItems(paymentsModel, PaymentDistributionFragment.this);
        }

        @Override
        public void onFailure(String exceptionMessage) {
            hideProgressDialog();
            showErrorNotification(exceptionMessage);
        }
    };

}
