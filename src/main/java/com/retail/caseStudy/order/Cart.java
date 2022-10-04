package com.retail.caseStudy.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

@Entity

public class Cart {

    @Id
    @GeneratedValue
    private long id;

    @JsonIgnore
    @OneToOne(mappedBy = "cart")
    private User user;

    private HashMap<Long, Integer> products;

    private BigDecimal subtotal;

    public Cart(User user, HashMap<Long, Integer> products, BigDecimal subtotal) {
        this.user = user;
        this.products = products;
        this.subtotal = subtotal;
    }

    public Cart() {}

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public HashMap<Long, Integer> getProducts() {
        return products;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setProducts(HashMap<Long, Integer> products) {
        this.products = products;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", user=" + user +
                ", products=" + products.toString() +
                ", subtotal=" + subtotal +
                '}';
    }
}
