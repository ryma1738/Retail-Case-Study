package com.retail.caseStudy.order;

import com.retail.caseStudy.exceptions.BadRequestException;
import com.retail.caseStudy.exceptions.OrderNotFoundException;
import com.retail.caseStudy.exceptions.ProductNotFoundException;
import com.retail.caseStudy.exceptions.UserNotFoundException;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductRepository;
import com.retail.caseStudy.user.User;
import com.retail.caseStudy.user.UserRepository;
import com.retail.caseStudy.util.CartJson;
import com.retail.caseStudy.util.OrderJson;
import com.retail.caseStudy.util.UsersCartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    @Autowired
    ProductRepository productRep;

    @Autowired
    UserRepository userRep;

    @Autowired
    ProductRepository prodRep;

    @Autowired
    CartRepository cartRep;

    @Autowired
    OrderRepository orderRep;

    @GetMapping("")
    public ResponseEntity<Object> getUsersOrders() {
        User user = confirmUser((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        List<OrderJson> orders = user.getOrders().stream().map(order -> {
            HashMap<Long, Integer> itemsById = order.getProducts();
            List<UsersCartInfo> cart = itemsById.keySet().stream().map(pId -> {
                Product product = prodRep.findById(pId).get();
                int quantity = itemsById.get(product.getId());
                return new UsersCartInfo(product, quantity);
            }).collect(Collectors.toList());
            return new OrderJson(order.getId(), cart, order.getStatus(),
                    order.getTotal(), order.getCreatedAt());
        }).collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Object> getUsersOrder( @PathVariable Long orderId) {
        User user = confirmUser((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Order order = confirmOrderUserRelationship(user.getId(), orderId);
        HashMap<Long, Integer> itemsById = order.getProducts();
        List<UsersCartInfo> cart = itemsById.keySet().stream().map(pId -> {
            Product product = prodRep.findById(pId).get();
            int quantity = itemsById.get(product.getId());
            return new UsersCartInfo(product, quantity);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(new OrderJson(order.getId(), cart, order.getStatus(),
                order.getTotal(), order.getCreatedAt()));
    }

    @Transactional
    @PostMapping("")
    public ResponseEntity<Object> createOrder() {
        //would also confirm payment has been received here if I was adding a payment feature.
        User user = confirmUser((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Cart cart = user.getCart();
        if (cart.getProducts().isEmpty())
            throw new BadRequestException("You must have items in your cart to create an order.");
        BigDecimal subTotal = cart.getProducts().keySet().stream().map(pId -> {
            Optional<Product> confirmProduct = productRep.findById(pId);
            if(!confirmProduct.isPresent()) {
                cart.getProducts().remove(pId);
                cartRep.save(cart);
                throw new ProductNotFoundException(pId);
            }
            Product product =  confirmProduct.get();
            return product.getPrice().multiply(BigDecimal.valueOf(cart.getProducts().get(pId)));
        }).reduce((price, sum) -> sum.add(price)).get();
        BigDecimal total = subTotal.add(subTotal.multiply(BigDecimal.valueOf(0.1)));
        Order order = orderRep.save(new Order(cart.getProducts(), OrderStatus.ORDER_PLACED, user, total));
        cart.setProducts(new HashMap<Long, Integer>());
        cart.setSubtotal(BigDecimal.valueOf(0));
        cartRep.save(cart);
        Set<Order> usersOrders = user.getOrders();
        usersOrders.add(order);
        user.setOrders(usersOrders);
        userRep.save(user);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/userId")
                .buildAndExpand(order.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{orderId}/{status}")
    public ResponseEntity<Object> updateStatus(@PathVariable Long orderId,
                                               @PathVariable OrderStatus status) {
        Long userId = confirmUser((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getId();
        Order order = confirmOrderUserRelationship(userId, orderId);
        if (order.getStatus().equals(OrderStatus.CANCELED))
            throw new BadRequestException("You can not modify a canceled order.");
        if(status.equals(OrderStatus.CANCELED)) {
            if (order.getStatus().equals(OrderStatus.SHIPPING) || order.getStatus().equals(OrderStatus.COMPLETE))
                throw new BadRequestException("You can not cancel " +
                        "an order that has already shipped. Please contact site Support");
        }
        order.setStatus(status);
        orderRep.save(order);
        return ResponseEntity.ok().build();
    }

    private User confirmUser(String email) {
        Optional<User> confirm = userRep.findByEmail(email);
        if(confirm.isPresent()) return confirm.get();
        else throw new UserNotFoundException();
    }

    private Order confirmOrderUserRelationship(Long userId, Long orderId) {
        Optional<Order> orderConfirm = orderRep.findById(orderId);
        if(!orderConfirm.isPresent()) throw new OrderNotFoundException(orderId);
        Order order = orderConfirm.get();
        if(!order.getUser().getId().equals(userId))
            throw new BadRequestException("ERROR: This order does not belong to this user.");
        return order;
    }
}
