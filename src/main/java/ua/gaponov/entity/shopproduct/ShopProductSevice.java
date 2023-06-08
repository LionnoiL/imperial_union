package ua.gaponov.entity.shopproduct;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.DatabaseRequest;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ShopProductSevice {

    public static void addShopProduct(String id, String code, int shopId) throws SQLException {
        List<DatabaseRequest> requestList = new ArrayList<>();

        StatementParameters<Object> parameters = StatementParameters.build(id, code, shopId);
        String sql = """
                INSERT INTO shop_products (product_id, product_code, shop_id) VALUES (?, ?, ?)
                """;
        requestList.add(new DatabaseRequest(sql, parameters));
        try {
            new SqlHelper<>().execSql(requestList);
        } catch (Exception e) {
            log.error(code);
        }

    }

    public static void delete(String productId){
        StatementParameters<String> parameters = StatementParameters.build(productId);
        String sql = """
                delete from shop_products where product_id = ?
                """;
        try {
            new SqlHelper<>().execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveShopProducts(String mainProductId, String simProductId) {
        Product simProduct = ProductService.getProductById(simProductId);
        ProductService.fillShopProducts(simProduct);
        List<ShopProduct> shopProducts = simProduct.getShopProducts();
        for (ShopProduct shopProduct : shopProducts) {
            try {
                addShopProduct(mainProductId, shopProduct.getCode(), shopProduct.getShopId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        delete(simProductId);
    }
}
