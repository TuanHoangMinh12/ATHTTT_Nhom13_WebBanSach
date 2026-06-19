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

        String filter = request.getParameter("filter");
        List<KeyLossReportModel> reports;

        if ("pending".equals(filter)) {
            reports = dao.getPendingReports();
        } else {
            reports = dao.getAllReports();
        }

        // Đếm số lượng chờ xử lý để hiển thị badge trên menu
        long pendingCount = reports.stream()
                .filter(r -> r.getStatus() == KeyLossReportModel.STATUS_PENDING)
                .count();
        if (!"pending".equals(filter)) {
            // Khi xem tất cả, vẫn cần số pending thực từ DB
            pendingCount = dao.getPendingReports().size();
        }

        request.setAttribute("title",        "Thông Báo Mất Khóa");
        request.setAttribute("reports",      reports);
        request.setAttribute("pendingCount", pendingCount);
        request.setAttribute("currentFilter", filter != null ? filter : "all");

        // Truyền thông báo kết quả xử lý (nếu có)
        String msg = request.getParameter("msg");
        if ("approved".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg",  "Đã xác nhận mất khóa. Các đơn hàng sau thời điểm báo mất đã được cập nhật lỗi.");
        } else if ("rejected".equals(msg)) {
            request.setAttribute("alertType", "info");
            request.setAttribute("alertMsg",  "Đã từ chối báo cáo mất khóa.");
        } else if ("error".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg",  "Có lỗi xảy ra. Vui lòng thử lại.");
        }

        request.getRequestDispatcher("/views/admin/table-key-loss-report.jsp")
                .forward(request, response);
    }
