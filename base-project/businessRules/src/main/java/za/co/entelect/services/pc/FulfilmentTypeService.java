package za.co.entelect.services.pc;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.FulfilmentTypeEntity;
import za.co.entelect.mappers.pc.FulfilmentTypeMapper;
import za.co.entelect.pojo.pc.FulfilmentType;
import za.co.entelect.repository.pc.FulfilmentTypeRepository;

@Service
@Slf4j
@AllArgsConstructor
public class FulfilmentTypeService {

    @Autowired
    private final FulfilmentTypeRepository fulfilmentTypeRepository;

    @Autowired
    private final FulfilmentTypeMapper fulfilmentTypeMapper;

    public FulfilmentType getFulfilmentTypeByProductId(Long productId) {
        FulfilmentTypeEntity fulfilmentTypeEntity = fulfilmentTypeRepository.findByProductId(productId);
        return fulfilmentTypeMapper.toDomain(fulfilmentTypeEntity);
    }

}
