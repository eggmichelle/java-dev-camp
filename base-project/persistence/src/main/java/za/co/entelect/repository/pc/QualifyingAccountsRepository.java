package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.QualifyingAccountsEntity;

import java.util.List;

@Repository
public interface QualifyingAccountsRepository extends JpaRepository<QualifyingAccountsEntity, Long> {
    List<QualifyingAccountsEntity> findAllByProductId(Long id);
}