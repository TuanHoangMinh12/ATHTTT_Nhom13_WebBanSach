package vn.edu.hcmuaf.edu.vn.controller.admin.managementOrder;

import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.CustomerDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.utils.ObjectVerifyUtil;
import vn.edu.hcmuaf.fit.utils.RSAUtil;
import vn.edu.hcmuaf.fit.utils.SHA256Util;
import vn.edu.hcmuaf.fit.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "admin-order-detail", value = "/admin-order-detail")
public class OrderDetailController extends HttpServlet {
    CartDao cartDao = new CartDao();
    BillDAO billDAO = new BillDAO();
    CustomerDAO customerDAO = new CustomerDAO();
    SHA256Util sha256Util = new SHA256Util();
    SessionUtil sessionUtil = new SessionUtil();
    RSAUtil rsa = new RSAUtil();
    ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        System.out.println("id find"+  idInt);
        CartModel cartModel = cartDao.getCartById(idInt);
        // lay id
        request.setAttribute("id", idInt);
        request.setAttribute("CUSTOMER", customerDAO.findById(cartModel.getIdUser())) ;
        request.setAttribute("cart", listDonHang(idInt));
        request.setAttribute("LISTBILL",  cartDao.getAllDetailCart(cartModel.getIdUser(),idInt));
        request.getRequestDispatcher("/views/admin/confirm-order-detail.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        System.out.println("id find"+  idInt);
        CartModel cartModel = cartDao.getCartById(idInt);
        // lay id
        request.setAttribute("id", idInt);
        int idUser =cartModel.getIdUser();
        // lay public key
        String publicKey= cartDao.getPuclickey( idUser ,idInt);
        // lay chuoi ma hoa
        String verfy =cartDao.getHash(idInt, idUser);
        // lay don hang
        String  order = objectVerifyUtil.string(idUser, idInt);
        System.out.println(order);
        // bam don hang
        String hash1 = sha256Util.check(order);
        try {
            rsa.setPublicKey(publicKey);
            String hash2 = rsa.decrypt(verfy);
            if(hash1.equals(hash2)){
                request.setAttribute("successMessage", "Verification successful!");
            }else{
                if(!hash1.equals(hash2)){
                    String link = "<a href=\"" + request.getContextPath() + "/admin-table-order\" style=\"color: #007FFF; text-decoration: none;\">Confirm</a>";
                    cartDao.updateCart(idInt,4);
                    request.setAttribute("nosuccessMessage", "The order information is wrong, do you want to cancel the order ? " +link );
                }
            }
        } catch (Exception e) {
            request.setAttribute("nosuccessMessage", "Verification no successful!");
        }

        request.setAttribute("CUSTOMER", customerDAO.findById(cartModel.getIdUser())) ;
        request.setAttribute("cart", listDonHang(idInt));
        request.setAttribute("LISTBILL",  cartDao.getAllDetailCart(cartModel.getIdUser(),idInt));
        request.getRequestDispatcher("/views/admin/confirm-order-detail.jsp").forward(request, response);
    }
    public CartModel listDonHang( int id) {
        CartDao cartDao = new CartDao();
        CartModel listModel = cartDao.getCartById(id);
        System.out.println("CartModel: "+listModel);
        System.out.println("findAllBill: "+ new BillDAO().findAllBillByIdCart(id));
        listModel.setBills(new BillDAO().findAllBillByIdCart(id));
        return listModel;
    }
}
