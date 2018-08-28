package com.carecloud.carepaylibray.retail.models;

import com.carecloud.carepaylibray.utils.SystemUtil;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RetailItemDto {

    @SerializedName("id")
    private String id;

    @SerializedName("sku")
    private String sku;

    @SerializedName("thumbnail_url")
    private String thumbnailUrl;

    @SerializedName("in_stock")
    private boolean inStock = true;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private double price;

    @SerializedName("tax")
    private RetailItemTax tax = new RetailItemTax();

    @SerializedName("enabled")
    private boolean enabled = true;

    @SerializedName("options")
    private List<RetailItemOptionDto> options = new ArrayList<>();

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("description")
    private String description;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public RetailItemTax getTax() {
        return tax;
    }

    public void setTax(RetailItemTax tax) {
        this.tax = tax;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<RetailItemOptionDto> getOptions() {
        return options;
    }

    public void setOptions(List<RetailItemOptionDto> options) {
        this.options = options;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get price modification based on selected options
     * @param selectedOptions selected item options
     * @return modification amount
     */
    public double getPriceModification(Map<Integer, RetailItemOptionChoiceDto> selectedOptions){
        double basePrice = getPrice();
        for(RetailItemOptionChoiceDto choiceDto : selectedOptions.values()){
            double modificationAmount = choiceDto.getPriceModify();
            if(modificationAmount != 0){
                switch (choiceDto.getPriceModifyType()){
                    case RetailItemOptionChoiceDto.MODIFIER_TYPE_PERCENT:
                        basePrice = basePrice + SystemUtil.safeMultiply(basePrice,
                                modificationAmount/100);
                        break;
                    case RetailItemOptionChoiceDto.MODIFIER_TYPE_AMOUNT:
                    default:
                        basePrice += modificationAmount;
                }
            }
        }
        return SystemUtil.safeSubtract(basePrice, getPrice());
    }

}
