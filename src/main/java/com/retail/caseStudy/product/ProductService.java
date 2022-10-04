package com.retail.caseStudy.product;

import com.retail.caseStudy.exceptions.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository proRep;

    public List<Product> getAllProducts() {
        return proRep.findAll();
    }

    public Product getProduct(Long id) {
        Optional<Product> product = proRep.findById(id);
        if (product.isPresent()) {
            return product.get();
        } else throw new ProductNotFoundException(id);
    }

    public ResponseEntity<Object> createProduct(Product product) {
        Product savedPro = proRep.save(product);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPro.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    public ResponseEntity<Object> updateProduct(Product product) {
        if (product.getId() == null) return ResponseEntity.badRequest().build();
        if (proRep.findById(product.getId()).isPresent()) {
            Product updatedProduct = proRep.save(product);

            return ResponseEntity.ok(updatedProduct);
        } else return ResponseEntity.notFound().build();
    }

    public ResponseEntity<Object> deleteProduct(Long id) {
        if (proRep.findById(id).isPresent()) {
            proRep.deleteById(id);

            return ResponseEntity.ok().build();
        } else return ResponseEntity.notFound().build();
    }


}
