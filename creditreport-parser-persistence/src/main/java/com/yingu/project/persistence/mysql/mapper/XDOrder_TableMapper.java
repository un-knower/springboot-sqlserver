package com.yingu.project.persistence.mysql.mapper;

import com.yingu.project.persistence.mysql.entity.XDOrder_Table;
import org.springframework.stereotype.Component;

@Component
public interface XDOrder_TableMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(XDOrder_Table record);

    int insertSelective(XDOrder_Table record);

    XDOrder_Table selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(XDOrder_Table record);

    int updateByPrimaryKeyWithBLOBs(XDOrder_Table record);

    int updateByPrimaryKey(XDOrder_Table record);
    XDOrder_Table selectByOrderNum(String ordernum);
}