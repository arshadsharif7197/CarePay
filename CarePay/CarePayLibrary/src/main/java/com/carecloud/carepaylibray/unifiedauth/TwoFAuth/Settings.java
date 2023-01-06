package com.carecloud.carepaylibray.unifiedauth.TwoFAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {

@SerializedName("metadata")
@Expose
private Metadata metadata;
@SerializedName("payload")
@Expose
private Payload payload;

public Metadata getMetadata() {
return metadata;
}

public void setMetadata(Metadata metadata) {
this.metadata = metadata;
}

public Payload getPayload() {
return payload;
}

public void setPayload(Payload payload) {
this.payload = payload;
}

}