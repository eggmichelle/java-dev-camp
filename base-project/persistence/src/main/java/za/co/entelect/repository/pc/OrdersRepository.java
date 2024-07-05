package za.co.entelect.repository.pc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.co.entelect.entities.pc.OrdersEntity;

import java.util.List;

@Repository
public interface OrdersRepository extends JpaRepository<OrdersEntity, Long> {
    List<OrdersEntity> findByCustomerId(Long customerId);

    List<OrdersEntity> findByCustomerIdAndStatus(Long customerId, String status);
}