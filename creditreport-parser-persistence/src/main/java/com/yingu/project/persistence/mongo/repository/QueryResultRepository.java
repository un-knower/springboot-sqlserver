package com.yingu.project.persistence.mongo.repository;





import com.yingu.project.persistence.mongo.entity.QueryResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

/**
 * Created by samli on 2017/8/21.
 */
public interface QueryResultRepository extends MongoRepository<QueryResult, String> {
    QueryResult findByCustomerId(String customerId);

    QueryResult findFirst1ByCustomerId(String customerId, Sort sort);

    QueryResult findFirstByCustomerIdAndAndInterfaceIdOrderByCreateTimeDesc(String customerId, String interfaceId);

    List<QueryResult> findAllByCreateTimeBetween(Date beginTime, Date endTime);

    /**
     * 时间降序查询queryresult,时间区间,用于强制刷新查询
     * @param customerId
     * @param interfaceId
     * @param beginTime
     * @param endTime
     * @return
     */
    QueryResult findFirstByCustomerIdAndInterfaceIdAndCreateTimeBetweenOrderByCreateTimeDesc(String customerId, String interfaceId, Date beginTime, Date endTime);

    List<QueryResult> findAllByCustomerId(String customerId);



}
