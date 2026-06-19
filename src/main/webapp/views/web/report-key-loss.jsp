<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Báo Mất Khóa | Web Bán Sách</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- Dùng đúng bộ CSS/CDN mà account.jsp, changePassword.jsp đang dùng trong project --%>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css"
          integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"/>
    <link rel="stylesheet" href="<c:url value='/templates/styles/Header.css'/>" />
    <link rel="stylesheet" href="<c:url value='/templates/styles/AccountPage.css'/>" />
    <link rel="stylesheet" href="<c:url value='/templates/styles/Footer.css'/>" />
    <style>
        .key-loss-card {
            max-width: 680px;
            margin: 40px auto;
            border-radius: 12px;
            box-shadow: 0 4px 24px rgba(0,0,0,.10);
        }
        .key-loss-card .card-header {
            background: linear-gradient(135deg, #e74c3c, #c0392b);
            color: #fff;
            border-radius: 12px 12px 0 0;
            padding: 20px 28px;
        }
        .key-loss-card .card-header h4 { margin: 0; font-size: 1.25rem; }
        .key-badge { font-family: monospace; font-size: 12px; background: #f8f9fa;
            padding: 4px 10px; border-radius: 4px; border: 1px solid #dee2e6; word-break: break-all; }
        .warning-box { background: #fff3cd; border-left: 4px solid #ffc107;
            padding: 14px 18px; border-radius: 4px; margin-bottom: 20px; }
        .warning-box ul { margin: 8px 0 0 0; padding-left: 18px; }
        .warning-box li { margin-bottom: 4px; }
    </style>
</head>
<body>
<%@include file="/common/web/header.jsp"%>

<div class="container py-5">
    <div class="card key-loss-card">
        <div class="card-header">
            <h4><i class="fas fa-exclamation-triangle mr-2"></i> Báo Mất Khóa Bí Mật (Private Key)</h4>
            <small style="opacity:.85">Thông báo cho quản trị viên khi bạn mất hoặc bị lộ private key</small>
        </div>
        <div class="card-body p-4">

            <%-- Thông báo thành công --%>
            <c:if test="${param.success eq '1'}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="fas fa-check-circle mr-1"></i>
                    <strong>Gửi thành công!</strong> Yêu cầu của bạn đã được gửi đến admin.
                    Bạn sẽ được thông báo khi admin xử lý. Trong thời gian chờ,
                    <strong>hãy ngừng sử dụng private key đó</strong>.
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>

            <%-- Thông báo lỗi --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-times-circle mr-1"></i> ${error}
                    <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>
            </c:if>

            <%-- Cảnh báo giải thích hậu quả --%>
            <div class="warning-box">
                <strong><i class="fas fa-shield-alt"></i> Điều gì xảy ra khi admin xác nhận?</strong>
                <ul>
                    <li>Khóa bí mật sẽ bị <strong>vô hiệu hóa ngay lập tức</strong>.</li>
                    <li>Đơn hàng được ký <strong>trước</strong> thời điểm bạn báo mất → <span class="text-success">vẫn hợp lệ</span>.</li>
                    <li>Đơn hàng được ký <strong>sau</strong> thời điểm báo mất → <span class="text-danger">chuyển trạng thái lỗi / hủy</span>.</li>
                    <li>Bạn có thể <strong>tạo cặp khóa mới</strong> sau khi admin xử lý.</li>
                </ul>
            </div>

            <%-- Kiểm tra có key active không --%>
            <c:choose>
                <c:when test="${empty activeKeys}">
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle mr-1"></i>
                        Bạn hiện không có khóa nào đang hoạt động. Không cần báo mất.
                    </div>
                </c:when>
                <c:otherwise>
                    <form action="<c:url value='/report-key-loss'/>" method="post">

                        <div class="form-group">
                            <label class="font-weight-bold">Chọn khóa bị mất <span class="text-danger">*</span></label>
                            <c:forEach var="key" items="${activeKeys}">
                                <div class="form-check border rounded p-3 mb-2 bg-light">
                                    <input class="form-check-input" type="radio"
                                           name="idKey" id="key_${key.idKey}"
                                           value="${key.idKey}" required>
                                    <label class="form-check-label w-100" for="key_${key.idKey}">
                                        <div class="d-flex justify-content-between align-items-start flex-wrap">
                                            <div>
                                                <span class="key-badge">${key.publicKeyShort}</span>
                                            </div>
                                            <small class="text-muted mt-1">
                                                Tạo:
                                                <fmt:formatDate value="${key.createDate}" pattern="dd/MM/yyyy HH:mm"/>
                                            </small>
                                        </div>
                                    </label>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="form-group">
                            <label for="reason" class="font-weight-bold">Mô tả tình huống mất khóa</label>
                            <textarea class="form-control" id="reason" name="reason"
                                      rows="4" maxlength="1000"
                                      placeholder="Ví dụ: Máy tính bị đánh cắp, file key bị xóa nhầm, bị lộ qua email..."></textarea>
                            <small class="form-text text-muted">Thông tin này giúp admin xử lý chính xác hơn (không bắt buộc).</small>
                        </div>

                        <div class="d-flex" style="gap: 8px;">
                            <button type="submit" class="btn btn-danger px-4"
                                    onclick="return confirm('Bạn xác nhận gửi báo cáo mất khóa này?')">
                                <i class="fas fa-paper-plane mr-1"></i> Gửi Báo Cáo
                            </button>
                            <a href="<c:url value='/account'/>" class="btn btn-outline-secondary px-4">
                                Quay lại
                            </a>
                        </div>
                    </form>
                </c:otherwise>
            </c:choose>

        </div><%-- card-body --%>
    </div><%-- card --%>
</div>

<%@include file="/common/web/footer.jsp"%>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.5.1/dist/jquery.slim.min.js"
        integrity="sha384-DfXdz2htPH0lsSSs5nCTpuj/zy4C+OGpamoFVy38MVBnE+IbbVYUew+OrCXaRkfj"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"
        integrity="sha384-9/reFTGAW83EW2RDu2S0VKaIzap3H66lZH81PoYlFhbGU+6BZp6G7niu735Sk7lN"
        crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.min.js"
        integrity="sha384-+sLIOodYLS7CIrQpBjl+C7nPvqq+FbNUBDunl/OZv93DB7Ln/533i8e/mZXLi/P+"
        crossorigin="anonymous"></script>
</body>
</html>