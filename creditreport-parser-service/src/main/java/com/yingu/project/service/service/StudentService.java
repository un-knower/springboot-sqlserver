package com.yingu.project.service.service;


import com.yingu.project.persistence.mysql.entity.StudentEntity;

/**
 * Created by MMM on 2018/01/25.
 */
public interface StudentService {
    StudentEntity add(StudentEntity studentEntity);
    Integer insert(StudentEntity studentEntity);
}
