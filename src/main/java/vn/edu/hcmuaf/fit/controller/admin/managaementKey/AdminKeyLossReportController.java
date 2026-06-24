package vn.edu.hcmuaf.fit.controller.admin.managaementKey;

import vn.edu.hcmuaf.fit.dao.impl.KeyLossReportDao;
import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "adminKeyLossReport", value = "/admin-key-loss-report")
public class AdminKeyLossReportController extends HttpServlet {

    private final KeyLossReportDao dao = new KeyLossReportDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<KeyLossReportModel> reports = dao.getAllReports();

        request.setAttribute("title",   "Lịch Sử Báo Mất Khóa");
        request.setAttribute("reports", reports);

        request.getRequestDispatcher("/views/admin/table-key-loss-report.jsp")
                .forward(request, response);
    }
}
