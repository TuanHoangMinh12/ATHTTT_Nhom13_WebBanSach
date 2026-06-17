package vn.edu.hcmuaf.fit.model;

public class CartDetailModel {
    private int id;
    private String nameSach;
    private int quantity;
    private String image;
    private double totalPrice;

    // ── Các field MỚI THÊM để đưa vào vùng hash, bảo vệ tính toàn vẹn ──
    private int idBook;          // bill.id_book — đổi sản phẩm là gian lận
    private int idDiscount;      // bill.id_discount — đổi mã giảm giá là gian lận
    private int pack;            // bill.pack — đóng gói, ảnh hưởng chi phí
    private int paymentMethod;   // bill.payment_method — đổi cách thanh toán là gian lận
    private String info;         // bill.info — ghi chú đơn hàng

    public CartDetailModel(int id, String nameSach, int quantity, String image, double totalPrice) {
        this.id = id;
        this.nameSach = nameSach;
        this.quantity = quantity;
        this.image = image;
        this.totalPrice = totalPrice;
    }

    public CartDetailModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameSach() {
        return nameSach;
    }

    public void setNameSach(String nameSach) {
        this.nameSach = nameSach;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getIdBook() {
        return idBook;
    }

    public void setIdBook(int idBook) {
        this.idBook = idBook;
    }

    public int getIdDiscount() {
        return idDiscount;
    }

    public void setIdDiscount(int idDiscount) {
        this.idDiscount = idDiscount;
    }

    public int getPack() {
        return pack;
    }

    public void setPack(int pack) {
        this.pack = pack;
    }

    public int getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(int paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return "CartDetailModel{" +
                "id=" + id +
                ", nameSach='" + nameSach + '\'' +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                ", totalPrice=" + totalPrice +
                ", idBook=" + idBook +
                ", idDiscount=" + idDiscount +
                ", pack=" + pack +
                ", paymentMethod=" + paymentMethod +
                ", info='" + info + '\'' +
                '}';
    }
}