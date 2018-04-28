package com.yingu.project.service.service;

import com.yingu.project.api.ResponseOneResult;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCreditBase;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.util.List;

/**
 * Created by MMM on 2018/03/21.
 * 个人信用word解析服务
 */
public interface PersonCreditService {
    PersonCredit getPersonCredit(XWPFDocument xdoc,String wordName,String ossFileName);
    ResponseOneResult<PersonCredit> getPersonCredit(String filePath, String wordName);
    List<PersonCredit> findAllByIdcard(String idcard);
    PersonCredit findFirstById(String id);
}
