package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {
    CartEntity findByCustomerId(Long customerId);
}
