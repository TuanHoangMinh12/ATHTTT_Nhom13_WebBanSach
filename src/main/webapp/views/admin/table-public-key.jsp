<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

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
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
</head>
<body onload="time()" class="app sidebar-mini rtl">

<%@include file="/common/admin/header.jsp"%>
<%@include file="/common/admin/aside.jsp"%>

<main class="app-content">
    <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
            <li class="breadcrumb-item active"><a href="#"><b>Danh sách Public Key hệ thống</b></a></li>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">

                    <%-- Khối thông báo Alert sau khi Revoke khóa --%>
                    <c:if test="${not empty alertMsg}">
                        <div class="alert alert-${alertType} alert-dismissible fade show" role="alert" style="border-left: 5px solid;">
                            <strong>Thông báo:</strong> ${alertMsg}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </c:if>

                    <%-- Bảng dữ liệu cấu trúc DataTables --%>
                    <table class="table table-hover table-bordered" id="keyTable">
                        <thead>
                        <tr>
                            <th width="40">STT</th>
                            <th>Người dùng (ID)</th>
                            <th>Email tài khoản</th>
                            <th width="280">Khóa công khai (Public Key)</th>
                            <th>Ngày kích hoạt</th>
                            <th>Ngày hết hạn</th>
                            <th width="100">Trạng thái</th>
                            <th width="110">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="key" items="${listKeys}" varStatus="loop">
                            <tr>
                                <td class="text-center">${loop.index + 1}</td>
                                <td><b>${key.userName}</b> <span class="text-muted">(ID: ${key.idUser})</span></td>
                                <td>${key.email}</td>
                                <td>
                                    <div style="position: relative;">
                                        <code style="font-size:11px; word-break:break-all; display:block; padding-right: 25px;" id="pubkey-${key.idKey}">${key.publicKeyShort}</code>
                                        <button class="btn btn-light btn-sm btn-copy" data-id="${key.idKey}" title="Sao chép toàn bộ khóa" style="position: absolute; right: 0; top: -3px; padding: 2px 6px;">
                                            <i class="far fa-copy"></i>
                                        </button>
                                    </div>
                                </td>
                                <td>
                                    <c:catch var="errCreate">
                                        <fmt:formatDate value="${key.createDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                    </c:catch>
                                    <c:if test="${not empty errCreate}">${key.createDate}</c:if>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${key.expire != null}">
                                            <c:catch var="errExpire">
                                                <fmt:formatDate value="${key.expire}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                            </c:catch>
                                            <c:if test="${not empty errExpire}">${key.expire}</c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">— (Vô thời hạn)</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${key.status == 1}">
                                            <span class="badge badge-success" style="padding: 6px 10px;">
                                                <i class="fas fa-check-circle"></i> Đang chạy
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge badge-danger" style="padding: 6px 10px;">
                                                <i class="fas fa-ban"></i> Thu hồi
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:if test="${key.status == 1}">
                                        <button type="button" onclick="confirmRevoke(${key.idKey})" class="btn btn-warning btn-sm" title="Vô hiệu hóa khóa này">
                                            <i class="fas fa-times-circle"></i> Thu hồi Key
                                        </button>
                                    </c:if>
                                    <c:if test="${key.status != 1}">
                                        <span class="text-muted" style="font-size:12px; font-style: italic;"><i class="fas fa-lock"></i> Khóa đã đóng</span>
                                    </c:if>
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
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/dataTables.bootstrap.min.js"></script>

<script>
    $(document).ready(function() {
        // Tích hợp thư viện DataTable phân trang tiếng Việt giống trang quản lý đơn hàng
        $('#keyTable').DataTable({
            "language": {
                "url": "https://cdn.datatables.net/plug-ins/1.13.4/i18n/vi.json"
            }
        });

        // Xử lý sự kiện Copy nhanh mã chuỗi rút gọn Public Key
        $('.btn-copy').click(function() {
            var keyId = $(this).data('id');
            var textToCopy = $('#pubkey-' + keyId).text().replace('...', '');

            navigator.clipboard.writeText(textToCopy).then(function() {
                $.notify({
                    title: "Sao chép : ",
                    message: "Đã sao chép Public Key vào bộ nhớ tạm!",
                    icon: 'fa fa-check'
                },{
                    type: "success"
                });
            });
        });
    });

    // Xác nhận trước khi hủy kích hoạt Khóa (Revoke Key)
    function confirmRevoke(keyId) {
        swal({
            title: "Cảnh báo thu hồi khóa!",
            text: "Khóa sau khi bị thu hồi (Revoke) sẽ lập tức mất hiệu lực xác thực dữ liệu cho các đơn đặt hàng mới. Bạn có chắc chắn?",
            icon: "warning",
            buttons: ["Quay lại", "Xác nhận thu hồi"],
            dangerMode: true,
        }).then(function(willRevoke) {
            if (willRevoke) {
                window.location.href = '${pageContext.request.contextPath}/admin-public-key?revoke=' + keyId;
            }
        });
    }

    // Thiết lập Đồng hồ hiển thị thời gian thật góc phải màn hình admin
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