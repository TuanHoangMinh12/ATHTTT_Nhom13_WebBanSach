package vn.edu.hcmuaf.fit.controller.admin.managementRate;

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

@WebServlet(name = "findRateAvtivity", value = "/findRateAvtivity")
public class ActivityRateController extends HttpServlet {
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

        iRateManagementService.activityRate(id,book);
        Log log = new Log(Log.INFO,ip,"Quản lý đánh giá, bình luận",cus.getIdUser(),"Hiện bình luận",1);
        log.insert();
        request.setAttribute("message","Hiện thành công");
        response.sendRedirect(request.getContextPath() + "/admin-manage-rate");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
