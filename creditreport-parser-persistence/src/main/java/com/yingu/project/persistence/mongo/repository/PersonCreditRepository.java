package com.yingu.project.persistence.mongo.repository;

import com.yingu.project.persistence.mongo.entity.PersonCreditEntitys.PersonCredit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MMM on 2018/03/22.
 */
@Repository
public interface PersonCreditRepository extends MongoRepository<PersonCredit, String> {
    PersonCredit findFirstByIdcard(String idcard);
    @Query(fields = "{ '_id': 1," +
            "'baseInfo' : 1," +
            "'reportNo':1," +
            "'requestDateTime':1," +
            "'strRequestDateTime':1," +
            "'generateDateTime':1," +
            "'strGenerateDateTime':1," +
            "'wordName':1," +
            "'wordType':1," +
            "'oosFileName':1," +
            "'createtime':1}")
    List<PersonCredit> findAllByIdcardOrWordNameOrderByCreatetimeAsc(String idcard,String wordname);
    PersonCredit findFirstByReportNo(String reportNo);
    PersonCredit findFirstById(String id);
}
