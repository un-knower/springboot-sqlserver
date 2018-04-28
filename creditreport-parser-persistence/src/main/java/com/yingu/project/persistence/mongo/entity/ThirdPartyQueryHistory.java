package com.yingu.project.persistence.mongo.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by samli on 2017/8/24.
 */
@Setter
@Getter
public class ThirdPartyQueryHistory {

    @Id
    private String id;

    private String interfaceId;

    private String interfaceName;

    private Date requestTime;

    private String param;

    private Date responseTime;

    private String returnCode;
    /**
     * 第三方查询状态,是否命中状态
     */
    private String returnState;

    private String message;

    private String customerId;
    /**
     * 第三方返回结果
     */
    private String result;
    /**
     * 第三方查询状态,成功与否
     */
    private boolean state;

}
