package ua.gaponov.entity.barcodes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.database.StringDatabaseMapper;
import ua.gaponov.entity.product1c.Product1C;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BarcodeService {

    private static final SqlHelper<Object> SQL_HELPER = new SqlHelper<>();

    public static void addBarcode(String productId, String barcode) throws SQLException {
        StatementParameters<Object> parameters = StatementParameters.build(
                productId,
                barcode
        );
        String sql = """
                INSERT INTO barcodes (product_id, barcode) VALUES (?, ?)
                """;
        SQL_HELPER.execSql(sql, parameters);
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
            SQL_HELPER.execSql(sql, parameters);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void moveBarcodes(String mainProductId, String simProductId) {
        addBarcode(mainProductId, getProductBarcodes(simProductId));
        delete(simProductId);
    }

    public static String getProductIdByBarcode(String barcode) {
        StatementParameters<String> parameters = StatementParameters.build(barcode);
        return new SqlHelper<String>().getOne("select product_id from barcodes where barcode = ?",
                parameters,
                new StringDatabaseMapper());
    }
}
