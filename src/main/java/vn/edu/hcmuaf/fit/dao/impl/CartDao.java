package vn.edu.hcmuaf.fit.dao.impl;

import vn.edu.hcmuaf.fit.db.JDBCConnector;
import vn.edu.hcmuaf.fit.model.BookModel;
import vn.edu.hcmuaf.fit.model.CartDetailModel;
import vn.edu.hcmuaf.fit.model.CartModel;
import vn.edu.hcmuaf.fit.model.OrderReviewDetail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDao {
    public void updateVerify(int idCart, String stringHash) {
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;

        if (connection != null) {
            try {
                String sql = "UPDATE carts SET verify = ? WHERE id = ?";
                statement = connection.prepareStatement(sql);
                statement.setString(1, stringHash);
                statement.setInt(2, idCart);
                statement.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int setID() {
        Connection connection = JDBCConnector.getConnection();
        String sql = new String("SELECT id FROM carts ORDER BY id DESC LIMIT 1;");
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        int result = 0;
        if (connection != null) {
            try {
                statement = connection.prepareStatement(sql.toString());
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

                return result+1;
            } catch (SQLException e) {
                return 0;
            } finally {
                try {
                    if (connection != null) connection.close();
                    if (statement != null) statement.close();
                    if (resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return 0;
                }
            }
        }
        return 1;
    }

    public String getCreatime( int idCart,int idUser) {
        String sql = "SELECT create_time FROM `carts` WHERE id = ? and idUser = ?";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String createTime = null;

        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, idCart);
            statement.setInt(2, idUser);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                createTime = resultSet.getString("create_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return createTime;
    }

    public int insert_Cart(int idUser, String timeShip, double feeShip, double totalPrice, String infoShip) {
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;
        int cartId = -1; // Giá trị mặc định

        if (connection != null) {
            try {
                String sql = "INSERT INTO carts( idUser, timeShip, feeShip, totalPrice, infoShip, create_time) VALUES ( ?, ?, ?, ?, ?,current_timestamp())";
                statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                statement.setInt(1, idUser);
                statement.setString(2, timeShip);
                statement.setDouble(3, feeShip);
                statement.setDouble(4, totalPrice);
                statement.setString(5, infoShip);
                statement.executeUpdate();
                // Thực hiện câu lệnh INSERT và nhận giá trị được tạo tự động

                generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    cartId = generatedKeys.getInt(1); // Lấy giá trị ID đã được tạo tự động
                    System.out.println("CartModel inserted successfully. Cart ID: " + cartId);

                } else {
                    System.err.println("Failed to retrieve auto-generated cart ID.");
                }
                return cartId;
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return cartId;
    }

    public void update_cart_to_bill(int id) {
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;

        if (connection != null) {
            try {
                // Lấy thời gian hiện tại trước khi cập nhật
                long timeBeforeUpdate = System.currentTimeMillis();

                String sql ="UPDATE bill SET create_order_time = CURRENT_TIMESTAMP WHERE idCart = ?";
                statement = connection.prepareStatement(sql);
                statement.setInt(1, id);

                int rowsAffected = statement.executeUpdate();

                // Lấy thời gian hiện tại sau khi cập nhật
                long timeAfterUpdate = System.currentTimeMillis();

                if (rowsAffected > 0) {
                    System.out.println("CartModel update successfully.");
                    System.out.println("Time before update: " + new Timestamp(timeBeforeUpdate));
                    System.out.println("Time after update: " + new Timestamp(timeAfterUpdate));
                } else {
                    System.out.println("No rows updated. Check if the idCart value is correct and exists in the database.");
                }
            } catch (SQLException e) {
                System.out.println("Update failed with error: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public OrderReviewDetail getAllByIdUserAndIdCart(int id, int idCart) {
        OrderReviewDetail orderReviewDetail = new OrderReviewDetail();
        String sql = "SELECT  CONCAT(t.first_name, ' ', t.last_name) AS fullname, b.address, b.phone, t.email, b.idCart, b.create_order_time, e.timeShip, e.totalPrice, b.shipping_info\n" +
                " FROM bill b JOIN carts e ON b.idCart = e.id JOIN customer t ON e.idUser = t.id_user WHERE t.id_user = ? and b.idCart =?";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                statement.setInt(2, idCart);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {

                    orderReviewDetail.setFullName(resultSet.getString(1));
                    orderReviewDetail.setAddress(resultSet.getString(2));
                    orderReviewDetail.setPhone(resultSet.getString(3));
                    orderReviewDetail.setEmail(resultSet.getString(4));
                    orderReviewDetail.setIdcart(resultSet.getInt(5));
                    orderReviewDetail.setCreate_order_time(resultSet.getString(6));
                    orderReviewDetail.setTimeShip(resultSet.getString(7));
                    orderReviewDetail.setTotolPrice(resultSet.getInt(8));
                    orderReviewDetail.setTrangThai(resultSet.getInt(9));

                }

                return orderReviewDetail;
            } catch (SQLException e) {
                return null;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;

    }

//    public OrderReviewDetail getAllByIdUserAndIdCartNoTimeship(int id, int idCart) {
//        OrderReviewDetail orderReviewDetail = new OrderReviewDetail();
//        String sql = "SELECT  CONCAT(t.first_name, ' ', t.last_name) AS fullname, b.address, b.phone, t.email, b.idCart, b.create_order_time, e.timeShip, e.totalPrice\n" +
//                " FROM bill b JOIN carts e ON b.idCart = e.id JOIN customer t ON e.idUser = t.id_user WHERE t.id_user = ? and b.idCart =?";
//        Connection connection = JDBCConnector.getConnection();
//        PreparedStatement statement = null;
//        ResultSet resultSet = null;
//        if(connection != null) {
//            try {
//                statement = connection.prepareStatement(sql);
//                statement.setInt(1, id);
//                statement.setInt(2, idCart);
//                resultSet = statement.executeQuery();
//                while (resultSet.next()) {
//
//                    orderReviewDetail.setFullName(resultSet.getString(1));
//                    orderReviewDetail.setAddress(resultSet.getString(2));
//                    orderReviewDetail.setPhone(resultSet.getString(3));
//                    orderReviewDetail.setEmail(resultSet.getString(4));
//                    orderReviewDetail.setIdcart(resultSet.getInt(5));
//                    orderReviewDetail.setCreate_order_time(resultSet.getString(6));
//                    orderReviewDetail.setTimeShip(resultSet.getString(7));
//                    orderReviewDetail.setTotolPrice(resultSet.getInt(8));
//
//                }
//
//                return orderReviewDetail;
//            } catch (SQLException e) {
//                return null;
//            } finally {
//                try {
//                    if(connection != null) connection.close();
//                    if(statement != null) statement.close();
//                    if(resultSet != null) resultSet.close();
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return null;
//
//    }

    public double getTotalBill(int id_user, int idCart){
        String sql = "SELECT b.totalBill\n" +
                "FROM bill b\n" +
                "WHERE b.id_user = ? and b.idCart = ? ";


        try (Connection connection = JDBCConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id_user);
            statement.setInt(2, idCart);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("totalBill");
                } else {

                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;

    }

    public double getFeeShip(int idUser, int idCart) {
        String sql = "SELECT feeShip FROM carts WHERE id = ? AND idUser = ?";
        try (Connection connection = JDBCConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idCart);
            statement.setInt(2, idUser);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble("feeShip");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public OrderReviewDetail getAllByIdUserAndIdCartNoTime(int id, int idCart) {
        String sql = "SELECT CONCAT(t.first_name, ' ', t.last_name) AS fullname, b.address, b.phone, t.email, b.idCart, "
                + "b.create_order_time, e.timeShip, e.totalPrice "
                + "FROM bill b "
                + "JOIN carts e ON b.idCart = e.id "
                + "JOIN customer t ON e.idUser = t.id_user "
                + "WHERE t.id_user = ? AND b.idCart = ?";

        try (Connection connection = JDBCConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.setInt(2, idCart);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    OrderReviewDetail orderReviewDetail = new OrderReviewDetail();
                    orderReviewDetail.setFullName(resultSet.getString("fullname"));
                    orderReviewDetail.setAddress(resultSet.getString("address"));
                    orderReviewDetail.setPhone(resultSet.getString("phone"));
                    orderReviewDetail.setEmail(resultSet.getString("email"));
                    orderReviewDetail.setIdcart(resultSet.getInt("idCart"));
                    orderReviewDetail.setTimeShip(resultSet.getString("timeShip"));
                    orderReviewDetail.setTotolPrice(resultSet.getInt("totalPrice"));
                    return orderReviewDetail;
                } else {
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }
    public List<CartDetailModel> getAllDetailCart(int id, int idCart) {
        List<CartDetailModel> result = new ArrayList<>();

        // CẬP NHẬT: thêm b.id_book, b.id_discount, b.pack, b.payment_method,
        // b.info vào SELECT — các trường này giờ được đưa vào vùng hash để
        // bảo vệ tính toàn vẹn (chống admin/người khác sửa sản phẩm, mã giảm giá,
        // kiểu đóng gói, phương thức thanh toán, hoặc ghi chú đơn hàng).
        String sql = "SELECT " +
                "b.idCart, " +
                "bk.name, " +
                "b.quantity, " +
                "(SELECT image FROM image_book WHERE id_book = bk.id_book LIMIT 1) AS image, " +
                "b.quantity * bk.price AS tongtien, " +
                "b.id_book, " +
                "IFNULL(b.id_discount, 0) AS id_discount, " +
                "b.pack, " +
                "b.payment_method, " +
                "IFNULL(b.info, '') AS info " +
                "FROM bill b " +
                "JOIN carts e ON b.idCart = e.id " +
                "JOIN book bk ON b.id_book = bk.id_book " +
                "WHERE b.id_user = ? AND b.idCart = ? " +
                "ORDER BY b.id_order";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, id);
                statement.setInt(2, idCart);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    CartDetailModel cartDetailModel = new CartDetailModel();
                    cartDetailModel.setId(resultSet.getInt(1));
                    cartDetailModel.setNameSach(resultSet.getString(2));
                    cartDetailModel.setQuantity(resultSet.getInt(3));
                    cartDetailModel.setImage(resultSet.getString(4));
                    cartDetailModel.setTotalPrice(resultSet.getInt(5));
                    cartDetailModel.setIdBook(resultSet.getInt(6));
                    cartDetailModel.setIdDiscount(resultSet.getInt(7));
                    cartDetailModel.setPack(resultSet.getInt(8));
                    cartDetailModel.setPaymentMethod(resultSet.getInt(9));
                    cartDetailModel.setInfo(resultSet.getString(10));
                    result.add(cartDetailModel);

                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return new ArrayList<>();
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }
    public CartModel getCartById( int idCart) {
        CartModel result = new CartModel();

        String sql = "SELECT id, idUser, timeShip, feeShip, totalPrice, infoShip, create_Time FROM carts WHERE id =?";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, idCart);

                resultSet = statement.executeQuery();
                while (resultSet.next()) {

                    result.setId(resultSet.getInt(1));
                    result.setIdUser(resultSet.getInt(2));
                    result.setTimeShip(resultSet.getString(3));
                    result.setShip(resultSet.getInt(4));
                    result.setTotalPrice(resultSet.getInt(5));
                    result.setInShip(resultSet.getInt(6));
                    result.setCreateTime(resultSet.getTimestamp(7));

                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }
    public ArrayList<CartModel> getAllCart() {
        ArrayList<CartModel> result = new ArrayList<>();
        String sql = "SELECT id, idUser, timeShip, feeShip, totalPrice, infoShip, create_time\n" +
                "FROM carts ";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    CartModel cartModel = new CartModel();
                    cartModel.setId(resultSet.getInt(1));
                    cartModel.setIdUser(resultSet.getInt(2));
                    cartModel.setTimeShip(resultSet.getString(3));
                    cartModel.setShip(resultSet.getInt(4));
                    cartModel.setTotalPrice(resultSet.getDouble(5));
                    cartModel.setInShip(resultSet.getInt(6));
                    cartModel.setCreateTime(resultSet.getTimestamp(7));
                    result.add(cartModel);

                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }
    public ArrayList<BookModel> top5BookBanChay() {
        String sql = "SELECT s.id_book, s.name, sum(b.quantity), s.price,SUM(b.quantity * s.price), ct.name\n" +
                "FROM bill b JOIN book s\n" +
                "ON b.id_book = s.id_book JOIN carts c\n" +
                "ON b.idCart = c.id JOIN catalog ct ON s.id_catalog = ct.id_catalog\n" +
                "WHERE c.infoShip = 3\n" +
                "GROUP BY s.id_book\n" +
                "ORDER by SUM(b.quantity * s.price)  DESC\n" +
                "LIMIT 5";
        ArrayList<BookModel> result  = new ArrayList<>();
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    BookModel bookModel = new BookModel();
                    bookModel.setIdBook(resultSet.getInt(1));
                    bookModel.setName(resultSet.getString(2));
                    bookModel.setQuantity(resultSet.getInt(3));
                    bookModel.setPrice(resultSet.getInt(4));
                    bookModel.setCatalog(resultSet.getString(6));
                    result.add(bookModel);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public String getHash(int idCart, int idUser) {
        String query = "SELECT verify FROM `carts` WHERE id = ? AND idUser = ?";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        if (connection != null) {
            try {
                statement = connection.prepareStatement(query);
                statement.setInt(1, idCart);
                statement.setInt(2, idUser);
                resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (resultSet != null) resultSet.close();
                    if (statement != null) statement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    public String getPuclickey(int idUser, int idCart) {
        String result = null;
        // Thay vì JOIN phức tạp dễ bị lệch idUser=0 trong bảng carts, ta chỉ truy vấn thẳng bảng public_key
        // để lấy key có hiệu lực tại thời điểm đơn hàng đó tạo ra.
        String sql = "SELECT public_Key FROM public_key " +
                "WHERE id_user = ? AND status = 1 " +
                "ORDER BY create_date DESC LIMIT 1";

        try (Connection connection = JDBCConnector.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, idUser);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    result = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }    public ArrayList<BookModel> bookHetHang() {
        String sql = "SELECT b.id_book, b.NAME, b.quantity, b.price,  ct.name\n" +
                "FROM book b JOIN catalog ct ON b.id_catalog = ct.id_catalog\n" +
                "WHERE quantity =0";
        ArrayList<BookModel> result  = new ArrayList<>();
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    BookModel bookModel = new BookModel();
                    bookModel.setIdBook(resultSet.getInt(1));
                    bookModel.setName(resultSet.getString(2));
                    bookModel.setQuantity(resultSet.getInt(3));
                    bookModel.setPrice(resultSet.getInt(4));
                    bookModel.setCatalog(resultSet.getString(5));
                    result.add(bookModel);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }
    public int soLuongKhachMoi() {
        String sql = "SELECT COUNT(*) FROM customer  WHERE created_time >= DATE_SUB(NOW(), INTERVAL 1 MONTH) AND ROLE = 'user';";
        int result = 0;
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    public int getAllSanPham() {
        String sql = "SELECT sum(quantity) FROM book";
        int result = 0;
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getInt(1);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return 0;
                }
            }
        }
        return 0;
    }
    public double chiPhiDonHang(int idCart) {
        double  result = 0.0;

        String sql = "SELECT SUM(b.prime_cost)\n" +
                "FROM book b JOIN bill c\n" +
                "ON b.id_book = c.id_book\n" +
                "WHERE c.idCart = ?\n" +
                "GROUP BY c.idCart";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, idCart);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    result = resultSet.getDouble(1);
                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return 0.0;
                }
            }
        }
        return 0.0;
    }
    public ArrayList<CartModel> getAllCartByIdUser(int idUser) {
        ArrayList<CartModel> result = new ArrayList<>();
        String sql = "SELECT id, idUser, timeShip, feeShip, totalPrice, infoShip, create_time\n" +
                "FROM carts where idUser = ? ";
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        if(connection != null) {
            try {
                statement = connection.prepareStatement(sql);
                statement.setInt(1, idUser);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    CartModel cartModel = new CartModel();
                    cartModel.setId(resultSet.getInt(1));
                    cartModel.setIdUser(resultSet.getInt(2));
                    cartModel.setTimeShip(resultSet.getString(3));
                    cartModel.setShip(resultSet.getInt(4));
                    cartModel.setTotalPrice(resultSet.getDouble(5));
                    cartModel.setInShip(resultSet.getInt(6));
                    cartModel.setCreateTime(resultSet.getTimestamp(7));
                    result.add(cartModel);

                }

                return result;
            } catch (SQLException e) {
                e.printStackTrace();
                return result;
            } finally {
                try {
                    if(connection != null) connection.close();
                    if(statement != null) statement.close();
                    if(resultSet != null) resultSet.close();
                } catch (SQLException e) {
                    return null;
                }
            }
        }
        return null;
    }
    public void updateCart(int id,int infoShip) {
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;

        if (connection != null) {
            try {
                String sql = " update carts set infoShip = ? where id = ?";
                statement = connection.prepareStatement(sql);

                statement.setInt(1, infoShip);
                statement.setInt(2, id);

                statement.executeUpdate();
                System.out.println("CartModel update successfully.");
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    // cap nhap inforship bằng int
    public int updateCartStatus(int id, int infoShip) {
        Connection connection = JDBCConnector.getConnection();
        PreparedStatement statement = null;
        int rowsAffected = 0; // Biến để lưu số dòng bị ảnh hưởng bởi cập nhật

        if (connection != null) {
            try {
                String sql = "UPDATE carts SET infoShip = ? WHERE id = ?";
                statement = connection.prepareStatement(sql);

                statement.setInt(1, infoShip);
                statement.setInt(2, id);

                rowsAffected = statement.executeUpdate();
                System.out.println("CartModel update successfully. Rows affected: " + rowsAffected);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) connection.close();
                    if (statement != null) statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return rowsAffected; // Trả về số dòng bị ảnh hưởng bởi cập nhật
    }

    public static void main(String[] args) {
        CartDao cartDao = new CartDao();
        System.out.println(cartDao.getTotalBill(48,31));

    }
}