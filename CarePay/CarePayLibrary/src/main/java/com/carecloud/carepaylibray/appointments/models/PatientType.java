package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientType {

@SerializedName("new_patient")
@Expose
private Boolean newPatient;

public Boolean getNewPatient() {
return newPatient;
}

public void setNewPatient(Boolean newPatient) {
this.newPatient = newPatient;
}

}