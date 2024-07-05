package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.ProductEntity;
import za.co.entelect.entities.pc.QualifyingAccountsEntity;
import za.co.entelect.entities.pc.QualifyingCustomerTypesEntity;
import za.co.entelect.pojo.pc.Product;
import za.co.entelect.repository.pc.QualifyingAccountsRepository;
import za.co.entelect.repository.pc.QualifyingCustomerTypesRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class ProductMapper {

    @Autowired
    private final QualifyingCustomerTypesRepository qualifyingCustomerTypesRepository;

    @Autowired
    private final QualifyingAccountsRepository qualifyingAccountsRepository;

    public Product toDomain(ProductEntity productEntity) {
        List<QualifyingCustomerTypesEntity> qualifyingCustomerTypesEntities = qualifyingCustomerTypesRepository.findAllByProductId(productEntity.getId());
        List<Long> qualifyingCustomerTypeIds = qualifyingCustomerTypesEntities.stream().map(QualifyingCustomerTypesEntity::getCustomerTypesId).toList();

        List<QualifyingAccountsEntity> qualifyingAccountsEntities = qualifyingAccountsRepository.findAllByProductId(productEntity.getId());
        List<Long> qualifyingAccountTypeIds = qualifyingAccountsEntities.stream().map(QualifyingAccountsEntity::getAccountTypeId).toList();

        return new Product(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getImageUrl(),
                qualifyingCustomerTypeIds,
                qualifyingAccountTypeIds);
    }

    public ProductEntity toEntity(Product product) {
        ProductEntity productEntity = new ProductEntity();

        productEntity.setId(product.getProductId());
        productEntity.setName(product.getName());
        productEntity.setDescription(product.getDescription());
        productEntity.setPrice(product.getPrice());
        productEntity.setImageUrl(product.getImageUrl());

        return productEntity;
    }

}