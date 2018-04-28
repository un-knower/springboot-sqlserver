package com.yingu.project.service.service;

import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;

import java.util.List;

/**
 * 简版 接口
 *
 * @Date: Created in 2018/4/16 9:36
 * @Author: wm
 */
public interface AbridgedCreditService {
    /**
     * 保存
     * @param abridgedCredit
     * @return  保存后的entity
     * @date: Created in 2018/4/16 9:56
     * @author: wm
     */
//    public AbridgedCredit save(AbridgedCredit abridgedCredit);

    /**
     * 根据证件号码查询列表
     *
     * @param idcard 证件号码
     * @return
     * @date: Created in 2018/4/16 9:45
     * @author: wm
     */
    public List<AbridgedCredit> findAllByIdcard(String idcard);

    AbridgedCredit findFirstById(String id);
}
