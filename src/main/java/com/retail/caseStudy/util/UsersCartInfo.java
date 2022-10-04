package com.retail.caseStudy.util;

import com.retail.caseStudy.product.Product;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UsersCartInfo {

    private Product product;
    private Integer quantity;

    public UsersCartInfo(Product product, Integer quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
