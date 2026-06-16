package vn.edu.hcmuaf.fit.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.bean.Log;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.InformationDeliverDao;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.InformationDeliverModel;
import vn.edu.hcmuaf.fit.services.IBillManagementService;
import vn.edu.hcmuaf.fit.utils.FeeGHNUtils;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;
@WebServlet(name = "admin-register-order", value = "/admin-register-order")
public class RegisterOrderController extends HttpServlet {
    @Inject
    IBillManagementService iBillManagementService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setCharacterEncoding("utf-8");
//        String id = request.getParameter("id");
//        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
//        InetAddress myIP=InetAddress.getLocalHost();
//        String ip= myIP.getHostAddress();
//        CartDao cartDao = new CartDao();
//        String idCus = request.getParameter("variable");
//
//        if(id != null) {
//            int idInt = Integer.parseInt(id);
//            int idCusInt = Integer.parseInt(idCus);
//
//            InformationDeliverDao daoInFo = new InformationDeliverDao();
//            InformationDeliverModel info = daoInFo.getById(idInt);
//            daoInFo.updateToken(idInt,FeeGHNUtils.registerShipForDeliver(info.getX()+"", info.getY()+"", info.getZ()+"", info.getW()+"",1463,21808,info.getDistrictTo(), info.getWarTo()));
//            iBillManagementService.confirmBill(id);
//            cartDao.updateCart(idInt, 2);
//            Log log = new Log(Log.ALER,ip,"Đăng kí đơn hàng",cus.getIdUser(),"Đăng kí đơn hàng vận chuyển: " + id,1);
//            log.insert();
//            request.setAttribute("message", "Đăng kí đơn hàng thành công: " + id);
//            request.setAttribute("alert", "success");
//            response.sendRedirect(request.getContextPath() + "/admin-order-detail?id="+id);
//        }
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String id = request.getParameter("id");
        String idCus = request.getParameter("variable");
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        String ip = request.getRemoteAddr();

        if (id != null) {
            int idInt = Integer.parseInt(id);
            CartDao cartDao = new CartDao();
            InformationDeliverDao daoInFo = new InformationDeliverDao();

            // 1. Cập nhật trạng thái tạm thời trong DB ngay lập tức (Để chặn bấm trùng)
            cartDao.updateCart(idInt, 5);

            // 2. Đẩy tác vụ gọi API GHN vào luồng ngầm (Async Thread)
            CompletableFuture.runAsync(() -> {
                try {
                    InformationDeliverModel info = daoInFo.getById(idInt);

                    // Gọi API GHN tốn thời gian ở đây (chạy ngầm)
                    String ghnToken = FeeGHNUtils.registerShipForDeliver(
                            info.getX()+"", info.getY()+"", info.getZ()+"", info.getW()+"",
                            1463, 21808, info.getDistrictTo(), info.getWarTo()
                    );

                    // Sau khi GHN trả kết quả, cập nhật hệ thống
                    daoInFo.updateToken(idInt, ghnToken);
                    iBillManagementService.confirmBill(id);

                    // Cập nhật trạng thái chính thức: Đang vận chuyển (Số 2)
                    cartDao.updateCart(idInt, 2);

                    // Ghi log thành công
                    Log log = new Log(Log.ALER, ip, "Đăng kí đơn hàng", cus.getIdUser(), "Đăng kí đơn hàng vận chuyển thành công: " + id, 1);
                    log.insert();

                } catch (Exception e) {
                    // Nếu lỗi, trả về "Chờ xử lý" (Số 1) để Admin bấm lại sau
                    cartDao.updateCart(idInt, 1);
                    e.printStackTrace();
                }
            });

            // 3. TRẢ VỀ PHẢN HỒI LẬP TỨC CHO AJAX (Mất 0ms)
            response.setContentType("text/plain");
            response.getWriter().write("processing");
        }
    }
}