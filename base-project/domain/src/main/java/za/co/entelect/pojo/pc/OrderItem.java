package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class OrderItem {

    private Long orderItemsId;
    private Product product;
    private String description;
    private CheckStatus checkStatus;

}
