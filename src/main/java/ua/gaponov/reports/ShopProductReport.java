package ua.gaponov.reports;

import lombok.extern.slf4j.Slf4j;
import ua.gaponov.entity.shopproduct.ShopProduct;
import ua.gaponov.entity.shopproduct.ShopProductService;

import java.util.List;

/**
 * @author Andriy Gaponov
 */
@Slf4j
public class ShopProductReport extends Report {

    public ShopProductReport() {
        super("shop-product");
    }

    @Override
    public void generate() {
        int shopId = 0;
        if (reportParameters.containsKey("shop_id")) {
            shopId = Integer.parseInt(reportParameters.get("shop_id"));
        }

        List<ShopProduct> byShopId = ShopProductService.getByShopId(shopId);
        StringBuilder sb = new StringBuilder("product_id;name;product_code;shop_id\n");
        for (ShopProduct shopProduct : byShopId) {
            sb.append(shopProduct.getProduct().getId());
            sb.append(";");
            sb.append(shopProduct.getProduct().getName());
            sb.append(";");
            sb.append(shopProduct.getCode());
            sb.append(";");
            sb.append(shopProduct.getShopId());
            sb.append("\n");
        }
        reportText = sb.toString();
    }
}
