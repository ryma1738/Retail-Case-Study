package com.retail.caseStudy.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductService;
import com.retail.caseStudy.user.JwtJson;
import com.retail.caseStudy.user.UserRepository;
import com.retail.caseStudy.util.CartJson;
import com.retail.caseStudy.util.OrderJson;
import com.retail.caseStudy.util.SignupCredentials;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class OrderServiceTest {
    private static String jwt;

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
    @Autowired
    private  ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  MockMvc mock;

    @BeforeEach
    void initializeUser() throws Exception {
        SignupCredentials signup = new SignupCredentials();
        signup.setEmail("Tester2@gmail.com");
        signup.setPassword("Password");
        signup.setPhoneNumber("999-999-9999");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(signup);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/user/create").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        JwtJson jwtTemp = new ObjectMapper().readValue(json, JwtJson.class);
        jwt = jwtTemp.getJwtToken();
        System.out.println(jwt);
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);

        Product product = productService.getProduct(3L);
        ObjectMapper mapper2 = new ObjectMapper();
        mapper2.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow2 = mapper2.writer().withDefaultPrettyPrinter();
        String requestJson2=ow2.writeValueAsString(product);

        RequestBuilder request2 = MockMvcRequestBuilders
                .put("/api/v1/user/cart/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson2).headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
    }

    @Test
    //creates an order then gets all, gets one, then changes the order status to canceled;
    void multiTest() throws Exception {
        //Create an Order
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.post("/api/v1/order")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        int jsonStatus = result.getResponse().getStatus();
        assertEquals(201, jsonStatus);

        //GetList of orders
        RequestBuilder request2 = MockMvcRequestBuilders.get("/api/v1/order")
                .headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
        int jsonStatus2 = result2.getResponse().getStatus();
        assertEquals(200, jsonStatus2);

        Long orderId = userRepository.findByEmail("Tester2@gmail.com").get().getOrders()
                .stream().reduce((order, finalOrder) ->  order).get().getId();

        //get one order
        RequestBuilder request3 = MockMvcRequestBuilders.get("/api/v1/order/" + orderId)
                .headers(headers);
        MvcResult result3 = mock.perform(request3).andReturn();
        String json = result3.getResponse().getContentAsString();
        OrderJson order = new ObjectMapper().readValue(json, OrderJson.class);
        assertEquals(OrderJson.class, order.getClass());

        //update Status
        RequestBuilder request4 = MockMvcRequestBuilders
                .put("/api/v1/order/" + orderId + "/CANCELED").headers(headers);
        MvcResult result4 = mock.perform(request4).andReturn();
        int jsonStatus3 = result4.getResponse().getStatus();
        assertEquals(200, jsonStatus3);
    }

    @AfterEach
    void deleteUser() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request3 = MockMvcRequestBuilders.delete("/api/v1/user")
                .headers(headers);
        mock.perform(request3).andReturn();
    }


}
