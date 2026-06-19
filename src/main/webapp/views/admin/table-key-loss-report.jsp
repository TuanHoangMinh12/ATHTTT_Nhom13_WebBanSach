<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Thông Báo Mất Khóa | Quản trị Admin</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/templates/admin/doc/css/main.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
    <style>
        .badge-pending  { background:#ffc107; color:#212529; }
        .badge-approved { background:#28a745; color:#fff; }
        .badge-rejected { background:#6c757d; color:#fff; }
        .filter-tabs .btn { border-radius: 20px; font-size: 13px; }
        .filter-tabs .btn.active { font-weight: 600; }
        .reason-col { max-width: 200px; white-space: normal; font-size: 12px; }
        .modal-header-danger { background: #e74c3c; color: #fff; }
        .modal-header-info   { background: #17a2b8; color: #fff; }
        .key-short { font-family: monospace; font-size: 11px; }
        .pending-badge {
            position: absolute;
            top: -6px; right: -8px;
            background: #e74c3c;
            color: #fff;
            border-radius: 50%;
            width: 20px; height: 20px;
            font-size: 11px;
            display: flex; align-items: center; justify-content: center;
            font-weight: bold;
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
                <a href="#"><b>Thông Báo Mất Khóa</b></a>
            </li>
            <c:if test="${pendingCount > 0}">
                <li class="breadcrumb-item">
                    <span class="badge badge-danger">${pendingCount} chờ xử lý</span>
                </li>
            </c:if>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">

                    <%-- Alert kết quả xử lý --%>
                    <c:if test="${not empty alertMsg}">
                        <div class="alert alert-${alertType} alert-dismissible fade show" role="alert"
                             style="border-left: 5px solid;">
                            <strong>Thông báo:</strong> ${alertMsg}
                            <button type="button" class="close" data-dismiss="alert">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </c:if>

                    <%-- Tabs lọc --%>
                    <div class="filter-tabs mb-3 d-flex gap-2 flex-wrap">
                        <a href="${pageContext.request.contextPath}/admin-key-loss-report"
                           class="btn btn-sm ${currentFilter eq 'all' ? 'btn-primary active' : 'btn-outline-primary'}">
                            <i class="fas fa-list"></i> Tất cả
                        </a>
                        <a href="${pageContext.request.contextPath}/admin-key-loss-report?filter=pending"
                           class="btn btn-sm ${currentFilter eq 'pending' ? 'btn-warning active' : 'btn-outline-warning'}"
                           style="position:relative;">
                            <i class="fas fa-clock"></i> Chờ xử lý
                            <c:if test="${pendingCount > 0}">
                                <span class="pending-badge">${pendingCount}</span>
                            </c:if>
                        </a>
                    </div>

                    <%-- Bảng danh sách báo cáo --%>
                    <table class="table table-hover table-bordered" id="reportTable">
                        <thead class="thead-dark">
                        <tr>
                            <th width="40">STT</th>
                            <th>Người dùng</th>
                            <th>Khóa báo mất</th>
                            <th>Thời điểm báo</th>
                            <th class="reason-col">Lý do</th>
                            <th width="110">Trạng thái</th>
                            <th>Thời gian xử lý</th>
                            <th width="160">Thao tác</th>
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
                                    <span class="badge badge-${r.statusBadge}" style="padding:6px 10px;">
                                            ${r.statusLabel}
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${r.processedAt != null}">
                                            <fmt:formatDate value="${r.processedAt}" pattern="dd/MM/yyyy HH:mm"/>
                                            <c:if test="${not empty r.adminNote}">
                                                <br><small class="text-muted">Ghi chú: ${r.adminNote}</small>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted font-italic">— Chưa xử lý</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <c:if test="${r.status == 0}">
                                        <%-- Nút Xác nhận --%>
                                        <button type="button"
                                                class="btn btn-success btn-sm mb-1"
                                                onclick="openApprove(${r.idReport}, '${r.userName}')"
                                                title="Xác nhận mất khóa">
                                            <i class="fas fa-check"></i> Xác nhận
                                        </button>
                                        <%-- Nút Từ chối --%>
                                        <button type="button"
                                                class="btn btn-secondary btn-sm"
                                                onclick="openReject(${r.idReport}, '${r.userName}')"
                                                title="Từ chối báo cáo">
                                            <i class="fas fa-times"></i> Từ chối
                                        </button>
                                    </c:if>
                                    <c:if test="${r.status != 0}">
                                        <span class="text-muted font-italic" style="font-size:12px;">
                                            <i class="fas fa-lock"></i> Đã xử lý
                                        </span>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reports}">
                            <tr>
                                <td colspan="8" class="text-center text-muted py-4">
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

<%-- ── Modal XÁC NHẬN ─────────────────────────────────────────────── --%>
<div class="modal fade" id="approveModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header modal-header-danger">
                <h5 class="modal-title text-white">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Xác nhận mất khóa
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <form action="${pageContext.request.contextPath}/admin-key-loss-report" method="post">
                <input type="hidden" name="action"   value="approve">
                <input type="hidden" name="idReport" id="approveIdReport" value="">
                <div class="modal-body">
                    <div class="alert alert-warning mb-3" style="border-left:4px solid #e67e22;">
                        <strong>Hậu quả khi xác nhận:</strong>
                        <ul class="mb-0 mt-1">
                            <li>Public key của <b id="approveUserName"></b> sẽ bị <strong>thu hồi ngay</strong>.</li>
                            <li>Đơn hàng ký <strong>sau</strong> thời điểm báo mất → <span class="text-danger">trạng thái Lỗi</span>.</li>
                            <li>Đơn hàng ký <strong>trước</strong> thời điểm báo mất → <span class="text-success">vẫn hợp lệ</span>.</li>
                            <li>Người dùng có thể tạo cặp khóa mới sau khi xử lý.</li>
                        </ul>
                    </div>
                    <div class="form-group">
                        <label for="approveNote"><strong>Ghi chú của admin (tùy chọn):</strong></label>
                        <textarea class="form-control" id="approveNote" name="adminNote"
                                  rows="3" placeholder="Ví dụ: Đã xác minh qua email, xác nhận mất khóa..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy bỏ</button>
                    <button type="submit" class="btn btn-danger">
                        <i class="fas fa-check-circle"></i> Xác nhận mất khóa
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<%-- ── Modal TỪ CHỐI ──────────────────────────────────────────────── --%>
<div class="modal fade" id="rejectModal" tabindex="-1" role="dialog">
    <div class="modal-dialog modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header modal-header-info">
                <h5 class="modal-title text-white">
                    <i class="fas fa-ban me-2"></i> Từ chối báo cáo
                </h5>
                <button type="button" class="close text-white" data-dismiss="modal">
                    <span>&times;</span>
                </button>
            </div>
            <form action="${pageContext.request.contextPath}/admin-key-loss-report" method="post">
                <input type="hidden" name="action"   value="reject">
                <input type="hidden" name="idReport" id="rejectIdReport" value="">
                <div class="modal-body">
                    <p>Bạn đang <strong>từ chối</strong> báo cáo mất khóa của <b id="rejectUserName"></b>.</p>
                    <p class="text-muted" style="font-size:13px;">
                        Khóa sẽ <strong>không bị thu hồi</strong>. Đơn hàng không bị ảnh hưởng.
                    </p>
                    <div class="form-group">
                        <label for="rejectNote"><strong>Lý do từ chối <span class="text-danger">*</span>:</strong></label>
                        <textarea class="form-control" id="rejectNote" name="adminNote"
                                  rows="3" required
                                  placeholder="Ví dụ: Không có bằng chứng, thông tin không khớp..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-dismiss="modal">Hủy bỏ</button>
                    <button type="submit" class="btn btn-info text-white">
                        <i class="fas fa-times-circle"></i> Xác nhận từ chối
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

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
            order: [[3, 'desc']] // Sắp xếp theo thời gian báo giảm dần
        });
    });

    function openApprove(idReport, userName) {
        $('#approveIdReport').val(idReport);
        $('#approveUserName').text(userName);
        $('#approveNote').val('');
        $('#approveModal').modal('show');
    }

    function openReject(idReport, userName) {
        $('#rejectIdReport').val(idReport);
        $('#rejectUserName').text(userName);
        $('#rejectNote').val('');
        $('#rejectModal').modal('show');
    }

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
