package com.retail.caseStudy.util;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangePasswordJson {

    private Long userId;

    private int key;

    private String password;
}
