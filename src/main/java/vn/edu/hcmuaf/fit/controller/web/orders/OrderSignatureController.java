package vn.edu.hcmuaf.fit.controller.web.orders;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(
        name = "save-signature",
        value = "/order/save-signature"
)
public class OrderSignatureController extends HttpServlet {

    CartDao cartDao = new CartDao();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        int idCart =
                Integer.parseInt(
                        request.getParameter("idCart"));

        String signature =
                request.getParameter("signature");

        cartDao.updateVerify(
                idCart,
                signature
        );

        response.sendRedirect(
                request.getContextPath()
                        + "/order/reviewOrder?orderSuccess=1"
        );
    }
}