package com.carecloud.carepay.patient.payment.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.payment.PaymentConstants;
import com.carecloud.carepay.patient.payment.androidpay.AndroidPayAdapter;
import com.carecloud.carepay.patient.payment.interfaces.PatientPaymentMethodInterface;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.fragments.PaymentMethodFragment;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.payments.presenter.PaymentViewHandler;
import com.carecloud.carepaylibray.utils.DtoHelper;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.WalletConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class PatientPaymentMethodFragment extends PaymentMethodFragment implements AndroidPayAdapter.AndroidPayReadyCallback {

    //Patient Specific Stuff
    private ProgressBar paymentMethodFragmentProgressBar;
    private PatientPaymentMethodInterface callback;

    private AndroidPayAdapter androidPayAdapter;

    /**
     * @param paymentsModel the payments DTO
     * @param amount        the amount
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PatientPaymentMethodFragment newInstance(PaymentsModel paymentsModel, double amount) {
        PatientPaymentMethodFragment fragment = new PatientPaymentMethodFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof PaymentViewHandler) {
                callback = (PatientPaymentMethodInterface) ((PaymentViewHandler) context).getPaymentPresenter();
            }else if (context instanceof AppointmentViewHandler){
                callback = (PatientPaymentMethodInterface) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (PatientPaymentMethodInterface) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }


    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        paymentTypeMap.put(CarePayConstants.TYPE_ANDROID_PAY, R.drawable.payment_android_button_selector);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle icicle) {
        super.onViewCreated(view, icicle);
        paymentMethodFragmentProgressBar = (ProgressBar) view.findViewById(R.id.paymentMethodFragmentProgressBar);
        initAndroidPay();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(androidPayAdapter != null) {
            androidPayAdapter.disconnectClient();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // retrieve the error code, if available
        int errorCode = -1;
        if (data != null) {
            errorCode = data.getIntExtra(WalletConstants.EXTRA_ERROR_CODE, -1);
        }
        switch (requestCode) {
            case PaymentConstants.REQUEST_CODE_MASKED_WALLET:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        if(data != null) {
                            MaskedWallet maskedWallet = data.getParcelableExtra(WalletConstants.EXTRA_MASKED_WALLET);
                            callback.createWalletFragment(maskedWallet, amountToMakePayment);
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    default:
                        handleError(errorCode);
                        break;
                }
                break;

            case WalletConstants.RESULT_ERROR:
                handleError(errorCode);
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    protected void setupTitleViews(View view) {
        super.setupTitleViews(view);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.icn_nav_back));
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().onBackPressed();
                }
            });
        }
    }

    private void showOrHideProgressDialog(boolean show) {
        if (show) {
            paymentMethodFragmentProgressBar.setVisibility(View.VISIBLE);
        } else {
            paymentMethodFragmentProgressBar.setVisibility(View.GONE);
        }

    }

    private void initAndroidPay() {
        androidPayAdapter = new AndroidPayAdapter(getActivity(), paymentsModel.getPaymentPayload().getMerchantServices());
        androidPayAdapter.initAndroidPay(this);
        showOrHideProgressDialog(true);
    }


    private void addAndroidPayPaymentMethod() {
        androidPayAdapter.createWalletButton(amountToMakePayment, (ViewGroup) findViewById(R.id.dynamic_wallet_button_fragment));
    }

    private void handleError(int errorCode) {
        switch (errorCode) {
            case WalletConstants.ERROR_CODE_SPENDING_LIMIT_EXCEEDED:
                Toast.makeText(getContext(), "Way too much!!", Toast.LENGTH_LONG).show();
                break;
            case WalletConstants.ERROR_CODE_INVALID_PARAMETERS:
            case WalletConstants.ERROR_CODE_AUTHENTICATION_FAILURE:
            case WalletConstants.ERROR_CODE_BUYER_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_MERCHANT_ACCOUNT_ERROR:
            case WalletConstants.ERROR_CODE_SERVICE_UNAVAILABLE:
            case WalletConstants.ERROR_CODE_UNSUPPORTED_API_VERSION:
            case WalletConstants.ERROR_CODE_UNKNOWN:
            default:
                // unrecoverable error
                String errorMessage = "Android Pay is unavailable" + "\n" +
                        "Error code: " + errorCode;
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                break;
        }
    }


    @Override
    public void onAndroidPayReady() {
        showOrHideProgressDialog(false);
        addAndroidPayPaymentMethod();
    }

}
