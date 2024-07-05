package za.co.entelect.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.entelect.ContractHouse;
import za.co.entelect.InsuranceHouse;
import za.co.entelect.InvestmentProductHouse;
import za.co.entelect.client.cis.ApiException;
import za.co.entelect.client.cis.api.CustomerApi;
import za.co.entelect.client.cis.models.CustomerDto;
import za.co.entelect.mappers.cis.CustomerMapper;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.*;
import za.co.entelect.pojo.requests.FulfilmentTestRequest;
import za.co.entelect.services.*;
import za.co.entelect.services.cis.CustomerService;
import za.co.entelect.services.pc.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final CustomerService customerService;
    private final FulfilmentTypeService fulfilmentTypeService;
    private final CustomerApi customerApi;
    private final CustomerMapper customerMapper;
    private final ProductMessageService productMessageService;
    private final CartService cartService;
    private final ProductHousesService productHousesService;

    @Autowired
    public OrderController(OrderService orderService, OrderItemService orderItemService, CustomerService customerService, FulfilmentTypeService fulfilmentTypeService, CustomerMapper customerMapper, InsuranceHouse insuranceHouse, ContractHouse contractHouse, InvestmentProductHouse investmentProductHouse, ProductMessageService productMessageService, CheckStatusService checkStatusService, CartService cartService, ProductHousesService productHousesService) {
        this.orderService = orderService;
        this.orderItemService = orderItemService;
        this.customerService = customerService;
        this.fulfilmentTypeService = fulfilmentTypeService;
        this.customerMapper = customerMapper;
        this.productMessageService = productMessageService;
        this.cartService = cartService;
        this.productHousesService = productHousesService;
        this.customerApi = new CustomerApi(new za.co.entelect.client.cis.ApiClient());
        this.customerApi.getApiClient().setBearerToken(TokenHolder.getToken());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderSummary>> getOrdersForCustomer(@PathVariable Long customerId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (customerService.isNotAuthorized(customerId, authorizationHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            List<Order> orders = orderService.getOrdersForCustomer(customerId);
            List<OrderSummary> orderSummaries = orders.stream().map(order -> new OrderSummary(order.getOrderId(), order.getCreateAt(), order.getStatus())).collect(Collectors.toList());
            log.info("Orders for customer with id {}", customerId);

            return ResponseEntity.ok(orderSummaries);
        } catch (ApiException e) {
            log.warn("Could not get customer orders: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{customerId}/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId, @RequestHeader("Authorization") String authorizationHeader) {
        try {
            if (customerService.isNotAuthorized(customerId, authorizationHeader)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Order order = orderService.findById(orderId);

            if (order != null) {
                log.info("Retrieved customer order {}", order);
                return new ResponseEntity<>(order, HttpStatus.OK);
            } else {
                log.info("Order not found");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (ApiException e) {
            log.warn("Could not get customer orders: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @PostMapping("/pending")
    public ResponseEntity<Order> createPendingOrder(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            Customer customer = customerService.getCustomerFromAuth(authorizationHeader);

            Cart cart = cartService.getCartByCustomerId(customer.getId());
            if (cart == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            List<Product> products = cartService.getProducts(cart);

            ResponseEntity<Order> error = productHousesService.createPendingAccounts(products, customer);
            if (error != null) return error;

            Order order = orderService.createPendingOrder(customer.getId());
            List<OrderItem> orderItems = orderItemService.createOrderItems(products, order);
            order.setOrderItemList(orderItems);

            for (Product product : products) {
                FulfilmentType fulfilmentType = fulfilmentTypeService.getFulfilmentTypeByProductId(product.getProductId());
                productMessageService.produceMessage(customer, fulfilmentType.getName());
            }

            cartService.deleteCart(cart);

            log.info("Created pending order: {}", order);
            return ResponseEntity.accepted().body(order);
        } catch (ApiException e) {
            log.warn("Failed to create pending order: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/fulfilmentChecksTest")
    public ResponseEntity<String> fulfilmentChecksTest(@RequestBody FulfilmentTestRequest fulfilmentTestRequest) {
        try {
            CustomerDto customer = customerApi.getCustomerById(fulfilmentTestRequest.getCustomerId());
            productMessageService.produceMessage(customerMapper.toDomain(customer), fulfilmentTestRequest.getFulfilmentType());

            log.info("Sent fulfilment request type {} for customer: {}", fulfilmentTestRequest.getFulfilmentType(), customer);
            return ResponseEntity.ok("Sent Message");
        } catch (ApiException e) {
            log.warn("Failed to send fulfilment request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
