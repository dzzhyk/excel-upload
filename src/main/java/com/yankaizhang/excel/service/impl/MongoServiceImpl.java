package com.yankaizhang.excel.service.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import com.mongodb.client.MongoCollection;
import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.*;

@Service
@Slf4j
@Scope("prototype")
public class MongoServiceImpl implements MongoService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    Excel03SaxReader xlsReader;

    @Autowired
    Excel07SaxReader xlsxReader;

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @Override
    public Boolean parseExcel(File file, ExcelConstant type) {

        if (!file.exists()){
            log.warn("待解析parse文件不存在");
            return false;
        }

        Future<Boolean> booleanFuture = ThreadUtil.execAsync(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                // 创建Collection
                MongoCollection<Document> collection = mongoTemplate.createCollection(file.getName());

                if (ExcelConstant.XLS == type) {
                    try {
                        xlsReader.read(file, -1);
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                } else if (ExcelConstant.XLSX == type) {
                    try {
                        xlsxReader.read(file, -1);
                    }catch (Exception e){
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }

                log.warn("未知excel类型");
                return false;
            }
        });

        try {
            return booleanFuture.get(300, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.warn("处理excel被迫中断");
            return false;
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.warn("处理excel过程错误");
            return false;
        } catch (TimeoutException e) {
            e.printStackTrace();
            log.warn("处理excel超时");
            return false;
        }

    }
}
