package ua.gaponov.entity.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.Database;
import ua.gaponov.database.DatabaseRequest;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.entity.barcodes.BarcodeService;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.shopproduct.ShopProduct;
import ua.gaponov.entity.shopproduct.ShopProductDatabaseMapper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static java.util.Objects.isNull;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ProductService {

    private static final ProductDatabaseMapper MAPPER = new ProductDatabaseMapper();
    private static final SqlHelper<Product> PRODUCT_SQL_HELPER = new SqlHelper<>();

    public static List<Product> getAll() {
        return PRODUCT_SQL_HELPER.getAll(
                "SELECT * FROM products",
                MAPPER
        );
    }

    public static List<Product> getAllNonComplete() {
        return PRODUCT_SQL_HELPER.getAll(
                "SELECT * FROM products where complete_shop = false and complete_name = false",
                MAPPER
        );
    }

    public static Product getById(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        StatementParameters<String> parameters = StatementParameters.build(id);
        String sql = """
                SELECT * FROM products
                WHERE products.id = ?
                """;
        return PRODUCT_SQL_HELPER.getOne(sql, parameters, MAPPER);
    }

    public static Product getByBarcode(String barcode) {
        if (barcode == null || barcode.isEmpty()) {
            return null;
        }
        StatementParameters<String> parameters = StatementParameters.build(barcode);
        String sql = """
                SELECT * FROM barcodes
                LEFT JOIN products ON barcodes.product_id = products.id
                WHERE barcodes.barcode = ?
                """;
        return PRODUCT_SQL_HELPER.getOne(sql, parameters, MAPPER);
    }

    public static Product getByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        StatementParameters<String> parameters = StatementParameters.build(name);
        String sql = """
                SELECT * FROM products
                WHERE products.product_name = ?
                """;
        return PRODUCT_SQL_HELPER.getOne(sql, parameters, MAPPER);
    }

    public static void add(Product1C product) throws SQLException {
        List<DatabaseRequest> requestList = new ArrayList<>();
        String newProductId = UUID.randomUUID().toString();

        StatementParameters<Object> parameters;
        String sql;

        //PRODUCTS
        Product findProductByName = getByName(product.getName());
        if (isNull(findProductByName)) {
            parameters = StatementParameters.build(
                    newProductId,
                    product.getName(),
                    product.isWeight()
            );
            sql = """
                    INSERT INTO products (id, product_name, weight) VALUES (?, ?, ?)
                    """;
            requestList.add(new DatabaseRequest(sql, parameters));
        } else {
            newProductId = findProductByName.getId();
        }

        //SHOP_PRODUCTS
        int shopProductFinded = PRODUCT_SQL_HELPER.getCount("select count(product_code) from shop_products where product_code = '"
                + product.getCode()
                + "' and shop_id = " + product.getShopId());
        if (shopProductFinded == 0) {
            parameters = StatementParameters.build(
                    newProductId,
                    product.getCode(),
                    product.getShopId()
            );
            sql = """
                    INSERT INTO shop_products (product_id, product_code, shop_id) VALUES (?, ?, ?)
                    """;
            requestList.add(new DatabaseRequest(sql, parameters));
        }

        //BARCODES
        if (Objects.nonNull(product.getBarcode()) && !product.getBarcode().isEmpty()) {
            parameters = StatementParameters.build(
                    newProductId,
                    product.getBarcode()
            );
            sql = """
                    INSERT INTO barcodes (product_id, barcode) VALUES (?, ?)
                    """;
            requestList.add(new DatabaseRequest(sql, parameters));
        }

        try {
            PRODUCT_SQL_HELPER.execSql(requestList);
        } catch (Exception e) {
            log.error(product.toString());
        }
    }

    public static void checkCompleteProduct() {
        String sql = """
                update products set products.complete_shop = IF(products.id IN (
                SELECT a.id FROM (SELECT products.id, COUNT(shop_products.product_id) cnt FROM products
                LEFT JOIN shop_products ON shop_products.product_id = products.id
                GROUP BY products.id) a
                WHERE a.cnt = 3
                ), true, false);
                """;
        Database.execSql(sql);
    }

    public static void delete(String productId) {
        StatementParameters<String> parameters = StatementParameters.build(productId);
        String sql = """
                delete from products where id = ?
                """;
        try {
            PRODUCT_SQL_HELPER.execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void completeNameProduct(String productId) {
        int count = PRODUCT_SQL_HELPER.getCount(
                "SELECT COUNT(s.product_id_2) cnt FROM similarity_products s WHERE s.product_id_1 = '" +
                        productId + "'"
        );
        if (count == 0) {
            StatementParameters<String> parameters = StatementParameters.build(productId);
            String sql = """
                    update products set products.complete_name = true where products.id = ?;
                    """;
            try {
                PRODUCT_SQL_HELPER.execSql(sql, parameters);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void fillBarcodes(Product product) {
        product.setBarcodes(BarcodeService.getProductBarcodes(product.getId()));
    }

    public static void fillShopProducts(Product product) {
        StatementParameters<String> productIdParameter = StatementParameters.build(product.getId());
        product.setShopProducts(new SqlHelper<ShopProduct>().getAll(
                "SELECT * FROM shop_products WHERE product_id = ?",
                productIdParameter,
                new ShopProductDatabaseMapper()
        ));
    }
}
