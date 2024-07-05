package za.co.entelect.mappers.cis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.entelect.client.cis.models.CustomerTypesDto;
import za.co.entelect.pojo.cis.CustomerType;

@Component
@AllArgsConstructor
public class CustomerTypeMapper {

    public CustomerType toDomain(CustomerTypesDto customerTypesDto) {
        Long customerTypeId = null;
        if (customerTypesDto.getId() != null) customerTypeId = customerTypesDto.getId().longValue();

        return new CustomerType(
                customerTypeId,
                customerTypesDto.getDescription(),
                customerTypesDto.getName()
        );
    }
}
