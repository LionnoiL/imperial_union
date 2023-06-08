package ua.gaponov.entity.similarity;

import lombok.Getter;
import lombok.Setter;
import ua.gaponov.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Getter
@Setter
public class SimilarityProduct {

    private Product mainProduct;
    private List<Product> similarityProducts = new ArrayList<>();
}
