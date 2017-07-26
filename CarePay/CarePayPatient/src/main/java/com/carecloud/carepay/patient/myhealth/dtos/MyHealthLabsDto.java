package com.carecloud.carepay.patient.myhealth.dtos;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pjohnson on 17/07/17.
 */
public class MyHealthLabsDto {

    private List<LabDto> labs = new ArrayList<>();

    public List<LabDto> getLabs() {
        return labs;
    }

    public void setLabs(List<LabDto> labs) {
        this.labs = labs;
    }
}
