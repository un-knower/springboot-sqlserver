package com.yingu.project.persistence.mongo.repository;


import com.yingu.project.persistence.mongo.entity.ThirdPartyQueryHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

/**
 * Created by samli on 2017/8/24.
 */
public interface ThirdPartyQueryHistoryRepository extends MongoRepository<ThirdPartyQueryHistory, String> {
    Long countByInterfaceIdAndRequestTimeBetween(String interfaceId, Date beginTime, Date endTime);
    Long countByInterfaceId(String interfaceId);
}
