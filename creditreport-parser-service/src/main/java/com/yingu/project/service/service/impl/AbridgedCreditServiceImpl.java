package com.yingu.project.service.service.impl;

import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mongo.repository.AbridgedCreditRepository;
import com.yingu.project.service.service.AbridgedCreditService;
import com.yingu.project.util.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Date: Created in 2018/4/16 9:41
 * @Author: wm
 */
@Service
public class AbridgedCreditServiceImpl implements AbridgedCreditService {
    @Autowired
    private AbridgedCreditRepository abridgedCreditRepository;

//    @Override
//    public AbridgedCredit save(AbridgedCredit abridgedCredit) {
//        /* 创建时间 */
//        if (null == abridgedCredit.getCreatetime()) {
//            abridgedCredit.setCreatetime(DateUtil.getDate());
//        }
//        /* 是否删除 */
//        if (null==abridgedCredit.getIsdel()){
//            abridgedCredit.setIsdel(false);
//        }
//        return abridgedCreditRepository.save(abridgedCredit);
//    }

    @Override
    public List<AbridgedCredit> findAllByIdcard(String idcard) {
        return abridgedCreditRepository.findAllByIdcardOrWordNameOrderByCreatetimeAsc(idcard,idcard);
    }
    @Override
    public AbridgedCredit findFirstById(String id){
        return abridgedCreditRepository.findFirstById(id);
    }
}
