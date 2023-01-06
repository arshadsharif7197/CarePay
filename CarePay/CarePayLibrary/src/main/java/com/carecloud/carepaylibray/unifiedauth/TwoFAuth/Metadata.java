package com.carecloud.carepaylibray.unifiedauth.TwoFAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Metadata {

@SerializedName("user_id")
@Expose
private String userId;
@SerializedName("username")
@Expose
private String username;
@SerializedName("created_dt")
@Expose
private String createdDt;
@SerializedName("updated_dt")
@Expose
private String updatedDt;

public String getUserId() {
return userId;
}

public void setUserId(String userId) {
this.userId = userId;
}

public String getUsername() {
return username;
}

public void setUsername(String username) {
this.username = username;
}

public String getCreatedDt() {
return createdDt;
}

public void setCreatedDt(String createdDt) {
this.createdDt = createdDt;
}

public String getUpdatedDt() {
return updatedDt;
}

public void setUpdatedDt(String updatedDt) {
this.updatedDt = updatedDt;
}

}