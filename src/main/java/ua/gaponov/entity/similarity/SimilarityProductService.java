package ua.gaponov.entity.similarity;

import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.database.StringDatabaseMapper;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductDatabaseMapper;
import ua.gaponov.entity.product.ProductService;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author Andriy Gaponov
 */
public class SimilarityProductService {

    private static final ProductDatabaseMapper PRODUCT_DATABASE_MAPPER = new ProductDatabaseMapper();
    private static final SqlHelper<Product> PRODUCT_SQL_HELPER = new SqlHelper<>();

    public static SimilarityProduct getFirst() {

        SimilarityProduct similarityProduct = new SimilarityProduct();
        List<Product> products = PRODUCT_SQL_HELPER.getAll(
                """
                        SELECT p.* FROM similarity_products s
                        LEFT JOIN products p ON p.id = s.product_id_1
                        GROUP BY s.product_id_1
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
                Product productById = ProductService.getProductById(simProductId);
                if (nonNull(productById)) {
                    similarityProduct.getSimilarityProducts().add(productById);
                }
            }
        }

        return similarityProduct;
    }

    public static int getCount(){
        return PRODUCT_SQL_HELPER.getCount(
                "SELECT count(product_id_1) cnt FROM similarity_products");
    }
}
