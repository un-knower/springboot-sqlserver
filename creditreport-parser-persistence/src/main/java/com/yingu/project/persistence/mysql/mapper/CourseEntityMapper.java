package com.yingu.project.persistence.mysql.mapper;

import com.yingu.project.persistence.mysql.entity.CourseEntity;
import com.yingu.project.persistence.mysql.entity.CourseEntityExample;
import java.util.List;

public interface CourseEntityMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseEntity record);

    int insertSelective(CourseEntity record);

    List<CourseEntity> selectByExample(CourseEntityExample example);

    CourseEntity selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseEntity record);

    int updateByPrimaryKey(CourseEntity record);
}