package za.co.entelect;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.client.creditcheck.CreditCheckResponse;
import za.co.entelect.client.dha.models.DuplicateIDDocumentCheck;
import za.co.entelect.client.dha.models.LivingStatus;
import za.co.entelect.client.dha.models.MaritalStatusEnum;
import za.co.entelect.client.dha.models.MaritalStatusResponse;
import za.co.entelect.client.kyc.models.KYC;
import za.co.entelect.client.kyc.models.TaxComplianceStatus;
import za.co.entelect.config.FulfilmentConfig;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.enums.CheckType;
import za.co.entelect.pojo.enums.ResponseStatus;
import za.co.entelect.pojo.requests.FulfilmentServiceRequest;
import za.co.entelect.pojo.responses.FraudCheckResponse;
import za.co.entelect.pojo.responses.FulfilmentServiceResponse;
import za.co.entelect.pojo.us.PastCheck;
import za.co.entelect.services.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Component
@Slf4j
public class FulfilmentChecks {

    private static FulfilmentServiceResponse fulfilmentServiceResponse;
    private final KYCService kycService;
    private final DHAService dhaService;
    private final CreditService creditService;
    private final ChecksHistoryService checksHistoryService;
    private final FraudService fraudService;
    private final FulfilmentConfig fulfilmentConfig;

    @Autowired
    public FulfilmentChecks(KYCService kycService, DHAService dhaService, CreditService creditService, ChecksHistoryService checksHistoryService, FraudService fraudService, FulfilmentConfig fulfilmentConfig) {
        this.kycService = kycService;
        this.dhaService = dhaService;
        this.creditService = creditService;
        this.checksHistoryService = checksHistoryService;
        this.fraudService = fraudService;
        this.fulfilmentConfig = fulfilmentConfig;
    }

    private static void initResponse() {
        fulfilmentServiceResponse = new FulfilmentServiceResponse();
        fulfilmentServiceResponse.setStatus(ResponseStatus.PASSED);
        fulfilmentServiceResponse.setFailedCheckTypes(new ArrayList<>());
    }

    private static void setFailedFulfilmentServiceResponse(CheckType checkType) {
        fulfilmentServiceResponse.setStatus(ResponseStatus.FAILED);
        fulfilmentServiceResponse.getFailedCheckTypes().add(checkType);
    }

    public FulfilmentServiceResponse doChecks(FulfilmentServiceRequest fulfilmentServiceRequest) throws za.co.entelect.client.kyc.ApiException, za.co.entelect.client.dha.ApiException {
        initResponse();
        Customer customer = fulfilmentServiceRequest.getCustomer();
        String fulfilmentType = fulfilmentServiceRequest.getFulfilmentType();

        check(customer, CheckType.KYC, fulfilmentConfig.getKycTimePeriod());

        if (fulfilmentType.equals("A")) return fulfilmentServiceResponse;

        check(customer, CheckType.FRAUD, fulfilmentConfig.getFraudTimePeriod());
        check(customer, CheckType.LIVING_STATUS, fulfilmentConfig.getLivingStatusTimePeriod());
        check(customer, CheckType.DUPLICATE_ID, fulfilmentConfig.getDuplicateIdTimePeriod());

        if (fulfilmentType.equals("B")) return fulfilmentServiceResponse;

        check(customer, CheckType.MARITAL_STATUS, fulfilmentConfig.getMaritalStatusTimePeriod());
        check(customer, CheckType.CREDIT, fulfilmentConfig.getCreditTimePeriod());

        return fulfilmentServiceResponse;
    }

    private void check(Customer customer, CheckType checkType, Duration duration) throws za.co.entelect.client.kyc.ApiException, za.co.entelect.client.dha.ApiException {
        PastCheck pastCheck = checksHistoryService.getHistory(customer.getId(), checkType);
        if (pastCheck != null) {
            if (daysDifference(pastCheck) > duration.toDays()) {
                if (checkFailed(customer, checkType)) {
                    setFailedFulfilmentServiceResponse(checkType);
                } else {
                    checksHistoryService.updateCheck(pastCheck);
                }
            }
        } else {
            if (checkFailed(customer, checkType)) {
                setFailedFulfilmentServiceResponse(checkType);
            } else {
                checksHistoryService.saveCheck(customer.getId(), LocalDate.now(), checkType);
            }
        }
    }

    private boolean checkFailed(Customer customer, CheckType checkType) throws za.co.entelect.client.kyc.ApiException, za.co.entelect.client.dha.ApiException {
        return !switch (checkType) {
            case KYC -> doKYCCheck(customer.getId());
            case CREDIT -> doCreditCheck(customer.getId());
            case DUPLICATE_ID -> doDuplicateIDStatusCheck(customer.getIdNumber());
            case FRAUD -> doFraudCheck(customer.getId(), customer.getIdNumber());
            case LIVING_STATUS -> doLivingDeceasedStatusCheck(customer.getIdNumber());
            case MARITAL_STATUS -> doMaritalStatusCheck(customer.getIdNumber());
        };
    }

    private long daysDifference(PastCheck pastCheck) {
        LocalDate currentDate = LocalDate.now();
        LocalDate checkDate = pastCheck.getCheckDate();

        return ChronoUnit.DAYS.between(checkDate, currentDate);
    }

    private Boolean doKYCCheck(Long idNumber) throws za.co.entelect.client.kyc.ApiException {
        KYC kyc = kycService.getKYCResponse(idNumber);
        return kyc.getPrimaryIndicator() && (kyc.getTaxCompliance().equals(TaxComplianceStatus.GREEN) || kyc.getTaxCompliance().equals(TaxComplianceStatus.AMBER));
    }

    private Boolean doLivingDeceasedStatusCheck(String idNumber) throws za.co.entelect.client.dha.ApiException {
        LivingStatus livingStatus = dhaService.getLivingStatus(idNumber);
        return livingStatus.getLivingStatus().equals(LivingStatus.LivingStatusEnum.ALIVE);
    }

    private Boolean doDuplicateIDStatusCheck(String idNumber) throws za.co.entelect.client.dha.ApiException {
        DuplicateIDDocumentCheck duplicateIDDocumentCheck = dhaService.getDuplicateIDStatus(idNumber);
        return !duplicateIDDocumentCheck.getHasDuplicateId();
    }

    private Boolean doMaritalStatusCheck(String idNumber) throws za.co.entelect.client.dha.ApiException {
        MaritalStatusResponse maritalStatusResponse = dhaService.getMaritalStatus(idNumber);
        return maritalStatusResponse.getCurrentStatus().getStatus().equals(MaritalStatusEnum.MARRIED);
    }

    private boolean doCreditCheck(Long id) {
        CreditCheckResponse creditCheckResponse = creditService.getCreditCheck(id.intValue());
        return !creditCheckResponse.getCreditCheckResult().equals("RED");
    }

    private boolean doFraudCheck(Long id, String idNumber) {
        FraudCheckResponse fraudCheckResponse = fraudService.getFraudCheckResponse(id, idNumber);
        return fraudCheckResponse.getBankStatus().equals("OK") && fraudCheckResponse.getNationalStatus().equals("OK");
    }
}