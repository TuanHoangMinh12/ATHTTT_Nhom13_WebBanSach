package vn.edu.hcmuaf.edu.vn.controller.admin.managementRate;

import vn.edu.hcmuaf.fit.bean.Log;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.services.IRateManagementService;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;

@WebServlet(name = "findRate", value = "/findRate")
public class FindRateController extends HttpServlet {
    @Inject
    IRateManagementService iRateManagementService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // title dung de active aside
        String id = request.getParameter("id");
        String book = request.getParameter("book");
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        InetAddress myIP=InetAddress.getLocalHost();
        String ip= myIP.getHostAddress();

        iRateManagementService.hiddenRate(id,book);
        Log log = new Log(Log.INFO,ip,"Quản lý đánh giá, bình luận",cus.getIdUser(),"Ẩn bình luận",1);
        log.insert();

        request.setAttribute("message","Ẩn thành công");
        response.sendRedirect(request.getContextPath() + "/admin-manage-rate");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
