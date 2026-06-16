package vn.edu.hcmuaf.fit.utils;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartDetailModel;
import vn.edu.hcmuaf.fit.model.OrderReviewDetail;

import java.util.List;

public class ObjectVerifyUtil {
    CartDao cartDao = new CartDao();

//    public String string(int idUser, int idCart) {
//        OrderReviewDetail o1 = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
//        String getTime = cartDao.getCreatime(idCart, idUser);
//        System.out.println(getTime);
//        String s1 = String.valueOf(o1);
//        System.out.println( "s1"+s1);
//
//        String s2 = String.valueOf(cartDao.getAllDetailCart(idUser, idCart));
//        System.out.println("s2"+s2);
//
//        double getTotalBill = cartDao.getTotalBill(idUser, idCart);
//        return s1 + s2 +getTime + getTotalBill;
//    }
//
//    public String stringPrinlt(int idUser, int idCart) {
//        OrderReviewDetail o = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
//        List<CartDetailModel> list = cartDao.getAllDetailCart(idUser, idCart);
//        StringBuilder result = new StringBuilder();
//        for (CartDetailModel value : list) {
//            result.append("Tên khách hàng :").append(o.getFullName() +"- ").append("Địa chỉ :").append(o.getAddress()  +"- ").append("Số điện thoại:").append(o.getPhone()+"- ").append("Email :").append(o.getEmail()  +"- "
//                    ).append("Mã sản phẩm:").append(o.getIdcart() +"- ").append(" Tổng tiền :").append(o.getTotolPrice()  +"- ").append("Ngày đặt :").append(o.getCreate_order_time() +"\n" ).append("Tên sản phẩm :").append(value.getNameSach() +"- ")
//                    .append("Số lượng:").append(value.getQuantity());
//        }
//        return  result.toString();
//
//    }



    /**
     * Serialize đơn hàng thành chuỗi NHẤT QUÁN để hash.
     * QUAN TRỌNG: thứ tự và format phải GIỐNG HỆT lúc user ký (phía web).
     * Chỉ dùng các field KHÔNG thay đổi sau khi đặt hàng.
     */
    public String string(int idUser, int idCart) {
        OrderReviewDetail o1 = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
        String getTime = cartDao.getCreatime(idCart, idUser);
        List<CartDetailModel> details = cartDao.getAllDetailCart(idUser, idCart);
        double totalBill = cartDao.getTotalBill(idUser, idCart);

        // Build chuỗi theo thứ tự CỐ ĐỊNH, KHÔNG dùng toString() của object
        StringBuilder sb = new StringBuilder();

        // Thông tin đơn hàng
        sb.append(o1.getIdcart());
        sb.append("|").append(o1.getFullName());
        sb.append("|").append(o1.getAddress());
        sb.append("|").append(o1.getPhone());
        sb.append("|").append(o1.getEmail());
        sb.append("|").append(getTime);
        sb.append("|").append(totalBill);

        // Thông tin từng sản phẩm (sắp xếp theo id để đảm bảo thứ tự)
        details.sort((a, b) -> Integer.compare(a.getId(), b.getId()));
        for (CartDetailModel item : details) {
            sb.append("|").append(item.getId());
            sb.append(":").append(item.getNameSach());
            sb.append(":").append(item.getQuantity());
            sb.append(":").append(item.getTotalPrice());
        }

        return sb.toString();
    }

    // Hàm in ra để debug / báo cáo (không dùng để hash)
    public String stringPrinlt(int idUser, int idCart) {
        OrderReviewDetail o = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
        List<CartDetailModel> list = cartDao.getAllDetailCart(idUser, idCart);
        StringBuilder result = new StringBuilder();
        for (CartDetailModel value : list) {
            result.append("Tên khách hàng: ").append(o.getFullName()).append("\n")
                    .append("Địa chỉ: ").append(o.getAddress()).append("\n")
                    .append("Số điện thoại: ").append(o.getPhone()).append("\n")
                    .append("Email: ").append(o.getEmail()).append("\n")
                    .append("Mã đơn hàng: ").append(o.getIdcart()).append("\n")
                    .append("Tổng tiền: ").append(o.getTotolPrice()).append("\n")
                    .append("Ngày đặt: ").append(o.getCreate_order_time()).append("\n")
                    .append("Sản phẩm: ").append(value.getNameSach()).append("\n")
                    .append("Số lượng: ").append(value.getQuantity()).append("\n\n");
        }
        return result.toString();
    }


    public static void main(String[] args) {ObjectVerifyUtil objectVerifyUtil = new ObjectVerifyUtil();
        System.out.println(objectVerifyUtil.stringPrinlt(49,43));
    }
}
