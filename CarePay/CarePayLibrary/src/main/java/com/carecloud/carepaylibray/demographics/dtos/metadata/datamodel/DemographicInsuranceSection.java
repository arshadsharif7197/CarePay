package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 6/15/17
 */

public class DemographicInsuranceSection {

    @SerializedName("properties")
    @Expose
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public class Properties{

        @SerializedName("items")
        @Expose
        private Items items = new Items();

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public class Items {

            @SerializedName("insurance")
            @Expose
            private InsuranceModel insuranceModel = new InsuranceModel();

            public InsuranceModel getInsuranceModel() {
                return insuranceModel;
            }

            public void setInsuranceModel(InsuranceModel insuranceModel) {
                this.insuranceModel = insuranceModel;
            }

            public class InsuranceModel {

                @SerializedName("properties")
                @Expose
                private InsuranceModelProperties insuranceModelProperties = new InsuranceModelProperties();

                public InsuranceModelProperties getInsuranceModelProperties() {
                    return insuranceModelProperties;
                }

                public void setInsuranceModelProperties(InsuranceModelProperties insuranceModelProperties) {
                    this.insuranceModelProperties = insuranceModelProperties;
                }
            }
        }
    }

}
