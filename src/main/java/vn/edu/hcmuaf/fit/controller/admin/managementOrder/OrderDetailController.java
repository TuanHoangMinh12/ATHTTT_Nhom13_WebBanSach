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

@WebServlet(name = "admin-order-detail", value = "/admin-order-detail")
public class OrderDetailController extends HttpServlet {

    CartDao cartDao = new CartDao();
    BillDAO billDAO = new BillDAO();
    CustomerDAO customerDAO = new CustomerDAO();
    SHA256Util sha256Util = new SHA256Util();
    ObjectVerifyUtil objectVerify = new ObjectVerifyUtil();

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
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        int idInt = parseId(request);
        CartModel cartModel = cartDao.getCartById(idInt);
        int idUser = cartModel.getIdUser();

        // Lấy dữ liệu phục vụ Verify
        String publicKey = cartDao.getPuclickey(idUser, idInt);
        String signature = cartDao.getHash(idInt, idUser);
        String orderNow = objectVerify.string(idUser, idInt);
        String hash1 = sha256Util.check(orderNow);

        if (publicKey == null || signature == null) {
            request.setAttribute("verifyResult", "ERROR");
            request.setAttribute("verifyError", "Đơn hàng này chưa có chữ ký số (thiếu public key hoặc signature).");
        } else {
            try {
                RSAUtil rsa = new RSAUtil();
                rsa.setPublicKey(publicKey);
                String hash2 = rsa.decrypt(signature);

                // 1. Chạy so sánh hash tự nhiên của đơn hàng
                if (hash1.equals(hash2)) {
                    request.setAttribute("verifyResult", "OK");
                } else {
                    // Nếu bị sửa, hiển thị đúng badge FAIL (Invalid) lên màn hình để Cảnh báo
                    request.setAttribute("verifyResult", "FAIL");

                    // 2. CHỈ tự động hủy sang 4 nếu đơn hàng đang ở trạng thái Chờ xử lý (1)
                    // Nếu đang giao (2) hoặc đã giao (3), giữ nguyên tình trạng vận chuyển của DB, để yên nút bấm!
                    if (cartModel.getInShip() == 1) {
                        cartDao.updateCart(idInt, 4);
                    }
                }
            } catch (Exception e) {
                request.setAttribute("verifyResult", "ERROR");
                request.setAttribute("verifyError", "Lỗi khi giải mã chữ ký: " + e.getMessage());
            }
        }

        setCommonAttributes(request, idInt);
        forward(request, response);
    }

    // ── Helper: set các attribute dùng chung cho GET và POST
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
        return Integer.parseInt(request.getParameter("id"));
    }

    private CartModel buildCart(int id) {
        CartModel cart = cartDao.getCartById(id);
        cart.setBills(billDAO.findAllBillByIdCart(id));
        return cart;
    }
}