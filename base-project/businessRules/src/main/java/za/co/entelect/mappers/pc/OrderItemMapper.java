package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.CheckStatusEntity;
import za.co.entelect.entities.pc.OrderItemsEntity;
import za.co.entelect.entities.pc.OrdersEntity;
import za.co.entelect.pojo.pc.CheckStatus;
import za.co.entelect.pojo.pc.OrderItem;
import za.co.entelect.repository.pc.CheckStatusRepository;

@Component
@AllArgsConstructor
public class OrderItemMapper {

    @Autowired
    private final ProductMapper productMapper;

    @Autowired
    private final CheckStatusMapper checkStatusMapper;

    @Autowired
    private final CheckStatusRepository checkStatusRepository;

    public OrderItem toDomain(OrderItemsEntity orderItemsEntity) {
        CheckStatusEntity checkStatusEntity = checkStatusRepository.findByOrderItemId(orderItemsEntity.getId());

        CheckStatus checkStatus = null;
        if (checkStatusEntity != null) {
            checkStatus = checkStatusMapper.toDomain(checkStatusEntity);
        }

        return new OrderItem(
                orderItemsEntity.getId(),
                productMapper.toDomain(orderItemsEntity.getProduct()),
                orderItemsEntity.getDescription(),
                checkStatus
        );
    }

    public OrderItemsEntity toEntity(OrderItem orderItem, OrdersEntity order) {
        OrderItemsEntity orderItemsEntity = new OrderItemsEntity();

        orderItemsEntity.setId(orderItem.getOrderItemsId());
        orderItemsEntity.setProduct(productMapper.toEntity(orderItem.getProduct()));
        orderItemsEntity.setDescription(orderItem.getDescription());
        orderItemsEntity.setOrder(order);

        return orderItemsEntity;
    }

}
