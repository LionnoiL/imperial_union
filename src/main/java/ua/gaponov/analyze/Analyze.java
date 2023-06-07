package ua.gaponov.analyze;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.product1c.Product1C;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Analyze {

    public static void checkBarcodes(List<Product1C> productList) {
        log.info("Start check barcodes");
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
            if (nonNull(product1C.getBarcode()) && !product1C.getBarcode().isEmpty()) {
                Product productByBarcode = ProductService.getProductByBarcode(product1C.getBarcode());
                if (isNull(productByBarcode)) {
                    //new barcode. add product
                    try {
                        ProductService.save(product1C);
                    } catch (SQLException e) {
                        log.error("Save product {} failed", product1C, e);
                    }
                } else {
                    //add shop_product
                    try {
                        ProductService.addShopProduct(productByBarcode.getId(), product1C);
                    } catch (SQLException e) {
                        log.error("Save shop product {} failed", product1C.getName(), e);
                    }
                }
            }
        }
        log.info("End check barcodes");
    }

    public static void analyzeNames() {
        ProductService.checkCompleteProduct();
        ProductService.deleteAllSimilarityProducts();

        log.info("Start analyze names");
        int matchValue = 5;

        LevenshteinDistance defaultInstance = LevenshteinDistance.getDefaultInstance();
        Map<Product, Map<Product, Integer>> results = new HashMap<>();

        List<Product> allNonComplete = ProductService.getAllNonComplete();
        List<Product> allProducts = ProductService.getAll();

        int listSize = allProducts.size();
        int packetSize = listSize / 100;
        int packet = 1;
        int currentIndex = 0;

        for (Product product : allNonComplete) {

            currentIndex += 1;
            if (currentIndex >= packetSize) {
                currentIndex = 0;
                packet += 1;
                log.debug("{}%", packet);
            }

            for (Product product1 : allProducts) {
                if (product.getId().equals(product1.getId())) {
                    continue;
                }
                Integer result = defaultInstance.apply(product.getName(), product1.getName());
                if (result <= matchValue) {
                    Map<Product, Integer> productIntegerMap = new HashMap<>();
                    if (results.containsKey(product)) {
                        productIntegerMap = results.get(product);
                    }
                    productIntegerMap.put(product1, result);
                    results.put(product, productIntegerMap);
                    try {
                        ProductService.addSimilarityProducts(product, product1);
                    } catch (SQLException e) {
                        log.error("Failed save similarity product", e);
                    }
                }
            }
        }
        log.info("End analyze names");
    }
}
