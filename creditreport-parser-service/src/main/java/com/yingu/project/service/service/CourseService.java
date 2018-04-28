package com.yingu.project.service.service;

import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by MMM on 2018/02/27.
 */
public interface CourseService {
    PageInfo<Map> selectPager(Integer pageIndex,Integer pageSize);
}
