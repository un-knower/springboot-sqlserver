package com.yingu.project.persistence.mysql.repository;

import com.yingu.project.persistence.mysql.entity.TeacherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by MMM on 2018/01/25.
 */
@Repository
public interface TeacherRepository extends JpaRepository<TeacherEntity,Long>{

}
