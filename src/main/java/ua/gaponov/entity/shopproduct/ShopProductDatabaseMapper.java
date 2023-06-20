package ua.gaponov.entity.shopproduct;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Mapper;
import ua.gaponov.entity.product.ProductService;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class ShopProductDatabaseMapper implements Mapper<ShopProduct> {

    @Override
    public ShopProduct map(ResultSet rs) {
        try {
            ShopProduct product = new ShopProduct();
            product.setShopId(rs.getInt("shop_id"));
            product.setCode(rs.getString("product_code"));
            product.setProduct(ProductService.getById(rs.getString("product_id")));

            return product;
        } catch (SQLException ex) {
            log.error("Error map shop product", ex);
        }
        return null;
    }
}
