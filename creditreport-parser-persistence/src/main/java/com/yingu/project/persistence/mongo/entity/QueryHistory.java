package com.yingu.project.persistence.mongo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by samli on 2017/8/23.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection = "query_history")
public class QueryHistory {

    @Id
    private String id;
    /**
     * 进件编号
     */
    public String intoInfoNo;

    @Field("create_time")
    private Date createTime = new Date();

    private String param;

    @Field("customer_id")
    private String customerId;

    @Field("Status")
    private String status;

}
