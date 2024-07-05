package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.FulfilmentTypeEntity;

@Repository
public interface FulfilmentTypeRepository extends JpaRepository<FulfilmentTypeEntity, Long> {
    FulfilmentTypeEntity findByProductId(Long productId);
}