package za.co.entelect.services.cis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.AccountsApi;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.api.CustomerTypesApi;
import za.co.entelect.client.cis.models.AccountTypeDto;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.client.cis.models.CustomerTypesDto;
import za.co.entelect.mappers.cis.AccountTypeMapper;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.mappers.cis.CustomerTypeMapper;
import za.co.entelect.pojo.cis.AccountType;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerType;
import za.co.entelect.pojo.pc.DetailedProduct;
import za.co.entelect.services.TokenHolder;

import java.util.List;

@Slf4j
@Service
public class CustomerService {
    private final CustomerApi customerApi;
    private final CustomerTypesApi customerTypesApi;
    private final AccountsApi accountsApi;
    private final CustomerMapper customerMapper;
    private final CustomerTypeMapper customerTypeMapper;
    private final AccountTypeMapper accountTypeMapper;
    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    public CustomerService(CustomerMapper customerMapper, CustomerTypeMapper customerTypeMapper, AccountTypeMapper accountTypeMapper) {
        this.customerMapper = customerMapper;
        this.customerTypeMapper = customerTypeMapper;
        this.accountTypeMapper = accountTypeMapper;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
        this.customerTypesApi = new CustomerTypesApi(new za.co.entelect.client.cis.ApiClient());
        this.customerTypesApi.getApiClient().setBearerToken(TokenHolder.getToken());
        this.accountsApi = new AccountsApi(new za.co.entelect.client.cis.ApiClient());
        this.accountsApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    public String getUserEmailFromToken(String jwtToken) {
        Jwt decodedJwt = jwtDecoder.decode(jwtToken);
        return decodedJwt.getSubject();
    }

    public List<CustomerType> getEligibleCustomerTypes(List<Long> qualifyingCustomerTypeIds) {
        try {
            List<CustomerTypesDto> customerTypeDtos = customerTypesApi.getCustomerTypes();
            List<CustomerType> customerTypes = customerTypeDtos.stream().map(customerTypeMapper::toDomain).toList();
            return customerTypes.stream().filter(customerType -> qualifyingCustomerTypeIds.contains(customerType.getId())).toList();
        } catch (ApiException e) {
            log.error("Error occurred while fetching customer types: " + e.getMessage());
            return null;
        }
    }

    public List<AccountType> getEligibleAccountTypes(List<Long> qualifyingAccountTypeIds) {
        try {
            List<AccountTypeDto> accountTypeDtos = accountsApi.getAccountTypes();
            List<AccountType> accountTypes = accountTypeDtos.stream().map(accountTypeMapper::toDomain).toList();
            return accountTypes.stream().filter(accountType -> qualifyingAccountTypeIds.contains(accountType.getId())).toList();
        } catch (ApiException e) {
            log.error("Error occurred while fetching account types: " + e.getMessage());
            return null;
        }
    }

    public boolean isAccountTypeMatch(DetailedProduct detailedProduct, Customer customer) {
        return detailedProduct.getEligibleAccountTypes().stream().anyMatch(accountType -> customer.getCustomerAccounts().stream().anyMatch(account -> account.getId().equals(accountType.getId())));
    }

    public boolean isCustomerTypeMatch(DetailedProduct detailedProduct, Customer customer) {
        return detailedProduct.getEligibleCustomerTypes().stream().anyMatch(customerType -> customer.getCustomerType().getId().equals(customerType.getId()));
    }

    public boolean isNotAuthorized(Long customerId, String authorizationHeader) throws ApiException {
        String userEmail = getUserEmailFromToken(authorizationHeader.substring("Bearer ".length()));
        CustomerDto customer = customerApi.getCustomerById(customerId.intValue());
        return !userEmail.equals(customer.getUsername());
    }

    public Customer getCustomerFromAuth(String authorizationHeader) throws ApiException {
        String customerEmail = getUserEmailFromToken(authorizationHeader.substring("Bearer ".length()));
        return customerMapper.toDomain(customerApi.getCustomerByEmailAddress(customerEmail));
    }
}
