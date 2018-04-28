package com.yingu.project.service.service;


import com.yingu.project.persistence.mongo.entity.Customer;

import java.util.List;
import java.util.Map;

/**
 * Created by samli on 2017/8/23.
 */
public interface CustomerService {

    Customer findByIdCard(String idCard);

    Customer save(Customer customer);

    void del(String id);

    void del(Customer customer);

    List<Customer> findCustomerByIdCard(String idCard);

    Customer findFirstById(String customerId);

    public Map<String,Customer> findAllList();

    List<Customer> findAllByIntoInfoNoAndStatus(String intoInfoNo,Integer status);

    Customer findFirstByIntoInfoNo(String intoInfoNo);

    Customer findFirstByAll(
            String intoInfoNo, String name,String idcard,String phone);
    List<Customer> findAllByIntoInfoNo(String intoInfoNo);

    Customer findFirstByIntoInfoNoAndStatusOrderByCreateTimeDesc(String intoInfoNo,Integer status);
}
