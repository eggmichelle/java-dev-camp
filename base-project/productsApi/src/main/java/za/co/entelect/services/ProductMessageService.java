package za.co.entelect.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.enums.ResponseStatus;
import za.co.entelect.pojo.requests.FulfilmentServiceRequest;
import za.co.entelect.pojo.responses.FulfilmentServiceResponse;
import za.co.entelect.services.pc.CheckStatusService;

@Service
@Slf4j
public class ProductMessageService {

    private final RabbitTemplate rabbitTemplate;
    private final CheckStatusService checkStatusService;

    @Autowired
    public ProductMessageService(RabbitTemplate rabbitTemplate, CheckStatusService checkStatusService) {
        this.rabbitTemplate = rabbitTemplate;
        this.checkStatusService = checkStatusService;
    }

    public void produceMessage(Customer customer, String fulfilmentType) {
        FulfilmentServiceRequest fulfilmentServiceRequest = new FulfilmentServiceRequest(customer, fulfilmentType);
        rabbitTemplate.convertAndSend("", "q.fulfilmentRequest",
                fulfilmentServiceRequest);
    }

    @RabbitListener(queues = {"q.fulfilmentResponse"})
    public void consumeMessage(FulfilmentServiceResponse fulfilmentServiceResponse) {
        log.info("Consumed Message: {}", fulfilmentServiceResponse);

        if (fulfilmentServiceResponse.getStatus().equals(ResponseStatus.FAILED_TO_COMPLETE)) {
            produceMessage(fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getFulfilmentType());
        } else {
            checkStatusService.handleFulfilmentResponse(fulfilmentServiceResponse);
        }
    }
}
