<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">

<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css"
          integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"
          integrity="sha512-xh6O/CkQoPOWDdYTDqeRdPCVd1SpvCA9XXcUnZS2FmJNp1coAFzvtCN9BmamE+4aHK8yyUHUSCcJHgXloTyT2A=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>

    <link href="https://fonts.googleapis.com/css2?family=Open+Sans:ital,wght@0,300;0,400;0,500;0,600;0,700;0,800;1,300;1,400;1,500;1,600;1,700&display=swap"
          rel="stylesheet">

    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/slick-carousel/1.8.1/slick.min.css"
          integrity="sha512-yHknP1/AwR+yx26cB1y0cjvQUMvEa2PFzt1c9LlS4pRQ5NOTZFWbhBig+X9G9eYW/8m0/4OXNx8pxJ6z57x0dw=="
          crossorigin="anonymous" referrerpolicy="no-referrer"/>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/templates/styles/Header.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/templates/styles/About.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/templates/styles/Footer.css">

    <title>Giới thiệu nhà sách cũ Hoàng Tiến</title>
</head>

<body>

<!-- ----------- phần header ---------------- -->
<%@include file="/common/web/header.jsp" %>
<!-- ----------- end header ---------------- -->

<div id="content">
    <div class="container">

        <nav id="breadcrumbbar">
            <ul class="breadcrumb">
                <li class="breadcrumb-item">
                    <a class="chang_font" href="${pageContext.request.contextPath}/home">Trang chủ</a>
                </li>
                <li class="breadcrumb-item active">
                    <a href="">Giới thiệu nhà sách cũ Hoàng Tiến</a>
                </li>
            </ul>
        </nav>

        <div class="wrapper">
            <h1>GIỚI THIỆU</h1>
        </div>

        <div class="content_container">

            <h1>Giới thiệu nhà sách cũ Hoàng Tiến</h1>

            <p style="text-align: justify;">
                <strong>Nhà sách cũ Hoàng Tiến</strong> là website chuyên cung cấp các đầu sách cũ với giá cả hợp lý,
                phù hợp cho học sinh, sinh viên, người đi làm và những ai yêu thích đọc sách. Chúng tôi mong muốn
                mang đến cho khách hàng một không gian mua sách trực tuyến đơn giản, tiết kiệm và đáng tin cậy.
            </p>

            <p style="text-align: justify;">
                Tại Hoàng Tiến, khách hàng có thể dễ dàng tìm kiếm nhiều thể loại sách khác nhau như:
                sách giáo trình, sách tham khảo, sách văn học, sách kỹ năng sống, sách kinh tế, truyện tranh,
                sách thiếu nhi và nhiều đầu sách hữu ích khác. Mỗi cuốn sách đều được chọn lọc, kiểm tra tình trạng
                trước khi đăng bán nhằm đảm bảo thông tin rõ ràng, minh bạch và thuận tiện cho người mua.
            </p>

            <p style="text-align: justify;">
                Với phương châm <strong>“Sách cũ – Giá tốt – Tri thức bền lâu”</strong>, Hoàng Tiến không chỉ giúp
                khách hàng tiết kiệm chi phí mua sách mà còn góp phần lan tỏa thói quen đọc sách, tái sử dụng sách
                và bảo vệ môi trường. Chúng tôi tin rằng mỗi cuốn sách cũ đều mang trong mình một giá trị riêng
                và có thể tiếp tục đồng hành cùng những người đọc mới.
            </p>

            <p style="text-align: justify;">
                Website Hoàng Tiến được xây dựng nhằm hỗ trợ khách hàng mua sách nhanh chóng và dễ dàng hơn.
                Người dùng có thể xem danh mục sản phẩm, tìm kiếm sách, xem chi tiết sách, thêm sách vào giỏ hàng,
                đăng ký tài khoản, đăng nhập và đặt mua sách trực tuyến.
            </p>

            <p style="text-align: justify;">
                Chúng tôi luôn cố gắng trình bày thông tin sản phẩm một cách rõ ràng, bao gồm tên sách, giá bán,
                mô tả, tình trạng sách và các thông tin liên quan khác. Điều này giúp khách hàng có thể cân nhắc
                và lựa chọn được cuốn sách phù hợp với nhu cầu học tập, làm việc hoặc giải trí của mình.
            </p>

            <p style="text-align: justify;">
                <strong>Hoàng Tiến cam kết</strong> mang đến những sản phẩm có giá trị sử dụng tốt, mức giá phù hợp
                và dịch vụ hỗ trợ khách hàng tận tình. Chúng tôi luôn lắng nghe ý kiến đóng góp của khách hàng
                để từng bước hoàn thiện website, nâng cao chất lượng sản phẩm và cải thiện trải nghiệm mua sắm.
            </p>

            <p style="text-align: justify;">
                Không chỉ là nơi mua bán sách cũ, Hoàng Tiến còn mong muốn trở thành cầu nối giúp sách được tiếp tục
                sử dụng, tri thức được lan tỏa và văn hóa đọc ngày càng phát triển trong cộng đồng.
            </p>

            <p style="text-align: justify;">
                Cảm ơn quý khách đã tin tưởng và lựa chọn nhà sách cũ Hoàng Tiến. Sự hài lòng của khách hàng
                chính là động lực để chúng tôi không ngừng phát triển và hoàn thiện hơn trong tương lai.
            </p>

            <p class="caption" style="text-align: justify;">
                NHÀ SÁCH CŨ HOÀNG TIẾN – SÁCH CŨ GIÁ TỐT, TRI THỨC BỀN LÂU
            </p>

        </div>
    </div>
</div>

<!-- ----------- footer ---------------- -->
<%@include file="/common/web/footer.jsp" %>
<!-- ----------- end footer ---------------- -->

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"
        integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
        crossorigin="anonymous"></script>

<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"
        integrity="sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN"
        crossorigin="anonymous"></script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"
        integrity="sha384-+sLIOodYLS7CIrQpBjl+C7nPvqq+FbNUBDunl/OZv93DB7Ln/533i8e/mZXLi/P+"
        crossorigin="anonymous"></script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-Fy6S3B9q64WdZWQUiU+q4/2Lc9npb8tCaSX9FK7E8HnRr0Jz8D6OP9dO5Vg3Q9ct"
        crossorigin="anonymous"></script>

<script src="${pageContext.request.contextPath}/templates/scripts/header.js"></script>

</body>

</html>