package vn.edu.hcmuaf.edu.vn.services;

import vn.edu.hcmuaf.fit.model.BillDetail;

import java.util.List;

public interface IBillDetailsService {
    List<BillDetail> findAll();
    BillDetail findByID(int id);
}
