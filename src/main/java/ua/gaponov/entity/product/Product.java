package ua.gaponov.entity.product;

import lombok.Getter;
import lombok.Setter;
import ua.gaponov.entity.shopproduct.ShopProduct;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
public class Product {
    List<String> barcodes = new ArrayList<>();
    List<ShopProduct> shopProducts = new ArrayList<>();
    private String id;
    private String name;
    private boolean weight;

    public String getBarcodesStringList() {
        return String.join(", ", barcodes);
    }
}
