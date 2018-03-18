package com.carecloud.carepay.patient.payment;

import android.os.Bundle;

import java.util.List;

/**
 * Created by kkannan on 12/18/16.
 */

public class PaymentResponsibilityModel {

    private static PaymentResponsibilityModel instance;

    public Bundle getArguments() {
        return arguments;
    }

    public void setArguments(Bundle arguments) {
        this.arguments = arguments;
    }

    private Bundle arguments ;

    public List getBalancesList() {
        return balancesList;
    }

    public void setBalancesList(List balancesList) {
        this.balancesList = balancesList;
    }

    public List balancesList;

    public String getBalance1() {
        return balance1;
    }

    public void setBalance1(String balance1) {
        this.balance1 = balance1;
    }

    public String balance1;



    public static PaymentResponsibilityModel getInstance() {
        if (instance == null) {
            instance = new PaymentResponsibilityModel();


        }
        return instance;
    }
}
