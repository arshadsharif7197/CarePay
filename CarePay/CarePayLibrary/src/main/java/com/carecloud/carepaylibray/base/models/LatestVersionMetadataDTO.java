package com.carecloud.carepaylibray.base.models;

import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LatestVersionMetadataDTO {

    @SerializedName("links")
    @Expose
    private LatestVersionMetadataLinksDTO links = new LatestVersionMetadataLinksDTO();

    public LatestVersionMetadataLinksDTO getLinks() {
        return links;
    }

    public void setLinks(LatestVersionMetadataLinksDTO links) {
        this.links = links;
    }

    public class LatestVersionMetadataLinksDTO {

        @SerializedName("check_latest_version")
        @Expose
        private TransitionDTO checkLatestVersion = new TransitionDTO();

        public TransitionDTO getCheckLatestVersion() {
            return checkLatestVersion;
        }

        public void setCheckLatestVersion(TransitionDTO checkLatestVersion) {
            this.checkLatestVersion = checkLatestVersion;
        }
    }
}
