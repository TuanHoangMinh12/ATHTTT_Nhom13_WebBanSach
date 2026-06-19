package vn.edu.hcmuaf.fit.controller.admin.managaementKey;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.RSAUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(name = "admin-test-sign-order", value = "/admin-test-sign-order")
public class TestSignOrderController extends HttpServlet {

    CartDao cartDao = new CartDao();
    SHA256Util sha256Util = new SHA256Util();
    ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idCartStr = request.getParameter("idCart");
        if (idCartStr == null) {
            out.println("<h3>Thiếu param idCart. Dùng: /admin-test-sign-order?idCart=1</h3>");
            return;
        }

        int idCart = Integer.parseInt(idCartStr);
        CartModel cart = cartDao.getCartById(idCart);
        if (cart == null) {
            out.println("<h3>Không tìm thấy đơn hàng id=" + idCart + "</h3>");
            return;
        }

        //  BỔ SUNG CHẶN: Nếu đơn hàng đã hủy (inShip == 4) thì chặn đứng, không cho phép ký lại nữa
        if (cart.getInShip() == 4) {
            out.println("<h2 style='color:red'> Không thể ký đơn hàng này!</h2>");
            out.println("<p style='font-size:16px;'><b>Lý do bảo mật:</b> Đơn hàng #" + idCart + " đã bị <b>HỦY (hoặc Khóa do chỉnh sửa trái phép)</b>. Theo nguyên tắc chống trối bỏ, không được phép tái ký đơn hàng đã đóng lịch sử. Vui lòng tạo một đơn đặt hàng mới!</p>");
            out.println("<p><a href='" + request.getContextPath() + "/admin-table-order'>Quay lại danh sách đơn hàng</a></p>");
            return;
        }

        int idUser = cart.getIdUser();

        try {
            RSAUtil rsa = new RSAUtil();
            rsa.genKey();
            String publicKeyStr = rsa.getPublicKeyAsString();

            insertPublicKeyBeforeCart(idUser, publicKeyStr);

            String orderString = objectVerifyUtil.string(idUser, idCart);
            String hash = sha256Util.check(orderString);
            String signature = rsa.encrypt(hash);

            cartDao.updateVerify(idCart, signature);

            out.println("<h2 style='color:green'> Đã ký đơn hàng #" + idCart + " thành công</h2>");
            out.println("<p><b>idUser:</b> " + idUser + "</p>");
            out.println("<p><b>Order string (dùng để hash):</b><br><code style='word-break:break-all'>"
                    + orderString + "</code></p>");
            out.println("<p><b>Hash (SHA256):</b><br><code>" + hash + "</code></p>");
            out.println("<p><b>Signature (đã lưu vào carts.verify):</b><br>"
                    + "<code style='word-break:break-all;font-size:11px'>" + signature + "</code></p>");
            out.println("<hr>");
            out.println("<p>Giờ vào <a href='" + request.getContextPath()
                    + "/admin-order-detail?id=" + idCart + "'>chi tiết đơn hàng #" + idCart
                    + "</a> và nhấn <b>[Verify]</b> để test.</p>");

        } catch (Exception e) {
            out.println("<h3 style='color:red'>Lỗi: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }

    private void insertPublicKeyBeforeCart(int idUser, String publicKey) throws SQLException {
        // Sử dụng hoàn toàn thời gian thực tại của hệ thống (NOW())
        // và ngày hết hạn là 1 năm sau kể từ thời điểm tạo khóa.
        String insertSql = "INSERT INTO public_key (id_user, public_Key, status, create_date, expire) " +
                "VALUES (?, ?, 1, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR))";

        try (Connection conn = vn.edu.hcmuaf.fit.db.JDBCConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSql)) {

            stmt.setInt(1, idUser);
            stmt.setString(2, publicKey);

            stmt.executeUpdate();
        }
    }}