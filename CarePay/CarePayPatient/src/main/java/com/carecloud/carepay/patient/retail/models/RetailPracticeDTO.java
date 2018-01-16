package com.carecloud.carepay.patient.retail.models;

import com.carecloud.carepaylibray.appointments.models.PracticePatientIdsDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 11/20/17
 */

public class RetailPracticeDTO extends PracticePatientIdsDTO {

    @SerializedName("store")
    private Store store;

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }


    public class Store {

        @SerializedName("store_id")
        private String storeId;

        @SerializedName("store_type")
        private String storeType;

        @SerializedName("html")
        private String storeHtml;

        @SerializedName("sso")
        private RetailPracticeSsoDTO sso;

        public String getStoreId() {
            return storeId;
        }

        public void setStoreId(String storeId) {
            this.storeId = storeId;
        }

        public String getStoreType() {
            return storeType;
        }

        public void setStoreType(String storeType) {
            this.storeType = storeType;
        }

        public String getStoreHtml() {
            return storeHtml;
        }

        public void setStoreHtml(String storeHtml) {
            this.storeHtml = storeHtml;
        }

        public RetailPracticeSsoDTO getSso() {
            return sso;
        }

        public void setSso(RetailPracticeSsoDTO sso) {
            this.sso = sso;
        }
    }
}
