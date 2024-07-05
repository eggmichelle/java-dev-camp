package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.QualifyingCustomerTypesEntity;

import java.util.List;

@Repository
public interface QualifyingCustomerTypesRepository extends JpaRepository<QualifyingCustomerTypesEntity, Long> {
    List<QualifyingCustomerTypesEntity> findAllByProductId(Long id);
}