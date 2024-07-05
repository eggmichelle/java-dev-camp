package za.co.entelect.mappers.pc;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import za.co.entelect.entities.pc.CartEntity;
import za.co.entelect.entities.pc.CartItemEntity;
import za.co.entelect.pojo.pc.Cart;
import za.co.entelect.pojo.pc.CartItem;
import za.co.entelect.repository.pc.CartItemRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class CartMapper {

    @Autowired
    private final CartItemRepository cartItemRepository;

    @Autowired
    private final CartItemMapper cartItemMapper;

    public Cart toDomain(CartEntity entity) {
        List<CartItemEntity> cartItemEntities = cartItemRepository.findAllByCartId(entity.getId());

        List<CartItem> cartItems = cartItemEntities.stream()
                .map(cartItemMapper::toDomain)
                .toList();

        return new Cart(
                entity.getId(),
                entity.getCustomerId(),
                cartItems
        );
    }

    public CartEntity toEntity(Cart cart) {
        CartEntity entity = new CartEntity();

        entity.setId(cart.getCartId());
        entity.setCustomerId(cart.getCustomerId());

        return entity;
    }

}
