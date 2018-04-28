package com.yingu.project.persistence.mongo.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Customer {
    @Id
    public String id;
    /**
     * 进件编号
     */
    public String intoInfoNo;

    public String name;

    @Field("id_card")
    public String idCard;

    @Field("phone_number")
    public String phoneNumber;

    @Field("create_time")
    private Date createTime = new Date();
    /** 0:删除;1:正常*/
    private Integer status=1;

}
