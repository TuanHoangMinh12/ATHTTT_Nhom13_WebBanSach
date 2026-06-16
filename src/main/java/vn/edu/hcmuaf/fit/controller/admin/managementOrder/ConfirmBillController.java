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

@WebServlet(name = "confirmBill", value = "/confirmBill")
public class ConfirmBillController extends HttpServlet {
    @Inject
    IBillManagementService iBillManagementService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        request.setCharacterEncoding("utf-8");
//        response.setCharacterEncoding("utf-8");
//        String id = request.getParameter("id");
//        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
//        InetAddress myIP=InetAddress.getLocalHost();
//        String ip= myIP.getHostAddress();
//
//        CartDao cartDao = new CartDao();
//        String idCus = request.getParameter("variable");
//
//        if(id != null) {
//            int idInt = Integer.parseInt(id);
//            int idCusInt = Integer.parseInt(idCus);
//
//            InformationDeliverDao daoInFo = new InformationDeliverDao();
//            InformationDeliverModel info = daoInFo.getById(idInt);
//            daoInFo.updateToken(idInt, FeeGHNUtils.registerShipForDeliver(info.getX()+"", info.getY()+"", info.getZ()+"", info.getW()+"",1463,21808,info.getDistrictTo(), info.getWarTo()));
//            iBillManagementService.confirmBill(id);
//            cartDao.updateCart(idInt, 3);
//            Log log = new Log(Log.ALER,ip,"Xác nhận đơn hàng",cus.getIdUser(),"Đã giao: " + id,1);
//            log.insert();
//            request.setAttribute("message", "Xác nhận đơn hàng thành công: " + id);
//            request.setAttribute("alert", "success");
//            response.sendRedirect(request.getContextPath() + "/admin-order-detail?id="+id);
//        }
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String id = request.getParameter("id");
        String idCus = request.getParameter("variable");
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");

        // TỐI ƯU: Thay đổi lấy IP trực tiếp từ request để tránh bị nghẽn mạng
        String ip = request.getRemoteAddr();

        CartDao cartDao = new CartDao();

        if(id != null) {
            int idInt = Integer.parseInt(id);

            // Chuyển trực tiếp trạng thái sang 3 (Đã hoàn thành / Đã giao)
            cartDao.updateCart(idInt, 3);

            Log log = new Log(Log.ALER, ip, "Xác nhận đơn hàng", cus.getIdUser(), "Đã giao thành công đơn hàng: " + id, 1);
            log.insert();

            // Trả kết quả về cho AJAX nhận biết
            response.setContentType("text/plain");
            response.getWriter().write("success");
        }
    }
}