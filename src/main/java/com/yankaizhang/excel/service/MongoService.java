package com.yankaizhang.excel.service;

import com.yankaizhang.excel.constant.ExcelConstant;
import com.yankaizhang.excel.entity.ExcelLine;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author dzzhyk
 */
public interface MongoService {

    /**
     * 解析excel表格
     * @return 解析结果
     */
    Boolean parseExcel(File file, ExcelConstant type);

    /**
     * 获取某表格某sheet的某个Page段
     * curr ~ curr + size
     */
    List<ExcelLine> getExcelLinesPage(String collectionName, Integer sheet, Integer curr, Integer size);

    /**
     * 获取excel表格的sheet个数
     */
    Integer getExcelSheetCount(String collectionName);

    /**
     * 获取某个sheet的总行数
     */
    Long getExcelLineCountBySheet(String collectionName, Integer sheet);
}
