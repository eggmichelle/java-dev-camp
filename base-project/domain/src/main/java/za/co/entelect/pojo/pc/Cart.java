package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Cart {

    private Long cartId;
    private Long customerId;
    private List<CartItem> cartItems;

    public Cart() {
    }
}
