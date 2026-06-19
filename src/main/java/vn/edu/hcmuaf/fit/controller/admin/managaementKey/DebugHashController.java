package vn.edu.hcmuaf.fit.controller.admin.managaementKey;

import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

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