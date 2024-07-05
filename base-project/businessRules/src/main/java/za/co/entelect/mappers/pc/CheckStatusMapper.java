package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.CheckStatusEntity;
import za.co.entelect.pojo.pc.CheckStatus;
import za.co.entelect.pojo.pc.OrderItem;

@Component
@AllArgsConstructor
public class CheckStatusMapper {

    public CheckStatus toDomain(CheckStatusEntity entity) {

        return new CheckStatus(
                entity.getId(),
                entity.getStatus(),
                entity.getDescription()
        );
    }

    public CheckStatusEntity toEntity(CheckStatus checkStatus, OrderItem orderItem) {
        CheckStatusEntity entity = new CheckStatusEntity();

        entity.setId(checkStatus.getCheckStatusId());
        entity.setDescription(checkStatus.getDescription());
        entity.setStatus(checkStatus.getStatus());
        entity.setOrderItemId(orderItem.getOrderItemsId());

        return entity;
    }

}
