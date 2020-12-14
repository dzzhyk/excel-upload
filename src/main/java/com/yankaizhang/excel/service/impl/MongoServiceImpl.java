package com.yankaizhang.excel.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.mongodb.client.MongoCollection;
import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.entity.ExcelLine;
import com.yankaizhang.excel.entity.ExcelLinePage;
import com.yankaizhang.excel.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOptions;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregationOptions;

@Service
@Slf4j
@Scope("prototype")
public class MongoServiceImpl implements MongoService {

    @Autowired
    MongoTemplate mongoTemplate;

    Excel03SaxReader xlsReader = new Excel03SaxReader(createRowHandler());

    Excel07SaxReader xlsxReader = new Excel07SaxReader(createRowHandler());

    private final List<ExcelLine> lineBuf = new ArrayList<>(512);

    private static final int LINEBUF_SIZE = 500;

    private String collectionName;

    @Value(value = "${excel.save-path}")
    private String uploadPath;

    @Override
    public synchronized Boolean parseExcel(File file, ExcelConstant type) {
        long start = System.currentTimeMillis();
        if (!file.exists()){
            log.warn("待解析parse文件不存在");
            return false;
        }

        // 使用文件名创建collection
        collectionName = FileUtil.getPrefix(file);
        mongoTemplate.createCollection(collectionName);

        if (ExcelConstant.XLS == type) {
            try {
                if (file.exists()){
                    xlsReader.read(file, -1);
                    insert2Mongo();
                }
                long end = System.currentTimeMillis();
                log.info("文件: "+file.getName()+", 处理时长: " + (end-start) +" ms");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        } else if (ExcelConstant.XLSX == type) {
            try {
                if (file.exists()){
                    xlsxReader.read(file, -1);
                    // 写入剩余
                    insert2Mongo();
                }
                long end = System.currentTimeMillis();
                log.info("文件: "+file.getName()+", 处理时长: " + (end-start) +" ms");
                return true;
            }catch (Exception e){
                e.printStackTrace();
                log.debug(file.getName());
                return false;
            }
        }
        return false;
    }

    @Override
    public Long getExcelLineCount(Integer sheet, String collectionName) {
        return mongoTemplate.count(new Query(Criteria.where("sheet").is(sheet)), ExcelLine.class, collectionName);
    }

    @Override
    public List<ExcelLine> getExcelLinesPage(String collectionName, Integer sheet, Integer curr, Integer size) {
        long start = System.currentTimeMillis();
        Query query = new Query(Criteria.where("sheet").is(sheet)).with(new ExcelLinePage(curr, size, Sort.by("row")));
//        ArrayList<AggregationOperation> operations = new ArrayList<>(4);
//        operations.add(Aggregation.match(Criteria.where("sheet").is(sheet)));
//        operations.add(Aggregation.sort(Sort.by("row")));
//        operations.add(Aggregation.skip((curr-1) * size));
//        operations.add(Aggregation.limit(size));
//        Aggregation aggregation = Aggregation.newAggregation(operations).withOptions(newAggregationOptions().
//                allowDiskUse(true).build());
//        AggregationResults<ExcelLine> aggregate = mongoTemplate.aggregate(aggregation, collectionName, ExcelLine.class);
//        List<ExcelLine> excelLineList = aggregate.getMappedResults();
        List<ExcelLine> excelLineList = mongoTemplate.find(query, ExcelLine.class,  collectionName);
        long end = System.currentTimeMillis();
        log.info("sheet: " + sheet + ", curr: "+curr+", size: "+ size +" 查询时长: " + (end-start) +" ms");
        return excelLineList;
    }


    /**
     * 定义行处理器
     * @return RowHandler
     */
    private RowHandler createRowHandler() {
        return new RowHandler() {
            @Override
            public void handle(int sheetIndex, long rowIndex, List<Object> rowList) {
                // 每500行记录存入MongoDB
                lineBuf.add(new ExcelLine(sheetIndex, rowIndex, rowList));
                if (lineBuf.size() >= LINEBUF_SIZE){
                    insert2Mongo();
                    lineBuf.clear();
                }
            }
        };
    }

    private void insert2Mongo(){
        mongoTemplate.insert(lineBuf, collectionName);
    }
}
