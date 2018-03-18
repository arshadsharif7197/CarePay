package com.carecloud.carepay.practice.library.payments.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.carecloud.carepay.practice.library.R;
import com.carecloud.carepay.practice.library.payments.CloverPaymentAdapter;
import com.carecloud.carepay.practice.library.payments.interfaces.ShamrockPaymentsCallback;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepay.service.library.RestCallServiceCallback;
import com.carecloud.carepay.service.library.RestCallServiceHelper;
import com.carecloud.carepay.service.library.RestDef;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.models.postmodel.IntegratedPaymentPostModel;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PracticePaymentMethodFragment extends PaymentMethodFragment {

    private ShamrockPaymentsCallback shamrockCallback;

    /**
     * @param paymentsModel the payments model
     * @param amount        the amount
     * @return an instance of PracticePaymentMethodFragment
     */
    public static PracticePaymentMethodFragment newInstance(PaymentsModel paymentsModel, double amount) {
        Bundle args = new Bundle();

        Gson gson = new Gson();
        String paymentsDTOString = gson.toJson(paymentsModel);
        args.putString(CarePayConstants.INTAKE_BUNDLE, paymentsDTOString);

        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);

        PracticePaymentMethodFragment fragment = new PracticePaymentMethodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method_practice, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        setSwipeCardNowVisibility(view);
    }

    @Override
    public void attachCallback(Context context){
        super.attachCallback(context);
        try{
            shamrockCallback = (ShamrockPaymentsCallback) context;
        }catch (ClassCastException cce){
            cce.printStackTrace();
        }
    }

    @Override
    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
    }

    private void setSwipeCardNowVisibility(View view) {
        final boolean isCloverDevice = HttpConstants.getDeviceInformation().getDeviceType().equals(CarePayConstants.CLOVER_DEVICE);
        final boolean isPracticeMode = getApplicationMode().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE;
        Button swipeCreditCarNowButton = (Button) view.findViewById(R.id.swipeCreditCarNowButton);
        View swipeCreditCardNowLayout = view.findViewById(R.id.swipeCreditCardNowLayout);
        swipeCreditCarNowButton.setEnabled(isCloverDevice || isPracticeMode);
        swipeCreditCardNowLayout.setVisibility(isCloverDevice || isPracticeMode ? View.VISIBLE : View.GONE);
        swipeCreditCarNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isCloverDevice) {
                    handleSwipeCard();
                }else if(isPracticeMode){
                    handleIntegratedPayment();
                }
            }
        });

    }

    protected void handleSwipeCard() {
        CloverPaymentAdapter cloverPaymentAdapter = new CloverPaymentAdapter(getActivity(), paymentsModel, callback.getAppointmentId());
        IntegratedPaymentPostModel paymentPostModel = paymentsModel.getPaymentPayload().getPaymentPostModel();
        if (paymentPostModel == null) {
            cloverPaymentAdapter.setCloverPayment(amountToMakePayment);
        } else {
            cloverPaymentAdapter.setCloverPayment(paymentPostModel);
        }
        logPaymentMethodSelection(getString(R.string.payment_clover));
    }

    private void checkIntegratedPayments(){
        String endpoint = String.format(getString(R.string.carepay_locations), getApplicationMode().getUserPracticeDTO().getPracticeId());
        String url = HttpConstants.getPaymentsUrl();

        Map<String, String> headers = new HashMap<>();
        headers.put("x-api-key", HttpConstants.getPaymentsApiKey());

        RestCallServiceHelper restCallServiceHelper = new RestCallServiceHelper(getAppAuthorizationHelper(), getApplicationMode());
        restCallServiceHelper.executeRequest(RestDef.GET, url, integratedPaymentsReadyCallback, true, false, null, null, headers, "", endpoint);
    }

    protected void handleIntegratedPayment(){
        if(shamrockCallback != null) {
            shamrockCallback.showChooseDeviceList(paymentsModel, amountToMakePayment);
            logPaymentMethodSelection(getString(R.string.payment_clover));
        }
    }

    private RestCallServiceCallback integratedPaymentsReadyCallback = new RestCallServiceCallback() {
        @Override
        public void onPreExecute() {

        }

        @Override
        public void onPostExecute(JsonElement jsonElement) {
            Log.d("Integrated Callback", jsonElement.toString());

        }

        @Override
        public void onFailure(String errorMessage) {

        }
    };
}