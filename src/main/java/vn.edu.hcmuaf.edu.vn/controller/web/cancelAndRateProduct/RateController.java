package vn.edu.hcmuaf.edu.vn.controller.web.cancelAndRateProduct;

import vn.edu.hcmuaf.fit.bean.Log;
import vn.edu.hcmuaf.fit.dao.IBillDAO;
import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;


@WebServlet(name = "rate", value = "/rate")
public class RateController extends HttpServlet {
    IBillDAO iBillDAO = new BillDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        String idBook = request.getParameter("idBook");

        if(id != null) {
            request.setAttribute("idOrder", id);
            request.setAttribute("idBook", idBook);
        }
        request.getRequestDispatcher("views/web/rate.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String comment = request.getParameter("comment");
        String idBill = request.getParameter("idBill");
        String idBook = request.getParameter("idBook");
        String numberStar =  request.getParameter("numberStar");
        int number = Integer.parseInt(numberStar);
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        InetAddress myIP=InetAddress.getLocalHost();
        String ip= myIP.getHostAddress();
        if(comment != null && idBill != null && idBook != null) {
            int idBookInt = Integer.parseInt(idBook);
            int idOrderInt = Integer.parseInt(idBill);
            int rate = iBillDAO.rateBook(cus.getIdUser(), idBookInt, idOrderInt, number, comment);
            String message;
            String messageType;
            if(rate >=1) {
                Log log = new Log(Log.INFO,ip,"Đánh giá sản phẩm",cus.getIdUser(),"Đánh giá sản phẩm: "+ idBook,1);
                log.insert();
                message = "Đánh giá thành công";
                messageType = "success";
//                new MessageParameterUntil("Hủy đơn hàng thành công", "success", "/views/web/reviewOrders.jsp", request, response).send();

            }else {
                message = "Đánh giá thất bại";
                messageType = "danger";
            }
            request.setAttribute("message", message);
            request.setAttribute("alert", messageType);

            response.sendRedirect(request.getContextPath()+"/account?action=reviewOrders");
        }


    }
}
