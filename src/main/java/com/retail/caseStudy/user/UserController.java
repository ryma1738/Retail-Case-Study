package com.retail.caseStudy.user;

import com.retail.caseStudy.product.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



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
    public HashMap<Product, Integer> getUsersCart(@PathVariable long id) {
        return userService.getUsersCart(id);
    }


//    @GetMapping("/{email}/{password}")
//    public ResponseEntity<Object> login(@PathVariable String email, @PathVariable String password) {
//
//    }

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
    @PutMapping("/{id}/cart/{quantity}")
    public ResponseEntity<Object> updateCart(@PathVariable long id,
                                            @RequestBody Product product,
                                            @PathVariable int quantity) {
        return userService.updateUsersCart(id, product, quantity);
    }

}
