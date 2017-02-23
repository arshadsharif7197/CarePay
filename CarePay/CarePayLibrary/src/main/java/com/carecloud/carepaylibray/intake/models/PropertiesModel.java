
package com.carecloud.carepaylibray.intake.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PropertiesModel {

    @SerializedName("items")
    @Expose
    private ItemsModel items = new ItemsModel();

    /**
     * 
     * @return
     *     The items
     */
    public ItemsModel getItems() {
        return items;
    }

    /**
     * 
     * @param items
     *     The items
     */
    public void setItems(ItemsModel items) {
        this.items = items;
    }

}
