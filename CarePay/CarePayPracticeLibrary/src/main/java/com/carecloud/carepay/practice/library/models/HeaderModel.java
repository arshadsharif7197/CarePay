package com.carecloud.carepay.practice.library.models;

import java.io.Serializable;

/**
 * Created by pjohnson on 23/03/17.
 */
public class HeaderModel implements Serializable {

    private String headerFullTitle;
    private String headerShortTitle;
    private String headerSubtitle;
    private String headerPhotoUrl;

    public String getHeaderFullTitle() {
        return headerFullTitle;
    }

    public void setHeaderFullTitle(String headerFullTitle) {
        this.headerFullTitle = headerFullTitle;
    }

    public String getHeaderSubtitle() {
        return headerSubtitle;
    }

    public String getHeaderShortTitle() {
        return headerShortTitle;
    }

    public void setHeaderShortTitle(String headerShortTitle) {
        this.headerShortTitle = headerShortTitle;
    }

    public void setHeaderSubtitle(String headerSubtitle) {
        this.headerSubtitle = headerSubtitle;
    }

    public String getHeaderPhotoUrl() {
        return headerPhotoUrl;
    }

    public void setHeaderPhotoUrl(String headerPhotoUrl) {
        this.headerPhotoUrl = headerPhotoUrl;
    }
}
