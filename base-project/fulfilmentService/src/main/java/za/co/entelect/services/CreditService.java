package za.co.entelect.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import za.co.entelect.client.creditcheck.CreditCheck;
import za.co.entelect.client.creditcheck.CreditCheckResponse;
import za.co.entelect.services.rabbit.WebServiceMessageSenderWithAuth;

@Slf4j
public class CreditService extends WebServiceGatewaySupport {

    private final WebServiceTemplate webServiceTemplate;

    public CreditService() {
        webServiceTemplate = getWebServiceTemplate();
        webServiceTemplate.setMessageSender(new WebServiceMessageSenderWithAuth());
    }

    public CreditCheckResponse getCreditCheck(int id) {
        CreditCheck request = new CreditCheck();
        request.setCustomerId(id);
        String newSoapAction = "CreditCheck";

        CreditCheckResponse creditCheckResponse = (CreditCheckResponse) webServiceTemplate
                .marshalSendAndReceive("http://localhost:8083/CreditCheck", request,
                        new SoapActionCallback(
                                newSoapAction
                        ));

        log.info("Credit Check Response: {}", creditCheckResponse.getCreditCheckResult());
        return creditCheckResponse;
    }
}
