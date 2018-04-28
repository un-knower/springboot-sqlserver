package com.yingu.project.service.service.impl;

import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import com.yingu.project.persistence.mongo.repository.AnalyseFailRecordRepository;
import com.yingu.project.service.service.AnalyseFailRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by MMM on 2018/04/25.
 */
@Service
public class AnalyseFailRecordServiceImpl implements AnalyseFailRecordService {
    @Autowired
    AnalyseFailRecordRepository analyseFailRecordRepository;
    @Override
    public List<AnalyseFailRecord> findByWordNameOrderByCreatetimeAsc(String wordName) {
        return analyseFailRecordRepository.findByWordNameOrderByCreatetimeAsc(wordName);
    }
}
