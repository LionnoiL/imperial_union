package ua.gaponov.servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import ua.gaponov.config.LoggingConfiguration;

/**
 * @author Andriy Gaponov
 */
public class ApplicationServlet extends HttpServlet {

    protected TemplateEngine engine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        new LoggingConfiguration().setup();

        engine = new TemplateEngine();
        JakartaServletWebApplication app = JakartaServletWebApplication.buildApplication(
                config.getServletContext());

        WebApplicationTemplateResolver resolver =
                new WebApplicationTemplateResolver(app);
        resolver.setPrefix("/WEB-INF/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
}
