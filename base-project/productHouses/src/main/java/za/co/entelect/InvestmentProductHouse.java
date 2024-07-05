package za.co.entelect;

import org.springframework.stereotype.Component;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.Product;

@Component
public class InvestmentProductHouse {
    public boolean createPendingAccount(Product product, Customer customer) {
        return switch (product.getName()) {
            case "Short-Term Investment Product", "Long-Term Investment Product", "Islamic Investment Product", "VIP Investment Product" ->
                    true;
            default -> false;
        };
    }

    public void openAccount(Product product, Customer customer) {
    }

    public void closeAccount(Product product, Customer customer) {
    }

}
