package za.co.entelect.mappers.cis;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.entities.cis.testing.CustomerEntity;
import za.co.entelect.pojo.cis.AccountType;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.cis.CustomerBody;
import za.co.entelect.pojo.cis.CustomerType;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CustomerMapper {

    @Autowired
    private final CustomerTypeMapper customerTypeMapper;

    @Autowired
    private final AccountTypeMapper accountTypeMapper;

    public Customer toDomain(CustomerDto customerDto) {
        Long customerId = null;
        if (customerDto.getId() != null) customerId = customerDto.getId().longValue();

        Long customerTypeId = null;
        if (customerDto.getCustomerTypeId() != null) customerTypeId = customerDto.getCustomerTypeId().longValue();

        CustomerType customerType = null;
        if (customerDto.getCustomerType() != null) {
            customerType = customerTypeMapper.toDomain(customerDto.getCustomerType());
        }

        List<AccountType> accountTypes = null;
        if (customerDto.getCustomerAccounts() != null) {
            accountTypes = customerDto.getCustomerAccounts().stream().map(accountTypeMapper::toDomain).collect(Collectors.toList());
        }

        return new Customer(
                customerId,
                customerDto.getUsername(),
                customerDto.getPassword(),
                customerDto.getFirstName(),
                customerDto.getLastName(),
                customerDto.getIdNumber(),
                customerTypeId,
                customerType,
                accountTypes
        );
    }

    public CustomerDto toDto(CustomerBody customerBody) {
        CustomerDto customerDto = new CustomerDto();

        customerDto.setUsername(customerBody.getUsername());
        customerDto.setPassword(customerBody.getPassword());
        customerDto.setFirstName(customerBody.getFirstName());
        customerDto.setLastName(customerBody.getLastName());
        customerDto.setIdNumber(customerBody.getIdNumber());
        customerDto.setCustomerTypeId(customerBody.getCustomerTypeId().intValue());

        return customerDto;
    }

    public CustomerEntity toEntity(CustomerDto customerDto) {
        CustomerEntity customerEntity = new CustomerEntity();

        Long customerId = null;
        if (customerDto.getId() != null) customerId = customerDto.getId().longValue();

        Long customerTypeId = null;
        if (customerDto.getCustomerTypeId() != null) customerTypeId = customerDto.getCustomerTypeId().longValue();

        customerEntity.setId(customerId);
        customerEntity.setEmail(customerDto.getUsername());
        customerEntity.setPassword(customerDto.getPassword());
        customerEntity.setFirstName(customerDto.getFirstName());
        customerEntity.setLastName(customerDto.getLastName());
        customerEntity.setIdNumber(customerDto.getIdNumber());
        customerEntity.setCustomerTypesId(customerTypeId);
        customerEntity.setRole("ADMIN");

        return customerEntity;
    }

    public CustomerEntity toEntity(Customer customer) {
        CustomerEntity customerEntity = new CustomerEntity();

        customerEntity.setId(customer.getId());
        customerEntity.setEmail(customer.getUsername());
        customerEntity.setPassword(customer.getPassword());
        customerEntity.setFirstName(customer.getFirstName());
        customerEntity.setLastName(customer.getLastName());
        customerEntity.setIdNumber(customer.getIdNumber());
        customerEntity.setCustomerTypesId(customer.getCustomerTypeId());
        customerEntity.setRole("ADMIN");

        return customerEntity;
    }
}
