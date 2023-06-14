package ua.gaponov.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.entity.product.Product;
import ua.gaponov.entity.product.ProductService;
import ua.gaponov.entity.similarity.SimilarityProduct;
import ua.gaponov.entity.similarity.SimilarityProductService;
import ua.gaponov.entity.users.User;
import ua.gaponov.utils.ServletUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/similarity/*")
public class SimilarityProductsServlet extends ApplicationServlet {

    private static Map<User, List<String>> skippedProducts = new HashMap<>();
    private static Map<User, Boolean> usersRandomOptions = new HashMap<>();

    public static void redirect(HttpServletResponse resp) {
        resp.setStatus(resp.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", "/similarity");
    }

    private static void processRandom(User user) {
        boolean userRandomValue = false;
        if (usersRandomOptions.containsKey(user)) {
            userRandomValue = usersRandomOptions.get(user);
        }
        usersRandomOptions.put(user, !userRandomValue);
    }

    private static void processSkip(User user, String mainProductId) {
        List<String> listSkippedUserProducts = skippedProducts.get(user);
        if (listSkippedUserProducts == null) {
            listSkippedUserProducts = new ArrayList<>();
        }
        listSkippedUserProducts.add("'" + mainProductId + "'");
        skippedProducts.put(user, listSkippedUserProducts);
    }

    private SimilarityProduct getSimilarityProduct(User user, boolean userRandomOptionsValue) {
        SimilarityProduct similarityProduct = SimilarityProductService.getFirst(skippedProducts.get(user), userRandomOptionsValue);
        ProductService.fillBarcodes(similarityProduct.getMainProduct());
        ProductService.fillShopProducts(similarityProduct.getMainProduct());

        List<Product> similarityProducts = similarityProduct.getSimilarityProducts();
        for (Product product : similarityProducts) {
            ProductService.fillBarcodes(product);
            ProductService.fillShopProducts(product);
        }
        return similarityProduct;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            LoginServlet.redirect(resp);
            return;
        }

        int count = SimilarityProductService.getCount();
        boolean userRandomOptionsValue = false;
        if (usersRandomOptions.containsKey(user)) {
            userRandomOptionsValue = usersRandomOptions.get(user);
        }
        SimilarityProduct similarityProduct = getSimilarityProduct(user, userRandomOptionsValue);

        Map<String, Object> parameters = Map.of(
                "product", similarityProduct,
                "count", count,
                "random", userRandomOptionsValue,
                "user", user
        );
        ServletUtils.processTemplate(engine, req, resp, "similarity", parameters);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");

        String pathInfo = req.getPathInfo();
        String simProductId = req.getParameter("simProductId");
        String mainProductId = req.getParameter("mainProductId");
        switch (pathInfo) {
            case "/analog":
                SimilarityProductService.processAnalog(simProductId, mainProductId);
                break;
            case "/different":
                SimilarityProductService.deleteSimilarityProductsByProductId(simProductId, mainProductId);
                break;
            case "/skip":
                processSkip(user, mainProductId);
                break;
            case "/delete-skip":
                skippedProducts.clear();
                break;
            case "/random":
                processRandom(user);
                break;
            default:
        }

        ProductService.completeNameProduct(mainProductId);
        redirect(resp);
    }


}
