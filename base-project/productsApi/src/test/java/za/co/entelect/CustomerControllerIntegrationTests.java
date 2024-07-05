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
import za.co.entelect.client.cis.models.DocumentDto;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.entities.us.UserEntity;
import za.co.entelect.mappers.us.UserMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.cis.CustomerType;
import za.co.entelect.pojo.responses.EligibilityResponse;
import za.co.entelect.pojo.us.User;
import za.co.entelect.repository.us.UserRepository;
import za.co.entelect.repository.cis.testing.CustomerRepository;
import za.co.entelect.services.TokenHolder;
import za.co.entelect.services.us.UserService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@SpringBootTest(classes = ProductsApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerIntegrationTests {

    private final TestRestTemplate testRestTemplate;
    private final CustomerApi customerApi;
    private final UserService userService;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    @Autowired
    public CustomerControllerIntegrationTests(TestRestTemplate testRestTemplate, UserService userService, CustomerRepository customerRepository, UserRepository userRepository, CustomerMapper customerMapper, UserMapper userMapper, PasswordEncoder encoder) {
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

    private Customer customer;
    private User user;
    private String token;

    @BeforeEach
    public void setUp() throws Exception {
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
        customerRepository.delete(customerMapper.toEntity(customer));
        userRepository.delete(userMapper.toEntity(user));
    }

    @Test
    void getAllCustomers_Success() {
        ResponseEntity<List<CustomerDto>> response = testRestTemplate.exchange(
                "/customers",
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                }
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getCustomerById_Success() {
        ResponseEntity<Customer> response = testRestTemplate.exchange(
                "/customers/" + customer.getId(),
                HttpMethod.GET,
                getHttpEntity(),
                Customer.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Customer customerReceived = response.getBody();
        assert customerReceived != null;
        assertThat(customerReceived.getId()).isEqualTo(customer.getId());
    }

    @Test
    void getCustomerById_Forbidden() {
        ResponseEntity<Customer> response = testRestTemplate.exchange(
                "/customers/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                Customer.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getCustomerById_NoContent() {
        Long customerId = customer.getId();
        cleanUp();

        ResponseEntity<Customer> response = testRestTemplate.exchange(
                "/customers/" + customerId,
                HttpMethod.GET,
                getHttpEntity(),
                Customer.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void checkEligibility_Success() {
        ResponseEntity<EligibilityResponse> response = testRestTemplate.exchange(
                "/customers/checkEligibility/" + customer.getId() + "/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                EligibilityResponse.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void checkEligibility_Forbidden() {
        ResponseEntity<EligibilityResponse> response = testRestTemplate.exchange(
                "/customers/checkEligibility/" + 1L + "/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                EligibilityResponse.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void checkEligibility_ProductNotFound() {
        ResponseEntity<EligibilityResponse> response = testRestTemplate.exchange(
                "/customers/checkEligibility/" + customer.getId() + "/" + 100L,
                HttpMethod.GET,
                getHttpEntity(),
                EligibilityResponse.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void checkEligibility_CustomerNotFound() {
        Long customerId = customer.getId();
        cleanUp();

        ResponseEntity<EligibilityResponse> response = testRestTemplate.exchange(
                "/customers/checkEligibility/" + customerId + "/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                EligibilityResponse.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCustomerTypes_Success() {
        ResponseEntity<List<CustomerType>> response = testRestTemplate.exchange(
                "/customers/types",
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                });
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getCustomerTypesById_Success() {
        ResponseEntity<CustomerType> response = testRestTemplate.exchange(
                "/customers/types/" + customer.getCustomerType().getId(),
                HttpMethod.GET,
                getHttpEntity(),
                CustomerType.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CustomerType customerType = response.getBody();
        assert customerType != null;
        assertThat(customerType.getId()).isEqualTo(customer.getCustomerType().getId());
    }

    @Test
    void getCustomerTypesById_NotFound() {
        ResponseEntity<CustomerType> response = testRestTemplate.exchange(
                "/customers/types/" + 100L,
                HttpMethod.GET,
                getHttpEntity(),
                CustomerType.class
        );
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCustomerDocuments_Success() {
        ResponseEntity<List<DocumentDto>> response = testRestTemplate.exchange(
                "/customers/documents/" + customer.getId(),
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                });
        then(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getCustomerDocuments_Forbidden() {
        ResponseEntity<List<DocumentDto>> response = testRestTemplate.exchange(
                "/customers/documents/" + 1L,
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                });
        then(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getCustomerDocuments_NotFound() {
        Long customerId = customer.getId();
        cleanUp();

        ResponseEntity<List<DocumentDto>> response = testRestTemplate.exchange(
                "/customers/documents/" + customerId,
                HttpMethod.GET,
                getHttpEntity(),
                new ParameterizedTypeReference<>() {
                });
        then(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}