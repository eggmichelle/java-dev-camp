package za.co.entelect.pojo.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FulfilmentTestRequest {

    private int customerId;
    private String fulfilmentType;
}
