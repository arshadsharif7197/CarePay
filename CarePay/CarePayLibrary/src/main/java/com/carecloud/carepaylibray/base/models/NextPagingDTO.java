package com.carecloud.carepaylibray.base.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/28/17
 */

public class NextPagingDTO {

    @SerializedName("page_number")
    private long nextPage;

    @SerializedName("page_transaction_count")
    private long pageCount;

    public long getNextPage() {
        return nextPage;
    }

    public void setNextPage(long nextPage) {
        this.nextPage = nextPage;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }
}
