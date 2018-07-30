package com.carecloud.carepaylibray.retail.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class RetailProductsModel {

    @SerializedName("products")
    private ProductsDto products = new ProductsDto();

    public ProductsDto getProducts() {
        return products;
    }

    public void setProducts(ProductsDto products) {
        this.products = products;
    }


    public class ProductsDto {

        @SerializedName("items")
        private List<RetailItemDto> items = new ArrayList<>();

        public List<RetailItemDto> getItems() {
            return items;
        }

        public void setItems(List<RetailItemDto> items) {
            this.items = items;
        }
    }
}
