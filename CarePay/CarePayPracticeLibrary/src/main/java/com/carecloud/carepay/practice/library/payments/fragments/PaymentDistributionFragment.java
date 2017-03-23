package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentApplication;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentNewCharge;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentObject;
import com.carecloud.carepaylibray.payments.models.postmodel.PaymentPostModel;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/14/17.
 */

public class PaymentDistributionFragment extends BaseDialogFragment implements PaymentDistributionAdapter.PaymentDistributionCallback, PopupPickerAdapter.PopupPickCallback,
                                                                                AddPaymentItemFragment.AddItemCallback, PaymentDistributionEntryFragment.PaymentDistributionAmountCallback {

    private TextView patientName;
    private TextView balance;
    private TextView paymentTotal;
    private RecyclerView balanceDetailsRecycler;
    private Button payButton;

    private PopupPickerWindow locationPickerWindow;
    private PopupPickerWindow providerPickerWindow;

    private PaymentsModel paymentsModel;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();

    private PracticePaymentNavigationCallback callback;

    private double paymentAmount;
    private double balanceAmount;

    private NumberFormat currencyFormatter;

    private View lastSwipeView = null;

    private List<LocationDTO> locations;
    private List<ProviderDTO> providers;
    private LocationDTO defaultLocation;
    private ProviderDTO defaultProvider;

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
        setupToolbar(view);

        setupButtons(view);

        patientName = (TextView) view.findViewById(R.id.patient_name);
        balance = (TextView) view.findViewById(R.id.balance_value);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        balanceDetailsRecycler = (RecyclerView) view.findViewById(R.id.balances_recycler);
        balanceDetailsRecycler.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new SwipeHelperCallback());
        touchHelper.attachToRecyclerView(balanceDetailsRecycler);

        setInitialValues();
        setAdapter();
        setupPickerWindows();
        setDefaultProviderLocation();

    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(Label.getLabel("payment_title"));
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
                callback.onPaymentPlanAction();
            }
        });

        payButton = (Button) view.findViewById(R.id.payment_pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateBalanceItems()) {
                    generatePaymentsModel();
                    callback.onPayButtonClicked(paymentAmount, paymentsModel);
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

    }

    private void setInitialValues(){
        if(paymentsModel!=null){
            balanceAmount = calculateTotalBalance();
            paymentAmount = balanceAmount;

            setCurrency(balance, balanceAmount);
            setCurrency(paymentTotal, paymentAmount);

            String patientNameString = paymentsModel.getPaymentPayload().getPatientBalances().get(0).getDemographics().getPayload().getPersonalDetails().getFullName();
            patientName.setText(StringUtil.getLabelForView(patientNameString));
        }
    }

    private void setAdapter(){
        if(balanceDetailsRecycler.getAdapter()==null){
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), balanceItems, this);
            balanceDetailsRecycler.setAdapter(adapter);
        }else{
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) balanceDetailsRecycler.getAdapter();
            adapter.setBalanceItems(balanceItems);
            adapter.notifyDataSetChanged();
        }
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
                    total+=pendingBalancePayload.getAmount();
