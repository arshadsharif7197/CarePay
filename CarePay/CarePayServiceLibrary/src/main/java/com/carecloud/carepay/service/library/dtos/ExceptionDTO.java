
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExceptionDTO {

    @SerializedName("body")
    @Expose
    private FaultErrorBodyDTO body;

    public FaultErrorBodyDTO getBody() {
        return body;
    }

    public void setBody(FaultErrorBodyDTO body) {
        this.body = body;
    }

}
