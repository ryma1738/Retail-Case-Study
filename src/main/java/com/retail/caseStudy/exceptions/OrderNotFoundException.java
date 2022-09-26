package com.retail.caseStudy.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(Long l) { super("Order with ID: " + l + ", was not found;");}
}
