package ua.gaponov.entity.shopproduct;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.DatabaseRequest;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.entity.barcodes.BarcodeService;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.similarity.SimilarityProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ShopProductService {

    private static final SqlHelper<Object> SQL_HELPER = new SqlHelper<>();

    public static void add(String id, String code, int shopId) throws SQLException {
        List<DatabaseRequest> requestList = new ArrayList<>();

        StatementParameters<Object> parameters = StatementParameters.build(id, code, shopId);
        String sql = """
                INSERT INTO shop_products (product_id, product_code, shop_id) VALUES (?, ?, ?)
                """;
        requestList.add(new DatabaseRequest(sql, parameters));
        try {
            SQL_HELPER.execSql(requestList);
        } catch (Exception e) {
            log.error(code);
        }
    }

    public static void addShopProduct(String id, Product1C product) throws SQLException {
        if (!checkShopProduct(product)) {
            List<DatabaseRequest> requestList = new ArrayList<>();

            StatementParameters<Object> parameters = StatementParameters.build(
                    id,
                    product.getCode(),
                    product.getShopId()
            );
            String sql = """
                    INSERT INTO shop_products (product_id, product_code, shop_id) VALUES (?, ?, ?)
                    """;
            requestList.add(new DatabaseRequest(sql, parameters));
            try {
                SQL_HELPER.execSql(requestList);
            } catch (Exception e) {
                log.error(product.toString());
            }
        }
    }

    public static void delete(String productId) {
        StatementParameters<String> parameters = StatementParameters.build(productId);
        String sql = """
                delete from shop_products where product_id = ?
                """;
        try {
            SQL_HELPER.execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveShopProducts(String mainProductId, String simProductId) {
        Product simProduct = ProductService.getById(simProductId);
        ProductService.fillShopProducts(simProduct);
        List<ShopProduct> shopProducts = simProduct.getShopProducts();
        for (ShopProduct shopProduct : shopProducts) {
            try {
                add(mainProductId, shopProduct.getCode(), shopProduct.getShopId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        delete(simProductId);
    }

    public static boolean checkShopProduct(Product1C product) {
        int shopProductFinded = SQL_HELPER.getCount(
                "select count(product_code) from shop_products where product_code = '"
                + product.getCode()
                + "' and shop_id = " + product.getShopId());
        return shopProductFinded > 0;
    }
}
