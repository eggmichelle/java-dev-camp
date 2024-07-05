package za.co.entelect.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.entelect.client.dha.ApiException;
import za.co.entelect.client.dha.api.DhaApi;
import za.co.entelect.client.dha.models.DuplicateIDDocumentCheck;
import za.co.entelect.client.dha.models.LivingStatus;
import za.co.entelect.client.dha.models.MaritalStatusResponse;
import za.co.entelect.config.TokenHolder;

@Service
@Slf4j
public class DHAService {

    private final DhaApi dhaApi;

    public DHAService() {
        this.dhaApi = new DhaApi(new za.co.entelect.client.dha.ApiClient());
        this.dhaApi.getApiClient().setBasePath("http://localhost:8082");
        this.dhaApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    public MaritalStatusResponse getMaritalStatus(String idNumber) throws ApiException {
            MaritalStatusResponse maritalStatusResponse = dhaApi.getMaritalStatusById(idNumber);
            log.info("Marital Status Response: {}", maritalStatusResponse);
            return maritalStatusResponse;
    }

    public DuplicateIDDocumentCheck getDuplicateIDStatus(String idNumber) throws ApiException {
        DuplicateIDDocumentCheck duplicateIDDocumentCheck = dhaApi.getDuplicateIdDocumentStatusById(idNumber);
        log.info("Duplicate ID Document Response: {}", duplicateIDDocumentCheck);
        return duplicateIDDocumentCheck;
    }

    public LivingStatus getLivingStatus(String idNumber) throws ApiException {
        LivingStatus livingStatus = dhaApi.getLivingStatusById(idNumber);
        log.info("Living Status Response: {}", livingStatus);
        return livingStatus;
    }
}
