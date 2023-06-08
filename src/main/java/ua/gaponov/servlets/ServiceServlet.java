package ua.gaponov.servlets;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import ua.gaponov.analyze.Analyze;

import java.io.IOException;
import java.util.Map;

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

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of()
        );

        resp.setContentType("text/html");
        engine.process("similarity", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
