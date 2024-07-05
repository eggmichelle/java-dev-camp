package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.*;
import za.co.entelect.entities.IdentifiableEntity;

import java.time.LocalDate;

@Entity
@Table(name = "orders", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "order_id"))
@Getter
@Setter
@ToString
public class OrdersEntity extends IdentifiableEntity {

    @Column(name = "createdat", nullable = false)
    private LocalDate createdAt;

    private String status;

    @Column(name = "contract_url")
    private String contractUrl;

    private Long customerId;

}