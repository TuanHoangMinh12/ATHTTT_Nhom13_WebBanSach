package vn.edu.hcmuaf.fit.controller.web.accounts;

import vn.edu.hcmuaf.fit.dao.impl.KeyLossReportDao;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý báo mất private key của người dùng.
 *
 * GET  /report-key-loss  → Hiển thị form báo mất (danh sách key đang active)
 * POST /report-key-loss  → Lưu báo cáo vào DB và thông báo thành công
 */
@WebServlet(name = "reportKeyLoss", value = "/report-key-loss")
public class ReportKeyLossController extends HttpServlet {

    private final KeyLossReportDao reportDao  = new KeyLossReportDao();
    private final PublicKeyDao     publicKeyDao = new PublicKeyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        CustomerModel user  = (session != null) ? (CustomerModel) session.getAttribute("account") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy danh sách key ĐANG ACTIVE của user
        List<PublicKeyModel> activeKeys = publicKeyDao.getActiveKeysByUser(user.getIdUser());
        request.setAttribute("activeKeys", activeKeys);
        request.setAttribute("title", "Báo Mất Khóa");
        request.getRequestDispatcher("/views/web/report-key-loss.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session  = request.getSession(false);
        CustomerModel user   = (session != null) ? (CustomerModel) session.getAttribute("account") : null;

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idKeyParam = request.getParameter("idKey");
        String reason     = request.getParameter("reason");

        if (idKeyParam == null || idKeyParam.isEmpty()) {
            request.setAttribute("error", "Vui lòng chọn khóa bị mất.");
            doGet(request, response);
            return;
        }

        int idKey;
        try {
            idKey = Integer.parseInt(idKeyParam);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Khóa không hợp lệ.");
            doGet(request, response);
            return;
        }

        // Kiểm tra đã có báo cáo pending chưa
        if (reportDao.hasPendingReport(idKey)) {
            request.setAttribute("error", "Bạn đã gửi báo cáo cho khóa này và đang chờ admin xử lý.");
            doGet(request, response);
            return;
        }

        boolean ok = reportDao.submitReport(idKey, user.getIdUser(), reason);

        if (ok) {
            // Redirect để tránh re-submit khi refresh
            response.sendRedirect(request.getContextPath() + "/report-key-loss?success=1");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra khi gửi báo cáo. Vui lòng thử lại.");
            doGet(request, response);
        }
    }
}
