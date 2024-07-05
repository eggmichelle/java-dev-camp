package za.co.entelect.pojo.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.cis.Customer;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FulfilmentServiceRequest {

    private Customer customer;
    private String fulfilmentType;

}
