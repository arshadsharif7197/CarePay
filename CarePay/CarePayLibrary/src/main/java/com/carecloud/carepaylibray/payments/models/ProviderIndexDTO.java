
package com.carecloud.carepaylibray.payments.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProviderIndexDTO {

    @SerializedName("Dr. Lauren Derosa")
    @Expose
    private List<String> drLaurenDerosa = null;
    @SerializedName("Dr. Eric Mulkey")
    @Expose
    private List<String> drEricMulkey = null;

    public List<String> getDrLaurenDerosa() {
        return drLaurenDerosa;
    }

    public void setDrLaurenDerosa(List<String> drLaurenDerosa) {
        this.drLaurenDerosa = drLaurenDerosa;
    }

    public List<String> getDrEricMulkey() {
        return drEricMulkey;
    }

    public void setDrEricMulkey(List<String> drEricMulkey) {
        this.drEricMulkey = drEricMulkey;
    }

}
