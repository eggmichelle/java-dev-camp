package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
public class Order {

    private Long orderId;
    private LocalDate createAt;
    private String status;
    private String contractUrl;
    private Long customerId;
    private List<OrderItem> orderItemList;

    public Order() {
    }
}