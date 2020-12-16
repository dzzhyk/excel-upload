package com.yankaizhang.excel.entity.config;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mongoDB配置包装类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "spring.data.mongodb.custom")
public class CustomMongoProperties {

    private String address;
    private Integer minConnections;
    private Integer maxConnections;

}
