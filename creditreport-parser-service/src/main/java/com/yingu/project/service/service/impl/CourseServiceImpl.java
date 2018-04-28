package com.yingu.project.service.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yingu.project.persistence.mysql.entity.CourseEntity;
import com.yingu.project.persistence.mysql.mapper.CourseEntityMapper;
import com.yingu.project.service.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by MMM on 2018/02/27.
 */
@Service
public class CourseServiceImpl implements CourseService{
    @Override
    public PageInfo<Map> selectPager(Integer pageIndex, Integer pageSize) {
        return null;
    }
//    @Autowired
//    CourseEntityMapper courseEntityMapper;
//    @Override
//    public PageInfo<Map> selectPager(Integer pageIndex, Integer pageSize){
//        //分页查询
//        PageHelper.startPage(2, 3);
//        List<CourseEntity> list = courseEntityMapper.selectByExample(null);
//        PageInfo<Map> pageInfo = new PageInfo(list);
//        return pageInfo;
//    }
}
