<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Lịch Sử Báo Mất Khóa | Quản trị Admin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/templates/admin/doc/css/main.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
    <style>
        .alert-notice {
            background: #1a3d6d;
            color: #fff;
            border-left: 5px solid #0d2447;
        }
        .alert-notice i { color: #fff; }
        .reason-col { max-width: 200px; white-space: normal; font-size: 12px; }
        .key-short { font-family: monospace; font-size: 11px; }
        #reportTable thead.thead-dark th {
            background: #1a3d6d;
            color: #fff;
            border-color: #0d2447;
        }
    </style>
</head>
<body onload="time()" class="app sidebar-mini rtl">

<%@include file="/common/admin/header.jsp"%>
<%@include file="/common/admin/aside.jsp"%>

<main class="app-content">
    <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
            <li class="breadcrumb-item active">
                <a href="#"><b>Lịch Sử Báo Mất Khóa</b></a>
            </li>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">

                    <div class="alert alert-notice mb-3">
                        <i class="fas fa-circle-info"></i>
                        Hệ thống tự động xử lý ngay khi người dùng báo mất khóa (vô hiệu hóa khóa
                        và hủy các đơn hàng liên quan) — trang này chỉ hiển thị <b>lịch sử</b> để
                        tra cứu, không cần admin xác nhận hay từ chối.
                    </div>

                    <%-- Bảng lịch sử báo mất khóa --%>
                    <table class="table table-hover table-bordered" id="reportTable">
                        <thead class="thead-dark">
                        <tr>
                            <th width="40">STT</th>
                            <th>Người dùng</th>
                            <th>Khóa báo mất</th>
                            <th>Thời điểm báo mất</th>
                            <th class="reason-col">Lý do</th>
                            <th width="110">Trạng thái</th>
                            <th>Thời gian xử lý</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="r" items="${reports}" varStatus="loop">
                            <tr>
                                <td class="text-center">${loop.index + 1}</td>
                                <td>
                                    <b>${r.userName}</b><br>
                                    <small class="text-muted">${r.email}</small>
                                </td>
                                <td>
                                    <span class="key-short">${r.publicKeyShort}</span><br>
                                    <small class="text-muted">Key ID: #${r.idKey}</small>
                                </td>
                                <td>
                                    <fmt:formatDate value="${r.reportTime}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </td>
                                <td class="reason-col">
                                    <c:choose>
                                        <c:when test="${not empty r.reason}">${r.reason}</c:when>
                                        <c:otherwise><span class="text-muted font-italic">— Không có</span></c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${r.status == 1}">
                                            <span style="background-color:#1a3d6d; color:#ffffff; padding:6px 10px; display:inline-block; border-radius:4px; font-weight:600;">
                                                    ${r.statusLabel}
                                            </span>
                                        </c:when>
                                        <c:when test="${r.status == 2}">
                                            <span style="background-color:#6c757d; color:#ffffff; padding:6px 10px; display:inline-block; border-radius:4px; font-weight:600;">
                                                    ${r.statusLabel}
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <span style="background-color:#ffc107; color:#212529; padding:6px 10px; display:inline-block; border-radius:4px; font-weight:600;">
                                                    ${r.statusLabel}
                                            </span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.processedAt != null}">
                                            <fmt:formatDate value="${r.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted font-italic">— Chưa xử lý</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reports}">
                            <tr>
                                <td colspan="7" class="text-center text-muted py-4">
                                    <i class="fas fa-inbox fa-2x mb-2"></i><br>
                                    Không có báo cáo nào.
                                </td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>

                </div><%-- tile-body --%>
            </div>
        </div>
    </div>
</main>

<%-- Scripts --%>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/jquery-3.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/popper.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/main.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/pace.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/jquery.dataTables.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/dataTables.bootstrap.min.js"></script>

<script>
    $(document).ready(function () {
        $('#reportTable').DataTable({
            language: { url: "https://cdn.datatables.net/plug-ins/1.13.4/i18n/vi.json" },
            order: [[3, 'desc']] // Sắp xếp theo thời điểm báo mất giảm dần
        });
    });

    function time() {
        var today = new Date();
        var weekday = ["Chủ Nhật","Thứ Hai","Thứ Ba","Thứ Tư","Thứ Năm","Thứ Sáu","Thứ Bảy"];
        var day = weekday[today.getDay()];
        var dd  = String(today.getDate()).padStart(2,'0');
        var mm  = String(today.getMonth()+1).padStart(2,'0');
        var yyyy = today.getFullYear();
        var h   = String(today.getHours()).padStart(2,'0');
        var m   = String(today.getMinutes()).padStart(2,'0');
        var s   = String(today.getSeconds()).padStart(2,'0');
        document.getElementById("clock").innerHTML =
            '<span class="date">' + day + ', ' + dd + '/' + mm + '/' + yyyy +
            ' - ' + h + ' giờ ' + m + ' phút ' + s + ' giây</span>';
        setTimeout("time()", 1000);
    }
</script>
</body>
</html>
