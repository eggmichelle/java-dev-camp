package za.co.entelect.pojo.cis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CustomerType {

    private Long id;
    private String description;
    private String name;

}
