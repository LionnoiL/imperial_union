package ua.gaponov;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.analyze.Analyze;
import ua.gaponov.config.LoggingConfiguration;
import ua.gaponov.database.Database;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.product1c.Product1CService;

import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class Main {
    public static void main(String[] args) {

        new LoggingConfiguration().setup();

        boolean importProducts = true;

        if (importProducts){
            List<Product1C> productList1 = Product1CService.getProductsFromFile("imperial_1.csv", 1);
            List<Product1C> productList2 = Product1CService.getProductsFromFile("imperial_2.csv", 2);
            List<Product1C> productList3 = Product1CService.getProductsFromFile("imperial_3.csv", 3);

            Analyze.importData(productList3);
            Analyze.importData(productList2);
            Analyze.importData(productList1);
        }
        Analyze.analyzeNames();

        Database.execSql("""
                DELETE si FROM similarity_products si
                LEFT JOIN shop_products s1 ON s1.product_id = si.product_id_1
                LEFT JOIN shop_products s2 ON s2.product_id = si.product_id_2
                LEFT JOIN products p1 ON p1.id = si.product_id_1
                LEFT JOIN products p2 ON p2.id = si.product_id_2
                WHERE s1.shop_id = s2.shop_id
                """);
    }
}