package vn.edu.hcmuaf.fit.controller.web.accounts;

import vn.edu.hcmuaf.fit.dao.impl.KeyLossReportDao;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;


@WebServlet(name = "reportKeyLoss", value = "/report-key-loss")
public class ReportKeyLossController extends HttpServlet {

    private final KeyLossReportDao reportDao  = new KeyLossReportDao();
    private final PublicKeyDao     publicKeyDao = new PublicKeyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerModel user = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");

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
        CustomerModel user = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");

        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String idKeyParam   = request.getParameter("idKey");
        String reason       = request.getParameter("reason");
        String lossTimeParam = request.getParameter("lossTime"); // input type="datetime-local"

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

        if (lossTimeParam == null || lossTimeParam.isEmpty()) {
            request.setAttribute("error", "Vui lòng chọn thời điểm bạn bị mất khóa.");
            doGet(request, response);
            return;
        }

        Timestamp lostTime;
        try {
            LocalDateTime ldt = LocalDateTime.parse(lossTimeParam);
            lostTime = Timestamp.valueOf(ldt);
        } catch (DateTimeParseException e) {
            request.setAttribute("error", "Thời điểm mất khóa không hợp lệ.");
            doGet(request, response);
            return;
        }

        if (lostTime.after(new Timestamp(System.currentTimeMillis()))) {
            request.setAttribute("error", "Thời điểm mất khóa không thể ở tương lai.");
            doGet(request, response);
            return;
        }

        if (reportDao.hasPendingReport(idKey)) {
            request.setAttribute("error", "Bạn đã gửi báo cáo cho khóa này và đang chờ admin xử lý.");
            doGet(request, response);
            return;
        }

        boolean ok = reportDao.submitReport(idKey, user.getIdUser(), reason, lostTime);

        if (ok) {
            // Redirect để tránh re-submit khi refresh
            response.sendRedirect(request.getContextPath() + "/report-key-loss?success=1");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra khi gửi báo cáo. Vui lòng thử lại.");
            doGet(request, response);
        }
    }
}
