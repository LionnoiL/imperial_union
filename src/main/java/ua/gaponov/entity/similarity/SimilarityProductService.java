package ua.gaponov.entity.similarity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.*;
import ua.gaponov.entity.barcodes.BarcodeService;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductDatabaseMapper;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.shopproduct.ShopProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class SimilarityProductService {

    private static final ProductDatabaseMapper PRODUCT_DATABASE_MAPPER = new ProductDatabaseMapper();
    private static final SqlHelper<Product> PRODUCT_SQL_HELPER = new SqlHelper<>();

    public static void addSimilarityProducts(Product mainProduct, Product similarityProduct) throws SQLException {
        List<DatabaseRequest> requestList = new ArrayList<>();

        StatementParameters<Object> parameters = StatementParameters.build(
                mainProduct.getId(),
                similarityProduct.getId()
        );
        String sql = """
                INSERT INTO similarity_products (product_id_1, product_id_2) VALUES (?, ?)
                """;
        requestList.add(new DatabaseRequest(sql, parameters));
        try {
            PRODUCT_SQL_HELPER.execSql(requestList);
        } catch (Exception e) {
            log.error(mainProduct.toString());
        }
    }

    public static SimilarityProduct getFirst(List<String> skipProducts, boolean random) {
        String order = " p.product_name ";
        if (random) {
            order = " rand() ";
        }

        String whereString = "";
        if (skipProducts != null && skipProducts.size() > 0) {
            whereString = " where s.product_id_1 not in (" + String.join(",", skipProducts) + ")";
        }

        SimilarityProduct similarityProduct = new SimilarityProduct();
        List<Product> products = PRODUCT_SQL_HELPER.getAll(
                """
                        SELECT p.* FROM similarity_products s
                        LEFT JOIN products p ON p.id = s.product_id_1
                        """ +
                        whereString +
                        """
                                GROUP BY s.product_id_1
                                ORDER BY""" +
                        order +
                        """
                                LIMIT 1
                                """,
                PRODUCT_DATABASE_MAPPER
        );
        for (Product product : products) {
            similarityProduct.setMainProduct(product);

            StatementParameters<String> productIdParameter = StatementParameters.build(product.getId());
            List<String> similarityProductList = new SqlHelper<String>().getAll(
                    "SELECT product_id_2 FROM similarity_products WHERE product_id_1 = ?",
                    productIdParameter,
                    new StringDatabaseMapper()
            );
            for (String simProductId : similarityProductList) {
                Product productById = ProductService.getById(simProductId);
                if (nonNull(productById)) {
                    similarityProduct.getSimilarityProducts().add(productById);
                }
            }
        }

        return similarityProduct;
    }

    public static int getCount() {
        return PRODUCT_SQL_HELPER.getCount(
                "SELECT count(product_id_1) cnt FROM similarity_products");
    }

    public static void deleteAllSimilarityProducts() {
        Database.execSql("delete from similarity_products");
    }

    public static void deleteSimilarityProductsByProductId(String productId) {
        StatementParameters<String> parameters = StatementParameters.build(productId);
        String sql = """
                delete from similarity_products where product_id_2 = ?
                """;
        try {
            PRODUCT_SQL_HELPER.execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        sql = """
                delete from similarity_products where product_id_1 = ?
                """;
        try {
            PRODUCT_SQL_HELPER.execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void processAnalog(String simProductId, String mainProductId) {
        ShopProductService.moveShopProducts(mainProductId, simProductId);
        BarcodeService.moveBarcodes(mainProductId, simProductId);

        SimilarityProductService.deleteSimilarityProductsByProductId(simProductId);
        ProductService.delete(simProductId);
    }
}
