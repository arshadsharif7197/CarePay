
package com.carecloud.breezemini.payments.postmodel.credittransaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreditTransactionDTO {

    @SerializedName("creditTransactionSourceId")
    @Expose
    private String creditTransactionSourceId;
    @SerializedName("batchNumber")
    @Expose
    private String batchNumber;
    @SerializedName("cardName")
    @Expose
    private String cardName;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("eRemittanceId")
    @Expose
    private String eRemittanceId;
    @SerializedName("clientBillingPeriodId")
    @Expose
    private String clientBillingPeriodId;
    @SerializedName("cardCcv")
    @Expose
    private String cardCcv;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("checkDate")
    @Expose
    private String checkDate;
    @SerializedName("businessEntityId")
    @Expose
    private String businessEntityId;
    @SerializedName("creditTransactionType")
    @Expose
    private CreditTransactionTypeDTO creditTransactionType;
    @SerializedName("creatorNameApplication")
    @Expose
    private String creatorNameApplication;
    @SerializedName("payerName")
    @Expose
    private String payerName;
    @SerializedName("isElectronic")
    @Expose
    private boolean isElectronic;
    @SerializedName("effectiveDate")
    @Expose
    private String effectiveDate;
    @SerializedName("papiChargeId")
    @Expose
    private String papiChargeId;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("creditTransactionSubtype")
    @Expose
    private CreditTransactionSubtypeDTO creditTransactionSubtype;
    @SerializedName("papiCardData")
    @Expose
    private String papiCardData;
    @SerializedName("creditTransactionSource")
    @Expose
    private String creditTransactionSource;
    @SerializedName("abstractBoolean")
    @Expose
    private boolean abstractBoolean;
    @SerializedName("batchIdentifier")
    @Expose
    private String batchIdentifier;
    @SerializedName("documentSetId")
    @Expose
    private String documentSetId;
    @SerializedName("companionTransactionId")
    @Expose
    private String companionTransactionId;
    @SerializedName("creatorName")
    @Expose
    private String creatorName;
    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;
    @SerializedName("checkAmount")
    @Expose
    private String checkAmount;
    @SerializedName("enteredBy")
    @Expose
    private String enteredBy;
    @SerializedName("cardTypeId")
    @Expose
    private String cardTypeId;
    @SerializedName("remittanceId")
    @Expose
    private String remittanceId;
    @SerializedName("comments")
    @Expose
    private String comments;
    @SerializedName("enteredByName")
    @Expose
    private String enteredByName;
    @SerializedName("unappliedCredit")
    @Expose
    private String unappliedCredit;
    @SerializedName("postingDate")
    @Expose
    private String postingDate;
    @SerializedName("accountNumber")
    @Expose
    private String accountNumber;
    @SerializedName("transactionStatus")
    @Expose
    private TransactionStatusDTO transactionStatus;
    @SerializedName("reversedBy")
    @Expose
    private String reversedBy;
    @SerializedName("updaterNameApplication")
    @Expose
    private String updaterNameApplication;
    @SerializedName("updaterName")
    @Expose
    private String updaterName;
    @SerializedName("reversedAt")
    @Expose
    private String reversedAt;
    @SerializedName("providerId")
    @Expose
    private String providerId;
    @SerializedName("isReversed")
    @Expose
    private boolean isReversed;
    @SerializedName("cid")
    @Expose
    private String cid;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("documentHandler")
    @Expose
    private String documentHandler;
    @SerializedName("payerId")
    @Expose
    private String payerId;
    @SerializedName("otherAdjustmentReasonId")
    @Expose
    private String otherAdjustmentReasonId;
    @SerializedName("checkNumber")
    @Expose
    private String checkNumber;
    @SerializedName("payerClaimNumber")
    @Expose
    private String payerClaimNumber;
    @SerializedName("locationId")
    @Expose
    private String locationId;
    @SerializedName("pageNumber")
    @Expose
    private String pageNumber;
    @SerializedName("enteredAt")
    @Expose
    private String enteredAt;
    @SerializedName("creditTransactionSubtypeId")
    @Expose
    private String creditTransactionSubtypeId;
    @SerializedName("cardNumber")
    @Expose
    private String cardNumber;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("papiChargeRefundableBalance")
    @Expose
    private String papiChargeRefundableBalance;
    @SerializedName("creditTransactionTypeId")
    @Expose
    private String creditTransactionTypeId;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("merchantServiceTransactionId")
    @Expose
    private String merchantServiceTransactionId;
    @SerializedName("documentFormat")
    @Expose
    private String documentFormat;
    @SerializedName("patientId")
    @Expose
    private String patientId;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("cardExpiration")
    @Expose
    private String cardExpiration;
    @SerializedName("papiRefundId")
    @Expose
    private String papiRefundId;

    public String getCreditTransactionSourceId() {
        return creditTransactionSourceId;
    }

    public void setCreditTransactionSourceId(String creditTransactionSourceId) {
        this.creditTransactionSourceId = creditTransactionSourceId;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getAmount() {
            return amount != null ? amount : "0";
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getERemittanceId() {
        return eRemittanceId;
    }

    public void setERemittanceId(String eRemittanceId) {
        this.eRemittanceId = eRemittanceId;
    }

    public String getClientBillingPeriodId() {
        return clientBillingPeriodId;
    }

    public void setClientBillingPeriodId(String clientBillingPeriodId) {
        this.clientBillingPeriodId = clientBillingPeriodId;
    }

    public String getCardCcv() {
        return cardCcv;
    }

    public void setCardCcv(String cardCcv) {
        this.cardCcv = cardCcv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getBusinessEntityId() {
        return businessEntityId;
    }

    public void setBusinessEntityId(String businessEntityId) {
        this.businessEntityId = businessEntityId;
    }

    public CreditTransactionTypeDTO getCreditTransactionType() {
        return creditTransactionType;
    }

    public void setCreditTransactionType(CreditTransactionTypeDTO creditTransactionType) {
        this.creditTransactionType = creditTransactionType;
    }

    public String getCreatorNameApplication() {
        return creatorNameApplication;
    }

    public void setCreatorNameApplication(String creatorNameApplication) {
        this.creatorNameApplication = creatorNameApplication;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    public boolean getIsElectronic() {
        return isElectronic;
    }

    public void setIsElectronic(boolean isElectronic) {
        this.isElectronic = isElectronic;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getPapiChargeId() {
        return papiChargeId;
    }

    public void setPapiChargeId(String papiChargeId) {
        this.papiChargeId = papiChargeId;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public CreditTransactionSubtypeDTO getCreditTransactionSubtype() {
        return creditTransactionSubtype;
    }

    public void setCreditTransactionSubtype(CreditTransactionSubtypeDTO creditTransactionSubtype) {
        this.creditTransactionSubtype = creditTransactionSubtype;
    }

    public String getPapiCardData() {
        return papiCardData;
    }

    public void setPapiCardData(String papiCardData) {
        this.papiCardData = papiCardData;
    }

    public String getCreditTransactionSource() {
        return creditTransactionSource;
    }

    public void setCreditTransactionSource(String creditTransactionSource) {
        this.creditTransactionSource = creditTransactionSource;
    }

    public boolean getAbstractBoolean() {
        return abstractBoolean;
    }

    public void setAbstractBoolean(boolean abstractBoolean) {
        this.abstractBoolean = abstractBoolean;
    }

    public String getBatchIdentifier() {
        return batchIdentifier;
    }

    public void setBatchIdentifier(String batchIdentifier) {
        this.batchIdentifier = batchIdentifier;
    }

    public String getDocumentSetId() {
        return documentSetId;
    }

    public void setDocumentSetId(String documentSetId) {
        this.documentSetId = documentSetId;
    }

    public String getCompanionTransactionId() {
        return companionTransactionId;
    }

    public void setCompanionTransactionId(String companionTransactionId) {
        this.companionTransactionId = companionTransactionId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(String checkAmount) {
        this.checkAmount = checkAmount;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public String getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getRemittanceId() {
        return remittanceId;
    }

    public void setRemittanceId(String remittanceId) {
        this.remittanceId = remittanceId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEnteredByName() {
        return enteredByName;
    }

    public void setEnteredByName(String enteredByName) {
        this.enteredByName = enteredByName;
    }

    public String getUnappliedCredit() {
        return unappliedCredit;
    }

    public void setUnappliedCredit(String unappliedCredit) {
        this.unappliedCredit = unappliedCredit;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionStatusDTO getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatusDTO transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getReversedBy() {
        return reversedBy;
    }

    public void setReversedBy(String reversedBy) {
        this.reversedBy = reversedBy;
    }

    public String getUpdaterNameApplication() {
        return updaterNameApplication;
    }

    public void setUpdaterNameApplication(String updaterNameApplication) {
        this.updaterNameApplication = updaterNameApplication;
    }

    public String getUpdaterName() {
        return updaterName;
    }

    public void setUpdaterName(String updaterName) {
        this.updaterName = updaterName;
    }

    public String getReversedAt() {
        return reversedAt;
    }

    public void setReversedAt(String reversedAt) {
        this.reversedAt = reversedAt;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public boolean getIsReversed() {
        return isReversed;
    }

    public void setIsReversed(boolean isReversed) {
        this.isReversed = isReversed;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDocumentHandler() {
        return documentHandler;
    }

    public void setDocumentHandler(String documentHandler) {
        this.documentHandler = documentHandler;
    }

    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }

    public String getOtherAdjustmentReasonId() {
        return otherAdjustmentReasonId;
    }

    public void setOtherAdjustmentReasonId(String otherAdjustmentReasonId) {
        this.otherAdjustmentReasonId = otherAdjustmentReasonId;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public String getPayerClaimNumber() {
        return payerClaimNumber;
    }

    public void setPayerClaimNumber(String payerClaimNumber) {
        this.payerClaimNumber = payerClaimNumber;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

    public String getEnteredAt() {
        return enteredAt;
    }

    public void setEnteredAt(String enteredAt) {
        this.enteredAt = enteredAt;
    }

    public String getCreditTransactionSubtypeId() {
        return creditTransactionSubtypeId;
    }

    public void setCreditTransactionSubtypeId(String creditTransactionSubtypeId) {
        this.creditTransactionSubtypeId = creditTransactionSubtypeId;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPapiChargeRefundableBalance() {
        return papiChargeRefundableBalance;
    }

    public void setPapiChargeRefundableBalance(String papiChargeRefundableBalance) {
        this.papiChargeRefundableBalance = papiChargeRefundableBalance;
    }

    public String getCreditTransactionTypeId() {
        return creditTransactionTypeId;
    }

    public void setCreditTransactionTypeId(String creditTransactionTypeId) {
        this.creditTransactionTypeId = creditTransactionTypeId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMerchantServiceTransactionId() {
        return merchantServiceTransactionId;
    }

    public void setMerchantServiceTransactionId(String merchantServiceTransactionId) {
        this.merchantServiceTransactionId = merchantServiceTransactionId;
    }

    public String getDocumentFormat() {
        return documentFormat;
    }

    public void setDocumentFormat(String documentFormat) {
        this.documentFormat = documentFormat;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCardExpiration() {
        return cardExpiration;
    }

    public void setCardExpiration(String cardExpiration) {
        this.cardExpiration = cardExpiration;
    }

    public String getPapiRefundId() {
        return papiRefundId;
    }

    public void setPapiRefundId(String papiRefundId) {
        this.papiRefundId = papiRefundId;
    }

}
