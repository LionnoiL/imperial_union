package ua.gaponov.entity.barcodes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.entity.product.ProductService;

import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BarcodeService {

    public static void delete(String productId){
        StatementParameters<String> parameters = StatementParameters.build(productId);
        String sql = """
                delete from barcodes where product_id = ?
                """;
        try {
            new SqlHelper<>().execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveBarcodes(String mainProductId, String simProductId) {
        ProductService.addBarcode(mainProductId, ProductService.getProductBarcodes(simProductId));
        delete(simProductId);
    }
}
