package ua.gaponov.entity.shopproduct;

import lombok.Getter;
import lombok.Setter;
import ua.gaponov.entity.product.Product;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
public class ShopProduct {

    private String code;
    private int shopId;
    private Product product;
}
