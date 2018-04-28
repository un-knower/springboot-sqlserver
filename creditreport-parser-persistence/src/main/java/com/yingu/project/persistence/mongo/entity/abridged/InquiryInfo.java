package com.yingu.project.persistence.mongo.entity.abridged;

import com.yingu.project.persistence.mongo.entity.abridged.inquiryinfo.InquiryDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 查询记录明细
 * 包括：企业查询、个人查询、强制查询
 * @Date: Created in 2018/4/2 14:28
 * @Author: wm
 */
@Getter
@Setter
public class InquiryInfo {
    /**机构查询记录明细*/
    List<InquiryDetails> authInquiryDetailsList;
//    /**个人查询记录明细*/
//    List<InquiryDetails> personInquiryDetailsList;
}
