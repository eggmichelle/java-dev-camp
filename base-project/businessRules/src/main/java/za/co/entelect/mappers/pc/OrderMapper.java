package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.OrderItemsEntity;
import za.co.entelect.entities.pc.OrdersEntity;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.repository.pc.OrderItemsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class OrderMapper {

    @Autowired
    private final OrderItemsRepository orderItemsRepository;

    @Autowired
    private final OrderItemMapper orderItemMapper;

    public Order toDomain(OrdersEntity ordersEntity) {
        List<OrderItemsEntity> orderItemsEntities = orderItemsRepository.findAllByOrderId(ordersEntity.getId());

        List<OrderItem> orderItems = orderItemsEntities.stream()
                .map(orderItemMapper::toDomain)
                .collect(Collectors.toList());

        return new Order(
                ordersEntity.getId(),
                ordersEntity.getCreatedAt(),
                ordersEntity.getStatus(),
                ordersEntity.getContractUrl(),
                ordersEntity.getCustomerId(),
                orderItems
        );
    }

    public OrdersEntity toEntity(Order orders) {
        OrdersEntity ordersEntity = new OrdersEntity();

        ordersEntity.setId(orders.getOrderId());
        ordersEntity.setCreatedAt(orders.getCreateAt());
        ordersEntity.setStatus(orders.getStatus());
        ordersEntity.setContractUrl(orders.getContractUrl());
        ordersEntity.setCustomerId(orders.getCustomerId());

        return ordersEntity;
    }

}