package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.CustomerDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.utils.RSAUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "admin-order-detail", value = "/admin-order-detail")
public class OrderDetailController extends HttpServlet {

    CartDao cartDao = new CartDao();
    BillDAO billDAO = new BillDAO();
    CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int idInt = Integer.parseInt(request.getParameter("id"));
        CartModel cartModel = cartDao.getCartById(idInt);

        if (cartModel != null) {
            // Lấy ID người dùng thực tế được lưu kèm giỏ hàng trong DB công khai
            int idUser = cartModel.getIdUser();

            // TỰ ĐỘNG CHẠY HÀM VERIFY KHI ADMIN VỪA MỞ CHI TIẾT
            String verifyResult = RSAUtil.autoVerifyOrder(idInt, idUser);

            if (verifyResult == null) {
                request.setAttribute("verifyResult", "CHUA_VERIFY");
            } else {
                request.setAttribute("verifyResult", verifyResult); // Trả về "OK" hoặc "FAIL"
            }

            System.out.println("Kết quả tự động Verify đơn hàng #" + idInt + ": " + verifyResult);
            setCommonAttributes(request, idInt, cartModel);
        } else {
            request.setAttribute("verifyResult", "ERROR");
            request.setAttribute("verifyError", "Không tìm thấy dữ liệu đơn hàng hợp lệ trên hệ thống.");
        }

        request.getRequestDispatcher("/views/admin/confirm-order-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Đồng bộ hoàn toàn luồng POST về luồng GET tự động hóa
        doGet(request, response);
    }

    private void setCommonAttributes(HttpServletRequest request, int idInt, CartModel cartModel) {
        request.setAttribute("id", idInt);
        request.setAttribute("CUSTOMER", customerDAO.findById(cartModel.getIdUser()));

        // Nạp danh sách hóa đơn chi tiết đi kèm giỏ hàng
        cartModel.setBills(billDAO.findAllBillByIdCart(idInt));
        request.setAttribute("cart", cartModel);
        request.setAttribute("LISTBILL", cartDao.getAllDetailCart(cartModel.getIdUser(), idInt));
    }
}