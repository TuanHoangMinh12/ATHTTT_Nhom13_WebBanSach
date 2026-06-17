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

/**
 * TASK 2 — TableOrderController (bản cập nhật)
 * Đặt file vào: src/main/java/vn/edu/hcmuaf/fit/controller/admin/managementOrder/TableOrderController.java
 *
 * QUY TẮC:
 * 1. Mọi đơn hàng (bất kể trạng thái vận chuyển) đều được verify lại
 *    mỗi lần load trang danh sách, để badge "Xác thực chữ ký" luôn
 *    phản ánh đúng tình trạng dữ liệu hiện tại trong DB.
 *
 * 2. CHỈ tự động chuyển infoShip → 4 (Đã hủy) khi:
 *      verify ra FAIL  VÀ  đơn đang ở trạng thái 1 (Chờ xử lý)
 *    Vì đơn ở trạng thái 1 chưa thực sự được giao đi ngoài đời thật,
 *    nên hủy là hợp lý khi phát hiện dữ liệu bị thay đổi.
 *
 * 3. Nếu đơn đang ở trạng thái 2 (Đang vận chuyển) hoặc 3 (Đã giao) mà
 *    verify ra FAIL, hệ thống CHỈ báo "Invalid" trên badge xác thực
 *    chữ ký, KHÔNG tự đổi trạng thái vận chuyển — vì đơn đã thực sự
 *    được giao/đang giao ngoài đời thật, đổi trạng thái lúc này không
 *    còn ý nghĩa nghiệp vụ (sách đã/đang ở tay đơn vị vận chuyển hoặc
 *    khách hàng). Admin xem badge Invalid để biết cần kiểm tra thủ công.
 *
 * 4. Đơn đã ở trạng thái 4 (Đã hủy) vẫn tiếp tục verify lại để badge
 *    phản ánh đúng, nhưng không cần update lại DB (đã là 4 sẵn).
 */
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Lấy danh sách đơn hàng, gắn verifyStatus cho MỌI đơn (bất kể trạng
     * thái vận chuyển), và chỉ tự động hủy khi đơn đang ở trạng thái
     * "Chờ xử lý" (1) và bị phát hiện sửa đổi.
     */
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

    /**
     * Chạy lại logic verify() — giống OrderDetailController.
     * Trả về "OK", "FAIL", hoặc null nếu chưa có dữ liệu / lỗi.
     */
    // ── TRONG FILE TableOrderController.java ──

// ── TRONG FILE TableOrderController.java ──
    private String calcVerifyStatus(int idCart, int idUser) {
        try {
            String signature = cartDao.getHash(idCart, idUser); //
            String publicKey = cartDao.getPuclickey(idUser, idCart); //

            if (signature == null || publicKey == null) return null; //

            // Kiểm tra xem khóa công khai này có bị Admin bấm thu hồi (Revoke) trước đó hay không
            boolean isKeyRevoked = publicKeyDao.getAllKeys().stream()
                    .anyMatch(key -> key.getIdUser() == idUser
                            && publicKey.contains(key.getPublicKey())
                            && key.getStatus() == 0);

            if (isKeyRevoked) {
                return "FAIL"; // Khóa chết thì báo Invalid luôn
            }

            // Tính toán hash tự nhiên theo dữ liệu DB hiện tại
            String orderString = objUtil.string(idUser, idCart); //
            String hash1 = sha256Util.check(orderString); //

            RSAUtil rsaUtil = new RSAUtil(); //
            rsaUtil.setPublicKey(publicKey); //
            String hash2 = rsaUtil.decrypt(signature); //

            // Trả về kết quả thật (OK hoặc FAIL) không can thiệp ép uổng
            return hash1.equals(hash2) ? "OK" : "FAIL"; //

        } catch (Exception e) {
            return "FAIL";
        }
    }
}