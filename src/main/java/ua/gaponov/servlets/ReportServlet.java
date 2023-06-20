package ua.gaponov.servlets;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import ua.gaponov.entity.users.User;
import ua.gaponov.entity.users.UsersRole;
import ua.gaponov.reports.Report;
import ua.gaponov.reports.ShopProductReport;
import ua.gaponov.utils.ServletUtils;

import java.io.IOException;
import java.util.Collections;

import static java.util.Objects.isNull;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@WebServlet(value = "/report/*")
public class ReportServlet extends ApplicationServlet {

    public static void redirect(HttpServletResponse resp) {
        resp.setStatus(resp.SC_MOVED_PERMANENTLY);
        resp.setHeader("Location", "/report");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !UsersRole.ROLE_ADMIN.equals(user.getRole())) {
            LoginServlet.redirect(resp);
            return;
        }

        String reportName = req.getParameter("name");
        String reportParams = req.getParameter("params");
        if (isNull(reportName)) {
            ServletUtils.processTemplate(engine, req, resp, "report", Collections.emptyMap());
        } else {
            Report report = generateReport(reportName, reportParams);
            sendReport(resp, report);
        }
    }

    private Report generateReport(String reportName, String reportParams) {
        Report report = null;
        switch (reportName) {
            case "shop-product":
                report = new ShopProductReport();
                report.addParameters(reportParams);
                report.generate();
                return report;
            default:
                //nop
        }
        return report;
    }

    private void sendReport(HttpServletResponse resp, Report report) {

        String filename = report.getName() + ".csv";
        try {
            ServletOutputStream out = resp.getOutputStream();
            byte[] byteArray = report.getReportText().getBytes();
            resp.setContentType("text/plain");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            out.write(byteArray);
            out.flush();
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
