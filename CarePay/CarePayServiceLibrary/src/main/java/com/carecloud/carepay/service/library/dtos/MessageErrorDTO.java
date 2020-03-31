
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessageErrorDTO {

    @SerializedName("body")
    @Expose
    private ErrorBodyDTO body = new ErrorBodyDTO();

    public ErrorBodyDTO getBody() {
        return body;
    }

    public void setBody(ErrorBodyDTO body) {
        this.body = body;
    }

}
