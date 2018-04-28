package com.yingu.project.persistence.mysql.mapper;


import com.yingu.project.persistence.mysql.entity.StudentEntity;
import org.springframework.stereotype.Component;

/**
 * Created by MMM on 2018/02/11.
 */
@Component
public interface StudentMapper {
    int insert(StudentEntity studentEntity);
}
