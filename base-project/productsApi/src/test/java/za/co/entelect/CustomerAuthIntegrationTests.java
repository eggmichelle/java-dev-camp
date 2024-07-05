package za.co.entelect;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.mappers.us.UserMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.us.User;
import za.co.entelect.repository.us.UserRepository;
import za.co.entelect.repository.cis.testing.CustomerRepository;
import za.co.entelect.services.TokenHolder;
import za.co.entelect.services.us.UserService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@SpringBootTest(classes = ProductsApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerAuthIntegrationTests {

    private final TestRestTemplate testRestTemplate;
    private final CustomerApi customerApi;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;
    private Customer customer;
    private User user;

    @Autowired
    public CustomerAuthIntegrationTests(TestRestTemplate testRestTemplate, UserService userService, CustomerRepository customerRepository, UserRepository userRepository, CustomerMapper customerMapper, UserMapper userMapper, PasswordEncoder encoder) {
        this.testRestTemplate = testRestTemplate;
        this.userService = userService;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
        this.encoder = encoder;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    @BeforeEach
    public void setUp() throws Exception {
        try {
            CustomerDto customerToDelete = customerApi.getCustomerByEmailAddress("test.user@example.com");
            Optional<UserEntity> userToDelete = userRepository.findByEmail(customerToDelete.getUsername());
            customerRepository.delete(customerMapper.toEntity(customerToDelete));
            userToDelete.ifPresent(userRepository::delete);
        } catch (ApiException ignored) {
        }

        try {
            CustomerDto customerToDelete = customerApi.getCustomerByEmailAddress("new.user@example.com");
            customerRepository.delete(customerMapper.toEntity(customerToDelete));
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

    @AfterEach
    public void cleanUp() {
        try {
            CustomerDto customerToDelete = customerApi.getCustomerByEmailAddress("new.user@example.com");
            customerRepository.delete(customerMapper.toEntity(customerToDelete));
        } catch (ApiException ignored) {
        }
        customerRepository.delete(customerMapper.toEntity(customer));
        userRepository.delete(userMapper.toEntity(user));
    }

    @Test
    void createUser_Success() {
        CustomerBody testUser = new CustomerBody();
        testUser.setUsername("new.user@example.com");
        testUser.setFirstName("New");
        testUser.setLastName("User");
        testUser.setIdNumber("9203031000093");
        testUser.setPassword("password");
        testUser.setCustomerTypeId(3L);

        ResponseEntity<Customer> response = testRestTemplate.postForEntity(
                "/customers/register",
                testUser,
                Customer.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void createUser_Conflict() {
        CustomerBody existingUser = new CustomerBody();
        existingUser.setUsername("test.user@example.com");
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setIdNumber("9203031000093");
        existingUser.setPassword("password");
        existingUser.setCustomerTypeId(3L);

        ResponseEntity<Customer> response = testRestTemplate.postForEntity(
                "/customers/register",
                existingUser,
                Customer.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_Success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = testRestTemplate.withBasicAuth
                        (customer.getUsername(), "password")
                .exchange(
                        "/customers/login",
                        HttpMethod.POST,
                        entity,
                        String.class
                );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}
