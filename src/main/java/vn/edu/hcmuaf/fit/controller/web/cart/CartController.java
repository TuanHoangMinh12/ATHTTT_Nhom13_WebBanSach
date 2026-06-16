package vn.edu.hcmuaf.fit.controller.web.cart;

import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "cart", value = "/cart")
public class CartController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        CustomerModel user = (CustomerModel) request.getSession().getAttribute("USERMODEL");

        if (user != null) {
            String cartKey = "cart_" + user.getIdUser();
            CartModel cart = (CartModel) request.getSession().getAttribute(cartKey);
            request.setAttribute("cart", cart);
        }
        request.getRequestDispatcher("/views/web/cart.jsp").forward(request, response);
    }
}