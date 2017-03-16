package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.models.BalanceItemDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.BaseDialogFragment;
import com.carecloud.carepaylibray.payments.PaymentNavigationCallback;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsLabelDTO;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.PendingBalanceDTO;
import com.carecloud.carepaylibray.payments.models.PendingBalancePayloadDTO;
import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lmenendez on 3/14/17.
 */

public class PaymentDistributionFragment extends BaseDialogFragment implements PaymentDistributionAdapter.PaymentDistributionCallback, PopupPickerAdapter.PopupPickCallback {

    private TextView patientName;
    private TextView balance;
    private TextView paymentTotal;
    private RecyclerView balanceDetailsRecycler;

    private PopupPickerWindow locationPickerWindow;
    private PopupPickerWindow providerPickerWindow;

    private PaymentsModel paymentsModel;
    private PaymentsLabelDTO labels;
    private List<BalanceItemDTO> balanceItems = new ArrayList<>();

    private PaymentNavigationCallback callback;

    private double paymentAmount;
    private double balanceAmount;

    private NumberFormat currencyFormatter;


    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        try{
            callback = (PaymentNavigationCallback) context;
        }catch (ClassCastException cce){
            throw new ClassCastException("Attached context must implement PaymentNavigationCallback");
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
            labels = paymentsModel.getPaymentsMetadata().getPaymentsLabel();
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

        TextView balanceLabel = (TextView) view.findViewById(R.id.balance_label);
        balanceLabel.setText(StringUtil.getLabelForView(labels.getPaymentBalanceOwedLabel()));
        TextView paymentLabel = (TextView) view.findViewById(R.id.payment_label);
        paymentLabel.setText(StringUtil.getLabelForView(labels.getPaymentTotalPaidLabel()));

        patientName = (TextView) view.findViewById(R.id.patient_name);
        balance = (TextView) view.findViewById(R.id.balance_value);
        paymentTotal = (TextView) view.findViewById(R.id.payment_value);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        balanceDetailsRecycler = (RecyclerView) view.findViewById(R.id.balances_recycler);
        balanceDetailsRecycler.setLayoutManager(layoutManager);

        ItemTouchHelper touchHelper = new ItemTouchHelper(swipeCallback);
        touchHelper.attachToRecyclerView(balanceDetailsRecycler);

        setInitialValues();
        setAdapter();
        setupPickerWindows();
    }

