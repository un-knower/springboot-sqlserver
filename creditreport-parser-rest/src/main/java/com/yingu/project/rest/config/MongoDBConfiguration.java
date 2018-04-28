package com.yingu.project.rest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by asus on 8/18/2017.
 */
@Configuration
@EnableMongoRepositories("com.yingu.project.persistence.mongo.repository")
public class MongoDBConfiguration {
}
