package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.bean.Log;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.services.IBillManagementService;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "removerBill", value = "/removerBill")
public class RemoveBillController extends HttpServlet {
    @Inject
    IBillManagementService iBillManagementService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Chuyển bảo mật: Không cho phép gọi qua thẻ <a> GET nữa
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String id = request.getParameter("id");
        String ip = request.getRemoteAddr();
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        CartDao cartDao = new CartDao();

        if(id != null) {
            try {
                // 1. Cập nhật shipping_info = 4 trong bảng bill theo idCart
                iBillManagementService.deleteBill(id);

                // 2. Cập nhật trạng thái = 4 trong bảng carts để đồng bộ giao diện
                int idInt = Integer.parseInt(id);
                cartDao.updateCart(idInt, 4);

                // 3. Ghi Log lịch sử hệ thống
                Log log = new Log(Log.ALER, ip, "Xác nhận đơn hàng", cus.getIdUser(), "Hủy đơn hàng thành công: " + id, 1);
                log.insert();

                // Trả kết quả chữ về cho AJAX
                response.setContentType("text/plain");
                response.getWriter().write("success");
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Lỗi: " + e.getMessage());
            }
        }
    }
}