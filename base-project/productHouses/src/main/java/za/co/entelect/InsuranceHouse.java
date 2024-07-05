package za.co.entelect;

import org.springframework.stereotype.Component;
import za.co.entelect.pojo.cis.Customer;
import za.co.entelect.pojo.pc.Product;

@Component
public class InsuranceHouse {

    public boolean createPendingAccount(Product product, Customer customer) {
        return switch (product.getName()) {
            case "Retail Short Term Insurance", "Retail Long-Term Insurance", "Commercial Short Term Insurance", "Commercial Long-Term Insurance" ->
                    true;
            default -> false;
        };
    }

    public void openAccount(Product product, Customer customer) {
    }

    public void closeAccount(Product product, Customer customer) {
    }

}
