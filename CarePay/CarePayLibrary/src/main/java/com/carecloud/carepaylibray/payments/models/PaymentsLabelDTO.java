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
    @SerializedName("your_total_patient_responsibility")
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
    @SerializedName("payment_patient_balance_toolbar")
    @Expose
    private String paymentPatientBalanceToolbar;
    @SerializedName("payment_patient_balance_tab")
    @Expose
    private String paymentPatientBalanceTab;
    @SerializedName("payment_patient_history_tab")
    @Expose
    private String paymentPatientHistoryTab;
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
    @SerializedName("payment_pending_text")
    @Expose
    private String paymentPendingText;
    @SerializedName("payment_how_much_text")
    @Expose
    private String paymentHowMuchText;
    @SerializedName("payment_new_credit_card")
    @Expose
    private String paymentNewCreditCard;
    @SerializedName("payment_credit_card_number")
    @Expose
    private String paymentCreditCardNumber;
    @SerializedName("payment_verification_number")
    @Expose
    private String paymentVerificationNumber;
    @SerializedName("payment_expiration_date")
    @Expose
    private String paymentExpirationDate;
    @SerializedName("payment_pick_date")
    @Expose
    private String paymentPickDate;
    @SerializedName("payment_save_card_on_file")
    @Expose
    private String paymentSaveCardOnFile;
    @SerializedName("payment_cancel_button")
    @Expose
    private String paymentCancelButton;
    @SerializedName("payment_name_on_card_text")
    @Expose
    private String paymentNameOnCardText;
    @SerializedName("payment_billing_address_text")
    @Expose
    private String paymentBillingAddressText;
    @SerializedName("payment_address_line1_text")
    @Expose
    private String paymentAddressLine1Text;
    @SerializedName("payment_address_line2_text")
    @Expose
    private String paymentAddressLine2Text;
    @SerializedName("payment_city")
    @Expose
    private String paymentCity;
    @SerializedName("payment_state")
    @Expose
    private String paymentState;
    @SerializedName("payment_country")
    @Expose
    private String paymentCountry;
    @SerializedName("payment_zipcode")
    @Expose
    private String paymentZipcode;
    @SerializedName("demographics_pay_button_text")
    @Expose
    private String paymentPayText;
    @SerializedName("payment_use_profile_address")
    @Expose
    private String paymentUseProfileAddress;
    @SerializedName("payment_set_as_default_credit_card_label")
    @Expose
    private String paymentSetAsDefaultCreditCard;
    @SerializedName("payment_change_payment_label")
    @Expose
    private String paymentChangeMethodButton;
    @SerializedName("payment_failed_error")
    @Expose
    private String paymentFailedErrorMessage;
    @SerializedName("payment_receipt_total_amount")
    @Expose
    private String paymentReceiptTotalAmount;
    @SerializedName("payment_receipt_save_receipt")
    @Expose
    private String paymentReceiptSaveReceipt;
    @SerializedName("payment_receipt_share_receipt")
    @Expose
    private String paymentReceiptShareReceipt;
    @SerializedName("payment_receipt_payment_type")
    @Expose
    private String paymentReceiptPaymentType;
    @SerializedName("payment_receipt_no_label")
    @Expose
    private String paymentReceiptNoLabel;
    @SerializedName("payment_receipt_title")
    @Expose
    private String paymentReceiptTitle;
    @SerializedName("payment_receipt_total_paid_label")
    @Expose
    private String paymentReceiptTotalPaidLabel;
    @SerializedName("payment_responsibility_details")
    @Expose
    private String paymentResponsibilityDetails;
    @SerializedName("payment_responsibility_pay_later")
    @Expose
    private String paymentResponsibilityPayLater;
    @SerializedName("payment_details_pay_now")
    @Expose
    private String paymentDetailsPayNow;
    @SerializedName("payment_details_patient_balance_label")
    @Expose
    private String paymentDetailsPatientBalanceLabel;
    @SerializedName("practice_payments_back_label")
    @Expose
    private String practicePaymentsBackLabel;
    @SerializedName("practice_payments_header")
    @Expose
    private String practicePaymentsHeader;
    @SerializedName("practice_payments_findPatient_label")
    @Expose
    private String practicePaymentsFindPatientLabel;
    @SerializedName("practice_payments_filter")
    @Expose
    private String practicePaymentsFilter;
    @SerializedName("practice_payments_inoffice")
    @Expose
    private String practicePaymentsInOffice;
    @SerializedName("practice_payments_detail_dialog_balance")
    @Expose
    private String practicePaymentsDetailDialogBalance;
    @SerializedName("practice_payments_detail_dialog_payment_plan")
    @Expose
    private String practicePaymentsDetailDialogPaymentPlan;
    @SerializedName("practice_payments_detail_dialog_pay")
    @Expose
    private String practicePaymentsDetailDialogPay;
    @SerializedName("practice_payments_detail_dialog_label")
    @Expose
    private String practicePaymentsDetailDialogLabel;
    @SerializedName("practice_payments_detail_dialog_close_button")
    @Expose
    private String practicePaymentsDetailDialogCloseButton;

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

    /**
     *
     * @return paymentPendingText
     */
    public String getPaymentPendingText() {
        return StringUtil.getLabelForView(paymentPendingText);
    }

    /**
     * Sets payment pending text.
     *
     * @param paymentPendingText the payment pending text
     */
    public void setPaymentPendingText(String paymentPendingText) {
        this.paymentPendingText = paymentPendingText;
    }

    /**
     * Gets payment how much text.
     *
     * @return the payment how much text
     */
    public String getPaymentHowMuchText() {
        return StringUtil.getLabelForView(paymentHowMuchText);
    }

    /**
     * Sets payment how much text.
     *
     * @param paymentHowMuchText the payment how much text
     */
    public void setPaymentHowMuchText(String paymentHowMuchText) {
        this.paymentHowMuchText = paymentHowMuchText;
    }

    /**
     * Gets payment new credit card.
     *
     * @return the payment new credit card
     */
    public String getPaymentNewCreditCard() {
        return StringUtil.getLabelForView(paymentNewCreditCard);
    }

    /**
     * Sets payment new credit card.
     *
     * @param paymentNewCreditCard the payment new credit card
     */
    public void setPaymentNewCreditCard(String paymentNewCreditCard) {
        this.paymentNewCreditCard = paymentNewCreditCard;
    }

    /**
     * Gets payment credit card number.
     *
     * @return the payment credit card number
     */
    public String getPaymentCreditCardNumber() {
        return StringUtil.getLabelForView(paymentCreditCardNumber);
    }

    /**
     * Sets payment credit card number.
     *
     * @param paymentCreditCardNumber the payment credit card number
     */
    public void setPaymentCreditCardNumber(String paymentCreditCardNumber) {
        this.paymentCreditCardNumber = paymentCreditCardNumber;
    }

    /**
     * Gets payment verification number.
     *
     * @return the payment verification number
     */
    public String getPaymentVerificationNumber() {
        return StringUtil.getLabelForView(paymentVerificationNumber);
    }

    /**
     * Sets payment verification number.
     *
     * @param paymentVerificationNumber the payment verification number
     */
    public void setPaymentVerificationNumber(String paymentVerificationNumber) {
        this.paymentVerificationNumber = paymentVerificationNumber;
    }

    /**
     * Gets payment expiration date.
     *
     * @return the payment expiration date
     */
    public String getPaymentExpirationDate() {
        return StringUtil.getLabelForView(paymentExpirationDate);
    }

    /**
     * Sets payment expiration date.
     *
     * @param paymentExpirationDate the payment expiration date
     */
    public void setPaymentExpirationDate(String paymentExpirationDate) {
        this.paymentExpirationDate = paymentExpirationDate;
    }

    /**
     * Gets payment pick date.
     *
     * @return the payment pick date
     */
    public String getPaymentPickDate() {
        return StringUtil.getLabelForView(paymentPickDate);
    }

    /**
     * Sets payment pick date.
     *
     * @param paymentPickDate the payment pick date
     */
    public void setPaymentPickDate(String paymentPickDate) {
        this.paymentPickDate = paymentPickDate;
    }

    /**
     * Gets payment save card on file.
     *
     * @return the payment save card on file
     */
    public String getPaymentSaveCardOnFile() {
        return StringUtil.getLabelForView(paymentSaveCardOnFile);
    }

    /**
     * Sets payment save card on file.
     *
     * @param paymentSaveCardOnFile the payment save card on file
     */
    public void setPaymentSaveCardOnFile(String paymentSaveCardOnFile) {
        this.paymentSaveCardOnFile = paymentSaveCardOnFile;
    }

    /**
     *
     * @return paymentCancelButton
     */
    public String getPaymentCancelButton() {
        return StringUtil.getLabelForView(paymentCancelButton);
    }

    /**
     *
     * @param paymentCancelButton paymentCancelButton
     */
    public void setPaymentCancelButton(String paymentCancelButton) {
        this.paymentCancelButton = paymentCancelButton;
    }

    /**
     * Gets payment name on card text.
     *
     * @return the payment name on card text
     */
    public String getPaymentNameOnCardText() {
        return StringUtil.getLabelForView(paymentNameOnCardText);
    }

    /**
     * Sets payment name on card text.
     *
     * @param paymentNameOnCardText the payment name on card text
     */
    public void setPaymentNameOnCardText(String paymentNameOnCardText) {
        this.paymentNameOnCardText = paymentNameOnCardText;
    }

    /**
     * Gets payment billing address text.
     *
     * @return the payment billing address text
     */
    public String getPaymentBillingAddressText() {
        return StringUtil.getLabelForView(paymentBillingAddressText);
    }

    /**
     * Sets payment billing address text.
     *
     * @param paymentBillingAddressText the payment billing address text
     */
    public void setPaymentBillingAddressText(String paymentBillingAddressText) {
        this.paymentBillingAddressText = paymentBillingAddressText;
    }

    /**
     * Gets payment address line 1 text.
     *
     * @return the payment address line 1 text
     */
    public String getPaymentAddressLine1Text() {
        return StringUtil.getLabelForView(paymentAddressLine1Text);
    }

    /**
     * Sets payment address line 1 text.
     *
     * @param paymentAddressLine1Text the payment address line 1 text
     */
    public void setPaymentAddressLine1Text(String paymentAddressLine1Text) {
        this.paymentAddressLine1Text = paymentAddressLine1Text;
    }

    /**
     * Gets payment address line 2 text.
     *
     * @return the payment address line 2 text
     */
    public String getPaymentAddressLine2Text() {
        return StringUtil.getLabelForView(paymentAddressLine2Text);
    }

    /**
     * Sets payment address line 2 text.
     *
     * @param paymentAddressLine2Text the payment address line 2 text
     */
    public void setPaymentAddressLine2Text(String paymentAddressLine2Text) {
        this.paymentAddressLine2Text = paymentAddressLine2Text;
    }

    /**
     * Gets payment city.
     *
     * @return the payment city
     */
    public String getPaymentCity() {
        return StringUtil.getLabelForView(paymentCity);
    }

    /**
     * Sets payment city.
     *
     * @param paymentCity the payment city
     */
    public void setPaymentCity(String paymentCity) {
        this.paymentCity = paymentCity;
    }

    /**
     * Gets payment state.
     *
     * @return the payment state
     */
    public String getPaymentState() {
        return StringUtil.getLabelForView(paymentState);
    }

    /**
     * Sets payment state.
     *
     * @param paymentState the payment state
     */
    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    /**
     * Gets payment country.
     *
     * @return the payment country
     */
    public String getPaymentCountry() {
        return StringUtil.getLabelForView(paymentCountry);
    }

    /**
     * Sets payment country.
     *
     * @param paymentCountry the payment country
     */
    public void setPaymentCountry(String paymentCountry) {
        this.paymentCountry = paymentCountry;
    }

    /**
     * Gets payment zipcode.
     *
     * @return the payment zipcode
     */
    public String getPaymentZipcode() {
        return StringUtil.getLabelForView(paymentZipcode);
    }

    /**
     * Sets payment zipcode.
     *
     * @param paymentZipcode the payment zipcode
     */
    public void setPaymentZipcode(String paymentZipcode) {
        this.paymentZipcode = paymentZipcode;
    }

    /**
     * Gets payment pay text.
     *
     * @return the payment pay text
     */
    public String getPaymentPayText() {
        return StringUtil.getLabelForView(paymentPayText);
    }

    /**
     * Sets payment pay text.
     *
     * @param paymentPayText the payment pay text
     */
    public void setPaymentPayText(String paymentPayText) {
        this.paymentPayText = paymentPayText;
    }

    /**
     * Gets payment use profile address.
     *
     * @return the payment use profile address
     */
    public String getPaymentUseProfileAddress() {
        return StringUtil.getLabelForView(paymentUseProfileAddress);
    }

    /**
     * Sets payment use profile address.
     *
     * @param paymentUseProfileAddress the payment use profile address
     */
    public void setPaymentUseProfileAddress(String paymentUseProfileAddress) {
        this.paymentUseProfileAddress = paymentUseProfileAddress;
    }

    /**
     * Gets payment set as default credit card.
     *
     * @return the payment set as default credit card
     */
    public String getPaymentSetAsDefaultCreditCard() {
        return StringUtil.getLabelForView(paymentSetAsDefaultCreditCard);
    }

    /**
     * Sets payment set as default credit card.
     *
     * @param paymentSetAsDefaultCreditCard the payment set as default credit card
     */
    public void setPaymentSetAsDefaultCreditCard(String paymentSetAsDefaultCreditCard) {
        this.paymentSetAsDefaultCreditCard = paymentSetAsDefaultCreditCard;
    }

    /**
     *
     * @return paymentChangeMethodButton
     */
    public String getPaymentChangeMethodButton() {
        return StringUtil.getLabelForView(paymentChangeMethodButton);
    }

    /**
     *
     * @param paymentChangeMethodButton paymentChangeMethodButton
     */
    public void setPaymentChangeMethodButton(String paymentChangeMethodButton) {
        this.paymentChangeMethodButton = paymentChangeMethodButton;
    }

    /**
     *
     * @return paymentFailedErrorMessage
     */
    public String getPaymentFailedErrorMessage() {
        return StringUtil.getLabelForView(paymentFailedErrorMessage);
    }

    /**
     *
     * @param paymentFailedErrorMessage paymentFailedErrorMessage
     */
    public void setPaymentFailedErrorMessage(String paymentFailedErrorMessage) {
        this.paymentFailedErrorMessage = paymentFailedErrorMessage;
    }

    /**
     *
     * @return paymentReceiptTotalAmount
     */
    public String getPaymentReceiptTotalAmount() {
        return StringUtil.getLabelForView(paymentReceiptTotalAmount);
    }

    /**
     *
      * @param paymentReceiptTotalAmount paymentReceiptTotalAmount
     */
    public void setPaymentReceiptTotalAmount(String paymentReceiptTotalAmount) {
        this.paymentReceiptTotalAmount = paymentReceiptTotalAmount;
    }

    /**
     *
     * @return paymentReceiptSaveReceipt
     */
    public String getPaymentReceiptSaveReceipt() {
        return StringUtil.getLabelForView(paymentReceiptSaveReceipt);
    }

    /**
     *
     * @param paymentReceiptSaveReceipt paymentReceiptSaveReceipt
     */
    public void setPaymentReceiptSaveReceipt(String paymentReceiptSaveReceipt) {
        this.paymentReceiptSaveReceipt = paymentReceiptSaveReceipt;
    }

    /**
     *
     * @return paymentReceiptShareReceipt
     */
    public String getPaymentReceiptShareReceipt() {
        return StringUtil.getLabelForView(paymentReceiptShareReceipt);
    }

    /**
     *
     * @param paymentReceiptShareReceipt paymentReceiptShareReceipt
     */
    public void setPaymentReceiptShareReceipt(String paymentReceiptShareReceipt) {
        this.paymentReceiptShareReceipt = paymentReceiptShareReceipt;
    }

    /**
     *
     * @return paymentReceiptPaymentType
     */
    public String getPaymentReceiptPaymentType() {
        return StringUtil.getLabelForView(paymentReceiptPaymentType);
    }

    /**
     *
     * @param paymentReceiptPaymentType paymentReceiptPaymentType
     */
    public void setPaymentReceiptPaymentType(String paymentReceiptPaymentType) {
        this.paymentReceiptPaymentType = paymentReceiptPaymentType;
    }

    /**
     *
     * @return paymentReceiptNoLabel
     */
    public String getPaymentReceiptNoLabel() {
        return StringUtil.getLabelForView(paymentReceiptNoLabel);
    }

    /**
     *
     * @param paymentReceiptNoLabel paymentReceiptNoLabel
     */
    public void setPaymentReceiptNoLabel(String paymentReceiptNoLabel) {
        this.paymentReceiptNoLabel = paymentReceiptNoLabel;
    }

    /**
     *
     * @return paymentReceiptTitle
     */
    public String getPaymentReceiptTitle() {
        return StringUtil.getLabelForView(paymentReceiptTitle);
    }

    /**
     *
     * @param paymentReceiptTitle paymentReceiptTitle
     */
    public void setPaymentReceiptTitle(String paymentReceiptTitle) {
        this.paymentReceiptTitle = paymentReceiptTitle;
    }

    /**
     *
     * @return paymentReceiptTotalPaidLabel
     */
    public String getPaymentReceiptTotalPaidLabel() {
        return StringUtil.getLabelForView(paymentReceiptTotalPaidLabel);
    }

    /**
     *
     * @param paymentReceiptTotalPaidLabel paymentReceiptTotalPaidLabel
     */
    public void setPaymentReceiptTotalPaidLabel(String paymentReceiptTotalPaidLabel) {
        this.paymentReceiptTotalPaidLabel = paymentReceiptTotalPaidLabel;
    }

    /**
     *
     * @return paymentResponsibilityDetails
     */
    public String getPaymentResponsibilityDetails() {
        return StringUtil.getLabelForView(paymentResponsibilityDetails);
    }

    /**
     *
     * @param paymentResponsibilityDetails paymentResponsibilityDetails
     */
    public void setPaymentResponsibilityDetails(String paymentResponsibilityDetails) {
        this.paymentResponsibilityDetails = paymentResponsibilityDetails;
    }

    /**
     *
     * @return paymentResponsibilityPayLater
     */
    public String getPaymentResponsibilityPayLater() {
        return StringUtil.getLabelForView(paymentResponsibilityPayLater);
    }

    /**
     *
     * @param paymentResponsibilityPayLater paymentResponsibilityPayLater
     */
    public void setPaymentResponsibilityPayLater(String paymentResponsibilityPayLater) {
        this.paymentResponsibilityPayLater = paymentResponsibilityPayLater;
    }

    /**
     *
     * @return paymentDetailsPayNow
     */
    public String getPaymentDetailsPayNow() {
        return StringUtil.getLabelForView(paymentDetailsPayNow);
    }

    /**
     *
     * @param paymentDetailsPayNow paymentDetailsPayNow
     */
    public void setPaymentDetailsPayNow(String paymentDetailsPayNow) {
        this.paymentDetailsPayNow = paymentDetailsPayNow;
    }

    public String getPaymentPatientBalanceToolbar() {
        return paymentPatientBalanceToolbar;
    }

    public void setPaymentPatientBalanceToolbar(String paymentPatientBalanceToolbar) {
        this.paymentPatientBalanceToolbar = paymentPatientBalanceToolbar;
    }

    public String getPaymentPatientBalanceTab() {
        return paymentPatientBalanceTab;
    }

    public void setPaymentPatientBalanceTab(String paymentPatientBalanceTab) {
        this.paymentPatientBalanceTab = paymentPatientBalanceTab;
    }

    public String getPaymentPatientHistoryTab() {
        return paymentPatientHistoryTab;
    }

    public void setPaymentPatientHistoryTab(String paymentPatientHistoryTab) {
        this.paymentPatientHistoryTab = paymentPatientHistoryTab;
    }

    /**
     *
     * @return paymentDetailsPatientBalanceLabel
     */
    public String getPaymentDetailsPatientBalanceLabel() {
        return StringUtil.getLabelForView(paymentDetailsPatientBalanceLabel);
    }

    /**
     *
     * @param paymentDetailsPatientBalanceLabel paymentDetailsPatientBalanceLabel
     */
    public void setPaymentDetailsPatientBalanceLabel(String paymentDetailsPatientBalanceLabel) {
        this.paymentDetailsPatientBalanceLabel = paymentDetailsPatientBalanceLabel;
    }

    /**
     *
     * @return practicePaymentsBackLabel
     */
    public String getPracticePaymentsBackLabel() {
        return StringUtil.getLabelForView(practicePaymentsBackLabel);
    }

    /**
     *
     * @param practicePaymentsBackLabel practicePaymentsBackLabel
     */
    public void setPracticePaymentsBackLabel(String practicePaymentsBackLabel) {
        this.practicePaymentsBackLabel = practicePaymentsBackLabel;
    }

    /**
     *
     * @return practicePaymentsHeader
     */
    public String getPracticePaymentsHeader() {
        return StringUtil.getLabelForView(practicePaymentsHeader);
    }

    /**
     *
     * @param practicePaymentsHeader practicePaymentsHeader
     */
    public void setPracticePaymentsHeader(String practicePaymentsHeader) {
        this.practicePaymentsHeader = practicePaymentsHeader;
    }

    /**
     *
     * @return practicePaymentsFindPatientLabel
     */
    public String getPracticePaymentsFindPatientLabel() {
        return StringUtil.getLabelForView(practicePaymentsFindPatientLabel);
    }

    /**
     *
     * @param practicePaymentsFindPatientLabel practicePaymentsFindPatientLabel
     */
    public void setPracticePaymentsFindPatientLabel(String practicePaymentsFindPatientLabel) {
        this.practicePaymentsFindPatientLabel = practicePaymentsFindPatientLabel;
    }

    /**
     *
     * @return practicePaymentsFilter
     */
    public String getPracticePaymentsFilter() {
        return StringUtil.getLabelForView(practicePaymentsFilter);
    }

    /**
     *
     * @param practicePaymentsFilter practicePaymentsFilter
     */
    public void setPracticePaymentsFilter(String practicePaymentsFilter) {
        this.practicePaymentsFilter = practicePaymentsFilter;
    }

    /**
     *
     * @return practicePaymentsInOffice
     */
    public String getPracticePaymentsInOffice() {
        return StringUtil.getLabelForView(practicePaymentsInOffice);
    }

    /**
     *
     * @param practicePaymentsInOffice practicePaymentsInOffice
     */
    public void setPracticePaymentsInOffice(String practicePaymentsInOffice) {
        this.practicePaymentsInOffice = practicePaymentsInOffice;
    }

    /**
     *
     * @return practicePaymentsDetailDialogBalance
     */
    public String getPracticePaymentsDetailDialogBalance() {
        return StringUtil.getLabelForView(practicePaymentsDetailDialogBalance);
    }

    /**
     *
     * @return practicePaymentsDetailDialogPaymentPlan
     */
    public String getPracticePaymentsDetailDialogPaymentPlan() {
        return StringUtil.getLabelForView(practicePaymentsDetailDialogPaymentPlan);
    }

    /**
     *
     * @return practicePaymentsDetailDialogPay
     */
    public String getPracticePaymentsDetailDialogPay() {
        return StringUtil.getLabelForView(practicePaymentsDetailDialogPay);
    }

    /**
     *
     * @param practicePaymentsDetailDialogBalance practicePaymentsDetailDialogBalance
     */
    public void setPracticePaymentsDetailDialogBalance(String practicePaymentsDetailDialogBalance) {
        this.practicePaymentsDetailDialogBalance = practicePaymentsDetailDialogBalance;
    }

    /**
     *
     * @param practicePaymentsDetailDialogPaymentPlan practicePaymentsDetailDialogPaymentPlan
     */
    public void setPracticePaymentsDetailDialogPaymentPlan(String practicePaymentsDetailDialogPaymentPlan) {
        this.practicePaymentsDetailDialogPaymentPlan = practicePaymentsDetailDialogPaymentPlan;
    }

    /**
     *
     * @param practicePaymentsDetailDialogPay practicePaymentsDetailDialogPay
     */
    public void setPracticePaymentsDetailDialogPay(String practicePaymentsDetailDialogPay) {
        this.practicePaymentsDetailDialogPay = practicePaymentsDetailDialogPay;
    }

    /**
     *
     * @param practicePaymentsDetailDialogLabel practicePaymentsDetailDialogLabel
     */
    public void setPracticePaymentsDetailDialogLabel(String practicePaymentsDetailDialogLabel) {
        this.practicePaymentsDetailDialogLabel = practicePaymentsDetailDialogLabel;
    }

    /**
     *
     * @return practicePaymentsDetailDialogLabel
     */
    public String getPracticePaymentsDetailDialogLabel() {
        return StringUtil.getLabelForView(practicePaymentsDetailDialogLabel);
    }

    /**
     *
     * @return practicePaymentsDetailDialogCloseButton
     */
    public String getPracticePaymentsDetailDialogCloseButton() {
        return StringUtil.getLabelForView(practicePaymentsDetailDialogCloseButton);
    }

    /**
     *
     * @param practicePaymentsDetailDialogCloseButton practicePaymentsDetailDialogCloseButton
     */
    public void setPracticePaymentsDetailDialogCloseButton(String practicePaymentsDetailDialogCloseButton) {
        this.practicePaymentsDetailDialogCloseButton = practicePaymentsDetailDialogCloseButton;
    }
}