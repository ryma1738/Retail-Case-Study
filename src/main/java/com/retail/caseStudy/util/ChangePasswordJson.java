package com.retail.caseStudy.util;

import lombok.Data;

@Data
public class ChangePasswordJson {

    private Long userId;

    private int key;

    private String password;
}
