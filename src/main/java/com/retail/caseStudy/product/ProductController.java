package com.retail.caseStudy.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    ProductService prodService;

    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return prodService.getAllProducts();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
       return prodService.getProduct(id);
    }

    @PostMapping("/admin/product")
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        return prodService.createProduct(product);
    }

    @PutMapping("/admin/product")
    public ResponseEntity<Object> updateProduct(@RequestBody Product product) {
        return prodService.updateProduct(product);
    }

    @DeleteMapping("/admin/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        return prodService.deleteProduct(id);
    }
}
