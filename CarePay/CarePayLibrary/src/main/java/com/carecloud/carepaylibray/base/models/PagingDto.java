package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

/**
 * @author pjohnson on 18/07/17.
 */

public class PagingDto {

    @SerializedName("paging")
    private Paging paging = new Paging();

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}
