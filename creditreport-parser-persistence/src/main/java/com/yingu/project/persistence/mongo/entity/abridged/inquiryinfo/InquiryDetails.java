package com.yingu.project.persistence.mongo.entity.abridged.inquiryinfo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 机构查询记录明细
 * @Date: Created in 2018/4/2 14:22
 * @Author: wm
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDetails {
    /** 编号 */
    private String number;
    /** 查询日期 */
    private String dateStr;
    /** 查询日期 */
    @NotNull
    private Date date;
    /** 查询操作员 */
    @NotNull
    @Length(min = 5, max = 50)
    private String operator;
    /** 查询原因 */
    @NotNull
    @Length(min = 1, max = 20)
    private String cause;
}
