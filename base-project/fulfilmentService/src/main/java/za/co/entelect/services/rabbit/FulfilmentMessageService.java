package za.co.entelect.services.rabbit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.FulfilmentChecks;
import za.co.entelect.pojo.requests.FulfilmentServiceRequest;
import za.co.entelect.pojo.responses.FulfilmentServiceResponse;
import za.co.entelect.pojo.enums.ResponseStatus;

@Service
@Slf4j
public class FulfilmentMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final FulfilmentChecks fulfilmentChecks;

    @Autowired
    public FulfilmentMessageService(RabbitTemplate rabbitTemplate, FulfilmentChecks fulfilmentChecks) {
        this.rabbitTemplate = rabbitTemplate;
        this.fulfilmentChecks = fulfilmentChecks;
    }

    public void produceMessage(FulfilmentServiceResponse fulfilmentServiceResponse) {
        rabbitTemplate.convertAndSend("", "q.fulfilmentResponse",
                fulfilmentServiceResponse);
    }

    @RabbitListener(queues = {"q.fulfilmentRequest"})
    public void consumeMessage(FulfilmentServiceRequest fulfilmentServiceRequest) {
        log.info("Consumed Message: {}", fulfilmentServiceRequest);

        try {
            FulfilmentServiceResponse fulfilmentServiceResponse = fulfilmentChecks.doChecks(fulfilmentServiceRequest);
            fulfilmentServiceResponse.setFulfilmentServiceRequest(fulfilmentServiceRequest);
            produceMessage(fulfilmentServiceResponse);

        } catch (za.co.entelect.client.kyc.ApiException | za.co.entelect.client.dha.ApiException e) {
            log.warn("Fulfilment checks failed: {}", e.toString());
            produceMessage(new FulfilmentServiceResponse(fulfilmentServiceRequest,ResponseStatus.FAILED_TO_COMPLETE, null));
        }
    }
}
