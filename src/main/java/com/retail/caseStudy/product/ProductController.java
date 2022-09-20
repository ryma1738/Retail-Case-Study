package com.retail.caseStudy.product;

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
        } else throw new ProductNotFoundException("id-" + id);
    }

    //Needs Authentication
    @PostMapping("product")
    public ResponseEntity<Object> createProduct(@RequestBody Product product) {
        Product savedPro = proRep.save(product);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPro.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }


}
