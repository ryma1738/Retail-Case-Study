package com.retail.caseStudy.product;

import com.retail.caseStudy.exceptions.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.math.BigDecimal;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ProductControllerTest {

    @Autowired
    ProductService productService;

    @Test
    void getAll() {
        List<Product> products = productService.getAllProducts();
        assertThat(products).size().isGreaterThan(0);
    }

    @Test
    void getOne() {
        Product product = productService.getProduct(3L);
        assertNotNull(product);
    }

    @Test
    void createAndDelete() {
        Product product = new Product();
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(1.25));
        product.setName("Test Product");
        product.setImage("testImage.jpg");
        product.setDescription("asdlkfjaslkdjflas;lkdfj;asdjf;asldkfj;alksdk");
        productService.createProduct(product);
        Product savedProduct = productService.getAllProducts().stream().reduce((productFound, productCorrect) -> {
            if (productFound.getName().equals("Test Product")) return productFound;
            else return productCorrect;
        }).get();
        assertEquals(product, productService.getProduct(savedProduct.getId()));
        productService.deleteProduct(savedProduct.getId());
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(savedProduct.getId()));
    }

    @Test
    void updateProduct() {
        Product product = new Product();
        product.setQuantity(10);
        product.setPrice(BigDecimal.valueOf(1.25));
        product.setName("Test Product");
        product.setImage("testImage.jpg");
        product.setDescription("asdlkfjaslkdjflas;lkdfj;asdjf;asldkfj;alksdk");
        productService.createProduct(product);
        Product savedProduct = productService.getAllProducts().stream().reduce((productFound, productCorrect) -> {
            if (productFound.getName().equals("Test Product")) return productFound;
            else return productCorrect;
        }).get();
        savedProduct.setDescription("Updated Description.");
        productService.updateProduct(savedProduct);
        assertEquals(savedProduct, productService.getProduct(savedProduct.getId()));
        productService.deleteProduct(savedProduct.getId());
        assertThrows(ProductNotFoundException.class,
                () -> productService.getProduct(savedProduct.getId()));
    }
}
