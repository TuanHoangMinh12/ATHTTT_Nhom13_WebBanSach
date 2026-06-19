<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Ký đơn hàng</title>
</head>
<body>

<h2>Ký xác thực đơn hàng</h2>

<p>Copy mã hash dưới đây sang ToolSign</p>

<textarea rows="10" cols="120" readonly>${hash}</textarea>

<br><br>

<form action="${pageContext.request.contextPath}/order/save-signature"
      method="post">

    <input type="hidden"
           name="idCart"
           value="${idCart}">

    <label>Chữ ký số</label>

    <br>

    <textarea
            name="signature"
            rows="10"
            cols="120"></textarea>

    <br><br>

    <button type="submit">
        Hoàn tất xác thực
    </button>

</form>

</body>
</html>