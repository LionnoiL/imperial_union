package ua.gaponov.analyze;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import ua.gaponov.entity.barcodes.BarcodeService;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.shopproduct.ShopProductService;
import ua.gaponov.entity.similarity.SimilarityProductService;

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

    private static final int MATCH_VALUE = 5;
    private static int packet = 0;
    private static int currentIndex = 0;
    private static int packetSize = 0;

    public static void importData(List<Product1C> productList) {
        log.info("Start import data");
        currentIndex = 0;
        packet = 0;
        packetSize = productList.size() / 100;

        for (Product1C product1C : productList) {
            printStatus();
            if (nonNull(product1C.getBarcode()) && !product1C.getBarcode().isEmpty()) {
                String productId = BarcodeService.getProductIdByBarcode(product1C.getBarcode());
                if (isNull(productId) || productId.isEmpty()) {
                    //new barcode. add product
                    addProduct(product1C);
                } else {
                    //add shop_product
                    addShopProduct(productId, product1C);
                }
            } else {
                addProduct(product1C);
            }
        }
        log.info("End import data");
    }

    private static void addProduct(Product1C product1C) {
        try {
            ProductService.add(product1C);
        } catch (SQLException e) {
            log.error("Add product {} failed", product1C, e);
        }
    }

    private static void addShopProduct(String productId, Product1C product1C) {
        try {
            ShopProductService.addShopProduct(productId, product1C);
        } catch (SQLException e) {
            log.error("Save shop product {} failed", product1C.getName(), e);
        }
    }

    public static void analyzeNames() {
        log.info("Start analyze names");

        ProductService.checkCompleteProduct();
        SimilarityProductService.deleteAllSimilarityProducts();

        LevenshteinDistance defaultInstance = LevenshteinDistance.getDefaultInstance();
        Map<Product, Map<Product, Integer>> results = new HashMap<>();

        List<Product> allNonComplete = ProductService.getAllNonComplete();
        List<Product> allProducts = ProductService.getAll();

        currentIndex = 0;
        packet = 0;
        packetSize = allProducts.size() / 100;

        for (Product product : allNonComplete) {
            printStatus();

            for (Product product1 : allProducts) {
                if (product.getId().equals(product1.getId())) {
                    continue;
                }
                Integer result = defaultInstance.apply(product.getName(), product1.getName());
                if (result <= MATCH_VALUE) {
                    Map<Product, Integer> productIntegerMap = new HashMap<>();
                    if (results.containsKey(product)) {
                        productIntegerMap = results.get(product);
                    }
                    productIntegerMap.put(product1, result);
                    results.put(product, productIntegerMap);
                    try {
                        SimilarityProductService.addSimilarityProducts(product, product1);
                    } catch (SQLException e) {
                        log.error("Failed add similarity product", e);
                    }
                }
            }
        }
        log.info("End analyze names");
    }

    private static void printStatus() {
        currentIndex += 1;
        if (currentIndex >= packetSize) {
            currentIndex = 0;
            packet += 1;
            log.debug("{}%", packet);
        }
    }
}
