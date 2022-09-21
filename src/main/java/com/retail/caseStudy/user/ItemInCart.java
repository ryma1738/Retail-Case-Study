package com.retail.caseStudy.user;

import com.retail.caseStudy.product.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
public class ItemInCart {
    @Id
    @GeneratedValue
    private Long id;

    private Product product;

    private int quantity;

    private BigDecimal totalCost;

    public ItemInCart(Product prod, int q, BigDecimal total) {
        this.product = prod;
        this.quantity = q;
        this.totalCost = total;
    }

}
