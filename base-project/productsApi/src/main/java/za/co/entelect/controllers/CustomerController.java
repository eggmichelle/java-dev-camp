package za.co.entelect.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.api.CustomerTypesApi;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.client.cis.models.CustomerTypesDto;
import za.co.entelect.client.cis.models.DocumentDto;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.mappers.cis.CustomerTypeMapper;
import za.co.entelect.pojo.cis.AccountType;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.cis.CustomerType;
import za.co.entelect.pojo.pc.DetailedProduct;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.pojo.responses.EligibilityResponse;
import za.co.entelect.pojo.us.User;
import za.co.entelect.services.*;
import za.co.entelect.services.cis.CustomerService;
import za.co.entelect.services.pc.ProductService;
import za.co.entelect.services.us.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder encoder;
    private final ProductService productService;
    private final UserService userService;
    private final CustomerService customerService;
    private final CustomerMapper customerMapper;
    private final CustomerTypeMapper customerTypeMapper;
    private final CustomerApi customerApi;
    private final CustomerTypesApi customerTypesApi;

    @Autowired
    public CustomerController(JwtEncoder jwtEncoder, PasswordEncoder encoder, ProductService productService, UserService userService, CustomerService customerService, CustomerMapper customerMapper, CustomerTypeMapper customerTypeMapper) {
        this.jwtEncoder = jwtEncoder;
        this.encoder = encoder;
        this.productService = productService;
        this.userService = userService;
        this.customerService = customerService;
        this.customerMapper = customerMapper;
        this.customerTypeMapper = customerTypeMapper;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
        this.customerTypesApi = new CustomerTypesApi(new za.co.entelect.client.cis.ApiClient());
        this.customerTypesApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    @PostMapping("/register")
    public ResponseEntity<Customer> createUser(@RequestBody CustomerBody customerBody) {
        try {
            if (userService.isEmailUnique(customerBody.getUsername())) {
                Customer customer = customerMapper.toDomain(customerApi.registerCustomer(customerMapper.toDto(customerBody)));
                customerBody.setPassword(encoder.encode(customerBody.getPassword()));
                log.info("Customer created: " + customer);

                User user = userService.createUser(customerBody.getUsername(), customerBody.getPassword());
                log.info("User created: " + user);

                return ResponseEntity.ok(customer);
            } else {
                log.error("Error occurred while creating customer: " + "This user already exists. Please login, or register with a different email address.");
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        } catch (ApiException e) {
            log.error("Error occurred while creating customer: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/login")
    public String login(Authentication authentication) {
        Instant now = Instant.now();
        long expiry = 3600L;

        Set<String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
        Long id = ((CustomUser) authentication.getPrincipal()).getId();
        JwtClaimsSet claimsSet = JwtClaimsSet.builder().issuer("self").issuedAt(now).expiresAt(now.plusSeconds(expiry)).subject(authentication.getName()).claim("authorities", authorities).claim("userId", id).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();
    }

    @GetMapping
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        try {
            List<CustomerDto> customers = customerApi.getCustomers();
            log.info("List of customers: " + customers);
            return ResponseEntity.ok(customers);
        } catch (ApiException e) {
            log.error("Error occurred while fetching customer profiles: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            Customer customer = customerService.getCustomerFromAuth(authorizationHeader);
            if (!Objects.equals(customerId, customer.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            log.info("Customer: " + customer);
            return ResponseEntity.ok(customer);
        } catch (ApiException e) {
            log.error("Error occurred while fetching customer profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
    }

    @GetMapping("/checkEligibility/{customerId}/{productId}")
    public ResponseEntity<EligibilityResponse> checkEligibility(@PathVariable Long customerId, @PathVariable Long productId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            try {
                Customer customer = customerService.getCustomerFromAuth(authorizationHeader);
                if (!Objects.equals(customerId, customer.getId())) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }

                Product product = productService.getProductById(productId);
                if (product == null) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
                }
                DetailedProduct detailedProduct = new DetailedProduct(product);
                EligibilityResponse response = productService.setEligibilityCriteria(detailedProduct);

                boolean isAccountTypeMatch = customerService.isAccountTypeMatch(detailedProduct, customer);
                boolean isCustomerTypeMatch = customerService.isCustomerTypeMatch(detailedProduct, customer);

                if (isCustomerTypeMatch) {
                    String matchingCustomerType = customer.getCustomerType().getName();
                    response.setMatchingCustomerType(Character.toUpperCase(matchingCustomerType.charAt(0)) + matchingCustomerType.substring(1).toLowerCase());
                }

                if (isAccountTypeMatch) {
                    for (AccountType accountType : customer.getCustomerAccounts()) {
                        if (detailedProduct.getEligibleAccountTypes().toString().contains(accountType.getName())) {
                            response.getMatchingAccountTypes().add(accountType.getName());
                        }
                    }
                }
                if (isAccountTypeMatch && isCustomerTypeMatch) {
                    response.setEligible(true);
                }

                log.info("Customer with id: " + customerId + " is eligible for product with id: " + productId + " : " + response.isEligible());
                return ResponseEntity.ok(response);

            } catch (ApiException e) {
                log.error("Error occurred while checking eligibility: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            log.error("Error occurred while checking eligibility: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/types")
    public ResponseEntity<List<CustomerType>> getCustomerTypes() {
        try {
            List<CustomerTypesDto> customerTypeDtos = customerTypesApi.getCustomerTypes();
            List<CustomerType> customerTypes = customerTypeDtos.stream().map(customerTypeMapper::toDomain).collect(Collectors.toList());
            log.info("Retrieved customer types: {}", customerTypes);
            return ResponseEntity.ok(customerTypes);
        } catch (ApiException e) {
            log.warn("Error retrieving customer types: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/types/{typeId}")
    public ResponseEntity<CustomerType> getCustomerTypesById(@PathVariable Long typeId) {
        try {
            List<CustomerTypesDto> customerTypes = customerTypesApi.getCustomerTypes();

            CustomerTypesDto customerType = customerTypes.stream().filter(type -> {
                Integer id = type.getId();
                return id != null && id.equals(typeId.intValue());
            }).findFirst().orElse(null);

            if (customerType != null) {
                log.info("Retrieved customer type {}", customerType);
                return ResponseEntity.ok(customerTypeMapper.toDomain(customerType));
            } else {
                log.info("Customer type with typeId {} not found ", typeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        } catch (ApiException e) {
            log.warn("Error retrieving customer types: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/documents/{customerId}")
    public ResponseEntity<List<DocumentDto>> getCustomerDocuments(@PathVariable Long customerId, @RequestHeader("Authorization") String authorizationHeader) {
        List<DocumentDto> documents = null;
        try {
            if (customerService.isNotAuthorized(customerId, authorizationHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            documents = customerApi.getCustomerDocumentsByCustomerId(Math.toIntExact(customerId));
            log.info("Retrieved customer documents: {}", documents);

            String downloadsPath = System.getProperty("user.home") + "/Downloads/";

            for (DocumentDto document : documents) {
                String filePath = downloadsPath + "contract" + document.getId() + ".pdf";

                byte[] decodedBytes = Base64.getDecoder().decode(document.getDocument());
                String decodedString = new String(decodedBytes);

                byte[] byteArray = Base64.getDecoder().decode(decodedString);
                Files.write(Paths.get(filePath), byteArray);
                log.info("Downloaded contract: {}", filePath);
            }

            return ResponseEntity.ok(documents);
        } catch (ApiException e) {
            log.error("Error occurred while fetching customer documents: " + e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("Could not save documents: {}", e.getMessage());
            return ResponseEntity.ok(documents);
        }
    }
}