package vn.edu.hcmuaf.fit.controller.admin.managementSale;

import vn.edu.hcmuaf.fit.dao.impl.VoucherDAO;
import vn.edu.hcmuaf.fit.utils.MessageParameterUntil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "admin-add-voucher", value = "/admin-add-voucher")
public class AddSaleController extends HttpServlet {
    private VoucherDAO voucherDAO = new VoucherDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("views/admin/form-add-voucher.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String name = request.getParameter("name");
        String quantity = request.getParameter("quantity");
        String percent_discount = request.getParameter("percent_discount");
        String diktat = request.getParameter("diktat");
        String price_minimum = request.getParameter("price_minimum");

        if(!name.equals("") && !quantity.equals("") && !percent_discount.equals("") && !diktat.equals("") && !price_minimum.equals("")) {
            int qualityInt = Integer.parseInt(quantity);
            int percent_discountInt = Integer.parseInt(percent_discount);
            int price_minimumInt = Integer.parseInt(price_minimum);
            voucherDAO.addVoucher(name, qualityInt, percent_discountInt, diktat, price_minimumInt);
        }

        new MessageParameterUntil("Thêm thành công", "success", "views/admin/form-add-voucher.jsp", request, response).send();
    }
}
