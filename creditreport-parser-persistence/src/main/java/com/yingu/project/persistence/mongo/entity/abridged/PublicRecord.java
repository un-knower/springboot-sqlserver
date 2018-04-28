package com.yingu.project.persistence.mongo.entity.abridged;

import com.yingu.project.persistence.mongo.entity.abridged.publicrecord.EnforcementRecord;
import lombok.Getter;
import lombok.Setter;

/**
 * 公共记录
 * 这部分包含您最近5年内的欠税记录、民事判决记录、强制执行记录、行政处罚记录及电信欠费记录。
 * 金额类数据均以人民币计算，精确到元。
 * @Date: Created in 2018/4/10 11:24
 * @Author: wm
 */
@Getter
@Setter
public class PublicRecord {
    /**强制执行记录*/
    EnforcementRecord enforcementRecord;

}
