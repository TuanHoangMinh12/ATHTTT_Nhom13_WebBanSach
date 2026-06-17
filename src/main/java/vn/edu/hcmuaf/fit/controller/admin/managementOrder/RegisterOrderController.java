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

            try {
                // Chuyển trạng thái giỏ hàng sang 2 (Đang vận chuyển) ngay tại luồng chính.
                cartDao.updateCart(idInt, 2);

                // Đẩy tác vụ gọi API GHN (tốn thời gian) chạy ngầm ở luồng phụ
                CompletableFuture.runAsync(() -> {
                    try {
                        InformationDeliverModel info = daoInFo.getById(idInt);

                        // Gọi API GHN tốn thời gian ở đây
                        String ghnToken = FeeGHNUtils.registerShipForDeliver(
                                info.getX()+"", info.getY()+"", info.getZ()+"", info.getW()+"",
                                1463, 21808, info.getDistrictTo(), info.getWarTo()
                        );

                        // Sau khi GHN trả kết quả, cập nhật mã vận đơn và bảng bill
                        daoInFo.updateToken(idInt, ghnToken);
                        iBillManagementService.confirmBill(id);

                        // Ghi log lịch sử hệ thống thành công
                        Log log = new Log(Log.ALER, ip, "Đăng kí đơn hàng", cus.getIdUser(), "Đăng kí đơn hàng vận chuyển thành công: " + id, 1);
                        log.insert();

                    } catch (Exception e) {
                        e.printStackTrace();
                        // Logic ngoại lệ: Nếu API GHN thất bại (sai địa chỉ, mất mạng),
                        // luồng ngầm mới trả trạng thái về Chờ xử lý (1) để Admin có thể bấm đăng ký lại.
                        cartDao.updateCart(idInt, 1);
                    }
                });

                // TRẢ VỀ PHẢN HỒI LẬP TỨC CHO AJAX (Mất 0ms)
                response.setContentType("text/plain");
                response.getWriter().write("processing");

            } catch (Exception ex) {
                ex.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Lỗi hệ thống: " + ex.getMessage());
            }
        }
    }}