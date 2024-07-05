package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.OrderItemsEntity;
import za.co.entelect.mappers.pc.OrderItemMapper;
import za.co.entelect.mappers.pc.OrderMapper;
import za.co.entelect.pojo.pc.Order;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.repository.pc.OrderItemsRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class OrderItemService {

    @Autowired
    private final OrderItemsRepository orderItemsRepository;

    @Autowired
    private final OrderItemMapper orderItemMapper;

    @Autowired
    private final OrderMapper orderMapper;

    @Autowired
    private final CheckStatusService checkStatusService;

    public OrderItem createOrderItem(Product product, Order order) {
        OrderItem orderItem = new OrderItem(
                null,
                product,
                "",
                null
        );

        OrderItemsEntity savedOrderItem = orderItemsRepository.save(orderItemMapper.toEntity(orderItem, orderMapper.toEntity(order)));

        return orderItemMapper.toDomain(savedOrderItem);
    }

    public OrderItem getOrderItem(Long orderItemsId) {
        Optional<OrderItemsEntity> orderItemsEntity = orderItemsRepository.findById(orderItemsId);
        return orderItemsEntity.map(orderItemMapper::toDomain).orElse(null);
    }

    public List<OrderItem> createOrderItems(List<Product> products, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (Product product : products) {
            OrderItem orderItem = createOrderItem(product, order);
            checkStatusService.createPendingCheckStatus(orderItem);
            orderItems.add(getOrderItem(orderItem.getOrderItemsId()));
        }
        return orderItems;
    }

}
