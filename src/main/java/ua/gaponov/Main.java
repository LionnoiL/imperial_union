package ua.gaponov;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.analyze.Analyze;
import ua.gaponov.config.LoggingConfiguration;
import ua.gaponov.entity.product1c.Product1C;
import ua.gaponov.entity.product1c.Product1CService;

import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class Main {
    public static void main(String[] args) {

        new LoggingConfiguration();

//        List<Product1C> productList1 = Product1CService.getProductsFromFile("imperial_1.csv", 1);
//        List<Product1C> productList2 = Product1CService.getProductsFromFile("imperial_2.csv", 2);
//        List<Product1C> productList3 = Product1CService.getProductsFromFile("imperial_3.csv", 3);
//
//        Analyze.checkBarcodes(productList3);
//        Analyze.checkBarcodes(productList2);
//        Analyze.checkBarcodes(productList1);

        Analyze.analyzeNames();
    }


}