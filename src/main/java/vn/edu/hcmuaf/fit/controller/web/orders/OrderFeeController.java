package vn.edu.hcmuaf.fit.controller.web.orders;

import vn.edu.hcmuaf.fit.model.CartItem;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.InformationDeliverModel;
import vn.edu.hcmuaf.fit.utils.FeeGHNUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

@WebServlet(name = "order/fee", value = "/orderFee")
public class OrderFeeController extends HttpServlet {
    public ArrayList<String> feeDeliver(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nameDistrict = request.getParameter("exits");
        StringTokenizer tokenizer1 = new StringTokenizer(nameDistrict,"/");
        String quan = tokenizer1.nextToken().trim();
        String xa = tokenizer1.nextToken().trim();
        ArrayList<String> result = new ArrayList<>();
        ArrayList<CartItem> p = new ArrayList<>();
        HttpSession session = request.getSession();
        CartModel cart = (CartModel) session.getAttribute("cartOrder");
        int x = cart.sizeX();
        int y = cart.sizeY();
        int z = cart.sizeZ();
        int w = cart.weight();
        double cost =  FeeGHNUtils.getFeeShip(x,y,z,w,1463,21808,quan, xa);
        String dateTime =  FeeGHNUtils.getDateShip(x,y,z,w,1463,21808,quan, xa);
        HttpSession httpSession = request.getSession();
        InformationDeliverModel informationDeliverModel = new InformationDeliverModel(cart.getId(),x,y,z,w,quan,xa);
        httpSession.setAttribute("deliver", informationDeliverModel);
        result.add(dateTime);
        result.add(String.valueOf(cost));
        cart.setShip((int) Double.parseDouble(result.get(1)));
        cart.setTimeShip(result.get(0));
        return result;
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        PrintWriter out = response.getWriter();
        out.println(feeDeliver(request,response));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        CartModel cart = (CartModel) session.getAttribute("cartOrder");
        feeDeliver(request,response);
        PrintWriter out = response.getWriter();
        out.println(cart.getTotalPriceShipVoucher());
    }
}
