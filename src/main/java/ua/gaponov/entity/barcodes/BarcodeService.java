package ua.gaponov.entity.barcodes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.database.StringDatabaseMapper;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BarcodeService {

    public static void addBarcode(String productId, String barcode) throws SQLException {
        StatementParameters<Object> parameters = StatementParameters.build(
                productId,
                barcode
        );
        String sql = """
                INSERT INTO barcodes (product_id, barcode) VALUES (?, ?)
                """;
        new SqlHelper<>().execSql(sql, parameters);
    }

    public static void addBarcode(String productId, List<String> barcodes) {
        try {
            for (String barcode : barcodes) {
                addBarcode(productId, barcode);
            }
        } catch (Exception e) {
            //NOP
        }
    }

    public static List<String> getProductBarcodes(String productId) {
        StatementParameters<String> parameters = StatementParameters.build(productId);
        return new SqlHelper<String>().getAll("select barcode from barcodes where product_id = ?",
                parameters,
                new StringDatabaseMapper());
    }

    public static void delete(String productId) {
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
        addBarcode(mainProductId, getProductBarcodes(simProductId));
        delete(simProductId);
    }
}
