package za.co.entelect.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.ChecksHistoryEntity;
import za.co.entelect.pojo.enums.CheckType;

@Repository
public interface ChecksHistoryRepository extends JpaRepository<ChecksHistoryEntity, Long> {

    ChecksHistoryEntity findByCustomerIdAndCheckType(Long customer_id, CheckType checkType);

}
