<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Quản Lý Khóa</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css"
          integrity="sha384-xOolHFLEh07PJGoPkLv1IbcEPTNtaed2xpHsD9ESMhqIYd0nLMwNLD69Npy4HI+N" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.0/css/all.min.css"/>
    <link rel="stylesheet" href="<c:url value='/templates/styles/Header.css'/> " />
    <link rel="stylesheet" href="<c:url value='/templates/styles/AccountPage.css'/> " />
    <link rel="stylesheet" href="<c:url value='/templates/styles/Footer.css'/> " />
    <style>
        .key-card {
            border: 1px solid #e2e2e2;
            border-radius: 6px;
            padding: 18px 20px;
            margin-bottom: 16px;
            background: #fff;
        }
        .key-card h4 {
            font-size: 16px;
            font-weight: 700;
            margin-bottom: 10px;
        }
        .key-card h4 i { margin-right: 6px; }
        .key-status-active {
            display: inline-block; padding: 4px 10px; border-radius: 4px;
            background: #e6f7ed; color: #1a9e4d; font-size: 13px; font-weight: 600;
        }
        .key-status-none {
            display: inline-block; padding: 4px 10px; border-radius: 4px;
            background: #fdecea; color: #c0392b; font-size: 13px; font-weight: 600;
        }
        code.key-block {
            display: block; word-break: break-all; background: #f7f7f7;
            padding: 10px; border-radius: 4px; font-size: 12px; margin-top: 8px;
            max-height: 120px; overflow-y: auto;
        }
        .private-key-box {
            border: 2px solid #f39c12; background: #fffaf0; border-radius: 6px;
            padding: 16px; margin-bottom: 18px;
        }
        .private-key-box h4 { color: #c0742b; }
        .btn-copy-private {
            margin-top: 8px;
        }
        .feature-actions .btn { margin-right: 8px; margin-bottom: 6px; }
    </style>
</head>
<body>
<!-- header -->
<%@include file="/common/web/header.jsp"%>

<div id="content">
    <div class="wrapper">
        <div class="form_ctrl">

            <%-- ── Menu trái: dùng chung với account.jsp ───────────────────── --%>
            <div class="acc_ctrl m_r12">
                <h2>Tài khoản</h2>
                <div class="list_ctrl">
                    <ul>
                        <li class="first">
                            <a id="account" title="Thông tin tài khoản" href="${pageContext.request.contextPath}/account?action=account">Thông tin tài khoản</a></li>
                        <li class="first">
                            <a id="changePassword" title="Đổi mật khẩu" href="${pageContext.request.contextPath}/account?action=changePassword">Đổi mật khẩu</a></li>
                        <li class="first">
                            <a id="reviewOrders" title="Xem lại đơn hàng" href="${pageContext.request.contextPath}/account?action=reviewOrders">Xem lại đơn hàng</a></li>
                        <li class="first active">
                            <a id="keyManagement" title="Quản lý khóa" href="${pageContext.request.contextPath}/key-management">Quản Lý Khóa</a>
                        </li>
                        <li class="first">
                            <a id="logout" title="Đăng xuất" href="${pageContext.request.contextPath}/logout?action=logout">Đăng xuất</a>
                        </li>
                    </ul>
                </div>
            </div>

            <%-- ── Nội dung chính: Quản Lý Khóa ─────────────────────────────── --%>
            <div class="col_1_1">
                <div class="frm_content">
                    <h2>Quản Lý Khóa Bảo Mật (RSA)</h2>

                    <%-- Thông báo kết quả thao tác --%>
                    <c:if test="${not empty alertMsg}">
                        <div class="alert alert-${alertType} alert-dismissible fade show mt-3" role="alert">
                            <strong>Thông báo:</strong> ${alertMsg}
                            <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                    </c:if>

                    <%-- Private key vừa tạo: hiển thị MỘT LẦN duy nhất ─────────────── --%>
                    <c:if test="${not empty newPrivateKey}">
                        <div class="private-key-box">
                            <h4><i class="fas fa-triangle-exclamation"></i> Private Key của bạn — CHỈ HIỂN THỊ MỘT LẦN</h4>
                            <p class="mb-1">
                                Hãy sao chép và lưu private key này ở nơi an toàn (offline). Hệ thống
                                <b>không lưu trữ</b> private key trên server — nếu mất, bạn sẽ phải báo mất khóa.
                            </p>
                            <code class="key-block" id="newPrivateKeyText">${newPrivateKey}</code>
                            <button type="button" class="btn btn-warning btn-sm btn-copy-private" onclick="copyPrivateKey()">
                                <i class="far fa-copy"></i> Sao chép Private Key
                            </button>
                        </div>
                    </c:if>

                    <%-- 1) TRẠNG THÁI KHÓA HIỆN TẠI ─────────────────────────────────── --%>
                    <div class="key-card">
                        <h4><i class="fas fa-key"></i> Trạng thái khóa hiện tại</h4>
                        <c:choose>
                            <c:when test="${not empty activeKey}">
                                <span class="key-status-active"><i class="fas fa-check-circle"></i> Đang hoạt động</span>
                                <p class="mt-2 mb-1"><b>Ngày kích hoạt:</b>
                                    <fmt:formatDate value="${activeKey.createDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                                </p>
                                <p class="mb-1"><b>Public Key:</b></p>
                                <code class="key-block">${activeKey.publicKey}</code>
                            </c:when>
                            <c:otherwise>
                                <span class="key-status-none"><i class="fas fa-ban"></i> Chưa có khóa hoạt động</span>
                                <p class="mt-2 text-muted">
                                    Bạn cần tạo cặp khóa mới để có thể đặt hàng / xác thực giao dịch trên hệ thống.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- 2) BÁO MẤT KHÓA ────────────────────────────────────────────── --%>
                    <div class="key-card">
                        <h4><i class="fas fa-exclamation-triangle text-danger"></i> Báo Mất Khóa Bí Mật</h4>
                        <p class="text-muted mb-2">
                            Nếu private key của bạn bị mất hoặc bị lộ, hãy báo ngay để admin xử lý.
                            Sau khi admin xác nhận, khóa hiện tại sẽ bị thu hồi và các đơn hàng tạo
                            <b>sau</b> thời điểm báo mất sẽ bị hủy để đảm bảo an toàn.
                        </p>
                        <c:choose>
                            <c:when test="${not empty activeKey}">
                                <a href="${pageContext.request.contextPath}/report-key-loss" class="btn btn-outline-danger btn-sm">
                                    <i class="fas fa-exclamation-triangle me-1"></i> Báo Mất Khóa Bí Mật
                                </a>
                            </c:when>
                            <c:otherwise>
                                <button class="btn btn-outline-secondary btn-sm" disabled>
                                    Không có khóa nào để báo mất
                                </button>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <%-- 3) TẠO KHÓA MỚI (chỉ hiện khi CHƯA có khóa active) ──────────── --%>
                    <c:if test="${empty activeKey}">
                        <div class="key-card" style="border-left:4px solid #f39c12;">
                            <h4><i class="fas fa-plus-circle text-warning"></i> Tạo Cặp Khóa Mới</h4>
                            <p class="text-muted mb-2">
                                Bạn hiện chưa có khóa hoạt động (có thể do khóa cũ đã bị admin thu hồi
                                sau khi xác nhận báo mất). Tạo cặp khóa mới để tiếp tục sử dụng các
                                chức năng cần xác thực trên hệ thống.
                            </p>
                            <form method="POST" action="${pageContext.request.contextPath}/key-management"
                                  onsubmit="return confirm('Bạn chắc chắn muốn tạo cặp khóa mới?');">
                                <input type="hidden" name="action" value="generate">
                                <button type="submit" class="btn btn-warning btn-sm">
                                    <i class="fas fa-key"></i> Tạo cặp khóa mới
                                </button>
                            </form>
                        </div>
                    </c:if>

                    <%-- 4) CẬP NHẬT / THAY KHÓA CŨ (chỉ hiện khi ĐANG có khóa active) - --%>
                    <c:if test="${not empty activeKey}">
                        <div class="key-card" style="border-left:4px solid #1a94ff;">
                            <h4><i class="fas fa-rotate text-primary"></i> Cập Nhật Khóa (Đổi Khóa Mới)</h4>
                            <p class="text-muted mb-2">
                                Chủ động tạo khóa mới để thay thế khóa hiện tại. Khóa cũ sẽ
                                <b>hết hiệu lực ngay lập tức</b> sau khi cập nhật. Chỉ dùng khi bạn vẫn
                                còn giữ quyền kiểm soát tài khoản (không phải trường hợp mất khóa —
                                trường hợp mất khóa hãy dùng chức năng "Báo Mất Khóa" ở trên).
                            </p>
                            <form method="POST" action="${pageContext.request.contextPath}/key-management"
                                  onsubmit="return confirm('Khóa cũ sẽ hết hiệu lực ngay lập tức. Bạn chắc chắn muốn cập nhật khóa mới?');">
                                <input type="hidden" name="action" value="renew">
                                <button type="submit" class="btn btn-primary btn-sm">
                                    <i class="fas fa-rotate"></i> Cập nhật khóa mới
                                </button>
                            </form>
                        </div>
                    </c:if>

                </div>
            </div>
        </div>
    </div>
</div>
<!-----footer------>
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
<script src="/templates/scripts/header.js"></script>
<script>
    function copyPrivateKey() {
        var text = document.getElementById('newPrivateKeyText').innerText;
        navigator.clipboard.writeText(text).then(function () {
            alert('Đã sao chép Private Key vào bộ nhớ tạm. Hãy lưu lại ở nơi an toàn!');
        });
    }
</script>
</body>
</html>
