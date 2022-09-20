package com.retail.caseStudy.order;

import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.user.User;
import com.sun.istack.NotNull;
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
public class Order {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private List<Product> products;

    private OrderStatus status;

    @ManyToOne
    @NonNull
    private User userId;

    @NonNull
    private BigDecimal total;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;
}
