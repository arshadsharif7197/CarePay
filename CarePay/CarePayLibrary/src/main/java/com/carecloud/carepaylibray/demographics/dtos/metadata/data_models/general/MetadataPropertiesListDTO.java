package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO holding a list of properties;
 * In json, it corresponds to whatever is under "properties" -> "items".
 */
public class MetadataPropertiesListDTO extends MetadataPropertiesDTO {
    @SerializedName("items") @Expose
    public MetadataPropertiesListObjectDTO itemsMetaDTO;
}