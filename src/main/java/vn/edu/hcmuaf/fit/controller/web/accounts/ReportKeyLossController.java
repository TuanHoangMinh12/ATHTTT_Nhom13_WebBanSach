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
import java.util.List;

@WebServlet(name = "reportKeyLoss", value = "/report-key-loss")
public class ReportKeyLossController extends HttpServlet {

    private final KeyLossReportDao reportDao    = new KeyLossReportDao();
    private final PublicKeyDao     publicKeyDao = new PublicKeyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerModel user = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?action=login");
            return;
        }

        // Lấy danh sách key đang ACTIVE của user
        List<PublicKeyModel> activeKeys = publicKeyDao.getActiveKeysByUser(user.getIdUser());
        request.setAttribute("activeKeys", activeKeys);
        request.getRequestDispatcher("/views/web/report-key-loss.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        CustomerModel user = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?action=login");
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

        boolean ok = reportDao.submitReport(idKey, user.getIdUser(), reason);

        if (ok) {
            response.sendRedirect(request.getContextPath() + "/key-management?msg=keylost");
        } else {
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
            doGet(request, response);
        }
    }
}
