package za.co.entelect.mappers.cis;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.entelect.client.cis.models.AccountTypeDto;
import za.co.entelect.pojo.cis.AccountType;

@Component
@AllArgsConstructor
public class AccountTypeMapper {

    public AccountType toDomain(AccountTypeDto accountTypeDto) {
        return new AccountType(
                accountTypeDto.getId().longValue(),
                accountTypeDto.getName(),
                accountTypeDto.getDescription()
        );
    }
}
