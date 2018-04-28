package com.yingu.project.service.service;

import com.alibaba.fastjson.JSON;
import com.yingu.project.ApplicationTest;
import com.yingu.project.persistence.mongo.entity.abridged.AbridgedCredit;
import com.yingu.project.persistence.mongo.repository.AbridgedCreditRepository;
import com.yingu.project.service.service.simple.util.SpringValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import java.text.MessageFormat;
import java.util.List;
import java.util.Set;

/**
 * @Date: Created in 2018/4/16 9:46
 * @Author: wm
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApplicationTest.class)
@Slf4j
public class AbridgedCreditServiceTest {
    @Autowired
    AbridgedCreditService abridgedCreditService;

    @Autowired
    AbridgedCreditRepository abridgedCreditRepository;

    @Test
    public void findAllByIdcard() {
        String idcard = "test-231004197303281219";
        AbridgedCredit abridgedCredit = new AbridgedCredit();
        abridgedCredit.setIdcard(idcard);
        abridgedCredit.setName("张三");
        AbridgedCredit entity = abridgedCreditRepository.save(abridgedCredit);
        log.info(MessageFormat.format("save result: {0}", JSON.toJSONString(entity)));
        Assert.notNull(entity.getId(), "id is null");

        List<AbridgedCredit> list = abridgedCreditService.findAllByIdcard(idcard);
        log.info(MessageFormat.format("find result: {0}", JSON.toJSONString(list)));
        Assert.notNull(list, "list is null");
        Assert.isTrue(list.size() > 0, "count is zero");

        abridgedCreditRepository.deleteAllByIdcard(idcard);
    }

    @Autowired
    SpringValidator springValidator;
    @Test
    public void validate(){
        String idcard = "231004197303281219";
        AbridgedCredit entity = abridgedCreditRepository.findFirstByIdcard(idcard);
        Set<ConstraintViolation> set = springValidator.validate(entity);
        log.info("");
    }

    @Test
    public void countByReportNo(){
        String reportNo = "test-7303281219";
        String idcard = "231004197303281219";
        AbridgedCredit abridgedCredit = new AbridgedCredit();

        abridgedCredit.setReportNo(reportNo);
        abridgedCredit.setIdcard(idcard);
        abridgedCredit.setName("张三");
        AbridgedCredit entity = abridgedCreditRepository.save(abridgedCredit);
        log.info(MessageFormat.format("save result: {0}", JSON.toJSONString(entity)));
        Assert.notNull(entity.getId(), "id is null");

        Long count = abridgedCreditRepository.countByReportNo(reportNo);
        log.info(MessageFormat.format("find result: {0}", count));
        Assert.notNull(count, "count is null");
        Assert.isTrue(count > 0, "count is zero");

        abridgedCreditRepository.deleteAllByIdcard(idcard);

    }
}
