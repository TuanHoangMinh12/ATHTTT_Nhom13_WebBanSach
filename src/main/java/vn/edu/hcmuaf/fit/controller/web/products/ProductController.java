package vn.edu.hcmuaf.fit.controller.web.products;

import vn.edu.hcmuaf.fit.services.IProductService;
import vn.edu.hcmuaf.fit.services.impl.ProductService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "product", value = "/products")
public class ProductController extends HttpServlet {

    IProductService iProductService = new ProductService();
    int pageCurrent = 1;
    @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getParameter("page");
        if(page != null) {
            int pageInt = Integer.parseInt(page);
            pageCurrent = pageInt;

            request.setAttribute("currentPage", pageInt);
            request.setAttribute("list12Book", iProductService.findAllLimitOffsetService(pageInt));
        }else {
            request.setAttribute("currentPage", 1);
            request.setAttribute("list12Book", iProductService.find12Product());
        }


        request.setAttribute("totalPage", iProductService.totalPage());
        request.getRequestDispatcher("/views/web/product.jsp").forward(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
