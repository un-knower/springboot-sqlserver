package com.yingu.project.persistence.mongo.entity.abridged;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 头部 基础信息
 * @Date: Created in 2018/3/29 19:03
 * @Author: wm
 */
@Getter
@Setter
public class BaseInfo {
    /**报告编号*/
    @NotNull
    @Length(min = 15, max = 25)
    String contractNo;

    /**姓名*/
    @NotNull
    @Length(min = 2, max = 6)
    String clientName;

    /**查询时间*/
    @NotNull
    @Length(min = 15, max = 25)
    String inquiryDateTimeStr;
    /**查询时间*/
    Date inquiryDateTime;

    /**证件类型*/
    @NotNull
    @Length(min = 2, max = 6)
    String cardType;

    /**报告时间*/
    @NotNull
    @Length(min = 15, max = 25)
    String generationDateTimeStr;
    /**报告时间*/
    Date generationDateTime;

    /**证件号码*/
    @NotNull
    @Length(min = 5, max = 20)
    String cardNo;

    /**是否已婚*/
    @NotNull
    @Length(min = 1, max = 3)
    String isMarried;
}
