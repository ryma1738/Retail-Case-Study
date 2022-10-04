package com.retail.caseStudy.user;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.product.Product;

import com.retail.caseStudy.security.JWTUtil;
import com.retail.caseStudy.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    @Autowired UserService userService;
    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;


    @GetMapping("/admin/user")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUserById() {
        User user = userService.getUserById();
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user/forgot/{email}/{phoneNumber}")
    public ResponseEntity<Object> checkForgottenUser(@PathVariable String email, @PathVariable String phoneNumber) {
        return userService.forgottenUserCheck(email, phoneNumber);
    }

    @PostMapping("/user/forgot")
    public ResponseEntity<Object> updatePassword(@RequestBody ChangePasswordJson info) {
        return userService.resetPassword(info);
    }

    @GetMapping("/user/cart")
    public CartJson getUsersCart() {
        return userService.getUsersCart();
    }

    @PostMapping("/user/create")
    public Map<String, String> createUser(@RequestBody SignupCredentials signupInfo) {
        return userService.createUser(signupInfo);
    }

    @PutMapping("/user") //need authentication
    public ResponseEntity<Object> updateUser(@RequestBody  User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/user")
    public ResponseEntity<Object> deleteUser() {
        return userService.deleteUser();
    }


    @PutMapping("/user/cart/{quantity}")
    public ResponseEntity<Object> updateCart(@RequestBody Product product,
                                            @PathVariable int quantity) {
        return userService.updateUsersCart(product, quantity);
    }

    @PostMapping("/user/login")
    public Map<String, Object> loginHandler(@RequestBody LoginCredentials body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword());
            authManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(body.getEmail());

            return Collections.singletonMap("jwtToken", token);
        }catch (AuthenticationException authExc){
            throw new BadRequestException("Invalid Login Credentials");
        }
    }

}
