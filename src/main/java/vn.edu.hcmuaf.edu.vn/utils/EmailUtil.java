package vn.edu.hcmuaf.edu.vn.utils;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.edu.hcmuaf.fit.model.Bill;
import vn.edu.hcmuaf.fit.model.CustomerModel;
import vn.edu.hcmuaf.fit.model.EmailModel;

import java.util.List;
import java.util.Properties;
import java.util.Random;


public class EmailUtil {
    public static void send(EmailModel email) throws Exception {
        Properties prop = new Properties();

        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email.getFrom(), email.getFromPassword());
            }
        });

        try {
             Message message = new MimeMessage(session);
             message.setFrom(new InternetAddress(email.getFrom()));
             message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email.getTo()));
             message.setSubject(email.getSubject());
             message.setContent(email.getContent(), "text/html; charset=utf-8");

            Transport.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    public String getRandom() {
        Random rnd = new Random();
        int number = rnd.nextInt(999999);
        return String.format("%06d", number);
    }
    public boolean sendEmail( String toEmail, String private_key) {
        boolean test = false;

        String fromEmail = "lapnguyen37651@gmail.com";
        String password = "pgvh dkgh nejm jeix";

        try {
            Properties pr = new Properties();
            pr.put("mail.smtp.host", "smtp.gmail.com");
            pr.put("mail.smtp.port", "587");
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            //set email message details
            Message mess = new MimeMessage(session);

            //set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            //set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            //set email subject
            mess.setSubject("User Email Verification");

            //set message text
            mess.setText("This is your private key: " + "\n" + private_key);
            //send the message
            Transport.send(mess);
            test=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }

    /**
     *
     * @param toEmail
     * @param
     * @return
     */
    public boolean sendEmailOrder( String toEmail,CustomerModel customerModel, Bill bill, List<Bill> listBill) {
        boolean test = false;

        String fromEmail = "lapnguyen37651@gmail.com";
        String password = "pgvh dkgh nejm jeix";

        try {
            Properties pr = new Properties();
            pr.put("mail.smtp.host", "smtp.gmail.com");
            pr.put("mail.smtp.port", "587");
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            //set email message details
            Message mess = new MimeMessage(session);

            //set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            //set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            //set email subject
            mess.setSubject("User Email Verification");
            String html =toStringHtml(customerModel,bill, listBill);
            mess.setContent(html, "text/html; charset=utf-8");
            //set message text
           // mess.setText("Notification about orders: "+"\n"+ "Your order has been canceled due to incorrect information with the original information" + "\n"+"Information at :" + billOrder+"\n" +"from : " +fromEmail);
            //send the message
            Transport.send(mess);
            test=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }

    public String toStringHtml(CustomerModel customerModel, Bill bill, List<Bill> listBill){
        String htmlCode = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Đơn hàng</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            margin: 20px;\n" +
                "        }\n" +
                "\n" +
                "        h1 {\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "\n" +
                "        table {\n" +
                "            width: 100%;\n" +
                "            border-collapse: collapse;\n" +
                "            margin-bottom: 20px;\n" +
                "        }\n" +
                "\n" +
                "        th, td {\n" +
                "            border: 1px solid #ccc;\n" +
                "            padding: 8px;\n" +
                "            text-align: center; /* Canh giữa dữ liệu */\n" +
                "        }\n" +
                "\n" +
                "        th {\n" +
                "            background-color: #f2f2f2;\n" +
                "            font-weight: bold;\n" +
                "        }\n" +
                "\n" +
                "        tr:nth-child(even) {\n" +
                "            background-color: #f9f9f9;\n" +
                "        }\n" +
                "\n" +
                "        tr:hover {\n" +
                "            background-color: #e6e6e6;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h6>Chúng tôi nhận thấy thông tin đơn hàng bị sai thông tin do đó đơn hàng đã bị huỷ .Thông tin :</h6>\n" +
                "    <h2>Thông tin khách hàng</h2>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Tên khách hàng</th>\n" +
                "            <th>Địa chỉ</th>\n" +
                "            <th>Số điện thoại</th>\n" +
                "            <th>Email</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td>"+customerModel.getFirstName()+" "+customerModel.getLastName()+"</td>\n" +
                "            <td>"+customerModel.getAddress()+"</td>\n" +
                "            <td>"+customerModel.getPhone()+"</td>\n" +
                "            <td>"+customerModel.getEmail()+"</td>\n" +
                "        </tr>\n" +
                "        <!-- Thêm các hàng khách hàng khác vào đây -->\n" +
                "    </table>\n" +
                "\n" +
                "    <h2>Thông tin người giao</h2>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Tên người giao</th>\n" +
                "            <th>Số điện thoại</th>\n" +
                "            <th>Tên công ty</th>\n" +
                "            <th>Địa chỉ</th>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "            <td>Nguyễn Dư Lập</td>\n" +
                "            <td>0867415853</td>\n" +
                "            <td>Doraemon</td>\n" +
                "            <td>Đại Học Nông Lâm tp.HCM</td>\n" +
                "        </tr>\n" +
                "        <!-- Thêm các hàng người giao khác vào đây -->\n" +
                "    </table>\n" +
                "\n" +
                "    <h2>Thông tin đơn hàng</h2>\n" +
                "    <table>\n" +
                "        <tr>\n" +
                "            <th>Mã sản phẩm</th>\n" +
                "            <th>Số lượng</th>\n" +
                "            <th>Tên sản phẩm</th>\n" +
                "            <th>Ghi chú</th>\n" +
                "        </tr>\n";
        for(Bill b: listBill) {
            int idOder = b.getIdOrder();
            int quantity = b.getQuantity();
            String nameProduct = b.getName();
            String information = b.getInfo() != null ? b.getInfo() : "";

            htmlCode += "<tr>\n" +
                    "<td>"+idOder+"</td>\n" +
                    "<td>"+quantity+"</td>\n" +
                    "<td>"+nameProduct+"</td>\n" +
                    "<td>"+information+"</td>\n" +
                    "</tr>\n";
        }

        htmlCode += "<!-- Thêm các hàng đơn hàng khác vào đây -->\n"+
                "    </table>\n" +
                "<h2>Tổng tiền: "+ PriceFormatUtil.formatPrice(bill.getTotalPrice())+"</h2>\n" +
                "</body>\n" +
                "</html>\n";
        return htmlCode;
    }

    public boolean sendNewEmail( String toEmail, String private_key) {
        boolean test = false;

        String fromEmail = "lapnguyen37651@gmail.com";
        String password = "pgvh dkgh nejm jeix";

        try {
            Properties pr = new Properties();
            pr.put("mail.smtp.host", "smtp.gmail.com");
            pr.put("mail.smtp.port", "587");
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            //set email message details
            Message mess = new MimeMessage(session);

            //set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            //set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            //set email subject
            mess.setSubject("User Email Verification");

            //set message text
            mess.setText("This is your new private key: " + "\n" + private_key);
            //send the message
            Transport.send(mess);
            test=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }
    public boolean sendEmail(CustomerModel user) {
        boolean test = false;

        String toEmail = user.getEmail();
        String fromEmail = "lapnguyen37651@gmail.com";
        String password = "pgvh dkgh nejm jeix";

        try {
            Properties pr = new Properties();
            pr.put("mail.smtp.host", "smtp.gmail.com");
            pr.put("mail.smtp.port", "587");
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.starttls.enable", "true");
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            //set email message details
            Message mess = new MimeMessage(session);

            //set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            //set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            //set email subject
            mess.setSubject("User Email Verification");

            //set message text
            mess.setText("Registered successfully.Please verify your account using this code: " + user.getCode());
            //send the message
            Transport.send(mess);
            test=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }
    public boolean sendEmailFeedBack(String  email, String nameUser,String content) {
        boolean test = false;

        String toEmail = email;
        String fromEmail = "lapnguyen37651@gmail.com";
        String password = "pgvh dkgh nejm jeix";

        try {

            // your host email smtp server details

            Properties pr = new Properties();
            pr.put("mail.smtp.host", "smtp.gmail.com");
            pr.put("mail.smtp.port", "587");
            pr.put("mail.smtp.auth", "true");
            pr.put("mail.smtp.starttls.enable", "true");
//            pr.put("mail.smtp.socketFactory.port", "587");
//            pr.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

            //get session to authenticate the host email address and password
            Session session = Session.getInstance(pr, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            //set email message details
            Message mess = new MimeMessage(session);

            //set from email address
            mess.setFrom(new InternetAddress(fromEmail));
            //set to email address or destination email address
            mess.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));

            //set email subject
            mess.setSubject("Thư phản hồi từ cửa hàng: Book-web");

            //set message text
            mess.setText("Chúng tôi đã nhận được nội dung phản ảnh của bạn: "+ nameUser+"\n"+content);
            //send the message
            Transport.send(mess);

            test=true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;
    }
}
