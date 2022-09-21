package com.retail.caseStudy.user;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.exceptions.ProductNotFoundException;
import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserRepository userRep;

    @Autowired
    ProductRepository prodRep;

    @Autowired
    ItemInCartRepository itemRep;

    //Authentication need
    @GetMapping("")
    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        System.out.println(userRep.findById(id).isPresent());
        Optional<User> user = userRep.findById(id);
        if (user.isPresent()) return user.get();
        else throw new UserNotFoundException(id);
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@RequestBody User user) {
        User savedUser = userRep.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("")
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

    @Transactional
    @PutMapping("/{id}/cart/{quantity}")
    public ResponseEntity<Object> addToCart(@PathVariable long id,
                                            @RequestBody Product product,
                                            @PathVariable int quantity) {
        if(!userRep.findById(id).isPresent()) throw new UserNotFoundException(id);
        if (product.getQuantity() < quantity) throw new BadRequestException(
                "You can not add more items to you cart than are currently available: " + product.getQuantity());
        if(quantity <= 0) throw new BadRequestException("Quantity can not be less than one.");
        User user = userRep.findById(id).get();
        ItemInCart itemInCart = itemRep.save(new ItemInCart(user, product, quantity, product.getPrice().multiply(BigDecimal.valueOf(quantity))));
        List<ItemInCart> cart = user.getCart();
        cart.add(itemInCart);
        user.setCart(cart);
        userRep.save(user);
        return ResponseEntity.ok().build();
    }

    //Authentication
    @Transactional
    @PutMapping("/{id}/cart/{index}/{quantity}")
    public ResponseEntity<Object> updateCart(@PathVariable long id,
                                            @PathVariable int index,
                                            @PathVariable int quantity) {
        if (!userRep.findById(id).isPresent()) throw new UserNotFoundException(id);
        User user = userRep.findById(id).get();
        if (user.getCart().size() > index - 1 || index < 0)
            throw new BadRequestException("The item must be in your cart to make changes. ('index' out of bounds");
        List<ItemInCart> cart = user.getCart();
        ItemInCart item = cart.get(index);
        if (quantity > 0 && quantity < item.getProduct().getQuantity()) {
            item.setQuantity(quantity);
            cart.set(index, item);
        } else {
            cart.remove(item);
        }
        user.setCart(cart);
        userRep.save(user);
        return ResponseEntity.ok(user);
    }

}
