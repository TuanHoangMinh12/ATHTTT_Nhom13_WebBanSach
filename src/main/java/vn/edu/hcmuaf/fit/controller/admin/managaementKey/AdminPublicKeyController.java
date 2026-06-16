package vn.edu.hcmuaf.fit.controller.admin.managaementKey;

import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
/**
 * URL: /admin-public-key         → GET  → hiển thị danh sách
 *      /admin-public-key?revoke=<id> → GET → revoke key rồi redirect
 */
@WebServlet(name = "admin-public-key", value = "/admin-public-key")
public class AdminPublicKeyController extends HttpServlet {
    private final PublicKeyDao publicKeyDao = new PublicKeyDao();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Nếu có param revoke=<id> thì thực hiện revoke trước
        String revokeParam = request.getParameter("revoke");
        if (revokeParam != null) {
            try {
                int idKey = Integer.parseInt(revokeParam);
                boolean ok = publicKeyDao.revokeKey(idKey);
                if (ok) {
                    response.sendRedirect(request.getContextPath() + "/admin-public-key?msg=revoked");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin-public-key?msg=error");
                }
                return;
            } catch (NumberFormatException e) {
                response.sendRedirect(request.getContextPath() + "/admin-public-key");
                return;
            }
        }
        // Dùng title để aside.jsp active đúng menu
        request.setAttribute("title", "Danh Sách Public Key");
        request.setAttribute("listKeys", publicKeyDao.getAllKeys());

        // Truyền thông báo nếu có
        String msg = request.getParameter("msg");
        if ("revoked".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg", "Đã revoke key thành công.");
        } else if ("error".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg", "Có lỗi xảy ra, vui lòng thử lại.");
        }
        request.getRequestDispatcher("/views/admin/table-public-key.jsp").forward(request, response);
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}