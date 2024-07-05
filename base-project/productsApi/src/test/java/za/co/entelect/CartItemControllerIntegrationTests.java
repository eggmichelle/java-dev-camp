package za.co.entelect;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.entities.pc.ProductEntity;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.mappers.us.UserMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.pojo.us.User;
import za.co.entelect.repository.pc.ProductRepository;
import za.co.entelect.repository.us.UserRepository;
import za.co.entelect.repository.cis.testing.CustomerRepository;
import za.co.entelect.services.*;
import za.co.entelect.services.pc.CartItemService;
import za.co.entelect.services.pc.CartService;
import za.co.entelect.services.us.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest(classes = ProductsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CartItemControllerIntegrationTests {

    private final TestRestTemplate testRestTemplate;
    private final CustomerApi customerApi;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final ProductRepository productRepository;
    private final PasswordEncoder encoder;
    private final CartService cartService;
    private final CartItemService cartItemService;
    private Customer customer;
    private User user;
    private String token;

    @Autowired
    public CartItemControllerIntegrationTests(TestRestTemplate testRestTemplate, UserService userService, CustomerRepository customerRepository, UserRepository userRepository, CustomerMapper customerMapper, UserMapper userMapper, ProductRepository productRepository, PasswordEncoder encoder, CartService cartService, CartItemService cartItemService) {
        this.testRestTemplate = testRestTemplate;
        this.userService = userService;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.productRepository = productRepository;
        this.encoder = encoder;
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    @BeforeEach
    public void setUp() throws Exception {
        try {
            CustomerDto customerToDelete = customerApi.getCustomerByEmailAddress("test.user@example.com");
            Optional<UserEntity> userToDelete = userRepository.findByEmail(customerToDelete.getUsername());

            if (customerToDelete.getId() != null ) {
                Cart cart = cartService.getCartByCustomerId(customerMapper.toDomain(customerToDelete).getId());
                if (cart != null) cartService.deleteCart(cart);
            }

            customerRepository.delete(customerMapper.toEntity(customerToDelete));
            userToDelete.ifPresent(userRepository::delete);
        } catch (ApiException ignored) {
        }

        CustomerBody testUser = new CustomerBody();
        testUser.setUsername("test.user@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setIdNumber("9203031000093");
        testUser.setPassword(encoder.encode("password"));
        testUser.setCustomerTypeId(3L);

        try {
            CustomerDto customerDto = customerMapper.toDto(testUser);
            this.customer = customerMapper.toDomain(customerApi.registerCustomer(customerDto));
            this.user = userService.createUser(testUser.getUsername(), testUser.getPassword());
        } catch (ApiException e) {
            throw new Exception("Could not register");
        }

        login();
    }

    protected void login() {
        ResponseEntity<String> response = testRestTemplate
                .withBasicAuth(customer.getUsername(), "password")
                .postForEntity("/customers/login", null, String.class);
        token = response.getBody();
    }

    protected HttpEntity<String> getHttpEntity() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return new HttpEntity<>(null, httpHeaders);
    }

    @AfterEach
    public void cleanUp() {
        Cart cart = cartService.getCartByCustomerId(customer.getId());
        if (cart != null) {
            cartService.deleteCart(cart);
        }

        customerRepository.delete(customerMapper.toEntity(customer));
        userRepository.delete(userMapper.toEntity(user));
    }

    @Test
    void addCartItem_Success() {
        ProductEntity existingProduct = productRepository.findById(1L).orElse(null);
        assertThat(existingProduct).isNotNull();

        ResponseEntity<Cart> response = testRestTemplate.exchange(
                "/cartItems/" + 1L,
                HttpMethod.POST,
                getHttpEntity(),
                Cart.class,
                existingProduct.getId()
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getCartItems_Success() {
        Cart cart = cartService.createCart(customer.getId());
        ProductEntity existingProduct = productRepository.findById(1L).orElse(null);
        assertThat(existingProduct).isNotNull();
        cartItemService.createCartItem(cart, existingProduct.getId());

        ResponseEntity<List<Product>> response = testRestTemplate.exchange(
                "/cartItems/" + customer.getId(),
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getCartItems_Forbidden() {
        ResponseEntity<List<Product>> response = testRestTemplate.exchange(
                "/cartItems/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getCartItems_NoContent() {
        ResponseEntity<List<Product>> response = testRestTemplate.exchange(
                "/cartItems/" + customer.getId(),
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}