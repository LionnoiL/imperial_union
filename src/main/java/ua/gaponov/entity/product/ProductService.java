package ua.gaponov.entity.product;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.database.DatabaseRequest;
import ua.gaponov.database.SqlHelper;
import ua.gaponov.database.StatementParameters;
import ua.gaponov.entity.barcodes.Barcode;
import ua.gaponov.entity.barcodes.BarcodeDatabaseMapper;
import ua.gaponov.entity.product1c.Product1C;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ProductService {

    private static final ProductDatabaseMapper MAPPER = new ProductDatabaseMapper();
    private static final SqlHelper<Product> PRODUCT_SQL_HELPER = new SqlHelper<>();
    private static final SqlHelper<Barcode> BARCODE_SQL_HELPER = new SqlHelper<>();



    public static Product getProductById(String id){
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

    public static Product getProductByBarcode(String id){
        if (id == null || id.isEmpty()) {
            return null;
        }
        StatementParameters<String> parameters = StatementParameters.build(id);
        String sql = """
                SELECT * FROM barcodes
                LEFT JOIN products ON barcodes.product_id = products.id
                WHERE barcodes.barcode = ?
                """;
        return PRODUCT_SQL_HELPER.getOne(sql, parameters, MAPPER);
    }

    public static void save(Product1C product) throws SQLException {
       List<DatabaseRequest> requestList = new ArrayList<>();
       String newProductId = UUID.randomUUID().toString();

        //PRODUCTS
        StatementParameters<Object> parameters = StatementParameters.build(
                newProductId,
                product.getName(),
                product.isWeight()
        );
        String sql = """
                INSERT INTO products (id, product_name, weight) VALUES (?, ?, ?)
                """;
        requestList.add(new DatabaseRequest(sql, parameters));

        //SHOP_PRODUCTS
        parameters = StatementParameters.build(
                newProductId,
                product.getCode(),
                product.getShopId()
        );
        sql = """
                INSERT INTO shop_products (product_id, product_code, shop_id) VALUES (?, ?, ?)
                """;
        requestList.add(new DatabaseRequest(sql, parameters));

        //BARCODES
        if (Objects.nonNull(product.getBarcode()) && !product.getBarcode().isEmpty()){
            parameters = StatementParameters.build(
                    newProductId,
                    product.getBarcode()
            );
            sql = """
                INSERT INTO barcodes (product_id, barcode) VALUES (?, ?)
                """;
            requestList.add(new DatabaseRequest(sql, parameters));
        }


        PRODUCT_SQL_HELPER.execSql(requestList);
    }

    public static List<Barcode> getProductBarcodes(Product product) {
            StatementParameters<String> parameters = StatementParameters.build(product.getId());
            return BARCODE_SQL_HELPER.getAll("select * from barcodes where product_id = ?",
                    parameters,
                    new BarcodeDatabaseMapper());
    }
}
