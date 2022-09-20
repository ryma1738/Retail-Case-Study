package com.retail.caseStudy.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserRepository userRep;

    //Authentication need
    @GetMapping("/")
    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        Optional<User> user = userRep.findById(id);
        if (user.isPresent()) return user.get();
        else throw new UserNotFoundException("id-" + id);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User savedUser = userRep.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

}
