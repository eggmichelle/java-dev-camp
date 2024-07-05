package za.co.entelect.services.pc;

import jakarta.persistence.NoResultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import za.co.entelect.entities.pc.ProductEntity;
import za.co.entelect.mappers.pc.ProductMapper;
import za.co.entelect.pojo.cis.AccountType;
import za.co.entelect.pojo.cis.CustomerType;
import za.co.entelect.pojo.pc.DetailedProduct;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.pojo.responses.EligibilityResponse;
import za.co.entelect.repository.pc.ProductRepository;
import za.co.entelect.services.cis.CustomerService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class ProductService {

    @Autowired
    private final ProductRepository productRepository;

    @Autowired
    private final ProductMapper productMapper;

    @Autowired
    private final CustomerService customerService;

    public List<Product> getAllProducts() {

        List<ProductEntity> productEntities = productRepository.findAll();

        if (productEntities.isEmpty()) {
            throw new NoResultException();
        }

        List<Product> products = new ArrayList<>();

        for (ProductEntity productEntity : productEntities) {
            products.add(productMapper.toDomain(productEntity));
        }

        return products;
    }

    public List<Product> getPaginatedProducts(int page, int size) {
        Page<ProductEntity> allProducts = productRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "name")));

        List<ProductEntity> productEntities = allProducts.hasContent() ? allProducts.getContent().stream().sorted(Comparator.comparing(ProductEntity::getName)).toList() : Collections.emptyList();

        List<Product> products = new ArrayList<>();

        for (ProductEntity productEntity : productEntities) {
            products.add(productMapper.toDomain(productEntity));
        }

        return products;

    }

    public Product getProductById(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId).orElse(null);
        if (productEntity == null) {
            return null;
        }
        return productMapper.toDomain(productEntity);
    }

    public EligibilityResponse setEligibilityCriteria(DetailedProduct product) {
        List<CustomerType> eligibleCustomerTypes = customerService.getEligibleCustomerTypes(product.getQualifyingCustomerTypeIds());
        List<AccountType> eligibleAccountTypes = customerService.getEligibleAccountTypes(product.getQualifyingAccountTypeIds());
        if (eligibleCustomerTypes != null) {
            product.setEligibleCustomerTypes(eligibleCustomerTypes);
        }
        if (eligibleAccountTypes != null) {
            product.setEligibleAccountTypes(eligibleAccountTypes);
        }
        return this.generateEligibilityResponse(product);
    }

    private EligibilityResponse generateEligibilityResponse(DetailedProduct product) {
        EligibilityResponse response = new EligibilityResponse();
        for (AccountType accountType : product.getEligibleAccountTypes()) {
            response.getEligibleAccountTypes().add(accountType.getName());
        }
        for (CustomerType customerType : product.getEligibleCustomerTypes()) {
            response.getEligibleCustomerTypes().add((Character.toUpperCase(customerType.getName().charAt(0))) + (customerType.getName().substring(1).toLowerCase()));
        }
        return response;
    }

}