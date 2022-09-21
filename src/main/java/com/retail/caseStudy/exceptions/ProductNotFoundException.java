package com.retail.caseStudy.exceptions;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long l) { super("The Product with ID: " + l + ", was not found;");}
}
