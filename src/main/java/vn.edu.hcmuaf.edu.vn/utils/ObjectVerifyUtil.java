package vn.edu.hcmuaf.edu.vn.utils;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartDetailModel;
import vn.edu.hcmuaf.fit.model.OrderReviewDetail;

import java.util.List;

public class ObjectVerifyUtil {
    CartDao cartDao = new CartDao();

    public String string(int idUser, int idCart) {
        OrderReviewDetail o1 = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
        String getTime = cartDao.getCreatime(idCart, idUser);
        System.out.println(getTime);
        String s1 = String.valueOf(o1);
        System.out.println( "s1"+s1);

        String s2 = String.valueOf(cartDao.getAllDetailCart(idUser, idCart));
        System.out.println("s2"+s2);

        double getTotalBill = cartDao.getTotalBill(idUser, idCart);
        return s1 + s2 +getTime + getTotalBill;
    }

    public String stringPrinlt(int idUser, int idCart) {
        OrderReviewDetail o = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
        List<CartDetailModel> list = cartDao.getAllDetailCart(idUser, idCart);
        StringBuilder result = new StringBuilder();
        for (CartDetailModel value : list) {
            result.append("Tên khách hàng :").append(o.getFullName() +"- ").append("Địa chỉ :").append(o.getAddress()  +"- ").append("Số điện thoại:").append(o.getPhone()+"- ").append("Email :").append(o.getEmail()  +"- "
                    ).append("Mã sản phẩm:").append(o.getIdcart() +"- ").append(" Tổng tiền :").append(o.getTotolPrice()  +"- ").append("Ngày đặt :").append(o.getCreate_order_time() +"\n" ).append("Tên sản phẩm :").append(value.getNameSach() +"- ")
                    .append("Số lượng:").append(value.getQuantity());
        }
        return  result.toString();

    }





    public static void main(String[] args) {ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();
        System.out.println(objectVerifyUtil.stringPrinlt(49,43));
    }
}
