<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <title>Chi tiết đơn hàng | Quản trị Admin</title>
    <meta charset="UTF-8">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/templates/admin/doc/css/main.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" href="https://unpkg.com/boxicons@latest/css/boxicons.min.css">
    <link rel="stylesheet" type="text/css" href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.2.1/css/all.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/sweetalert/2.1.2/sweetalert.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.css">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

    <style>
        /* ── Verify result box ── */
        .verify-box {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px 20px;
            border-radius: 8px;
            font-size: 16px;
            font-weight: 500;
            margin: 20px 0;
            border-left: 5px solid;
        }
        .verify-box i { font-size: 24px; }
        .verify-ok   { background: #d4edda; color: #155724; border-color: #28a745; }
        .verify-fail { background: #f8d7da; color: #721c24; border-color: #dc3545; }
        .verify-none { background: #e2e3e5; color: #383d41; border-color: #6c757d; }

        /* ── Verify button ── */
        .btn-verify {
            background-color: #2c7be5;
            color: #fff;
            border: none;
            padding: 10px 28px;
            font-size: 15px;
            border-radius: 6px;
            cursor: pointer;
        }
        .btn-verify:hover { background-color: #1a5bbf; color: #fff; }

        .parent-button {
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 24px 0 8px;
        }
    </style>
</head>

<body onload="time()" class="app sidebar-mini rtl">
<%@include file="/common/admin/header.jsp"%>
<%@include file="/common/admin/aside.jsp"%>

<main class="app-content">
    <div class="app-title">
        <ul class="app-breadcrumb breadcrumb side">
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/admin-table-order">Danh sách đơn hàng</a>
            </li>
            <li class="breadcrumb-item active"><b>Chi tiết đơn hàng #${id}</b></li>
        </ul>
        <div id="clock"></div>
    </div>

    <div class="row">
        <%-- Toolbar --%>
        <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">
                    <div class="row element-button">
                        <div class="col-sm-2">
                            <a class="btn btn-excel btn-sm" href="" title="Xuất Excel">
                                <i class="fas fa-file-excel"></i> Xuất Excel
                            </a>
                        </div>
                        <div class="col-sm-2">
                            <a href="${pageContext.request.contextPath}/exportFIlePDFOrder?id=${id}"
                               class="btn btn-delete btn-sm" title="Xuất PDF">
                                <i class="fas fa-file-pdf"></i> Xuất PDF
                            </a>
                        </div>
                        <div class="col-sm-8">
                            <c:if test="${not empty message}">
                                <div class="alert alert-${alert}" role="alert">${message}</div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <form action="${pageContext.request.contextPath}/admin-order-detail?id=${id}" method="post" >
            <div class="col-md-12">
            <div class="tile">
                <div class="tile-body">
                    <div class="container" style="min-height: 600px">

                        <h1 class="text-center my-4">Chi tiết đơn hàng</h1>

                        <%-- KẾT QUẢ VERIFY
                             verifyResult được set bởi OrderDetailController:
                               "OK"    → Đã xác thực
                               "FAIL"  → Đơn hàng đã bị chỉnh sửa
                               null    → chưa nhấn Verify --%>
                        <c:choose>
                            <c:when test="${verifyResult == 'OK'}">
                                <div class="verify-box verify-ok">
                                    <i class="fas fa-check-circle"></i>
                                    <div>
                                        <strong>Chữ ký số: HỢP LỆ</strong><br>
                                        <small>Hệ thống đối chiếu thành công chữ ký số của khách hàng từ Tool ký ngoài — Dữ liệu đơn hàng đảm bảo toàn vẹn 100%.</small>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${verifyResult == 'FAIL'}">
                                <div class="verify-box verify-fail">
                                    <i class="fas fa-exclamation-triangle"></i>
                                    <div>
                                        <strong>Cảnh báo: CHỮ KÝ KHÔNG HỢP LỆ!</strong><br>
                                        <small>Dữ liệu hiện tại trong hệ thống đã bị chỉnh sửa lén so với dữ liệu gốc ban đầu lúc khách hàng thực hiện ký bằng Tool ngoài.</small>
                                    </div>
                                </div>
                            </c:when>
                            <c:when test="${verifyResult == 'ERROR'}">
                                <div class="verify-box verify-none">
                                    <i class="fas fa-question-circle"></i>
                                    <div>
                                        <strong>Không thể tiến hành xác thực</strong><br>
                                        <small>${verifyError}</small>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <%-- Chưa nhấn nút Verify: Để trống không hiển thị hộp thoại kết quả --%>
                            </c:otherwise>
                        </c:choose>

                        <%-- Thông tin khách hàng & đơn hàng --%>
                        <div class="row">
                            <div class="col-md-6">
                                <h2>Thông tin khách hàng</h2>
                                <table class="table">
                                    <tbody>
                                    <tr>
                                        <td>Tên khách hàng:</td>
                                        <td>${CUSTOMER.firstName} ${CUSTOMER.lastName}</td>
                                    </tr>
                                    <tr>
                                        <td>Địa chỉ:</td>
                                        <td>${cart.bills.get(0).address}</td>
                                    </tr>
                                    <tr>
                                        <td>Số điện thoại:</td>
                                        <td>${cart.bills.get(0).phone}</td>
                                    </tr>
                                    <tr>
                                        <td>Email:</td>
                                        <td>${CUSTOMER.email}</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>

                            <div class="col-md-6">
                                <h2>Thông tin đơn hàng</h2>
                                <table class="table">
                                    <tbody>
                                    <tr>
                                        <td>Mã đơn hàng:</td>
                                        <td><b>${cart.id}</b></td>
                                    </tr>
                                    <tr>
                                        <td>Ngày đặt hàng:</td>
                                        <td>${cart.createTime}</td>
                                    </tr>
                                    <tr>
                                        <td>Ngày dự kiến giao:</td>
                                        <td>${cart.timeShip}</td>
                                    </tr>
                                    <tr>
                                        <td>Đóng gói:</td>
                                        <td>${cart.bills.get(0).pack}</td>
                                    </tr>
                                    <tr>
                                        <td>Phương thức thanh toán:</td>
                                        <td>${cart.bills.get(0).paymentMethod}</td>
                                    </tr>
                                    <tr>
                                        <td>Ghi chú:</td>
                                        <td>${cart.bills.get(0).info}</td>
                                    </tr>
                                    <tr>
                                        <td>Tổng giá trị:</td>
                                        <td>
                                            <b>
                                                <fmt:formatNumber value="${cart.getTotalPriceFromCart()}" type="number" groupingUsed="true"/> VNĐ
                                            </b>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Tình trạng:</td>
                                        <td>${cart.getInFoShipString()}</td>
                                    </tr>
                                    <tr>
                                        <td>Đăng ký giao hàng:</td>
                                        <td>
                                            <c:if test="${cart.getInFoShipString().equals('Chờ xử lý')}">
                                                <button type="button"
                                                        class="btn btn-danger btn-sm btn-register-ghn"
                                                        data-id="${cart.id}"
                                                        data-cus="${CUSTOMER.idUser}">
                                                    Đăng ký đơn hàng
                                                </button>
                                                <button type="button"
                                                        class="btn btn-warning btn-sm btn-cancel-order"
                                                        data-id="${cart.id}">
                                                    Hủy đơn
                                                </button>
                                            </c:if>
                                            <c:if test="${cart.getInFoShipString().equals('Đang vận chuyển')}">
                                                <button type="button"
                                                        class="btn btn-success btn-sm btn-confirm-delivered"
                                                        data-id="${cart.id}"
                                                        data-cus="${CUSTOMER.idUser}">
                                                    Xác nhận Đã giao
                                                </button>
                                                <button type="button"
                                                        class="btn btn-warning btn-sm btn-cancel-order"
                                                        data-id="${cart.id}">
                                                    Hủy đơn
                                                </button>
                                            </c:if>
                                            <c:if test="${cart.getInFoShipString().equals('Đã hoàn thành')}">
                                                <span class="text-success"><i class="fas fa-check"></i> Đơn hàng đã giao thành công</span>
                                            </c:if>
                                            <c:if test="${cart.getInFoShipString().equals('Đã hủy')}">
                                                <span class="text-danger" style="font-weight: 500;"><i class="fas fa-times-circle"></i> Đơn hàng đã hủy</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <%-- Danh sách sản phẩm trong đơn --%>
                        <div class="row">
                            <div class="col-md-12">
                                <h2>Danh sách sản phẩm</h2>
                                <table class="table">
                                    <thead>
                                    <tr>
                                        <th>Tên sản phẩm</th>
                                        <th>Ảnh</th>
                                        <th>Số lượng</th>
                                        <th>Tổng tiền</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="item" items="${LISTBILL}">
                                        <tr>
                                            <td>${item.nameSach}</td>
                                            <td>
                                                <img style="height:50px"
                                                     src="${pageContext.request.contextPath}/${item.image}"
                                                     alt="${item.nameSach}">
                                            </td>
                                            <td>${item.quantity}</td>
                                            <td>
                                                <fmt:formatNumber value="${item.totalPrice}" type="number" groupingUsed="true"/> VNĐ
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>

                                <%--
                                   TASK 3 — NÚT [Verify]
                                     Chỉ hiển thị khi đơn chưa bị hủy (inShip != 4) và chưa verify OK
                                   --%>
                                <%-- Nút kích hoạt lệnh Xác thực: Chỉ hiển thị khi đơn chưa hủy và chưa xác thực thành công --%>
                                <c:if test="${cart.getInShip() != 4 && verifyResult != 'OK'}">
                                    <div class="parent-button">
                                        <button type="submit" class="btn-verify">
                                            <i class="fas fa-shield-alt"></i> Verify đơn hàng
                                        </button>
                                    </div>
                                </c:if>

                                <c:if test="${verifyResult == 'OK'}">
                                    <div class="parent-button">
                                        <span class="text-success" style="font-size:15px; font-weight: 500;">
                                            <i class="fas fa-check-circle"></i> Hệ thống đã hoàn thành xác thực đơn hàng này.
                                        </span>
                                    </div>
                                </c:if>
                            </div>
                        </div>

                    </div>
                </div>
            </div>
        </div>
        </form>
    </div>
</main>

<script src="${pageContext.request.contextPath}/templates/admin/doc/js/jquery-3.2.1.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/popper.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/main.js"></script>
<script src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/pace.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-confirm/3.3.2/jquery-confirm.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/templates/admin/doc/js/plugins/dataTables.bootstrap.min.js"></script>

<script>
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

<script>

    $(document).ready(function () {

        // 1. XỬ LÝ NÚT ĐĂNG KÝ GIAO HÀNG (ẨN CHÍNH NÓ VÀ XỔ RA 2 NÚT MỚI)
        $(document).on('click', '.btn-register-ghn', function (e) {
            e.preventDefault();

            // ĐIỀU KIỆN BẢO MẬT: Kiểm tra xem đơn hàng đã được Xác thực chữ ký số chưa
            var isVerified = "${verifyResult}";
            if (isVerified !== "OK") {
                swal("Hành động bị chặn!", "Đơn hàng này chưa được xác thực chữ ký số hoặc chữ ký không hợp lệ. Bạn phải bấm nút [Verify đơn hàng] trước khi giao cho đối tác vận chuyển!", "error");
                return;
            }

            var orderId = $(this).data('id');
            var cusId = $(this).data('cus');
            var $btn = $(this);
            var $tdContainer = $btn.parent(); // Thẻ <td> chứa cụm nút bấm

            // Khóa nút tạm thời để chống bấm trùng
            $btn.prop('disabled', true).addClass('disabled').text('Đang xử lý...');

            swal({
                title: "Đang xử lý...",
                text: "Hệ thống đang tiến hành đăng ký vận đơn ngầm với GHN, vui lòng đợi.",
                icon: "info",
                buttons: false,
                closeOnClickOutside: false,
                closeOnEsc: false
            });

            $.ajax({
                url: '${pageContext.request.contextPath}/admin-register-order',
                type: 'POST',
                data: {id: orderId, variable: cusId},
                success: function (response) {
                    if (response.trim() === "processing") {
                        swal({
                            title: "Thành công!",
                            text: "Đơn hàng đã được xếp lịch vận chuyển ngầm thành công!",
                            icon: "success",
                            button: "Đóng"
                        }).then(() => {
                            // ── ĐỔI GIAO DIỆN TẠI CHỖ CHUẨN XÁC THEO Ý BẠN ──

                            // 1. Cập nhật text Tình trạng hiển thị từ "Chờ xử lý" sang "Đang vận chuyển"
                            $('td:contains("Tình trạng:")').next().html('<span class="text-warning" style="font-weight:500;">Đang vận chuyển</span>');

                            // 2. Ẩn hẳn nút Đăng ký đơn hàng đi và "xổ" ra cặp nút mới: Xác nhận Đã giao & Hủy đơn
                            var newButtons =
                                '<button type="button" class="btn btn-success btn-sm btn-confirm-delivered" data-id="' + orderId + '" data-cus="' + cusId + '" style="margin-right: 5px;">' +
                                '   Xác nhận Đã giao' +
                                '</button>' +
                                '<button type="button" class="btn btn-warning btn-sm btn-cancel-order" data-id="' + orderId + '">' +
                                '   Hủy đơn' +
                                '</button>';

                            $tdContainer.html(newButtons);
                        });
                    } else {
                        swal("Lỗi", "Không thể xử lý yêu cầu giao hàng.", "error");
                        $btn.prop('disabled', false).removeClass('disabled').text('Đăng ký đơn hàng');
                    }
                },
                error: function () {
                    swal("Lỗi kết nối", "Không thể gửi dữ liệu lên hệ thống.", "error");
                    $btn.prop('disabled', false).removeClass('disabled').text('Đăng ký đơn hàng');
                }
            });
        });

        // 2. XỬ LÝ NÚT XÁC NHẬN ĐÃ GIAO (CÓ CHẶN BẢO MẬT KHI CHỮ KÝ INVALID)
        $(document).on('click', '.btn-confirm-delivered', function (e) {
            e.preventDefault();

            // ĐIỀU KIỆN BẢO MẬT: Chặn không cho hoàn thành đơn hàng nếu chữ ký không hợp lệ
            var isVerified = "${verifyResult}";
            if (isVerified !== "OK") {
                swal("Cảnh báo bảo mật lỗi!", "Đơn hàng này phát hiện có sự sai lệch dữ liệu hệ thống (Chữ ký Invalid). Vui lòng không thực hiện hoàn thành đơn hàng này và liên hệ quản trị viên!", "error");
                return;
            }

            var orderId = $(this).data('id');
            var cusId = $(this).data('cus');
            var $btn = $(this);

            $btn.prop('disabled', true).addClass('disabled').text('Đang lưu...');

            $.ajax({
                url: '${pageContext.request.contextPath}/confirmBill',
                type: 'POST',
                data: {id: orderId, variable: cusId},
                success: function (response) {
                    if (response.trim() === "success") {
                        swal("Thành công!", "Đơn hàng đã được xác nhận hoàn thành!", "success")
                            .then(() => {
                                window.location.reload(); // Đã giao hoàn tất thành công thì reload trang lại để đóng đơn
                            });
                    } else {
                        swal("Lỗi", "Không thể cập nhật trạng thái đơn hàng.", "error");
                        $btn.prop('disabled', false).removeClass('disabled').text('Xác nhận Đã giao');
                    }
                },
                error: function () {
                    swal("Lỗi kết nối", "Không thể gửi dữ liệu lên hệ thống.", "error");
                    $btn.prop('disabled', false).removeClass('disabled').text('Xác nhận Đã giao');
                }
            });
        });

        // 3. XỬ LÝ NÚT HỦY ĐƠN HÀNG
        $(document).on('click', '.btn-cancel-order', function (e) {
            e.preventDefault();
            var orderId = $(this).data('id');
            var $btn = $(this);

            swal({
                title: "Xác nhận hủy đơn?",
                text: "Bạn có chắc chắn muốn hủy đơn hàng #" + orderId + " này không?",
                icon: "warning",
                buttons: ["Quay lại", "Đồng ý hủy"],
                dangerMode: true,
            }).then((willDelete) => {
                if (willDelete) {
                    $btn.prop('disabled', true).addClass('disabled').text('Đang hủy...');

                    $.ajax({
                        url: '${pageContext.request.contextPath}/removerBill',
                        type: 'POST',
                        data: {id: orderId},
                        success: function(response) {
                            if(response.trim() === "success") {
                                swal("Đã hủy!", "Đơn hàng đã được chuyển sang trạng thái hủy thành công.", "success")
                                    .then(() => {
                                        window.location.href = window.location.pathname + "?id=" + orderId + "&t=" + new Date().getTime();
                                    });
                            } else {
                                swal("Lỗi", response, "error");
                                $btn.prop('disabled', false).removeClass('disabled').text('Hủy đơn');
                            }
                        },
                        error: function (xhr) {
                            swal("Lỗi hệ thống", xhr.responseText, "error");
                            $btn.prop('disabled', false).removeClass('disabled').text('Hủy đơn');
                        }
                    });
                }
            });
        });

    });

</script>


</body>
</html>
