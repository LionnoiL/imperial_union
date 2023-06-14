package ua.gaponov.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.util.Map;

/**
 * @author Andriy Gaponov
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServletUtils {

    public static void processTemplate(TemplateEngine engine, HttpServletRequest req, HttpServletResponse resp,
                                       String template, Map<String, Object> parameters) throws IOException {
        Context simpleContext = new Context(
                req.getLocale(),
                parameters
        );

        resp.setContentType("text/html");
        engine.process(template, simpleContext, resp.getWriter());
        resp.getWriter().close();
    }
}
