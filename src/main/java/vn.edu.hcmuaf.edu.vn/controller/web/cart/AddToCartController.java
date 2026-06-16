package vn.edu.hcmuaf.edu.vn.controller.web.cart;

import vn.edu.hcmuaf.fit.dao.IProductDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.ProductDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "add-to-cart", value = "/add-to-cart")
public class AddToCartController extends HttpServlet {
    private final IProductDAO productDAO = new ProductDAO();
    private final CartDao cartDao = new CartDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productId = request.getParameter("product_id");
        if (productId != null) {
            Product product = productDAO.getProductById(Integer.parseInt(productId));
            int remainQuantity = productDAO.getRemainQuantity(product.getIdBook());

            CustomerModel user = (CustomerModel) request.getSession().getAttribute("USERMODEL");

            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login?action=login");
                return;
            }

            String cartKey = "cart_" + user.getIdUser();
            CartModel cart = (CartModel) request.getSession().getAttribute(cartKey);
            if (cart == null) {
                cart = new CartModel();
            }
            cart.setId(cartDao.setID());
            String action = request.getParameter("action");
            if (action != null) {
                switch (action) {
                    case "add":
                        String quantity = request.getParameter("quantity");
                        int qnt = quantity == null ? 1 : Integer.parseInt(quantity);
                        if (remainQuantity < qnt) {
                            break;
                        }
                        cart.addProduct(product, qnt);
                        productDAO.updateQuantity(product.getIdBook(), remainQuantity - qnt);
                        break;
                    case "remove":
                        cart.removeProduct(product);
                        productDAO.updateQuantity(product.getIdBook(), remainQuantity + 1);
                        break;
                    case "delete":
                        int deleteQuantities = cart.getMap().get(product.getIdBook()).getQuantity();
                        cart.deleteProduct(product);
                        productDAO.updateQuantity(product.getIdBook(), remainQuantity + deleteQuantities);
                        break;
                }
            } else if (remainQuantity >= 1) {
                cart.setId(cartDao.setID());
                cart.addProduct(product, 1);
                productDAO.updateQuantity(product.getIdBook(), remainQuantity - 1);
            }
            request.getSession().setAttribute(cartKey, cart);
            cart.setId(cartDao.setID());
            response.sendRedirect(request.getContextPath() + "/cart");
        }
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
