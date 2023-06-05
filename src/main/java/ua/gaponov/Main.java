package ua.gaponov;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.config.LoggingConfiguration;
import ua.gaponov.database.Database;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.product1c.Product1CService;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        new LoggingConfiguration();

        Database.execSql("select * from products");

        List<Product1C> productList1 = Product1CService.getProductsFromFile("imperial_1.csv", 1);
        List<Product1C> productList2 = Product1CService.getProductsFromFile("imperial_2.csv", 2);
        List<Product1C> productList3 = Product1CService.getProductsFromFile("imperial_3.csv", 3);

        checkBarcodes(productList3);
        checkBarcodes(productList2);
        checkBarcodes(productList1);
    }

    private static void checkBarcodes(List<Product1C> productList) {
        int listSize = productList.size();
        int packetSize = listSize / 100;
        int packet = 1;
        int currentIndex = 0;

        for (Product1C product1C : productList) {
            currentIndex += 1;
            if (currentIndex >= packetSize) {
                currentIndex = 0;
                packet += 1;
                log.debug("{}%", packet);
            }
            if (Objects.nonNull(product1C.getBarcode()) && !product1C.getBarcode().isEmpty()) {
                Product productByBarcode = ProductService.getProductByBarcode(product1C.getBarcode());
                if (Objects.isNull(productByBarcode)) {
                    try {
                        ProductService.save(product1C);
                    } catch (SQLException e) {
                        log.error("Save product {} failed", product1C.getName(), e);
                    }
                }
            }
        }
    }
}