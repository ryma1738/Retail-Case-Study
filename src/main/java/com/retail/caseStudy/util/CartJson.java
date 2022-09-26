package com.retail.caseStudy.util;

import java.math.BigDecimal;
import java.util.List;

public class CartJson {
    private List<UsersCartInfo> products;
    private BigDecimal subTotal;

    public CartJson(List<UsersCartInfo> products, BigDecimal subTotal) {
        this.products = products;
        this.subTotal = subTotal;
    }

    public List<UsersCartInfo> getProducts() {
        return products;
    }

    public void setProducts(List<UsersCartInfo> products) {
        this.products = products;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }
}
