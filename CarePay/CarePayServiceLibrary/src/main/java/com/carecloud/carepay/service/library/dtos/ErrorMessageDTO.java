
package com.carecloud.carepay.service.library.dtos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class
ErrorMessageDTO {

    @SerializedName("error")
    @Expose
    private ServiceErrorDTO serviceErrorDTO;

    public ServiceErrorDTO getServiceErrorDTO() {
        return serviceErrorDTO;
    }

    public void setServiceErrorDTO(ServiceErrorDTO serviceErrorDTO) {
        this.serviceErrorDTO = serviceErrorDTO;
    }

}
