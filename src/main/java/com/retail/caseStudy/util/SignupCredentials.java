package com.retail.caseStudy.util;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupCredentials {

    private String email;
    private String password;
    private String phoneNumber;
}