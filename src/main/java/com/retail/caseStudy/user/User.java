package com.retail.caseStudy.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retail.caseStudy.order.Order;
import com.retail.caseStudy.product.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private String email;


    private String password;

    @OneToMany(mappedBy = "user")
    @Column(name = "cart")
    private List<ItemInCart> cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
