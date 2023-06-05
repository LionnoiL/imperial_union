package ua.gaponov.entity.barcodes;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;

import java.sql.SQLException;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class BarcodeService {

    private static final BarcodeDatabaseMapper MAPPER = new BarcodeDatabaseMapper();
    private static final SqlHelper<Barcode> SQL_HELPER = new SqlHelper<>();

    public static Barcode getByBarcode(String barcode) {
        StatementParameters<String> parameters = StatementParameters.build(barcode);
        return new SqlHelper<Barcode>().getOne("select * from barcodes where barcode = ?",
                parameters,
                MAPPER);
    }

    public static void save(Barcode barcode){
        Barcode barcodeFound = getByBarcode(barcode.getValue());
        if (barcodeFound == null) {
            insert(barcode);
        } else {
            update(barcode);
        }
    }

    private static void insert(Barcode barcode) {
        if (barcode.getProduct() != null && !barcode.getValue().isEmpty()) {
            StatementParameters<String> parameters = StatementParameters.build(
                    barcode.getValue(),
                    barcode.getProduct().getId()
            );
            String sql = """
                    insert into barcodes (barcode, product_id)
                    values
                    (?, ?)
                    """;
            try {
                SQL_HELPER.execSql(sql, parameters);
            } catch (SQLException ex) {
                log.error("Insert barcode {} failed. {}", barcode, ex);
            }
        }
    }

    private static void update(Barcode barcode) {
        if (barcode.getProduct() != null && !barcode.getValue().isEmpty()) {
            StatementParameters<String> parameters = StatementParameters.build(
                    barcode.getProduct().getId(),
                    barcode.getValue()
            );
            String sql = """
                    update barcodes set product_id= ?
                    where barcode = ?
                    """;
            try {
                SQL_HELPER.execSql(sql, parameters);
            } catch (SQLException ex) {
                log.error("Update barcode {} failed. {}", barcode, ex);
            }
        }
    }
}
