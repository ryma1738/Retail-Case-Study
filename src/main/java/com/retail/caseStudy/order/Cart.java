package com.retail.caseStudy.order;

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
@Data
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue
    private long id;

    @OneToOne(mappedBy = "cart")
    private User user;

    private HashMap<Long, Integer> products;

    private BigDecimal subtotal;

    public Cart(User user) {
        this.user = user;
    }

}
