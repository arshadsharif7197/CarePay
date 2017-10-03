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
import com.carecloud.carepay.practice.library.payments.dialogs.PopupPickerWindow;
import com.carecloud.carepay.practice.library.payments.interfaces.PracticePaymentNavigationCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.carecloud.carepaylibray.payments.models.SimpleChargeItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentLineItem;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.BounceHelper;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/14/17
 */

public class PaymentDistributionFragment extends BaseDialogFragment implements PaymentDistributionAdapter.PaymentDistributionCallback, PopupPickerAdapter.PopupPickCallback,
        AddPaymentItemFragment.AddItemCallback, PaymentDistributionEntryFragment.PaymentDistributionAmountCallback, BounceHelper.BounceHelperListener {

    private TextView balance;
    private TextView paymentTotal;
    private TextView unapplied;
    private View unappliedLayout;

    private NestedScrollView scrollView;
    private RecyclerView balanceDetailsRecycler;
    private View newChargesLayout;
    private RecyclerView newChargesRecycler;
    private Button payButton;

    private BounceHelper balanceViewSwipeHelper;
    private BounceHelper chargeViewSwipeHelper;

    private PopupPickerWindow locationPickerWindow;
    private PopupPickerWindow providerPickerWindow;

    private PaymentsModel paymentsModel;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();
    private List<BalanceItemDTO> chargeItems = new ArrayList<>();

    private PracticePaymentNavigationCallback callback;

    private double paymentAmount;
    private double balanceAmount;
    private double chargesAmount;
    private double overPaymentAmount;
    private double unappliedCredit = 0D;
    private double originalUnapplied = 0D;

    private NumberFormat currencyFormatter;

    private List<LocationDTO> locations;
    private List<ProviderDTO> providers;
    private LocationDTO defaultLocation;
    private ProviderDTO defaultProvider;

    private boolean hasPaymentError = false;

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PracticePaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PracticePaymentNavigationCallback");
        }
    }

    @Override
    public void onCreate(Bundle icicle){
        super.onCreate(icicle);
        currencyFormatter = NumberFormat.getCurrencyInstance();

        Bundle args = getArguments();
        if(args!=null){
            Gson gson = new Gson();
            String paymentPayload = args.getString(CarePayConstants.PAYMENT_PAYLOAD_BUNDLE);
            paymentsModel = gson.fromJson(paymentPayload, PaymentsModel.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle){
        return inflater.inflate(R.layout.fragment_payment_distribution, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle icicle){

        setupButtons(view);

        balance = (TextView) view.findViewById(R.id.balance_value);
        unapplied = (TextView) view.findViewById(R.id.unapplied_value);

        unappliedLayout = view.findViewById(R.id.unapplied_layout);

        scrollView = (NestedScrollView) view.findViewById(R.id.nested_scroller);

        RecyclerView.LayoutManager balanceLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        balanceDetailsRecycler = (RecyclerView) view.findViewById(R.id.balances_recycler);
        balanceDetailsRecycler.setLayoutManager(balanceLayoutManager);
        balanceDetailsRecycler.addOnScrollListener(scrollListener);

        balanceViewSwipeHelper = new BounceHelper(this);
        ItemTouchHelper balanceTouchHelper = new ItemTouchHelper(balanceViewSwipeHelper);
        balanceTouchHelper.attachToRecyclerView(balanceDetailsRecycler);


        newChargesLayout = view.findViewById(R.id.new_charges_layout);
        RecyclerView.LayoutManager chargesLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        newChargesRecycler = (RecyclerView) view.findViewById(R.id.new_charges_recycler);
        newChargesRecycler.setLayoutManager(chargesLayoutManager);
        newChargesRecycler.addOnScrollListener(scrollListener);

        chargeViewSwipeHelper = new BounceHelper(this);
        ItemTouchHelper chargesTouchHelper = new ItemTouchHelper(chargeViewSwipeHelper);
        chargesTouchHelper.attachToRecyclerView(newChargesRecycler);

        setInitialValues(view);
        setAdapter();
        setupPickerWindows();
        setDefaultProviderLocation();

        View container = view.findViewById(R.id.container_main);
        container.setSoundEffectsEnabled(false);
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPickers();
                clearLastSwipeView();
            }
        });

    }

    private void setupToolbar(View view, String titleString){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(titleString);
        }
    }

    private void setupButtons(View view){
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button addButton = (Button) view.findViewById(R.id.add_item_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.lookupChargeItem(paymentsModel.getPaymentPayload().getSimpleChargeItems(), PaymentDistributionFragment.this);
            }
        });

        Button leftButton = (Button) view.findViewById(R.id.payment_left_button);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onPaymentPlanAction(paymentsModel);
            }
        });

        payButton = (Button) view.findViewById(R.id.payment_pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateBalanceItems()) {
                    distributeAmountOverBalanceItems(paymentAmount);

                    generatePaymentsModel();
                    if(!hasPaymentError) {
                        callback.onPayButtonClicked(round(paymentAmount + chargesAmount), paymentsModel);
                        hideDialog();
                    }
                }
            }
        });

        paymentTotal = (TextView) view.findViewById(R.id.payment_value);
        paymentTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showAmountEntry(PaymentDistributionFragment.this, null, null);
            }
        });

        View historyButton = view.findViewById(R.id.button_history);
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.showPaymentHistory(paymentsModel);
                hideDialog();
            }
        });

    }

    private void setInitialValues(View view){
        if(paymentsModel!=null){
            balanceAmount = calculateTotalBalance();
            paymentAmount = balanceAmount;

            setCurrency(balance, balanceAmount);
            updatePaymentAmount();

            PatientBalanceDTO patientBalanceDTO = null;
            if(!paymentsModel.getPaymentPayload().getPatientBalances().isEmpty()){
                patientBalanceDTO = paymentsModel.getPaymentPayload().getPatientBalances().get(0);
            }

            if(patientBalanceDTO!=null) {
                String patientNameString = patientBalanceDTO.getDemographics().getPayload().getPersonalDetails().getFullName();

                patientNameString = StringUtil.capitalize(patientNameString);
                setupToolbar(view, StringUtil.getLabelForView(patientNameString));

                try {
                    unappliedCredit = Double.parseDouble(patientBalanceDTO.getUnappliedCredit()) *-1;
                }catch (NumberFormatException nfe){
                    nfe.printStackTrace();
                }
            }

            if(unappliedCredit > 0D){
                originalUnapplied = unappliedCredit;
                unappliedLayout.setVisibility(View.VISIBLE);
                setCurrency(unapplied, unappliedCredit);
            }else{
                unappliedLayout.setVisibility(View.GONE);
            }

            setMaxAmounts();
        }
    }

    private void setAdapter(){
        if(balanceDetailsRecycler.getAdapter()==null){
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), balanceItems, this, PaymentDistributionAdapter.PaymentRowType.BALANCE);
            balanceDetailsRecycler.setAdapter(adapter);
        }else{
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) balanceDetailsRecycler.getAdapter();
            adapter.setBalanceItems(balanceItems);
            adapter.notifyDataSetChanged();
        }

        if(newChargesRecycler.getAdapter() == null){
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), chargeItems, this, PaymentDistributionAdapter.PaymentRowType.NEW_CHARGE);
            newChargesRecycler.setAdapter(adapter);
        }else{
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) newChargesRecycler.getAdapter();
            adapter.setBalanceItems(chargeItems);
            adapter.notifyDataSetChanged();
        }
    }

    private void scrollAdapterToItem(BalanceItemDTO balanceItemDTO){
        int position;
        int locationY;
        position= balanceItems.indexOf(balanceItemDTO);
        if(position > 0){
            locationY = (int) balanceDetailsRecycler.getChildAt(position).getY();
        }else{
            //check the charges adapter
            position = chargeItems.indexOf(balanceItemDTO);
            if(position < 0){
                //not found
                return;
            }
            locationY = (int) newChargesRecycler.getChildAt(position).getY();
        }
        Log.d("RecyclerView", "Scroll to Position: "+position+" at: "+locationY);
        scrollView.smoothScrollTo(0, locationY+10);

    }

    private void setupPickerWindows(){
        locationPickerWindow = new PopupPickerWindow(getContext());
        locations = paymentsModel.getPaymentPayload().getLocations();
        PopupPickLocationAdapter locationAdapter = new PopupPickLocationAdapter(getContext(), locations, this);
        locationPickerWindow.setAdapter(locationAdapter);

        providerPickerWindow = new PopupPickerWindow(getContext());
        providers = paymentsModel.getPaymentPayload().getProviders();
        PopupPickProviderAdapter providerAdapter = new PopupPickProviderAdapter(getContext(), providers, this);
        providerPickerWindow.setAdapter(providerAdapter);
    }

    private double calculateTotalBalance(){
        double total = 0D;
        for(PatientBalanceDTO patientBalance : paymentsModel.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalance : patientBalance.getBalances()){
                for(PendingBalancePayloadDTO pendingBalancePayload : pendingBalance.getPayload()){
                    total = round(total + pendingBalancePayload.getAmount());
                    switch (pendingBalancePayload.getType()){
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:
                        case PendingBalancePayloadDTO.CO_INSURANCE_TYPE:
                        case PendingBalancePayloadDTO.DEDUCTIBLE_TYPE:
                        {
                            double amount = pendingBalancePayload.getAmount();
                            BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
                            balanceItemDTO.setBalance(amount);
                            balanceItemDTO.setAmount(amount);
                            balanceItemDTO.setDescription(pendingBalancePayload.getType());

                            balanceItems.add(balanceItemDTO);
                            break;
                        }
                        default:{
                            balanceItems.addAll(pendingBalancePayload.getDetails());
                        }
                    }
                }
            }
        }

        return total;
    }

    private void setMaxAmounts(){
        for(BalanceItemDTO balanceItemDTO : balanceItems){
            balanceItemDTO.setMaxAmount(balanceItemDTO.getBalance());
        }
    }

    private void resetInitialAmounts(){
        for(BalanceItemDTO balanceItemDTO : balanceItems){
            balanceItemDTO.setBalance(balanceItemDTO.getMaxAmount());
        }
        unappliedCredit = originalUnapplied;
    }

    private void setDefaultProviderLocation(){
        if(paymentsModel.getPaymentPayload().getPatientBalances().isEmpty() ||
                paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().isEmpty()){
            return;
        }
        String patientID = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getBalances().get(0).getMetadata().getPatientId();
        String locationID = null;
        String providerID = null;
        for(LocationIndexDTO locationIndex : paymentsModel.getPaymentPayload().getLocationIndex()){
            for(String locationPatientID : locationIndex.getPatientIds()){
                if(locationPatientID.equals(patientID)){
                    locationID = locationIndex.getId();
                    break;
                }
            }
        }
        for(ProviderIndexDTO providerIndex : paymentsModel.getPaymentPayload().getProviderIndex()){
            for(String providerPatientID : providerIndex.getPatientIds()){
                if(providerPatientID.equals(patientID)){
                    providerID = providerIndex.getId();
                    break;
                }
            }
        }

        if(locationID!=null){
            for(LocationDTO location : locations){
                if(locationID.equals(location.getId().toString())){
                    defaultLocation = location;
                    break;
                }
            }
        }else if(locations.size() == 1){
            defaultLocation = locations.get(0);
        }

        if(providerID!=null){
            for(ProviderDTO provider : providers){
                if(providerID.equals(provider.getId().toString())){
                    defaultProvider = provider;
                    break;
                }
            }
        }else if(providers.size() == 1){
            defaultProvider = providers.get(0);
        }
    }

    private void modifyLineItem(BalanceItemDTO balanceItem, ProviderDTO updateProvider, LocationDTO updateLocation, Double updateAmount){
        if(updateAmount!=null){
            double difference;
            double currentAmount = balanceItem.getBalance();

            difference = round(currentAmount-updateAmount);

            if(paymentAmount <= balanceAmount && unappliedCredit > 0) {
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

        if(updateProvider!=null){
            balanceItem.setProvider(updateProvider);
            balanceItem.setProviderId(updateProvider.getId());
        }

        if(updateLocation!=null){
            balanceItem.setLocation(updateLocation);
            balanceItem.setLocationId(updateLocation.getId());
        }

        int index = balanceItems.indexOf(balanceItem);
        if(index >= 0){
            balanceDetailsRecycler.getAdapter().notifyItemChanged(index);
        }else{
            index = chargeItems.indexOf(balanceItem);
            if(index >= 0){
                newChargesRecycler.getAdapter().notifyItemChanged(index);
            }
        }

    }


    private void updatePaymentAmount(){
        if(paymentAmount >= balanceAmount){
            paymentTotal.setBackgroundResource(R.drawable.bg_green_border_trans);
            paymentTotal.setTextColor(ContextCompat.getColor(getContext(), R.color.emerald));
        }else{
            paymentTotal.setBackgroundResource(R.drawable.bg_orange_border_trans);
            paymentTotal.setTextColor(ContextCompat.getColor(getContext(), R.color.lightning_yellow));
        }

        double totalAmount = round(paymentAmount + chargesAmount);
        payButton.setEnabled(totalAmount > 0);

        setCurrency(paymentTotal, totalAmount);
    }

    private void setCurrency(TextView textView, double amount){
        textView.setText(currencyFormatter.format(amount));
    }

    private void setChargeLayoutVisibility(){
        newChargesLayout.setVisibility(chargeItems.isEmpty()?View.GONE:View.VISIBLE);
    }

    private RecyclerView.OnScrollListener scrollListener =  new RecyclerView.OnScrollListener() {
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
        clearPickers();
        clearLastSwipeView();
        int offset = view.getWidth()/2-providerPickerWindow.getWidth()/2;
        providerPickerWindow.setBalanceItemDTO(balanceItem);
        providerPickerWindow.showAsDropDown(view, offset, 10);
    }

    @Override
    public void pickProvider(ProviderDTO provider, BalanceItemDTO balanceItem) {
        clearPickers();
        modifyLineItem(balanceItem, provider, null, null);
    }

    @Override
    public void pickLocation(View view, BalanceItemDTO balanceItem) {
        clearPickers();
        clearLastSwipeView();
        int offset = view.getWidth()/2-locationPickerWindow.getWidth()/2;
        locationPickerWindow.setBalanceItemDTO(balanceItem);
        locationPickerWindow.showAsDropDown(view, offset, 10);
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
    public void addNewCharge(double amount, SimpleChargeItem chargeItem){
        BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
        balanceItemDTO.setNewCharge(true);
        balanceItemDTO.setId(chargeItem.getId());
        balanceItemDTO.setDescription(chargeItem.getDescription());
        balanceItemDTO.setAmount(amount);
        balanceItemDTO.setBalance(amount);
        balanceItemDTO.setMaxAmount(amount);
        balanceItemDTO.setResponsibilityType(chargeItem.getResponsibilityType());
        if(defaultProvider!=null){
            balanceItemDTO.setProvider(defaultProvider);
        }
        if(defaultLocation!=null){
            balanceItemDTO.setLocation(defaultLocation);
        }
        chargeItems.add(balanceItemDTO);
        chargesAmount = round(chargesAmount + amount);
        updatePaymentAmount();
        setAdapter();
        setChargeLayoutVisibility();
    }

    @Override
    public void addChargeItem(SimpleChargeItem chargeItem) {
        callback.showAmountEntry(this, null, chargeItem);
    }

    @Override
    public void applyNewDistributionAmount(double amount) {
        paymentAmount = amount;
        chargesAmount = 0D;
        updatePaymentAmount();

        resetInitialAmounts();
        balanceItems.clear();
        chargeItems.clear();
        calculateTotalBalance();

        distributeAmountOverBalanceItems(amount);

        setAdapter();
        setChargeLayoutVisibility();
    }

    @Override
    public void applyAmountToBalanceItem(double amount, BalanceItemDTO balanceItem){
        modifyLineItem(balanceItem, null, null, amount);
        setAdapter();
    }


    private void clearPickers(){
        providerPickerWindow.dismiss();
        locationPickerWindow.dismiss();
    }

    private void clearLastSwipeView(){
        balanceViewSwipeHelper.clearLastSwipeView();
        chargeViewSwipeHelper.clearLastSwipeView();
    }


    private void distributeAmountOverBalanceItems(double amount){

        for (BalanceItemDTO balanceItem : balanceItems){
            double balance = balanceItem.getBalance();
            if(balance <= 0){//this should be skipped and cleared
                balanceItem.setBalance(0);
            }else {
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

        if(amount > 0){//there is some amount left over for the payment
            overPaymentAmount = amount;
        }
    }

    private boolean validateBalanceItems(){
        boolean isValid = true;
        BalanceItemDTO firstInvalidItem = null;
        for(BalanceItemDTO balanceItem : balanceItems){
            if(!isValidItem(balanceItem)){
                isValid = false;
                if(firstInvalidItem == null){
                    firstInvalidItem = balanceItem;
                }
            }
        }

        for(BalanceItemDTO chargeItem : chargeItems){
            if(!isValidItem(chargeItem)){
                isValid = false;
                if(firstInvalidItem == null){
                    firstInvalidItem = chargeItem;
                }
            }
        }

        if(!isValid){
            setAdapter();
        }

        if(firstInvalidItem!=null){
            scrollAdapterToItem(firstInvalidItem);
        }
        return isValid;
    }

    private boolean isValidItem(BalanceItemDTO balanceItem){
        boolean isValid = true;
        if(balanceItem.getBalance() > 0) {
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

    private void generatePaymentsModel(){
        hasPaymentError = false;
        IntegratedPaymentPostModel postModel = new IntegratedPaymentPostModel();
        postModel.setAmount( round(paymentAmount + chargesAmount));
        for(BalanceItemDTO balanceItemDTO : balanceItems){
            addPaymentObject(balanceItemDTO, postModel);
        }
        for(BalanceItemDTO balanceItemDTO : chargeItems){
            addPaymentObject(balanceItemDTO, postModel);
        }

        if(overPaymentAmount > 0){
            IntegratedPaymentLineItem paymentLineItem = new IntegratedPaymentLineItem();
            paymentLineItem.setAmount(overPaymentAmount);
            paymentLineItem.setItemType(IntegratedPaymentLineItem.TYPE_UNAPPLIED);
            paymentLineItem.setDescription("Unapplied Amount");

            postModel.addLineItem(paymentLineItem);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);

    }

    private void addPaymentObject(BalanceItemDTO balanceItem, IntegratedPaymentPostModel postModel){
        if(balanceItem.getBalance()>0){
            IntegratedPaymentLineItem lineItem = new IntegratedPaymentLineItem();
            lineItem.setDescription(balanceItem.getDescription());
            lineItem.setAmount(balanceItem.getAmount());
            if(balanceItem.getResponsibilityType()!=null){
                //this is a responsibility item
                lineItem.setItemType(balanceItem.getResponsibilityType());
            }else if(balanceItem.isNewCharge()){
                lineItem.setItemType(IntegratedPaymentLineItem.TYPE_NEWCHARGE);
                lineItem.setId(balanceItem.getId().toString());
            }else if(balanceItem.getId()!=null){
                lineItem.setItemType(IntegratedPaymentLineItem.TYPE_APPLICATION);
                lineItem.setId(balanceItem.getId().toString());
            }

            if(balanceItem.getLocation()!=null){
                lineItem.setLocationID(balanceItem.getLocation().getGuid());
            }

            if(balanceItem.getProvider()!=null){
                lineItem.setProviderID(balanceItem.getProvider().getGuid());
            }

            double paymentAmount = balanceItem.getBalance();
            if(paymentAmount > balanceItem.getMaxAmount()){
                overPaymentAmount = round(overPaymentAmount + round(paymentAmount - balanceItem.getMaxAmount()));
                paymentAmount = balanceItem.getMaxAmount();
            }
            lineItem.setAmount(paymentAmount);

            postModel.addLineItem(lineItem);
        }else if(balanceItem.getBalance()<0){
            SystemUtil.showErrorToast(getContext(), Label.getLabel("negative_payment_amount_error"));
            hasPaymentError = true;
        }
    }

    private static double round(double amount){
        return (double) Math.round(amount * 100) / 100;
    }

}
