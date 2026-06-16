package vn.edu.hcmuaf.fit.controller.admin.managementSale;

import vn.edu.hcmuaf.fit.dao.impl.VoucherDAO;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "admin-table-sales", value = "/admin-table-sales")
public class ManagementSaleController extends HttpServlet {
    VoucherDAO voucherDAO = new VoucherDAO();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("title", "Danh Sách Khuyến Mãi");
        request.setAttribute("listVoucher", voucherDAO.findAllVoucher());
        System.out.println(voucherDAO.findAllVoucher());
        request.getRequestDispatcher("views/admin/management-sales.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
