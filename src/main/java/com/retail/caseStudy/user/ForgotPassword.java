package com.retail.caseStudy.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPassword {
    @Id
    @GeneratedValue
    private Long id;

    @NonNull
    private long usersId;

    @NonNull
    private int keyValue;

    public ForgotPassword(Long userId, int key){
        this.usersId = userId;
        this.keyValue = key;
    }
}
