package ua.gaponov;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.config.LoggingConfiguration;
import ua.gaponov.product.Product1C;
import ua.gaponov.product.Product1CService;

import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        new LoggingConfiguration();


            //List<Product1C> productList1 = Product1CService.getProductsFromFile("imperial_1.csv");
            //List<Product1C> productList2 = Product1CService.getProductsFromFile("imperial_2.csv");
            List<Product1C> productList3 = Product1CService.getProductsFromFile("imperial_3.csv");


    }
}