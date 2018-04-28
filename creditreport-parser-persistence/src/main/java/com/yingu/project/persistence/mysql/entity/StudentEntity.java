package com.yingu.project.persistence.mysql.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

/**
 * Created by MMM on 2018/01/25.
 */
@Entity
@Table(name="student")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class StudentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private Integer age;
    private String city;
    private Integer height;
    private Integer weight;
}
