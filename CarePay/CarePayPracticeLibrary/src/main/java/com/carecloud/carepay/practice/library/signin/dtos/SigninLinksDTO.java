package com.carecloud.carepay.practice.library.signin.dtos;

import com.carecloud.carepaylibray.base.models.BaseLinkModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Jahirul Bhuiyan on 10/25/2016.
 */
@Deprecated
public class SigninLinksDTO {
    @SerializedName("self")
    @Expose
    private BaseLinkModel self = new BaseLinkModel();

    /**
     *
     * @return
     * The self
     */
    public BaseLinkModel getSelf() {
        return self;
    }

    /**
     *
     * @param self
     * The self
     */
    public void setSelf(BaseLinkModel self) {
        this.self = self;
    }
}
