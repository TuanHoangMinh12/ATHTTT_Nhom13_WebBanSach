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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Xử lý download file private key (gọi từ iframe) ─────────────────
        String action = request.getParameter("action");
        if ("download".equals(action)) {
            handleDownload(request, response);
            return;
        }

        CustomerModel user = getLoggedInUser(request);
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login?action=login");
            return;
        }

        PublicKeyModel activeKey = findActiveKey(user.getIdUser());
        request.setAttribute("cus",       user);
        request.setAttribute("activeKey", activeKey);

        String msg = request.getParameter("msg");
        if ("generated".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg",
                    "Đã tạo cặp khóa mới thành công. Private key đã được tải xuống máy bạn.");
        } else if ("renewed".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg",
                    "Đã cập nhật khóa mới. Khóa cũ đã hết hiệu lực. Private key đã được tải xuống máy bạn.");
        } else if ("haveactive".equals(msg)) {
            request.setAttribute("alertType", "warning");
            request.setAttribute("alertMsg",
                    "Bạn đang có khóa hoạt động. Nếu muốn đổi khóa, hãy dùng chức năng Cập Nhật Khóa.");
        } else if ("error".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg", "Có lỗi xảy ra. Vui lòng thử lại sau.");
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

        boolean ok = generateAndStoreKey(request, response, user.getIdUser(), "generated");
        if (!ok) redirect(response, request, "error");
    }

    private void handleRenew(HttpServletRequest request, HttpServletResponse response,
                             CustomerModel user) throws IOException {

        customerDAO.update_publicKey(user.getIdUser());

        boolean ok = generateAndStoreKey(request, response, user.getIdUser(), "renewed");
        if (!ok) redirect(response, request, "error");
    }

    private boolean generateAndStoreKey(HttpServletRequest request, HttpServletResponse response,
                                        int idUser, String successMsg) throws IOException {
        try {
            RSAUtil rsa = new RSAUtil();
            rsa.genKey();

            String publicKey  = rsa.getPublicKeyAsString();
            String privateKey = rsa.getPrivateKeyAsString();

            // Lưu public key vào DB (status = 1, active)
            customerDAO.insert_publicKey(idUser, publicKey);

            // Tên file: private_key_<idUser>_<timestamp>.txt
            String fileName = "private_key_" + idUser + "_" + System.currentTimeMillis() + ".txt";

            // Lưu private key vào session tạm để handleDownload lấy
            HttpSession session = request.getSession(true);
            session.setAttribute("PRIVATE_KEY_DOWNLOAD", privateKey);
            session.setAttribute("PRIVATE_KEY_FILENAME", fileName);

            String redirectUrl = request.getContextPath() + "/key-management?msg=" + successMsg;

            // Trả về trang HTML trung gian:
            // - iframe ẩn trigger download file
            // - meta refresh redirect về trang chính sau 2 giây
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(
                    "<!DOCTYPE html><html><head><meta charset='UTF-8'>" +
                            "<title>Đang tải xuống...</title>" +
                            "<meta http-equiv='refresh' content='2;url=" + redirectUrl + "'>" +
                            "</head><body>" +
                            "<p>Đang tải private key xuống máy bạn... Vui lòng chờ.</p>" +
                            "<iframe src='" + request.getContextPath() + "/key-management?action=download' " +
                            "style='display:none'></iframe>" +
                            "</body></html>"
            );
            response.getWriter().flush();

            return true;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void handleDownload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        HttpSession session = request.getSession(false);
        if (session == null) return;

        String privateKey = (String) session.getAttribute("PRIVATE_KEY_DOWNLOAD");
        String fileName   = (String) session.getAttribute("PRIVATE_KEY_FILENAME");

        if (privateKey == null || fileName == null) return;

        // Xóa khỏi session ngay sau khi lấy — chỉ download được 1 lần
        session.removeAttribute("PRIVATE_KEY_DOWNLOAD");
        session.removeAttribute("PRIVATE_KEY_FILENAME");

        response.setContentType("text/plain; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.getWriter().write(privateKey);
        response.getWriter().flush();
    }

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