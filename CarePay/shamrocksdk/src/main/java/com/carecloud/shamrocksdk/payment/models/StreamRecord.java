package com.carecloud.shamrocksdk.payment.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Model Class for providing Device id to Shamrock Payments during device registration
 */
public class StreamRecord {

@SerializedName("deepstream_record_id")
@Expose
private String deepstreamRecordId;

public String getDeepstreamRecordId() {
return deepstreamRecordId;
}

public void setDeepstreamRecordId(String deepstreamRecordId) {
this.deepstreamRecordId = deepstreamRecordId;
}

}