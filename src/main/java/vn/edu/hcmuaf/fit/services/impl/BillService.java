package vn.edu.hcmuaf.fit.services.impl;

import vn.edu.hcmuaf.fit.dao.IBillDAO;
import vn.edu.hcmuaf.fit.dao.impl.BillDAO;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.ShippingInfoModel;
import vn.edu.hcmuaf.fit.services.IBillService;
import vn.edu.hcmuaf.fit.utils.MessageParameterUntil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
public class BillService implements IBillService {
    IBillDAO billDAO = new BillDAO();
    @Override
    public int totalBill() {
        return billDAO.totalBill();
    }
    @Override
    public List<ShippingInfoModel> shippingInfo() {
        return billDAO.shippingInfo();
    }
    @Override
    public void addBill(int idUser, int idBook, String address, String city, String district, String ward, int packInt, int payInt, int quantity, double totalPriceShipVoucher, String info, String phone, int idCart, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int checkAddBill = billDAO.addBill(idUser, idBook, address + ", " + ward + ", " + district + ", " + city, packInt, payInt, quantity, totalPriceShipVoucher, info, phone, idCart);
        if (checkAddBill == 0) {
            new MessageParameterUntil("Đặt hàng thât bại", "warning", "/views/web/reviewOrders.jsp", request, response).send();
        }

    }
    @Override
    public void removeProductInCart(List<Integer> listIdRemove, HttpServletRequest request) {
        CustomerModel user = (CustomerModel) request.getSession().getAttribute("USERMODEL");
        if (user == null) {
            return;
        }
        String cartKey = "cart_" + user.getIdUser();
        CartModel cart = (CartModel) request.getSession().getAttribute(cartKey);
        CartModel cartOrder = (CartModel) request.getSession().getAttribute("cartOrder");

        // tránh lỗi null
        if (cart == null) {
            cart = new CartModel();
        }

        if (cartOrder == null) {
            cartOrder = new CartModel();
        }

        for (int id : listIdRemove) {
            cart.deleteProductById(id);
            cartOrder.deleteProductById(id);
        }
        request.getSession().setAttribute(cartKey, cart);
        request.getSession().setAttribute("cartOrder", cartOrder);
    }
}
