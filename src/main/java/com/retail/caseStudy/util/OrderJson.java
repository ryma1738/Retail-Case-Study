package com.retail.caseStudy.util;

import com.retail.caseStudy.order.OrderStatus;
import com.retail.caseStudy.user.User;
import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.List;

public class OrderJson {

    private Long id;

    private List<UsersCartInfo> products;

    private OrderStatus status;

    private BigDecimal total;

    private Timestamp createdAt;

    public OrderJson(Long id, List<UsersCartInfo> products, OrderStatus status, BigDecimal total, Timestamp createdAt) {
        this.id = id;
        this.products = products;
        this.status = status;

        this.total = total;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<UsersCartInfo> getProducts() {
        return products;
    }

    public void setProducts(List<UsersCartInfo> products) {
        this.products = products;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
