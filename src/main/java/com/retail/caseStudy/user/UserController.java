package com.retail.caseStudy.user;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.product.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
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
        else throw new UserNotFoundException(id);
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

    @PutMapping("/")
    public ResponseEntity<Object> updateUser(@RequestBody User user) {
        if(userRep.findById(user.getId()).isPresent()) {
            userRep.save(user);
            return ResponseEntity.ok().build();
        } else throw new UserNotFoundException(user.getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        if(userRep.findById(id).isPresent()){
            userRep.deleteById(id);
           return ResponseEntity.ok().build();
        } else throw new UserNotFoundException(id);
    }

    @PutMapping("/{id}/cart/{quantity}")
    public ResponseEntity<Object> addToCart(@PathVariable long id,
                                            @RequestBody Product product,
                                            @PathVariable int quantity) {
        if(!userRep.findById(id).isPresent()) throw new UserNotFoundException(id);
        if (product.getQuantity() < quantity) throw new BadRequestException(
                "You can not add more items to you cart than are currently available: " + product.getQuantity());
        ItemInCart itemInCart = new ItemInCart(product, quantity, product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        User user = userRep.findById(id).get();
        List<ItemInCart> cart = user.getCart();
        cart.add(itemInCart);
        user.setCart(cart);
        userRep.save(user);
        return ResponseEntity.ok(user);
    }

    //Authentication
    @PutMapping("/{id}/cart/{index}/{quantity}")
    public ResponseEntity<Object> removeFromCart(@PathVariable long id,
                                            @PathVariable int index,
                                            @PathVariable int quantity) {
        if(!userRep.findById(id).isPresent()) throw new UserNotFoundException(id);
        User user = userRep.findById(id).get();
        if(user.getCart().size() > index - 1 || index < 0)
            throw new BadRequestException("The item must be in your cart to make changes. ('index' out of bounds");
        ItemInCart item = user.getCart().get(index);
        List<ItemInCart> cart = user.getCart();
        if(item.getQuantity() > quantity) {
            item.setQuantity(item.getQuantity() - quantity);
            cart.set(index, item);
            user.setCart(cart);
            userRep.save(user);
        } else {
            cart.remove(item);
            user.setCart(cart);
            userRep.save(user);
        }
        return ResponseEntity.ok(user);




}
