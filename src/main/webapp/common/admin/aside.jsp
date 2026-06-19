<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    int pendingLossCount = new vn.edu.hcmuaf.fit.dao.impl.PublicKeyDao().countPendingLossReports();
    request.setAttribute("pendingLossCount", pendingLossCount);
%>
<!-- Sidebar menu-->
<div class="app-sidebar__overlay" data-toggle="sidebar"></div>
<aside class="app-sidebar">
    <div class="app-sidebar__user"><img class="app-sidebar__user-avatar" src="${pageContext.request.contextPath}/templates/images/img_admin.png" width="50px"
                                        alt="User Image">
        <div>
            <p class="app-sidebar__user-name"><b>Mod</b></p>
            <p class="app-sidebar__user-designation">Chào mừng bạn trở lại</p>
        </div>
    </div>
    <hr>
    <ul class="app-menu">
        <c:if test="${title.equals('Bảng điều khiển')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-home" />"><i class='app-menu__icon bx bx-tachometer'></i><span
                    class="app-menu__label">Bảng điều khiển</span></a></li>
        </c:if>
        <c:if test="${!title.equals('Bảng điều khiển')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-home" />"><i class='app-menu__icon bx bx-tachometer'></i><span
                    class="app-menu__label">Bảng điều khiển</span></a></li>
        </c:if>

        <c:if test="${title.equals('Danh Sách Khách Hàng')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-table-customer" />"><i class='app-menu__icon bx bx-user-voice'></i><span
                    class="app-menu__label">Quản lý khách hàng</span></a></li>
        </c:if>
        <c:if test="${!title.equals('Danh Sách Khách Hàng')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-table-customer" />"><i class='app-menu__icon bx bx-user-voice'></i><span
                    class="app-menu__label">Quản lý khách hàng</span></a></li>
        </c:if>

        <c:if test="${title.equals('Danh Sách Sản Phẩm')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-table-product" />"><i
                    class='app-menu__icon bx bx-purchase-tag-alt'></i><span class="app-menu__label">Quản lý sản phẩm</span></a>
            </li>
        </c:if>
        <c:if test="${!title.equals('Danh Sách Sản Phẩm')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-table-product" />"><i
                    class='app-menu__icon bx bx-purchase-tag-alt'></i><span class="app-menu__label">Quản lý sản phẩm</span></a>
            </li>
        </c:if>

        <c:if test="${title.equals('Danh Sách Đơn Hàng')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-table-order" />">
                <i class='app-menu__icon bx bx-task'></i><span class="app-menu__label">Quản lý đơn hàng</span></a></li>
        </c:if>
        <c:if test="${!title.equals('Danh Sách Đơn Hàng')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-table-order" />">
                <i class='app-menu__icon bx bx-task'></i><span class="app-menu__label">Quản lý đơn hàng</span></a></li>
        </c:if>

        <%-- ── Menu: Quản lý Public Key (GIỮ NGUYÊN) ── --%>
        <c:if test="${title.equals('Danh Sách Public Key')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-public-key" />">
                <i class="app-menu__icon bx bx-key"></i><span class="app-menu__label">Quản lý Public Key</span>
            </a></li>
        </c:if>
        <c:if test="${!title.equals('Danh Sách Public Key')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-public-key" />">
                <i class="app-menu__icon bx bx-key"></i><span class="app-menu__label">Quản lý Public Key</span>
            </a></li>
        </c:if>

        <%-- ── Menu: Thông Báo Mất Khóa (THÊM MỚI) ── --%>
        <c:if test="${title.equals('Thông Báo Mất Khóa')}">
            <li>
                <a class="app-menu__item active" href="<c:url value='/admin-key-loss-report'/>">
                    <i class="app-menu__icon bx bx-bell-minus" style="color:#e74c3c;"></i>
                    <span class="app-menu__label">
                      Thông báo mất khóa
                      <c:if test="${pendingLossCount > 0}">
                          <span class="badge badge-danger ml-1" style="font-size:10px;">${pendingLossCount}</span>
                      </c:if>
                  </span>
                </a>
            </li>
        </c:if>
        <c:if test="${!title.equals('Thông Báo Mất Khóa')}">
            <li>
                <a class="app-menu__item" href="<c:url value='/admin-key-loss-report'/>">
                    <i class="app-menu__icon bx bx-bell-minus"></i>
                    <span class="app-menu__label">
                      Thông báo mất khóa
                      <c:if test="${pendingLossCount > 0}">
                          <span class="badge badge-danger ml-1" style="font-size:10px;">${pendingLossCount}</span>
                      </c:if>
                  </span>
                </a>
            </li>
        </c:if>

        <c:if test="${title.equals('Danh sách đánh giá, bình luận')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-manage-rate"/>">
                <i class="app-menu__icon  fa-regular fa-user"></i><span class="app-menu__label">Quản lý đánh giá, bình luận</span></a>
            </li>
        </c:if>
        <c:if test="${!title.equals('Danh sách đánh giá, bình luận')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-manage-rate" />">
                <i class="app-menu__icon  fa-regular fa-user"></i><span class="app-menu__label">Quản lý đánh giá, bình luận</span></a>
            </li>
        </c:if>

        <c:if test="${title.equals('Danh sách liên hệ')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-management-contact" />"><i class='app-menu__icon bx bx-calendar-check'></i><span
                    class="app-menu__label">Quản lý contact</span></a></li>
        </c:if>
        <c:if test="${!title.equals('Danh sách liên hệ')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-management-contact" />"><i class='app-menu__icon bx bx-calendar-check'></i><span
                    class="app-menu__label">Quản lý contact</span></a></li>
        </c:if>
        <c:if test="${title.equals('Danh Sách Khuyến Mãi')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-table-sales" />"><i class='app-menu__icon bx bx-task'></i><span
                    class="app-menu__label">Quản lý khuyến mãi</span></a></li>
        </c:if>
        <c:if test="${!title.equals('Danh Sách Khuyến Mãi')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-table-sales" />"><i class='app-menu__icon bx bx-task'></i><span
                    class="app-menu__label">Quản lý khuyến mãi</span></a></li>
        </c:if>

        <c:if test="${title.equals('Báo Cáo Doanh Thu')}">
            <li><a class="app-menu__item active" href="<c:url value="/admin-report-management" />"><i
                    class='app-menu__icon bx bx-pie-chart-alt-2'></i><span class="app-menu__label">Báo cáo doanh thu</span></a>
            </li>
        </c:if>
        <c:if test="${!title.equals('Báo Cáo Doanh Thu')}">
            <li><a class="app-menu__item" href="<c:url value="/admin-report-management" />"><i
                    class='app-menu__icon bx bx-pie-chart-alt-2'></i><span class="app-menu__label">Báo cáo doanh thu</span></a>
            </li>
        </c:if>

    </ul>
</aside>
