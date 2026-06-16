package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.bean.Log;
import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.CustomerDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.utils.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.InetAddress;

@WebServlet(name = "admin-order-detail", value = "/admin-order-detail")
public class OrderDetailController extends HttpServlet {

    CartDao cartDao = new CartDao();
    BillDAO billDAO = new BillDAO();
    CustomerDAO customerDAO = new CustomerDAO();
    SHA256Util sha256Util = new SHA256Util();
    SessionUtil sessionUtil = new SessionUtil();
    RSAUtil rsa = new RSAUtil();
    ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        CartModel cartModel = cartDao.getCartById(idInt);

        request.setAttribute("id", idInt);
        request.setAttribute("CUSTOMER", customerDAO.findById(cartModel.getIdUser()));
        request.setAttribute("cart", listDonHang(idInt));
        request.setAttribute("LISTBILL", cartDao.getAllDetailCart(cartModel.getIdUser(), idInt));
        request.getRequestDispatcher("/views/admin/confirm-order-detail.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        CartModel cartModel = cartDao.getCartById(idInt);
        int idUser = cartModel.getIdUser();

        // Lấy admin đang đăng nhập để ghi log
        CustomerModel admin = (CustomerModel) SessionUtil.getInstance()
                .getValue(request, "USERMODEL");
        String ip = "unknown";
        try { ip = InetAddress.getLocalHost().getHostAddress(); } catch (Exception ignored) {}

        // Lấy public key của user đặt hàng
        String publicKey = cartDao.getPuclickey(idUser, idInt);

        // Lấy chữ ký đã lưu khi user đặt hàng
        String signature = cartDao.getHash(idInt, idUser);

        // Tái tạo chuỗi từ dữ liệu DB HIỆN TẠI và băm lại
        String orderNow = objectVerifyUtil.string(idUser, idInt);
        String hash1 = sha256Util.check(orderNow);

        VerifyResult result = VerifyResult.ERROR;

        try {
            rsa.setPublicKey(publicKey);
            // Giải mã chữ ký → lấy hash gốc lúc user ký
            String hash2 = rsa.decrypt(signature);

            if (hash1.equals(hash2)) {
                // ✅ Dữ liệu DB khớp với chữ ký → đơn hàng toàn vẹn
                result = VerifyResult.OK;
                request.setAttribute("successMessage", "✅ Đã xác thực — đơn hàng toàn vẹn, không bị chỉnh sửa.");

                // Ghi log verify thành công
                new Log(Log.INFO, ip, "Verify đơn hàng", admin.getIdUser(),
                        "Đơn hàng #" + idInt + " hợp lệ", 1).insert();

            } else {
                // ❌ hash1 != hash2 → dữ liệu trong DB đã bị thay đổi so với lúc ký
                result = VerifyResult.TAMPERED;

                // Ghi log cảnh báo
                new Log(Log.ALER, ip, "CẢNH BÁO: Đơn hàng bị chỉnh sửa",
                        admin.getIdUser(),
                        "Đơn hàng #" + idInt + " - hash không khớp. " +
                                "Hash hiện tại: " + hash1.substring(0, 16) + "...", 1).insert();

                // Tự động cập nhật trạng thái thành "đã hủy" (infoShip = 4)
                cartDao.updateCart(idInt, 4);

                String link = "<a href=\"" + request.getContextPath()
                        + "/admin-table-order\" style=\"color:#007FFF\">Về danh sách đơn hàng</a>";
                request.setAttribute("nosuccessMessage",
                        "❌ Đơn hàng đã bị chỉnh sửa trong database! " +
                                "Đơn hàng đã tự động bị hủy. " + link);
            }

        } catch (Exception e) {
            // Lỗi khi giải mã: public key không đúng hoặc chữ ký hỏng
            result = VerifyResult.ERROR;
            request.setAttribute("nosuccessMessage",
                    "⚠️ Không thể xác thực: " + e.getMessage() +
                            " (Public key có thể đã bị thay đổi)");

            new Log(Log.ALER, ip, "LỖI Verify đơn hàng", admin.getIdUser(),
                    "Đơn #" + idInt + " lỗi decrypt: " + e.getMessage(), 1).insert();
        }

        request.setAttribute("verifyResult", result.name()); // "OK" / "TAMPERED" / "ERROR"
        request.setAttribute("id", idInt);
        request.setAttribute("CUSTOMER", customerDAO.findById(idUser));
        request.setAttribute("cart", listDonHang(idInt));
        request.setAttribute("LISTBILL", cartDao.getAllDetailCart(idUser, idInt));
        request.getRequestDispatcher("/views/admin/confirm-order-detail.jsp")
                .forward(request, response);
    }

    private CartModel listDonHang(int id) {
        CartModel cartModel = cartDao.getCartById(id);
        cartModel.setBills(new BillDAO().findAllBillByIdCart(id));
        return cartModel;
    }

    // Enum kết quả verify để JSP dễ xử lý
    public enum VerifyResult { OK, TAMPERED, ERROR }
}