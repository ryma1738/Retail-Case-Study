package com.retail.caseStudy.product;

import com.retail.caseStudy.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    ProductRepository proRep;

    @GetMapping("/product")
    public List<Product> getAllProducts() {
        return proRep.findAll();
    }

    @GetMapping("/product/{id}")
    public Product getProduct(@PathVariable Long id) {
        Optional<Product> product = proRep.findById(id);
        if (product.isPresent()) {
            return product.get();
        } else throw new ProductNotFoundException(id);
    }

    //Needs Authentication
    @PostMapping("/admin/product")
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        Product savedPro = proRep.save(product);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPro.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    //Needs Authentication
    @PutMapping("/admin/product")
    public ResponseEntity<Object> updateProduct(@RequestBody Product product) {
        if (product.getId() == null) return ResponseEntity.badRequest().build();
        if (proRep.findById(product.getId()).isPresent()) {
            Product updatedProduct = proRep.save(product);

            return ResponseEntity.ok(updatedProduct);
        } else return ResponseEntity.notFound().build();
    }

    //Needs Authentication
    @DeleteMapping("/admin/product/{id}")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        if (proRep.findById(id).isPresent()) {
            proRep.deleteById(id);

            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }
}
