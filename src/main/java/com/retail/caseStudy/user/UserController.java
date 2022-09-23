package com.retail.caseStudy.user;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.exceptions.ProductNotFoundException;
import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.order.Cart;
import com.retail.caseStudy.order.CartRepository;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired UserService userService;

    //Authentication need
    @GetMapping("")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/cart")
    public Cart getUsersCart(@PathVariable long id) {
        return userService.getUsersCart(id);
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("")
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    //Authentication
    @Transactional
    @PutMapping("/{id}/cart/{quantity}")
    public ResponseEntity<Object> updateCart(@PathVariable long id,
                                            @RequestBody Product product,
                                            @PathVariable int quantity) {
        return userService.updateUsersCart(id, product, quantity);
    }

}
