package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.services.IBillManagementService;
import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.RSAUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * TASK 2 — TableOrderController (bản cập nhật)
 * Đặt file vào: src/main/java/vn/edu/hcmuaf/fit/controller/admin/managementOrder/TableOrderController.java
 *
 * CẬP NHẬT: mỗi lần load trang danh sách, hệ thống tự verify NGẦM cho
 * từng đơn. Nếu phát hiện hash không khớp (FAIL) VÀ đơn đó chưa bị hủy
 * trước đó, hệ thống tự động set inShip = 4 (Đã hủy) ngay tại đây —
 * không cần admin vào trang chi tiết bấm [Verify] mới đổi trạng thái.
 */
@WebServlet(name = "admin-table-order", value = "/admin-table-order")
public class TableOrderController extends HttpServlet {

    @Inject
    IBillManagementService iBillManagementService;

    CartDao cartDao          = new CartDao();
    SHA256Util sha256Util    = new SHA256Util();
    RSAUtil rsaUtil          = new RSAUtil();
    ObjectVerifyUtil objUtil = new ObjectVerifyUtil();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("title", "Danh Sách Đơn Hàng");

        String message = request.getParameter("message");
        String alert   = request.getParameter("alert");
        if (message != null && alert != null) {
            request.setAttribute("message", message);
            request.setAttribute("alert", alert);
        }

        request.setAttribute("listBill", buildOrderList());
        request.getRequestDispatcher("views/admin/table-data-order.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Lấy danh sách đơn hàng, gắn verifyStatus, và TỰ ĐỘNG HỦY đơn
     * nếu phát hiện bị chỉnh sửa (FAIL).
     */
    private List<CartModel> buildOrderList() {
        List<CartModel> list = cartDao.getAllCart();
        for (CartModel cart : list) {
            // Gắn danh sách bill (giữ nguyên logic cũ)
            cart.setBills(new BillDAO().findAllBillByIdCart(cart.getId()));

            // Tính verifyStatus
            String verifyStatus = calcVerifyStatus(cart.getId(), cart.getIdUser());

            // ── TỰ ĐỘNG HỦY nếu phát hiện bị sửa ────────────────────────
            // Chỉ hủy nếu: verify ra FAIL  VÀ  đơn chưa ở trạng thái hủy (4)
            // VÀ đơn chưa hoàn thành (3) — tránh hủy nhầm đơn đã giao xong.
            if ("FAIL".equals(verifyStatus) && cart.getInShip() != 4) {
                cartDao.updateCart(cart.getId(), 4);
                cart.setInShip(4);   // cập nhật luôn object đang hiển thị,
                // để KHÔNG cần load lại trang mới thấy badge đổi
            }

            cart.setVerifyStatus(verifyStatus);
        }
        return list;
    }

    /**
     * Chạy lại logic verify() — giống OrderDetailController.
     * Trả về "OK", "FAIL", hoặc null nếu chưa có dữ liệu / lỗi.
     */
    private String calcVerifyStatus(int idCart, int idUser) {
        try {
            String signature = cartDao.getHash(idCart, idUser);
            String publicKey = cartDao.getPuclickey(idUser, idCart);

            // Nếu chưa có chữ ký → chưa verify
            if (signature == null || publicKey == null) return null;

            String orderString = objUtil.string(idUser, idCart);
            String hash1 = sha256Util.check(orderString);

            rsaUtil.setPublicKey(publicKey);
            String hash2 = rsaUtil.decrypt(signature);

            return hash1.equals(hash2) ? "OK" : "FAIL";

        } catch (Exception e) {
            // Lỗi decrypt hoặc key không hợp lệ → coi như chưa verify được
            return null;
        }
    }
}