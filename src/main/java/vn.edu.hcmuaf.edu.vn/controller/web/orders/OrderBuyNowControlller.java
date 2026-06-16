package vn.edu.hcmuaf.edu.vn.controller.web.orders;

import vn.edu.hcmuaf.fit.dao.IProductDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.ProductDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "orderBuyNowControlller", value = "/orderBuyNowControlller")
public class OrderBuyNowControlller extends HttpServlet {
    private final IProductDAO productDAO = new ProductDAO();
    private final CartDao cartDao = new CartDao();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter("product_id");
        if (productId != null) {
            Product product = productDAO.getProductById(Integer.parseInt(productId));
            int remainQuantity = productDAO.getRemainQuantity(product.getIdBook());

            CartModel cart = new CartModel();
            cart.setId(cartDao.setID());
            String quantity = request.getParameter("quantity");
            int qnt = quantity == null ? 1 : Integer.parseInt(quantity);

            cart.addProduct(product, qnt);
            request.getSession().setAttribute("cart", cart);
            response.sendRedirect(request.getContextPath()+"/orderAddVoucher?list_id="+productId);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
