package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.CartItemEntity;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.pc.CartItem;

@Component
@AllArgsConstructor
public class CartItemMapper {

    @Autowired
    ProductMapper productMapper;

    public CartItem toDomain(CartItemEntity entity) {
        return new CartItem(
                entity.getId(),
                productMapper.toDomain(entity.getProductEntity())
        );
    }

    public CartItemEntity toEntity(CartItem cartItem, Cart cart) {
        CartItemEntity entity = new CartItemEntity();

        entity.setId(cartItem.getCartItemId());
        entity.setCartId(cart.getCartId());
        entity.setProductEntity(productMapper.toEntity(cartItem.getProduct()));

        return entity;
    }

}