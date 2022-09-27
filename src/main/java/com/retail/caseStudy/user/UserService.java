package com.retail.caseStudy.user;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.exceptions.ProductNotFoundException;
import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.order.Cart;
import com.retail.caseStudy.order.CartRepository;
import com.retail.caseStudy.order.OrderRepository;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductRepository;
import com.retail.caseStudy.security.JWTUtil;
import com.retail.caseStudy.util.CartJson;
import com.retail.caseStudy.util.UsersCartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService  {
    @Autowired
    UserRepository userRep;

    @Autowired
    OrderRepository orderRep;

    @Autowired
    ProductRepository prodRep;

    @Autowired
    CartRepository cartRep;

    @Autowired private JWTUtil jwtUtil;
    @Autowired private AuthenticationManager authManager;
    @Autowired private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRep.findAll();
    }

    public User getUserById() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> user = userRep.findByEmail(email);
        if (user.isPresent()) return user.get();
        else throw new UserNotFoundException();
    }

    public CartJson getUsersCart() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> getUser = userRep.findByEmail(email);
        if (getUser.isPresent()) {
            if(getUser.get().getCart().getProducts() == null || getUser.get().getCart().getProducts().isEmpty()) {
                return new CartJson(new ArrayList<UsersCartInfo>(), BigDecimal.valueOf(0));
            }
            HashMap<Long, Integer> itemsById = getUser.get().getCart().getProducts();
            List<UsersCartInfo> cart = itemsById.keySet().stream().map(pId -> {
                try {
                    Product product = prodRep.findById(pId).get();
                    int quantity = itemsById.get(product.getId());
                    return new UsersCartInfo(product, quantity);
                } catch(Exception e) {throw new ProductNotFoundException(pId);}
            }).collect(Collectors.toList());
            BigDecimal subtotal = cart.stream().map(cartInfo -> {
                BigDecimal itemPrice = cartInfo.getProduct().getPrice();
                itemPrice = itemPrice.multiply(BigDecimal.valueOf(cartInfo.getQuantity()));
                return itemPrice;
            }).reduce((price, sum) -> price.add(sum)).get();
            return new CartJson(cart, subtotal);
        }
        else throw new UserNotFoundException();
    }

    @Transactional
    public Map<String, String> createUser(LoginCredentials loginInfo) {
        User user = new User();
        String encodedPass = passwordEncoder.encode(loginInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(loginInfo.getEmail());
        User savedUser = userRep.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        Cart savedCart = cartRep.save(new Cart(savedUser));
        savedUser.setCart(savedCart);
        userRep.save(savedUser);

        return Collections.singletonMap("jwt-token", token);
    }

    public ResponseEntity<Object> updateUser(User user) {
        if(userRep.findById(user.getId()).isPresent()) {
            userRep.save(user);
            return ResponseEntity.ok().build();
        } else throw new UserNotFoundException();
    }

    @Transactional
    public ResponseEntity<Object> deleteUser() {
        String email = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()){
            User user = confirm.get();
            cartRep.delete(user.getCart());
            user.getOrders().stream().forEach(order -> orderRep.delete(order));
            userRep.delete(user);
            return ResponseEntity.ok().build();
        } else throw new UserNotFoundException();
    }

    @Transactional
    public ResponseEntity<Object> updateUsersCart(Product product, int quantity) {
        if (!userRep.findByEmail((String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).isPresent()) throw new UserNotFoundException();
        Optional<Product> actualProduct = prodRep.findById(product.getId());
        if(actualProduct.isPresent()) {
            if(!product.equals(actualProduct.get()))
                throw new BadRequestException("Product information provided does not match our records. " +
                        "Please try again with updated info.");
        } else throw new ProductNotFoundException(product.getId());
        if (product.getQuantity() < quantity) throw new BadRequestException(
                "You can not add more items to you cart than are currently available: " + product.getQuantity());
        User user = userRep.findByEmail((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).get();
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
        BigDecimal subtotal;
        if(finalCartsProducts.isEmpty()) subtotal = BigDecimal.valueOf(0);
        else {
            subtotal = cartsProducts.keySet().stream().map((p) -> {
                int q = finalCartsProducts.get(p);
                return prodRep.findById(p).get().getPrice().multiply(BigDecimal.valueOf(q));
            }).reduce((total, sum) -> sum.add(total)).get();
        }
        cart.setSubtotal(subtotal);
        Cart cart1 =cartRep.save(cart);
        System.out.println(cart1);
        return ResponseEntity.ok().build();
    }


}
