package za.co.entelect.entities.pc;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.entities.IdentifiableEntity;
import za.co.entelect.pojo.enums.Status;

@Entity
@Table(name = "checks_status", schema = "pc")
@AttributeOverride(name = "id", column = @Column(name = "check_status_id"))
@Getter
@Setter
@ToString
public class CheckStatusEntity extends IdentifiableEntity {

    @Column(name = "order_items_id", nullable = false)
    private Long orderItemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;



}
