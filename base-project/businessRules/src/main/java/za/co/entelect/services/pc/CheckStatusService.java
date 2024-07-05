package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.ContractHouse;
import za.co.entelect.InsuranceHouse;
import za.co.entelect.InvestmentProductHouse;
import za.co.entelect.entities.pc.CheckStatusEntity;
import za.co.entelect.mappers.pc.CheckStatusMapper;
import za.co.entelect.pojo.enums.ResponseStatus;
import za.co.entelect.pojo.enums.Status;
import za.co.entelect.pojo.pc.CheckStatus;
import za.co.entelect.pojo.pc.FulfilmentType;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.pojo.responses.FulfilmentServiceResponse;
import za.co.entelect.repository.pc.CheckStatusRepository;
import za.co.entelect.services.cis.DocumentService;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class CheckStatusService {

    @Autowired
    private final InsuranceHouse insuranceHouse;

    @Autowired
    private final ContractHouse contractHouse;

    @Autowired
    private final InvestmentProductHouse investmentProductHouse;

    @Autowired
    DocumentService documentService;

    @Autowired
    CheckStatusRepository checkStatusRepository;

    @Autowired
    CheckStatusMapper checkStatusMapper;

    @Autowired
    OrderService orderService;

    @Autowired
    FulfilmentTypeService fulfilmentTypeService;

    public void createPendingCheckStatus(OrderItem orderItem) {
        CheckStatus checkStatus = new CheckStatus(
                null,
                Status.PENDING,
                ""
        );

        CheckStatusEntity savesCheckStatus = checkStatusRepository.save(checkStatusMapper.toEntity(checkStatus, orderItem));

        checkStatusMapper.toDomain(savesCheckStatus);
    }

    private void updateCheckStatus(CheckStatus checkStatus) {
        Optional<CheckStatusEntity> optionalCheckStatusEntity = checkStatusRepository.findById(checkStatus.getCheckStatusId());

        if (optionalCheckStatusEntity.isPresent()) {
            CheckStatusEntity existingCheckStatusEntity = optionalCheckStatusEntity.get();
            existingCheckStatusEntity.setStatus(checkStatus.getStatus());
            existingCheckStatusEntity.setDescription(checkStatus.getDescription());

            checkStatusRepository.save(existingCheckStatusEntity);
        } else {
            log.warn("Check status with ID " + checkStatus.getCheckStatusId() + " not found");
        }
    }

    public void openAccount(OrderItem orderItem, FulfilmentServiceResponse fulfilmentServiceResponse) {

        if (orderItem.getProduct().getName().contains("Insurance")) {
            insuranceHouse.openAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        } else if (orderItem.getProduct().getName().contains("Contract")) {
            contractHouse.openAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        } else if (orderItem.getProduct().getName().contains("Investment Product")) {
            investmentProductHouse.openAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        }
    }

    public void closeAccount(OrderItem orderItem, FulfilmentServiceResponse fulfilmentServiceResponse) {
        if (orderItem.getProduct().getName().contains("Insurance")) {
            insuranceHouse.closeAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        } else if (orderItem.getProduct().getName().contains("Contract")) {
            contractHouse.closeAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        } else if (orderItem.getProduct().getName().contains("Investment Product")) {
            investmentProductHouse.closeAccount(orderItem.getProduct(), fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
        }

    }

    public void handleFulfilmentResponse(FulfilmentServiceResponse fulfilmentServiceResponse) {
        List<Order> orders = orderService.getPendingCustomerOrders(fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer().getId());
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItemList()) {
                FulfilmentType fulfilmentType = fulfilmentTypeService.getFulfilmentTypeByProductId(orderItem.getProduct().getProductId());
                if (fulfilmentType.getName().equals(fulfilmentServiceResponse.getFulfilmentServiceRequest().getFulfilmentType())) {
                    CheckStatus checkStatus = orderItem.getCheckStatus();
                    if (checkStatus != null) {
                        if (fulfilmentServiceResponse.getStatus().equals(ResponseStatus.PASSED)) {
                            checkStatus.setStatus(Status.PASSED);
                            openAccount(orderItem, fulfilmentServiceResponse);
                        } else {
                            checkStatus.setStatus(Status.FAILED);
                            checkStatus.setDescription(fulfilmentServiceResponse.getFailedCheckTypes().toString());
                            closeAccount(orderItem, fulfilmentServiceResponse);
                        }
                        updateCheckStatus(checkStatus);
                    }
                }
            }

            boolean completed = true;
            for (OrderItem orderItem : order.getOrderItemList()) {
                if (orderItem.getCheckStatus() != null) {
                    if (orderItem.getCheckStatus().getStatus().equals(Status.PENDING)) {
                        completed = false;
                        break;
                    }
                }
            }
            if (completed) {
                order.setStatus("COMPLETED");
                orderService.updateOrder(order);
                documentService.generatePdfContract(order, fulfilmentServiceResponse.getFulfilmentServiceRequest().getCustomer());
            }
        }
    }



}