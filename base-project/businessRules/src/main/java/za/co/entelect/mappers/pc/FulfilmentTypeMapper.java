package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.FulfilmentTypeEntity;
import za.co.entelect.pojo.pc.FulfilmentType;

@Component
@AllArgsConstructor
public class FulfilmentTypeMapper {

    @Autowired
    ProductMapper productMapper;

    public FulfilmentType toDomain(FulfilmentTypeEntity fulfilmentTypeEntity) {
        return new FulfilmentType(
                fulfilmentTypeEntity.getId(),
                fulfilmentTypeEntity.getName(),
                fulfilmentTypeEntity.getDescription(),
                productMapper.toDomain(fulfilmentTypeEntity.getProduct())
        );
    }

    public FulfilmentTypeEntity toEntity(FulfilmentType fulfilmentType) {
        FulfilmentTypeEntity fulfilmentTypeEntity = new FulfilmentTypeEntity();

        fulfilmentTypeEntity.setId(fulfilmentType.getFulfilmentTypeId());
        fulfilmentTypeEntity.setName(fulfilmentType.getName());
        fulfilmentTypeEntity.setDescription(fulfilmentType.getDescription());
        fulfilmentTypeEntity.setProduct(productMapper.toEntity(fulfilmentType.getProduct()));

        return fulfilmentTypeEntity;
    }

}
