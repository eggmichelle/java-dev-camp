package za.co.entelect.pojo.cis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class AccountType {

    private Long id;
    private String name;
    private String description;

}