//                    total-=pendingBalancePayload.getUnappliedCredit();
                    switch (pendingBalancePayload.getType()){
                        case PendingBalancePayloadDTO.CO_PAY_TYPE:{
                            BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
                            balanceItemDTO.setBalance(pendingBalancePayload.getAmount());
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

    private void setDefaultProviderLocation(){
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
        }

        if(providerID!=null){
            for(ProviderDTO provider : providers){
                if(providerID.equals(provider.getId().toString())){
                    defaultProvider = provider;
                    break;
                }
            }
        }
    }

    private void modifyLineItem(BalanceItemDTO updateBalanceItem, ProviderDTO updateProvider, LocationDTO updateLocation, Double updateAmount){
        for(BalanceItemDTO balanceItem : balanceItems){
            if(balanceItem.equals(updateBalanceItem)){
                if(updateAmount!=null){
                    double difference;
                    double currentAmount = balanceItem.getBalance();

                    difference = currentAmount-updateAmount;
                    balanceItem.setBalance(updateAmount);
                    paymentAmount-=difference;
                    setCurrency(paymentTotal, paymentAmount);

                    payButton.setEnabled(paymentAmount>0);

                }

                if(updateProvider!=null){
                    balanceItem.setProvider(updateProvider);
                    balanceItem.setProviderId(updateProvider.getId());
                }

                if(updateLocation!=null){
                    balanceItem.setLocation(updateLocation);
                    balanceItem.setLocationId(updateLocation.getId());
                }
                setAdapter();
                return;
            }
        }
    }

    private void setCurrency(TextView textView, double amount){
        textView.setText(currencyFormatter.format(amount));
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
    public void addNewCharge(double amount, SimpleChargeItem chargeItem){
        BalanceItemDTO balanceItemDTO = new BalanceItemDTO();
        balanceItemDTO.setNewCharge(true);
        balanceItemDTO.setId(chargeItem.getId());
        balanceItemDTO.setDescription(chargeItem.getDescription());
        balanceItemDTO.setAmount(amount);
        balanceItemDTO.setBalance(amount);
        balanceItemDTO.setResponsibilityType(chargeItem.getResponsibilityType());
        if(defaultProvider!=null){
            balanceItemDTO.setProvider(defaultProvider);
        }
        if(defaultLocation!=null){
            balanceItemDTO.setLocation(defaultLocation);
        }
        balanceItems.add(balanceItemDTO);
        paymentAmount+=amount;
        setCurrency(paymentTotal, paymentAmount);
        setAdapter();
    }

    @Override
    public void addChargeItem(SimpleChargeItem chargeItem) {
        callback.showAmountEntry(this, null, chargeItem);
    }

    @Override
    public void applyNewDistributionAmount(double amount) {
        paymentAmount = amount;
        setCurrency(paymentTotal, amount);
        distributeAmountOverBalanceItems(amount);
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
        if(lastSwipeView !=null){
            lastSwipeView.setLeft(0);
        }
    }


    private void distributeAmountOverBalanceItems(double amount){
        balanceItems.clear();
        calculateTotalBalance();

        for (BalanceItemDTO balanceItem : balanceItems){
            double balance = balanceItem.getBalance();
            if(amount>=balance){
                amount-=balance;
            }else{
                balance = amount;
                balanceItem.setBalance(balance);
                if(amount > 0) {
                    amount -= balance;
                }
            }
        }

        setAdapter();
    }

    private boolean validateBalanceItems(){
        boolean isValid = true;
        for(BalanceItemDTO balanceItem : balanceItems){
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
        }

        if(!isValid){
            setAdapter();
        }
        return isValid;
    }

    private void generatePaymentsModel(){
        PaymentPostModel postModel = new PaymentPostModel();
        postModel.setAmount(paymentAmount);
        for(BalanceItemDTO balanceItemDTO : balanceItems){
            addPaymentObject(balanceItemDTO, postModel);
        }

        paymentsModel.getPaymentPayload().setPaymentPostModel(postModel);
    }

    private void addPaymentObject(BalanceItemDTO balanceItem, PaymentPostModel postModel){
        if(balanceItem.getBalance()>0){
            PaymentObject paymentObject = new PaymentObject();
            paymentObject.setAmount(balanceItem.getBalance());
            paymentObject.setDescription(balanceItem.getDescription());

            if(balanceItem.getResponsibilityType()!=null){
                //this is a responsibility item
                paymentObject.setResponsibilityType(balanceItem.getResponsibilityType());
            }else if(balanceItem.isNewCharge()){
                PaymentNewCharge paymentNewCharge = new PaymentNewCharge();
                paymentNewCharge.setChargeType(balanceItem.getId());
                paymentNewCharge.setAmount(balanceItem.getAmount());
                paymentObject.setPaymentNewCharge(paymentNewCharge);
            }else if(balanceItem.getId()!=null){
                PaymentApplication paymentApplication = new PaymentApplication();
                paymentApplication.setDebitTransactionID(balanceItem.getId());
                paymentObject.setPaymentApplication(paymentApplication);
            }

            if(balanceItem.getLocation()!=null){
                paymentObject.setLocationID(balanceItem.getLocation().getGuid());
            }

            if(balanceItem.getProvider()!=null){
                paymentObject.setProviderID(balanceItem.getProvider().getGuid());
            }

            postModel.addPaymentMethod(paymentObject);
        }
    }


    private class SwipeHelperCallback extends ItemTouchHelper.Callback {
        private float clearWidth = 0;
        private float maxSwipe = 0;

        private boolean bounceBack = false;

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if(viewHolder!=null) {
                maxSwipe = 0;
                clearLastSwipeView();
                clearPickers();
                PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;

                View swipeView = paymentViewHolder.getRowLayout();
                if(lastSwipeView == null || !lastSwipeView.equals(swipeView)){
                    lastSwipeView = swipeView;
                }

                getDefaultUIUtil().onSelected(swipeView);
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
            final PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
            getDefaultUIUtil().clearView(paymentViewHolder.getRowLayout());

        }


        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float distanceX, float distanceY, int actionState, boolean isCurrentlyActive) {
            PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
            View rowLayout = paymentViewHolder.getRowLayout();

            getDefaultUIUtil().onDraw(canvas, recyclerView, rowLayout, distanceX, distanceY, actionState, isCurrentlyActive);

            float swipeDistance = Math.abs(distanceX);
            if( swipeDistance > maxSwipe){
                maxSwipe = swipeDistance;
            }
        }


        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//            clearLastSwipeView();
            recyclerView.setOnTouchListener(swipeTouchListener);
            getMaxDistance(viewHolder);
            return makeMovementFlags(0, ItemTouchHelper.LEFT);
        }

        @Override
        public int convertToAbsoluteDirection(int flags, int layoutDirection) {
            return bounceBack ? 0 : super.convertToAbsoluteDirection(flags, layoutDirection);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        private View.OnTouchListener swipeTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    bounceBack = true;
                    if (maxSwipe > clearWidth) {
                        lastSwipeView.setLeft((int)-clearWidth);
                    }
                } else {
                    bounceBack = false;
                }
                return false;
            }
        };


        private void getMaxDistance(RecyclerView.ViewHolder viewHolder) {
            if(clearWidth == 0) {
                PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
                clearWidth = paymentViewHolder.getClearButton().getMeasuredWidth();
            }
        }



    }
}
