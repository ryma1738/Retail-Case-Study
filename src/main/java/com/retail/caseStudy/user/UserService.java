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
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRep;

    @Autowired
    ProductRepository prodRep;

    @Autowired
    CartRepository cartRep;

    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    public User getUserById(Long id) {
        Optional<User> user = userRep.findById(id);
        if (user.isPresent()) return user.get();
        else throw new UserNotFoundException(id);
    }

    public Cart getUsersCart(Long id) {
        Optional<User> getUser = userRep.findById(id);
        if (getUser.isPresent()) return getUser.get().getCart();
        else throw new UserNotFoundException(id);
    }

    public ResponseEntity<Object> createUser(User user) {
        User savedUser = userRep.save(user);
        Cart savedCart = cartRep.save(new Cart(savedUser));
        savedUser.setCart(savedCart);
        userRep.save(savedUser);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Object> updateUser(User user) {
        if(userRep.findById(user.getId()).isPresent()) {
            userRep.save(user);
            return ResponseEntity.ok().build();
        } else throw new UserNotFoundException(user.getId());
    }

    public ResponseEntity<Object> deleteUser(Long id) {
        if(userRep.findById(id).isPresent()){
            userRep.deleteById(id);
            return ResponseEntity.ok().build();
        } else throw new UserNotFoundException(id);
    }

    public ResponseEntity<Object> updateUsersCart(long id, Product product, int quantity) {
        if (!userRep.findById(id).isPresent()) throw new UserNotFoundException(id);
        Optional<Product> actualProduct = prodRep.findById(product.getId());
        if(actualProduct.isPresent()) {
            if(!product.equals(actualProduct.get()))
                throw new BadRequestException("Product information provided does not match our records. " +
                        "Please try again with updated info.");
        } else throw new ProductNotFoundException(product.getId());
        if (product.getQuantity() < quantity) throw new BadRequestException(
                "You can not add more items to you cart than are currently available: " + product.getQuantity());
        User user = userRep.findById(id).get();
        Cart cart = user.getCart();
        HashMap<Long, Integer> cartsProducts = cart.getProducts();
        if(cartsProducts == null)  cartsProducts = new HashMap<Long, Integer>();
        if(!cartsProducts.containsKey(product.getId())) {
            if (quantity == 0) throw new BadRequestException("You must have a quantity higher than 0 to add an item to your cart");
            else cartsProducts.put(product.getId(), quantity);
        } else if(quantity <=0 && cartsProducts.containsKey(product.getId())) {
            cartsProducts.remove(product.getId());
        } else {
            cartsProducts.replace(product.getId(), quantity);
        }
        cart.setProducts(cartsProducts);
        HashMap<Long, Integer> finalCartsProducts = cartsProducts;
        BigDecimal subtotal = cartsProducts.keySet().stream().map((p) -> {
            int q = finalCartsProducts.get(p);
            return prodRep.findById(p).get().getPrice().multiply(BigDecimal.valueOf(q));
        }).reduce((total, sum) -> sum.add(total)).get();
        cart.setSubtotal(subtotal);
        cartRep.save(cart);
        return ResponseEntity.ok().build();
    }

}
