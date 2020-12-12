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
import java.util.concurrent.locks.ReentrantLock;

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
    public synchronized Boolean parseExcel(File file, ExcelConstant type) {

        if (!file.exists()){
            log.warn("待解析parse文件不存在");
            return false;
        }

        if (ExcelConstant.XLS == type) {
            try {
                if (file.exists()){
                    xlsReader.read(file, -1);
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } else if (ExcelConstant.XLSX == type) {
            try {
                if (file.exists()){
                    xlsxReader.read(file, -1);
                }
                return true;
            }catch (Exception e){
                e.printStackTrace();
                log.debug(file.getName());
                return false;
            }
        }
        return false;
    }
}
