<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Danh sách Public Key | Quản trị Admin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/templates/admin/doc/css/main.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
</head>
<body onload="time()" class="app sidebar-mini rtl">

<%@include file="/common/admin/header.jsp"%>
<%@include file="/common/admin/aside.jsp"%>

<main class="app-content">
    <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
            <li class="breadcrumb-item active"><a href="#"><b>Danh sách Public Key</b></a></li>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">

                    <%-- Thông báo sau khi revoke --%>
                    <c:if test="${not empty alertMsg}">
                        <div class="alert alert-${alertType} alert-dismissible" role="alert">
                            <button type="button" class="close" data-dismiss="alert">
                                <span>&times;</span>
                            </button>
                                ${alertMsg}
                        </div>
                    </c:if>

                    <table class="table table-hover table-bordered" id="keyTable">
                        <thead>
                        <tr>
                            <th>STT</th>
                            <th>Người dùng</th>
                            <th>Email</th>
                            <th>Public Key (rút gọn)</th>
                            <th>Ngày tạo</th>
                            <th>Ngày hết hạn</th>
                            <th>Trạng thái</th>
                            <th>Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="key" items="${listKeys}" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${key.userName}</td>
                                <td>${key.email}</td>
                                <td>
                                    <code style="font-size:11px; word-break:break-all">
                                            ${key.publicKeyShort}
                                    </code>
                                </td>
                                <td>${key.createDate}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${key.expire != null}">${key.expire}</c:when>
                                        <c:otherwise><span class="text-muted">—</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                        <%-- BADGE: Active hoặc Revoked --%>
                                    <c:choose>
                                        <c:when test="${key.status == 1}">
                                            <span class="badge badge-success">
                                                <i class="fas fa-check-circle"></i> Active
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger">
                                                <i class="fas fa-ban"></i> Revoked
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                        <%-- Chỉ cho Revoke nếu key đang Active --%>
                                    <c:if test="${key.status == 1}">
                                        <a href="#"
                                           onclick="confirmRevoke(${key.idKey})"
                                           class="btn btn-warning btn-sm"
                                           title="Revoke key này">
                                            <i class="fas fa-times-circle"></i> Revoke
                                        </a>
                                    </c:if>
                                    <c:if test="${key.status != 1}">
                                        <span class="text-muted" style="font-size:12px">Đã revoked</span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty listKeys}">
                            <tr>
                                <td colspan="8" class="text-center text-muted">
                                    Chưa có public key nào trong hệ thống
                                </td>
                            </tr>
                        </c:if>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/dataTables.bootstrap.min.js"></script>

<script>
    // DataTable
    $('#keyTable').DataTable({
        language: { url: 'https://cdn.datatables.net/plug-ins/1.13.4/i18n/vi.json' }
    });

    // Xác nhận trước khi Revoke
    function confirmRevoke(keyId) {
        swal({
            title: "Xác nhận Revoke",
            text: "Key bị revoke sẽ không thể dùng để verify đơn hàng nữa. Tiếp tục?",
            icon: "warning",
            buttons: ["Hủy", "Revoke"],
            dangerMode: true
        }).then(function(ok) {
            if (ok) {
                window.location.href = '${pageContext.request.contextPath}/admin-public-key?revoke=' + keyId;
            }
        });
    }

    // Đồng hồ
    function time() {
        var today = new Date();
        var weekday = ["Chủ Nhật","Thứ Hai","Thứ Ba","Thứ Tư","Thứ Năm","Thứ Sáu","Thứ Bảy"];
        var day = weekday[today.getDay()];
        var dd = String(today.getDate()).padStart(2,'0');
        var mm = String(today.getMonth()+1).padStart(2,'0');
        var yyyy = today.getFullYear();
        var h = String(today.getHours()).padStart(2,'0');
        var m = String(today.getMinutes()).padStart(2,'0');
        var s = String(today.getSeconds()).padStart(2,'0');
        document.getElementById("clock").innerHTML =
            '<span class="date">' + day + ', ' + dd + '/' + mm + '/' + yyyy +
            ' - ' + h + ' giờ ' + m + ' phút ' + s + ' giây</span>';
        setTimeout("time()", 1000);
    }
</script>
</body>
</html>
