package com.retail.caseStudy.user;

import com.retail.caseStudy.product.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
public class ItemInCart {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(targetEntity = Product.class)
    private Product product;

    @NonNull
    @Column(nullable = false)
    private int quantity;

    private BigDecimal totalCost;

    public ItemInCart(User user, Product product, int q, BigDecimal total) {
        this.user = user;
        this.product = product;
        this.quantity = q;
        this.totalCost = total;
    }

}
