package com.yingu.project.persistence.mongo.entity.PersonCreditEntitys;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by MMM on 2018/03/23.
 * 居住信息
 */
@Getter
@Setter
public class ResidenInfo extends CreditBase{
    /* 编号 */
    public String residenNo;
    /* 居住地址 */
    public String address;
    /* 居住状态 */
    public String livingState;
    /* 信息更新日期 */
    public Date updateTime;
    public String strUpdateTime;
    /* 数据发生机构 */
    public String dataGenerating;
}
