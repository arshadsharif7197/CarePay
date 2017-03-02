
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FaultResponseDTO {

    private static final String ERROR_CODE_UNPROCESSABLE_ENTITY = "422";

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("message")
    @Expose
    private ErrorMessageDTO errorMessageDTO;
    @SerializedName("call")
    @Expose
    private String call;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ErrorMessageDTO getErrorMessageDTO() {
        return errorMessageDTO;
    }

    public void setErrorMessageDTO(ErrorMessageDTO errorMessageDTO) {
        this.errorMessageDTO = errorMessageDTO;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    /**
     * Is unprocessable entity error boolean.
     *
     * @return the boolean
     */
    public boolean isUnprocessableEntityError(){
        return  getStatus().equalsIgnoreCase(ERROR_CODE_UNPROCESSABLE_ENTITY) ;
    }
}
