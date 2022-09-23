package com.retail.caseStudy.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String s) {super(s);}
}
