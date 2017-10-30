package com.carecloud.carepay.patient.myhealth.dtos;

import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 9/27/17
 */

public class MyHealthProviderDto extends ProviderDTO {

    @SerializedName("business_entity")
    private BusinessEntity businessEntity = new BusinessEntity();


    public BusinessEntity getBusinessEntity() {
        return businessEntity;
    }

    public void setBusinessEntity(BusinessEntity businessEntity) {
        this.businessEntity = businessEntity;
    }
}
