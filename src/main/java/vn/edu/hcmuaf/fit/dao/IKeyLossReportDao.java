package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.util.List;

public interface IKeyLossReportDao {

    /** Người dùng gửi yêu cầu báo mất khóa */
    boolean submitReport(int idKey, int idUser, String reason);

    /** Admin lấy tất cả yêu cầu chờ xử lý */
    List<KeyLossReportModel> getPendingReports();

    /** Admin lấy toàn bộ lịch sử */
    List<KeyLossReportModel> getAllReports();

    /**
     * Admin xác nhận mất khóa:
     * - Cập nhật status = 1 trong key_loss_report
     * - Thu hồi public_key (status = 0, expire = reportTime)
     * - Đơn hàng dùng khóa đó SAU reportTime → shipping_info = 5 (lỗi)
     * - Đơn hàng dùng khóa đó TRƯỚC reportTime → giữ nguyên
     */
    boolean approveReport(int idReport, String adminNote);

    /** Admin từ chối báo cáo */
    boolean rejectReport(int idReport, String adminNote);

    /** Kiểm tra user đã có báo cáo đang chờ xử lý chưa (tránh spam) */
    boolean hasPendingReport(int idKey);
}
