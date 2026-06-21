package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao;
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
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@WebServlet(name = "admin-table-order", value = "/admin-table-order")
public class TableOrderController extends HttpServlet {

    @Inject
    IBillManagementService iBillManagementService;

    CartDao cartDao = new CartDao();
    SHA256Util sha256Util = new SHA256Util();
    ObjectVerifyUtil objUtil = new ObjectVerifyUtil();
    PublicKeyDao publicKeyDao = new PublicKeyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setAttribute("title", "Quản Lý Đơn Hàng");

        String message = request.getParameter("message");
        String alert   = request.getParameter("alert");
        if (message != null && alert != null) {
            request.setAttribute("message", message);
            request.setAttribute("alert", alert);
        }

        // Giữ nguyên lấy từ Cart gốc từ database theo ảnh DB của bạn
        List<CartModel> rawCarts = cartDao.getAllCart();
        List<CartModel> finalizedCarts = attachOrderMetaData(rawCarts);

        request.setAttribute("listBill", finalizedCarts);
        request.getRequestDispatcher("/views/admin/table-data-order.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private List<CartModel> attachOrderMetaData(List<CartModel> list) {
        if (list == null) return new ArrayList<>();

        BillDAO billDAO = new BillDAO();
        for (CartModel cart : list) {
            int idCart = cart.getId();
            int idUser = cart.getIdUser();

            cart.setBills(billDAO.findAllBillByIdCart(idCart));

            // 1. Chạy tự động verify đơn hàng
            String verifyStatus = RSAUtil.autoVerifyOrder(idCart, idUser);
            cart.setVerifyStatus(verifyStatus);

            // 2. ĐỒNG BỘ NGAY TRÊN GIAO DIỆN: Nếu chữ ký giả mạo, gán tạm trạng thái hiển thị là 4
            if ("FAIL".equals(verifyStatus)) {
                cart.setInShip(4);
            }
        }
        return list;
    }

//    private String calcVerifyStatus(int idCart, int idUser) {
//        try {
//            String signature = cartDao.getHash(idCart, idUser);
//            String publicKey = cartDao.getPuclickey(idUser, idCart);
//
//            // Nếu đơn hàng chưa được ký số (cột verify trong DB null) -> Trả về trạng thái "Chưa verify"
//            if (signature == null || signature.trim().isEmpty() || "NULL".equalsIgnoreCase(signature)) {
//                return "CHUA_VERIFY";
//            }
//            if (publicKey == null) return "FAIL";
//
//            // Kiểm tra trạng thái thu hồi khóa (Revoke) của Public Key
//            boolean isKeyRevoked = publicKeyDao.getAllKeys().stream()
//                    .anyMatch(key -> key.getIdUser() == idUser
//                            && publicKey.contains(key.getPublicKey())
//                            && key.getStatus() == 0);
//
//            if (isKeyRevoked) {
//                return "FAIL";
//            }
//
//            // Sinh chuỗi hash MD5/SHA256 thô hiển thị trên màn hình sign-order.jsp để đối chiếu
//            String orderString = objUtil.string(idUser, idCart);
//            String hash1 = sha256Util.check(orderString);
//
//            // Tiến hành Verify chữ ký số chuẩn thuật toán SHA256withRSA
//            boolean isValid = verifySignature(publicKey, hash1, signature);
//
//            return isValid ? "OK" : "FAIL";
//
//        } catch (Exception e) {
//            return "FAIL";
//        }
//    }

//    private boolean verifySignature(String publicKeyStr, String plaintextData, String signatureStr) {
//        try {
//            String cleanPublicKey = publicKeyStr.trim().replace("\r\n", "").replace("\n", "");
//            byte[] publicBytes = Base64.getDecoder().decode(cleanPublicKey);
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
//            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
//            PublicKey publicKey = keyFactory.generatePublic(keySpec);
//
//            String cleanSignature = signatureStr.trim().replace("\r\n", "").replace("\n", "");
//            byte[] signatureBytes = Base64.getDecoder().decode(cleanSignature);
//
//            Signature sig = Signature.getInstance("SHA256withRSA");
//            sig.initVerify(publicKey);
//            sig.update(plaintextData.getBytes("UTF-8"));
//
//            return sig.verify(signatureBytes);
//        } catch (Exception e) {
//            System.err.println("Lỗi verify signature: " + e.getMessage());
//            return false;
//        }
//    }
}