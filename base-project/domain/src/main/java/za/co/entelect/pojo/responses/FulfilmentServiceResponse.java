package za.co.entelect.pojo.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.requests.FulfilmentServiceRequest;
import za.co.entelect.pojo.enums.CheckType;
import za.co.entelect.pojo.enums.ResponseStatus;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class FulfilmentServiceResponse {

    private FulfilmentServiceRequest fulfilmentServiceRequest;
    private ResponseStatus status;
    private List<CheckType> failedCheckTypes;

    public FulfilmentServiceResponse() {
    }
}
