package com.yingu.project.persistence.mongo.repository;

import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.AnalyseFailRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MMM on 2018/03/27.
 */
@Repository
public interface AnalyseFailRecordRepository extends MongoRepository<AnalyseFailRecord, String> {
    List<AnalyseFailRecord> findByWordNameOrderByCreatetimeAsc(String wordName);
}
