package za.co.entelect.pojo.cis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Customer {

    private Long id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String idNumber;
    private Long customerTypeId;
    private CustomerType customerType;
    private List<AccountType> customerAccounts;

    public Customer() {
    }
}