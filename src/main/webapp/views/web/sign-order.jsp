<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css">

    <link rel="stylesheet"
          href="<c:url value='/templates/styles/Header.css'/>">

    <link rel="stylesheet"
          href="<c:url value='/templates/styles/Footer.css'/>">

    <title>


        Xác thực đơn hàng</title>

    <style>

        .verify-container{
            margin:40px auto;
            max-width:900px;
        }

        .verify-card{
            background:#fff;
            border-radius:10px;
            box-shadow:0 2px 10px rgba(0,0,0,.1);
            padding:30px;
        }

        .step-title{
            color:#ed4d2b;
            font-weight:700;
            margin-top:25px;
        }

        .hash-box{
            background:#f8f9fa;
            border:1px solid #ddd;
            border-radius:6px;
            padding:15px;
            word-break:break-all;
        }

        .btn-orange{
            background:#ed4d2b;
            border:none;
            color:white;
        }

        .btn-orange:hover{
            background:#d84320;
            color:white;
        }

    </style>
</head>

<body>

<%@include file="/common/web/header.jsp"%>

<div class="container verify-container">

    <div class="verify-card">

        <h2 class="text-center mb-4">
            <i class="fa-solid fa-shield-halved"></i>
            XÁC THỰC ĐƠN HÀNG
        </h2>

        <div class="alert alert-warning">
            Để đảm bảo đơn hàng được tạo bởi chính chủ tài khoản,
            vui lòng ký điện tử đơn hàng bằng Private Key.
        </div>

        <h5 class="step-title">
            Bước 1: Copy mã Hash đơn hàng
        </h5>

        <div class="hash-box" id="hashValue">
            ${hash}
        </div>

        <button class="btn btn-orange mt-3"
                onclick="copyHash()">
            <i class="fa-solid fa-copy"></i>
            Copy Hash
        </button>

        <hr>

        <h5 class="step-title">
            Bước 2: Ký bằng ToolSign
        </h5>

        <ul>
            <li>Mở ToolSign.</li>
            <li>Chọn file Private Key.</li>
            <li>Dán Hash vừa copy.</li>
            <li>Nhấn nút Ký.</li>
            <li>Copy Signature nhận được.</li>
        </ul>

        <hr>

        <h5 class="step-title">
            Bước 3: Dán chữ ký số
        </h5>

        <form action="${pageContext.request.contextPath}/order/save-signature"
              method="post">

            <input type="hidden"
                   name="idCart"
                   value="${idCart}">

            <div class="form-group">

                <label>Chữ ký số (Signature)</label>

                <textarea
                        class="form-control"
                        name="signature"
                        rows="6"
                        required
                        placeholder="Dán chữ ký số từ ToolSign vào đây">
                </textarea>

            </div>

            <button type="submit"
                    class="btn btn-orange btn-lg btn-block">

                <i class="fa-solid fa-check"></i>
                XÁC NHẬN ĐƠN HÀNG

            </button>

        </form>

    </div>

</div>

<%@include file="/common/web/footer.jsp"%>

<script>

    function copyHash(){

        let hash =
            document.getElementById("hashValue")
                .innerText;

        navigator.clipboard.writeText(hash);

        alert("Đã copy Hash");
    }

</script>

</body>
</html>