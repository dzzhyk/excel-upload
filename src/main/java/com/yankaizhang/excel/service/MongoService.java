package com.yankaizhang.excel.service;

import com.yankaizhang.excel.constant.ExcelConstant;

import java.io.File;

/**
 * @author dzzhyk
 */
public interface MongoService {

    /**
     * 解析excel表格
     * @return 解析结果
     */
    Boolean parseExcel(File file, ExcelConstant type);

}
