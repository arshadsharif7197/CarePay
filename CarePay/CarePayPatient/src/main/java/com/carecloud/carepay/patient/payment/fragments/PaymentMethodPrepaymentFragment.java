package com.carecloud.carepay.patient.payment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.carecloud.carepay.patient.R;
import com.carecloud.carepay.patient.appointments.presenter.PatientAppointmentPresenter;
import com.carecloud.carepay.service.library.CarePayConstants;
import com.carecloud.carepaylibray.appointments.interfaces.AppointmentPrepaymentCallback;
import com.carecloud.carepaylibray.appointments.presenter.AppointmentViewHandler;
import com.carecloud.carepaylibray.payments.models.PaymentsModel;
import com.carecloud.carepaylibray.utils.DtoHelper;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by lmenendez on 8/21/17
 */

public class PaymentMethodPrepaymentFragment extends PatientPaymentMethodFragment {
    private static final String KEY_TITLE = "title";

    private AppointmentPrepaymentCallback callback;
    private String titleString;

    /**
     * @param paymentsModel the payments DTO
     * @param amount        the amount
     * @return an instance of PatientPaymentMethodFragment
     */
    public static PaymentMethodPrepaymentFragment newInstance(PaymentsModel paymentsModel, double amount, String title) {
        PaymentMethodPrepaymentFragment fragment = new PaymentMethodPrepaymentFragment();
        Bundle args = new Bundle();
        DtoHelper.bundleDto(args, paymentsModel);
        args.putDouble(CarePayConstants.PAYMENT_AMOUNT_BUNDLE, amount);
        args.putString(KEY_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void attachCallback(Context context) {
        super.attachCallback(context);
        try {
            if (context instanceof AppointmentViewHandler) {
                callback = (AppointmentPrepaymentCallback) ((AppointmentViewHandler) context).getAppointmentPresenter();
            } else {
                callback = (AppointmentPrepaymentCallback) context;
            }
        } catch (ClassCastException cce) {
            throw new ClassCastException("Attached Context must implement PaymentMethodInterface");
        }
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Bundle args = getArguments();
        titleString = args.getString(KEY_TITLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_pre_payment_method, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle icicle) {
        super.onViewCreated(view, icicle);

        TextView prepaymentAmount = view.findViewById(R.id.prepaymentAmount);
        prepaymentAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amountToMakePayment));

    }

    @Override
    public void setupTitleViews(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar_layout);
        if (toolbar != null) {
            TextView title = toolbar.findViewById(R.id.respons_toolbar_title);
            title.setText(titleString);
            toolbar.setTitle("");

            toolbar.setNavigationIcon(R.drawable.icn_patient_mode_nav_close);
            toolbar.setNavigationOnClickListener(view1 -> {
                getActivity().onBackPressed();
                callback.onPaymentDismissed();
            });
            if (callback instanceof PatientAppointmentPresenter) {
                ((PatientAppointmentPresenter) callback).displayToolbar(false, null);
            }
        }

    }

}
