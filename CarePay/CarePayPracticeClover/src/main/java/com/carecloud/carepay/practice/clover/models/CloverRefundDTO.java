package com.carecloud.carepay.practice.clover.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/20/17
 */

public class CloverRefundDTO {

    @SerializedName("message")
    private String message;

    @SerializedName("result")
    private String result;

    @SerializedName("success")
    private boolean success;

    @SerializedName("reason")
    private String reason;

    @SerializedName("refund")
    private Refund refund = new Refund();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }


    public class Refund {

        @SerializedName("amount")
        private long amount;

        @SerializedName("id")
        private String id;

        @SerializedName("payment")
        private Payment payment = new Payment();

        @SerializedName("employee")
        private Employee employee = new Employee();

        public Employee getEmployee() {
            return employee;
        }

        public void setEmployee(Employee employee) {
            this.employee = employee;
        }

        public Payment getPayment() {
            return payment;
        }

        public void setPayment(Payment payment) {
            this.payment = payment;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public class Payment {

            @SerializedName("id")
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }

        public class Employee {

            @SerializedName("id")
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }
}
