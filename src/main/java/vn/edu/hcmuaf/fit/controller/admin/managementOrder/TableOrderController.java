package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;
import vn.edu.hcmuaf.fit.model.PublicKeyModel;
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
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "admin-table-order", value = "/admin-table-order")
public class TableOrderController extends HttpServlet {

    @Inject
    IBillManagementService iBillManagementService;

    CartDao cartDao= new CartDao();
    SHA256Util sha256Util = new SHA256Util();
    ObjectVerifyUtil objUtil = new ObjectVerifyUtil();
    PublicKeyDao publicKeyDao = new PublicKeyDao();

    // KHÔNG khai báo RSAUtil ở đây — tạo mới bên trong calcVerifyStatus()
    // cho từng đơn, tránh nhiều request/nhiều vòng lặp dùng chung field
    // publicKey của 1 object RSAUtil (gây verify nhầm key giữa các đơn).

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    private List<CartModel> buildOrderList() {
        List<CartModel> list = cartDao.getAllCart();
        for (CartModel cart : list) {
            // Gắn danh sách bill (giữ nguyên logic cũ)
            cart.setBills(new BillDAO().findAllBillByIdCart(cart.getId()));

            // Verify lại CHO MỌI đơn, không phân biệt trạng thái vận chuyển
            String verifyStatus = calcVerifyStatus(cart.getId(), cart.getIdUser());
            cart.setVerifyStatus(verifyStatus);

            // ── TỰ ĐỘNG HỦY: chỉ áp dụng khi đơn đang "Chờ xử lý" (1) ───
            // Đơn đang vận chuyển (2) hoặc đã giao (3) KHÔNG bị đổi trạng
            // thái dù verify FAIL — chỉ badge Invalid cảnh báo cho admin.
            if ("FAIL".equals(verifyStatus) && cart.getInShip() == 1) {
                cartDao.updateCart(cart.getId(), 4); // Tự động đổi infoShip sang 4 (Đã hủy) trong DB
                cart.setInShip(4);                   // Đổi trạng thái hiển thị tại chỗ
            }
        }
        return list;
    }

    private String calcVerifyStatus(int idCart, int idUser) {
        try {
            String signature = cartDao.getHash(idCart, idUser);
            String publicKey = cartDao.getPuclickey(idUser, idCart);

            if (signature == null || publicKey == null) return null;

            // Kiểm tra xem khóa công khai này có bị Admin bấm thu hồi (Revoke) trước đó hay không
            boolean isKeyRevoked = publicKeyDao.getAllKeys().stream()
                    .anyMatch(key -> key.getIdUser() == idUser
                            && publicKey.contains(key.getPublicKey())
                            && key.getStatus() == 0);

            if (isKeyRevoked) {
                return "FAIL"; // Khóa chết thì báo Invalid luôn
            }

            // Tính toán hash tự nhiên theo dữ liệu DB hiện tại
            String orderString = objUtil.string(idUser, idCart);
            String hash1 = sha256Util.check(orderString);

            RSAUtil rsaUtil = new RSAUtil();
            rsaUtil.setPublicKey(publicKey);
            String hash2 = rsaUtil.decrypt(signature);

            // Trả về kết quả thật (OK hoặc FAIL) không can thiệp ép uổng
            return hash1.equals(hash2) ? "OK" : "FAIL";

        } catch (Exception e) {
            return "FAIL";
        }
    }
}