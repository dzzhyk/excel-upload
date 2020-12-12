package com.yankaizhang.excel.config;

import cn.hutool.core.lang.Console;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@Configuration
public class ExcelConfig {

    @Autowired
    MongoTemplate mongoTemplate;

    @Bean
    public Excel03SaxReader xlsReader(){
        return new Excel03SaxReader(createRowHandler());
    }

    @Bean
    public Excel07SaxReader xlsxReader(){
        return new Excel07SaxReader(createRowHandler());
    }

    /**
     * 定义行处理器
     * @return RowHandler
     */
    private RowHandler createRowHandler() {
        return new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowlist) {
//                Console.log("[{}] [{}] {}", sheetIndex, rowIndex, rowlist);
            }
        };
    }

}
