package com.yingu.project.service.service.impl;

import com.yingu.project.persistence.mongo.entity.Customer;
import com.yingu.project.persistence.mongo.repository.CustomerRepository;
import com.yingu.project.service.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by samli on 2017/8/23.
 */

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    CustomerRepository repository;



    @Override
    public Customer save(Customer customer) {
        return repository.save(customer);
    }

    @Override
    public void del(String id) {
        repository.delete(id);
    }

    @Override
    public void del(Customer customer) {
        repository.delete(customer);
    }

    @Override
    public List<Customer> findCustomerByIdCard(String idCard) {
        return repository.findByIdCard(idCard);
    }

    @Override
    public Customer findByIdCard(String idCard)
    {
        List<Customer> customers = repository.findByIdCard(idCard);
        if (customers !=null && customers.isEmpty() ==false)
        {
            return customers.get(0);
        }
        return null;
    }
    @Override
    public Customer findFirstById(String customerId){
         Customer customer=repository.findFirstById(customerId);
         return customer;
    }
    @Override
    public Map<String,Customer> findAllList(){
        List<Customer> dataList=repository.findAll();
        Map<String,Customer> map=new HashMap<>();
        for(Customer customer:dataList){
            map.put(customer.getId(),customer);
        }
        return map;
    }
    @Override
    public List<Customer> findAllByIntoInfoNoAndStatus(String intoInfoNo, Integer status){
        return repository.findAllByIntoInfoNoAndStatus(intoInfoNo,status);
    }
    @Override
    public Customer findFirstByIntoInfoNo(String intoInfoNo){
        return repository.findFirstByIntoInfoNo(intoInfoNo);
    }
    @Override
    public Customer findFirstByAll(
            String intoInfoNo, String name,String idcard,String phone){
        return repository.findFirstByIntoInfoNoAndNameAndIdCardAndPhoneNumber(intoInfoNo,
                name,idcard,phone);
    }
    @Override
    public List<Customer> findAllByIntoInfoNo(String intoInfoNo){
        return repository.findAllByIntoInfoNo(intoInfoNo);
    }
    @Override
    public Customer findFirstByIntoInfoNoAndStatusOrderByCreateTimeDesc(String intoInfoNo, Integer status){
        return repository.findFirstByIntoInfoNoAndStatusOrderByCreateTimeDesc(intoInfoNo,status);
    }

}
