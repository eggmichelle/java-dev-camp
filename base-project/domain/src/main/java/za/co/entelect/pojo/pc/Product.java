package za.co.entelect.pojo.pc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class Product {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private List<Long> qualifyingCustomerTypeIds;
    private List<Long> qualifyingAccountTypeIds;

    public Product() {
    }
}