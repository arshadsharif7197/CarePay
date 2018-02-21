package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 16/02/18.
 */

public class StatementDTO {

    @Expose
    @SerializedName("id")
    private int id;

    @Expose
    @SerializedName("statement_date")
    private String statementDate;

    @Expose
    @SerializedName("account_number")
    private String accountNumber;

    @Expose
    @SerializedName("amount_due")
    private double amountDue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatementDate() {
        return statementDate;
    }

    public void setStatementDate(String statementDate) {
        this.statementDate = statementDate;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }
}
