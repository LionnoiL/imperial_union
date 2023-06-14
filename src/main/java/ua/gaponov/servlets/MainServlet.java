package ua.gaponov.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/")
public class MainServlet extends ApplicationServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(resp.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", "/similarity");
    }
}
