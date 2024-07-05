package za.co.entelect.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.entelect.client.kyc.ApiException;
import za.co.entelect.client.kyc.api.KycApi;
import za.co.entelect.client.kyc.models.KYC;
import za.co.entelect.config.TokenHolder;

@Service
@Slf4j
public class KYCService {
    private final KycApi kycApi;

    public KYCService() {
        this.kycApi = new KycApi(new za.co.entelect.client.kyc.ApiClient());
        this.kycApi.getApiClient().setBasePath("http://localhost:8081");
        this.kycApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    public KYC getKYCResponse(Long customerId) throws ApiException {
            KYC kyc = kycApi.getKYCStatusById(String.valueOf(customerId));
            log.info("KYC Response: {}", kyc);
            return kyc;
    }
}
