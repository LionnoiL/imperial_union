package ua.gaponov.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import ua.gaponov.entity.similarity.SimilarityProduct;
import ua.gaponov.entity.similarity.SimilarityProductService;

import java.io.IOException;
import java.util.Map;

import static ua.gaponov.config.Constants.PRODUCT_ID_PARAMETER_NAME;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/similarity")
public class SimilarityProductsServlet extends ApplicationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String productId = "";
        if (req.getParameterMap().containsKey(PRODUCT_ID_PARAMETER_NAME)) {
            productId = req.getParameter(PRODUCT_ID_PARAMETER_NAME);
        }

        SimilarityProduct similarityProduct = SimilarityProductService.getFirst();

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("product", similarityProduct,
                        "id", productId)
        );

        resp.setContentType("text/html");
        engine.process("similarity", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
