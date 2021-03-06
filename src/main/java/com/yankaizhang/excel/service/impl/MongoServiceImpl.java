package com.yankaizhang.excel.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.poi.excel.sax.Excel03SaxReader;
import cn.hutool.poi.excel.sax.Excel07SaxReader;
import cn.hutool.poi.excel.sax.handler.RowHandler;
import com.mongodb.client.model.IndexOptions;
import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.entity.ExcelLine;
import com.yankaizhang.excel.service.MongoService;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
@Scope("prototype")
public class MongoServiceImpl implements MongoService {

    @Autowired
    MongoTemplate mongoTemplate;

    Excel03SaxReader xlsReader = new Excel03SaxReader(createRowHandler());

    Excel07SaxReader xlsxReader = new Excel07SaxReader(createRowHandler());

    private final List<ExcelLine> lineBuf = new ArrayList<>(512);

    private static final int LINEBUF_SIZE = 1000;

    private String collectionName;

    private static final ExecutorService executor = ThreadUtil.newExecutor(5);

    @Override
    public synchronized Boolean parseExcel(File file, ExcelConstant type) {
        if (!file.exists()){
            log.warn("待解析parse文件不存在");
            return false;
        }

        // 使用文件名创建collection
        collectionName = FileUtil.getPrefix(file);

        if (ExcelConstant.XLS == type || ExcelConstant.XLSX == type) {
            mongoTemplate.createCollection(collectionName);
            try{
                if (file.exists()){
                    switch (type){
                        case XLS:
                            xlsReader.read(file, -1);break;
                        case XLSX:
                            xlsxReader.read(file, -1);break;
                        default:
                            break;
                    }
                    insert2Mongo();
                }
                createIndexForExcel();
                return true;
            }catch (Exception e){
                e.printStackTrace();
                mongoTemplate.dropCollection(collectionName);
                log.error("写入MongoDB出错 : {}", file.getName());
                return false;
            }
        }
        return false;
    }

    @Override
    public List<ExcelLine> getExcelLinesPage(String collectionName, Integer sheet, Integer curr, Integer size) {
        ArrayList<AggregationOperation> operations = new ArrayList<>(4);
        operations.add( Aggregation.match(Criteria.where("sheet").is(sheet)));
        operations.add( Aggregation.match(Criteria.where("row").gte((curr-1) * size).lt(curr * size)));
        operations.add( Aggregation.sort(Sort.by("row")) );

        Aggregation aggregation = Aggregation.newAggregation(operations);
        AggregationResults<ExcelLine> aggregate = mongoTemplate.aggregate(aggregation, collectionName, ExcelLine.class);

        return aggregate.getMappedResults();
    }


    @Override
    public Integer getExcelSheetCount(String collectionName) {
        return mongoTemplate.findDistinct(new Query(), "sheet", collectionName, ExcelLine.class, Integer.class).size();
    }

    @Override
    public Long getExcelLineCountBySheet(String collectionName, Integer sheet) {
        return mongoTemplate.count(new Query(Criteria.where("sheet").is(sheet)), ExcelLine.class, collectionName);
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

    /**
     * 建立sheet_row复合索引
     */
    private void createIndexForExcel(){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String resultStr = mongoTemplate.getCollection(collectionName)
                        .createIndex(new BsonDocument()
                                        .append("sheet", new BsonInt32(1))
                                        .append("row", new BsonInt32(1)),
                                new IndexOptions().name("index_sheet_row"));

                log.info("创建索引结果: {}", resultStr);
            }
        });
    }
}
