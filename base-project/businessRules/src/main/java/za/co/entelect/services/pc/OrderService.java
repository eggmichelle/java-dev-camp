package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.OrdersEntity;
import za.co.entelect.mappers.pc.OrderMapper;
import za.co.entelect.pojo.enums.Status;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.repository.pc.OrdersRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class OrderService {

    @Autowired
    private final OrdersRepository ordersRepository;

    @Autowired
    private final OrderMapper orderMapper;

    public Order createPendingOrder(Long customerId) {
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = new Order(
                null,
                LocalDate.now(),
                "PENDING",
                null,
                customerId,
                orderItems
        );

        OrdersEntity savedOrder = ordersRepository.save(orderMapper.toEntity(order));

        return orderMapper.toDomain(savedOrder);
    }

    public List<Order> getOrdersForCustomer(Long customerId) {
        List<OrdersEntity> orders = ordersRepository.findByCustomerId(customerId);
        return orders.stream().map(orderMapper::toDomain).collect(Collectors.toList());
    }

    public Order findById(Long orderId) {
        Optional<OrdersEntity> order = ordersRepository.findById(orderId);
        return order.map(orderMapper::toDomain).orElse(null);

    }

    public List<Order> getPendingCustomerOrders(Long customerId) {
        List<OrdersEntity> orders = ordersRepository.findByCustomerIdAndStatus(customerId, Status.PENDING.toString());
        return orders.stream().map(orderMapper::toDomain).collect(Collectors.toList());
    }

    public void updateOrder(Order order) {
      Optional<OrdersEntity> orderEntity = ordersRepository.findById(order.getOrderId());

        if (orderEntity.isPresent()) {
            OrdersEntity existingOrderEntity = orderEntity.get();
            existingOrderEntity.setStatus("COMPLETED");

            ordersRepository.save(existingOrderEntity);
        } else {
            log.warn("Order with ID " + order.getOrderId() + " not found");
        }

    }

}
