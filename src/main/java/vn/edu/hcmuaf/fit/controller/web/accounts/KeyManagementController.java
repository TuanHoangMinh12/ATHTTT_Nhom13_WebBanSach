package vn.edu.hcmuaf.fit.controller.web.accounts;

import vn.edu.hcmuaf.fit.dao.impl.CustomerDAO;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;
import vn.edu.hcmuaf.fit.utils.RSAUtil;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet(name = "keyManagement", value = "/key-management")
public class KeyManagementController extends HttpServlet {

    private final PublicKeyDao publicKeyDao = new PublicKeyDao();
    private final CustomerDAO  customerDAO  = new CustomerDAO();

    // ── GET: hiển thị trang quản lý khóa ────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerModel user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?action=login");
            return;
        }

        PublicKeyModel activeKey = findActiveKey(user.getIdUser());
        request.setAttribute("cus",       user);
        request.setAttribute("activeKey", activeKey);

        // Thông báo kết quả thao tác (nếu có, sau redirect)
        String msg = request.getParameter("msg");
        if ("generated".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg",
                    "Đã tạo cặp khóa mới thành công. Private key chỉ hiển thị MỘT LẦN, hãy lưu lại ngay.");
        } else if ("renewed".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg",
                    "Đã cập nhật khóa mới. Khóa cũ đã hết hiệu lực. Private key chỉ hiển thị MỘT LẦN, hãy lưu lại ngay.");
        } else if ("haveactive".equals(msg)) {
            request.setAttribute("alertType", "warning");
            request.setAttribute("alertMsg",
                    "Bạn đang có khóa hoạt động. Nếu muốn đổi khóa, hãy dùng chức năng Cập Nhật Khóa.");
        } else if ("error".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }

        // Private key vừa sinh ra (chỉ tồn tại trong 1 lần request - flash attribute)
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("NEW_PRIVATE_KEY") != null) {
            request.setAttribute("newPrivateKey", session.getAttribute("NEW_PRIVATE_KEY"));
            session.removeAttribute("NEW_PRIVATE_KEY"); // chỉ hiển thị đúng 1 lần
        }

        request.getRequestDispatcher("/views/web/key-management.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        CustomerModel user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?action=login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "generate":
                handleGenerate(request, response, user);
                break;
            case "renew":
                handleRenew(request, response, user);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/key-management");
        }
    }

    private void handleGenerate(HttpServletRequest request, HttpServletResponse response,
                                CustomerModel user) throws IOException {

        PublicKeyModel activeKey = findActiveKey(user.getIdUser());
        if (activeKey != null) {
            redirect(response, request, "haveactive");
            return;
        }

        boolean ok = generateAndStoreKey(request, user.getIdUser());
        redirect(response, request, ok ? "generated" : "error");
    }

    private void handleRenew(HttpServletRequest request, HttpServletResponse response,
                             CustomerModel user) throws IOException {

        // Thu hồi toàn bộ khóa đang active của user
        customerDAO.update_publicKey(user.getIdUser());

        boolean ok = generateAndStoreKey(request, user.getIdUser());
        redirect(response, request, ok ? "renewed" : "error");
    }

    private boolean generateAndStoreKey(HttpServletRequest request, int idUser) {
        try {
            RSAUtil rsa = new RSAUtil();
            rsa.genKey();

            String publicKey  = rsa.getPublicKeyAsString();
            String privateKey = rsa.getPrivateKeyAsString();

            // Lưu public key vào DB (status = 1, active)
            customerDAO.insert_publicKey(idUser, publicKey);

            // Private key KHÔNG được lưu trên server.
            // Chỉ đưa vào session để hiển thị MỘT LẦN duy nhất cho người dùng tải về.
            HttpSession session = request.getSession(true);
            session.setAttribute("NEW_PRIVATE_KEY", privateKey);

            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Lấy khóa đang active (status = 1) của user, null nếu không có */
    private PublicKeyModel findActiveKey(int idUser) {
        List<PublicKeyModel> keys = publicKeyDao.getAllKeys();
        for (PublicKeyModel k : keys) {
            if (k.getIdUser() == idUser && k.getStatus() == 1) {
                return k;
            }
        }
        return null;
    }

    private CustomerModel getLoggedInUser(HttpServletRequest request) {
        return (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
    }

    private void redirect(HttpServletResponse response, HttpServletRequest request, String msg)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/key-management?msg=" + msg);
    }
}
