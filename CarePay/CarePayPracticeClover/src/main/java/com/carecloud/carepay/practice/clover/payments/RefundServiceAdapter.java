package com.carecloud.carepay.practice.clover.payments;

import com.clover.connector.sdk.v3.PaymentV3Connector;
import com.clover.sdk.v3.remotepay.AuthResponse;
import com.clover.sdk.v3.remotepay.CapturePreAuthResponse;
import com.clover.sdk.v3.remotepay.ConfirmPaymentRequest;
import com.clover.sdk.v3.remotepay.ManualRefundResponse;
import com.clover.sdk.v3.remotepay.PreAuthResponse;
import com.clover.sdk.v3.remotepay.ReadCardDataResponse;
import com.clover.sdk.v3.remotepay.RefundPaymentResponse;
import com.clover.sdk.v3.remotepay.RetrievePendingPaymentsResponse;
import com.clover.sdk.v3.remotepay.SaleResponse;
import com.clover.sdk.v3.remotepay.TipAdded;
import com.clover.sdk.v3.remotepay.TipAdjustAuthResponse;
import com.clover.sdk.v3.remotepay.VaultCardResponse;
import com.clover.sdk.v3.remotepay.VerifySignatureRequest;
import com.clover.sdk.v3.remotepay.VoidPaymentResponse;

/**
 * Created by lmenendez on 10/20/17
 */

public class RefundServiceAdapter implements PaymentV3Connector.PaymentServiceListener {
    @Override
    public void onPreAuthResponse(PreAuthResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onAuthResponse(AuthResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onTipAdjustAuthResponse(TipAdjustAuthResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onCapturePreAuthResponse(CapturePreAuthResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onVerifySignatureRequest(VerifySignatureRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onConfirmPaymentRequest(ConfirmPaymentRequest request) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onSaleResponse(SaleResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onManualRefundResponse(ManualRefundResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onRefundPaymentResponse(RefundPaymentResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onTipAdded(TipAdded tipAdded) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onVoidPaymentResponse(VoidPaymentResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onVaultCardResponse(VaultCardResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onRetrievePendingPaymentsResponse(RetrievePendingPaymentsResponse retrievePendingPaymentResponse) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void onReadCardDataResponse(ReadCardDataResponse response) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
