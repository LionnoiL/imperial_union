package ua.gaponov.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.analyze.Analyze;

import java.io.IOException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/service")
public class ServiceServlet extends ApplicationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        String command = "";
        if (req.getParameterMap().containsKey("command")) {
            command = req.getParameter("command");
        }

        switch (command) {
            case "analyze-names":
                Analyze.analyzeNames();
                break;
        }

        SimilarityProductsServlet.redirect(resp);
    }
}
