package vn.edu.hcmuaf.edu.vn.services.impl;

import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.services.IOrderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class OrderService implements IOrderService {

    @Override
    public boolean checkIdExistsInCart(String listId, HttpServletRequest request, HttpServletResponse response) {

        CustomerModel user = (CustomerModel) request.getSession().getAttribute("USERMODEL");

        if (user == null) {
            return true;
        }

        String cartKey = "cart_" + user.getIdUser();
        CartModel cart = (CartModel) request.getSession().getAttribute(cartKey);

        // kiểm tra cart null
        if (cart == null || cart.getMap() == null) {
            return true;
        }

        // lấy tất cả id mua hàng
        StringTokenizer st = new StringTokenizer(listId, ",");
        while (st.hasMoreTokens()) {
            int id = Integer.parseInt(st.nextToken());
            System.out.println("ID URL: " + id);
            System.out.println("KEY CART: " + cart.getMap().keySet());
            if (!cart.getMap().containsKey(id)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public CartModel cartOrder(String list, HttpServletRequest request) {
        CartModel result = new CartModel();
        CustomerModel user = (CustomerModel) request.getSession().getAttribute("USERMODEL");
        if (user == null) {
            return result;
        }
        String cartKey = "cart_" + user.getIdUser();
        CartModel cart = (CartModel) request.getSession().getAttribute(cartKey);
        System.out.println(cart);
        System.out.println(list);

        // kiểm tra cart null
        if (cart == null || cart.getMap() == null) {
            return result;
        }

        List<Integer> listId = getListId(list);
        for (int id : listId) {
            if (cart.getMap().containsKey(id)) {
                result.getMap().put(id, cart.getMap().get(id));
            }
        }
        return result;
    }

    public List<Integer> getListId(String list) {
        List<Integer> result = new ArrayList<>();
        if (list == null || list.trim().isEmpty()) {
            return result;
        }

        StringTokenizer st = new StringTokenizer(list, ",");
        while (st.hasMoreTokens()) {
            result.add(Integer.parseInt(st.nextToken()));
        }
        return result;
    }
}