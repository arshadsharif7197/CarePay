package com.carecloud.carepaylibray.intake.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LinksModel {

    @SerializedName("self")
    @Expose
    private SelfModel self = new SelfModel();
    @SerializedName("payments")
    @Expose
    private PaymentModel payments = new PaymentModel();
    @SerializedName("intake")
    @Expose
    private TransitionDTO intake = new TransitionDTO();


    /**
     * 
     * @return
     *     The self
     */
    public SelfModel getSelf() {
        return self;
    }

    /**
     * 
     * @param self
     *     The self
     */
    public void setSelf(SelfModel self) {
        this.self = self;
    }

    /**
     * 
     * @return
     *     The payments
     */
    public PaymentModel getPayments() {
        return payments;
    }

    /**
     * 
     * @param payments
     *     The payments
     */
    public void setPayments(PaymentModel payments) {
        this.payments = payments;
    }

    public TransitionDTO getIntake() {
        return intake;
    }

    public void setIntake(TransitionDTO intake) {
        this.intake = intake;
    }
}
