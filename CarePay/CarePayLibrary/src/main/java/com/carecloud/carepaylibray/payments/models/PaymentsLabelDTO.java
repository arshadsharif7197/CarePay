
package com.carecloud.carepaylibray.payments.models;


import com.carecloud.carepaylibray.utils.StringUtil;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PaymentsLabelDTO {
    @SerializedName("demographics_checkin_heading")
    @Expose
    private String demographicsCheckinHeading;
    @SerializedName("demographics_appointments_heading")
    @Expose
    private String demographicsAppointmentsHeading;
    @SerializedName("demographics_infomation_check_title")
    @Expose
    private String demographicsInfomationCheckTitle;
    @SerializedName("demographics_consent_forms_title")
    @Expose
    private String demographicsConsentFormsTitle;
    @SerializedName("demographics_intake_forms_title")
    @Expose
    private String demographicsIntakeFormsTitle;
    @SerializedName("demographics_payment_title")
    @Expose
    private String demographicsPaymentTitle;
    @SerializedName("demographics_pay_button")
    @Expose
    private String demographicsPayButton;
    @SerializedName("payment_next_button")
    @Expose
    private String paymentNextButton;
    @SerializedName("payment_add_new_credit_card_button")
    @Expose
    private String paymentAddNewCreditCardButton;
    @SerializedName("payment_method_title")
    @Expose
    private String paymentMethodTitle;
    @SerializedName("payment_choose_method_button")
    @Expose
    private String paymentChooseMethodButton;
    @SerializedName("payment_create_plan_button")
    @Expose
    private String paymentCreatePlanButton;
    @SerializedName("payment_choose_credit_card_button")
    @Expose
    private String paymentChooseCreditCardButton;
    @SerializedName("payment_back_button")
    @Expose
    private String paymentBackButton;
    @SerializedName("payment_see_front_desk_button")
    @Expose
    private String paymentSeeFrontDeskButton;
    @SerializedName("payment_close_button")
    @Expose
    private String paymentCloseButton;
    @SerializedName("payment_partial_amount_title")
    @Expose
    private String paymentPartialAmountTitle;
    @SerializedName("payment_pay_total_amount_button")
    @Expose
    private String paymentPayTotalAmountButton;
    @SerializedName("payment_partial_amount_button")
    @Expose
    private String paymentPartialAmountButton;

    /**
     *
     * @return
     * The demographicsCheckinHeading
     */
    public String getDemographicsCheckinHeading() {
        return demographicsCheckinHeading;
    }

    /**
     *
     * @param demographicsCheckinHeading
     * The demographics_checkin_heading
     */
    public void setDemographicsCheckinHeading(String demographicsCheckinHeading) {
        this.demographicsCheckinHeading = demographicsCheckinHeading;
    }

    /**
     *
     * @return
     * The demographicsAppointmentsHeading
     */
    public String getDemographicsAppointmentsHeading() {
        return demographicsAppointmentsHeading;
    }

    /**
     *
     * @param demographicsAppointmentsHeading
     * The demographics_appointments_heading
     */
    public void setDemographicsAppointmentsHeading(String demographicsAppointmentsHeading) {
        this.demographicsAppointmentsHeading = demographicsAppointmentsHeading;
    }

    /**
     *
     * @return
     * The demographicsInfomationCheckTitle
     */
    public String getDemographicsInfomationCheckTitle() {
        return demographicsInfomationCheckTitle;
    }

    /**
     *
     * @param demographicsInfomationCheckTitle
     * The demographics_infomation_check_title
     */
    public void setDemographicsInfomationCheckTitle(String demographicsInfomationCheckTitle) {
        this.demographicsInfomationCheckTitle = demographicsInfomationCheckTitle;
    }

    /**
     *
     * @return
     * The demographicsConsentFormsTitle
     */
    public String getDemographicsConsentFormsTitle() {
        return demographicsConsentFormsTitle;
    }

    /**
     *
     * @param demographicsConsentFormsTitle
     * The demographics_consent_forms_title
     */
    public void setDemographicsConsentFormsTitle(String demographicsConsentFormsTitle) {
        this.demographicsConsentFormsTitle = demographicsConsentFormsTitle;
    }

    /**
     *
     * @return
     * The demographicsIntakeFormsTitle
     */
    public String getDemographicsIntakeFormsTitle() {
        return demographicsIntakeFormsTitle;
    }

    /**
     *
     * @param demographicsIntakeFormsTitle
     * The demographics_intake_forms_title
     */
    public void setDemographicsIntakeFormsTitle(String demographicsIntakeFormsTitle) {
        this.demographicsIntakeFormsTitle = demographicsIntakeFormsTitle;
    }

    /**
     *
     * @return
     * The demographicsPaymentTitle
     */
    public String getDemographicsPaymentTitle() {
        return demographicsPaymentTitle;
    }

    /**
     *
     * @param demographicsPaymentTitle
     * The demographics_payment_title
     */
    public void setDemographicsPaymentTitle(String demographicsPaymentTitle) {
        this.demographicsPaymentTitle = demographicsPaymentTitle;
    }

    /**
     *
     * @return
     * The demographicsPayButton
     */
    public String getDemographicsPayButton() {
        return demographicsPayButton;
    }

    /**
     *
     * @param demographicsPayButton
     * The demographics_pay_button
     */
    public void setDemographicsPayButton(String demographicsPayButton) {
        this.demographicsPayButton = demographicsPayButton;
    }

    /**
     *
     * @return
     * The paymentNextButton
     */
    public String getPaymentNextButton() {
        return paymentNextButton;
    }

    /**
     *
     * @param paymentNextButton
     * The payment_next_button
     */
    public void setPaymentNextButton(String paymentNextButton) {
        this.paymentNextButton = paymentNextButton;
    }

    /**
     *
     * @return
     * The paymentAddNewCreditCardButton
     */
    public String getPaymentAddNewCreditCardButton() {
        return paymentAddNewCreditCardButton;
    }

    /**
     *
     * @param paymentAddNewCreditCardButton
     * The payment_add_new_credit_card_button
     */
    public void setPaymentAddNewCreditCardButton(String paymentAddNewCreditCardButton) {
        this.paymentAddNewCreditCardButton = paymentAddNewCreditCardButton;
    }

    /**
     *
     * @return
     * The paymentMethodTitle
     */
    public String getPaymentMethodTitle() {
        return paymentMethodTitle;
    }

    /**
     *
     * @param paymentMethodTitle
     * The payment_method_title
     */
    public void setPaymentMethodTitle(String paymentMethodTitle) {
        this.paymentMethodTitle = paymentMethodTitle;
    }

    /**
     *
     * @return
     * The paymentChooseMethodButton
     */
    public String getPaymentChooseMethodButton() {
        return paymentChooseMethodButton;
    }

    /**
     *
     * @param paymentChooseMethodButton
     * The payment_choose_method_button
     */
    public void setPaymentChooseMethodButton(String paymentChooseMethodButton) {
        this.paymentChooseMethodButton = paymentChooseMethodButton;
    }

    /**
     *
     * @return
     * The paymentCreatePlanButton
     */
    public String getPaymentCreatePlanButton() {
        return paymentCreatePlanButton;
    }

    /**
     *
     * @param paymentCreatePlanButton
     * The payment_create_plan_button
     */
    public void setPaymentCreatePlanButton(String paymentCreatePlanButton) {
        this.paymentCreatePlanButton = paymentCreatePlanButton;
    }

    /**
     *
     * @return
     * The paymentChooseCreditCardButton
     */
    public String getPaymentChooseCreditCardButton() {
        return paymentChooseCreditCardButton;
    }

    /**
     *
     * @param paymentChooseCreditCardButton
     * The payment_choose_credit_card_button
     */
    public void setPaymentChooseCreditCardButton(String paymentChooseCreditCardButton) {
        this.paymentChooseCreditCardButton = paymentChooseCreditCardButton;
    }

    /**
     *
     * @return
     * The paymentBackButton
     */
    public String getPaymentBackButton() {
        return paymentBackButton;
    }

    /**
     *
     * @param paymentBackButton
     * The payment_back_button
     */
    public void setPaymentBackButton(String paymentBackButton) {
        this.paymentBackButton = paymentBackButton;
    }

    /**
     *
     * @return
     * The paymentSeeFrontDeskButton
     */
    public String getPaymentSeeFrontDeskButton() {
        return paymentSeeFrontDeskButton;
    }

    /**
     *
     * @param paymentSeeFrontDeskButton
     * The payment_see_front_desk_button
     */
    public void setPaymentSeeFrontDeskButton(String paymentSeeFrontDeskButton) {
        this.paymentSeeFrontDeskButton = paymentSeeFrontDeskButton;
    }

    /**
     *
     * @return
     * The paymentCloseButton
     */
    public String getPaymentCloseButton() {
        return paymentCloseButton;
    }

    /**
     *
     * @param paymentCloseButton
     * The payment_close_button
     */
    public void setPaymentCloseButton(String paymentCloseButton) {
        this.paymentCloseButton = paymentCloseButton;
    }

    /**
     *
     * @return
     * The paymentPartialAmountTitle
     */
    public String getPaymentPartialAmountTitle() {
        return paymentPartialAmountTitle;
    }

    /**
     *
     * @param paymentPartialAmountTitle
     * The payment_partial_amount_title
     */
    public void setPaymentPartialAmountTitle(String paymentPartialAmountTitle) {
        this.paymentPartialAmountTitle = paymentPartialAmountTitle;
    }

    /**
     *
     * @return
     * The paymentPayTotalAmountButton
     */
    public String getPaymentPayTotalAmountButton() {
        return paymentPayTotalAmountButton;
    }

    /**
     *
     * @param paymentPayTotalAmountButton
     * The payment_pay_total_amount_button
     */
    public void setPaymentPayTotalAmountButton(String paymentPayTotalAmountButton) {
        this.paymentPayTotalAmountButton = paymentPayTotalAmountButton;
    }

    /**
     *
     * @return
     * The paymentPartialAmountButton
     */
    public String getPaymentPartialAmountButton() {
        return paymentPartialAmountButton;
    }

    /**
     *
     * @param paymentPartialAmountButton
     * The payment_partial_amount_button
     */
    public void setPaymentPartialAmountButton(String paymentPartialAmountButton) {
        this.paymentPartialAmountButton = paymentPartialAmountButton;
    }

}
