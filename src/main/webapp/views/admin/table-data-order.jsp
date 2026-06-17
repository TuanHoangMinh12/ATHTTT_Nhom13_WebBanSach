<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh sách đơn hàng | Quản trị Admin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/templates/admin/doc/css/main.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
</head>

<body onload="time()" class="app sidebar-mini rtl">
<%@include file="/common/admin/header.jsp" %>
<%@include file="/common/admin/aside.jsp" %>

<main class="app-content">
    <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
            <li class="breadcrumb-item active"><a href="#"><b>Danh sách đơn hàng</b></a></li>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">
                    <div class="row element-button">
                        <div class="col-sm-2">
                            <a class="btn btn-delete btn-sm print-file" onclick="myApp.printTable()">
                                <i class="fas fa-print"></i> In dữ liệu
                            </a>
                        </div>
                        <div class="col-sm-2">
                            <a class="btn btn-excel btn-sm" href="">
                                <i class="fas fa-file-excel"></i> Xuất Excel
                            </a>
                        </div>
                    </div>

                    <table class="table table-hover table-bordered" id="sampleTable">
                        <thead>
                        <tr>
                            <th width="10"><input type="checkbox" id="all"></th>
                            <th>ID đơn hàng</th>
                            <th>Khách hàng (ID)</th>
                            <th>Địa chỉ</th>
                            <th>Thành tiền</th>
                            <th>Tình trạng vận chuyển</th>
                            <%-- CỘT MỚI: kết quả Verify --%>
                            <th>Xác thực chữ ký</th>
                            <th>Chi tiết</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="cart" items="${listBill}">
                            <tr>
                                <td width="10"><input type="checkbox" name="check1"></td>
                                <td>${cart.id}</td>
                                <td>${cart.idUser}</td>

                                <c:choose>
                                    <c:when test="${cart.bills != null and !cart.bills.isEmpty()}">
                                        <td>${cart.bills.get(0).address}</td>
                                    </c:when>
                                    <c:otherwise>
                                        <td class="text-muted">—</td>
                                    </c:otherwise>
                                </c:choose>

                                <td>
                                    <fmt:formatNumber value="${cart.getTotalPriceFromCart()}" type="number" groupingUsed="true" maxFractionDigits="0"/> VNĐ
                                </td>

                                <td>
                                    <c:choose>
                                        <c:when test="${cart.inShip == 1}">
                                            <span class="badge badge-secondary">Chờ xử lý</span>
                                        </c:when>
                                        <c:when test="${cart.inShip == 2}">
                                            <span class="badge badge-warning">Đang giao</span>
                                        </c:when>
                                        <c:when test="${cart.inShip == 3}">
                                            <span class="badge badge-success">Đã giao</span>
                                        </c:when>
                                        <c:when test="${cart.inShip == 4}">
                                            <span class="badge badge-danger">Đã hủy</span>
                                        </c:when>
                                        <c:when test="${cart.inShip == 5}">
                                            <span class="badge badge-info">Đang xử lý đăng ký</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-light">Không xác định</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                    <%--
                                        TASK 2 — Badge "Xác thực chữ ký"
                                        Dùng cart.verifyStatus được set từ TableOrderController:
                                          null   → Chưa verify
                                          "OK"   → Verified (chữ ký hợp lệ)
                                          "FAIL" → Invalid (phát hiện bị sửa)
                                    --%>
                                <td>
                                    <c:choose>
                                        <c:when test="${cart.verifyStatus == 'OK'}">
                                            <span class="badge badge-success">
                                                <i class="fas fa-shield-alt"></i> Verified
                                            </span>
                                        </c:when>
                                        <c:when test="${cart.verifyStatus == 'FAIL'}">
                                            <span class="badge badge-danger">
                                                <i class="fas fa-exclamation-triangle"></i> Invalid
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-secondary" style="opacity:0.6">
                                                Chưa verify
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>

                                <td>
                                    <a href="${pageContext.request.contextPath}/admin-order-detail?id=${cart.id}">
                                        <button class="btn btn-primary btn-sm" title="Xem chi tiết & Verify">
                                            <i class="fa fa-eye"></i> Chi tiết
                                        </button>
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>

                </div>
            </div>
        </div>
    </div>
</main>

<script src="${pageContext.request.contextPath}/templates/admin/doc/js/jquery-3.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/popper.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/main.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/pace.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/dataTables.bootstrap.min.js"></script>

<script>
    $('#sampleTable').DataTable();

    $('#all').click(function (e) {
        $('#sampleTable tbody :checkbox').prop('checked', $(this).is(':checked'));
        e.stopImmediatePropagation();
    });

    var myApp = new function () {
        this.printTable = function () {
            var tab = document.getElementById('sampleTable');
            var win = window.open('', '', 'height=700,width=700');
            win.document.write(tab.outerHTML);
            win.document.close();
            win.print();
        };
    };

    function time() {
        var today = new Date();
        var weekday = ["Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư", "Thứ Năm", "Thứ Sáu", "Thứ Bảy"];
        var day = weekday[today.getDay()];
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0');
        var yyyy = today.getFullYear();
        var h = String(today.getHours()).padStart(2, '0');
        var m = String(today.getMinutes()).padStart(2, '0');
        var s = String(today.getSeconds()).padStart(2, '0');
        document.getElementById("clock").innerHTML =
            '<span class="date">' + day + ', ' + dd + '/' + mm + '/' + yyyy +
            ' - ' + h + ' giờ ' + m + ' phút ' + s + ' giây</span>';
        setTimeout("time()", 1000);
    }
</script>
</body>
</html>
