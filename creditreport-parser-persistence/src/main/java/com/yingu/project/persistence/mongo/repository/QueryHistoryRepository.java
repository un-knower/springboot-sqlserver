package com.yingu.project.persistence.mongo.repository;


import com.yingu.project.persistence.mongo.entity.QueryHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by samli on 2017/8/23.
 */
public interface QueryHistoryRepository extends MongoRepository<QueryHistory, String> {
    List<QueryHistory> findAllByCustomerId(String customerId);

    QueryHistory findFirstByCustomerIdOrderByCreateTimeDesc(String customerId);
}
