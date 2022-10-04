package com.retail.caseStudy.util;

import com.retail.caseStudy.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordJson {

    private Long userId;

    private int key;
}
