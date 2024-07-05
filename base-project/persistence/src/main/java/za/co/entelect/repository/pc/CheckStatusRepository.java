package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.CheckStatusEntity;

@Repository
public interface CheckStatusRepository extends JpaRepository<CheckStatusEntity, Long> {
    CheckStatusEntity findByOrderItemId(Long id);
}
