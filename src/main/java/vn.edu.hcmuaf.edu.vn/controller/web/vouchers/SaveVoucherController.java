package vn.edu.hcmuaf.edu.vn.controller.web.vouchers;

import vn.edu.hcmuaf.fit.dao.IVoucherDAO;
import vn.edu.hcmuaf.fit.dao.impl.VoucherDAO;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "saveVoucher", value = "/saveVoucher")
public class SaveVoucherController extends HttpServlet {
    IVoucherDAO iVoucherDAO = new VoucherDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");
        if(id!= null) {
            int idInt = Integer.parseInt(id);
            iVoucherDAO.saveVoucherCus(idInt, cus.getIdUser());
            iVoucherDAO.updateVoucher(idInt);
        }
        request.getRequestDispatcher("/views/web/voucher.jsp").forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
