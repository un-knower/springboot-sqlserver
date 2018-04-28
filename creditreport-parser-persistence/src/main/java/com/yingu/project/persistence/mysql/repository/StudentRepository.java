package com.yingu.project.persistence.mysql.repository;

import com.yingu.project.persistence.mysql.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by MMM on 2018/01/25.
 */
@Repository
public interface StudentRepository extends JpaRepository<StudentEntity,Long>{
    @Query(nativeQuery = true,
            value = "select *" +
                    " from student" +
                    " where 1=1" +
                    " and name=:name")
    List<StudentEntity> findStudentByName(@Param("name")String name);
}
