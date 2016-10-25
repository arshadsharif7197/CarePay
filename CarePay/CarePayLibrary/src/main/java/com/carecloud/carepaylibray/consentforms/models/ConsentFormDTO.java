package com.carecloud.carepaylibray.consentforms.models;

/**
 * Created by Rahul on 10/21/16.
 */

    import com.google.gson.annotations.Expose;
    import com.google.gson.annotations.SerializedName;
    import com.smartystreets.api.us_street.Metadata;

    public class ConsentFormDTO {

        @SerializedName("metadata")
        @Expose
        private ConsentFormMetadataDTO metadata;

        @SerializedName("state")
        @Expose
        private String state;

        /**
         *
         * @return
         * The metadata
         */
        public ConsentFormMetadataDTO getMetadata() {
            return metadata;
        }

        /**
         *
         * @param metadata
         * The metadata
         */
        public void setMetadata(ConsentFormMetadataDTO metadata) {
            this.metadata = metadata;
        }

        /**
         *
         * @return
         * The state
         */
        public String getState() {
            return state;
        }

        /**
         *
         * @param state
         * The state
         */
        public void setState(String state) {
            this.state = state;
        }

    }

