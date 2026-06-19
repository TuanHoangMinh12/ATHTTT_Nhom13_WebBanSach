package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.Timestamp;
import java.util.List;

public interface IKeyLossReportDao {

    /**
     * Người dùng gửi yêu cầu báo mất khóa.
     * @param lostTime thời điểm user CHỌN là lúc bị mất khóa (lấy từ input datetime-local "lossTime").
     *                 Đây là mốc dùng để thu hồi key / hủy đơn — KHÁC với report_time (lúc bấm gửi).
     */
    boolean submitReport(int idKey, int idUser, String reason, Timestamp lostTime);

    /** Admin lấy tất cả yêu cầu chờ xử lý */
    List<KeyLossReportModel> getPendingReports();

    /** Admin lấy toàn bộ lịch sử */
    List<KeyLossReportModel> getAllReports();

    /**
     * Admin xác nhận mất khóa:
     * - Cập nhật status = 1 trong key_loss_report
     * - Thu hồi public_key (status = 0, expire = lostTime, fallback report_time nếu lostTime NULL)
     * - Đơn hàng dùng khóa đó tạo SAU lostTime → shipping_info = 4 (đã hủy)
     * - Đơn hàng dùng khóa đó tạo TRƯỚC HOẶC BẰNG lostTime → giữ nguyên
     */
    boolean approveReport(int idReport, String adminNote);

    /** Admin từ chối báo cáo */
    boolean rejectReport(int idReport, String adminNote);

    /** Kiểm tra user đã có báo cáo đang chờ xử lý chưa (tránh spam) */
    boolean hasPendingReport(int idKey);
}
