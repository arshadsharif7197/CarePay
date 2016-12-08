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
    @SerializedName("payment_pay_total_responsibility")
    @Expose
    private String paymentTotalResponsibility;
    @SerializedName("payment_previous_balance")
    @Expose
    private String paymentPreviousBalance;
    @SerializedName("payment_insurance_copay")
    @Expose
    private String paymentInsuranceCopay;
    @SerializedName("payment_responsibility_title")
    @Expose
    private String paymentResponsibilityTitle;
    @SerializedName("payment_plan_heading")
    @Expose
    private String paymentPlanHeading;
    @SerializedName("payment_lets_establish_payment_plan")
    @Expose
    private String paymentLetsEstablishPaymentPlan;
    @SerializedName("payment_plan_name")
    @Expose
    private String paymentPlanName;
    @SerializedName("payment_day_of_the_month")
    @Expose
    private String paymentDayOfTheMonth;
    @SerializedName("payment_number_of_months")
    @Expose
    private String paymentNumberOfMonths;
    @SerializedName("payment_monthly_payment")
    @Expose
    private String paymentMonthlyPayment;
    @SerializedName("payment_change_card")
    @Expose
    private String paymentChangeCard;
    @SerializedName("payment_create_plan")
    @Expose
    private String paymentCreatePlan;
    @SerializedName("payment_add_card_button")
    @Expose
    private String paymentAddCardButton;
    @SerializedName("payment_optional_hint")
    @Expose
    private String paymentOptionalHint;
    @SerializedName("payment_terms")
    @Expose
    private String paymentTerms;
    @SerializedName("payment_agree_and_pay")
    @Expose
    private String paymentAgreeAndPay;
    @SerializedName("payment_agree_to_pay_terms")
    @Expose
    private String paymentAgreeToPayTerms;
    @SerializedName("payment_plan_create_condition_error")
    @Expose
    private String paymentPlanCreateConditionError;
    @SerializedName("payment_plan_min_months_error")
    @Expose
    private String paymentPlanMinMonthsError;
    @SerializedName("payment_plan_max_months_error")
    @Expose
    private String paymentPlanMaxMonthsError;
    @SerializedName("payment_plan_min_amount_error")
    @Expose
    private String paymentPlanMinAmountError;
    @SerializedName("payment_plan_max_amount_error")
    @Expose
    private String paymentPlanMaxAmountError;

    /**
     * @return The demographicsCheckinHeading
     */
    public String getDemographicsCheckinHeading() {
        return StringUtil.getLabelForView(demographicsCheckinHeading);
    }

    /**
     * @param demographicsCheckinHeading The demographics_checkin_heading
     */
    public void setDemographicsCheckinHeading(String demographicsCheckinHeading) {
        this.demographicsCheckinHeading = demographicsCheckinHeading;
    }

    /**
     * @return The demographicsAppointmentsHeading
     */
    public String getDemographicsAppointmentsHeading() {
        return StringUtil.getLabelForView(demographicsAppointmentsHeading);
    }

    /**
     * @param demographicsAppointmentsHeading The demographics_appointments_heading
     */
    public void setDemographicsAppointmentsHeading(String demographicsAppointmentsHeading) {
        this.demographicsAppointmentsHeading = demographicsAppointmentsHeading;
    }

    /**
     * @return The demographicsInfomationCheckTitle
     */
    public String getDemographicsInfomationCheckTitle() {
        return StringUtil.getLabelForView(demographicsInfomationCheckTitle);
    }

    /**
     * @param demographicsInfomationCheckTitle The demographics_infomation_check_title
     */
    public void setDemographicsInfomationCheckTitle(String demographicsInfomationCheckTitle) {
        this.demographicsInfomationCheckTitle = demographicsInfomationCheckTitle;
    }

    /**
     * @return The demographicsConsentFormsTitle
     */
    public String getDemographicsConsentFormsTitle() {
        return StringUtil.getLabelForView(demographicsConsentFormsTitle);
    }

    /**
     * @param demographicsConsentFormsTitle The demographics_consent_forms_title
     */
    public void setDemographicsConsentFormsTitle(String demographicsConsentFormsTitle) {
        this.demographicsConsentFormsTitle = demographicsConsentFormsTitle;
    }

    /**
     * @return The demographicsIntakeFormsTitle
     */
    public String getDemographicsIntakeFormsTitle() {
        return StringUtil.getLabelForView(demographicsIntakeFormsTitle);
    }

    /**
     * @param demographicsIntakeFormsTitle The demographics_intake_forms_title
     */
    public void setDemographicsIntakeFormsTitle(String demographicsIntakeFormsTitle) {
        this.demographicsIntakeFormsTitle = demographicsIntakeFormsTitle;
    }

    /**
     * @return The demographicsPaymentTitle
     */
    public String getDemographicsPaymentTitle() {
        return StringUtil.getLabelForView(demographicsPaymentTitle);
    }

    /**
     * @param demographicsPaymentTitle The demographics_payment_title
     */
    public void setDemographicsPaymentTitle(String demographicsPaymentTitle) {
        this.demographicsPaymentTitle = demographicsPaymentTitle;
    }

    /**
     * @return The demographicsPayButton
     */
    public String getDemographicsPayButton() {
        return StringUtil.getLabelForView(demographicsPayButton);
    }

    /**
     * @param demographicsPayButton The demographics_pay_button
     */
    public void setDemographicsPayButton(String demographicsPayButton) {
        this.demographicsPayButton = demographicsPayButton;
    }

    /**
     * @return The paymentNextButton
     */
    public String getPaymentNextButton() {
        return StringUtil.getLabelForView(paymentNextButton);
    }

    /**
     * @param paymentNextButton The payment_next_button
     */
    public void setPaymentNextButton(String paymentNextButton) {
        this.paymentNextButton = paymentNextButton;
    }

    /**
     * @return The paymentAddNewCreditCardButton
     */
    public String getPaymentAddNewCreditCardButton() {
        return StringUtil.getLabelForView(paymentAddNewCreditCardButton);
    }

    /**
     * @param paymentAddNewCreditCardButton The payment_add_new_credit_card_button
     */
    public void setPaymentAddNewCreditCardButton(String paymentAddNewCreditCardButton) {
        this.paymentAddNewCreditCardButton = paymentAddNewCreditCardButton;
    }

    /**
     * @return The paymentMethodTitle
     */
    public String getPaymentMethodTitle() {
        return StringUtil.getLabelForView(paymentMethodTitle);
    }

    /**
     * @param paymentMethodTitle The payment_method_title
     */
    public void setPaymentMethodTitle(String paymentMethodTitle) {
        this.paymentMethodTitle = paymentMethodTitle;
    }

    /**
     * @return The paymentChooseMethodButton
     */
    public String getPaymentChooseMethodButton() {
        return StringUtil.getLabelForView(paymentChooseMethodButton);
    }

    /**
     * @param paymentChooseMethodButton The payment_choose_method_button
     */
    public void setPaymentChooseMethodButton(String paymentChooseMethodButton) {
        this.paymentChooseMethodButton = paymentChooseMethodButton;
    }

    /**
     * @return The paymentCreatePlanButton
     */
    public String getPaymentCreatePlanButton() {
        return StringUtil.getLabelForView(paymentCreatePlanButton);
    }

    /**
     * @param paymentCreatePlanButton The payment_create_plan_button
     */
    public void setPaymentCreatePlanButton(String paymentCreatePlanButton) {
        this.paymentCreatePlanButton = paymentCreatePlanButton;
    }

    /**
     * @return The paymentChooseCreditCardButton
     */
    public String getPaymentChooseCreditCardButton() {
        return StringUtil.getLabelForView(paymentChooseCreditCardButton);
    }

    /**
     * @param paymentChooseCreditCardButton The payment_choose_credit_card_button
     */
    public void setPaymentChooseCreditCardButton(String paymentChooseCreditCardButton) {
        this.paymentChooseCreditCardButton = paymentChooseCreditCardButton;
    }

    /**
     * @return The paymentBackButton
     */
    public String getPaymentBackButton() {
        return StringUtil.getLabelForView(paymentBackButton);
    }

    /**
     * @param paymentBackButton The payment_back_button
     */
    public void setPaymentBackButton(String paymentBackButton) {
        this.paymentBackButton = paymentBackButton;
    }

    /**
     * @return The paymentSeeFrontDeskButton
     */
    public String getPaymentSeeFrontDeskButton() {
        return StringUtil.getLabelForView(paymentSeeFrontDeskButton);
    }

    /**
     * @param paymentSeeFrontDeskButton The payment_see_front_desk_button
     */
    public void setPaymentSeeFrontDeskButton(String paymentSeeFrontDeskButton) {
        this.paymentSeeFrontDeskButton = paymentSeeFrontDeskButton;
    }

    /**
     * @return The paymentCloseButton
     */
    public String getPaymentCloseButton() {
        return StringUtil.getLabelForView(paymentCloseButton);
    }

    /**
     * @param paymentCloseButton The payment_close_button
     */
    public void setPaymentCloseButton(String paymentCloseButton) {
        this.paymentCloseButton = paymentCloseButton;
    }

    /**
     * @return The paymentPartialAmountTitle
     */
    public String getPaymentPartialAmountTitle() {
        return StringUtil.getLabelForView(paymentPartialAmountTitle);
    }

    /**
     * @param paymentPartialAmountTitle The payment_partial_amount_title
     */
    public void setPaymentPartialAmountTitle(String paymentPartialAmountTitle) {
        this.paymentPartialAmountTitle = paymentPartialAmountTitle;
    }

    /**
     * @return The paymentPayTotalAmountButton
     */
    public String getPaymentPayTotalAmountButton() {
        return StringUtil.getLabelForView(paymentPayTotalAmountButton);
    }

    /**
     * @param paymentPayTotalAmountButton The payment_pay_total_amount_button
     */
    public void setPaymentPayTotalAmountButton(String paymentPayTotalAmountButton) {
        this.paymentPayTotalAmountButton = paymentPayTotalAmountButton;
    }

    /**
     * @return The paymentPartialAmountButton
     */
    public String getPaymentPartialAmountButton() {
        return StringUtil.getLabelForView(paymentPartialAmountButton);
    }

    /**
     * @param paymentPartialAmountButton The payment_partial_amount_button
     */
    public void setPaymentPartialAmountButton(String paymentPartialAmountButton) {
        this.paymentPartialAmountButton = paymentPartialAmountButton;
    }

    /**
     *
     * @return paymentTotalResponsibility
     */
    public String getPaymentTotalResponsibility() {
        return StringUtil.getLabelForView(paymentTotalResponsibility);
    }

    /**
     *
     * @param paymentTotalResponsibility The paymentTotalResponsibility
     */
    public void setPaymentTotalResponsibility(String paymentTotalResponsibility) {
        this.paymentTotalResponsibility = paymentTotalResponsibility;
    }

    /**
     *
     * @return paymentInsuranceCopay
     */
    public String getPaymentInsuranceCopay() {
        return StringUtil.getLabelForView(paymentInsuranceCopay);
    }

    /**
     *
     * @param paymentInsuranceCopay The paymentResponsibilityTitle
     */
    public void setPaymentInsuranceCopay(String paymentInsuranceCopay) {
        this.paymentInsuranceCopay = paymentInsuranceCopay;
    }

    /**
     *
     * @return paymentResponsibilityTitle
     */
    public String getPaymentResponsibilityTitle() {
        return StringUtil.getLabelForView(paymentResponsibilityTitle);
    }

    /**
     *
     * @param paymentResponsibilityTitle The paymentResponsibilityTitle
     */
    public void setPaymentResponsibilityTitle(String paymentResponsibilityTitle) {
        this.paymentResponsibilityTitle = paymentResponsibilityTitle;
    }

    /**
     *
     * @return The paymentPreviousBalance
     */
    public String getPaymentPreviousBalance() {
        return StringUtil.getLabelForView(paymentPreviousBalance);
    }

    /**
     *
     * @param paymentPreviousBalance The paymentPreviousBalance
     */
    public void setPaymentPreviousBalance(String paymentPreviousBalance) {
        this.paymentPreviousBalance = paymentPreviousBalance;
    }

    /**
     *
     * @return The paymentPlanHeading
     */
    public String getPaymentPlanHeading() {
        return StringUtil.getLabelForView(paymentPlanHeading);
    }

    /**
     *
     * @return The paymentTerms
     */
    public String getPaymentTerms() {
        return StringUtil.getLabelForView(paymentTerms);
    }

    /**
     *
     * @param paymentPlanHeading The paymentPlanHeading
     */
    public void setPaymentPlanHeading(String paymentPlanHeading) {
        this.paymentPlanHeading = paymentPlanHeading;
    }

    /**
     *
     * @param paymentTerms The paymentTerms
     */
    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    /**
     *
     * @return The paymentLetsEstablishPaymentPlan
     */
    public String getPaymentLetsEstablishPaymentPlan() {
        return StringUtil.getLabelForView(paymentLetsEstablishPaymentPlan);
    }

    /**
     *
     * @return The paymentAgreeAndPay
     */
    public String getPaymentAgreeAndPay() {
        return StringUtil.getLabelForView(paymentAgreeAndPay);
    }

    /**
     *
     * @param paymentLetsEstablishPaymentPlan The paymentLetsEstablishPaymentPlan
     */
    public void setPaymentLetsEstablishPaymentPlan(String paymentLetsEstablishPaymentPlan) {
        this.paymentLetsEstablishPaymentPlan = paymentLetsEstablishPaymentPlan;
    }

    /**
     *
     * @param paymentAgreeAndPay The paymentAgreeAndPay
     */
    public void setPaymentAgreeAndPay(String paymentAgreeAndPay) {
        this.paymentAgreeAndPay = paymentAgreeAndPay;
    }

    /**
     *
     * @return The paymentPlanName
     */
    public String getPaymentPlanName() {
        return StringUtil.getLabelForView(paymentPlanName);
    }

    /**
     *
     * @return The paymentAgreeToPayTerms
     */
    public String getPaymentAgreeToPayTerms() {
        return StringUtil.getLabelForView(paymentAgreeToPayTerms);
    }

    /**
     *
     * @param paymentPlanName The paymentPlanName
     */
    public void setPaymentPlanName(String paymentPlanName) {
        this.paymentPlanName = paymentPlanName;
    }

    /**
     *
     * @return The paymentDayOfTheMonth
     */
    public String getPaymentDayOfTheMonth() {
        return StringUtil.getLabelForView(paymentDayOfTheMonth);
    }

    /**
     *
     * @param paymentDayOfTheMonth The paymentDayOfTheMonth
     */
    public void setPaymentDayOfTheMonth(String paymentDayOfTheMonth) {
        this.paymentDayOfTheMonth = paymentDayOfTheMonth;
    }

    /**
     *
     * @return The paymentNumberOfMonths
     */
    public String getPaymentNumberOfMonths() {
        return StringUtil.getLabelForView(paymentNumberOfMonths);
    }

    /**
     *
     * @param paymentNumberOfMonths The paymentNumberOfMonths
     */
    public void setPaymentNumberOfMonths(String paymentNumberOfMonths) {
        this.paymentNumberOfMonths = paymentNumberOfMonths;
    }

    /**
     *
     * @return The paymentMonthlyPayment
     */
    public String getPaymentMonthlyPayment() {
        return StringUtil.getLabelForView(paymentMonthlyPayment);
    }

    /**
     *
     * @param paymentMonthlyPayment The paymentMonthlyPayment
     */
    public void setPaymentMonthlyPayment(String paymentMonthlyPayment) {
        this.paymentMonthlyPayment = paymentMonthlyPayment;
    }

    /**
     *
     * @return The paymentChangeCard
     */
    public String getPaymentChangeCard() {
        return StringUtil.getLabelForView(paymentChangeCard);
    }

    /**
     *
     * @param paymentChangeCard The paymentChangeCard
     */
    public void setPaymentChangeCard(String paymentChangeCard) {
        this.paymentChangeCard = paymentChangeCard;
    }

    /**
     *
     * @return The paymentCreatePlan
     */
    public String getPaymentCreatePlan() {
        return StringUtil.getLabelForView(paymentCreatePlan);
    }

    /**
     *
     * @param paymentCreatePlan The paymentCreatePlan
     */
    public void setPaymentCreatePlan(String paymentCreatePlan) {
        this.paymentCreatePlan = paymentCreatePlan;
    }

    /**
     *
     * @return The paymentAddCardButton
     */
    public String getPaymentAddCardButton() {
        return StringUtil.getLabelForView(paymentAddCardButton);
    }

    /**
     *
     * @param paymentAddCardButton The paymentAddCardButton
     */
    public void setPaymentAddCardButton(String paymentAddCardButton) {
        this.paymentAddCardButton = paymentAddCardButton;
    }

    /**
     *
     * @return The paymentOptionalHint
     */
    public String getPaymentOptionalHint() {
        return StringUtil.getLabelForView(paymentOptionalHint);
    }

    /**
     *
     * @param paymentOptionalHint The paymentOptionalHint
     */
    public void setPaymentOptionalHint(String paymentOptionalHint) {
        this.paymentOptionalHint = paymentOptionalHint;
    }

    /**
     *
     * @param paymentAgreeToPayTerms The paymentAgreeToPayTerms
     */
    public void setPaymentAgreeToPayTerms(String paymentAgreeToPayTerms) {
        this.paymentAgreeToPayTerms = paymentAgreeToPayTerms;
    }

    /**
     *
     * @return paymentPlanCreateConditionError
     */
    public String getPaymentPlanCreateConditionError() {
        return StringUtil.getLabelForView(paymentPlanCreateConditionError);
    }

    /**
     *
     * @param paymentPlanCreateConditionError paymentPlanCreateConditionError
     */
    public void setPaymentPlanCreateConditionError(String paymentPlanCreateConditionError) {
        this.paymentPlanCreateConditionError = paymentPlanCreateConditionError;
    }

    /**
     *
     * @return paymentPlanMinMonthsError
     */
    public String getPaymentPlanMinMonthsError() {
        return StringUtil.getLabelForView(paymentPlanMinMonthsError);
    }

    /**
     *
     * @param paymentPlanMinMonthsError paymentPlanMinMonthsError
     */
    public void setPaymentPlanMinMonthsError(String paymentPlanMinMonthsError) {
        this.paymentPlanMinMonthsError = paymentPlanMinMonthsError;
    }

    /**
     *
     * @return paymentPlanMaxMonthsError
     */
    public String getPaymentPlanMaxMonthsError() {
        return StringUtil.getLabelForView(paymentPlanMaxMonthsError);
    }

    /**
     *
     * @param paymentPlanMaxMonthsError paymentPlanMaxMonthsError
     */
    public void setPaymentPlanMaxMonthsError(String paymentPlanMaxMonthsError) {
        this.paymentPlanMaxMonthsError = paymentPlanMaxMonthsError;
    }

    /**
     *
     * @return paymentPlanMinAmountError
     */
    public String getPaymentPlanMinAmountError() {
        return StringUtil.getLabelForView(paymentPlanMinAmountError);
    }

    /**
     *
     * @param paymentPlanMinAmountError paymentPlanMinAmountError
     */
    public void setPaymentPlanMinAmountError(String paymentPlanMinAmountError) {
        this.paymentPlanMinAmountError = paymentPlanMinAmountError;
    }

    /**
     *
     * @return paymentPlanMaxAmountError
     */
    public String getPaymentPlanMaxAmountError() {
        return StringUtil.getLabelForView(paymentPlanMaxAmountError);
    }

    /**
     *
     * @param paymentPlanMaxAmountError paymentPlanMaxAmountError
     */
    public void setPaymentPlanMaxAmountError(String paymentPlanMaxAmountError) {
        this.paymentPlanMaxAmountError = paymentPlanMaxAmountError;
    }
}