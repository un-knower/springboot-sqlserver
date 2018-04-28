package com.yingu.project.service.service;

import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by MMM on 2018/04/25.
 */
@Service
public interface AnalyseFailRecordService {
    List<AnalyseFailRecord> findByWordNameOrderByCreatetimeAsc(String wordName);
}
