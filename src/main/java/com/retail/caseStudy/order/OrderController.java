package com.retail.caseStudy.order;

import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.user.User;
import com.retail.caseStudy.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    UserRepository userRep;

    @GetMapping("/{userId}")
    public Set<Order> getUsersOrders(@PathVariable Long userId) {
        Optional<User> confirm = userRep.findById(userId);
        if(confirm.isPresent()) return confirm.get().getOrders();
        else throw new UserNotFoundException(userId);
    }
}
