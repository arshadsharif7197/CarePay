package com.carecloud.carepay.patient.payment.androidpay.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 10/18/17
 */

public class PayeezyAndroidPayResponse {

    @SerializedName("transaction_status")
    private String transactionStatus;

    @SerializedName("currency")
    private String currency;

    @SerializedName("amount")
    private int amount;

    @SerializedName("token")
    private Token token = new Token();

    @SerializedName("card")
    private Card card;

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public class Token {

        @SerializedName("token_type")
        private String tokenType;

        @SerializedName("token_data")
        private TokenData tokenData = new TokenData();

        public String getTokenType() {
            return tokenType;
        }

        public void setTokenType(String tokenType) {
            this.tokenType = tokenType;
        }

        public TokenData getTokenData() {
            return tokenData;
        }

        public void setTokenData(TokenData tokenData) {
            this.tokenData = tokenData;
        }

        public class TokenData {

            @SerializedName("value")
            private String value;

            public String getValue() {
                return value;
            }

            public void setValue(String value) {
                this.value = value;
            }
        }
    }

    public class Card {

        @SerializedName("type")
        private String type;

        @SerializedName("cardholder_name")
        private String cardholderName;

        @SerializedName("card_number")
        private String cardNumber;

        @SerializedName("exp_date")
        private String expDate;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCardholderName() {
            return cardholderName;
        }

        public void setCardholderName(String cardholderName) {
            this.cardholderName = cardholderName;
        }

        public String getCardNumber() {
            return cardNumber;
        }

        public void setCardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
        }

        public String getExpDate() {
            return expDate;
        }

        public void setExpDate(String expDate) {
            this.expDate = expDate;
        }
    }

}
