package vn.edu.hcmuaf.fit.dao;

import vn.edu.hcmuaf.fit.model.KeyLossReportModel;

import java.sql.Timestamp;
import java.util.List;

public interface IKeyLossReportDao {


    boolean submitReport(int idKey, int idUser, String reason);

    List<KeyLossReportModel> getPendingReports();

    List<KeyLossReportModel> getAllReports();

    boolean approveReport(int idReport, String adminNote);

    boolean rejectReport(int idReport, String adminNote);

    boolean hasPendingReport(int idKey);
}
