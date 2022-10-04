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
import com.retail.caseStudy.util.*;
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

    @Autowired private OrderRepository orderRep;

    @Autowired private ProductRepository prodRep;

    @Autowired private CartRepository cartRep;

    @Autowired private ForgotPasswordRepository forgotRep;

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

    public ResponseEntity<Object> forgottenUserCheck(String email, String phoneNumber) {
        Optional<User> userInfo = userRep.findByEmail(email);
        if (userInfo.isPresent()) {
            String phone = userInfo.get().getPhoneNumber();
            if (phone.equals(phoneNumber)) {
                ForgotPasswordJson json = new ForgotPasswordJson(userInfo.get().getId(),
                        (int) Math.round(Math.random() * 1000));
                ForgotPassword sql = new ForgotPassword(userInfo.get().getId(), json.getKey());
                ForgotPassword savedSQL = forgotRep.save(sql);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        forgotRep.delete(savedSQL);
                    }
                }, 300000);
                return ResponseEntity.ok(json);
            } else throw new BadRequestException("This is the wrong phone number for this user!");
        } else throw new UserNotFoundException();
    }

    public ResponseEntity<Object> resetPassword(ChangePasswordJson info) {
        Optional<ForgotPassword> confirm = forgotRep.findByKeyValue(info.getKey());
        if (confirm.isPresent()){
            forgotRep.delete(confirm.get());
            User user = userRep.findById(info.getUserId()).get();
            user.setPassword(passwordEncoder.encode(info.getPassword()));
            User savedUser = userRep.save(user);
            return ResponseEntity.ok().build();
        } else throw new BadRequestException("This user does not have an active request to update password.");
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
    public Map<String, String> createUser(SignupCredentials signupInfo) {
        User user = new User();
        String encodedPass = passwordEncoder.encode(signupInfo.getPassword());
        user.setPassword(encodedPass);
        user.setEmail(signupInfo.getEmail());
        user.setPhoneNumber(signupInfo.getPhoneNumber());
        User savedUser = userRep.save(user);
        String token = jwtUtil.generateToken(user.getEmail());

        Cart savedCart = cartRep.save(new Cart(savedUser, new HashMap<Long, Integer>(), BigDecimal.valueOf(0)));
        savedUser.setCart(savedCart);
        userRep.save(savedUser);

        return Collections.singletonMap("jwtToken", token);
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
