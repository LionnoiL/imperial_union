package ua.gaponov.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.context.Context;
import ua.gaponov.entity.users.UserService;

import java.io.IOException;
import java.util.Collections;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/login")
public class LoginServlet extends ApplicationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.invalidate();

        Context simpleContext = new Context(
                req.getLocale(),
                Collections.emptyMap()
        );

        resp.setContentType("text/html");
        engine.process("login", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("userName");
        String userPassword = req.getParameter("userPassword");

        if (UserService.login(userName, userPassword)) {
            HttpSession session = req.getSession();
            session.setAttribute("user", UserService.getByName(userName));

            resp.setStatus(resp.SC_MOVED_PERMANENTLY);
            resp.setHeader("Location", "/similarity");
            return;
        }

        resp.setStatus(resp.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", "/login");
    }
}
