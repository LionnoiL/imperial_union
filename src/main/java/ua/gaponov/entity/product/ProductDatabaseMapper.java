package ua.gaponov.entity.product;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Mapper;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.database.StringDatabaseMapper;
import ua.gaponov.entity.shopproduct.ShopProduct;
import ua.gaponov.entity.shopproduct.ShopProductDatabaseMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class ProductDatabaseMapper implements Mapper<Product> {

    @Override
    public Product map(ResultSet rs) {
        try {
            Product product = new Product();
            product.setId(rs.getString("id"));
            product.setName(rs.getString("product_name"));
            product.setWeight(rs.getBoolean("weight"));

            StatementParameters<String> productIdParameter = StatementParameters.build(product.getId());

            product.setBarcodes(new SqlHelper<String>().getAll(
                    "SELECT barcode FROM barcodes WHERE product_id = ?",
                    productIdParameter,
                    new StringDatabaseMapper()
            ));

            product.setShopProducts(new SqlHelper<ShopProduct>().getAll(
                    "SELECT * FROM shop_products WHERE product_id = ?",
                    productIdParameter,
                    new ShopProductDatabaseMapper()
            ));

            return product;
        } catch (SQLException ex) {
            log.error("Error map product", ex);
        }
        return null;
    }
}
