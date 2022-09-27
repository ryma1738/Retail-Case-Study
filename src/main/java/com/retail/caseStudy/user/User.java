package com.retail.caseStudy.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retail.caseStudy.order.Cart;
import com.retail.caseStudy.order.Order;
import com.retail.caseStudy.product.Product;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id", referencedColumnName = "id")
    private Cart cart;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Order> orders;

    @JsonIgnore
    private String role = "user";

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    public User() {}

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Cart getCart() {
        return cart;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", cart_id=" + cart.getId() +
                ", number of orders=" + orders.size() +
                ", createdAt=" + createdAt +
                '}';
    }
}
