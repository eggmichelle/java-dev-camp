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
import za.co.entelect.entities.pc.*;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.mappers.pc.CartItemMapper;
import za.co.entelect.mappers.pc.CartMapper;
import za.co.entelect.mappers.pc.OrderMapper;
import za.co.entelect.mappers.pc.ProductMapper;
import za.co.entelect.mappers.us.UserMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.pc.*;
import za.co.entelect.pojo.us.User;
import za.co.entelect.repository.cis.testing.CustomerRepository;
import za.co.entelect.repository.pc.*;
import za.co.entelect.repository.us.UserRepository;
import za.co.entelect.services.TokenHolder;
import za.co.entelect.services.us.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest(classes = ProductsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderControllerIntegrationTest {

    private final TestRestTemplate testRestTemplate;
    private final CustomerApi customerApi;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrdersRepository ordersRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final CheckStatusRepository checkStatusRepository;
    private final UserService userService;
    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private Customer customer;
    private User user;
    private String token;
    private Order orders;
    private Long createPendingOrderOrderId;

    @Autowired
    public OrderControllerIntegrationTest(TestRestTemplate testRestTemplate, CustomerRepository customerRepository, UserRepository userRepository, CartRepository cartRepository, CartItemRepository cartItemRepository, ProductRepository productRepository, OrderItemsRepository orderItemsRepository, CheckStatusRepository checkStatusRepository, UserService userService, CartItemMapper cartItemMapper, CartMapper cartMapper, ProductMapper productMapper, OrdersRepository ordersRepository, OrderMapper orderMapper, CustomerMapper customerMapper, UserMapper userMapper, PasswordEncoder encoder) {
        this.testRestTemplate = testRestTemplate;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.checkStatusRepository = checkStatusRepository;
        this.userService = userService;
        this.cartItemMapper = cartItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.ordersRepository = ordersRepository;
        this.orderMapper = orderMapper;
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    @BeforeEach
    public void setUp() throws Exception {
        createCustomer();
        createCart();
        createOrder();
        login();
    }

    private void createCustomer() throws Exception {
        try {
            CustomerDto customerToDelete = customerApi.getCustomerByEmailAddress("test.user@example.com");
            Optional<UserEntity> userToDelete = userRepository.findByEmail(customerToDelete.getUsername());
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

    protected void createCart() {
        Optional<CartEntity> cartEntityOptional = Optional.ofNullable(cartRepository.findByCustomerId(customer.getId()));

        Cart cart;
        if (cartEntityOptional.isPresent()) {
            cart = cartMapper.toDomain(cartEntityOptional.get());
        } else {
            cart = new Cart();
            cart.setCustomerId(customer.getId());
            cart = cartMapper.toDomain(cartRepository.save(cartMapper.toEntity(cart)));
        }

        if (cart.getCartItems().isEmpty()) {
            ProductEntity productEntity = productRepository.findById(1L).orElse(null);
            assert productEntity != null;
            Product product = productMapper.toDomain(productEntity);

            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);

            cartItemRepository.save(cartItemMapper.toEntity(cartItem, cart));
        }
    }

    protected void createOrder() {
        orders = new Order();
        orders.setCustomerId(customer.getId());
        orders = orderMapper.toDomain(ordersRepository.save(orderMapper.toEntity(orders)));
    }

    @AfterEach
    public void cleanUp() {
        ordersRepository.delete(orderMapper.toEntity(orders));
        deleteCart();
        deletePendingOrder();
        deleteCustomer();
    }

    private void deleteCustomer() {
        customerRepository.delete(customerMapper.toEntity(customer));
        userRepository.delete(userMapper.toEntity(user));
    }

    protected void deleteCart() {
        CartEntity cartEntity = cartRepository.findByCustomerId(customer.getId());
        if (cartEntity != null) {
            List<CartItemEntity> cartItemsEntities = cartItemRepository.findAllByCartId(cartEntity.getId());
            for (CartItemEntity cartItemsEntity : cartItemsEntities) {
                cartItemRepository.delete(cartItemsEntity);
            }
            cartRepository.delete(cartEntity);
        }
    }

    protected void deletePendingOrder() {
        if (createPendingOrderOrderId != null) {
            List<OrderItemsEntity> orderItemsEntities = orderItemsRepository.findAllByOrderId(createPendingOrderOrderId);
            for (OrderItemsEntity orderItemsEntity : orderItemsEntities) {
                CheckStatusEntity checkStatusEntity = checkStatusRepository.findByOrderItemId(orderItemsEntity.getId());
                checkStatusRepository.delete(checkStatusEntity);
                orderItemsRepository.delete(orderItemsEntity);
            }
            ordersRepository.deleteById(createPendingOrderOrderId);
        }
    }

    @Test
    void getOrdersByCustomerId_Success() {
        ResponseEntity<List<OrderSummary>> response = testRestTemplate.exchange(
                "/orders/customer/{customerId}",
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
        },
                customer.getId()
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getOrdersByCustomerId_Forbidden() {
        ResponseEntity<List<OrderSummary>> response = testRestTemplate.exchange(
                "/orders/customer/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                });
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getOrdersByCustomerId_NotFound() {
        Long customerId = customer.getId();
        cleanUp();
        ResponseEntity<List<OrderSummary>> response = testRestTemplate.exchange(
                "/orders/customer/" + customerId,
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>(){}
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createPendingOrder_Success() {
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/orders/pending",
                HttpMethod.POST,
                getHttpEntity(),
                Order.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        Order order = response.getBody();
        assertThat(order).isNotNull();
        assertThat(order.getStatus()).isEqualTo("PENDING");

        createPendingOrderOrderId = order.getOrderId();
    }

    @Test
    void createPendingOrder_NoContent() {
        deleteCart();
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/orders/pending",
                HttpMethod.POST,
                getHttpEntity(),
                Order.class);
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getOrderById_Success() {
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/orders/{customerId}/{orderId}",
                HttpMethod.GET, getHttpEntity(),
                Order.class,
                customer.getId(),
                orders.getOrderId()
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Order order = response.getBody();
        assertThat(order).isNotNull();
        assertThat(order.getOrderId()).isEqualTo(orders.getOrderId());
    }

    @Test
    void getOrderById_Forbidden() {
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/orders/{customerId}/{orderId}",
                HttpMethod.GET,
                getHttpEntity(),
                Order.class,
                1L,
                orders.getOrderId());
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getOrderById_NotFound() {
        cleanUp();
        ResponseEntity<Order> response = testRestTemplate.exchange(
                "/orders/{customerId}/{orderId}",
                HttpMethod.GET,
                getHttpEntity(),
                Order.class,
                customer.getId(),
                orders.getOrderId());
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
