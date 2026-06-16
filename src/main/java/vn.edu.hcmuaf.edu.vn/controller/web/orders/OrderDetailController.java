package vn.edu.hcmuaf.edu.vn.controller.web.orders;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartDetailModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.OrderReviewDetail;
import vn.edu.hcmuaf.fit.utils.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "orderDetail", value = "/orderDetail")
public class OrderDetailController extends HttpServlet {
    CartDao cartDao = new CartDao();
    SHA256Util sha256Util = new SHA256Util();
    SessionUtil sessionUtil = new SessionUtil();
    RSAUtil rsa = new RSAUtil();
    ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();
    EmailUtil emailUtil = new EmailUtil();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {

            request.setCharacterEncoding("UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=UTF-8");

            CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request ,"USERMODEL");

            System.out.println("CUS = " + cus);

            // KIỂM TRA ĐĂNG NHẬP
            if (cus == null) {
                response.sendRedirect(request.getContextPath() + "/login?action=login");
                return;
            }

            String id = request.getParameter("id");

            System.out.println("ID = " + id);

            int idInt = Integer.parseInt(id);

            int idUser = cus.getIdUser();

            System.out.println("idUser = " + idUser);

            List<CartDetailModel> cartDaos = cartDao.getAllDetailCart(idUser,idInt);

            System.out.println("cartDaos = " + cartDaos);

            request.setAttribute("orderReviewDetail",
                    cartDao.getAllByIdUserAndIdCart(idUser,idInt));

            request.setAttribute("cartReviewDetail", cartDaos);

            request.getRequestDispatcher("/views/web/orderDetail.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request ,"USERMODEL");
        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        System.out.println("CUS = " + cus);
        int idUser = cus.getIdUser();
        String publicKey= cartDao.getPuclickey( idUser ,idInt);

        String verfy =cartDao.getHash(idInt, idUser);
        String  order = objectVerifyUtil.string(idUser, idInt);
        System.out.println("don hang" + order);
        String hash1 = sha256Util.check(order);
         OrderReviewDetail o = cartDao.getAllByIdUserAndIdCart(idUser,idInt);
        List<CartDetailModel> cartDaos =cartDao.getAllDetailCart(idUser,idInt);


        try {
            rsa.setPublicKey(publicKey);
            String hash2 = rsa.decrypt(verfy);
           if(hash1.equals(hash2)){
               request.setAttribute("successMessage", "Verification successful!");
           }else{
               //kiem tr neu 2 chuoi hash khác nhau thi don hang bị huỷ
               if(!hash1.equals(hash2)){
                   String or = objectVerifyUtil.stringPrinlt(idUser,idInt);
                   CartDao dao = new CartDao();
                   String link = "<a href=\"" + request.getContextPath() + "/account?action=reviewOrders\" style=\"color: #007FFF; text-decoration: none;\">Confirm</a>";
                  // update ve trang thai don hang huỷ
                   dao.updateCart(idInt, 4);
                   request.setAttribute("nosuccessMessage", "The order information is wrong, do you want to cancel the order ?  " +link );
               }
           }
        } catch (Exception e) {
            // bao loi
            request.setAttribute("nosuccessMessage", "Verification no successful!");

        }

        for (CartDetailModel c :cartDaos){
            request.setAttribute("id", c.getId());
        }
        request.setAttribute("orderReviewDetail", cartDao.getAllByIdUserAndIdCart(idUser,idInt));
        request.setAttribute("cartReviewDetail", cartDao.getAllDetailCart(idUser,idInt));
        request.getRequestDispatcher("/views/web/orderDetail.jsp").forward(request, response);


    }



}
