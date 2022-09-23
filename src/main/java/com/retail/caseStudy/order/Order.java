package com.retail.caseStudy.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    private HashMap<Long, Integer> products;

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
}
