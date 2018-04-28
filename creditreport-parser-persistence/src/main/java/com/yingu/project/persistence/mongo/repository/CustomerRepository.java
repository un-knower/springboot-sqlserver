package com.yingu.project.persistence.mongo.repository;


import com.yingu.project.persistence.mongo.entity.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<Customer, String> {

    Customer findByName(String name);

    List<Customer> findByIdCard(String idCard);

    Customer findFirstByIdCard(String idCard);
    Customer findFirstById(String id);

    List<Customer> findAllByIntoInfoNoAndStatus(String intoInfoNo, Integer status);
    Customer findFirstByIntoInfoNo(String intoInfoNo);
    Customer findFirstByIntoInfoNoAndNameAndIdCardAndPhoneNumber(
            String intoInfoNo, String name, String idcard, String phone);
    List<Customer> findAllByIntoInfoNo(String intoInfoNo);
    Customer findFirstByIntoInfoNoAndStatusOrderByCreateTimeDesc(String intoInfoNo, Integer status);
}