    private void setupToolbar(View view){
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.payment_toolbar);
        if(toolbar!=null){
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);
            title.setText(StringUtil.getLabelForView(labels.getPaymentPatientBalanceToolbar()));
        }
    }

    private void setupButtons(View view){
        View closeButton = view.findViewById(R.id.closeViewLayout);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        Button addButton = (Button) view.findViewById(R.id.add_item_button);
        addButton.setText(StringUtil.getLabelForView(labels.getPaymentAddItemButton()));
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callback.//TODO will probably need to add a new callback function where I can pass line items
            }
        });

        Button leftButton = (Button) view.findViewById(R.id.payment_left_button);
        leftButton.setText(StringUtil.getLabelForView(labels.getPracticePaymentsDetailDialogPaymentPlan()));
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPaymentPlanAction();
            }
        });

        Button payButton = (Button) view.findViewById(R.id.payment_pay_button);
        payButton.setText(StringUtil.getLabelForView(labels.getPracticePaymentsDetailDialogPay()));
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onPayButtonClicked(paymentAmount, paymentsModel);
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
            PaymentDistributionAdapter adapter = new PaymentDistributionAdapter(getContext(), balanceItems, this, StringUtil.getLabelForView(labels.getPaymentClearButton()));
            balanceDetailsRecycler.setAdapter(adapter);
        }else{
            PaymentDistributionAdapter adapter = (PaymentDistributionAdapter) balanceDetailsRecycler.getAdapter();
            adapter.setBalanceItems(balanceItems);
            adapter.notifyDataSetChanged();
        }
    }

    private void setupPickerWindows(){
        locationPickerWindow = new PopupPickerWindow(getContext());
        List<LocationDTO> locations = paymentsModel.getPaymentPayload().getLocations();
        PopupPickLocationAdapter locationAdapter = new PopupPickLocationAdapter(getContext(), locations, this);
        locationPickerWindow.setAdapter(locationAdapter);

        providerPickerWindow = new PopupPickerWindow(getContext());
        List<ProviderDTO> providers = paymentsModel.getPaymentPayload().getProviders();
        PopupPickProviderAdapter providerAdapter = new PopupPickProviderAdapter(getContext(), providers, this);
        providerPickerWindow.setAdapter(providerAdapter);
    }

    private double calculateTotalBalance(){
        double total = 0D;
        for(PatientBalanceDTO patientBalance : paymentsModel.getPaymentPayload().getPatientBalances()){
            for(PendingBalanceDTO pendingBalance : patientBalance.getBalances()){
                for(PendingBalancePayloadDTO pendingBalancePayload : pendingBalance.getPayload()){
                    total+=pendingBalancePayload.getAmount();
                    total-=pendingBalancePayload.getUnappliedCredit();
                    balanceItems.addAll(pendingBalancePayload.getDetails());
                }
            }
        }
        return total;
    }

    private void modifyLineItem(BalanceItemDTO updateBalanceItem, ProviderDTO updateProvider, LocationDTO updateLocation, Double updateAmount){
        for(BalanceItemDTO balanceItem : balanceItems){
            if(balanceItem.equals(updateBalanceItem)){
                if(updateAmount!=null){
                    double difference;
                    double currentAmount = 0;
                    try{
                        currentAmount = Double.parseDouble(balanceItem.getBalance());
                    }catch (NumberFormatException nfe){
                        nfe.printStackTrace();
                    }
                    difference = currentAmount-updateAmount;
                    balanceItem.setBalance(updateAmount.toString());
                    paymentAmount-=difference;
                    setCurrency(paymentTotal, paymentAmount);
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
        providerPickerWindow.showAsDropDown(view);
    }

    @Override
    public void pickLocation(View view, BalanceItemDTO balanceItem) {
        clearPickers();
        locationPickerWindow.showAsDropDown(view);
    }

    @Override
    public void editAmount(double amount, BalanceItemDTO balanceItem) {
        modifyLineItem(balanceItem, null, null, amount);
    }


    private ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        private float MAX_DISTANCE = -1F;

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (viewHolder != null){
                PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
                View rowLayout = paymentViewHolder.getRowLayout();

//                getDefaultUIUtil().onSelected(rowLayout);
            }
        }


        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//            PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
//            View clearButton = paymentViewHolder.getClearButton();
//            View rowLayout = paymentViewHolder.getRowLayout();

//            if(direction == ItemTouchHelper.LEFT) {
//                showClearButton(clearButton, rowLayout);
//            }else if (direction == ItemTouchHelper.RIGHT){
//                hideClearButton(clearButton, rowLayout);
//            }

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float distanceX, float distanceY, int actionState, boolean isCurrentlyActive) {
            PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
            View rowLayout = paymentViewHolder.getRowLayout();

            distanceX = getMaxDistance(viewHolder, distanceX);

//            drawBackground(viewHolder, distanceX, actionState);

//            getDefaultUIUtil().onDraw(c, recyclerView, rowLayout, distanceX, distanceY, actionState, isCurrentlyActive);

            rowLayout.setTranslationX(distanceX);

            if(distanceX >= MAX_DISTANCE){
                modifyLineItem(paymentViewHolder.getBalanceItem(), null, null, 0D);
            }

        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder){
            PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
            View clearButton = paymentViewHolder.getClearButton();
            View rowLayout = paymentViewHolder.getRowLayout();

//            clearButton.setRight(0);

//            getDefaultUIUtil().clearView(rowLayout);
        }


        private void drawBackground(RecyclerView.ViewHolder viewHolder, float distanceX, int actionState) {
            PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
            View clearButton = paymentViewHolder.getClearButton();

            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                //noinspection NumericCastThatLosesPrecision
                clearButton.setRight((int) Math.max(distanceX, 0));
            }
        }

        private float getMaxDistance(RecyclerView.ViewHolder viewHolder, float distanceX) {
            if(MAX_DISTANCE < 0) {
                PaymentDistributionAdapter.PaymentDistributionViewHolder paymentViewHolder = (PaymentDistributionAdapter.PaymentDistributionViewHolder) viewHolder;
                View clearButton = paymentViewHolder.getClearButton();
                MAX_DISTANCE = paymentViewHolder.getClearButton().getMeasuredWidth() * 2;
            }
            if(Math.abs(distanceX)<MAX_DISTANCE){
                return distanceX;
            }else{
                if(distanceX<0) {
                    return -MAX_DISTANCE;
                }else{
                    return MAX_DISTANCE;
                }
            }
        }
    };


    private void showClearButton(View clearButton, View rowLayout){
        clearButton.setVisibility(View.VISIBLE);
        clearButton.measure(0, 0);
        int width = clearButton.getMeasuredWidth();
        float left = rowLayout.getTranslationX();
        rowLayout.setTranslationX(left-width);
    }

    private void hideClearButton(View clearButton, View rowLayout){
        clearButton.setVisibility(View.GONE);
        rowLayout.setLeft(0);
    }

    private void clearPickers(){
        providerPickerWindow.dismiss();
        locationPickerWindow.dismiss();
    }

    @Override
    public void pickLocation(LocationDTO location, BalanceItemDTO balanceItem) {
        clearPickers();
        modifyLineItem(balanceItem, null, location, null);
    }

    @Override
    public void pickProvider(ProviderDTO provider, BalanceItemDTO balanceItem) {
        clearPickers();
        modifyLineItem(balanceItem, provider, null, null);
    }


}
