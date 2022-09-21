package com.retail.caseStudy.order;

import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.user.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @OneToMany(targetEntity = Product.class)
    private List<Product> products;

    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @NonNull
    private User user;

    @NonNull
    private BigDecimal total;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
