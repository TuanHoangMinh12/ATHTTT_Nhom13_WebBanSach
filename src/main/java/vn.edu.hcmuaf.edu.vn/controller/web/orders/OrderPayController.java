package vn.edu.hcmuaf.edu.vn.controller.web.orders;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.dao.impl.InformationDeliverDao;
import vn.edu.hcmuaf.fit.model.CartItem;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.InformationDeliverModel;
import vn.edu.hcmuaf.fit.services.IBillService;
import vn.edu.hcmuaf.fit.services.impl.BillService;
import vn.edu.hcmuaf.fit.utils.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@WebServlet(name = "order/pay", value = "/order/pay")
public class OrderPayController extends HttpServlet {
    IBillService billService = new BillService();
    CartDao cartDao = new CartDao();
    InformationDeliverDao informationDeliverDao = new InformationDeliverDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html; charset=UTF-8");
        InetAddress myIP=InetAddress.getLocalHost();
        String ip= myIP.getHostAddress();

        request.setCharacterEncoding("UTF-8");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String city = request.getParameter("city");
        String district = request.getParameter("district");
        String ward = request.getParameter("ward");
        String pack = request.getParameter("pack");
        String pay = request.getParameter("pay");
        String info = request.getParameter("note");

        int packInt = Integer.parseInt(pack);
        int payInt = Integer.parseInt(pay);
        CustomerModel cus = (CustomerModel) SessionUtil.getInstance().getValue(request, "USERMODEL");

        CartModel cart = (CartModel) request.getSession().getAttribute("cartOrder");
        cart.setIdUser(cus.getIdUser());
        // listIdRemove để xóa sản phẩm khỏi giỏ hàng
        List<Integer> listIdRemove = new ArrayList<>();
        Set<Integer> keySet = cart.getMap().keySet();

        // list cartItem để add vào bill
        List<CartItem> listCartItem = new LinkedList<>();
        for (Integer key : keySet) {
            CartItem item = cart.getMap().get(key);
            listCartItem.add(item);
            listIdRemove.add(item.getProduct().getIdBook());
        }

        int idCart = cartDao.insert_Cart( cart.getIdUser(),cart.getTimeShip(),cart.getShip(), cart.getTotalPriceShipVoucher(),"1" );

        System.out.println("cart id" +idCart);
        // lấy thông tin từ session ra
        HttpSession httpSession = request.getSession();
        InformationDeliverModel informationDeliverModel = (InformationDeliverModel) httpSession.getAttribute("deliver");
        if (informationDeliverModel == null) {
            informationDeliverModel = new InformationDeliverModel();
            httpSession.setAttribute("deliver", informationDeliverModel);
        }
        informationDeliverModel.setIdCart(idCart);

        // lưu informationDeliver vào DB
        informationDeliverDao.insertInfomationDeliver(informationDeliverModel);

        // add bill vào DB
        for(CartItem cartItem : listCartItem) {
            billService.addBill(cus.getIdUser(), cartItem.getProduct().getIdBook(),
                    address, city, district, ward, packInt, payInt,
                    cartItem.getQuantity(), cart.getTotalPriceShipVoucher(), info, phone, idCart ,request, response);
        }
        cartDao.update_cart_to_bill(idCart);

        // xóa dữ liệu khỏi session
        billService.removeProductInCart(listIdRemove, request);
        response.sendRedirect(
                request.getContextPath()
                        + "/order/reviewOrder?orderSuccess=1"
        );
    }
}
