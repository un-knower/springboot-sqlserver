package com.yingu.project.persistence.mongo.repository;

import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 简版个人征信报告
 *
 * @date: Created in 2018/4/13 15:24
 * @author: wm
 */
@Repository
public interface AbridgedCreditRepository extends MongoRepository<AbridgedCredit, String> {
    AbridgedCredit findFirstByIdcard(String idcard);

    /**
     * 查询列表
     *
     * @param idcard 证件号码
     * @return
     * @date: Created in 2018/4/16 9:45
     * @author: wm
     */
    @Query(fields = "{ '_id': 1," +
            "'baseInfo' : 1," +
            "'reportNo':1," +
            "'requestDateTime':1," +
            "'strRequestDateTime':1," +
            "'generateDateTime':1," +
            "'strGenerateDateTime':1," +
            "'wordName':1," +
            "'wordType':1," +
            "'name':1," +
            "'idcard':1," +
            "'oosFileName':1," +
            "'createtime':1}")
    List<AbridgedCredit> findAllByIdcardOrWordNameOrderByCreatetimeAsc(String idcard,String wordname);

    /**
     * 根据证件号码删除
     * @param idcard 证件号码
     * @date: Created in 2018/4/16 10:01
     * @author: wm
     */
    void deleteAllByIdcard(String idcard);



    /**
     * 根据报告编号统计简版数量
     * @param reportNo  报告编号
     * @return  总数
     * @date: Created in 2018/4/17 9:34
     * @author: wm
     */
    Long countByReportNo(String reportNo);

//    AbridgedCredit findFirstByReportNo(String reportNo);
    AbridgedCredit findFirstById(String id);
}
