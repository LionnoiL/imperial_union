package ua.gaponov.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import ua.gaponov.entity.barcodes.BarcodeService;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.shopproduct.ShopProductService;
import ua.gaponov.entity.similarity.SimilarityProduct;
import ua.gaponov.entity.similarity.SimilarityProductService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ua.gaponov.config.Constants.PRODUCT_ID_PARAMETER_NAME;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/similarity/*")
public class SimilarityProductsServlet extends ApplicationServlet {

    private static List<String> skipProducts = new ArrayList<>();
    private static boolean random;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String productId = "";
        if (req.getParameterMap().containsKey(PRODUCT_ID_PARAMETER_NAME)) {
            productId = req.getParameter(PRODUCT_ID_PARAMETER_NAME);
        }

        int count = SimilarityProductService.getCount();

        SimilarityProduct similarityProduct = SimilarityProductService.getFirst(skipProducts, random);

        ProductService.fillBarcodes(similarityProduct.getMainProduct());
        ProductService.fillShopProducts(similarityProduct.getMainProduct());

        List<Product> similarityProducts = similarityProduct.getSimilarityProducts();
        for (Product product : similarityProducts) {
            ProductService.fillBarcodes(product);
            ProductService.fillShopProducts(product);
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("product", similarityProduct,
                        "id", productId,
                        "count", count,
                        "random", random)
        );

        resp.setContentType("text/html");
        engine.process("similarity", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        String pathInfo = req.getPathInfo();
        String simProductId = req.getParameter("simProductId");
        String mainProductId = req.getParameter("mainProductId");
        switch (pathInfo) {
            case "/analog":
                ShopProductService.moveShopProducts(mainProductId, simProductId);
                BarcodeService.moveBarcodes(mainProductId, simProductId);

                SimilarityProductService.deleteSimilarityProductsByProductId(simProductId);
                ProductService.delete(simProductId);
                break;
            case "/different":
                SimilarityProductService.deleteSimilarityProductsByProductId(simProductId);
                break;
            case "/skip":
                skipProducts.add("'" + mainProductId + "'");
                break;
            case "/delete-skip":
                skipProducts.clear();
                break;
            case "/random":
                random = !random;
                break;
            default:
        }

        ProductService.completeNameProduct(mainProductId);
        //redirect
        resp.setStatus(resp.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", "/similarity");
    }
}
