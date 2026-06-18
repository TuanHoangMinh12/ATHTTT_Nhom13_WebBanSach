package vn.edu.hcmuaf.fit.utils;

import vn.edu.hcmuaf.fit.dao.impl.CartDao;
import vn.edu.hcmuaf.fit.model.CartDetailModel;
import vn.edu.hcmuaf.fit.model.OrderReviewDetail;

import java.util.List;

public class ObjectVerifyUtil {
    CartDao cartDao = new CartDao();

    public String string(int idUser, int idCart) {
        OrderReviewDetail o1 = cartDao.getAllByIdUserAndIdCart(idUser, idCart);
        String getTime = cartDao.getCreatime(idCart, idUser);
        List<CartDetailModel> details = cartDao.getAllDetailCart(idUser, idCart);
        double totalBillFromBillTable = cartDao.getTotalBill(idUser, idCart);   // bill.totalBill
        double totalPriceFromCarts = o1.getTotolPrice();                        // carts.totalPrice
        double feeShip = cartDao.getFeeShip(idUser, idCart);                    // carts.feeShip

        StringBuilder sb = new StringBuilder();

        // ── Thông tin đơn hàng (mức carts/bill chung)
        sb.append(idUser);
        sb.append("|").append(o1.getIdcart());
        sb.append("|").append(o1.getFullName());
        sb.append("|").append(o1.getAddress());
        sb.append("|").append(o1.getPhone());
        sb.append("|").append(o1.getEmail());
        sb.append("|").append(getTime);
        sb.append("|").append(totalBillFromBillTable);
        sb.append("|").append(totalPriceFromCarts);
        sb.append("|").append(feeShip);

        // Thông tin từng sản phẩm (giữ thứ tự theo ORDER BY b.id_order)
        for (CartDetailModel item : details) {
            sb.append("|").append(item.getId());
            sb.append(":").append(item.getNameSach());
            sb.append(":").append(item.getQuantity());
            sb.append(":").append(item.getTotalPrice());
            sb.append(":").append(item.getIdBook());        // chống đổi sản phẩm
            sb.append(":").append(item.getIdDiscount());     // chống đổi mã giảm giá
            sb.append(":").append(item.getPack());           // chống đổi kiểu đóng gói
            sb.append(":").append(item.getPaymentMethod());  // chống đổi phương thức thanh toán
            sb.append(":").append(item.getInfo());           // chống đổi ghi chú đơn hàng
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
}