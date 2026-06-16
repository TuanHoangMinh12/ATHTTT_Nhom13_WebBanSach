package vn.edu.hcmuaf.fit.controller.admin.managaementKey;

import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * ════════════════════════════════════════════════════════════════════
 *  SERVLET DEBUG TẠM THỜI — chỉ để tìm nguyên nhân, KHÔNG nộp vào đồ án
 * ════════════════════════════════════════════════════════════════════
 *
 * Mục đích: in ra CHÍNH XÁC chuỗi dùng để hash + hash kết quả cho 1 đơn
 * hàng, để so sánh giữa 2 thời điểm (trước và sau khi bấm "Đăng ký
 * đơn hàng") và biết chính xác ký tự nào bị thay đổi.
 *
 * CÁCH DÙNG:
 *   /admin-debug-hash?idCart=X
 *
 * QUY TRÌNH TEST:
 *   1. Sau khi verify OK, gọi URL này lần 1 → copy lại toàn bộ output
 *   2. Bấm "Đăng ký đơn hàng"
 *   3. Gọi lại URL này lần 2 (cùng idCart) → copy output
 *   4. So sánh 2 đoạn text — chỗ nào khác nhau chính là nguyên nhân
 * ════════════════════════════════════════════════════════════════════
 */
@WebServlet(name = "admin-debug-hash", value = "/admin-debug-hash")
public class DebugHashController extends HttpServlet {

    ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();
    SHA256Util sha256Util = new SHA256Util();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();

        String idCartStr = request.getParameter("idCart");
        String idUserStr = request.getParameter("idUser");

        if (idCartStr == null) {
            out.println("Thiếu param idCart. Dùng: /admin-debug-hash?idCart=7&idUser=3");
            return;
        }

        int idCart = Integer.parseInt(idCartStr);

        // Nếu không truyền idUser, tự lấy từ carts
        int idUser;
        if (idUserStr != null) {
            idUser = Integer.parseInt(idUserStr);
        } else {
            idUser = new vn.edu.hcmuaf.fit.dao.impl.CartDao()
                    .getCartById(idCart).getIdUser();
        }

        out.println("════════════════════════════════════════");
        out.println("idCart = " + idCart + " | idUser = " + idUser);
        out.println("Thời điểm gọi: " + new java.util.Date());
        out.println("════════════════════════════════════════");

        String orderString = objectVerifyUtil.string(idUser, idCart);
        String hash = sha256Util.check(orderString);

        out.println("--- ORDER STRING (dùng để hash) ---");
        out.println(orderString);
        out.println();
        out.println("--- ĐỘ DÀI CHUỖI: " + orderString.length() + " ký tự ---");
        out.println();
        out.println("--- HASH SHA256 ---");
        out.println(hash);
        out.println();
        out.println("--- ĐỘ DÀI HASH: " + hash.length() + " ký tự (cần luôn = 64) ---");

        // Lấy luôn signature hiện có trong DB để đối chiếu
        String signatureInDb = new vn.edu.hcmuaf.fit.dao.impl.CartDao().getHash(idCart, idUser);
        out.println();
        out.println("--- SIGNATURE ĐANG LƯU TRONG carts.verify ---");
        out.println(signatureInDb);
    }
}