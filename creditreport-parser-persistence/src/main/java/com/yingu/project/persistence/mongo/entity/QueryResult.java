package com.yingu.project.persistence.mongo.entity;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * Created by samli on 2017/8/21.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Document(collection = "query_result")
public class QueryResult
{
    @Id
    private String id;

    private String customerId;

    private String interfaceId;

    @Field("create_time")
    public Date createTime;

    private BasicDBObject responseBody;
}
