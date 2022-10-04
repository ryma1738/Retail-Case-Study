package com.retail.caseStudy.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.retail.caseStudy.product.Product;
import com.retail.caseStudy.product.ProductService;
import com.retail.caseStudy.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.nio.charset.Charset;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceTest {
    //Sources: https://stackoverflow.com/questions/20504399/testing-springs-requestbody-using-spring-mockmvc
    // https://stackoverflow.com/questions/51873620/is-there-a-method-built-in-spring-mockmvc-to-get-json-content-as-object
    //https://www.youtube.com/watch?v=pNiRNRgi5Ws

    String jwt = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
        ".eyJzdWIiOiJVc2VyIERldGFpbHMiLCJpc3MiOiJjYXNlU3R1ZHkvaGV4YXdhcmUvcnltYTE3MzgiLCJpYXQiOjE2NjQ5MDE5NjIsImVtYWlsIjoidGVzdGVyMUBnbWFpbC5jb20ifQ" +
        ".AdCGmSeXyBXackZSw9uFFEpdi4a4JPJikWjKDdU3PNY";

    public static final MediaType APPLICATION_JSON_UTF8 =
            new MediaType(MediaType.APPLICATION_JSON.getType(),
                    MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRep;

    @Autowired
    private ProductService productService;

    @Autowired
    private MockMvc mock;

    @Test
    void getAllUsers() {
        List<User> users = userService.getAllUsers();
        assertThat(users).size().isGreaterThan(0);
    }

    @Test
    void getUser() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/user")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        User user = new ObjectMapper().readValue(json, User.class);
        assertEquals(User.class, user.getClass());
    }

    @Test
    void forgottenUserCheck() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/api/v1/user/forgot/tester1@gmail.com/801-666-6666");
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        ForgotPasswordJson info = new ObjectMapper().readValue(json, ForgotPasswordJson.class);
        ChangePasswordJson changePass = new ChangePasswordJson(info.getUserId(),
                info.getKey(), "Password");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(changePass);
        RequestBuilder request2 = MockMvcRequestBuilders
                .post("/api/v1/user/forgot").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson);
        MvcResult result2 = mock.perform(request2).andReturn();
        int response2Status = result2.getResponse().getStatus();
        assertEquals(200, response2Status);
    }

    @Test
    void getUsersCart() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request = MockMvcRequestBuilders.get("/api/v1/user/cart")
                .headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        CartJson cart = new ObjectMapper().readValue(json, CartJson.class);
        assertEquals(CartJson.class, cart.getClass());
    }

    @Test
    void createDeleteUser() throws Exception {
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
        JwtJson jwt = new ObjectMapper().readValue(json, JwtJson.class);
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt.getJwtToken());
        HttpHeaders headers = new HttpHeaders(header);
        RequestBuilder request2 = MockMvcRequestBuilders.get("/api/v1/user")
                .headers(headers);
        MvcResult result2 = mock.perform(request2).andReturn();
        String json2 = result2.getResponse().getContentAsString();
        User user = new ObjectMapper().readValue(json2, User.class);
        assertEquals(User.class, user.getClass());

        RequestBuilder request3 = MockMvcRequestBuilders.delete("/api/v1/user")
                .headers(headers);
        MvcResult result3 = mock.perform(request3).andReturn();
        int json3 = result3.getResponse().getStatus();
        assertEquals(200, json3);
    }

    @Test
    void updateUsersCart() throws Exception {
        MultiValueMap<String, String> header = new LinkedMultiValueMap<>();
        header.add("Authorization", "Bearer " + jwt);
        HttpHeaders headers = new HttpHeaders(header);
        Product product = productService.getProduct(3L);
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(product);
        RequestBuilder request = MockMvcRequestBuilders
                .put("/api/v1/user/cart/1").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson).headers(headers);
        MvcResult result = mock.perform(request).andReturn();
        int json = result.getResponse().getStatus();
        assertEquals(200, json);
        MockMvcRequestBuilders.put("/api/v1/user/cart/0")
                .contentType(APPLICATION_JSON_UTF8).content(requestJson).headers(headers);
    }

    @Test
    void login() throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.setEmail("tester1@gmail.com");
        credentials.setPassword("Password");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(credentials);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/api/v1/user/login").contentType(APPLICATION_JSON_UTF8)
                .content(requestJson);
        MvcResult result = mock.perform(request).andReturn();
        String json = result.getResponse().getContentAsString();
        JwtJson jwtConfirm = new ObjectMapper().readValue(json, JwtJson.class);
        assertEquals(JwtJson.class, jwtConfirm.getClass());
    }

}
