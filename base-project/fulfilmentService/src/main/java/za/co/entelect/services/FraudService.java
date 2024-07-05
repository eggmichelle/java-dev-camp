package za.co.entelect.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.entelect.pojo.responses.FraudCheckResponse;

@Service
@Slf4j
public class FraudService {

    public FraudCheckResponse getFraudCheckResponse(Long customerId, String idNumber) {
        FraudCheckResponse fraudCheckResponse = new FraudCheckResponse("OK", "OK");
        log.info("Fraud Response: {}", fraudCheckResponse);
        return fraudCheckResponse;
    }

}
