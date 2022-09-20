package com.retail.caseStudy.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    @Column(length = 350)
    private String description;

    private int quantity;

    @NonNull
    private BigDecimal price;

    private String image; //image is stored on front end

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdDate;


}
