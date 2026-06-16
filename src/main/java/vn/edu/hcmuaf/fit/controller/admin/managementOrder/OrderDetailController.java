package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.CustomerDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.RSAUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * GET  → hiển thị chi tiết đơn hàng, chưa verify
 * POST → nhấn nút [Verify] → chạy verify() → set verifyResult → forward JSP
 */
@WebServlet(name = "admin-order-detail", value = "/admin-order-detail")
public class OrderDetailController extends HttpServlet {

    CartDao cartDao = new CartDao();
    BillDAO billDAO = new BillDAO();
    CustomerDAO customerDAO = new CustomerDAO();
    SHA256Util sha256Util = new SHA256Util();
    RSAUtil rsa = new RSAUtil();
    ObjectVerifyUtil objectVerify = new ObjectVerifyUtil();

    // ── GET: chỉ hiển thị, không verify
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idInt = parseId(request);
        setCommonAttributes(request, idInt);
        // verifyResult không set → JSP hiển thị nút Verify bình thường
        forward(request, response);
    }

    // ── POST: nhấn nút [Verify]
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idInt = parseId(request);
        CartModel cartModel = cartDao.getCartById(idInt);
        int idUser = cartModel.getIdUser();

        // Lấy dữ liệu cần thiết
        String publicKey = cartDao.getPuclickey(idUser, idInt);   // public key của user
        String signature = cartDao.getHash(idInt, idUser);        // chữ ký đã lưu khi đặt hàng
        String orderNow = objectVerify.string(idUser, idInt);    // dữ liệu DB hiện tại
        String hash1 = sha256Util.check(orderNow);            // hash của dữ liệu hiện tại

        if (publicKey == null || signature == null) {
            // Chưa có chữ ký → không thể verify
            request.setAttribute("verifyResult", "ERROR");
            request.setAttribute("verifyError", "Đơn hàng này chưa có chữ ký số (thiếu public key hoặc signature).");
        } else {
            try {
                rsa.setPublicKey(publicKey);
                String hash2 = rsa.decrypt(signature);  // hash gốc lúc user ký

                if (hash1.equals(hash2)) {
                    request.setAttribute("verifyResult", "OK");
                } else {
                    // verify() == false → Dữ liệu đơn hàng trong DB đã bị can thiệp, chỉnh sửa!
                    request.setAttribute("verifyResult", "FAIL");

                    // KIỂM TRA ĐIỀU KIỆN NGOẠI LỆ:
                    // Nếu trạng thái giỏ hàng hiện tại KHÁC 3 (Chưa giao thành công) thì mới thực hiện hủy đơn tự động
                    if (cartModel.getInShip() != 3) {
                        cartDao.updateCart(idInt, 4); // Tự động hủy đơn hàng bị giả mạo (infoShip = 4)

                        // Cập nhật lại model để giao diện hiển thị đúng trạng thái "Đã hủy" ngay lập tức sau khi reload
                        cartModel.setInShip(4);
                    } else {
                        // Đơn hàng đã giao thành công (infoShip == 3) -> Chỉ báo Invalid (FAIL) trên giao diện, KHÔNG hạ trạng thái xuống hủy
                        System.out.println("Cảnh báo bảo mật: Đơn hàng #" + idInt + " đã hoàn thành nhưng phát hiện có sự thay đổi dữ liệu trong DB!");
                    }
                }
            } catch (Exception e) {
                request.setAttribute("verifyResult", "ERROR");
                request.setAttribute("verifyError", "Lỗi khi giải mã chữ ký: " + e.getMessage());
            }
        }
    }

    // ── Helper: set các attribute dùng chung cho GET và POST ─────────────
    private void setCommonAttributes(HttpServletRequest request, int idInt) {
        CartModel cartModel = cartDao.getCartById(idInt);
        request.setAttribute("id", idInt);
        request.setAttribute("CUSTOMER", customerDAO.findById(cartModel.getIdUser()));
        request.setAttribute("cart", buildCart(idInt));
        request.setAttribute("LISTBILL", cartDao.getAllDetailCart(cartModel.getIdUser(), idInt));
    }

    private void forward(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.getRequestDispatcher("/views/admin/confirm-order-detail.jsp").forward(req, res);
    }

    private int parseId(HttpServletRequest request) {
//        return Integer.parseInt(request.getParameter("id"));
        String idStr = request.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            // Nếu trên URL không có, thử lấy từ attribute (do filter/servlet trước set)
            idStr = (String) request.getAttribute("id");
        }
        return Integer.parseInt(idStr.trim());
    }

    private CartModel buildCart(int id) {
        CartModel cart = cartDao.getCartById(id);
        cart.setBills(billDAO.findAllBillByIdCart(id));
        return cart;
    }
}