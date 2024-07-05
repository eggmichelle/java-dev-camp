package za.co.entelect.pojo.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class FraudCheckResponse {

    private String bankStatus;
    private String nationalStatus;

}
