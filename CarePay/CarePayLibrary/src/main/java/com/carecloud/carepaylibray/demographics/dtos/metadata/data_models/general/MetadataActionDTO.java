package com.carecloud.carepaylibray.demographics.dtos.metadata.data_models.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for an action in a metadata entity
 */
public class MetadataActionDTO {
    @SerializedName("method") @Expose
    public String methodMeta;

    @SerializedName("url") @Expose
    public String urlMeta;

    @SerializedName("allowed_ops") @Expose
    public List<MetadataAllowedOpDTO> allowedOpsMetaListDTO;

    @SerializedName("path") @Expose
    public String pathMeta;
}
