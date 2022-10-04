package com.retail.caseStudy.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("")
    public ResponseEntity<Object> getUsersOrders() {
        return orderService.getUsersOrders();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getUsersOrder( @PathVariable Long orderId) {
        return orderService.getUsersOrder(orderId);
    }

    @Transactional
    @PostMapping("")
    public ResponseEntity<Object> createOrder() {
        //would also confirm payment has been received here if I was adding a payment feature.
        return orderService.createOrder();
    }

    @Transactional
    @PutMapping("/{orderId}/{status}") //need to reset product quantity on cancel
    public ResponseEntity<Object> updateStatus(@PathVariable Long orderId,
                                               @PathVariable OrderStatus status) {
        return orderService.updateStatus(orderId, status);
    }
}
