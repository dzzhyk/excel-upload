package com.yankaizhang.excel.config;

import com.mongodb.Block;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ConnectionPoolSettings;
import com.yankaizhang.excel.entity.config.CustomMongoProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;


/**
 * MongoDB连接配置类
 */
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Autowired
    private CustomMongoProperties mongoProperties;

    @Bean
    CustomMongoProperties mongoSettingsProperties() {
        return new CustomMongoProperties();
    }

    @Override
    protected String getDatabaseName() {
        return "test";
    }

    @Override
    public MongoClient mongoClient() {

        // 设置连接
        final ConnectionString connectionString = new ConnectionString(mongoProperties.getAddress());
        final MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applyToConnectionPoolSettings(new Block<ConnectionPoolSettings.Builder>() {
                    @Override
                    public void apply(ConnectionPoolSettings.Builder builder) {
                        builder.minSize(mongoProperties.getMinConnections());
                        builder.maxSize(mongoProperties.getMaxConnections());
                    }
                })
                .build();
        return MongoClients.create(mongoClientSettings);
    }
}
