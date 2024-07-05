package za.co.entelect.pojo.pc;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.entelect.pojo.cis.AccountType;
import za.co.entelect.pojo.cis.CustomerType;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class DetailedProduct {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    @JsonIgnore
    private List<Long> qualifyingCustomerTypeIds;
    @JsonIgnore
    private List<Long> qualifyingAccountTypeIds;
    private List<CustomerType> eligibleCustomerTypes;
    private List<AccountType> eligibleAccountTypes;

    public DetailedProduct(Product product) {
        this(product.getProductId(), product.getName(), product.getDescription(), product.getPrice(), product.getImageUrl(), product.getQualifyingCustomerTypeIds(), product.getQualifyingAccountTypeIds());
    }

    public DetailedProduct(Long productId, String name, String description, BigDecimal price, String imageUrl, List<Long> qualifyingCustomerTypeIds, List<Long> qualifyingAccountTypeIds) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.qualifyingCustomerTypeIds = qualifyingCustomerTypeIds;
        this.qualifyingAccountTypeIds = qualifyingAccountTypeIds;
    }
}