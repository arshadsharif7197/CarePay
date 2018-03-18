
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaultErrorBodyDTO {

    @SerializedName("error")
    @Expose
    private ErrorDTO error;

    public ErrorDTO getError() {
        return error;
    }

    public void setError(ErrorDTO error) {
        this.error = error;
    }

}
