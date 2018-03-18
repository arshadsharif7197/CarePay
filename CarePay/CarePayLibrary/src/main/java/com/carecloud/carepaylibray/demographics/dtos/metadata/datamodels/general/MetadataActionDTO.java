package com.carecloud.carepaylibray.demographics.dtos.metadata.datamodels.general;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lsoco_user on 10/24/2016.
 * DTO for an action in a metadata entity
 */
public class MetadataActionDTO {
    @SerializedName("method") @Expose
    public String method;

    @SerializedName("url") @Expose
    public String url;

    @SerializedName("allowed_ops") @Expose
    public List<MetadataAllowedOpDTO> allowedOps = new ArrayList<>();

    @SerializedName("path") @Expose
    public String path;
}
