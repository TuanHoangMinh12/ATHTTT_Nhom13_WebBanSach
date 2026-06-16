package vn.edu.hcmuaf.edu.vn.dao;

import vn.edu.hcmuaf.fit.model.AuthorModel;

import java.util.List;

public interface IAuthorDAO {
    List<AuthorModel> findAll();
    List<AuthorModel> find10Author();
}
