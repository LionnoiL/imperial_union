package ua.gaponov.entity.barcodes;

import lombok.Getter;
import lombok.Setter;
import ua.gaponov.entity.product.Product;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
public class Barcode {
    private Product product;
    private String value;
}
