package com.yingu.project.service.service.simple.cache;

import com.yingu.project.service.service.simple.xdoc.FailInfo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存
 * @Date: Created in 2018/4/18 13:45
 * @Author: wm
 */
@Component
public class ThreadCache {
    /**失败信息记录*/
    private ThreadLocal<List<FailInfo>> failInfoList = new ThreadLocal<>();

    public void addFailInfoList(FailInfo failInfo){
        if (this.failInfoList.get()==null){
            this.failInfoList.set(new ArrayList<>());
        }
        failInfoList.get().add(failInfo);
    }

    public List<FailInfo> getFailInfoList(){
        return failInfoList.get();
    }
}
