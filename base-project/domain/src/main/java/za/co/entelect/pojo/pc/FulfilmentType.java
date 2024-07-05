package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FulfilmentType {

    private Long fulfilmentTypeId;
    private String name;
    private String description;
    private Product product;
}