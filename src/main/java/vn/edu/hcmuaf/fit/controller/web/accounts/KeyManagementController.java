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

    private static final int MIN_PUBLIC_KEY_LENGTH = 20;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
        } else if ("keylost".equals(msg)) {
            request.setAttribute("alertType", "warning");
            request.setAttribute("alertMsg",
                    "Khóa cũ đã được vô hiệu hóa do báo mất. Vui lòng tạo khóa mới hoặc nhập Public Key có sẵn để tiếp tục sử dụng hệ thống.");
        } else if ("publickeysaved".equals(msg)) {
            request.setAttribute("alertType", "success");
            request.setAttribute("alertMsg", "Đã lưu Public Key của bạn thành công.");
        } else if ("invalidpublickey".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg",
                    "Public Key không hợp lệ. Vui lòng kiểm tra lại và thử lại.");
        } else if ("duplicatepublickey".equals(msg)) {
            request.setAttribute("alertType", "danger");
            request.setAttribute("alertMsg",
                    "Public Key này đã được sử dụng trong hệ thống. Vui lòng nhập một key khác.");
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

        request.setCharacterEncoding("UTF-8");

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
            case "submitPublicKey":
                handleSubmitPublicKey(request, response, user);
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

    private void handleSubmitPublicKey(HttpServletRequest request, HttpServletResponse response,
                                       CustomerModel user) throws IOException {

        PublicKeyModel activeKey = findActiveKey(user.getIdUser());
        if (activeKey != null) {
            redirect(response, request, "haveactive");
            return;
        }

        String publicKey = request.getParameter("publicKey");
        if (!isValidPublicKey(publicKey)) {
            redirect(response, request, "invalidpublickey");
            return;
        }

        String trimmedKey = publicKey.trim();

        // Chặn key đã được dùng (bởi bất kỳ user nào, ở bất kỳ trạng thái nào)
        if (publicKeyDao.existsByPublicKey(trimmedKey)) {
            redirect(response, request, "duplicatepublickey");
            return;
        }

        try {
            // Lưu public key vào DB (status = 1, active) — không sinh private key,
            // vì private key do người dùng tự giữ.
            customerDAO.insert_publicKey(user.getIdUser(), trimmedKey);
            redirect(response, request, "publickeysaved");
        } catch (Exception e) {
            e.printStackTrace();
            redirect(response, request, "error");
        }
    }

    // Kiểm tra Public Key không rỗng và đủ độ dài tối thiểu hợp lý.
    private boolean isValidPublicKey(String publicKey) {
        if (publicKey == null) return false;
        String trimmed = publicKey.trim();
        return trimmed.length() >= MIN_PUBLIC_KEY_LENGTH;
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

            // Trả về trang HTML trung gian: iframe ẩn trigger download file
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
