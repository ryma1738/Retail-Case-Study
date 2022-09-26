package com.retail.caseStudy.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retail.caseStudy.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;


@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private HashMap<Long, Integer> products;

    @NonNull
    private OrderStatus status;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="user_id")
    @NonNull
    private User user;

    @NonNull
    private BigDecimal total;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    public Order(@NonNull HashMap<Long, Integer> products, @NonNull OrderStatus status, @NonNull User user, @NonNull BigDecimal total) {
        this.products = products;
        this.status = status;
        this.user = user;
        this.total = total;
    }
}


