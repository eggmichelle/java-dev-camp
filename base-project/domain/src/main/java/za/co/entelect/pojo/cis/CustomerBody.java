package za.co.entelect.pojo.cis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CustomerBody {
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String idNumber;
    private Long customerTypeId;

    public CustomerBody() {
    }
}